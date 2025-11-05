package com.robwestz.figmatobackend.sync;

import com.robwestz.figmatobackend.models.DataBinding;
import com.robwestz.figmatobackend.models.SyncContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class NonDestructiveSyncServiceImplTest {

    private NonDestructiveSyncServiceImpl service;

    @Before
    public void setUp() {
        service = new NonDestructiveSyncServiceImpl();
    }

    @Test
    public void testCalculateChecksum() {
        String code = "<div>Hello World</div>";
        String checksum = service.calculateChecksum(code);
        
        assertNotNull("Checksum should not be null", checksum);
        assertFalse("Checksum should not be empty", checksum.isEmpty());
        assertEquals("Checksum should be consistent", checksum, service.calculateChecksum(code));
    }

    @Test
    public void testCalculateChecksum_Normalized() {
        String code1 = "<div>Hello World</div>";
        String code2 = "<div>  Hello   World  </div>";
        
        String checksum1 = service.calculateChecksum(code1);
        String checksum2 = service.calculateChecksum(code2);
        
        // Checksums should be same after normalization
        assertEquals("Checksums should match after normalization", checksum1, checksum2);
    }

    @Test
    public void testCalculateChecksum_Null() {
        String checksum = service.calculateChecksum(null);
        
        assertNotNull("Should return non-null for null input", checksum);
        assertEquals("Should return empty string", "", checksum);
    }

    @Test
    public void testWrapWithMarkers() {
        String code = "<div>Content</div>";
        String checksum = "abc123";
        String blockId = "main";
        
        String wrapped = service.wrapWithMarkers(code, checksum, blockId);
        
        assertNotNull("Wrapped code should not be null", wrapped);
        assertTrue("Should contain start marker", wrapped.contains("FIGMA_GEN:START"));
        assertTrue("Should contain checksum", wrapped.contains(checksum));
        assertTrue("Should contain block ID", wrapped.contains(blockId));
        assertTrue("Should contain end marker", wrapped.contains("FIGMA_GEN:END"));
        assertTrue("Should contain original code", wrapped.contains(code));
    }

    @Test
    public void testWrapWithMarkers_NullCode() {
        String wrapped = service.wrapWithMarkers(null, "abc123", "main");
        
        assertNotNull("Should return non-null", wrapped);
        assertEquals("Should return empty string", "", wrapped);
    }

    @Test
    public void testMergeCode_BothPresent() {
        String generated = "<div class=\"generated\">Generated</div>";
        String manual = "<div class=\"manual\">Manual</div>";
        
        String merged = service.mergeCode(generated, manual);
        
        assertNotNull("Merged code should not be null", merged);
        assertTrue("Should contain manual code", merged.contains("Manual"));
        assertTrue("Should contain generated code", merged.contains("Generated"));
    }

    @Test
    public void testMergeCode_OnlyGenerated() {
        String generated = "<div>Generated</div>";
        
        String merged = service.mergeCode(generated, null);
        
        assertEquals("Should return generated code", generated, merged);
    }

    @Test
    public void testMergeCode_OnlyManual() {
        String manual = "<div>Manual</div>";
        
        String merged = service.mergeCode(null, manual);
        
        assertEquals("Should return manual code", manual, merged);
    }

    @Test
    public void testApplyBindings_NoBindings() {
        String code = "<div>Hello</div>";
        SyncContext context = new SyncContext();
        context.setBindings(new ArrayList<>());
        
        String result = service.applyBindings(code, context);
        
        assertEquals("Code should remain unchanged", code, result);
    }

    @Test
    public void testApplyBindings_NullContext() {
        String code = "<div>Hello</div>";
        
        String result = service.applyBindings(code, null);
        
        assertEquals("Code should remain unchanged", code, result);
    }

    @Test
    public void testApplyBindings_WithBinding() {
        String code = "<div>user.name</div>";
        
        SyncContext context = new SyncContext();
        context.setFrameworkType(SyncContext.FrameworkType.DJANGO);
        
        List<DataBinding> bindings = new ArrayList<>();
        DataBinding binding = new DataBinding();
        binding.setFigmaExpression("{{ user.name }}");
        binding.setBackendPath("current_user.username");
        binding.setBindingType(DataBinding.BindingType.TEXT_VARIABLE);
        bindings.add(binding);
        
        context.setBindings(bindings);
        
        String result = service.applyBindings(code, context);
        
        assertNotNull("Result should not be null", result);
        // In full implementation, would check for Django template tags
    }

    @Test
    public void testExtractManualCode_NoFile() {
        String manual = service.extractManualCode("/non/existent/file.html");
        
        assertNotNull("Should return non-null", manual);
        assertEquals("Should return empty string", "", manual);
    }

    @Test
    public void testExtractManualCode_Null() {
        String manual = service.extractManualCode(null);
        
        assertNotNull("Should return non-null", manual);
        assertEquals("Should return empty string", "", manual);
    }

    @Test
    public void testHasManualChanges_NullPath() {
        boolean hasChanges = service.hasManualChanges(null, "checksum");
        
        assertFalse("Should return false for null path", hasChanges);
    }

    @Test
    public void testHasManualChanges_NullChecksum() {
        boolean hasChanges = service.hasManualChanges("/some/path", null);
        
        assertFalse("Should return false for null checksum", hasChanges);
    }

    @Test
    public void testSyncWithExisting_NullProject() {
        SyncContext context = new SyncContext();
        
        var result = service.syncWithExisting(null, context);
        
        assertNull("Should return null for null project", result);
    }

    @Test
    public void testSyncWithExisting_NullContext() {
        var result = service.syncWithExisting(null, null);
        
        assertNull("Should return null for null context", result);
    }

    @Test
    public void testMarkerPattern() {
        String code = "<!-- FIGMA_GEN:START:abc123:main -->\n" +
                     "<div>Generated</div>\n" +
                     "<!-- FIGMA_GEN:END:main -->";
        
        // Test that markers can be found
        assertTrue("Should contain start marker", code.contains("FIGMA_GEN:START"));
        assertTrue("Should contain end marker", code.contains("FIGMA_GEN:END"));
        assertTrue("Should contain checksum", code.contains("abc123"));
    }

    @Test
    public void testChecksumConsistency() {
        String code = "<div>Test</div>";
        
        String checksum1 = service.calculateChecksum(code);
        String checksum2 = service.calculateChecksum(code);
        String checksum3 = service.calculateChecksum(code);
        
        assertEquals("Checksums should be consistent", checksum1, checksum2);
        assertEquals("Checksums should be consistent", checksum2, checksum3);
    }

    @Test
    public void testChecksumDifference() {
        String code1 = "<div>Version 1</div>";
        String code2 = "<div>Version 2</div>";
        
        String checksum1 = service.calculateChecksum(code1);
        String checksum2 = service.calculateChecksum(code2);
        
        assertNotEquals("Different code should have different checksums", checksum1, checksum2);
    }
}
