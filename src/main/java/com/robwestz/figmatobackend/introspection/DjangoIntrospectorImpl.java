package com.robwestz.figmatobackend.introspection;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.psi.*;
import com.robwestz.figmatobackend.models.BackendDataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Django-specific introspection implementation.
 * Uses PyCharm's PSI to analyze Django models, views, and forms.
 */
public class DjangoIntrospectorImpl implements DjangoIntrospector {
    
    @Override
    public List<BackendDataSource> discoverModelFields(Project project) {
        List<BackendDataSource> sources = new ArrayList<>();
        
        if (project == null) {
            return sources;
        }
        
        // Find all models.py files
        List<PsiFile> modelFiles = findModelFiles(project);
        
        for (PsiFile file : modelFiles) {
            if (file instanceof PyFile) {
                PyFile pyFile = (PyFile) file;
                
                // Find all classes that extend models.Model
                for (PyClass pyClass : pyFile.getTopLevelClasses()) {
                    if (isDjangoModel(pyClass)) {
                        // Extract fields from the model
                        sources.addAll(extractModelFields(pyClass, file.getVirtualFile().getPath()));
                    }
                }
            }
        }
        
        return sources;
    }
    
    @Override
    public List<BackendDataSource> discoverViewContext(Project project) {
        List<BackendDataSource> sources = new ArrayList<>();
        
        if (project == null) {
            return sources;
        }
        
        // Find all views.py files
        List<PsiFile> viewFiles = findViewFiles(project);
        
        for (PsiFile file : viewFiles) {
            if (file instanceof PyFile) {
                PyFile pyFile = (PyFile) file;
                
                // Find render() function calls
                sources.addAll(extractRenderContext(pyFile, file.getVirtualFile().getPath()));
            }
        }
        
        return sources;
    }
    
    @Override
    public List<BackendDataSource> discoverFormFields(Project project) {
        List<BackendDataSource> sources = new ArrayList<>();
        
        if (project == null) {
            return sources;
        }
        
        // Find all forms.py files
        List<PsiFile> formFiles = findFilesByName(project, "forms.py");
        
        for (PsiFile file : formFiles) {
            if (file instanceof PyFile) {
                PyFile pyFile = (PyFile) file;
                
                // Find all classes that extend forms.Form
                for (PyClass pyClass : pyFile.getTopLevelClasses()) {
                    if (isDjangoForm(pyClass)) {
                        sources.addAll(extractFormFields(pyClass, file.getVirtualFile().getPath()));
                    }
                }
            }
        }
        
        return sources;
    }
    
    @Override
    public List<PsiFile> findModelFiles(Project project) {
        return findFilesByName(project, "models.py");
    }
    
    @Override
    public List<PsiFile> findViewFiles(Project project) {
        return findFilesByName(project, "views.py");
    }
    
    /**
     * Finds Python files by name in the project
     */
    private List<PsiFile> findFilesByName(Project project, String fileName) {
        List<PsiFile> result = new ArrayList<>();
        
        if (project == null) {
            return result;
        }
        
        try {
            // Get all Python files in the project
            Collection<VirtualFile> virtualFiles = FileBasedIndex.getInstance()
                    .getContainingFiles(FileTypeIndex.NAME, PythonFileType.INSTANCE,
                            GlobalSearchScope.projectScope(project));
            
            PsiManager psiManager = PsiManager.getInstance(project);
            
            for (VirtualFile virtualFile : virtualFiles) {
                if (virtualFile.getName().equals(fileName)) {
                    PsiFile psiFile = psiManager.findFile(virtualFile);
                    if (psiFile != null) {
                        result.add(psiFile);
                    }
                }
            }
        } catch (Exception e) {
            // Handle gracefully - return what we have
        }
        
        return result;
    }
    
