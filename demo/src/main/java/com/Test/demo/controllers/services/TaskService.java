package com.Test.demo.controllers.services;

import com.Test.demo.controllers.models.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private List<String> attachments;

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getAllTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }


    public void deleteImageFromTask(int taskId, String imagePath) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.getPdfAttachments().remove(imagePath);
        }
    }

    public void updateImageInTask(int taskId, String oldImagePath, String newImagePath) {
        Task task = getTaskById(taskId);
        if (task != null) {
            List<String> pdfAttachments = task.getPdfAttachments();
            int index = pdfAttachments.indexOf(oldImagePath);
            if (index != -1) {
                pdfAttachments.set(index, newImagePath);
            }
        }
    }


    public List<Task> getTasksByCategory(String category) {
        return tasks.stream()
                .filter(task -> task.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    public void linkImagesToTask(int taskId, List<String> imagePaths) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setPdfAttachments(imagePaths);
        }
    }

    public List<Task> getAllTasksSortedByTitle(String sortOrder) {
        Comparator<Task> comparator = Comparator.comparing(Task::getTitle);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }


    public void addAttachmentToTask(int taskId, String attachment) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.getPdfAttachments().add(attachment);
        }
    }


    public Task getTaskById(int taskId) {
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                return task;
            }
        }
        return null;
    }

    public List<Task> getTasksByDate(String sortOrder) {
        Comparator<Task> comparator = Comparator.comparing(Task::getDate);

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByPriority(String priority) {
        return tasks.stream()
                .filter(task -> task.getPriority().equalsIgnoreCase(priority))
                .collect(Collectors.toList());
    }

    public void updateTask(Task updatedTask) {
        Task existingTask = getTaskById(updatedTask.getId());

        if (existingTask != null) {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCategories(updatedTask.getCategories());
            existingTask.setFileAttachment(updatedTask.getFileAttachment());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setDate(updatedTask.getDate());
        }
    }

    public List<Task> getAllTasksSortedByDate(String sortOrder) {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        Comparator<Task> comparator = Comparator.comparing(Task::getDate);

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        sortedTasks.sort(comparator);
        return sortedTasks;
    }

    public void deleteTask(int taskId) {
        tasks.removeIf(task -> task.getId() == taskId);
    }
}
