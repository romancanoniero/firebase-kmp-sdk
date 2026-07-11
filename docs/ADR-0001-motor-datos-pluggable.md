# ADR-0001 — Motor de datos intercambiable (Firebase ⇄ PostgreSQL sobre WebSocket)

- **Estado:** Propuesto
- **Fecha:** 2026-07-10
- **Autor:** Análisis de arquitectura (coordinación técnica)
- **Ámbito:** `firebase-database`, `firebase-firestore` (y patrón replicable al resto de módulos)
- **Consumidor principal:** `ian` / `ian-tv-kmp`. Debe quedar genérico para proyectos futuros.

---

## 1. Contexto y problema

Este proyecto es un **wrapper Kotlin Multiplatform (KMP)** que replica fielmente la API del
Firebase Android SDK para **Android, iOS (Kotlin/Native) y JS**, usando el patrón
`expect`/`actual`. Hoy el ecosistema (empezando por `ian`) no lo está aprovechando como
debería.

El objetivo es doble:

1. **Modo compatibilidad:** que el ecosistema pase a usar esta librería sin reescribir sus
   llamadas (misma API que ya conocen de Firebase).
2. **Nuevo motor oculto:** que las **mismas** funciones/métodos puedan, sin que el código
   llamador se entere, operar contra una base **PostgreSQL** a través de **WebSockets**
   (el WS queda oculto detrás de la API pública).

La pregunta de ingeniería es: **¿cuál es la mejor forma de que cambiar de motor (Firebase ↔
Postgres/WS) no impacte en las demás capas ni exija grandes modificaciones de código?**

---

## 2. Diagnóstico del diseño actual (por qué no alcanza con `expect`/`actual`)

La superficie pública **es** el mecanismo `expect`/`actual`. Ejemplo real
(`firebase-database/src/commonMain/.../FirebaseDatabase.kt`):

```kotlin
expect class FirebaseDatabase {
    companion object { fun getInstance(): FirebaseDatabase /* ... */ }
    fun getReference(path: String): DatabaseReference
    /* ... */
}
expect class DatabaseReference : Query { /* ... */ }
expect class DataSnapshot { /* ... */ }
```

Y cada `actual` **envuelve el objeto nativo** de esa plataforma. Por ejemplo Android
(`FirebaseDatabase.android.kt`):

```kotlin
actual class FirebaseDatabase internal constructor(val android: AndroidDatabase) { /* ... */ }
```

y JS (`FirebaseDatabase.js.kt`) envuelve `jsDb`/`jsRef` del SDK de Firebase para web.

**Consecuencia técnica clave:**

- `expect`/`actual` resuelve **variabilidad por plataforma en tiempo de compilación**. El
  compilador elige **exactamente un `actual` por target** (uno para Android, uno para iOS,
  uno para JS). **No existe un punto de polimorfismo en runtime.**
- Kotlin **no permite dos `actual` para el mismo target**, así que **no se puede "agregar
  Postgres" como un segundo `actual`**. El seam (costura) para cambiar de motor **no es** la
  plataforma; es el **motor de datos**, que es una dimensión ortogonal.
- Además, los tipos públicos exponen el objeto nativo (`val android`, `jsRef`) en sus
  constructores internos, lo que **acopla** la identidad del tipo a Firebase.

> En síntesis: hoy hay **una** variable de variación (plataforma). Necesitamos **dos**
> (plataforma × motor). El patrón correcto para la segunda es **inyección de dependencias +
> puertos/adaptadores**, no `expect`/`actual`.

---

## 3. Objetivos y restricciones

**Objetivos**

- **O1 — Compatibilidad de código fuente:** las clases y firmas públicas
  (`FirebaseDatabase`, `DatabaseReference`, `DataSnapshot`, `Query`, `FirebaseFirestore`,
  `DocumentReference`, ...) se conservan **idénticas** para el llamador.
- **O2 — Cambio de motor sin tocar capas superiores:** seleccionar el motor en el
  arranque/config; los ViewModels, repos y UI no cambian.
- **O3 — WS oculto:** el transporte WebSocket + Postgres es un detalle **interno**.
- **O4 — Flexibilidad futura:** añadir un tercer motor debe ser "escribir un adaptador".
- **O5 — Autoridad del servidor:** las reglas de acceso viven en el servidor (nunca confiar
  en el cliente). La frontera de confianza debe quedar explícita.

