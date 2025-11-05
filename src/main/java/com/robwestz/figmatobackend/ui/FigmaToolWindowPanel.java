package com.robwestz.figmatobackend.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.robwestz.figmatobackend.models.FigmaFile;
import com.robwestz.figmatobackend.models.GeneratedCode;
import com.robwestz.figmatobackend.services.CodeGeneratorService;
import com.robwestz.figmatobackend.services.FigmaApiService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FigmaToolWindowPanel {
    private final Project project;
    private final JPanel mainPanel;
    private final JTextField accessTokenField;
    private final JTextField fileKeyField;
    private final JTextField nodeNameField;
    private final JButton validateButton;
    private final JButton generateButton;
    private final JTextArea statusArea;
    private final FigmaApiService figmaApiService;
    private final CodeGeneratorService codeGeneratorService;

    public FigmaToolWindowPanel(Project project) {
        this.project = project;
        this.figmaApiService = ApplicationManager.getApplication().getService(FigmaApiService.class);
        this.codeGeneratorService = ApplicationManager.getApplication().getService(CodeGeneratorService.class);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Access Token
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Figma Access Token:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        accessTokenField = new JPasswordField(30);
        formPanel.add(accessTokenField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        validateButton = new JButton("Validate");
        validateButton.addActionListener(e -> validateToken());
        formPanel.add(validateButton, gbc);

        // File Key
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Figma File Key:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        fileKeyField = new JTextField(30);
        fileKeyField.setToolTipText("Enter the file key from Figma URL (e.g., https://www.figma.com/file/FILE_KEY/...)");
        formPanel.add(fileKeyField, gbc);

        // Node Name (optional)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Node Name (optional):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        nodeNameField = new JTextField(30);
        nodeNameField.setToolTipText("Optional: Enter specific node name to generate code for");
        formPanel.add(nodeNameField, gbc);

        // Generate Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        generateButton = new JButton("Generate Code from Figma");
        generateButton.addActionListener(e -> generateCode());
        formPanel.add(generateButton, gbc);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Status Area
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        statusArea = new JTextArea(10, 40);
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(statusArea);
        statusPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(statusPanel, BorderLayout.CENTER);

        // Instructions
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        JTextArea instructions = new JTextArea(
            "1. Get your Figma access token from: Account Settings > Personal access tokens\n" +
            "2. Enter your access token and click 'Validate'\n" +
            "3. Find the file key in your Figma URL: figma.com/file/FILE_KEY/...\n" +
            "4. Enter the file key and optionally a specific node name\n" +
            "5. Click 'Generate Code from Figma' to create HTML, CSS, and JavaScript files"
        );
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setBackground(mainPanel.getBackground());
        instructionsPanel.add(instructions, BorderLayout.CENTER);

        mainPanel.add(instructionsPanel, BorderLayout.SOUTH);

        logStatus("Ready. Please enter your Figma access token to begin.");
    }

    private void validateToken() {
        String token = accessTokenField.getText().trim();
        if (token.isEmpty()) {
            Messages.showErrorDialog(project, "Please enter a Figma access token", "Validation Error");
            return;
        }

        figmaApiService.setAccessToken(token);
        
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            logStatus("Validating token...");
            boolean isValid = figmaApiService.validateAccessToken();
            
            SwingUtilities.invokeLater(() -> {
                if (isValid) {
                    logStatus("✓ Token validated successfully!");
                    Messages.showInfoMessage(project, "Access token is valid!", "Success");
                } else {
                    logStatus("✗ Token validation failed. Please check your token.");
                    Messages.showErrorDialog(project, "Invalid access token. Please check and try again.", "Validation Failed");
                }
            });
        });
    }

    private void generateCode() {
        String token = accessTokenField.getText().trim();
        String fileKey = fileKeyField.getText().trim();
        String nodeName = nodeNameField.getText().trim();

        if (token.isEmpty()) {
            Messages.showErrorDialog(project, "Please enter a Figma access token", "Error");
            return;
        }

        if (fileKey.isEmpty()) {
            Messages.showErrorDialog(project, "Please enter a Figma file key", "Error");
            return;
        }

        figmaApiService.setAccessToken(token);

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                logStatus("Fetching Figma file...");
                FigmaFile figmaFile = figmaApiService.getFile(fileKey);
                
                logStatus("Generating code from design...");
                GeneratedCode code = codeGeneratorService.generateCode(
                    figmaFile, 
                    nodeName.isEmpty() ? null : nodeName
                );

                // Determine file paths
                String baseName = sanitizeFileName(figmaFile.getName());
                String suggestedPath = codeGeneratorService.suggestFilePath(project, baseName);
                
                SwingUtilities.invokeLater(() -> {
                    try {
                        saveGeneratedFiles(code, baseName, suggestedPath);
                        logStatus("✓ Code generation complete! Files saved successfully.");
                        Messages.showInfoMessage(
                            project,
                            "Generated files:\n- " + baseName + ".html\n- " + baseName + ".css\n- " + baseName + ".js",
                            "Success"
                        );
                    } catch (IOException e) {
                        logStatus("✗ Error saving files: " + e.getMessage());
                        Messages.showErrorDialog(project, "Failed to save files: " + e.getMessage(), "Error");
                    }
                });

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    logStatus("✗ Error: " + e.getMessage());
                    Messages.showErrorDialog(project, "Failed to generate code: " + e.getMessage(), "Error");
                });
            }
        });
    }

    private void saveGeneratedFiles(GeneratedCode code, String baseName, String suggestedPath) throws IOException {
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir == null) {
            throw new IOException("Project base directory not found");
        }

        String basePath = baseDir.getPath();
        Path targetDir = Paths.get(basePath);

        // Create directory structure if it doesn't exist
        if (suggestedPath.contains("/")) {
            String dirPath = suggestedPath.substring(0, suggestedPath.lastIndexOf("/"));
            targetDir = Paths.get(basePath, dirPath);
            Files.createDirectories(targetDir);
        }

        // Save HTML
        Path htmlPath = targetDir.resolve(baseName + ".html");
        Files.writeString(htmlPath, code.getHtml());
        logStatus("Created: " + htmlPath.toString());

        // Save CSS
        Path cssPath = targetDir.resolve(baseName + ".css");
        Files.writeString(cssPath, code.getCss());
        logStatus("Created: " + cssPath.toString());

        // Save JavaScript
        Path jsPath = targetDir.resolve(baseName + ".js");
        Files.writeString(jsPath, code.getJavascript());
        logStatus("Created: " + jsPath.toString());

        // Refresh file system and open files
        ApplicationManager.getApplication().invokeLater(() -> {
            LocalFileSystem.getInstance().refresh(false);
            VirtualFile htmlFile = LocalFileSystem.getInstance().findFileByPath(htmlPath.toString());
            if (htmlFile != null) {
                FileEditorManager.getInstance(project).openFile(htmlFile, true);
            }
        });
    }

    private String sanitizeFileName(String name) {
        if (name == null || name.isEmpty()) {
            return "figma-design";
        }
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    private void logStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusArea.append(message + "\n");
            statusArea.setCaretPosition(statusArea.getDocument().getLength());
        });
    }

    public JComponent getContent() {
        return mainPanel;
    }
}
