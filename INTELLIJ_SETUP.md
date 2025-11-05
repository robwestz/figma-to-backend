# IntelliJ IDEA / PyCharm Setup Guide

This guide will help you import and use the Figma-to-Backend plugin in IntelliJ IDEA or PyCharm.

## Prerequisites

- IntelliJ IDEA Ultimate or PyCharm Professional (2023.2 or later)
- JDK 17 or later
- Git
- Internet connection (required for first build to download dependencies from JetBrains repositories)

## Option 1: Import as a Project (for Development)

If you want to contribute to the plugin or customize it:

### 1. Clone the Repository

```bash
git clone https://github.com/robwestz/figma-to-backend.git
cd figma-to-backend
```

### 2. Open in IntelliJ IDEA

1. Launch IntelliJ IDEA or PyCharm
2. Click on `File → Open` (or `Open...` on the welcome screen)
3. Navigate to the cloned `figma-to-backend` directory
4. Click `OK`

IntelliJ will automatically detect this as a Gradle project and import it.

### 3. Build the Project

Once the project is imported:

1. Open the Gradle tool window (View → Tool Windows → Gradle)
2. Navigate to `figma-to-backend → Tasks → intellij → buildPlugin`
3. Double-click to run the build

Alternatively, use the terminal:

```bash
./gradlew buildPlugin
```

### 4. Run the Plugin in Development Mode

1. In the Gradle tool window, navigate to `Tasks → intellij → runIde`
2. Double-click to launch a new instance of PyCharm with the plugin installed

Or from the terminal:

```bash
./gradlew runIde
```

This will open a new PyCharm instance with the plugin pre-installed for testing.

## Option 2: Install as a Plugin (for Usage)

If you just want to use the plugin:

### 1. Build the Plugin Distribution

```bash
git clone https://github.com/robwestz/figma-to-backend.git
cd figma-to-backend
./gradlew buildPlugin
```

The plugin ZIP file will be created at: `build/distributions/figma-to-backend-1.0.0.zip`

### 2. Install in PyCharm/IntelliJ

1. Open PyCharm or IntelliJ IDEA
2. Go to `File → Settings → Plugins` (on macOS: `IntelliJ IDEA → Preferences → Plugins`)
3. Click the gear icon (⚙️) at the top
4. Select `Install Plugin from Disk...`
5. Navigate to `build/distributions/figma-to-backend-1.0.0.zip`
6. Click `OK`
7. Restart your IDE

### 3. Verify Installation

After restart:

1. Open any project (or create a new one)
2. Look for the "Figma Bridge" tool window (usually on the right side)
3. Or go to `View → Tool Windows → Figma Bridge`

## Using the Plugin

### Getting Your Figma Access Token

1. Log in to [Figma](https://www.figma.com/)
2. Go to Account Settings → Personal Access Tokens
3. Click "Create a new personal access token"
4. Give it a name (e.g., "PyCharm Plugin")
5. Copy the token (you won't be able to see it again!)

### Generating Code from Figma

1. Open the "Figma Bridge" tool window in PyCharm
2. Enter your Figma access token in the "Access Token" field
3. Enter a Figma file key (from the Figma file URL):
   - URL format: `https://www.figma.com/file/{FILE_KEY}/...`
   - Example: If URL is `https://www.figma.com/file/abc123xyz/MyDesign`
   - The file key is `abc123xyz`
4. (Optional) Enter a specific node ID if you only want to generate code for a specific frame
5. Click "Generate Code"
6. The plugin will generate HTML, CSS, and JavaScript files in your project

### Generated File Locations

The plugin intelligently detects your project structure and places files accordingly:

- **Django projects**: `templates/` and `static/`
- **Flask projects**: `templates/`
- **FastAPI projects**: `static/`
- **Generic projects**: Root directory

## Project Structure

```
figma-to-backend/
├── src/main/java/          # Java source code
│   └── com/robwestz/figmatobackend/
│       ├── models/         # Data models (FigmaFile, GeneratedCode)
│       ├── services/       # Core services (FigmaApiService, CodeGeneratorService)
│       └── ui/             # UI components (Tool window, actions)
├── src/main/resources/     # Plugin resources
│   └── META-INF/
│       └── plugin.xml      # Plugin configuration
├── src/test/              # Unit tests
├── build.gradle.kts       # Gradle build script
└── README.md              # Main documentation
```

## Development Commands

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Run the plugin in a test IDE
./gradlew runIde

# Clean build artifacts
./gradlew clean

# Verify plugin
./gradlew verifyPlugin
```

## Troubleshooting

### "Unsupported Java version" error

Make sure you're using JDK 17 or later:

```bash
java -version
```

If needed, set JAVA_HOME:

```bash
export JAVA_HOME=/path/to/jdk-17
```

### Gradle download issues

If you're behind a firewall, configure Gradle proxy in `gradle.properties`:

```properties
systemProp.http.proxyHost=proxy.example.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.example.com
systemProp.https.proxyPort=8080
```

### Build requires JetBrains repository access

The plugin depends on the IntelliJ Platform SDK and Python plugin from JetBrains repositories. The first build will download these dependencies from:
- `https://www.jetbrains.com/intellij-repository/releases`
- `https://cache-redirector.jetbrains.com/plugins.jetbrains.com/maven`

If these repositories are not accessible from your network, you may need to configure your firewall or proxy settings accordingly.

### Plugin not appearing after installation

1. Verify the plugin is enabled: `Settings → Plugins → Installed`
2. Check for any error messages in: `Help → Show Log in Finder/Explorer`
3. Try invalidating caches: `File → Invalidate Caches → Invalidate and Restart`

## Next Steps

- Read the [Quick Start Guide](QUICKSTART.md) for detailed usage examples
- Check out [EXAMPLES.md](EXAMPLES.md) for real-world usage scenarios
- See [DEVELOPMENT.md](DEVELOPMENT.md) for contribution guidelines
- Review [ARCHITECTURE.md](ARCHITECTURE.md) to understand the plugin structure

## Support

- Report issues: [GitHub Issues](https://github.com/robwestz/figma-to-backend/issues)
- Documentation: [README.md](README.md)
- Examples: [EXAMPLES.md](EXAMPLES.md)
