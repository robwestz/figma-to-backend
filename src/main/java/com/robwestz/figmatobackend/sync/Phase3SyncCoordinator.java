package com.robwestz.figmatobackend.sync;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.FigmaFile;
import com.robwestz.figmatobackend.models.GeneratedCode;
import com.robwestz.figmatobackend.models.SyncContext;

/**
 * Phase 3 Coordinator - Orchestrates the entire sync pipeline.
 * 
 * This is the main entry point for Phase 3 functionality.
 * It coordinates all 4 tasks:
 * 1. Parse Figma annotations
 * 2. Introspect backend code
 * 3. Manage data bindings
 * 4. Perform non-destructive sync
 * 
 * Workflow:
 * 1. User updates Figma design
 * 2. User clicks "Sync" in plugin
 * 3. Coordinator:
 *    - Fetches Figma file
 *    - Parses binding annotations
 *    - Introspects backend for available data
 *    - Loads existing bindings
 *    - Generates code with bindings applied
 *    - Merges with existing files (preserving manual code)
 *    - Updates files on disk
 */
public interface Phase3SyncCoordinator {
    
    /**
     * Performs a full sync operation.
     * This is the main method that orchestrates everything.
     * 
     * @param project The PyCharm project
     * @param figmaFileKey The Figma file key to sync
     * @return Result of the sync operation
     */
    SyncResult performSync(Project project, String figmaFileKey);
    
    /**
     * Prepares a sync context without executing.
     * Useful for preview or validation.
     * 
     * @param project The PyCharm project
     * @param figmaFile The Figma file
     * @return Prepared sync context
     */
    SyncContext prepareSyncContext(Project project, FigmaFile figmaFile);
    
    /**
     * Validates that sync can be performed safely.
     * Checks for conflicts, missing bindings, etc.
     * 
     * @param context The sync context
     * @return Validation result with any warnings or errors
     */
    ValidationResult validateSync(SyncContext context);
    
    /**
     * Result of a sync operation.
     */
    class SyncResult {
        private boolean success;
        private GeneratedCode generatedCode;
        private int filesUpdated;
        private int filesCreated;
        private int bindingsApplied;
        private String errorMessage;
        
        public SyncResult(boolean success) {
            this.success = success;
        }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public GeneratedCode getGeneratedCode() { return generatedCode; }
        public void setGeneratedCode(GeneratedCode code) { this.generatedCode = code; }
        
        public int getFilesUpdated() { return filesUpdated; }
        public void setFilesUpdated(int count) { this.filesUpdated = count; }
        
        public int getFilesCreated() { return filesCreated; }
        public void setFilesCreated(int count) { this.filesCreated = count; }
        
        public int getBindingsApplied() { return bindingsApplied; }
        public void setBindingsApplied(int count) { this.bindingsApplied = count; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String message) { this.errorMessage = message; }
    }
    
    /**
     * Result of sync validation.
     */
    class ValidationResult {
        private boolean valid;
        private String[] warnings;
        private String[] errors;
        
        public ValidationResult(boolean valid) {
            this.valid = valid;
        }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public String[] getWarnings() { return warnings; }
        public void setWarnings(String[] warnings) { this.warnings = warnings; }
        
        public String[] getErrors() { return errors; }
        public void setErrors(String[] errors) { this.errors = errors; }
    }
}
