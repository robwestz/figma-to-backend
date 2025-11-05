# Quick Setup Guide for IntelliJ IDEA Ultimate

This guide will help you set up the `figma-to-backend` project in IntelliJ IDEA Ultimate and start building.

## Prerequisites

Before you begin, ensure you have the following installed:

- **IntelliJ IDEA Ultimate** (latest version recommended)
  - Download from: https://www.jetbrains.com/idea/download/
- **Git** (for version control)
- **Node.js** (v18.x or later) and **npm** (if this becomes a Node.js/TypeScript project)
  - Download from: https://nodejs.org/
- **Python** (v3.9 or later) and **pip** (if this becomes a Python project)
  - Download from: https://www.python.org/downloads/

## Quick Start (5 minutes)

### 1. Clone the Repository

```bash
git clone https://github.com/robwestz/figma-to-backend.git
cd figma-to-backend
```

### 2. Open in IntelliJ IDEA Ultimate

**Option A: From Welcome Screen**
1. Open IntelliJ IDEA Ultimate
2. Click **Open** or **Open Project**
3. Navigate to the cloned `figma-to-backend` directory
4. Click **OK**

**Option B: From Command Line**
```bash
# Navigate to the project directory and open it
cd figma-to-backend
idea .
```
*Note: The `idea` command requires the IntelliJ IDEA command-line launcher. To enable it: **Tools → Create Command-line Launcher** in IntelliJ IDEA.*

### 3. Initial Project Setup

When IntelliJ opens the project for the first time:

1. **Trust the project** when prompted
2. Wait for IntelliJ to **index the project** (status bar at bottom)
3. If prompted to **import project settings**, choose the appropriate option based on your project type

## Configuration Steps

### For Node.js/TypeScript Projects

1. **Configure Node.js interpreter:**
   - Go to **File → Settings → Languages & Frameworks → Node.js**
   - Set the Node interpreter path (usually auto-detected)
   - Enable **Coding assistance for Node.js**

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Configure Run/Debug:**
   - Go to **Run → Edit Configurations**
   - Click **+** → **Node.js** (or **npm**)
   - Name: `Run Dev Server`
   - Configure the script or entry point (e.g., `npm run dev` or `src/index.ts`)
   - Click **OK**

### For Python Projects

1. **Configure Python interpreter:**
   - Go to **File → Settings → Project → Python Interpreter**
   - Click the gear icon → **Add Interpreter**
   - Choose **Virtualenv** or **System Interpreter**
   - Click **OK**

2. **Create virtual environment (recommended):**
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

3. **Install dependencies:**
   ```bash
   pip install -r requirements.txt  # if requirements.txt exists
   ```

4. **Configure Run/Debug:**
   - Go to **Run → Edit Configurations**
   - Click **+** → **Python**
   - Name: `Run Main`
   - Script path: point to your main Python file
   - Click **OK**

## Essential IntelliJ IDEA Ultimate Features

### Enable Useful Plugins

Go to **File → Settings → Plugins** and enable:
- **GitToolBox** - Enhanced Git integration
- **Rainbow Brackets** - Better code readability
- **Key Promoter X** - Learn shortcuts faster
- **String Manipulation** - Advanced string operations

### Recommended Settings

1. **Auto-save:**
   - **File → Settings → Appearance & Behavior → System Settings**
   - Enable **Save files automatically**

2. **Code style:**
   - **File → Settings → Editor → Code Style**
   - Import project-specific code style if `.editorconfig` exists

3. **Version Control:**
   - **File → Settings → Version Control → Git**
   - Configure Git executable path if not auto-detected

## Building and Running

### Build the Project

**For Node.js/TypeScript:**
```bash
npm run build
```

**For Python:**
```bash
# No build step typically required, but you might have:
python setup.py build
```

### Run the Project

**Option 1: Using Run Configuration**
- Click the **Run** button (▶️) in the toolbar
- Or press **Shift + F10**

**Option 2: Using Terminal**
- Open terminal in IntelliJ: **View → Tool Windows → Terminal**
- Run your start command:
  ```bash
  npm start        # For Node.js
  python main.py   # For Python
  ```

