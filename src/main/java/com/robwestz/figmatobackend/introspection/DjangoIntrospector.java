package com.robwestz.figmatobackend.introspection;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.robwestz.figmatobackend.models.BackendDataSource;

import java.util.List;

/**
 * Django-specific introspection implementation.
 * 
 * Analyzes:
 * - models.py: Django Model fields and relationships
 * - views.py: Context variables passed to render()
 * - forms.py: Form fields
 * 
 * Example discoveries:
 * - From models.py: User.username (CharField), Post.author (ForeignKey)
 * - From views.py: context['current_user'], context['posts']
 */
public interface DjangoIntrospector {
    
    /**
     * Discovers Django model fields.
     * 
     * @param project The PyCharm project
     * @return List of model fields as data sources
     */
    List<BackendDataSource> discoverModelFields(Project project);
    
    /**
     * Discovers context variables from view functions.
     * Parses render() calls to find what data is passed to templates.
     * 
     * @param project The PyCharm project
     * @return List of context variables
     */
    List<BackendDataSource> discoverViewContext(Project project);
    
    /**
     * Discovers form fields.
     * 
     * @param project The PyCharm project
     * @return List of form fields
     */
    List<BackendDataSource> discoverFormFields(Project project);
    
    /**
     * Finds all models.py files in the project.
     * 
     * @param project The PyCharm project
     * @return List of models.py files
     */
    List<PsiFile> findModelFiles(Project project);
    
    /**
     * Finds all views.py files in the project.
     * 
     * @param project The PyCharm project
     * @return List of views.py files
     */
    List<PsiFile> findViewFiles(Project project);
}
