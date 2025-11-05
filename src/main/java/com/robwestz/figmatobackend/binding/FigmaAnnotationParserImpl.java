package com.robwestz.figmatobackend.binding;

import com.robwestz.figmatobackend.models.DataBinding;
import com.robwestz.figmatobackend.models.FigmaFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of FigmaAnnotationParser that parses Figma node names
 * to extract data binding annotations.
 * 
 * Supported patterns:
 * - text:{{ variable }} - Text variable binding
 * - button:[action:name] - Action/event binding
 * - list:[for:item in items] - Loop/iteration binding
 * - [if:condition] - Conditional rendering
 */
public class FigmaAnnotationParserImpl implements FigmaAnnotationParser {
    
    // Regex patterns for different binding types
    private static final Pattern TEXT_PATTERN = Pattern.compile("text:\\{\\{\\s*([^}]+)\\s*\\}\\}");
    private static final Pattern ACTION_PATTERN = Pattern.compile("\\[action:([^\\]]+)\\]");
    private static final Pattern LOOP_PATTERN = Pattern.compile("list:\\[for:([^\\s]+)\\s+in\\s+([^\\]]+)\\]");
    private static final Pattern CONDITIONAL_PATTERN = Pattern.compile("\\[if:([^\\]]+)\\]");
    
    @Override
    public List<DataBinding> parseBindings(FigmaFile figmaFile) {
        List<DataBinding> bindings = new ArrayList<>();
        
        if (figmaFile == null || figmaFile.getDocument() == null) {
            return bindings;
        }
        
        // Recursively process all nodes in the document
        if (figmaFile.getDocument().getChildren() != null) {
            for (FigmaFile.Node node : figmaFile.getDocument().getChildren()) {
                processNodeRecursively(node, bindings);
            }
        }
        
        return bindings;
    }
    
    /**
     * Recursively processes nodes and their children to find bindings
     */
    private void processNodeRecursively(FigmaFile.Node node, List<DataBinding> bindings) {
        if (node == null) {
            return;
        }
        
        // Parse the current node
        DataBinding binding = parseNodeName(node.getName(), node.getId());
        if (binding != null) {
            bindings.add(binding);
        }
        
        // Process children
        if (node.getChildren() != null) {
            for (FigmaFile.Node child : node.getChildren()) {
                processNodeRecursively(child, bindings);
            }
        }
    }
    
    @Override
    public DataBinding parseNodeName(String nodeName, String nodeId) {
        if (nodeName == null || nodeName.trim().isEmpty()) {
            return null;
        }
        
        // Try to match each pattern type
        DataBinding binding;
        
        // Check for text variable binding
        binding = parseTextBinding(nodeName, nodeId);
        if (binding != null) return binding;
        
        // Check for action binding
        binding = parseActionBinding(nodeName, nodeId);
        if (binding != null) return binding;
        
        // Check for loop binding
        binding = parseLoopBinding(nodeName, nodeId);
        if (binding != null) return binding;
        
        // Check for conditional binding
        binding = parseConditionalBinding(nodeName, nodeId);
        if (binding != null) return binding;
        
        return null;
    }
    
    /**
     * Parses text variable binding: text:{{ variable }}
     */
    private DataBinding parseTextBinding(String nodeName, String nodeId) {
        Matcher matcher = TEXT_PATTERN.matcher(nodeName);
        if (matcher.find()) {
            String variable = matcher.group(1).trim();
            DataBinding binding = new DataBinding();
            binding.setFigmaNodeId(nodeId);
            binding.setFigmaNodeName(nodeName);
            binding.setBindingType(DataBinding.BindingType.TEXT_VARIABLE);
            binding.setFigmaExpression("{{ " + variable + " }}");
            return binding;
        }
        return null;
    }
    
