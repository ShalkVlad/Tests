package com.Test.demo.controllers.services;

import com.Test.demo.controllers.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class TaskExporter {

    private final ObjectMapper objectMapper;

    // Constructor that accepts an ObjectMapper
    public TaskExporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void exportTasks(List<Task> tasks, String exportPath) {
        try {
            // Create a directory for export
            File exportDir = new File(exportPath);
            if (!exportDir.exists() && !exportDir.mkdirs()) {
                log.error("Failed to create export directory: {}", exportPath);
                return;
            }

            // Save each task to a separate JSON file
            for (Task task : tasks) {
                String jsonFileName = "task_" + task.getId() + ".json";
                File jsonFile = new File(exportDir, jsonFileName);
                objectMapper.writeValue(jsonFile, task);
            }

            log.info("Task data export completed. Export directory: {}", exportPath);
        } catch (IOException e) {
            log.error("Error during task data export", e);
        }
    }
}