**Restricciones**

- **R1 — KMP puro en common:** el driver Postgres/WS debe vivir en `commonMain` (Ktor
  client multiplataforma), sin JDBC (no hay JDBC en iOS/JS y **nunca** debe haber conexión
  directa a Postgres desde el cliente).
- **R2 — Paridad de tiempo real:** Firebase empuja deltas (`valueEvents`, `ChildEventListener`,
  `snapshots`). Postgres necesita un mecanismo equivalente (ver §6).
- **R3 — Impedancia de modelo:** árbol JSON de RTDB / documentos de Firestore vs. modelo
  relacional (ver §7).

---

## 4. Decisión

Adoptar una arquitectura **Puertos y Adaptadores (Hexagonal)** con **driver de motor
seleccionable en tiempo de ejecución**, aplicada así:

1. **Definir puertos (SPI) neutrales** en `commonMain`: interfaces que expresan las
   operaciones de datos en términos agnósticos (rutas, snapshots, escrituras, suscripciones
   como `Flow`). **No** filtran tipos de Firebase ni de Postgres.
2. **Reconvertir la API pública en una fachada** (`commonMain`) que **delega** en el puerto.
   Mismos nombres y firmas ⇒ se cumple O1/O2/O3.
3. **Proveer dos adaptadores (drivers)**:
   - **`FirebaseDriver`**: envuelve el **código `expect`/`actual` actual** (que pasa a ser
     detalle interno, no la superficie pública).
   - **`PostgresWsDriver`**: implementación 100% `commonMain` con **Ktor WebSockets +
     kotlinx.serialization**, hablando con un **gateway** que traduce el protocolo a Postgres.
4. **Registrar/seleccionar el driver** en el arranque (extensión de `FirebaseOptions` o DSL
   de configuración). `getInstance()` instancia la fachada con el driver elegido.

Esto convierte el "cambio de motor" en un cambio de **una** línea de configuración, sin tocar
call sites. El `expect`/`actual` se conserva **sólo dentro del `FirebaseDriver`** (donde sí
corresponde: variación por plataforma del SDK nativo).

### Diagrama

```mermaid
flowchart TD
    subgraph App["Capas superiores (ian, repos, ViewModels, UI)  — SIN CAMBIOS"]
        UI[Repositorios / ViewModels]
    end

    subgraph Public["API pública (commonMain) — nombres Firebase, mismas firmas"]
        FD[FirebaseDatabase / DatabaseReference / DataSnapshot / Query]
        FS[FirebaseFirestore / DocumentReference / ...]
    end

    subgraph SPI["Puertos / SPI (commonMain, agnósticos)"]
        P[DatabaseDriver / DocumentDriver]
    end

    subgraph Drivers["Adaptadores"]
        A[FirebaseDriver\n(expect/actual nativo)]
        B[PostgresWsDriver\n(Ktor WebSocket)]
    end

    subgraph Backends["Backends"]
        FBK[(Firebase RTDB / Firestore)]
        GW[Gateway WS] --> PG[(PostgreSQL\nLISTEN/NOTIFY · RLS)]
    end

    UI --> FD --> P
    UI --> FS --> P
    P -->|selección en runtime| A
    P -->|selección en runtime| B
    A --> FBK
    B -->|WebSocket TLS| GW
```

La **frontera de confianza** está en el borde `Cliente ⇢ Gateway/Firebase`: todo lo que está
a la izquierda es no confiable; la autorización se resuelve a la derecha.

---

## 5. Diseño detallado

### 5.1 Capa de puertos (SPI) — `commonMain`

Interfaces neutrales que ambos motores implementan. Ejemplo para Realtime Database:

