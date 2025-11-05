# Architecture Documentation

## System Architecture

This document provides a detailed architectural overview of the Figma to Backend plugin.

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        PyCharm IDE                               │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    User Interface Layer                    │  │
│  │  ┌──────────────────┐         ┌──────────────────┐       │  │
│  │  │ FigmaToolWindow  │         │ GenerateCode     │       │  │
│  │  │     Panel        │◄────────┤     Action       │       │  │
│  │  │  (Swing UI)      │         │  (Menu Item)     │       │  │
│  │  └────────┬─────────┘         └──────────────────┘       │  │
│  └───────────┼──────────────────────────────────────────────┘  │
│              │                                                   │
│  ┌───────────▼──────────────────────────────────────────────┐  │
│  │                   Service Layer                           │  │
│  │  ┌──────────────────┐         ┌──────────────────┐       │  │
│  │  │  FigmaApiService │         │ CodeGenerator    │       │  │
│  │  │                  │         │    Service       │       │  │
│  │  │ • Token Mgmt     │         │ • HTML Gen       │       │  │
│  │  │ • API Calls      │         │ • CSS Gen        │       │  │
│  │  │ • Validation     │         │ • JS Gen         │       │  │
│  │  │ • Error Handle   │         │ • File Placement │       │  │
│  │  └────────┬─────────┘         └────────┬─────────┘       │  │
│  └───────────┼──────────────────────────┼────────────────────┘  │
│              │                           │                       │
│  ┌───────────▼───────────────────────────▼───────────────────┐  │
│  │                    Model Layer                             │  │
│  │  ┌──────────────────┐         ┌──────────────────┐       │  │
│  │  │   FigmaFile      │         │  GeneratedCode   │       │  │
│  │  │                  │         │                  │       │  │
│  │  │ • Document       │         │ • HTML           │       │  │
│  │  │ • Nodes          │         │ • CSS            │       │  │
│  │  │ • Styling        │         │ • JavaScript     │       │  │
│  │  │ • Metadata       │         │ • File Path      │       │  │
│  │  └──────────────────┘         └──────────────────┘       │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP
                              ▼
                    ┌──────────────────┐
                    │   Figma REST     │
                    │      API         │
                    └──────────────────┘
```

## Component Interaction Flow

### User Workflow

```
1. User Opens Tool Window
   └─> FigmaToolWindowPanel initialized
       └─> UI components created
       └─> Event listeners attached

2. User Enters Token
   └─> Token stored in FigmaApiService
       └─> Can be validated via API call

3. User Clicks "Validate"
   └─> FigmaApiService.validateAccessToken()
       └─> API call to /v1/me endpoint
       └─> Result displayed in UI

4. User Enters File Key & Clicks "Generate"
   └─> Background thread spawned
       └─> FigmaApiService.getFile(fileKey)
           └─> API call to /v1/files/{key}
           └─> JSON parsed into FigmaFile model
       
       └─> CodeGeneratorService.generateCode(figmaFile)
           └─> Traverse node tree
           └─> Generate HTML structure
           └─> Extract CSS styles
           └─> Create JS boilerplate
           └─> Return GeneratedCode model
       
       └─> CodeGeneratorService.suggestFilePath()
           └─> Analyze project structure
           └─> Determine best directory
       
       └─> Write files to disk
           └─> Create directories if needed
           └─> Save HTML, CSS, JS files
           └─> Refresh VFS
       
       └─> Open files in editor
           └─> FileEditorManager.openFile()

5. User Reviews Generated Code
   └─> Files opened in editor
   └─> Can customize as needed
