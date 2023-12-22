
package com.Test.demo.controllers;

import com.Test.demo.controllers.models.Task;
import com.Test.demo.controllers.services.PdfConverter;
import com.Test.demo.controllers.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.Test.demo.controllers.services.TaskService.logger;

@RestController
@RequestMapping("/")
public class MainController {

    private final TaskService taskService;
    private final FileService fileService;
    private final PdfConverter pdfConverter;

    @Autowired
    public MainController(TaskService taskService, FileService fileService, PdfConverter pdfConverter) {
        this.taskService = taskService;
        this.fileService = fileService;
        this.pdfConverter = pdfConverter;
    }

    private List<String> handlePdfUploads(List<MultipartFile> pdfFiles) {
        return pdfFiles.stream()
                .map(this::handleSinglePdfUpload)
                .collect(Collectors.toList());
    }
    private List<Task> createSampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, "Sample Task 1", "2023-01-01", "Description 1", "", ""));
        tasks.add(new Task(2, "Sample Task 2", "2023-01-02", "Description 2", "", ""));
        return tasks;
    }

    @PostMapping("/uploadPdf/{taskId}")
    public ResponseEntity<String> uploadPdf(@PathVariable int taskId, @RequestParam("pdfFile") MultipartFile pdfFile) {
        try {
            Task task = taskService.getTaskById(taskId);

            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            String pdfPath = fileService.saveFile(pdfFile);
            List<String> imagePaths = pdfConverter.convertPdfToImages(pdfPath);
            task.setPdfAttachments(imagePaths);

            return ResponseEntity.ok("PDF successfully uploaded and converted.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF: " + e.getMessage());
        }
    }

    private String handleSinglePdfUpload(MultipartFile pdfFile) {
        try {
            return fileService.saveFile(pdfFile);
        } catch (IOException e) {
            handleMainControllerError("Error handling PDF upload", e);
            return null;
        }
    }

    @GetMapping
    public String showTasks(Model model, @RequestParam(required = false, defaultValue = "asc") String sort) {
        try {
            List<Task> sortedTasks = taskService.getAllTasksSortedByDate(sort);
            model.addAttribute("tasks", sortedTasks);
            model.addAttribute("task", new Task(1, "Sample Task 1", "", "", "", ""));
        } catch (Exception e) {
            handleMainControllerError("Error fetching tasks", e);
        }
        return "taskList";
    }

    @GetMapping("/edit/{taskId}")
    public String editTask(@PathVariable int taskId, Model model) {
        try {
            Task task = taskService.getTaskById(taskId);
            model.addAttribute("task", task);
        } catch (Exception e) {
            handleMainControllerError("Error editing task", e);
        }
        return "taskList";
    }

    @PostMapping("/update")
    public String updateTask(@ModelAttribute Task updatedTask, Model model) {
        try {
            taskService.updateTask(updatedTask);
            List<Task> sortedTasks = taskService.getAllTasksSortedByDate("asc");
            model.addAttribute("tasks", sortedTasks);
            model.addAttribute("task", new Task(1, "Sample Task 1", "", "", "", ""));
        } catch (Exception e) {
            handleMainControllerError("Error updating task", e);
        }
        return "taskList";
    }

    @GetMapping("/delete/{taskId}")
    public String deleteTask(@PathVariable int taskId) {
        try {
            taskService.deleteTask(taskId);
        } catch (Exception e) {
            handleMainControllerError("Error deleting task", e);
        }
        return "redirect:/";
    }

    @GetMapping("/tasksByDate")
    public String getTasksByDate(@RequestParam String date, Model model) {
        try {
            List<Task> tasks = taskService.getTasksByDate(date);
            List<Task> sortedTasks = tasks.stream()
                    .sorted(Comparator.comparing(Task::getDate))
                    .collect(Collectors.toList());
            model.addAttribute("tasks", sortedTasks);
            model.addAttribute("task", new Task(1, "Sample Task 1", "", "", "", ""));
        } catch (Exception e) {
            handleMainControllerError("Error fetching tasks by date", e);
        }
        return "taskList";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, @RequestParam("pdfFiles") List<MultipartFile> pdfFiles) {
        try {
            List<String> pdfPaths = handlePdfUploads(pdfFiles);
            task.setPdfAttachments(pdfPaths);

            taskService.addTask(task);
        } catch (Exception e) {
            handleMainControllerError("Error adding task", e);
        }
        return "redirect:/";
    }

    private void handleMainControllerError(String message, Exception e) {
        logger.error(message, e);
    }
}