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

    @GetMapping
    public String showTasks(Model model, @RequestParam(required = false, defaultValue = "asc") String sort) {
        try {
            List<Task> sortedTasks = taskService.getAllTasksSortedByDate(sort);
            model.addAttribute("tasks", sortedTasks);

            // Removed unused method and variable
        } catch (Exception e) {
            handleMainControllerError("Error fetching tasks", e);
        }
        return "taskList";
    }

    private void handleMainControllerError(String message, Exception e) {
        logger.error(message, e);
    }
}