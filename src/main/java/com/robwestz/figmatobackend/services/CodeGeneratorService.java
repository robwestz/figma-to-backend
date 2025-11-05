package com.robwestz.figmatobackend.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.robwestz.figmatobackend.models.FigmaFile;
import com.robwestz.figmatobackend.models.GeneratedCode;

import java.util.List;

@Service
public final class CodeGeneratorService {

    public CodeGeneratorService() {
    }

    /**
     * Generates frontend code from a Figma file
     * @param figmaFile The Figma file to generate code from
     * @param nodeName Optional specific node name to generate code for
     * @return GeneratedCode object containing HTML, CSS, and JavaScript
     */
    public GeneratedCode generateCode(FigmaFile figmaFile, String nodeName) {
        GeneratedCode code = new GeneratedCode();
        
        StringBuilder html = new StringBuilder();
        StringBuilder css = new StringBuilder();
        StringBuilder js = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>").append(escapeHtml(figmaFile.getName())).append("</title>\n");
        html.append("    <link rel=\"stylesheet\" href=\"styles.css\">\n");
        html.append("</head>\n");
        html.append("<body>\n");

        // Generate CSS reset
        css.append("/* Reset and base styles */\n");
        css.append("* {\n");
        css.append("    margin: 0;\n");
        css.append("    padding: 0;\n");
        css.append("    box-sizing: border-box;\n");
        css.append("}\n\n");

        // Process the document
        if (figmaFile.getDocument() != null && figmaFile.getDocument().getChildren() != null) {
            for (FigmaFile.Node node : figmaFile.getDocument().getChildren()) {
                if (nodeName == null || nodeName.isEmpty() || node.getName().equals(nodeName)) {
                    processNode(node, html, css, 1);
                }
            }
        }

        html.append("    <script src=\"script.js\"></script>\n");
        html.append("</body>\n");
        html.append("</html>\n");

        js.append("// Generated JavaScript\n");
        js.append("document.addEventListener('DOMContentLoaded', function() {\n");
        js.append("    console.log('Figma design loaded');\n");
        js.append("    // Add your interactive functionality here\n");
        js.append("});\n");

        code.setHtml(html.toString());
        code.setCss(css.toString());
        code.setJavascript(js.toString());

        return code;
    }

    private void processNode(FigmaFile.Node node, StringBuilder html, StringBuilder css, int depth) {
        if (node == null) return;

        String indent = "    ".repeat(depth);
        String className = sanitizeClassName(node.getName());
        String elementType = getHtmlElementType(node.getType());

        // Generate HTML
        html.append(indent).append("<").append(elementType)
            .append(" class=\"").append(className).append("\"");

        // Add text content if it's a text node
        if ("TEXT".equals(node.getType()) && node.getCharacters() != null) {
            html.append(">").append(escapeHtml(node.getCharacters()));
        } else {
            html.append(">\n");
        }

        // Generate CSS for this node
        css.append(".").append(className).append(" {\n");

        // Add positioning and sizing
        if (node.getAbsoluteBoundingBox() != null) {
            FigmaFile.AbsoluteBoundingBox box = node.getAbsoluteBoundingBox();
            css.append("    width: ").append(box.getWidth()).append("px;\n");
            css.append("    height: ").append(box.getHeight()).append("px;\n");
            css.append("    position: relative;\n");
        }

        // Add background color if available
        if (node.getFills() != null && !node.getFills().isEmpty()) {
            FigmaFile.Fill fill = node.getFills().get(0);
            if (fill.getColor() != null) {
                css.append("    background-color: ").append(fill.getColor().toRgba()).append(";\n");
            }
        }

        // Add text styling
        if ("TEXT".equals(node.getType()) && node.getStyle() != null) {
            FigmaFile.TypeStyle style = node.getStyle();
            if (style.getFontFamily() != null) {
                css.append("    font-family: ").append(style.getFontFamily()).append(";\n");
            }
            if (style.getFontSize() > 0) {
                css.append("    font-size: ").append(style.getFontSize()).append("px;\n");
            }
            if (style.getFontWeight() != null) {
                css.append("    font-weight: ").append(style.getFontWeight()).append(";\n");
            }
        }

        css.append("}\n\n");

        // Process children recursively
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (FigmaFile.Node child : node.getChildren()) {
                processNode(child, html, css, depth + 1);
            }
        }

        // Close HTML tag
        if (!"TEXT".equals(node.getType()) || node.getCharacters() == null) {
            html.append(indent);
        }
        html.append("</").append(elementType).append(">\n");
    }

    private String getHtmlElementType(String figmaType) {
        if (figmaType == null) return "div";
        
        switch (figmaType) {
            case "TEXT":
                return "span";
            case "FRAME":
            case "GROUP":
                return "div";
            case "RECTANGLE":
            case "ELLIPSE":
                return "div";
            case "VECTOR":
                return "svg";
            default:
                return "div";
        }
    }

    private String sanitizeClassName(String name) {
        if (name == null) return "unnamed";
        // Convert to lowercase, replace spaces and special chars with hyphens
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Determines the best location in the project to place generated code
     * @param project The current project
     * @param fileName The name of the file being generated
     * @return Suggested path for the generated files
     */
    public String suggestFilePath(Project project, String fileName) {
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir == null) {
            return fileName;
        }

        // Look for common frontend directories
        String[] preferredDirs = {
            "static/templates", "templates", "static", 
            "frontend", "public", "src", "web"
        };

        for (String dir : preferredDirs) {
            VirtualFile targetDir = baseDir.findFileByRelativePath(dir);
            if (targetDir != null && targetDir.isDirectory()) {
                return dir + "/" + fileName;
            }
        }

        // Default to project root if no suitable directory found
        return fileName;
    }
}
