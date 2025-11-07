#!/bin/bash

# FigmaToPyCharmBridge - One-Click Setup Script
# This script automates the complete setup of the plugin development environment

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print functions
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Header
clear
print_header "Figma to PyCharm Bridge - Setup"
echo "Detta script kommer att sätta upp utvecklingsmiljön för pluginet."
echo ""

# Check prerequisites
print_header "Kontrollerar förutsättningar..."

# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        print_success "Java $JAVA_VERSION hittades"
    else
        print_error "Java 17 eller senare krävs. Du har version $JAVA_VERSION"
        exit 1
    fi
else
    print_error "Java hittades inte. Installera Java 17 eller senare."
    echo ""
    echo "För Ubuntu/Debian: sudo apt install openjdk-17-jdk"
    echo "För macOS: brew install openjdk@17"
    echo "För Windows: Ladda ner från https://adoptium.net/"
    exit 1
fi

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    print_error "Gradle wrapper hittades inte!"
    exit 1
fi

print_success "Gradle wrapper hittades"

# Make gradlew executable
chmod +x ./gradlew
print_success "Gradle wrapper är körbar"

# Run Gradle tasks
print_header "Kör Gradle-uppgifter..."

print_info "Laddar ner beroenden (detta kan ta en stund första gången)..."
if ./gradlew --version > /dev/null 2>&1; then
    print_success "Gradle konfigurerad korrekt"
else
    print_error "Problem med Gradle-konfigurationen"
    exit 1
fi

print_info "Förbereder IntelliJ-pluginets beroenden..."
# Note: This might fail in restricted environments, but the project structure is ready
if ./gradlew tasks --console=plain > /tmp/gradle_tasks.log 2>&1; then
    print_success "Gradle-uppgifter laddade"
else
    print_info "Vissa beroenden kanske inte laddades (nätverksbegränsningar)"
    print_info "Projektet är redo för lokal utveckling"
fi

# Success
print_header "Installation klar!"

echo ""
echo -e "${GREEN}✓ Projektet är redo för utveckling!${NC}"
echo ""
echo "Nästa steg:"
echo ""
echo "1. Öppna projektet i IntelliJ IDEA eller PyCharm:"
echo "   File → Open → Välj denna mapp"
echo ""
echo "2. Bygg pluginet:"
echo "   ./gradlew buildPlugin"
echo ""
echo "3. Kör pluginet i en sandbox-IDE:"
echo "   ./gradlew runIde"
echo ""
echo "4. Skapa en distributionsfil:"
echo "   ./gradlew buildPlugin"
echo "   Filen finns sedan i: build/distributions/"
echo ""
echo "5. Installera pluginet manuellt:"
echo "   I PyCharm: Settings → Plugins → ⚙️ → Install Plugin from Disk"
echo "   Välj filen från build/distributions/"
echo ""
echo -e "${BLUE}För mer information, se README.md${NC}"
echo ""
