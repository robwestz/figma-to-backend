# Contributing to Figma to Backend

Thank you for your interest in contributing to Figma to Backend! This document provides guidelines and instructions for contributing.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for all contributors.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates. When creating a bug report, include:

- **Clear title and description**
- **Steps to reproduce**
- **Expected behavior**
- **Actual behavior**
- **Screenshots** (if applicable)
- **Environment details** (OS, PyCharm version, plugin version)

Example:
```
**Title**: Generated CSS missing background color for FRAME nodes

**Description**: When generating code from a Figma frame with a solid fill, 
the background-color property is not included in the CSS.

**Steps to Reproduce**:
1. Create a FRAME in Figma with solid fill color
2. Generate code using the plugin
3. Check the generated CSS file

**Expected**: CSS should contain `background-color: rgba(...)`
**Actual**: No background-color property in CSS

**Environment**: 
- OS: macOS 14.0
- PyCharm: 2023.2.5
- Plugin: 1.0.0
```

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- **Clear title and description**
- **Use case** - why is this enhancement needed?
- **Proposed solution**
- **Alternative solutions** you've considered
- **Additional context** (screenshots, mockups)

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Make your changes**
3. **Add tests** for new functionality
4. **Update documentation** if needed
5. **Ensure tests pass**: `./gradlew test`
6. **Follow code style** guidelines
7. **Write clear commit messages**
8. **Submit a pull request**

## Development Process

### Setting Up Development Environment

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/figma-to-backend.git
cd figma-to-backend

# Add upstream remote
git remote add upstream https://github.com/robwestz/figma-to-backend.git

# Create a feature branch
git checkout -b feature/your-feature-name
```

### Making Changes

1. **Write tests first** (TDD approach recommended)
2. **Implement your feature**
3. **Run tests**: `./gradlew test`
4. **Build the plugin**: `./gradlew buildPlugin`
5. **Test in IDE**: `./gradlew runIde`

### Code Style

#### Java Code Style

- **Indentation**: 4 spaces
- **Line length**: 120 characters maximum
- **Naming**:
  - Classes: PascalCase (`FigmaApiService`)
  - Methods: camelCase (`generateCode`)
  - Constants: UPPER_SNAKE_CASE (`API_BASE_URL`)
  - Variables: camelCase (`figmaFile`)
- **Braces**: Opening brace on same line
- **JavaDoc**: Required for all public methods

Example:
```java
/**
 * Generates frontend code from a Figma design file.
 *
 * @param figmaFile The Figma file to generate code from
 * @param nodeName Optional specific node name to target
 * @return GeneratedCode object containing HTML, CSS, and JavaScript
 */
public GeneratedCode generateCode(FigmaFile figmaFile, String nodeName) {
    // Implementation
}
```

#### Gradle Build Files

- Use Kotlin DSL (`.kts`)
- Follow Kotlin coding conventions
- Group related configurations

### Testing Guidelines

#### Unit Tests

- Place in `src/test/java/`
- Mirror package structure of source code
- Name test classes with `Test` suffix
- Use descriptive test method names

Example:
```java
@Test
public void testGenerateCode_WithTextNode() {
    // Given
    FigmaFile figmaFile = createTestFigmaFile();
    
    // When
    GeneratedCode code = service.generateCode(figmaFile, null);
    
    // Then
    assertNotNull(code);
    assertTrue(code.getHtml().contains("expected-content"));
}
```

#### Integration Tests

- Test end-to-end workflows
- Use real API calls (with test data)
- Clean up test artifacts

### Commit Messages

Follow the Conventional Commits specification:

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

Examples:
```
feat(generator): add support for SVG node export

Implement SVG generation for VECTOR nodes in Figma designs.
Includes proper path rendering and style attributes.

Closes #123
```

```
fix(api): handle network timeout errors gracefully

Add retry logic and better error messages for API timeouts.
Improve user feedback in the tool window.
```

### Documentation

Update documentation when:
- Adding new features
- Changing existing functionality
- Fixing bugs that affect usage

Files to update:
- `README.md` - User documentation
- `DEVELOPMENT.md` - Developer documentation
- `EXAMPLES.md` - Usage examples
- JavaDoc comments in code

## Areas for Contribution

### High Priority

1. **Additional Node Type Support**
   - Implement handlers for more Figma node types
   - Better SVG generation
   - Component instance support

2. **Code Generation Improvements**
   - Framework templates (React, Vue, Angular)
   - CSS frameworks integration (Tailwind, Bootstrap)
   - TypeScript generation option

3. **UI Enhancements**
   - Settings page for configuration
   - Preview of generated code before saving
   - File conflict resolution dialog

### Medium Priority

1. **Testing**
   - Increase test coverage
   - Add integration tests
   - Performance tests for large designs

2. **Documentation**
   - Video tutorials
   - More usage examples
   - FAQ section

3. **Performance**
   - Optimize for large Figma files
   - Caching mechanisms
   - Parallel processing

### Low Priority

1. **Additional Features**
   - Export as specific framework components
   - Custom code templates
   - Batch export multiple designs

## Review Process

1. **Automated checks** must pass (build, tests, linting)
2. **Code review** by at least one maintainer
3. **Documentation** review if applicable
4. **Testing** on different platforms if possible
5. **Approval** and merge by maintainer

### Review Criteria

- Code quality and style compliance
- Test coverage for new code
- Documentation completeness
- No breaking changes (or properly documented)
- Performance considerations

## Getting Help

- **GitHub Issues**: For bugs and feature requests
- **GitHub Discussions**: For questions and general discussion
- **Email**: support@robwestz.com

## Recognition

Contributors will be:
- Listed in README.md contributors section
- Mentioned in release notes
- Credited in commit messages

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Thank You!

Your contributions make this project better for everyone. We appreciate your time and effort!
