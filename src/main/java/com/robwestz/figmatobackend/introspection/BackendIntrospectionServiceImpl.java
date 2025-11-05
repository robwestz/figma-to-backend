package com.robwestz.figmatobackend.introspection;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.robwestz.figmatobackend.models.BackendDataSource;
import com.robwestz.figmatobackend.models.SyncContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Main implementation of BackendIntrospectionService.
 * Coordinates framework-specific introspectors to discover available backend data.
 */
public class BackendIntrospectionServiceImpl implements BackendIntrospectionService {
    
    private DjangoIntrospector djangoIntrospector;
    private FlaskIntrospector flaskIntrospector;
    private FastAPIIntrospector fastAPIIntrospector;
    
    public BackendIntrospectionServiceImpl() {
        this.djangoIntrospector = new DjangoIntrospectorImpl();
        this.flaskIntrospector = new FlaskIntrospectorImpl();
        this.fastAPIIntrospector = new FastAPIIntrospectorImpl();
    }
    
    @Override
    public List<BackendDataSource> discoverDataSources(Project project) {
        if (project == null) {
            return new ArrayList<>();
        }
        
        // Detect framework
        SyncContext.FrameworkType framework = detectFramework(project);
        
        List<BackendDataSource> allSources = new ArrayList<>();
        
        // Use appropriate introspector based on framework
        switch (framework) {
            case DJANGO:
                allSources.addAll(djangoIntrospector.discoverModelFields(project));
                allSources.addAll(djangoIntrospector.discoverViewContext(project));
                allSources.addAll(djangoIntrospector.discoverFormFields(project));
                break;
                
            case FLASK:
                allSources.addAll(flaskIntrospector.discoverRoutes(project));
                allSources.addAll(flaskIntrospector.discoverTemplateVariables(project));
                allSources.addAll(flaskIntrospector.discoverModelFields(project));
                break;
                
            case FASTAPI:
                allSources.addAll(fastAPIIntrospector.discoverApiRoutes(project));
                allSources.addAll(fastAPIIntrospector.discoverPydanticModels(project));
                allSources.addAll(fastAPIIntrospector.discoverDependencies(project));
                break;
                
            case GENERIC:
                // Try all introspectors for generic projects
                allSources.addAll(djangoIntrospector.discoverModelFields(project));
                allSources.addAll(flaskIntrospector.discoverRoutes(project));
                allSources.addAll(fastAPIIntrospector.discoverApiRoutes(project));
                break;
        }
        
        return allSources;
    }
    
    @Override
    public SyncContext.FrameworkType detectFramework(Project project) {
        if (project == null) {
            return SyncContext.FrameworkType.GENERIC;
        }
        
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir == null) {
            return SyncContext.FrameworkType.GENERIC;
        }
        
        // Check for Django indicators
        if (hasFile(baseDir, "manage.py") || hasFile(baseDir, "settings.py")) {
            return SyncContext.FrameworkType.DJANGO;
        }
        
        // Check for Flask indicators
        if (hasFileContaining(baseDir, "from flask import") || 
            hasFileContaining(baseDir, "Flask(__name__)")) {
            return SyncContext.FrameworkType.FLASK;
        }
        
        // Check for FastAPI indicators
        if (hasFileContaining(baseDir, "from fastapi import") || 
            hasFileContaining(baseDir, "FastAPI()")) {
            return SyncContext.FrameworkType.FASTAPI;
        }
        
        return SyncContext.FrameworkType.GENERIC;
    }
    
    @Override
    public List<BackendDataSource> discoverInFile(Project project, String filePath) {
        if (project == null || filePath == null) {
            return new ArrayList<>();
        }
        
        List<BackendDataSource> sources = new ArrayList<>();
        
        // Determine file type and use appropriate introspector
        if (filePath.endsWith("models.py")) {
            sources.addAll(djangoIntrospector.discoverModelFields(project));
        } else if (filePath.endsWith("views.py")) {
            sources.addAll(djangoIntrospector.discoverViewContext(project));
        } else if (filePath.contains("app.py") || filePath.contains("routes")) {
            SyncContext.FrameworkType framework = detectFramework(project);
            if (framework == SyncContext.FrameworkType.FLASK) {
                sources.addAll(flaskIntrospector.discoverRoutes(project));
            } else if (framework == SyncContext.FrameworkType.FASTAPI) {
                sources.addAll(fastAPIIntrospector.discoverApiRoutes(project));
            }
        }
        
        return sources;
    }
    
    @Override
    public boolean validateBackendPath(Project project, String backendPath) {
        if (project == null || backendPath == null || backendPath.isEmpty()) {
            return false;
        }
        
        // Discover all available sources
        List<BackendDataSource> sources = discoverDataSources(project);
        
        // Check if the path exists in discovered sources
        for (BackendDataSource source : sources) {
            if (source.getName().equals(backendPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Helper to check if a file exists in the directory
     */
    private boolean hasFile(VirtualFile dir, String fileName) {
        if (dir == null) return false;
        VirtualFile file = dir.findChild(fileName);
        return file != null && !file.isDirectory();
    }
    
    /**
     * Helper to check if any Python file contains a specific string
     */
    private boolean hasFileContaining(VirtualFile dir, String searchString) {
        if (dir == null) return false;
        
        // Simple check - in real implementation would search file contents
        // For now, check file names and common patterns
        for (VirtualFile child : dir.getChildren()) {
            if (child.isDirectory()) {
                if (hasFileContaining(child, searchString)) {
                    return true;
                }
            } else if (child.getName().endsWith(".py")) {
                // Would need to read file contents - simplified for now
                // In production, use PSI to search for imports
                return false; // Placeholder
            }
        }
        return false;
    }
}