```

## Layer-by-Layer Details

### 1. UI Layer (Presentation)

**Components:**
- `FigmaToolWindowFactory` - Creates tool window
- `FigmaToolWindowPanel` - Main UI panel with form
- `GenerateCodeAction` - Menu action

**Responsibilities:**
- User input collection
- Form validation
- Event handling
- Status display
- Progress feedback

**Technologies:**
- Java Swing
- IntelliJ Platform UI components
- SwingUtilities for thread safety

### 2. Service Layer (Business Logic)

#### FigmaApiService

**Responsibilities:**
- Manage Figma access tokens
- Make HTTP requests to Figma API
- Parse API responses
- Handle network errors
- Validate credentials

**Key Methods:**
```java
void setAccessToken(String token)
boolean validateAccessToken()
FigmaFile getFile(String fileKey) throws IOException
String exportNode(String fileKey, String nodeId, String format)
```

**Dependencies:**
- OkHttp - HTTP client
- Gson - JSON parsing

#### CodeGeneratorService

**Responsibilities:**
- Traverse Figma node tree
- Generate HTML elements
- Extract and convert styles to CSS
- Create JavaScript scaffolding
- Suggest file placement

**Key Methods:**
```java
GeneratedCode generateCode(FigmaFile file, String nodeName)
String suggestFilePath(Project project, String fileName)
private void processNode(Node node, StringBuilder html, StringBuilder css, int depth)
private String getHtmlElementType(String figmaType)
private String sanitizeClassName(String name)
```

**Algorithms:**

1. **Node Processing** (Recursive):
```
processNode(node):
    1. Determine HTML element type
    2. Generate opening tag with class
    3. Extract and generate CSS for node:
       - Dimensions (width, height)
       - Colors (fills)
       - Typography (for text nodes)
       - Positioning
    4. Process children recursively
    5. Generate closing tag
```

2. **File Placement**:
```
suggestFilePath(project, fileName):
    1. Get project base directory
    2. Check for preferred directories in order:
       - templates/
       - static/templates/
       - frontend/
       - public/
       - src/
       - web/
    3. Return first match or default to root
```

### 3. Model Layer (Data)

#### FigmaFile Model

**Structure:**
```
FigmaFile
├── name: String
├── lastModified: String
├── thumbnailUrl: String
├── version: String
└── document: Document
    ├── id: String
    ├── name: String
    ├── type: String
    └── children: List<Node>

Node
├── id: String
├── name: String
├── type: String (FRAME, TEXT, RECTANGLE, etc.)
├── children: List<Node>
├── absoluteBoundingBox: AbsoluteBoundingBox
├── fills: List<Fill>
├── strokes: List<Stroke>
├── characters: String (for TEXT nodes)
└── style: TypeStyle (for TEXT nodes)

AbsoluteBoundingBox
├── x: double
├── y: double
├── width: double
└── height: double

Fill/Stroke
├── type: String
└── color: Color
    ├── r: double (0-1)
    ├── g: double (0-1)
    ├── b: double (0-1)
    └── a: double (0-1)

