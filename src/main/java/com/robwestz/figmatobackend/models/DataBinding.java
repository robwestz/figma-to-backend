package com.robwestz.figmatobackend.models;

/**
 * Represents a binding between a Figma design element and backend data.
 * This is the core model for Phase 3 data binding functionality.
 */
public class DataBinding {
    private String figmaNodeId;
    private String figmaNodeName;
    private BindingType bindingType;
    private String figmaExpression;  // e.g., "{{ user.name }}" or "[action:submit_form]"
    private String backendPath;      // e.g., "context['current_user'].username" or "submit_form"
    private String resolvedType;     // e.g., "str", "List[Product]", "function"

    public enum BindingType {
        TEXT_VARIABLE,    // text:{{ variable }}
        ACTION,           // button:[action:name]
        LOOP,             // list:[for:item in items]
        CONDITIONAL       // [if:condition]
    }

    public DataBinding() {
    }

    public DataBinding(String figmaNodeId, String figmaNodeName, BindingType bindingType, 
                       String figmaExpression, String backendPath) {
        this.figmaNodeId = figmaNodeId;
        this.figmaNodeName = figmaNodeName;
        this.bindingType = bindingType;
        this.figmaExpression = figmaExpression;
        this.backendPath = backendPath;
    }

    // Getters and setters
    public String getFigmaNodeId() { return figmaNodeId; }
    public void setFigmaNodeId(String figmaNodeId) { this.figmaNodeId = figmaNodeId; }

    public String getFigmaNodeName() { return figmaNodeName; }
    public void setFigmaNodeName(String figmaNodeName) { this.figmaNodeName = figmaNodeName; }

    public BindingType getBindingType() { return bindingType; }
    public void setBindingType(BindingType bindingType) { this.bindingType = bindingType; }

    public String getFigmaExpression() { return figmaExpression; }
    public void setFigmaExpression(String figmaExpression) { this.figmaExpression = figmaExpression; }

    public String getBackendPath() { return backendPath; }
    public void setBackendPath(String backendPath) { this.backendPath = backendPath; }

    public String getResolvedType() { return resolvedType; }
    public void setResolvedType(String resolvedType) { this.resolvedType = resolvedType; }

    public boolean isBound() {
        return backendPath != null && !backendPath.isEmpty();
    }

    @Override
    public String toString() {
        return "DataBinding{" +
                "figmaExpression='" + figmaExpression + '\'' +
                ", backendPath='" + backendPath + '\'' +
                ", type=" + bindingType +
                '}';
    }
}
