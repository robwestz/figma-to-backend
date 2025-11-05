package com.robwestz.figmatobackend.sync;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.GeneratedCode;
import com.robwestz.figmatobackend.models.SyncContext;

/**
 * Phase 3 - Task 4: Non-Destructive Code Synchronization
 * 
 * Performs intelligent code updates that preserve manual changes while
 * synchronizing with Figma design updates.
 * 
 * Uses:
 * - Code markers: <!-- FIGMA_GEN:START:checksum -->...<!-- FIGMA_GEN:END -->
 * - AST-based modification (not string replacement)
 * - Checksum comparison to detect changes
 * - PSI tree manipulation for safe code surgery
 * 
 * This is the "magic" that solves the "eject button" problem - developers
 * can modify generated code and still receive design updates.
 */
public interface NonDestructiveSyncService {
    
    /**
     * Synchronizes Figma design changes with existing code files.
     * Preserves manual modifications outside of generated code markers.
     * 
     * @param project The PyCharm project
     * @param context The sync context with all necessary information
     * @return Updated generated code
     */
    GeneratedCode syncWithExisting(Project project, SyncContext context);
    
    /**
     * Checks if a file has been manually modified since last generation.
     * 
     * @param filePath Path to the file
     * @param expectedChecksum The checksum from last generation
     * @return true if file has been manually modified
     */
    boolean hasManualChanges(String filePath, String expectedChecksum);
    
    /**
     * Extracts manual code (code outside FIGMA_GEN markers).
     * 
     * @param filePath Path to the file
     * @return Map of regions to manual code
     */
    String extractManualCode(String filePath);
    
    /**
     * Merges generated code with manual code using markers.
     * 
     * @param generatedCode New code from Figma
     * @param manualCode Existing manual modifications
     * @return Merged code preserving both changes
     */
    String mergeCode(String generatedCode, String manualCode);
    
    /**
     * Applies data bindings to generated code.
     * Replaces static text/values with template variables.
     * 
     * @param code The generated code
     * @param context The sync context with bindings
     * @return Code with bindings applied
     */
    String applyBindings(String code, SyncContext context);
    
    /**
     * Calculates checksum for code block.
     * Used to detect if generated code has changed.
     * 
     * @param code The code to checksum
     * @return Checksum string
     */
    String calculateChecksum(String code);
    
    /**
     * Wraps generated code with markers for future updates.
     * 
     * @param code The code to wrap
     * @param checksum The checksum of the code
     * @param blockId Unique identifier for this code block
     * @return Code wrapped with markers
     */
    String wrapWithMarkers(String code, String checksum, String blockId);
}