TypeStyle
├── fontFamily: String
├── fontSize: double
└── fontWeight: String
```

#### GeneratedCode Model

**Structure:**
```
GeneratedCode
├── html: String
├── css: String
├── javascript: String
└── suggestedPath: String
```

## Design Patterns Used

### 1. Factory Pattern
- `FigmaToolWindowFactory` creates tool window instances
- Decouples creation from usage

### 2. Service Pattern
- `FigmaApiService` and `CodeGeneratorService` as application services
- Singleton instances via IntelliJ Platform DI

### 3. Model-View Pattern
- Clear separation between data (models) and presentation (UI)
- Services act as controllers

### 4. Builder Pattern (Implicit)
- StringBuilder used for incremental code generation
- Efficient string concatenation

### 5. Visitor Pattern (Conceptual)
- Recursive node processing traverses tree structure
- Different handling based on node type

## Threading Model

### UI Thread (EDT - Event Dispatch Thread)
- All Swing UI updates
- Button clicks, text input
- Window rendering

### Background Threads
- API calls to Figma
- File I/O operations
- Code generation processing

### Thread Safety
```java
// API call on background thread
ApplicationManager.getApplication().executeOnPooledThread(() -> {
    try {
        FigmaFile file = figmaApiService.getFile(fileKey);
        
        // UI update on EDT
        SwingUtilities.invokeLater(() -> {
            updateStatus("Success!");
        });
    } catch (Exception e) {
        SwingUtilities.invokeLater(() -> {
            showError(e.getMessage());
        });
    }
});
```

## Extension Points

### Adding New Node Types

```java
// In CodeGeneratorService.getHtmlElementType()
switch (figmaType) {
    case "NEW_TYPE":
        return "new-element";
    // ...
}
```

### Custom Code Templates

```java
// In CodeGeneratorService.generateCode()
if (useReactTemplate) {
    generateReactComponent(node);
} else if (useVueTemplate) {
    generateVueComponent(node);
} else {
    generateVanillaHtml(node);
}
```

### Additional API Endpoints

```java
// In FigmaApiService
public Comments getComments(String fileKey) throws IOException {
    String url = FIGMA_API_BASE_URL + "/files/" + fileKey + "/comments";
    // ... implementation
}
```

## Security Considerations

### Token Storage
- Tokens stored in memory during session
- Not persisted to disk (user must re-enter)
- Secure password field in UI

### Input Validation
- File keys validated before API calls
- Node names sanitized for CSS classes
- Paths validated before file writes

### API Communication
- HTTPS only (enforced by OkHttp)
- Token sent in header, not URL
- Error messages don't expose tokens

## Performance Optimizations

### Lazy Loading
- Plugin classes loaded on demand
- Tool window created when opened, not at startup

### Efficient String Building
- StringBuilder for code generation
- Avoids repeated string concatenation

### Caching Opportunities
- API responses could be cached
- Token validation result cached temporarily
- Generated code could be memoized

## Error Handling Strategy

### Levels of Error Handling

1. **API Errors**
   - Network timeouts
   - Invalid tokens
   - File not found
   - Rate limiting

2. **Generation Errors**
   - Invalid node structure
   - Missing required fields
   - Type mismatches

3. **File System Errors**
   - Permission denied
   - Disk full
   - Path not found

### Error Recovery

```java
try {
    // Attempt operation
    result = riskyOperation();
} catch (IOException e) {
    // Log error
    LOG.error("Operation failed", e);
    
    // User feedback
    Messages.showErrorDialog(
        project,
        "Detailed error message",
        "Error Title"
    );
    
    // Graceful degradation
    return fallbackResult();
}
```

## Testing Strategy

### Unit Tests
- Service layer methods
- Model serialization
- Utility functions

### Integration Tests
- API communication
- File I/O
- UI interactions

### Manual Testing
- Plugin installation
- Tool window functionality
- Code generation quality
- Different project types

## Deployment Architecture

```
Developer Machine
    │
    ├─> Build (./gradlew buildPlugin)
    │       │
    │       └─> figma-to-backend-1.0.0.zip
    │
    └─> Distribute
            │
            ├─> JetBrains Marketplace
            │       └─> Users install via IDE
            │
            └─> GitHub Releases
                    └─> Users install from disk
```

## Configuration

### Build Configuration
- `build.gradle.kts` - Gradle build script
- `settings.gradle.kts` - Project settings
- `gradle.properties` - Build properties

### Plugin Configuration
- `plugin.xml` - Plugin metadata
  - ID, name, description
  - Dependencies
  - Extensions
  - Actions

### Runtime Configuration
- No persistent configuration yet
- Future: Settings page for:
  - Default token storage
  - Code generation preferences
  - Template selection

## Monitoring and Logging

### Logging
```java
private static final Logger LOG = Logger.getInstance(FigmaApiService.class);

LOG.info("Fetching file: " + fileKey);
LOG.warn("Slow API response: " + duration + "ms");
LOG.error("Failed to parse response", exception);
```

### Metrics
- API call duration
- Code generation time
- File sizes
- Error rates

## Future Architecture Enhancements

### 1. Plugin Settings
```
Settings Page
├── API Configuration
│   ├── Token storage (encrypted)
│   └── API timeout settings
├── Code Generation
│   ├── Framework selection
│   ├── CSS framework
│   └── Custom templates
└── File Placement
    ├── Custom directory rules
    └── Naming conventions
```

### 2. Caching Layer
```
Cache Service
├── API Response Cache
│   └── TTL-based invalidation
├── Generated Code Cache
│   └── File hash-based
└── Project Structure Cache
    └── Directory scan results
```

### 3. Template System
```
Template Manager
├── Built-in Templates
│   ├── React
│   ├── Vue
│   └── Angular
└── Custom Templates
    ├── User-defined
    └── Team-shared
```

## References

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- [Figma API Documentation](https://www.figma.com/developers/api)
- [Gradle IntelliJ Plugin](https://github.com/JetBrains/gradle-intellij-plugin)

---

**Version**: 1.0.0  
**Last Updated**: November 2024