```kotlin
// firebase-database/commonMain/.../spi/DatabaseDriver.kt  (internal)
internal interface DatabaseDriver {
    suspend fun get(path: String, query: QuerySpec?): NodeData
    suspend fun setValue(path: String, value: Any?)
    suspend fun updateChildren(path: String, updates: Map<String, Any?>)  // atómico multi-ruta
    suspend fun removeValue(path: String)
    fun push(path: String): String                    // genera key
    fun valueEvents(path: String, query: QuerySpec?): Flow<NodeData>
    fun childEvents(path: String, query: QuerySpec?): Flow<ChildEvent>
    // onDisconnect, priority, etc. según cobertura deseada
}

// Modelo de transporte neutral (ni Firebase ni Postgres)
internal data class NodeData(val key: String?, val value: Any?, val children: List<NodeData>)
internal data class QuerySpec(
    val orderBy: OrderBy? = null,
    val startAt: Bound? = null, val endAt: Bound? = null, val equalTo: Bound? = null,
    val limitToFirst: Int? = null, val limitToLast: Int? = null,
)
internal sealed interface ChildEvent { /* Added/Changed/Removed/Moved con NodeData + prevKey */ }
```

Para Firestore, un puerto análogo (`DocumentDriver`) con `getDocument`, `setDocument`,
`runQuery(QuerySpec)`, `documentSnapshots(...)`, `runTransaction`, `commitBatch`, etc.

> **Nota de diseño:** el SPI se define con `internal` para no ampliar la superficie pública.
> La única API pública sigue siendo la de Firebase.

### 5.2 Fachada pública — `commonMain`

Los tipos públicos dejan de ser `expect class` y pasan a ser clases **comunes** que sostienen
un `DatabaseDriver` y traducen a llamadas del puerto:

```kotlin
class FirebaseDatabase internal constructor(private val driver: DatabaseDriver) {
    companion object {
        fun getInstance(): FirebaseDatabase =
            FirebaseDatabase(EngineRegistry.databaseDriver())   // ← selección de motor
    }
    fun getReference(path: String) = DatabaseReference(driver, path)
    /* ... goOnline/goOffline delegan en driver ... */
}

class DatabaseReference internal constructor(
    private val driver: DatabaseDriver, private val path: String,
) : Query(driver, path) {
    suspend fun setValue(value: Any?) = driver.setValue(path, value)
    suspend fun updateChildren(update: Map<String, Any?>) = driver.updateChildren(path, update)
    fun child(p: String) = DatabaseReference(driver, "$path/$p")
    val valueEvents: Flow<DataSnapshot> get() = driver.valueEvents(path, spec).map { DataSnapshot(it) }
    /* ... mismas firmas que hoy ... */
}
```

Las **extensiones tipadas** ya existentes (`value<T>()`, `valueList<T>()`, `set<T>()`,
`toObject<T>()`, ...) **no cambian**: operan sobre `DataSnapshot`/`DocumentSnapshot` que ahora
son POJOs neutrales. Esto es una ventaja: la serialización kotlinx ya está desacoplada del SDK.

### 5.3 Adaptador Firebase (`FirebaseDriver`)

Aquí **sí** vive `expect`/`actual`, porque la variación es por plataforma. Se reutiliza casi
tal cual el código nativo actual, pero **degradado a interno** y detrás del puerto:

```kotlin
internal class FirebaseDatabaseDriver(...) : DatabaseDriver {
    override suspend fun setValue(path: String, value: Any?) =
        nativeRef(path).setValueNative(value)   // expect/actual → Android/iOS/JS SDK
    override fun valueEvents(path: String, q: QuerySpec?) =
        nativeQuery(path, q).valueEventsNative().map { it.toNodeData() }
    /* ... */
}
```

El trabajo es **mecánico**: mover los `actual` actuales bajo un tipo interno
(`NativeRef`/`NativeQuery`) y mapear `DataSnapshot` nativo ⇄ `NodeData`.

### 5.4 Adaptador Postgres sobre WebSocket (`PostgresWsDriver`)

100% `commonMain` con **Ktor client (WebSockets)** + **kotlinx.serialization**. El WS habla
con un **gateway** (servicio backend) que es el único que toca Postgres.

**Protocolo (propuesta, JSON sobre WS):**

```jsonc
// Cliente → Gateway
{ "op": "get",        "reqId": "…", "path": "users/u1", "query": { … } }
{ "op": "set",        "reqId": "…", "path": "users/u1", "value": { … } }
{ "op": "update",     "reqId": "…", "path": "users/u1", "patch": { … } }   // atómico
{ "op": "subscribe",  "subId": "…", "path": "users",    "query": { … } }
{ "op": "unsubscribe","subId": "…" }
{ "op": "tx",         "reqId": "…", "ops": [ … ] }                          // transacción

// Gateway → Cliente
{ "type": "ack",    "reqId": "…", "data": { … } }
{ "type": "error",  "reqId": "…", "code": 403, "message": "…" }
{ "type": "value",  "subId": "…", "snapshot": { … } }        // snapshot inicial + refresh
{ "type": "child",  "subId": "…", "kind": "added|changed|removed|moved", "key": "…", "value": { … }, "prevKey": "…" }
```

