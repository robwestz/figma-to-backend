package com.robwestz.figmatobackend.models;

public class GeneratedCode {
    private String html;
    private String css;
    private String javascript;
    private String suggestedPath;

    public GeneratedCode() {
    }

    public GeneratedCode(String html, String css, String javascript, String suggestedPath) {
        this.html = html;
        this.css = css;
        this.javascript = javascript;
        this.suggestedPath = suggestedPath;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public String getSuggestedPath() {
        return suggestedPath;
    }

    public void setSuggestedPath(String suggestedPath) {
        this.suggestedPath = suggestedPath;
    }
}