    /**
     * Parses action binding: button:[action:name]
     */
    private DataBinding parseActionBinding(String nodeName, String nodeId) {
        Matcher matcher = ACTION_PATTERN.matcher(nodeName);
        if (matcher.find()) {
            String actionName = matcher.group(1).trim();
            DataBinding binding = new DataBinding();
            binding.setFigmaNodeId(nodeId);
            binding.setFigmaNodeName(nodeName);
            binding.setBindingType(DataBinding.BindingType.ACTION);
            binding.setFigmaExpression("[action:" + actionName + "]");
            return binding;
        }
        return null;
    }
    
    /**
     * Parses loop binding: list:[for:item in items]
     */
    private DataBinding parseLoopBinding(String nodeName, String nodeId) {
        Matcher matcher = LOOP_PATTERN.matcher(nodeName);
        if (matcher.find()) {
            String itemVar = matcher.group(1).trim();
            String listVar = matcher.group(2).trim();
            DataBinding binding = new DataBinding();
            binding.setFigmaNodeId(nodeId);
            binding.setFigmaNodeName(nodeName);
            binding.setBindingType(DataBinding.BindingType.LOOP);
            binding.setFigmaExpression("[for:" + itemVar + " in " + listVar + "]");
            return binding;
        }
        return null;
    }
    
    /**
     * Parses conditional binding: [if:condition]
     */
    private DataBinding parseConditionalBinding(String nodeName, String nodeId) {
        Matcher matcher = CONDITIONAL_PATTERN.matcher(nodeName);
        if (matcher.find()) {
            String condition = matcher.group(1).trim();
            DataBinding binding = new DataBinding();
            binding.setFigmaNodeId(nodeId);
            binding.setFigmaNodeName(nodeName);
            binding.setBindingType(DataBinding.BindingType.CONDITIONAL);
            binding.setFigmaExpression("[if:" + condition + "]");
            return binding;
        }
        return null;
    }
    
    @Override
    public boolean isBindingAnnotation(String nodeName) {
        if (nodeName == null || nodeName.trim().isEmpty()) {
            return false;
        }
        
        return TEXT_PATTERN.matcher(nodeName).find() ||
               ACTION_PATTERN.matcher(nodeName).find() ||
               LOOP_PATTERN.matcher(nodeName).find() ||
               CONDITIONAL_PATTERN.matcher(nodeName).find();
    }
    
    @Override
    public String extractDisplayName(String nodeName) {
        if (nodeName == null || nodeName.trim().isEmpty()) {
            return nodeName;
        }
        
        // Remove all binding annotations to get display name
        String displayName = nodeName;
        
        // Remove text: prefix and {{ }}
        displayName = TEXT_PATTERN.matcher(displayName).replaceAll("$1");
        
        // Remove action annotations
        displayName = ACTION_PATTERN.matcher(displayName).replaceAll("");
        
        // Remove loop annotations
        displayName = LOOP_PATTERN.matcher(displayName).replaceAll("");
        
        // Remove conditional annotations
        displayName = CONDITIONAL_PATTERN.matcher(displayName).replaceAll("");
        
        // Clean up any remaining patterns
        displayName = displayName.replaceAll("^text:", "").trim();
        displayName = displayName.replaceAll("^list:", "").trim();
        displayName = displayName.replaceAll("^button:", "").trim();
        
        // If empty after removing annotations, try to extract a meaningful name
        if (displayName.isEmpty()) {
            // Try to extract variable name from the original
            Matcher textMatcher = TEXT_PATTERN.matcher(nodeName);
            if (textMatcher.find()) {
                String var = textMatcher.group(1).trim();
                // Convert user.name to "User Name"
                displayName = formatVariableName(var);
            }
        }
        
        return displayName.isEmpty() ? nodeName : displayName;
    }
    
    /**
     * Formats a variable name into a display name
     * Example: "user.name" -> "User Name"
     */
    private String formatVariableName(String varName) {
        if (varName == null) return "";
        
        // Replace dots and underscores with spaces
        String formatted = varName.replace(".", " ").replace("_", " ");
        
        // Capitalize first letter of each word
        String[] words = formatted.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
                result.append(" ");
            }
        }
        
        return result.toString().trim();
    }
}
