# Usage Examples

This document provides practical examples of using the Figma to Backend plugin.

## Example 1: Simple Button Component

### Figma Setup
1. Create a frame named "Button" in Figma
2. Add text "Click Me" inside the frame
3. Set frame background color to blue (#007bff)
4. Set text color to white
5. Note the file key from URL: `figma.com/file/ABC123/MyDesign`

### Plugin Usage
1. Open PyCharm
2. Open Figma tool window (View → Tool Windows → Figma)
3. Enter your Figma access token
4. Enter file key: `ABC123`
5. Enter node name: `Button`
6. Click "Generate Code from Figma"

### Generated Output

**button.html:**
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MyDesign</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="button">
        <span class="click-me">Click Me</span>
    </div>
    <script src="script.js"></script>
</body>
</html>
```

**button.css:**
```css
/* Reset and base styles */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.button {
    width: 120px;
    height: 40px;
    position: relative;
    background-color: rgba(0, 123, 255, 1.00);
}

.click-me {
    font-family: Arial;
    font-size: 16px;
    font-weight: 400;
}
```

**button.js:**
```javascript
// Generated JavaScript
document.addEventListener('DOMContentLoaded', function() {
    console.log('Figma design loaded');
    // Add your interactive functionality here
});
```

## Example 2: Landing Page Header

### Figma Setup
1. Create a frame named "Header"
2. Add logo, navigation items, and CTA button
3. Style appropriately
4. File key: `XYZ789`

### Plugin Usage
1. Open Figma tool window
2. Enter access token (if not already saved)
3. Enter file key: `XYZ789`
4. Enter node name: `Header` (or leave blank for entire file)
5. Generate code

### Project Integration

The plugin will:
1. Detect your project structure (Django, Flask, etc.)
2. Place files in appropriate directory:
   - Django: `templates/` or `static/templates/`
   - Flask: `templates/`
   - Generic: `frontend/` or project root
3. Open the generated HTML file in the editor

## Example 3: Card Component

### Figma Structure
```
Card (Frame)
├── Image (Rectangle)
├── Title (Text)
├── Description (Text)
└── Button (Frame)
    └── Label (Text)
```

### Generated Structure
```html
<div class="card">
    <div class="image"></div>
    <span class="title">Card Title</span>
    <span class="description">Card description text</span>
    <div class="button">
        <span class="label">Learn More</span>
    </div>
</div>
```

## Example 4: Complete Form

### Figma Design
- Form container with multiple input fields
- Labels for each field
- Submit button
- Error message placeholders

### Usage Workflow
1. Design form in Figma with proper naming:
   - "FormContainer" for main frame
   - "EmailInput" for email field
   - "PasswordInput" for password field
   - "SubmitButton" for button
2. Generate code via plugin
3. Enhance JavaScript for form validation:

```javascript
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.form-container');
    const submitButton = document.querySelector('.submit-button');
    
    submitButton.addEventListener('click', function(e) {
        e.preventDefault();
        // Add your form validation logic
        const email = form.querySelector('.email-input').value;
        const password = form.querySelector('.password-input').value;
        
        if (validateForm(email, password)) {
            submitForm(email, password);
        }
    });
});

function validateForm(email, password) {
    // Your validation logic
    return email && password;
}

function submitForm(email, password) {
    // Your submission logic
    console.log('Form submitted');
}
```

## Example 5: Responsive Navigation

### Figma Approach
1. Create two frames:
   - "DesktopNav" - horizontal navigation
   - "MobileNav" - hamburger menu
2. Use consistent naming for matching elements

### Plugin Usage
Generate code for both variants:
1. First: Node name = "DesktopNav"
2. Second: Node name = "MobileNav"

### Post-Processing
Add media queries to generated CSS:

```css
/* Desktop Navigation */
.desktop-nav {
    display: flex;
    /* ... generated styles ... */
}

.mobile-nav {
    display: none;
}

