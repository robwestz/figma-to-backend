package com.robwestz.figmatobackend.binding;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.BackendDataSource;
import com.robwestz.figmatobackend.models.DataBinding;

import java.util.List;
import java.util.Map;

/**
 * Phase 3 - Task 3: Data Binding Management
 * 
 * Manages the mappings between Figma binding points and backend data sources.
 * Provides persistence and retrieval of binding configurations.
 * 
 * This service acts as the bridge between what designers annotate in Figma
 * and what developers have available in their backend code.
 */
public interface DataBindingManager {
    
    /**
     * Creates a new binding between a Figma element and backend data.
     * 
     * @param binding The binding to create
     * @return The created binding with any additional resolved information
     */
    DataBinding createBinding(DataBinding binding);
    
    /**
     * Updates an existing binding.
     * 
     * @param binding The binding to update
     * @return The updated binding
     */
    DataBinding updateBinding(DataBinding binding);
    
    /**
     * Removes a binding.
     * 
     * @param figmaNodeId The ID of the Figma node
     */
    void removeBinding(String figmaNodeId);
    
    /**
     * Gets all bindings for a project.
     * 
     * @param project The PyCharm project
     * @return List of all configured bindings
     */
    List<DataBinding> getAllBindings(Project project);
    
    /**
     * Gets binding for a specific Figma node.
     * 
     * @param figmaNodeId The Figma node ID
     * @return The binding if exists, null otherwise
     */
    DataBinding getBinding(String figmaNodeId);
    
    /**
     * Suggests possible backend data sources for a Figma binding.
     * Uses fuzzy matching on names and type compatibility.
     * 
     * @param binding The Figma binding that needs a backend source
     * @param availableSources All available backend data sources
     * @return List of suggested matches, ordered by relevance
     */
    List<BackendDataSource> suggestMatches(DataBinding binding, 
                                           List<BackendDataSource> availableSources);
    
    /**
     * Validates that a binding is valid (both sides exist and types are compatible).
     * 
     * @param binding The binding to validate
     * @param project The PyCharm project
     * @return true if binding is valid
     */
    boolean validateBinding(DataBinding binding, Project project);
    
    /**
     * Saves all bindings to persistent storage.
     * 
     * @param project The PyCharm project
     * @param bindings The bindings to save
     */
    void saveBindings(Project project, List<DataBinding> bindings);
    
    /**
     * Loads bindings from persistent storage.
     * 
     * @param project The PyCharm project
     * @return Loaded bindings
     */
    List<DataBinding> loadBindings(Project project);
}