Cliente (esquema):

```kotlin
internal class PostgresWsDriver(
    private val client: HttpClient,          // Ktor + WebSockets
    private val endpoint: String,
    private val tokenProvider: suspend () -> String,   // JWT del usuario autenticado
) : DatabaseDriver {
    // Multiplexa reqId/subId sobre una única conexión; reconexión con backoff;
    // re-suscribe automáticamente tras reconectar; expone Flows por subId.
    override fun valueEvents(path: String, q: QuerySpec?): Flow<NodeData> = callbackFlow { … }
    override suspend fun updateChildren(path: String, updates: Map<String, Any?>) { … }
}
```

**Gateway (backend, fuera de este repo — especificar al equipo de backend):** ver §8 y §14.

### 5.5 Registro y selección de motor

```kotlin
enum class DataEngine { FIREBASE, POSTGRES_WS }

object EngineRegistry {
    fun configure(engine: DataEngine, config: EngineConfig) { … }   // llamar en el arranque
    internal fun databaseDriver(): DatabaseDriver = …               // según engine activo
}
```

Uso desde la app (única línea que cambia entre motores):

```kotlin
// Firebase (modo compatibilidad)
EngineRegistry.configure(DataEngine.FIREBASE, EngineConfig.Firebase(app))

// Postgres/WS (transparente para el resto del código)
EngineRegistry.configure(DataEngine.POSTGRES_WS,
    EngineConfig.PostgresWs(url = "wss://gw.miproyecto.com/rtdb", tokenProvider = { authToken() }))
```

> Alternativa: integrarlo como extensión de `FirebaseOptions`/`FirebaseApp` para que incluso
> el arranque sea idéntico. Si más adelante se usa un contenedor de DI (Koin/Hilt/manual),
> el driver se registra ahí (según regla del proyecto de incorporar dependencias inyectadas
> al módulo correspondiente).

---

## 6. Tiempo real: Firebase (push) vs. PostgreSQL

Es el punto más delicado. Firebase emite deltas; en Postgres hay que producirlos en el
**gateway** (nunca en el cliente):

- **Opción A — `LISTEN`/`NOTIFY` + triggers** (recomendada para empezar): triggers en las
  tablas emiten `NOTIFY canal, payload` en INSERT/UPDATE/DELETE. El gateway hace `LISTEN` y
  reenvía por WS a los suscriptores de esa ruta/consulta. Simple y suficiente para volúmenes
  moderados. Límite: payload de `NOTIFY` ≤ 8 KB (enviar sólo la clave/diff y, si hace falta,
  releer).
- **Opción B — Replicación lógica (WAL, p. ej. `wal2json`/Debezium)**: el gateway consume el
  WAL y deriva eventos por tabla/fila. Escala mejor y captura todos los cambios (incluidos los
  externos), a costa de más infraestructura. Migración natural desde A.
- **Snapshot inicial + deltas:** al `subscribe`, el gateway envía primero un `value` con el
  estado actual (equivalente al primer `onDataChange`) y luego `child`/`value` incrementales.

El cliente mapea estos mensajes a los mismos `valueEvents` / `ChildEventListener` /
`snapshots` que hoy expone la fachada ⇒ **paridad de API**.

---

## 7. Mapeo de modelo de datos (impedancia árbol/documentos ⇄ relacional)

Debe ser **data-driven** (configurable), no hardcodeado. Dos estrategias, combinables:

1. **Fidelidad máxima (JSONB):** cada nodo/documento se guarda como `jsonb`
   (`tabla(path text primary key, data jsonb, updated_at)`, o una tabla por colección). Ventaja:
   soporta cualquier forma sin modelar; migración trivial desde Firebase. Desventaja: se
   pierde parte del poder relacional/consultas SQL nativas.
2. **Relacional tipado:** un **registro de mapeo** ruta/colección → tabla/columnas
   (declarativo, p. ej. en un data asset o config). Ventaja: SQL real, integridad referencial,
   índices. Desventaja: requiere modelar por entidad. Recomendado para las entidades núcleo de
   proyectos "SQL-first".

