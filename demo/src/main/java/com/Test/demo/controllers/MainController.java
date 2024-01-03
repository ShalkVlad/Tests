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
import java.util.List;

import static com.Test.demo.controllers.services.TaskService.logger;

@RestController
@RequestMapping("/home")
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

    // Endpoint for uploading a PDF file and converting it to images
    @PostMapping("/uploadPdf/{taskId}")
    public ResponseEntity<String> uploadPdf(@PathVariable int taskId, @RequestParam("pdfFile") MultipartFile pdfFile) {
        try {
            // Retrieve the task by ID
            Task task = taskService.getTaskById(taskId);

            // Check if the task exists
            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            // Save the PDF file and get the path
            String pdfPath = fileService.saveFile(pdfFile);

            // Convert PDF to images and update the task's image attachments
            List<String> imagePaths = pdfConverter.convertPdfToImages(pdfPath);
            task.setPdfAttachments(imagePaths);

            return ResponseEntity.ok("PDF successfully uploaded and converted.");
        } catch (IOException e) {
            // Handle exception if there's an error processing the PDF
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF: " + e.getMessage());
        }
    }

    // Endpoint for displaying tasks sorted by date
    @GetMapping
    public String showTasks(Model model, @RequestParam(required = false, defaultValue = "asc") String sort) {
        try {
            // Get all tasks sorted by date
            List<Task> sortedTasks = taskService.getAllTasksSortedByDate(sort);

            // Add sorted tasks to the model for rendering
            model.addAttribute("tasks", sortedTasks);

            // Removed unused method and variable
        } catch (Exception e) {
            // Handle exception if there's an error fetching tasks
            handleMainControllerError("Error fetching tasks", e);
        }

        // Return the view name for rendering
        return "taskList";
    }

    // Method to handle errors and log messages
    private void handleMainControllerError(String message, Exception e) {
        logger.error(message, e);
    }
}
