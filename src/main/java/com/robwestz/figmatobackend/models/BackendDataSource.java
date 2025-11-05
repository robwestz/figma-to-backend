package com.robwestz.figmatobackend.models;

import java.util.List;
import java.util.Map;

/**
 * Represents available data from the backend that can be bound to Figma elements.
 * This is populated by the BackendIntrospectionService.
 */
public class BackendDataSource {
    private String name;              // e.g., "user.name", "products", "submit_form"
    private DataSourceType type;
    private String dataType;          // e.g., "str", "List[Product]", "function"
    private String filePath;          // Source file where this was found
    private int lineNumber;           // Line number in source file
    private Map<String, String> attributes;  // For complex types, nested attributes

    public enum DataSourceType {
        VARIABLE,       // Simple variable or field
        LIST,           // Iterable collection
        ACTION,         // Function/method that can be called
        MODEL_FIELD     // Django model field
    }

    public BackendDataSource() {
    }

    public BackendDataSource(String name, DataSourceType type, String dataType) {
        this.name = name;
        this.type = type;
        this.dataType = dataType;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DataSourceType getType() { return type; }
    public void setType(DataSourceType type) { this.type = type; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }

    @Override
    public String toString() {
        return "BackendDataSource{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}
