package com.robwestz.figmatobackend.introspection;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.BackendDataSource;
import com.robwestz.figmatobackend.models.SyncContext;

import java.util.List;

/**
 * Phase 3 - Task 2: Backend Introspection
 * 
 * Uses PyCharm's PSI (Program Structure Interface) to analyze project code
 * and discover available data sources that can be bound to Figma elements.
 * 
 * Supports:
 * - Django: models.py fields, views.py context variables
 * - Flask: route functions, returned data
 * - FastAPI: route functions, Pydantic models
 * 
 * This is the foundation that enables data binding by discovering what
 * data is available in the backend code.
 */
public interface BackendIntrospectionService {
    
    /**
     * Analyzes the project and discovers all available data sources.
     * 
     * @param project The PyCharm project to analyze
     * @return List of discovered data sources
     */
    List<BackendDataSource> discoverDataSources(Project project);
    
    /**
     * Detects the framework type used in the project.
     * 
     * @param project The PyCharm project
     * @return The detected framework type
     */
    SyncContext.FrameworkType detectFramework(Project project);
    
    /**
     * Discovers data sources specific to a file or module.
     * Useful for incremental updates.
     * 
     * @param project The PyCharm project
     * @param filePath Path to the specific file to analyze
     * @return List of data sources from that file
     */
    List<BackendDataSource> discoverInFile(Project project, String filePath);
    
    /**
     * Validates if a backend path exists and is accessible.
     * 
     * @param project The PyCharm project
     * @param backendPath The path to validate (e.g., "user.name")
     * @return true if the path is valid
     */
    boolean validateBackendPath(Project project, String backendPath);
}