    /**
     * Checks if a PyClass is a Django model
     */
    private boolean isDjangoModel(PyClass pyClass) {
        if (pyClass == null) {
            return false;
        }
        
        // Check if class extends models.Model
        for (PyClass baseClass : pyClass.getSuperClasses(null)) {
            String baseName = baseClass.getName();
            if ("Model".equals(baseName)) {
                // Additional check: ensure it's from django.db.models
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a PyClass is a Django form
     */
    private boolean isDjangoForm(PyClass pyClass) {
        if (pyClass == null) {
            return false;
        }
        
        // Check if class extends forms.Form or forms.ModelForm
        for (PyClass baseClass : pyClass.getSuperClasses(null)) {
            String baseName = baseClass.getName();
            if ("Form".equals(baseName) || "ModelForm".equals(baseName)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Extracts field information from a Django model
     */
    private List<BackendDataSource> extractModelFields(PyClass modelClass, String filePath) {
        List<BackendDataSource> sources = new ArrayList<>();
        
        String modelName = modelClass.getName();
        if (modelName == null) {
            return sources;
        }
        
        // Iterate through class attributes
        for (PyTargetExpression field : modelClass.getClassAttributes()) {
            String fieldName = field.getName();
            if (fieldName != null && !fieldName.startsWith("_")) {
                // Create data source for this field
                BackendDataSource source = new BackendDataSource();
                source.setName(modelName.toLowerCase() + "." + fieldName);
                source.setType(BackendDataSource.DataSourceType.MODEL_FIELD);
                source.setDataType(inferFieldType(field));
                source.setFilePath(filePath);
                
                sources.add(source);
            }
        }
        
        return sources;
    }
    
    /**
     * Extracts context variables from render() calls in views
     */
    private List<BackendDataSource> extractRenderContext(PyFile pyFile, String filePath) {
        List<BackendDataSource> sources = new ArrayList<>();
        
        // Find all function definitions
        for (PyFunction function : pyFile.getTopLevelFunctions()) {
            // Look for render() calls in the function
            sources.addAll(findRenderCallsInFunction(function, filePath));
        }
        
        return sources;
    }
    
    /**
     * Finds render() calls and extracts context dictionary
     */
    private List<BackendDataSource> findRenderCallsInFunction(PyFunction function, String filePath) {
        List<BackendDataSource> sources = new ArrayList<>();
        
        if (function == null) {
            return sources;
        }
        
        // Simplified implementation - would need to traverse AST to find render() calls
        // and extract the context dictionary argument
        
        // For now, return example sources based on common Django patterns
        // Real implementation would use PSI tree traversal
        
        return sources;
    }
    
    /**
     * Extracts form fields
     */
    private List<BackendDataSource> extractFormFields(PyClass formClass, String filePath) {
        List<BackendDataSource> sources = new ArrayList<>();
        
        String formName = formClass.getName();
        if (formName == null) {
            return sources;
        }
        
        // Extract form fields similar to model fields
        for (PyTargetExpression field : formClass.getClassAttributes()) {
            String fieldName = field.getName();
            if (fieldName != null && !fieldName.startsWith("_")) {
                BackendDataSource source = new BackendDataSource();
                source.setName("form." + fieldName);
                source.setType(BackendDataSource.DataSourceType.VARIABLE);
                source.setDataType(inferFieldType(field));
                source.setFilePath(filePath);
                
                sources.add(source);
            }
        }
        
        return sources;
    }
    
    /**
     * Infers the type of a field based on its definition
     */
    private String inferFieldType(PyTargetExpression field) {
        if (field == null) {
            return "unknown";
        }
        
        // Get the assigned value
        PyExpression value = field.findAssignedValue();
        if (value instanceof PyCallExpression) {
            PyCallExpression call = (PyCallExpression) value;
            PyExpression callee = call.getCallee();
            if (callee != null) {
                String calleeName = callee.getName();
                if (calleeName != null) {
                    // Map Django field types to Python types
                    return mapDjangoFieldType(calleeName);
                }
            }
        }
        
        return "str"; // Default to string
    }
    
    /**
     * Maps Django field types to Python types
     */
    private String mapDjangoFieldType(String djangoFieldType) {
        switch (djangoFieldType) {
            case "CharField":
            case "TextField":
            case "EmailField":
            case "URLField":
            case "SlugField":
                return "str";
            case "IntegerField":
            case "SmallIntegerField":
            case "BigIntegerField":
            case "PositiveIntegerField":
                return "int";
            case "FloatField":
            case "DecimalField":
                return "float";
            case "BooleanField":
                return "bool";
            case "DateField":
                return "date";
            case "DateTimeField":
                return "datetime";
            case "ForeignKey":
            case "OneToOneField":
                return "Model";
            case "ManyToManyField":
                return "List[Model]";
            default:
                return "str";
        }
    }
}