/* Mobile */
@media (max-width: 768px) {
    .desktop-nav {
        display: none;
    }
    
    .mobile-nav {
        display: block;
    }
}
```

## Example 6: Django Integration

### Project Structure
```
myproject/
├── manage.py
├── myapp/
│   ├── templates/
│   └── static/
└── figma-generated/  # Generated files will be placed here
```

### Django Template Integration

After generation, integrate into Django template:

**base.html:**
```django
{% load static %}
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="{% static 'header.css' %}">
</head>
<body>
    {% include 'components/header.html' %}
    {% block content %}{% endblock %}
    <script src="{% static 'header.js' %}"></script>
</body>
</html>
```

## Example 7: Flask Integration

### Flask Project
```
flask_app/
├── app.py
├── templates/
│   └── figma-generated files here
└── static/
    └── figma-generated CSS/JS here
```

### Route Integration

**app.py:**
```python
from flask import Flask, render_template

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('landing-page.html')

@app.route('/components/button')
def button():
    return render_template('button.html')
```

## Example 8: Component Library

### Workflow
1. Create a Figma file with multiple components:
   - Buttons
   - Cards
   - Forms
   - Navigation
2. Generate each component separately
3. Organize in a components directory

### Organization
```
components/
├── buttons/
│   ├── primary-button.html
│   ├── primary-button.css
│   └── primary-button.js
├── cards/
│   ├── product-card.html
│   ├── product-card.css
│   └── product-card.js
└── navigation/
    ├── header.html
    ├── header.css
    └── header.js
```

## Best Practices

### Naming in Figma
- Use descriptive, semantic names: "Header", "PrimaryButton", "ProductCard"
- Avoid special characters in names
- Use PascalCase or kebab-case for consistency
- Name layers meaningfully for better CSS class generation

### Code Organization
- Generate components separately for modularity
- Create a dedicated directory for Figma-generated code
- Version control generated code
- Customize generated code as needed

### Iteration Workflow
1. Design in Figma
2. Generate initial code
3. Test in browser/app
4. Refine design in Figma
5. Regenerate code
6. Merge changes carefully (use version control)

### Customization
- Generated code is a starting point
- Add interactivity in JavaScript
- Enhance responsive behavior in CSS
- Integrate with your backend logic
- Add accessibility features (ARIA labels, etc.)

## Troubleshooting Examples

### Issue: Text Doesn't Match Design
**Problem**: Font looks different than in Figma
**Solution**: Ensure web fonts are loaded:
```html
<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
```

### Issue: Layout is Broken
**Problem**: Elements don't align correctly
**Solution**: Review the generated CSS, Figma may use absolute positioning. Convert to flexbox/grid:
```css
.container {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
```

### Issue: Colors Don't Match
**Problem**: Colors appear different
**Solution**: Check color space and opacity. Figma uses RGB 0-1, CSS uses 0-255:
```css
/* Generated */
background-color: rgba(51, 102, 153, 1.00);

/* Verify in Figma: r:0.2, g:0.4, b:0.6, a:1.0 */
/* Calculation: r*255=51, g*255=102, b*255=153 */
```

## Advanced Usage

### Custom Code Templates
Modify `CodeGeneratorService.java` to use your preferred frameworks:

```java
// Add Tailwind classes
html.append("<div class=\"flex items-center justify-center\">")
    .append(node.getName())
    .append("</div>");

// Generate React components
html.append("import React from 'react';\n");
html.append("export const ").append(componentName).append(" = () => {\n");
// ... component code
```

### API Integration
Combine with your backend API:

```javascript
document.addEventListener('DOMContentLoaded', async function() {
    const data = await fetch('/api/products').then(r => r.json());
    renderProducts(data);
});

function renderProducts(products) {
    const container = document.querySelector('.product-container');
    products.forEach(product => {
        // Generate product cards from template
    });
}
```

## Next Steps

After generating code:
1. Review and test the generated files
2. Add business logic and interactivity
3. Enhance accessibility
4. Optimize for performance
5. Add responsive breakpoints
6. Integrate with your backend
7. Test across browsers
8. Deploy your application

For more information, see the [README](README.md) and [DEVELOPMENT](DEVELOPMENT.md) guides.
