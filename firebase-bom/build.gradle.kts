/**
 * Firebase KMP SDK - Bill of Materials (BOM)
 * 
 * El BOM permite gestionar las versiones de todos los módulos Firebase KMP
 * desde un único lugar, garantizando compatibilidad entre módulos.
 * 
 * Uso:
 * ```kotlin
 * dependencies {
 *     implementation(platform("io.github.romancanoniero:firebase-bom:1.2.0"))
 *     
 *     // Sin especificar versión - BOM la gestiona
 *     implementation("io.github.romancanoniero:firebase-core")
 *     implementation("io.github.romancanoniero:firebase-auth")
 *     implementation("io.github.romancanoniero:firebase-database")
 * }
 * ```
 */

plugins {
    `java-platform`
    alias(libs.plugins.vanniktech.mavenPublish)
}

// Permitir dependencias entre constraints
javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        // Core
        api("io.github.romancanoniero:firebase-core:${project.version}")
        
        // Authentication
        api("io.github.romancanoniero:firebase-auth:${project.version}")
        
        // Databases
        api("io.github.romancanoniero:firebase-database:${project.version}")
        api("io.github.romancanoniero:firebase-firestore:${project.version}")
        
        // Storage
        api("io.github.romancanoniero:firebase-storage:${project.version}")
        
        // Cloud Functions
        api("io.github.romancanoniero:firebase-functions:${project.version}")
        
        // Messaging (FCM)
        api("io.github.romancanoniero:firebase-messaging:${project.version}")
        
        // Analytics & Monitoring
        api("io.github.romancanoniero:firebase-analytics:${project.version}")
        api("io.github.romancanoniero:firebase-crashlytics:${project.version}")
        api("io.github.romancanoniero:firebase-performance:${project.version}")
        
        // Configuration
        api("io.github.romancanoniero:firebase-remote-config:${project.version}")
        
        // Security
        api("io.github.romancanoniero:firebase-appcheck:${project.version}")
        
        // Engagement
        api("io.github.romancanoniero:firebase-inappmessaging:${project.version}")
    }
}






