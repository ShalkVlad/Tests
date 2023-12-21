package com.Test.demo.controllers;

import com.Test.demo.controllers.models.Task;
import com.Test.demo.controllers.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TaskRestControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskRestController taskRestController;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTask() {
        // Create a new task
        Task task = new Task();
        // The addTask method returns void, so use doNothing()
        doNothing().when(taskService).addTask(task);

        // Perform the operation
        ResponseEntity<Task> response = taskRestController.addTask(task);

        // Assert the results
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(taskService, times(1)).addTask(task);
    }

    @Test
    void testGetTaskById() {
        // Set up test data
        int taskId = 1;
        Task task = new Task();
        when(taskService.getTaskById(taskId)).thenReturn(task);

        // Perform the operation
        ResponseEntity<Task> response = taskRestController.getTaskById(taskId);

        // Assert the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }
}