package com.robwestz.figmatobackend.binding;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.robwestz.figmatobackend.introspection.BackendIntrospectionService;
import com.robwestz.figmatobackend.models.BackendDataSource;
import com.robwestz.figmatobackend.models.DataBinding;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DataBindingManager that manages mappings between
 * Figma bindings and backend data sources.
 * 
 * Provides persistence, validation, and fuzzy matching capabilities.
 */
public class DataBindingManagerImpl implements DataBindingManager {
    
    private Map<String, DataBinding> bindingsCache = new HashMap<>();
    private static final String BINDINGS_FILE_NAME = ".figma-bindings.json";
    
    @Override
    public DataBinding createBinding(DataBinding binding) {
        if (binding == null || binding.getFigmaNodeId() == null) {
            return null;
        }
        
        // Store in cache
        bindingsCache.put(binding.getFigmaNodeId(), binding);
        
        return binding;
    }
    
    @Override
    public DataBinding updateBinding(DataBinding binding) {
        if (binding == null || binding.getFigmaNodeId() == null) {
            return null;
        }
        
        // Update in cache
        bindingsCache.put(binding.getFigmaNodeId(), binding);
        
        return binding;
    }
    
    @Override
    public void removeBinding(String figmaNodeId) {
        if (figmaNodeId != null) {
            bindingsCache.remove(figmaNodeId);
        }
    }
    
    @Override
    public List<DataBinding> getAllBindings(Project project) {
        return new ArrayList<>(bindingsCache.values());
    }
    
    @Override
    public DataBinding getBinding(String figmaNodeId) {
        if (figmaNodeId == null) {
            return null;
        }
        
        return bindingsCache.get(figmaNodeId);
    }
    
