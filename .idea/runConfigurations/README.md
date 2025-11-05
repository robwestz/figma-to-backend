# IntelliJ Run Configurations

This directory will contain IntelliJ IDEA run configurations once you set them up.

Run configurations allow you to quickly run, debug, and test your application with a single click.

## How to Create Run Configurations

1. **Run → Edit Configurations**
2. Click **+** to add a new configuration
3. Choose the appropriate type (Node.js, npm, Python, etc.)
4. Configure the settings
5. Click **OK**

## Common Configuration Types

- **npm** - Run npm scripts (start, test, build, etc.)
- **Node.js** - Run Node.js applications
- **Python** - Run Python scripts
- **Shell Script** - Run custom shell scripts
- **Jest/Mocha** - Run JavaScript tests
- **pytest** - Run Python tests

## Sharing Configurations

To share run configurations with your team:
1. Check the **Store as project file** option when creating the configuration
2. Commit the `.idea/runConfigurations/` directory to Git
