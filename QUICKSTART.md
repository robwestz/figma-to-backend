# Quick Start Guide

Get started with Figma to Backend plugin in 5 minutes!

## Prerequisites

- PyCharm 2023.2 or later
- A Figma account with design files
- Figma access token ([Get one here](https://www.figma.com/developers/api#access-tokens))

## Installation

### Option 1: From JetBrains Marketplace (Recommended)

1. Open PyCharm
2. Go to `Settings/Preferences → Plugins`
3. Search for "Figma to Backend"
4. Click `Install`
5. Restart PyCharm

### Option 2: From GitHub Release

1. Download the latest `.zip` from [Releases](https://github.com/robwestz/figma-to-backend/releases)
2. Open PyCharm
3. Go to `Settings/Preferences → Plugins → ⚙️ → Install Plugin from Disk`
4. Select the downloaded `.zip` file
5. Restart PyCharm

### Option 3: Build from Source

```bash
git clone https://github.com/robwestz/figma-to-backend.git
cd figma-to-backend
./gradlew buildPlugin
```

Then install `build/distributions/figma-to-backend-1.0.0.zip` via `Install Plugin from Disk`.

## First-Time Setup

### 1. Get Your Figma Access Token

1. Log in to [Figma](https://www.figma.com)
2. Click your profile picture → `Settings`
3. Scroll to `Personal access tokens`
4. Click `Create new token`
5. Give it a name (e.g., "PyCharm Plugin")
6. Copy the token (you'll need this in the next step)

⚠️ **Important**: Store this token securely. You won't be able to see it again!

### 2. Open the Plugin

In PyCharm:
- Go to `View → Tool Windows → Figma`
- Or press the Figma icon in the right sidebar

### 3. Configure Access Token

1. Paste your Figma access token in the "Figma Access Token" field
2. Click `Validate` to verify it works
3. You should see "✓ Token validated successfully!"

## Your First Code Generation

### Step 1: Prepare Your Figma Design

1. Open your Figma file
2. Note the **file key** from the URL:
   ```
   https://www.figma.com/file/ABC123xyz/MyDesign
                             ↑
                         This is your file key
   ```
3. (Optional) Note the specific **frame/component name** you want to export

### Step 2: Generate Code

1. In the Figma tool window in PyCharm:
   - Enter the **file key** (e.g., `ABC123xyz`)
   - (Optional) Enter a **node name** to export only that component
   - Click `Generate Code from Figma`

2. Wait for the generation to complete (status will show progress)

3. Files will be created and opened automatically!

### Step 3: Review Generated Files

You'll get three files:
- `[design-name].html` - HTML structure
- `[design-name].css` - Styling
- `[design-name].js` - JavaScript scaffolding

The plugin intelligently places them in your project (e.g., `templates/` for Django).

## Example Workflow

### Scenario: Creating a Navigation Bar

**In Figma:**
```
1. Create a frame called "Navigation"
2. Add logo, menu items, and buttons
3. Style it with colors and fonts
4. File URL: figma.com/file/NAV123/Website
```

**In PyCharm:**
```
1. Open Figma tool window
2. File Key: NAV123
3. Node Name: Navigation
4. Click "Generate Code from Figma"
```

**Result:**
```
✓ navigation.html created
✓ navigation.css created  
✓ navigation.js created
→ Files opened in editor
```

## Common Use Cases

### 1. Component Library
Generate individual components separately:
- Button component
- Card component
- Form inputs
- Navigation bars

### 2. Full Page Layouts
Leave "Node Name" empty to generate entire file:
- Landing pages
- Dashboard layouts
- Admin panels

### 3. Responsive Designs
Generate different breakpoints:
- Desktop version: "DesktopLayout"
- Mobile version: "MobileLayout"
- Combine in CSS with media queries

## Tips for Best Results

### Naming in Figma
✅ **Good names:**
- "PrimaryButton"
- "HeroSection"
- "ProductCard"

❌ **Avoid:**
- "Rectangle 1"
- "Group 47"
- Names with special characters: "@#$%"

### Organization
- Group related elements in frames
- Use consistent naming conventions
- Keep hierarchy logical

### Styling
- Use consistent colors (create color styles)
- Define text styles for typography
- Set proper dimensions and spacing

## Troubleshooting

### "Invalid Access Token"
- Double-check your token
- Generate a new token if needed
- Ensure it has proper permissions

### "File Not Found"
- Verify the file key is correct
- Check you have access to the Figma file
- Ensure the file isn't private/restricted

### "No Files Generated"
- Check the status area for error messages
- Verify your Figma design has content
- Try a simpler design first

### "Files in Wrong Location"
- Plugin auto-detects project structure
- Create `templates/` or `static/` folders manually if needed
- Check generated file paths in status messages

## Next Steps

Now that you're set up:

1. **Read the full documentation**: [README.md](README.md)
2. **Explore examples**: [EXAMPLES.md](EXAMPLES.md)
3. **Learn to customize**: [DEVELOPMENT.md](DEVELOPMENT.md)
4. **Integrate with your project**: 
   - Django: Add to templates
   - Flask: Integrate with routes
   - FastAPI: Serve static files

## Keyboard Shortcuts

Currently, the plugin is accessed through:
- Menu: `Tools → Generate Code from Figma`
- Tool Window: `View → Tool Windows → Figma`

## Getting Help

- 📖 **Documentation**: [README.md](README.md)
- 💬 **Issues**: [GitHub Issues](https://github.com/robwestz/figma-to-backend/issues)
- 📧 **Email**: support@robwestz.com

## What's Next?

After generating your first code:

1. **Customize the generated files** to fit your needs
2. **Add interactivity** in the JavaScript file
3. **Enhance responsiveness** in CSS
4. **Integrate with your backend** logic
5. **Share your results** - we'd love to see what you create!

---

**Happy Coding! 🎨→💻**

For more detailed information, see the [complete documentation](README.md).
