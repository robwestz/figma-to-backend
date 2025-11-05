package com.robwestz.figmatobackend.sync;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.DataBinding;
import com.robwestz.figmatobackend.models.GeneratedCode;
import com.robwestz.figmatobackend.models.SyncContext;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of NonDestructiveSyncService that performs intelligent
 * code merging while preserving manual modifications.
 * 
 * Uses code markers and checksums to identify generated vs. manual code.
 */
public class NonDestructiveSyncServiceImpl implements NonDestructiveSyncService {
    
    private static final String MARKER_START = "<!-- FIGMA_GEN:START:%s:%s -->";
    private static final String MARKER_END = "<!-- FIGMA_GEN:END:%s -->";
    private static final Pattern MARKER_PATTERN = Pattern.compile(
        "<!--\\s*FIGMA_GEN:START:([^:]+):([^\\s]+)\\s*-->(.*?)<!--\\s*FIGMA_GEN:END:\\2\\s*-->",
        Pattern.DOTALL
    );
    
    @Override
    public GeneratedCode syncWithExisting(Project project, SyncContext context) {
        if (project == null || context == null || context.getFigmaFile() == null) {
            return null;
        }
        
        GeneratedCode generatedCode = new GeneratedCode();
        
        // Generate new code from Figma
        // This would use CodeGeneratorService in real implementation
        String newHtml = generateHtmlFromFigma(context);
        String newCss = generateCssFromFigma(context);
        String newJs = generateJsFromFigma(context);
        
        // Apply data bindings
        if (context.isPreserveManualCode()) {
            newHtml = applyBindings(newHtml, context);
            newJs = applyBindings(newJs, context);
        }
        
        // Merge with existing files if they exist
        String projectPath = project.getBasePath();
        if (projectPath != null && context.isPreserveManualCode()) {
            newHtml = mergeWithExisting(projectPath, "design.html", newHtml);
            newCss = mergeWithExisting(projectPath, "design.css", newCss);
            newJs = mergeWithExisting(projectPath, "design.js", newJs);
        }
        
        generatedCode.setHtml(newHtml);
        generatedCode.setCss(newCss);
        generatedCode.setJavascript(newJs);
        
        return generatedCode;
    }
    
