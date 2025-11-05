# Development Guide

This guide provides detailed information for developers who want to contribute to or customize the Figma to Backend plugin.

## Project Structure

```
figma-to-backend/
├── gradle/                          # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── java/com/robwestz/figmatobackend/
│   │   │   ├── models/              # Data models
│   │   │   │   ├── FigmaFile.java   # Figma API response model
│   │   │   │   └── GeneratedCode.java # Code generation output model
│   │   │   ├── services/            # Business logic
│   │   │   │   ├── FigmaApiService.java      # Figma API integration
│   │   │   │   └── CodeGeneratorService.java # Code generation logic
│   │   │   └── ui/                  # User interface
│   │   │       ├── FigmaToolWindowFactory.java
│   │   │       ├── FigmaToolWindowPanel.java
│   │   │       └── GenerateCodeAction.java
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── plugin.xml       # Plugin configuration
│   │       └── icons/
│   │           └── figma.svg        # Plugin icon
│   └── test/
│       └── java/                    # Unit tests
├── build.gradle.kts                 # Gradle build script
├── settings.gradle.kts              # Gradle settings
├── gradle.properties                # Gradle properties
└── README.md                        # Main documentation
```

## Architecture Overview

### 1. FigmaApiService

Handles all communication with the Figma REST API:

- **Token Management**: Stores and validates Figma access tokens
- **File Retrieval**: Fetches design files from Figma
- **Node Export**: Exports specific nodes as images
- **Error Handling**: Manages API errors and network issues

Key methods:
- `setAccessToken(String)`: Configure the API token
- `validateAccessToken()`: Verify token validity
- `getFile(String)`: Fetch a Figma file by key
- `exportNode(String, String, String)`: Export a node as an image

### 2. CodeGeneratorService

Transforms Figma designs into frontend code:

- **HTML Generation**: Creates semantic HTML structure from Figma nodes
- **CSS Generation**: Converts Figma styling to CSS
- **JavaScript Generation**: Provides basic interactivity scaffolding
- **File Placement**: Intelligently suggests file locations in the project

Key methods:
- `generateCode(FigmaFile, String)`: Main code generation entry point
- `suggestFilePath(Project, String)`: Determines best location for generated files
- `processNode(Node, StringBuilder, StringBuilder, int)`: Recursive node processing

### 3. FigmaToolWindowPanel

The main UI component:

- **Token Input**: Secure field for Figma access token
- **File Key Input**: Field for specifying Figma file
- **Node Filter**: Optional field to generate code for specific nodes
- **Status Display**: Real-time feedback on operations
- **Instructions**: Built-in help text

### 4. Data Models

#### FigmaFile
Represents the structure of a Figma design file:
- Document hierarchy
- Node properties (position, size, styling)
- Fill and stroke information
- Text styling

#### GeneratedCode
Encapsulates the generated code:
- HTML content
- CSS content
- JavaScript content
- Suggested file path

## Development Setup

### Prerequisites

1. Install JDK 17 or higher
2. Clone the repository
3. Ensure you have internet access to JetBrains repositories

### Building the Plugin

```bash
# Clean and build
./gradlew clean build

# Build the plugin distribution
./gradlew buildPlugin

# The plugin ZIP will be in: build/distributions/
```

### Running in Development Mode

```bash
# Launch a test IDE instance with the plugin installed
./gradlew runIde

# This will:
# 1. Download the appropriate IDE version
# 2. Install your plugin
# 3. Launch the IDE
```

### Testing

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# View test results
open build/reports/tests/test/index.html
```

## Code Generation Algorithm

The code generation follows this process:

1. **Fetch Design**: Retrieve the Figma file via API
2. **Parse Structure**: Traverse the document tree
3. **Generate HTML**: Create elements for each node
   - FRAME/GROUP → `<div>`
   - TEXT → `<span>`
   - Other types → `<div>` or `<svg>`
4. **Generate CSS**: Extract styling information
   - Positioning and dimensions
   - Colors (fills and strokes)
   - Typography (for text nodes)
   - Layout properties
5. **Generate JS**: Create basic interactivity template
6. **Save Files**: Write to appropriate project directory

## Extending the Plugin

### Adding New Figma Node Types

To support additional Figma node types:

1. Update `FigmaFile.Node` model if needed
2. Add case in `CodeGeneratorService.getHtmlElementType()`
3. Implement custom processing in `processNode()`

Example:
```java
case "CUSTOM_TYPE":
    return "custom-element";
