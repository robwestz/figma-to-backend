# 🚀 Snabbstart - Figma to PyCharm Bridge

En enkel guide för att komma igång på under 5 minuter!

## Steg 1: Kontrollera förutsättningar ✓

Innan du börjar, se till att du har:
- ✅ Java 17 eller senare installerat
- ✅ Git installerat (för att klona projektet)

**Kontrollera Java-version:**
```bash
java -version
```

Om du behöver installera Java, följ instruktionerna i [README.md](README.md#-förutsättningar).

## Steg 2: Klona projektet 📥

```bash
git clone https://github.com/robwestz/figma-to-backend.git
cd figma-to-backend
```

## Steg 3: Kör setup-scriptet 🎯

### På Linux/macOS:
```bash
./setup.sh
```

### På Windows:
```cmd
setup.bat
```

**Detta tar bara några sekunder!** Scriptet kommer att:
- Kontrollera dina förutsättningar
- Konfigurera Gradle
- Förbereda projektet

## Steg 4: Testa pluginet 🎮

Nu kan du köra pluginet direkt:

```bash
./gradlew runIde
```

Detta startar en PyCharm-instans med pluginet installerat!

## Steg 5: Hitta verktygsfönstret 🔍

När PyCharm startar:
1. Leta efter **"Figma Bridge"** i höger sidofält
2. Klicka på ikonen för att öppna verktygsfönstret
3. Du kommer att se "Figma-anslutning" med en "Autentisera"-knapp

## Alternativ: Bygg och installera manuellt 🔧

Om du vill installera pluginet i din befintliga PyCharm:

```bash
# Bygg pluginet
./gradlew buildPlugin

# Hitta distributionsfilen i build/distributions/
```

Sedan i PyCharm:
1. **Settings** (⚙️) → **Plugins**
2. Klicka på kugghjulet ⚙️ → **Install Plugin from Disk...**
3. Välj ZIP-filen från `build/distributions/`
4. Starta om PyCharm

## Nästa steg 📚

- Läs [README.md](README.md) för fullständig dokumentation
- Se alla Gradle-kommandon: `./gradlew tasks`
- Börja utveckla: Öppna projektet i IntelliJ IDEA

## Hjälp! Något gick fel 🆘

### "Java not found"
Installera Java 17: Se [README.md](README.md#installera-java)

### "Gradle error"
Prova:
```bash
./gradlew --stop
./gradlew clean build
```

### "Plugin doesn't load"
- Kontrollera att PyCharm är version 2024.1 eller senare
- Se till att Java 17+ används

## Support 💬

Har du frågor? Skapa en issue på GitHub!

---

**Grattis! 🎉 Du är nu redo att använda Figma to PyCharm Bridge!**