    @Override
    public List<BackendDataSource> suggestMatches(DataBinding binding, 
                                                   List<BackendDataSource> availableSources) {
        if (binding == null || availableSources == null || availableSources.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ScoredMatch> scoredMatches = new ArrayList<>();
        
        // Extract the variable name from Figma expression
        String figmaVar = extractVariableName(binding.getFigmaExpression());
        if (figmaVar == null || figmaVar.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Score each available source
        for (BackendDataSource source : availableSources) {
            double score = calculateMatchScore(figmaVar, source, binding.getBindingType());
            if (score > 0) {
                scoredMatches.add(new ScoredMatch(source, score));
            }
        }
        
        // Sort by score (descending)
        scoredMatches.sort((a, b) -> Double.compare(b.score, a.score));
        
        // Return top matches
        return scoredMatches.stream()
                .map(m -> m.source)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean validateBinding(DataBinding binding, Project project) {
        if (binding == null || project == null) {
            return false;
        }
        
        // Check if Figma side is valid
        if (binding.getFigmaExpression() == null || binding.getFigmaExpression().isEmpty()) {
            return false;
        }
        
        // Check if backend path is set
        if (!binding.isBound()) {
            return false; // Not bound yet
        }
        
        // Validate backend path exists
        try {
            BackendIntrospectionService introspectionService = 
                project.getService(BackendIntrospectionService.class);
            
            if (introspectionService != null) {
                return introspectionService.validateBackendPath(project, binding.getBackendPath());
            }
        } catch (Exception e) {
            // Service not available or error - return false
        }
        
        return false;
    }
    
    @Override
    public void saveBindings(Project project, List<DataBinding> bindings) {
        if (project == null || bindings == null) {
            return;
        }
        
        try {
            String projectPath = project.getBasePath();
            if (projectPath == null) {
                return;
            }
            
            File bindingsFile = new File(projectPath, BINDINGS_FILE_NAME);
            
            // Simple serialization - in production would use JSON library
            try (PrintWriter writer = new PrintWriter(new FileWriter(bindingsFile))) {
                writer.println("# Figma to Backend Bindings");
                writer.println("# Format: nodeId|expression|backendPath|type");
                
                for (DataBinding binding : bindings) {
                    writer.println(String.format("%s|%s|%s|%s",
                        binding.getFigmaNodeId(),
                        binding.getFigmaExpression(),
                        binding.getBackendPath() != null ? binding.getBackendPath() : "",
                        binding.getBindingType()));
                }
            }
            
            // Update cache
            bindingsCache.clear();
            for (DataBinding binding : bindings) {
                bindingsCache.put(binding.getFigmaNodeId(), binding);
            }
            
        } catch (IOException e) {
            // Handle error silently - log in production
        }
    }
    
    @Override
    public List<DataBinding> loadBindings(Project project) {
        List<DataBinding> bindings = new ArrayList<>();
        
        if (project == null) {
            return bindings;
        }
        
        try {
            String projectPath = project.getBasePath();
            if (projectPath == null) {
                return bindings;
            }
            
            File bindingsFile = new File(projectPath, BINDINGS_FILE_NAME);
            if (!bindingsFile.exists()) {
                return bindings;
            }
            
            // Simple deserialization
            try (BufferedReader reader = new BufferedReader(new FileReader(bindingsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.trim().isEmpty()) {
                        continue; // Skip comments
                    }
                    
                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        DataBinding binding = new DataBinding();
                        binding.setFigmaNodeId(parts[0]);
                        binding.setFigmaExpression(parts[1]);
                        binding.setBackendPath(parts[2].isEmpty() ? null : parts[2]);
                        binding.setBindingType(DataBinding.BindingType.valueOf(parts[3]));
                        
                        bindings.add(binding);
                        bindingsCache.put(binding.getFigmaNodeId(), binding);
                    }
                }
            }
            
        } catch (IOException | IllegalArgumentException e) {
            // Handle error silently - log in production
        }
        
        return bindings;
    }
    
    /**
     * Extracts variable name from Figma expression
     * Example: "{{ user.name }}" -> "user.name"
     */
    private String extractVariableName(String expression) {
        if (expression == null) {
            return "";
        }
        
        // Remove {{ }} for text variables
        String cleaned = expression.replaceAll("\\{\\{\\s*", "").replaceAll("\\s*\\}\\}", "");
        
        // Remove [action:...] wrapper
        cleaned = cleaned.replaceAll("\\[action:", "").replaceAll("\\]", "");
        
        // Remove [for:...] wrapper and extract the list variable
        if (expression.contains("[for:")) {
            // Extract "items" from "[for:item in items]"
            String[] parts = cleaned.split("\\s+in\\s+");
            if (parts.length > 1) {
                cleaned = parts[1].trim();
            }
        }
        
        // Remove [if:...] wrapper
        cleaned = cleaned.replaceAll("\\[if:", "").replaceAll("\\]", "");
        
        return cleaned.trim();
    }
    
    /**
     * Calculates match score between Figma variable and backend source
     */
    private double calculateMatchScore(String figmaVar, BackendDataSource source, 
                                       DataBinding.BindingType bindingType) {
        double score = 0.0;
        
        String sourceName = source.getName();
        if (sourceName == null) {
            return 0.0;
        }
        
        // Exact match is best
        if (sourceName.equals(figmaVar)) {
            score += 100.0;
        }
        
        // Check for partial matches
        else if (sourceName.contains(figmaVar) || figmaVar.contains(sourceName)) {
            score += 50.0;
        }
        
        // Check for similar names (case-insensitive)
        else if (sourceName.toLowerCase().contains(figmaVar.toLowerCase()) ||
                 figmaVar.toLowerCase().contains(sourceName.toLowerCase())) {
            score += 30.0;
        }
        
        // Check last part match (e.g., "user.name" matches "current_user.name")
        String[] figmaParts = figmaVar.split("\\.");
        String[] sourceParts = sourceName.split("\\.");
        if (figmaParts.length > 0 && sourceParts.length > 0) {
            String figmaLast = figmaParts[figmaParts.length - 1];
            String sourceLast = sourceParts[sourceParts.length - 1];
            if (figmaLast.equalsIgnoreCase(sourceLast)) {
                score += 40.0;
            }
        }
        
        // Type compatibility bonus
        score += calculateTypeCompatibility(bindingType, source.getType());
        
        return score;
    }
    
    /**
     * Calculates type compatibility score
     */
    private double calculateTypeCompatibility(DataBinding.BindingType bindingType, 
                                              BackendDataSource.DataSourceType sourceType) {
        if (bindingType == null || sourceType == null) {
            return 0.0;
        }
        
        switch (bindingType) {
            case TEXT_VARIABLE:
                // Text variables work well with any scalar type
                if (sourceType == BackendDataSource.DataSourceType.VARIABLE ||
                    sourceType == BackendDataSource.DataSourceType.MODEL_FIELD) {
                    return 20.0;
                }
                break;
                
            case ACTION:
                // Actions need functions/methods
                if (sourceType == BackendDataSource.DataSourceType.ACTION) {
                    return 20.0;
                }
                break;
                
            case LOOP:
                // Loops need lists/collections
                if (sourceType == BackendDataSource.DataSourceType.LIST) {
                    return 20.0;
                }
                break;
                
            case CONDITIONAL:
                // Conditionals can use any boolean or variable
                if (sourceType == BackendDataSource.DataSourceType.VARIABLE) {
                    return 10.0;
                }
                break;
        }
        
        return 0.0;
    }
    
    /**
     * Helper class for scored matches
     */
    private static class ScoredMatch {
        BackendDataSource source;
        double score;
        
        ScoredMatch(BackendDataSource source, double score) {
            this.source = source;
            this.score = score;
        }
    }
}
