package com.robwestz.figmatobackend.services;

import com.robwestz.figmatobackend.models.FigmaFile;
import com.robwestz.figmatobackend.models.GeneratedCode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CodeGeneratorServiceTest {

    private CodeGeneratorService codeGeneratorService;

    @Before
    public void setUp() {
        codeGeneratorService = new CodeGeneratorService();
    }

    @Test
    public void testGenerateCode_EmptyFigmaFile() {
        FigmaFile figmaFile = new FigmaFile();
        figmaFile.setName("Test Design");

        GeneratedCode code = codeGeneratorService.generateCode(figmaFile, null);

        assertNotNull("Generated code should not be null", code);
        assertNotNull("HTML should not be null", code.getHtml());
        assertNotNull("CSS should not be null", code.getCss());
        assertNotNull("JavaScript should not be null", code.getJavascript());

        assertTrue("HTML should contain DOCTYPE", code.getHtml().contains("<!DOCTYPE html>"));
        assertTrue("HTML should contain title", code.getHtml().contains("<title>Test Design</title>"));
        assertTrue("CSS should contain reset styles", code.getCss().contains("box-sizing"));
        assertTrue("JavaScript should contain DOMContentLoaded", code.getJavascript().contains("DOMContentLoaded"));
    }

    @Test
    public void testGenerateCode_WithTextNode() {
        FigmaFile figmaFile = new FigmaFile();
        figmaFile.setName("Text Design");

        FigmaFile.Document document = new FigmaFile.Document();
        List<FigmaFile.Node> children = new ArrayList<>();

        FigmaFile.Node textNode = new FigmaFile.Node();
        textNode.setName("Welcome Text");
        textNode.setType("TEXT");
        textNode.setCharacters("Hello, World!");

        FigmaFile.AbsoluteBoundingBox box = new FigmaFile.AbsoluteBoundingBox();
        box.setWidth(200);
        box.setHeight(50);
        textNode.setAbsoluteBoundingBox(box);

        FigmaFile.TypeStyle style = new FigmaFile.TypeStyle();
        style.setFontFamily("Arial");
        style.setFontSize(16);
        style.setFontWeight("400");
        textNode.setStyle(style);

        children.add(textNode);
        document.setChildren(children);
        figmaFile.setDocument(document);

        GeneratedCode code = codeGeneratorService.generateCode(figmaFile, null);

        assertNotNull("Generated code should not be null", code);
        assertTrue("HTML should contain text content", code.getHtml().contains("Hello, World!"));
        assertTrue("HTML should use span for text", code.getHtml().contains("<span"));
        assertTrue("CSS should contain font-family", code.getCss().contains("font-family: Arial"));
        assertTrue("CSS should contain font-size", code.getCss().contains("font-size: 16"));
    }

    @Test
    public void testGenerateCode_WithFrameAndChildren() {
        FigmaFile figmaFile = new FigmaFile();
        figmaFile.setName("Frame Design");

        FigmaFile.Document document = new FigmaFile.Document();
        List<FigmaFile.Node> children = new ArrayList<>();

        FigmaFile.Node frameNode = new FigmaFile.Node();
        frameNode.setName("Main Frame");
        frameNode.setType("FRAME");

        FigmaFile.AbsoluteBoundingBox box = new FigmaFile.AbsoluteBoundingBox();
        box.setWidth(400);
        box.setHeight(300);
        frameNode.setAbsoluteBoundingBox(box);

        // Add background color
        List<FigmaFile.Fill> fills = new ArrayList<>();
        FigmaFile.Fill fill = new FigmaFile.Fill();
        fill.setType("SOLID");
        FigmaFile.Color color = new FigmaFile.Color();
        color.setR(0.2);
        color.setG(0.4);
        color.setB(0.6);
        color.setA(1.0);
        fill.setColor(color);
        fills.add(fill);
        frameNode.setFills(fills);

        // Add child text node
        List<FigmaFile.Node> frameChildren = new ArrayList<>();
        FigmaFile.Node childText = new FigmaFile.Node();
        childText.setName("Title");
        childText.setType("TEXT");
        childText.setCharacters("Title Text");
        frameChildren.add(childText);
        frameNode.setChildren(frameChildren);

        children.add(frameNode);
        document.setChildren(children);
        figmaFile.setDocument(document);

        GeneratedCode code = codeGeneratorService.generateCode(figmaFile, null);

        assertNotNull("Generated code should not be null", code);
        assertTrue("HTML should contain frame div", code.getHtml().contains("<div"));
        assertTrue("HTML should contain child text", code.getHtml().contains("Title Text"));
        assertTrue("CSS should contain width", code.getCss().contains("width: 400"));
        assertTrue("CSS should contain height", code.getCss().contains("height: 300"));
        assertTrue("CSS should contain background color", code.getCss().contains("background-color: rgba"));
    }

    @Test
    public void testGenerateCode_WithSpecificNodeName() {
        FigmaFile figmaFile = new FigmaFile();
        figmaFile.setName("Multi Node Design");

        FigmaFile.Document document = new FigmaFile.Document();
        List<FigmaFile.Node> children = new ArrayList<>();

        FigmaFile.Node node1 = new FigmaFile.Node();
        node1.setName("Button");
        node1.setType("FRAME");
        children.add(node1);

        FigmaFile.Node node2 = new FigmaFile.Node();
        node2.setName("Header");
        node2.setType("FRAME");
        children.add(node2);

        document.setChildren(children);
        figmaFile.setDocument(document);

        // Generate code for specific node
        GeneratedCode code = codeGeneratorService.generateCode(figmaFile, "Button");

        assertNotNull("Generated code should not be null", code);
        assertTrue("HTML should contain button class", code.getHtml().contains("button"));
        assertFalse("HTML should not contain header class", code.getHtml().contains("header"));
    }

    @Test
    public void testColorToRgba() {
        FigmaFile.Color color = new FigmaFile.Color();
        color.setR(1.0);
        color.setG(0.5);
        color.setB(0.0);
        color.setA(0.8);

        String rgba = color.toRgba();
        assertEquals("rgba(255, 127, 0, 0.80)", rgba);
    }

    @Test
    public void testSuggestFilePath() {
        // This test would require mocking the Project object
        // For now, we'll just verify the method exists and returns a non-null value
        String path = codeGeneratorService.suggestFilePath(null, "test.html");
        assertNotNull("Suggested path should not be null", path);
        assertEquals("test.html", path);
    }
}
