# Figma to PyCharm Bridge

Ett IntelliJ-plugin som integrerar Figma-designer direkt i PyCharm för att underlätta frontend-till-backend-utveckling.

## 🚀 Snabbstart (One-Click Setup)

### Linux/macOS
```bash
chmod +x setup.sh
./setup.sh
```

### Windows
```cmd
setup.bat
```

Dessa script kommer att:
- ✓ Kontrollera att Java 17+ är installerat
- ✓ Konfigurera Gradle wrapper
- ✓ Ladda ner beroenden
- ✓ Förbereda projektet för utveckling

## 📋 Förutsättningar

- **Java 17 eller senare** (rekommenderat: Java 17)
- **IntelliJ IDEA** eller **PyCharm** (Community eller Ultimate)
- **Git** (för att klona projektet)

### Installera Java

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**macOS:**
```bash
brew install openjdk@17
```

**Windows:**
Ladda ner från [Adoptium](https://adoptium.net/)

## 🛠️ Manuell installation

Om du föredrar att sätta upp projektet manuellt:

1. **Klona projektet**
   ```bash
   git clone https://github.com/robwestz/figma-to-backend.git
   cd figma-to-backend
   ```

2. **Bygg projektet**
   ```bash
   ./gradlew build
   ```

3. **Kör pluginet i sandbox**
   ```bash
   ./gradlew runIde
   ```

## 🎯 Användning

### Bygga pluginet
```bash
./gradlew buildPlugin
```
Distributionsfilen skapas i `build/distributions/`

### Testa pluginet
```bash
./gradlew runIde
```
Detta startar en PyCharm-instans med pluginet installerat

### Installera i befintlig PyCharm
1. Bygg pluginet: `./gradlew buildPlugin`
2. Öppna PyCharm
3. Gå till **Settings** → **Plugins** → **⚙️** → **Install Plugin from Disk...**
4. Välj filen från `build/distributions/`
5. Starta om PyCharm

### Använda pluginet
1. Efter installation, hitta "Figma Bridge" i höger sidofält
2. Klicka för att öppna verktygsfönstret
3. Använd "Autentisera"-knappen för att koppla till Figma (kommer i framtida version)

## 📁 Projektstruktur

```
FigmaToPyCharmBridge/
├── .gitignore
├── README.md
├── setup.sh                    # Linux/macOS setup script
├── setup.bat                   # Windows setup script
├── build.gradle.kts            # Gradle-konfiguration
├── gradle.properties           # Gradle-egenskaper
├── settings.gradle.kts         # Projektinställningar
├── gradlew                     # Gradle wrapper (Unix)
├── gradlew.bat                 # Gradle wrapper (Windows)
└── src/
    └── main/
        ├── kotlin/
        │   └── com/minorg/figmabridge/
        │       └── toolwindow/
        │           ├── FigmaToolWindowFactory.kt
        │           └── FigmaToolWindowPanel.kt
        └── resources/
            └── META-INF/
                ├── plugin.xml
                └── pluginIcon.svg
```

## 🔧 Utveckling

### Gradle-kommandon

| Kommando | Beskrivning |
|----------|-------------|
| `./gradlew tasks` | Lista alla tillgängliga uppgifter |
| `./gradlew build` | Bygg projektet |
| `./gradlew buildPlugin` | Bygg plugin-distributionen |
| `./gradlew runIde` | Kör pluginet i sandbox-IDE |
| `./gradlew test` | Kör tester |
| `./gradlew verifyPlugin` | Verifiera plugin-konfiguration |

### Öppna i IDE

1. Öppna IntelliJ IDEA eller PyCharm
2. **File** → **Open**
3. Välj projektmappen
4. Vänta på att Gradle importerar projektet

## 🎨 Funktioner

### Nuvarande (v0.1.0)
- ✅ Tool Window "Figma Bridge"
- ✅ Grundläggande UI med autentiseringsknapp
- ✅ Plugin-ikon

### Planerat
- 🔄 Figma OAuth-autentisering
- 🔄 Hämta Figma-designer
- 🔄 Generera frontend-kod (HTML/CSS/React)
- 🔄 Integration med Django/Flask-backends

## 📝 Teknisk stack

- **Språk:** Kotlin 1.9.23
- **Build-verktyg:** Gradle 8.2.1 (Kotlin DSL)
- **Framework:** IntelliJ Platform SDK
- **Target IDE:** PyCharm Community Edition 2024.1
- **Java-version:** 17

## 🐛 Felsökning

### Gradle-fel
Om du får fel vid körning av Gradle:
```bash
./gradlew --stop  # Stoppa alla Gradle-processer
./gradlew clean   # Rensa byggfiler
./gradlew build   # Bygg igen
```

### Plugin laddas inte
- Kontrollera att Java-versionen är 17 eller senare
- Se till att PyCharm är version 2024.1 eller senare
- Kontrollera `build/distributions/` för distributionsfilen

### Beroenden laddar inte
Om du är bakom en företagsfirewall eller proxy:
```bash
# Lägg till i gradle.properties
systemProp.http.proxyHost=proxy.company.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.company.com
systemProp.https.proxyPort=8080
```

## 📄 Licens

[Lägg till licensinformation här]

## 👥 Bidra

Bidrag är välkomna! Skapa gärna en issue eller pull request.

## 📧 Kontakt

- **Vendor:** MinOrg
- **Email:** support@minorg.com
- **URL:** https://www.minorg.com