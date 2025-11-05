package com.robwestz.figmatobackend.introspection;

import com.intellij.openapi.project.Project;
import com.robwestz.figmatobackend.models.BackendDataSource;
import com.robwestz.figmatobackend.models.SyncContext;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for BackendIntrospectionServiceImpl.
 * Note: Full PSI-based tests require IntelliJ test framework.
 * These tests verify the service structure and logic.
 */
public class BackendIntrospectionServiceImplTest {

    private BackendIntrospectionServiceImpl service;

    @Before
    public void setUp() {
        service = new BackendIntrospectionServiceImpl();
    }

    @Test
    public void testServiceCreation() {
        assertNotNull("Service should be created", service);
    }

    @Test
    public void testDiscoverDataSources_NullProject() {
        List<BackendDataSource> sources = service.discoverDataSources(null);
        
        assertNotNull("Should return non-null list", sources);
        assertEquals("Should return empty list for null project", 0, sources.size());
    }

    @Test
    public void testDetectFramework_NullProject() {
        SyncContext.FrameworkType framework = service.detectFramework(null);
        
        assertEquals("Should return GENERIC for null project", 
                     SyncContext.FrameworkType.GENERIC, framework);
    }

    @Test
    public void testDiscoverInFile_NullProject() {
        List<BackendDataSource> sources = service.discoverInFile(null, "models.py");
        
        assertNotNull("Should return non-null list", sources);
        assertEquals("Should return empty list for null project", 0, sources.size());
    }

    @Test
    public void testDiscoverInFile_NullPath() {
        // Would need mock project for full test
        List<BackendDataSource> sources = service.discoverInFile(null, null);
        
        assertNotNull("Should return non-null list", sources);
        assertEquals("Should return empty list for null path", 0, sources.size());
    }

    @Test
    public void testValidateBackendPath_NullProject() {
        boolean isValid = service.validateBackendPath(null, "user.name");
        
        assertFalse("Should return false for null project", isValid);
    }

    @Test
    public void testValidateBackendPath_NullPath() {
        boolean isValid = service.validateBackendPath(null, null);
        
        assertFalse("Should return false for null path", isValid);
    }

    @Test
    public void testValidateBackendPath_EmptyPath() {
        boolean isValid = service.validateBackendPath(null, "");
        
        assertFalse("Should return false for empty path", isValid);
    }
}
