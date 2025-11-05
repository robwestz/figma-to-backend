# Figma to Backend - PyCharm Plugin

[![Build](https://github.com/robwestz/figma-to-backend/workflows/Build%20Plugin/badge.svg)](https://github.com/robwestz/figma-to-backend/actions)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/robwestz/figma-to-backend/releases)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

A PyCharm plugin that bridges the gap between Figma frontend designs and backend development. This plugin enables developers to seamlessly integrate Figma designs into their PyCharm workflow and generate production-ready frontend code.

**🚀 [Quick Start Guide](QUICKSTART.md)** | **📖 [Examples](EXAMPLES.md)** | **🔧 [Development](DEVELOPMENT.md)**

## Features

- **Direct Figma Integration**: Access Figma designs directly from PyCharm through a dedicated tool window
- **Design-to-Code Generation**: Automatically convert Figma designs into HTML, CSS, and JavaScript code
- **Intelligent File Placement**: The plugin intelligently places generated code in appropriate project directories
- **Production-Ready Code**: Generates clean, well-structured frontend code ready for production use

## Installation

### From Source

1. Clone this repository:
   ```bash
   git clone https://github.com/robwestz/figma-to-backend.git
   cd figma-to-backend
   ```

2. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

3. Install the plugin in PyCharm:
   - Open PyCharm
   - Go to `File > Settings > Plugins`
   - Click the gear icon and select `Install Plugin from Disk...`
   - Navigate to `build/distributions/figma-to-backend-1.0.0.zip`
   - Click OK and restart PyCharm

## Usage

### 1. Get Your Figma Access Token

1. Log in to your Figma account
2. Go to Account Settings → Personal Access Tokens
3. Generate a new token with appropriate permissions
4. Copy the token (you'll need it for the plugin)

### 2. Open the Figma Tool Window

- In PyCharm, go to `View > Tool Windows > Figma`
- Or use the `Tools > Generate Code from Figma` menu action

### 3. Configure the Plugin

1. Enter your Figma access token in the "Figma Access Token" field
2. Click "Validate" to verify your token
3. Enter the Figma file key from your design URL:
   - Example URL: `https://www.figma.com/file/ABC123xyz/My-Design`
   - File key: `ABC123xyz`
4. (Optional) Enter a specific node name to generate code for only that component

### 4. Generate Code

1. Click "Generate Code from Figma"
2. The plugin will:
   - Fetch your Figma design
   - Analyze the design structure
   - Generate HTML, CSS, and JavaScript files
   - Place them intelligently in your project structure
   - Open the generated HTML file for review

## Generated Code Structure

The plugin generates three files:

- **[design-name].html**: Complete HTML structure with semantic markup
- **[design-name].css**: Styles including layout, colors, typography, and responsive design
- **[design-name].js**: JavaScript file with basic interactivity hooks

## Project Structure

```
figma-to-backend/
├── src/main/java/com/robwestz/figmatobackend/
│   ├── models/              # Data models for Figma API
│   │   ├── FigmaFile.java
│   │   └── GeneratedCode.java
│   ├── services/            # Core services
│   │   ├── FigmaApiService.java
│   │   └── CodeGeneratorService.java
│   └── ui/                  # User interface components
│       ├── FigmaToolWindowFactory.java
│       ├── FigmaToolWindowPanel.java
│       └── GenerateCodeAction.java
├── src/main/resources/
│   └── META-INF/
│       └── plugin.xml       # Plugin configuration
├── build.gradle.kts         # Build configuration
└── README.md
```

## Building and Development

### Requirements

- JDK 17 or higher
- Gradle 8.5 (included via Gradle wrapper)
- IntelliJ IDEA or PyCharm 2023.2 or higher
- Internet connection to download IntelliJ SDK dependencies

### Build Commands

```bash
# Build the plugin
./gradlew buildPlugin

# Run plugin in a test IDE instance
./gradlew runIde

# Run tests
./gradlew test

# Verify plugin
./gradlew verifyPlugin
```

### Build Notes

The plugin requires access to JetBrains repositories to download the IntelliJ Platform SDK. If you encounter network issues:

1. Ensure you have internet access to `www.jetbrains.com` and `cache-redirector.jetbrains.com`
2. Check your firewall and proxy settings
3. The Gradle wrapper will automatically download the correct Gradle version (8.5)

## Configuration

The plugin supports the following project structures:

- Django: `templates/`, `static/`
- Flask: `templates/`, `static/`
- FastAPI: `static/`, `templates/`
- Generic: `frontend/`, `public/`, `src/`, `web/`

Generated files will be placed in the most appropriate directory based on your project structure.

## Troubleshooting

### Invalid Access Token
- Ensure your Figma access token has not expired
- Verify you have proper permissions to access the Figma file
- Try regenerating your access token

### File Not Found
- Double-check the file key from your Figma URL
- Ensure the file is not private or you have access to it

### Generation Errors
- Verify your Figma design has proper structure
- Check that nodes have appropriate names and properties
- Review the status area in the tool window for detailed error messages

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source. Please check the LICENSE file for more details.

## Additional Resources

- **[Quick Start Guide](QUICKSTART.md)** - Get up and running in 5 minutes
- **[Usage Examples](EXAMPLES.md)** - Practical examples and integration patterns
- **[Development Guide](DEVELOPMENT.md)** - Architecture and developer documentation
- **[Contributing](CONTRIBUTING.md)** - How to contribute to the project

## Support

For issues, questions, or contributions, please visit:
- **Issues**: https://github.com/robwestz/figma-to-backend/issues
- **Discussions**: https://github.com/robwestz/figma-to-backend/discussions
- **Email**: support@robwestz.com