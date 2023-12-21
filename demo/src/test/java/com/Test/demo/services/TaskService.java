package com.Test.demo.services;

import com.Test.demo.controllers.models.Task;
import com.Test.demo.controllers.services.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    @Test
    void getAllTasks() {
        // Mocking the behavior of the taskService
        List<Task> mockTasks = new ArrayList<>();
        when(taskService.getAllTasks()).thenReturn(mockTasks);

        // Call the method under test
        List<Task> result = taskService.getAllTasks();

        // Assert the result
        assertEquals(mockTasks, result);
    }

    @Test
    void addTask() {
        // Arrange
        Task taskToAdd = new Task(/* provide necessary constructor arguments */);

        // Call the method under test
        taskService.addTask(taskToAdd);

        // Verify that the addTask method was called with the correct argument
        verify(taskService).addTask(taskCaptor.capture());

        // Assert the captured argument if needed
        assertEquals(taskToAdd, taskCaptor.getValue());
    }

    // Add more test methods for other methods in TaskService as needed
}