Sugerencia: **híbrido** — entidades núcleo relacionales; el resto en `jsonb`. La `QuerySpec`
neutral se traduce a `WHERE/ORDER BY/LIMIT` en el gateway. Las consultas de Firestore
(`whereEqualTo`, `orderBy`, rangos) mapean directo; las de RTDB (`orderByChild`+`startAt/endAt`)
también.

---

## 8. Frontera de confianza y autoridad del servidor (regla no negociable)

- El cliente **nunca** se conecta directo a Postgres ni porta credenciales de DB. Sólo habla
  con el **gateway** por WSS, autenticado con **JWT** (idealmente el mismo del módulo
  `firebase-auth`, para unificar identidad entre motores).
- La autorización (equivalente a las *Firebase Rules* del repo — `database.rules.json`,
  `firestore.rules`) se implementa en el gateway y/o con **RLS (Row-Level Security)** de
  Postgres. **Toda** operación se valida server-side.
- El gateway sanitiza/parametriza toda consulta (nada de SQL construido con input crudo →
  evitar inyección). La `QuerySpec` viaja como estructura, no como SQL.
- Auditoría/rate-limiting en el gateway.

> **Trust boundary explícita:** `App (no confiable)` → `WSS + JWT` → `Gateway (confiable:
> authZ, validación, rate-limit)` → `PostgreSQL (RLS)`.

---

## 9. Transacciones, batch y consistencia

- **Firestore** `runTransaction`/`WriteBatch` y **RTDB** `updateChildren` (multi-ruta atómico)
  se mapean a **una** transacción SQL en el gateway (mensaje `tx` con lista de operaciones).
- Para transacciones optimistas de Firestore (read-modify-write con reintento) el gateway
  usa versiones/`SELECT … FOR UPDATE` según convenga.
- Semántica de consistencia documentada por operación (Firebase es *eventually consistent* en
  algunos casos; Postgres es fuertemente consistente dentro de la transacción). Debe quedar
  claro para no crear expectativas divergentes entre motores.

---

## 10. Offline, reconexión y backpressure

- **Reconexión:** el `PostgresWsDriver` implementa reconexión con backoff exponencial y
  **re-suscripción automática** de todos los `subId` activos.
- **Offline/persistencia:** RTDB/Firestore tienen caché offline. En el driver WS es una
  **perilla** (por defecto off al inicio; luego caché local opcional con cola de escrituras).
- **Backpressure:** los `Flow` usan `callbackFlow` con buffer configurable; política de
  descarte/`conflate` para snapshots (nos interesa el último estado, no todos).

---

## 11. Estructura de módulos y build (KMP)

- Mantener un módulo por dominio (`firebase-database`, `firebase-firestore`, ...).
- Dentro de cada uno:
  - `commonMain/api` → fachada pública (hoy `expect`, pasa a común).
  - `commonMain/spi` → puertos (`internal`).
  - `commonMain/engine/postgres` → `PostgresWsDriver` (Ktor).
  - `androidMain`/`iosMain`/`jsMain` → sólo lo específico del `FirebaseDriver` nativo
    (donde sobreviven los `actual`).
- Nueva dependencia común: **Ktor client core + websockets + serialization**, y motor por
  plataforma (`ktor-client-okhttp`/`darwin`/`js`). Ktor es multiplataforma y no arrastra JDBC.
- Alternativa de empaquetado: separar el driver Postgres a un artefacto opcional
  (`firebase-database-postgres`) para no imponer Ktor a quien sólo use Firebase.

---

## 12. Estrategia de testing

- **Contract tests** (la pieza clave): una **única** batería de tests escrita contra el SPI y
  ejecutada **contra ambos drivers**. Garantiza paridad de comportamiento Firebase ⇄ Postgres.
- Firebase: seguir usando el **Firebase Emulator** (ya soportado en el repo:
  `run_integration_tests.sh`).
- Postgres/WS: `testcontainers` con Postgres + gateway; tests de reconexión, re-suscripción,
  transacciones y autorización (casos denegados).
- Regla del proyecto: **compilar** tras cada cambio y validar en las 3 plataformas.

---

## 13. Plan de migración incremental

