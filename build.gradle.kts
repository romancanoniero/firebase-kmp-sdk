/**
 * Firebase KMP SDK
 * 
 * Librería multiplataforma que replica la API del Firebase Android SDK
 * para Kotlin Multiplatform (Android, iOS, JS).
 * 
 * Paquete base: com.iyr.firebase
 * 
 * Módulos:
 * - firebase-core: FirebaseApp, FirebaseOptions
 * - firebase-auth: Autenticación completa
 * - firebase-database: Realtime Database
 * - firebase-firestore: Cloud Firestore
 * - firebase-storage: Cloud Storage
 * - firebase-functions: Cloud Functions client
 * - firebase-messaging: Push notifications (FCM)
 */
plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    group = "com.iyr.firebase"
    version = "1.0.0-SNAPSHOT"
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.vanniktech.maven.publish")) {
            extensions.configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
                coordinates(
                    groupId = "com.iyr.firebase",
                    artifactId = project.name,
                    version = project.version.toString()
                )
                
                pom {
                    name.set("Firebase KMP SDK - ${project.name.removePrefix("firebase-").replaceFirstChar { it.uppercase() }}")
                    description.set("Firebase ${project.name.removePrefix("firebase-")} para Kotlin Multiplatform (Android, iOS, JS)")
                    url.set("https://github.com/iyr/firebase-kmp-sdk")
                    
                    licenses {
                        license {
                            name.set("Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set("romancanoniero")
                            name.set("Roman Canoniero")
                            email.set("romancanoniero@gmail.com")
                        }
                        developer {
                            id.set("iyr")
                            name.set("IYR Team")
                            email.set("dev@iyr.com")
                        }
                    }
                    
                    scm {
                        url.set("https://github.com/iyr/firebase-kmp-sdk")
                        connection.set("scm:git:git://github.com/iyr/firebase-kmp-sdk.git")
                        developerConnection.set("scm:git:ssh://github.com/iyr/firebase-kmp-sdk.git")
                    }
                }
            }
        }
    }
}
