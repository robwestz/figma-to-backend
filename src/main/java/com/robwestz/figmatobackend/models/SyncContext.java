package com.robwestz.figmatobackend.models;

import java.util.List;
import java.util.Map;

/**
 * Context object that holds all information needed for a sync operation.
 * Contains Figma data, backend data, bindings, and generated code state.
 */
public class SyncContext {
    private FigmaFile figmaFile;
    private List<DataBinding> bindings;
    private List<BackendDataSource> availableDataSources;
    private Map<String, String> existingFileChecksums;  // filepath -> checksum
    private boolean preserveManualCode;
    private FrameworkType frameworkType;

    public enum FrameworkType {
        DJANGO,
        FLASK,
        FASTAPI,
        GENERIC
    }

    public SyncContext() {
        this.preserveManualCode = true;
    }

    // Getters and setters
    public FigmaFile getFigmaFile() { return figmaFile; }
    public void setFigmaFile(FigmaFile figmaFile) { this.figmaFile = figmaFile; }

    public List<DataBinding> getBindings() { return bindings; }
    public void setBindings(List<DataBinding> bindings) { this.bindings = bindings; }

    public List<BackendDataSource> getAvailableDataSources() { return availableDataSources; }
    public void setAvailableDataSources(List<BackendDataSource> sources) { 
        this.availableDataSources = sources; 
    }

    public Map<String, String> getExistingFileChecksums() { return existingFileChecksums; }
    public void setExistingFileChecksums(Map<String, String> checksums) { 
        this.existingFileChecksums = checksums; 
    }

    public boolean isPreserveManualCode() { return preserveManualCode; }
    public void setPreserveManualCode(boolean preserve) { this.preserveManualCode = preserve; }

    public FrameworkType getFrameworkType() { return frameworkType; }
    public void setFrameworkType(FrameworkType type) { this.frameworkType = type; }
}