1. **Fase 0 — Refactor no disruptivo:** introducir SPI + fachada delegando en `FirebaseDriver`
   (que envuelve el `expect/actual` actual). **La API pública no cambia**; el ecosistema sigue
   igual pero ya sobre la costura correcta. Riesgo bajo.
2. **Fase 1 — Driver Postgres/WS (piloto):** empezar por `firebase-database` (el árbol JSON es
   el que mejor mapea a un gateway genérico). Definir protocolo + gateway mínimo (Opción A de §6).
3. **Fase 2 — Contract tests** contra ambos motores; feature flag por módulo.
4. **Fase 3 — Firestore** (consultas más ricas) con el mismo patrón.
5. **Fase 4 — Endurecimiento:** RLS, replicación lógica, offline, observabilidad.

Cada fase es independiente y desplegable; el motor se activa por config (`DataEngine`), lo que
permite convivencia y rollback inmediato.

---

## 14. Perillas de tuning (data-driven)

- `DataEngine` activo (FIREBASE | POSTGRES_WS) — global o por módulo.
- Endpoint WSS, timeouts, backoff (mín/máx/factor), heartbeat/ping.
- Estrategia realtime (NOTIFY | WAL) y tamaño máximo de payload.
- Mapeo ruta→tabla (JSONB | relacional | híbrido) declarativo.
- Buffer/backpressure de `Flow`, política `conflate`.
- Caché offline on/off y tamaño de cola de escrituras.

---

## 15. Riesgos y deuda técnica

- **Paridad semántica imperfecta:** ordenamientos, tipos numéricos, reglas de nulos y
  *eventual consistency* difieren. Mitigar con contract tests y documentación por operación.
- **Realtime a escala:** `NOTIFY` tiene límites; prever migración a WAL.
- **Superficie amplia:** portar todos los módulos es trabajo grande. Se acota con el enfoque
  incremental (§13) y priorizando `database`/`firestore`.
- **Nuevo componente crítico (gateway):** pasa a ser parte de la frontera de confianza; su
  seguridad y disponibilidad son responsabilidad del backend (especificar con detalle).
- **Doble dependencia (Firebase nativo + Ktor):** peso del binario; mitigable separando el
  driver Postgres en artefacto opcional.

---

## 16. Implicancias para `ian` y proyectos futuros

- **`ian`:** adopción en **modo compatibilidad** primero (Fase 0): mismo código, sólo se pasa
  a usar la librería. El día que se quiera Postgres, se cambia **una** línea de config.
- **Proyectos futuros:** agregar un motor nuevo = **escribir un adaptador** del SPI; ni la
  API pública ni las capas superiores se enteran. Esa es la propiedad de extensibilidad que se
  buscaba.

---

## Apéndice — Requerimientos para el equipo de Backend (Gateway WS + Postgres)

Para implementar el gateway conforme a estos lineamientos:

1. **Transporte:** servidor WebSocket sobre TLS (`wss://`). Multiplexado de `reqId`/`subId`
   sobre una sola conexión por cliente. Heartbeat/ping-pong configurable.
2. **AuthN:** validar **JWT** en el handshake (y refresh); idealmente el mismo emisor que
   `firebase-auth`. Rechazar conexiones sin token válido.
3. **AuthZ:** motor de reglas equivalente a `database.rules.json`/`firestore.rules` +
   **RLS** en Postgres. Denegar por defecto.
4. **Protocolo:** implementar los mensajes de §5.4 (`get/set/update/subscribe/unsubscribe/tx`
   y respuestas `ack/error/value/child`). Formato JSON con `kotlinx.serialization`.
5. **Realtime:** Opción A (`LISTEN/NOTIFY` + triggers) para el piloto; ruta de evolución a
   replicación lógica (WAL) documentada.
6. **Snapshot inicial:** al suscribir, enviar estado actual y luego deltas.
7. **Transacciones:** `tx` ⇒ una transacción SQL atómica; soporte de update multi-ruta.
8. **Mapeo de datos:** definir tablas/JSONB según §7 y exponer el esquema de `QuerySpec`
   (orderBy, rangos, límites, filtros) → SQL parametrizado (anti-inyección).
9. **No funcionales:** rate-limiting, auditoría, límites de payload, métricas/observabilidad,
   reconexión idempotente (re-suscripción sin duplicar efectos).
