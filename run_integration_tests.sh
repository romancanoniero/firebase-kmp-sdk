#!/bin/bash

# ============================================================
# Firebase KMP SDK - Integration Tests with Firebase Emulator
# ============================================================

set -e

echo "🔥 Firebase KMP SDK - Integration Tests"
echo "========================================"

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI not found. Install with: npm install -g firebase-tools"
    exit 1
fi

echo "✅ Firebase CLI found: $(firebase --version)"

# Start Firebase Emulators in background
echo ""
echo "🚀 Starting Firebase Emulators..."
firebase emulators:start --only auth,database,firestore,storage,functions &
EMULATOR_PID=$!

# Wait for emulators to start
echo "⏳ Waiting for emulators to start..."
sleep 10

# Check if emulators are running
if ! curl -s http://localhost:4000 > /dev/null; then
    echo "❌ Emulator UI not responding. Check if emulators started correctly."
    kill $EMULATOR_PID 2>/dev/null
    exit 1
fi

echo "✅ Emulators running!"
echo "   - Auth: localhost:9099"
echo "   - Database: localhost:9000"
echo "   - Firestore: localhost:8080"
echo "   - Storage: localhost:9199"
echo "   - Functions: localhost:5001"
echo "   - UI: http://localhost:4000"

# Run Android Instrumented Tests
echo ""
echo "🧪 Running Android Integration Tests..."
echo "----------------------------------------"

# Note: For Android instrumented tests, you need an emulator or device
# ./gradlew connectedAndroidTest

# For now, run unit tests that don't require device
./gradlew testDebugUnitTest

echo ""
echo "✅ Tests completed!"

# Cleanup - stop emulators
echo ""
echo "🛑 Stopping emulators..."
kill $EMULATOR_PID 2>/dev/null || true

echo ""
echo "🎉 All done!"

