package com.Test.demo.controllers;

import com.Test.demo.controllers.models.Task;
import com.Test.demo.controllers.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    private final TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(TaskRestController.class);

    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Endpoint to add a new task
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        try {
            taskService.addTask(task);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error adding task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint to get a task by its ID
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable int taskId) {
        Task task = taskService.getTaskById(taskId);

        if (task != null) {
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to update a task
    @PostMapping("/update")
    public ResponseEntity<Task> updateTask(@RequestBody Task updatedTask) {
        Task existingTask = taskService.getTaskById(updatedTask.getId());

        return getTaskResponseEntity(updatedTask, existingTask);
    }

    private ResponseEntity<Task> getTaskResponseEntity(@RequestBody Task updatedTask, Task existingTask) {
        if (existingTask != null) {
            try {
                taskService.updateTask(updatedTask);
                return new ResponseEntity<>(updatedTask, HttpStatus.OK);
            } catch (Exception e) {
                logger.error("Error updating task", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to get tasks by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable String priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Endpoint to get all tasks with optional sorting parameter
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false, defaultValue = "asc") String sort) {
        try {
            List<Task> tasksCopy = taskService.getAllTasksSortedByDate(sort);
            return new ResponseEntity<>(tasksCopy, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting all tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint to update a task by ID
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable int taskId, @RequestBody Task updatedTask) {
        Task existingTask = taskService.getTaskById(taskId);

        return getTaskResponseEntity(updatedTask, existingTask);
    }

    // Endpoint to delete a task by ID
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable int taskId) {
        Task existingTask = taskService.getTaskById(taskId);

        if (existingTask != null) {
            try {
                taskService.deleteTask(taskId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                logger.error("Error deleting task", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to import a task from a ZIP file
    @PostMapping("/import")
    public ResponseEntity<String> importTask(@RequestParam("file") MultipartFile zipFile) {
        if (zipFile.isEmpty()) {
            return ResponseEntity.badRequest().body("ZIP file is empty");
        }

        try {
            // Create a temporary directory for unpacking the ZIP archive
            Path importDir = Files.createTempDirectory("task_import");

            // Unpack the ZIP archive
            String originalFilename = zipFile.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("ZIP file name is null");
            }

            Path zipFilePath = importDir.resolve(originalFilename);
            Files.copy(zipFile.getInputStream(), zipFilePath, StandardCopyOption.REPLACE_EXISTING);
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFilePath))) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    Path entryPath = importDir.resolve(entry.getName());
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    zipInputStream.closeEntry();
                }
            }

            // Read the JSON file with task information
            File jsonFile = importDir.resolve("task.json").toFile();
            ObjectMapper objectMapper = new ObjectMapper();
            Task importedTask = objectMapper.readValue(jsonFile, Task.class);

            // Restore images
            List<String> imagePaths;
            try (Stream<Path> imagePathStream = Files.walk(importDir)
                    .filter(path -> !Files.isDirectory(path) && !path.equals(jsonFile.toPath()))) {
                imagePaths = imagePathStream.map(Path::toString).collect(Collectors.toList());
            }

            importedTask.setPdfAttachments(imagePaths);

            // Add the task to the system
            taskService.addTask(importedTask);

            // Clear temporary files
            try (Stream<Path> pathStream = Files.walk(importDir).sorted(Comparator.reverseOrder())) {
                pathStream.map(Path::toFile).forEach(file -> {
                    if (!file.delete()) {
                        // Handle delete failure, e.g., log a warning
                        logger.warn("Failed to delete file: {}", file.getAbsolutePath());
                    }
                });
            }

            Files.deleteIfExists(importDir);

            return ResponseEntity.ok("Task successfully imported");
        } catch (IOException e) {
            logger.error("Error importing task", e);
            return ResponseEntity.status(500).body("Error importing task");
        }
    }
}
