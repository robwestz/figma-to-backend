package com.robwestz.figmatobackend.introspection;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.BackendDataSource;

import java.util.List;

/**
 * FastAPI-specific introspection implementation.
 * 
 * Analyzes:
 * - Route decorators: @app.get, @app.post
 * - Pydantic models: BaseModel subclasses
 * - Response models: response_model parameter
 * 
 * Example discoveries:
 * - From @app.get('/api/users', response_model=List[User])
 * - Pydantic User model fields: username, email, created_at
 */
public interface FastAPIIntrospector {
    
    /**
     * Discovers FastAPI routes and their response models.
     * 
     * @param project The PyCharm project
     * @return List of API endpoints and response data
     */
    List<BackendDataSource> discoverApiRoutes(Project project);
    
    /**
     * Discovers Pydantic model fields.
     * 
     * @param project The PyCharm project
     * @return List of Pydantic model fields
     */
    List<BackendDataSource> discoverPydanticModels(Project project);
    
    /**
     * Discovers dependency injection parameters.
     * FastAPI uses dependencies that provide data to routes.
     * 
     * @param project The PyCharm project
     * @return List of available dependencies
     */
    List<BackendDataSource> discoverDependencies(Project project);
}
