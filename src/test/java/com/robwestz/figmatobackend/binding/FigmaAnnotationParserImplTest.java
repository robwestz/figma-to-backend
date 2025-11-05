package com.robwestz.figmatobackend.binding;

import com.robwestz.figmatobackend.models.DataBinding;
import com.robwestz.figmatobackend.models.FigmaFile;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FigmaAnnotationParserImplTest {

    private FigmaAnnotationParserImpl parser;

    @Before
    public void setUp() {
        parser = new FigmaAnnotationParserImpl();
    }

    @Test
    public void testParseTextVariableBinding() {
        DataBinding binding = parser.parseNodeName("text:{{ user.name }}", "123:456");
        
        assertNotNull("Binding should not be null", binding);
        assertEquals("Node ID should match", "123:456", binding.getFigmaNodeId());
        assertEquals("Binding type should be TEXT_VARIABLE", 
                     DataBinding.BindingType.TEXT_VARIABLE, binding.getBindingType());
        assertEquals("Expression should match", "{{ user.name }}", binding.getFigmaExpression());
    }

    @Test
    public void testParseTextVariableWithSpaces() {
        DataBinding binding = parser.parseNodeName("text:{{   user.email   }}", "123:457");
        
        assertNotNull("Binding should not be null", binding);
        assertEquals("Expression should be trimmed", "{{ user.email }}", binding.getFigmaExpression());
    }

    @Test
    public void testParseActionBinding() {
        DataBinding binding = parser.parseNodeName("button:[action:submit_form]", "123:458");
        
        assertNotNull("Binding should not be null", binding);
        assertEquals("Binding type should be ACTION", 
                     DataBinding.BindingType.ACTION, binding.getBindingType());
        assertEquals("Expression should match", "[action:submit_form]", binding.getFigmaExpression());
    }

    @Test
    public void testParseLoopBinding() {
        DataBinding binding = parser.parseNodeName("list:[for:product in products]", "123:459");
        
        assertNotNull("Binding should not be null", binding);
        assertEquals("Binding type should be LOOP", 
                     DataBinding.BindingType.LOOP, binding.getBindingType());
        assertEquals("Expression should match", "[for:product in products]", binding.getFigmaExpression());
    }

    @Test
    public void testParseConditionalBinding() {
        DataBinding binding = parser.parseNodeName("[if:is_logged_in]", "123:460");
        
        assertNotNull("Binding should not be null", binding);
        assertEquals("Binding type should be CONDITIONAL", 
                     DataBinding.BindingType.CONDITIONAL, binding.getBindingType());
        assertEquals("Expression should match", "[if:is_logged_in]", binding.getFigmaExpression());
    }

    @Test
    public void testParseNoBinding() {
        DataBinding binding = parser.parseNodeName("Regular Node Name", "123:461");
        
        assertNull("Binding should be null for regular names", binding);
    }

    @Test
    public void testParseNullNodeName() {
        DataBinding binding = parser.parseNodeName(null, "123:462");
        
        assertNull("Binding should be null for null node name", binding);
    }

    @Test
    public void testParseEmptyNodeName() {
        DataBinding binding = parser.parseNodeName("", "123:463");
        
        assertNull("Binding should be null for empty node name", binding);
    }

    @Test
    public void testIsBindingAnnotation_TextVariable() {
        assertTrue("Should recognize text binding", 
                   parser.isBindingAnnotation("text:{{ user.name }}"));
    }

    @Test
    public void testIsBindingAnnotation_Action() {
        assertTrue("Should recognize action binding", 
                   parser.isBindingAnnotation("button:[action:submit]"));
    }

    @Test
    public void testIsBindingAnnotation_Loop() {
        assertTrue("Should recognize loop binding", 
                   parser.isBindingAnnotation("list:[for:item in items]"));
    }

    @Test
    public void testIsBindingAnnotation_Conditional() {
        assertTrue("Should recognize conditional binding", 
                   parser.isBindingAnnotation("[if:condition]"));
    }

    @Test
    public void testIsBindingAnnotation_Regular() {
        assertFalse("Should not recognize regular name", 
                    parser.isBindingAnnotation("Regular Name"));
    }

    @Test
    public void testExtractDisplayName_TextVariable() {
        String displayName = parser.extractDisplayName("text:{{ user.name }}");
        
        assertEquals("Display name should be formatted", "User Name", displayName);
    }

    @Test
    public void testExtractDisplayName_Action() {
        String displayName = parser.extractDisplayName("button:[action:submit_form]");
        
        assertNotNull("Display name should not be null", displayName);
        assertFalse("Display name should not contain annotation", 
                    displayName.contains("[action:"));
    }

    @Test
    public void testExtractDisplayName_Regular() {
        String displayName = parser.extractDisplayName("My Button");
        
        assertEquals("Display name should remain unchanged", "My Button", displayName);
    }

    @Test
    public void testParseBindings_WithFigmaFile() {
        // Create a test Figma file structure
        FigmaFile figmaFile = new FigmaFile();
        FigmaFile.Document document = new FigmaFile.Document();
        List<FigmaFile.Node> children = new ArrayList<>();

        // Add nodes with bindings
        FigmaFile.Node textNode = new FigmaFile.Node();
        textNode.setId("1:1");
        textNode.setName("text:{{ user.name }}");
        children.add(textNode);

        FigmaFile.Node actionNode = new FigmaFile.Node();
        actionNode.setId("1:2");
        actionNode.setName("button:[action:submit]");
        children.add(actionNode);

        FigmaFile.Node regularNode = new FigmaFile.Node();
        regularNode.setId("1:3");
        regularNode.setName("Regular Frame");
        children.add(regularNode);

        document.setChildren(children);
        figmaFile.setDocument(document);

        // Parse bindings
        List<DataBinding> bindings = parser.parseBindings(figmaFile);

        assertEquals("Should find 2 bindings", 2, bindings.size());
        
        // Verify first binding
        DataBinding firstBinding = bindings.get(0);
        assertEquals("First binding should be TEXT_VARIABLE", 
                     DataBinding.BindingType.TEXT_VARIABLE, firstBinding.getBindingType());
        
        // Verify second binding
        DataBinding secondBinding = bindings.get(1);
        assertEquals("Second binding should be ACTION", 
                     DataBinding.BindingType.ACTION, secondBinding.getBindingType());
    }

    @Test
    public void testParseBindings_WithNestedNodes() {
        // Create a test Figma file with nested structure
        FigmaFile figmaFile = new FigmaFile();
        FigmaFile.Document document = new FigmaFile.Document();
        List<FigmaFile.Node> children = new ArrayList<>();

        // Parent node
        FigmaFile.Node parentNode = new FigmaFile.Node();
        parentNode.setId("1:1");
        parentNode.setName("Container");
        
        // Child nodes with bindings
        List<FigmaFile.Node> childNodes = new ArrayList<>();
        
        FigmaFile.Node childText = new FigmaFile.Node();
        childText.setId("1:2");
        childText.setName("text:{{ title }}");
        childNodes.add(childText);
        
        FigmaFile.Node childAction = new FigmaFile.Node();
        childAction.setId("1:3");
        childAction.setName("[action:delete]");
        childNodes.add(childAction);
        
        parentNode.setChildren(childNodes);
        children.add(parentNode);

        document.setChildren(children);
        figmaFile.setDocument(document);

        // Parse bindings
        List<DataBinding> bindings = parser.parseBindings(figmaFile);

        assertEquals("Should find 2 bindings in nested structure", 2, bindings.size());
    }

    @Test
    public void testParseBindings_EmptyFile() {
        FigmaFile figmaFile = new FigmaFile();
        List<DataBinding> bindings = parser.parseBindings(figmaFile);
        
        assertEquals("Should return empty list for empty file", 0, bindings.size());
    }

    @Test
    public void testParseBindings_NullFile() {
        List<DataBinding> bindings = parser.parseBindings(null);
        
        assertNotNull("Should return non-null list", bindings);
        assertEquals("Should return empty list for null file", 0, bindings.size());
    }

    @Test
    public void testComplexTextVariable() {
        DataBinding binding = parser.parseNodeName("text:{{ current_user.profile.display_name }}", "123:500");
        
        assertNotNull("Binding should not be null", binding);
        assertEquals("Should handle complex paths", 
                     "{{ current_user.profile.display_name }}", binding.getFigmaExpression());
    }

    @Test
    public void testLoopWithComplexExpression() {
        DataBinding binding = parser.parseNodeName("list:[for:post in user.posts]", "123:501");
        
        assertNotNull("Binding should not be null", binding);
        assertEquals("Should handle complex loop expressions", 
                     "[for:post in user.posts]", binding.getFigmaExpression());
    }
}
