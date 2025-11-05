package com.robwestz.figmatobackend.binding;

import com.robwestz.figmatobackend.models.BackendDataSource;
import com.robwestz.figmatobackend.models.DataBinding;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataBindingManagerImplTest {

    private DataBindingManagerImpl manager;

    @Before
    public void setUp() {
        manager = new DataBindingManagerImpl();
    }

    @Test
    public void testCreateBinding() {
        DataBinding binding = new DataBinding();
        binding.setFigmaNodeId("123:456");
        binding.setFigmaExpression("{{ user.name }}");
        binding.setBindingType(DataBinding.BindingType.TEXT_VARIABLE);
        
        DataBinding created = manager.createBinding(binding);
        
        assertNotNull("Created binding should not be null", created);
        assertEquals("Node ID should match", "123:456", created.getFigmaNodeId());
    }

    @Test
    public void testCreateBinding_Null() {
        DataBinding created = manager.createBinding(null);
        
        assertNull("Should return null for null input", created);
    }

    @Test
    public void testCreateBinding_NullNodeId() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("{{ user.name }}");
        
        DataBinding created = manager.createBinding(binding);
        
        assertNull("Should return null when node ID is null", created);
    }

    @Test
    public void testUpdateBinding() {
        DataBinding binding = new DataBinding();
        binding.setFigmaNodeId("123:456");
        binding.setFigmaExpression("{{ user.name }}");
        binding.setBackendPath("user.username");
        
        manager.createBinding(binding);
        
        // Update the binding
        binding.setBackendPath("current_user.name");
        DataBinding updated = manager.updateBinding(binding);
        
        assertNotNull("Updated binding should not be null", updated);
        assertEquals("Backend path should be updated", "current_user.name", updated.getBackendPath());
    }

    @Test
    public void testGetBinding() {
        DataBinding binding = new DataBinding();
        binding.setFigmaNodeId("123:456");
        binding.setFigmaExpression("{{ user.name }}");
        
        manager.createBinding(binding);
        
        DataBinding retrieved = manager.getBinding("123:456");
        
        assertNotNull("Retrieved binding should not be null", retrieved);
        assertEquals("Should retrieve correct binding", "{{ user.name }}", retrieved.getFigmaExpression());
    }

    @Test
    public void testGetBinding_NonExistent() {
        DataBinding retrieved = manager.getBinding("999:999");
        
        assertNull("Should return null for non-existent binding", retrieved);
    }

    @Test
    public void testRemoveBinding() {
        DataBinding binding = new DataBinding();
        binding.setFigmaNodeId("123:456");
        binding.setFigmaExpression("{{ user.name }}");
        
        manager.createBinding(binding);
        manager.removeBinding("123:456");
        
        DataBinding retrieved = manager.getBinding("123:456");
        assertNull("Binding should be removed", retrieved);
    }

    @Test
    public void testGetAllBindings() {
        DataBinding binding1 = new DataBinding();
        binding1.setFigmaNodeId("123:456");
        binding1.setFigmaExpression("{{ user.name }}");
        
        DataBinding binding2 = new DataBinding();
        binding2.setFigmaNodeId("123:457");
        binding2.setFigmaExpression("{{ user.email }}");
        
        manager.createBinding(binding1);
        manager.createBinding(binding2);
        
        List<DataBinding> allBindings = manager.getAllBindings(null);
        
        assertEquals("Should have 2 bindings", 2, allBindings.size());
    }

    @Test
    public void testSuggestMatches_ExactMatch() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("{{ user.name }}");
        binding.setBindingType(DataBinding.BindingType.TEXT_VARIABLE);
        
        List<BackendDataSource> sources = new ArrayList<>();
        
        BackendDataSource exactMatch = new BackendDataSource();
        exactMatch.setName("user.name");
        exactMatch.setType(BackendDataSource.DataSourceType.VARIABLE);
        sources.add(exactMatch);
        
        BackendDataSource partialMatch = new BackendDataSource();
        partialMatch.setName("current_user.name");
        partialMatch.setType(BackendDataSource.DataSourceType.VARIABLE);
        sources.add(partialMatch);
        
        List<BackendDataSource> suggestions = manager.suggestMatches(binding, sources);
        
        assertFalse("Should have suggestions", suggestions.isEmpty());
        assertEquals("First suggestion should be exact match", "user.name", suggestions.get(0).getName());
    }

    @Test
    public void testSuggestMatches_PartialMatch() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("{{ name }}");
        binding.setBindingType(DataBinding.BindingType.TEXT_VARIABLE);
        
        List<BackendDataSource> sources = new ArrayList<>();
        
        BackendDataSource source1 = new BackendDataSource();
        source1.setName("user.name");
        source1.setType(BackendDataSource.DataSourceType.MODEL_FIELD);
        sources.add(source1);
        
        BackendDataSource source2 = new BackendDataSource();
        source2.setName("product.description");
        source2.setType(BackendDataSource.DataSourceType.MODEL_FIELD);
        sources.add(source2);
        
        List<BackendDataSource> suggestions = manager.suggestMatches(binding, sources);
        
        assertFalse("Should have suggestions", suggestions.isEmpty());
        // Should match "user.name" because "name" is in it
        assertTrue("Should include user.name", 
                   suggestions.stream().anyMatch(s -> s.getName().equals("user.name")));
    }

    @Test
    public void testSuggestMatches_EmptySources() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("{{ user.name }}");
        binding.setBindingType(DataBinding.BindingType.TEXT_VARIABLE);
        
        List<BackendDataSource> suggestions = manager.suggestMatches(binding, new ArrayList<>());
        
        assertTrue("Should return empty list", suggestions.isEmpty());
    }

    @Test
    public void testSuggestMatches_NullBinding() {
        List<BackendDataSource> sources = new ArrayList<>();
        BackendDataSource source = new BackendDataSource();
        source.setName("user.name");
        sources.add(source);
        
        List<BackendDataSource> suggestions = manager.suggestMatches(null, sources);
        
        assertTrue("Should return empty list for null binding", suggestions.isEmpty());
    }

    @Test
    public void testSuggestMatches_ActionBinding() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("[action:submit_form]");
        binding.setBindingType(DataBinding.BindingType.ACTION);
        
        List<BackendDataSource> sources = new ArrayList<>();
        
        BackendDataSource action = new BackendDataSource();
        action.setName("submit_form");
        action.setType(BackendDataSource.DataSourceType.ACTION);
        sources.add(action);
        
        BackendDataSource variable = new BackendDataSource();
        variable.setName("submit_form");
        variable.setType(BackendDataSource.DataSourceType.VARIABLE);
        sources.add(variable);
        
        List<BackendDataSource> suggestions = manager.suggestMatches(binding, sources);
        
        assertFalse("Should have suggestions", suggestions.isEmpty());
        // Action type should score higher
        assertEquals("First suggestion should be action type", 
                     BackendDataSource.DataSourceType.ACTION, suggestions.get(0).getType());
    }

    @Test
    public void testSuggestMatches_LoopBinding() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("[for:product in products]");
        binding.setBindingType(DataBinding.BindingType.LOOP);
        
        List<BackendDataSource> sources = new ArrayList<>();
        
        BackendDataSource list = new BackendDataSource();
        list.setName("products");
        list.setType(BackendDataSource.DataSourceType.LIST);
        sources.add(list);
        
        List<BackendDataSource> suggestions = manager.suggestMatches(binding, sources);
        
        assertFalse("Should have suggestions", suggestions.isEmpty());
        assertEquals("Should match products list", "products", suggestions.get(0).getName());
    }

    @Test
    public void testValidateBinding_NullBinding() {
        boolean isValid = manager.validateBinding(null, null);
        
        assertFalse("Should return false for null binding", isValid);
    }

    @Test
    public void testValidateBinding_NullProject() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("{{ user.name }}");
        binding.setBackendPath("user.name");
        
        boolean isValid = manager.validateBinding(binding, null);
        
        assertFalse("Should return false for null project", isValid);
    }

    @Test
    public void testValidateBinding_NotBound() {
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("{{ user.name }}");
        // No backend path set
        
        boolean isValid = manager.validateBinding(binding, null);
        
        assertFalse("Should return false for unbound binding", isValid);
    }
}
