package com.robwestz.figmatobackend.introspection;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.BackendDataSource;

import java.util.List;

/**
 * Flask-specific introspection implementation.
 * 
 * Analyzes:
 * - Route decorators: @app.route, @app.get, @app.post
 * - Return values: render_template() calls and returned data
 * - Jinja2 template variables
 * 
 * Example discoveries:
 * - From @app.route('/users'): endpoint 'users', returns {'users': User.query.all()}
 * - Template variables passed via render_template('users.html', users=users)
 */
public interface FlaskIntrospector {
    
    /**
     * Discovers Flask routes and their return data.
     * 
     * @param project The PyCharm project
     * @return List of route endpoints and data
     */
    List<BackendDataSource> discoverRoutes(Project project);
    
    /**
     * Discovers template variables from render_template calls.
     * 
     * @param project The PyCharm project
     * @return List of template variables
     */
    List<BackendDataSource> discoverTemplateVariables(Project project);
    
    /**
     * Discovers SQLAlchemy model fields (if used).
     * 
     * @param project The PyCharm project
     * @return List of model fields
     */
    List<BackendDataSource> discoverModelFields(Project project);
}