```

### Customizing Code Generation

Modify `CodeGeneratorService.generateCode()` to:

- Change HTML structure
- Add CSS frameworks (Bootstrap, Tailwind, etc.)
- Include JavaScript frameworks (React, Vue, etc.)
- Implement responsive design patterns

### Adding New UI Features

To extend the tool window:

1. Modify `FigmaToolWindowPanel`
2. Add new Swing components
3. Implement event handlers
4. Update status feedback

## API Reference

### Figma API

The plugin uses Figma's REST API v1:

- Base URL: `https://api.figma.com/v1`
- Authentication: Bearer token in `X-Figma-Token` header
- Main endpoints:
  - `GET /files/{file_key}`: Get file data
  - `GET /images/{file_key}`: Export images
  - `GET /me`: Validate token

Documentation: https://www.figma.com/developers/api

### IntelliJ Platform SDK

Key platform services used:

- `ApplicationService`: Singleton services
- `ToolWindowFactory`: Creates tool windows
- `VirtualFile`: File system abstraction
- `FileEditorManager`: Opens files in editor
- `LocalFileSystem`: File system access

Documentation: https://plugins.jetbrains.com/docs/intellij/

## Troubleshooting

### Build Issues

**Problem**: Gradle can't download dependencies
**Solution**: Check internet connection and firewall settings

**Problem**: Wrong Gradle version
**Solution**: Use the wrapper: `./gradlew` instead of `gradle`

### Runtime Issues

**Problem**: Plugin not appearing in IDE
**Solution**: Verify `plugin.xml` is correct and rebuild

**Problem**: API calls failing
**Solution**: 
- Verify access token is valid
- Check Figma file permissions
- Ensure file key is correct

### Code Generation Issues

**Problem**: Generated code is malformed
**Solution**:
- Check Figma design structure
- Verify node names don't contain special characters
- Review generated CSS for conflicts

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass: `./gradlew test`
6. Submit a pull request

## Code Style

- Follow Java naming conventions
- Use meaningful variable names
- Add JavaDoc for public methods
- Keep methods focused and single-purpose
- Maximum line length: 120 characters

## Debugging

### Enable Debug Logging

Add to `plugin.xml`:
```xml
<extensions defaultExtensionNs="com.intellij">
    <applicationConfigurable instance="..."/>
</extensions>
```

### View Logs

- **IDE Logs**: Help → Show Log in Finder/Explorer
- **Plugin Logs**: Use `Logger.getInstance()`

Example:
```java
private static final Logger LOG = Logger.getInstance(FigmaApiService.class);
LOG.info("Fetching Figma file: " + fileKey);
```

## Performance Considerations

- API calls are made on background threads
- UI updates use `SwingUtilities.invokeLater()`
- Large designs may take time to process
- Consider caching API responses

## Security Notes

- Never commit access tokens
- Store tokens securely using IDE credential storage
- Validate all user inputs
- Sanitize file paths before writing

## Release Process

1. Update version in `build.gradle.kts`
2. Update `plugin.xml` change notes
3. Build: `./gradlew buildPlugin`
4. Test the plugin ZIP
5. Create GitHub release
6. Upload to JetBrains Plugin Repository

## Resources

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- [Figma API Documentation](https://www.figma.com/developers/api)
- [Gradle IntelliJ Plugin](https://github.com/JetBrains/gradle-intellij-plugin)
- [Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)

## License

See LICENSE file for details.

## Support

For issues and questions:
- GitHub Issues: https://github.com/robwestz/figma-to-backend/issues
- Email: support@robwestz.com
