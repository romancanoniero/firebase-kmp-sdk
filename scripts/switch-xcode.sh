#!/bin/bash

# Script para cambiar entre versiones de Xcode

case "$1" in
    "16")
        echo "Cambiando a Xcode 16..."
        sudo xcode-select -s /Applications/Xcode-16.app/Contents/Developer
        ;;
    "26")
        echo "Cambiando a Xcode 26..."
        sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
        ;;
    *)
        echo "Uso: ./switch-xcode.sh [16|26]"
        echo ""
        echo "Xcode actual:"
        xcode-select -p
        xcodebuild -version
        exit 1
        ;;
esac

echo ""
echo "Xcode activo:"
xcodebuild -version
