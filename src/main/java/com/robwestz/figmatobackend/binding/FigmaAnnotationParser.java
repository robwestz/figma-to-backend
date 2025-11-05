package com.robwestz.figmatobackend.binding;

import com.robwestz.figmatobackend.models.DataBinding;
import com.robwestz.figmatobackend.models.FigmaFile;

import java.util.List;

/**
 * Phase 3 - Task 1: Design-to-Data Annotation
 * 
 * Parses Figma node names to identify data binding points based on naming conventions.
 * 
 * Supported patterns:
 * - text:{{ variable }} - Text variable binding
 * - button:[action:name] - Action/event binding
 * - list:[for:item in items] - Loop/iteration binding
 * - [if:condition] - Conditional rendering
 * 
 * Example:
 *   Node name: "text:{{ user.name }}"
 *   Result: DataBinding(type=TEXT_VARIABLE, expression="{{ user.name }}")
 */
public interface FigmaAnnotationParser {
    
    /**
     * Parses all nodes in a Figma file to extract binding annotations.
     * 
     * @param figmaFile The Figma file to parse
     * @return List of identified data bindings
     */
    List<DataBinding> parseBindings(FigmaFile figmaFile);
    
    /**
     * Parses a specific node name to check if it contains binding annotations.
     * 
     * @param nodeName The name of the Figma node
     * @param nodeId The ID of the node
     * @return DataBinding if annotation found, null otherwise
     */
    DataBinding parseNodeName(String nodeName, String nodeId);
    
    /**
     * Validates if a node name follows the binding convention.
     * 
     * @param nodeName The name to validate
     * @return true if it contains valid binding syntax
     */
    boolean isBindingAnnotation(String nodeName);
    
    /**
     * Extracts the display name (without annotation) from a node name.
     * Example: "text:{{ user.name }}" -> "user.name" or "User Name"
     * 
     * @param nodeName The annotated node name
     * @return The display name
     */
    String extractDisplayName(String nodeName);
}
