package com.Test.demo.controllers;

import com.Test.demo.controllers.models.Task;
import com.Test.demo.controllers.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final List<Task> tasks = new ArrayList<>();
    private final TaskService taskService;

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String showTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        model.addAttribute("task", new Task(1, "Sample Task 1", "", "", "", ""));
        return "taskList";
    }

    @PostMapping("/export/{taskId}")
    public ResponseEntity<byte[]> exportTask(@PathVariable int taskId) {
        Task task = taskService.getTaskById(taskId);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // Создаем временную директорию для сохранения экспортированных файлов
            Path exportDir = Files.createTempDirectory("task_export");

            // Сохраняем информацию о задаче в JSON-файл
            File jsonFile = new File(exportDir.toFile(), "task.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(jsonFile, task);

            // Копируем изображения во временную директорию
            for (String imagePath : task.getPdfAttachments()) {
                File imageFile = new File(imagePath);
                Path destination = exportDir.resolve(imageFile.getName());
                Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            }

            // Создаем ZIP-архив
            String zipFileName = "task_export_" + UUID.randomUUID() + ".zip";
            Path zipFilePath = exportDir.resolve(zipFileName);

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
                Files.walk(exportDir)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(exportDir.relativize(path).toString());
                            try {
                                zipOutputStream.putNextEntry(zipEntry);
                                Files.copy(path, zipOutputStream);
                                zipOutputStream.closeEntry();
                            } catch (IOException e) {
                                log.error("Ошибка при создании ZIP-архива", e);
                            }
                        });
            }

            // Читаем ZIP-архив в массив байтов
            byte[] zipBytes = Files.readAllBytes(zipFilePath);

            // Очищаем временные файлы
            try {
                Files.deleteIfExists(zipFilePath);
                Files.walk(exportDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (!file.delete()) {
                                log.warn("Не удалось удалить файл: {}", file.getAbsolutePath());
                            }
                        });
                Files.deleteIfExists(exportDir);
            } catch (IOException e) {
                log.error("Ошибка при удалении временных файлов", e);
            }

            // Возвращаем ZIP-архив как массив байтов
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=" + zipFileName)
                    .body(zipBytes);
        } catch (IOException e) {
            log.error("Ошибка при экспорте задачи", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task) {
        tasks.add(task);
        return "redirect:/tasks";
    }

    @GetMapping("/search/{taskId}")
    public String searchTask(@PathVariable int taskId, Model model) {
        Task foundTask = taskService.getTaskById(taskId);

        model.addAttribute("foundTask", foundTask);
        return "taskList";
    }

    @ModelAttribute("tasks")
    public List<Task> getTasks() {
        return tasks;
    }
}
