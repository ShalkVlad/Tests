package com.Test.demo.controllers;

import com.Test.demo.controllers.models.Task;
import com.Test.demo.controllers.services.TaskExporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TaskExporterTest {

    @InjectMocks
    private TaskExporter taskExporter;

    @Mock
    private Logger log;

    @Mock
    private ObjectMapper objectMapper;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Redirect standard error stream before each test
        System.setErr(new PrintStream(errContent));
    }

    @Test
    void exportTasks_FailedExportWithErrorMessage() {
        // Arrange
        List<Task> tasks = createSampleTasks();
        String exportPath = "testExportPath";

        // Mock the behavior of the File
        File exportDir = mock(File.class);

        // Use doReturn() for constructor call
        when(exportDir.exists()).thenReturn(false);
        when(exportDir.mkdirs()).thenReturn(false);
        when(exportDir.getAbsolutePath()).thenReturn(exportPath);

        // Act
        taskExporter.exportTasks(tasks, exportPath);

        // Assert
        String errorMessage = "Failed to create export directory: " + exportPath;
        assertTrue(errContent.toString().contains(errorMessage));

        // Verify that the expected methods were called
        verify(exportDir).exists();
        verify(exportDir).mkdirs();
        verify(exportDir).getAbsolutePath();

        // Log additional information
        System.out.println("Error Content: " + errContent.toString());
        System.out.println("Expected Error Message: " + errorMessage);
    }

    @Test
    void exportTasks_FailedExportWithoutErrorMessage() {
        // Arrange
        List<Task> tasks = createSampleTasks();
        String exportPath = "testExportPath";

        // Mock the behavior of the File
        File exportDir = mock(File.class);

        // Use doReturn() for constructor call
        when(exportDir.exists()).thenReturn(false);
        when(exportDir.mkdirs()).thenReturn(false);
        when(exportDir.getAbsolutePath()).thenReturn(exportPath);

        // Act
        taskExporter.exportTasks(tasks, exportPath);

        // Assert (you may add assertions based on your requirements)
        // For example, you might assert that certain methods were not called,
        // or that the logger was not used in a specific way.
    }

    private List<Task> createSampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "Sample Task 1", "", "", "", ""));
        return tasks;
    }
}