    @Override
    public boolean hasManualChanges(String filePath, String expectedChecksum) {
        if (filePath == null || expectedChecksum == null) {
            return false;
        }
        
        try {
            // Read the file
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return false;
            }
            
            String content = Files.readString(path);
            
            // Extract generated code sections
            String generatedOnly = extractGeneratedCode(content);
            
            // Calculate checksum
            String actualChecksum = calculateChecksum(generatedOnly);
            
            // Compare
            return !expectedChecksum.equals(actualChecksum);
            
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public String extractManualCode(String filePath) {
        if (filePath == null) {
            return "";
        }
        
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return "";
            }
            
            String content = Files.readString(path);
            
            // Remove all FIGMA_GEN blocks
            return MARKER_PATTERN.matcher(content).replaceAll("");
            
        } catch (IOException e) {
            return "";
        }
    }
    
    @Override
    public String mergeCode(String generatedCode, String manualCode) {
        if (generatedCode == null) {
            return manualCode != null ? manualCode : "";
        }
        
        if (manualCode == null || manualCode.trim().isEmpty()) {
            return generatedCode;
        }
        
        // Simple merge: manual code goes before generated code
        // In production, would use more sophisticated merging
        return manualCode + "\n" + generatedCode;
    }
    
    @Override
    public String applyBindings(String code, SyncContext context) {
        if (code == null || context == null || context.getBindings() == null) {
            return code;
        }
        
        String result = code;
        
        // Apply each binding
        for (DataBinding binding : context.getBindings()) {
            if (binding != null && binding.isBound()) {
                result = applyBinding(result, binding, context);
            }
        }
        
        return result;
    }
    
    @Override
    public String calculateChecksum(String code) {
        if (code == null) {
            return "";
        }
        
        try {
            // Normalize code before checksum (remove whitespace variations)
            String normalized = normalizeCode(code);
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(normalized.getBytes("UTF-8"));
            
            // Convert to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public String wrapWithMarkers(String code, String checksum, String blockId) {
        if (code == null || checksum == null || blockId == null) {
            return code != null ? code : "";
        }
        
        String start = String.format(MARKER_START, checksum, blockId);
        String end = String.format(MARKER_END, blockId);
        
        return start + "\n" + code + "\n" + end;
    }
    
    /**
     * Merges new generated code with existing file
     */
    private String mergeWithExisting(String projectPath, String fileName, String newCode) {
        String filePath = projectPath + "/" + fileName;
        
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                // File doesn't exist, return new code wrapped with markers
                String checksum = calculateChecksum(newCode);
                return wrapWithMarkers(newCode, checksum, "main");
            }
            
            String existingContent = Files.readString(path);
            
            // Extract manual code
            String manual = extractManualCode(filePath);
            
            // Wrap new code with markers
            String checksum = calculateChecksum(newCode);
            String wrappedNew = wrapWithMarkers(newCode, checksum, "main");
            
            // Merge
            return mergeCode(wrappedNew, manual);
            
        } catch (IOException e) {
            // Return new code if merge fails
            String checksum = calculateChecksum(newCode);
            return wrapWithMarkers(newCode, checksum, "main");
        }
    }
    
    /**
     * Extracts only the generated code sections
     */
    private String extractGeneratedCode(String content) {
        if (content == null) {
            return "";
        }
        
        StringBuilder generated = new StringBuilder();
        Matcher matcher = MARKER_PATTERN.matcher(content);
        
        while (matcher.find()) {
            generated.append(matcher.group(3));
        }
        
        return generated.toString();
    }
    
    /**
     * Normalizes code for consistent checksums
     */
    private String normalizeCode(String code) {
        if (code == null) {
            return "";
        }
        
        // Remove extra whitespace
        String normalized = code.replaceAll("\\s+", " ");
        
        // Trim
        normalized = normalized.trim();
        
        return normalized;
    }
    
    /**
     * Applies a single binding to code
     */
    private String applyBinding(String code, DataBinding binding, SyncContext context) {
        if (code == null || binding == null) {
            return code;
        }
        
        // Extract variable name from Figma expression
        String figmaVar = extractVariableName(binding.getFigmaExpression());
        String backendPath = binding.getBackendPath();
        
        if (figmaVar == null || backendPath == null) {
            return code;
        }
        
        // Replace based on framework type
        String replacement = formatBackendPath(backendPath, context.getFrameworkType());
        
        // Replace in code
        // For Django: replace text with {{ variable }}
        // For React: replace with {variable}
        // etc.
        
        switch (binding.getBindingType()) {
            case TEXT_VARIABLE:
                // Replace static text with template variable
                code = code.replaceAll(
                    ">" + Pattern.quote(figmaVar) + "<",
                    ">" + replacement + "<"
                );
                break;
                
            case ACTION:
                // Add event handler
                code = addEventHandler(code, figmaVar, backendPath, context);
                break;
                
            case LOOP:
                // Wrap in loop construct
                code = wrapInLoop(code, figmaVar, backendPath, context);
                break;
                
            case CONDITIONAL:
                // Wrap in conditional
                code = wrapInConditional(code, figmaVar, backendPath, context);
                break;
        }
        
        return code;
    }
    
    /**
     * Extracts variable name from Figma expression
     */
    private String extractVariableName(String expression) {
        if (expression == null) {
            return "";
        }
        
        return expression
            .replaceAll("\\{\\{\\s*", "")
            .replaceAll("\\s*\\}\\}", "")
            .replaceAll("\\[action:", "")
            .replaceAll("\\[if:", "")
            .replaceAll("\\]", "")
            .trim();
    }
    
    /**
     * Formats backend path for the target framework
     */
    private String formatBackendPath(String path, SyncContext.FrameworkType framework) {
        if (path == null) {
            return "";
        }
        
        if (framework == null) {
            framework = SyncContext.FrameworkType.GENERIC;
        }
        
        switch (framework) {
            case DJANGO:
                return "{{ " + path + " }}";
            case FLASK:
                return "{{ " + path + " }}";
            case FASTAPI:
                // FastAPI typically uses frontend frameworks
                return "{" + path + "}";
            default:
                return path;
        }
    }
    
    /**
     * Adds event handler for action binding
     */
    private String addEventHandler(String code, String elementName, String actionPath, 
                                   SyncContext context) {
        // Simplified implementation
        // Would need to find the element and add onclick/event handler
        return code;
    }
    
    /**
     * Wraps code in loop construct
     */
    private String wrapInLoop(String code, String itemVar, String listVar, 
                             SyncContext context) {
        if (context.getFrameworkType() == SyncContext.FrameworkType.DJANGO) {
            return "{% for " + itemVar + " in " + listVar + " %}\n" +
                   code + "\n" +
                   "{% endfor %}";
        }
        return code;
    }
    
    /**
     * Wraps code in conditional
     */
    private String wrapInConditional(String code, String condition, String backendPath, 
                                    SyncContext context) {
        if (context.getFrameworkType() == SyncContext.FrameworkType.DJANGO) {
            return "{% if " + backendPath + " %}\n" +
                   code + "\n" +
                   "{% endif %}";
        }
        return code;
    }
    
    /**
     * Placeholder for HTML generation
     */
    private String generateHtmlFromFigma(SyncContext context) {
        // This would use CodeGeneratorService in real implementation
        return "<div class=\"container\"><!-- Generated content --></div>";
    }
    
    /**
     * Placeholder for CSS generation
     */
    private String generateCssFromFigma(SyncContext context) {
        return ".container { width: 100%; }";
    }
    
    /**
     * Placeholder for JS generation
     */
    private String generateJsFromFigma(SyncContext context) {
        return "// Generated JavaScript\n";
    }
}
