#!/bin/bash

# ============================================================
# Firebase KMP SDK - Publish to Maven
# ============================================================

set -e

echo "🔥 Firebase KMP SDK - Maven Publishing"
echo "========================================"

# Check for required environment variables
if [ -z "$MAVEN_CENTRAL_USERNAME" ] || [ -z "$MAVEN_CENTRAL_PASSWORD" ]; then
    echo "⚠️  Maven Central credentials not set."
    echo "    Set MAVEN_CENTRAL_USERNAME and MAVEN_CENTRAL_PASSWORD"
    echo ""
    echo "📦 Publishing to Maven Local instead..."
    ./gradlew publishToMavenLocal
    echo ""
    echo "✅ Published to ~/.m2/repository"
    echo ""
    echo "To use locally, add to your project:"
    echo "  repositories { mavenLocal() }"
    echo "  dependencies { implementation(\"com.iyr.firebase:firebase-core:1.0.0-SNAPSHOT\") }"
    exit 0
fi

echo "📦 Publishing to Maven Central..."

# Clean and build
./gradlew clean

# Run tests first
./gradlew testDebugUnitTest

# Publish
./gradlew publish

echo ""
echo "✅ Published to Maven Central!"
echo ""
echo "Artifacts:"
echo "  com.iyr.firebase:firebase-core:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-auth:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-database:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-firestore:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-storage:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-functions:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-messaging:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-analytics:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-crashlytics:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-remote-config:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-performance:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-appcheck:1.0.0-SNAPSHOT"
echo "  com.iyr.firebase:firebase-inappmessaging:1.0.0-SNAPSHOT"

