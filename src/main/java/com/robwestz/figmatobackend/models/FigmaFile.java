package com.robwestz.figmatobackend.models;

import java.util.List;

public class FigmaFile {
    private String name;
    private String lastModified;
    private String thumbnailUrl;
    private String version;
    private Document document;

    public static class Document {
        private String id;
        private String name;
        private String type;
        private List<Node> children;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<Node> getChildren() { return children; }
        public void setChildren(List<Node> children) { this.children = children; }
    }

    public static class Node {
        private String id;
        private String name;
        private String type;
        private List<Node> children;
        private AbsoluteBoundingBox absoluteBoundingBox;
        private List<Fill> fills;
        private List<Stroke> strokes;
        private String characters;
        private TypeStyle style;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public List<Node> getChildren() { return children; }
        public void setChildren(List<Node> children) { this.children = children; }
        public AbsoluteBoundingBox getAbsoluteBoundingBox() { return absoluteBoundingBox; }
        public void setAbsoluteBoundingBox(AbsoluteBoundingBox absoluteBoundingBox) { 
            this.absoluteBoundingBox = absoluteBoundingBox; 
        }
        public List<Fill> getFills() { return fills; }
        public void setFills(List<Fill> fills) { this.fills = fills; }
        public List<Stroke> getStrokes() { return strokes; }
        public void setStrokes(List<Stroke> strokes) { this.strokes = strokes; }
        public String getCharacters() { return characters; }
        public void setCharacters(String characters) { this.characters = characters; }
        public TypeStyle getStyle() { return style; }
        public void setStyle(TypeStyle style) { this.style = style; }
    }

    public static class AbsoluteBoundingBox {
        private double x;
        private double y;
        private double width;
        private double height;

        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public double getWidth() { return width; }
        public void setWidth(double width) { this.width = width; }
        public double getHeight() { return height; }
        public void setHeight(double height) { this.height = height; }
    }

    public static class Fill {
        private String type;
        private Color color;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Color getColor() { return color; }
        public void setColor(Color color) { this.color = color; }
    }

    public static class Stroke {
        private String type;
        private Color color;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Color getColor() { return color; }
        public void setColor(Color color) { this.color = color; }
    }

    public static class Color {
        private double r;
        private double g;
        private double b;
        private double a;

        public double getR() { return r; }
        public void setR(double r) { this.r = r; }
        public double getG() { return g; }
        public void setG(double g) { this.g = g; }
        public double getB() { return b; }
        public void setB(double b) { this.b = b; }
        public double getA() { return a; }
        public void setA(double a) { this.a = a; }
        
        public String toRgba() {
            return String.format("rgba(%d, %d, %d, %.2f)", 
                (int)(r * 255), (int)(g * 255), (int)(b * 255), a);
        }
    }

    public static class TypeStyle {
        private String fontFamily;
        private double fontSize;
        private String fontWeight;

        public String getFontFamily() { return fontFamily; }
        public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
        public double getFontSize() { return fontSize; }
        public void setFontSize(double fontSize) { this.fontSize = fontSize; }
        public String getFontWeight() { return fontWeight; }
        public void setFontWeight(String fontWeight) { this.fontWeight = fontWeight; }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }
}