### Debug the Project

1. Set breakpoints by clicking in the gutter next to line numbers
2. Click the **Debug** button (🐞) in the toolbar
3. Or press **Shift + F9**

## Common Tasks

### Run Tests

**For Node.js/TypeScript:**
```bash
npm test
# Or use Jest/Mocha run configuration in IDE
```

**For Python:**
```bash
pytest
# Or use pytest run configuration in IDE
```

### Lint Code

**For Node.js/TypeScript:**
```bash
npm run lint
```

**For Python:**
```bash
flake8 .
# or
pylint src/
```

### Format Code

Use the keyboard shortcuts in the table below to format your code.

## Useful Keyboard Shortcuts

| Action | Windows/Linux | Mac |
|--------|---------------|-----|
| Search everywhere | **Double Shift** | **Double Shift** |
| Find file | **Ctrl + Shift + N** | **Cmd + Shift + N** |
| Find class | **Ctrl + N** | **Cmd + N** |
| Find action | **Ctrl + Shift + A** | **Cmd + Shift + A** |
| Run | **Shift + F10** | **Ctrl + R** |
| Debug | **Shift + F9** | **Ctrl + D** |
| Refactor this | **Ctrl + Alt + Shift + T** | **Ctrl + T** |
| Go to definition | **Ctrl + B** | **Cmd + B** |
| Show usages | **Ctrl + Alt + F7** | **Cmd + Option + F7** |
| Reformat code | **Ctrl + Alt + L** | **Cmd + Option + L** |
| Optimize imports | **Ctrl + Alt + O** | **Cmd + Option + O** |

## Troubleshooting

### Issue: "Cannot resolve symbol" errors

**Solution:**
1. **File → Invalidate Caches → Invalidate and Restart**
2. Ensure dependencies are installed (`npm install` or `pip install`)
3. Check that the interpreter/SDK is configured correctly

### Issue: Git integration not working

**Solution:**
1. **File → Settings → Version Control → Git**
2. Verify Git executable path
3. Click **Test** to verify Git is working

### Issue: Slow indexing

**Solution:**
1. Exclude build directories: **File → Settings → Project Structure → Modules**
2. Mark `node_modules`, `dist`, `build`, `.venv` as **Excluded**
3. Increase IDE memory: **Help → Edit Custom VM Options**
   ```
   -Xmx4096m
   ```

### Issue: Terminal not opening

**Solution:**
1. **File → Settings → Tools → Terminal**
2. Verify shell path is correct
3. Try alternative shell (bash, zsh, powershell)

## Project Structure Tips

Create a well-organized structure:

```
figma-to-backend/
├── src/              # Source code
├── tests/            # Test files
├── docs/             # Documentation
├── examples/         # Example usage
├── .idea/            # IntelliJ IDEA settings (gitignored)
├── .gitignore        # Git ignore rules
├── package.json      # Node.js dependencies (if applicable)
├── requirements.txt  # Python dependencies (if applicable)
├── README.md         # Project overview
└── SETUP.md          # This file
```

## Next Steps

1. **Read the project README.md** for project-specific details
2. **Set up your development environment** following this guide
3. **Create your first feature branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Start building!** Begin implementing the Figma to backend conversion logic
5. **Write tests** as you develop features
6. **Commit frequently** with meaningful commit messages

## Additional Resources

- [IntelliJ IDEA Documentation](https://www.jetbrains.com/idea/documentation/)
- [IntelliJ IDEA Keyboard Shortcuts](https://www.jetbrains.com/help/idea/mastering-keyboard-shortcuts.html)
- [IntelliJ IDEA Tips & Tricks](https://www.jetbrains.com/idea/guide/)

## Getting Help

If you encounter issues:
1. Check this SETUP.md guide
2. Review IntelliJ IDEA logs: **Help → Show Log in Explorer/Finder**
3. Search IntelliJ IDEA documentation
4. Open an issue on the GitHub repository

---

**Happy coding! 🚀**
