package com.Test.demo.controllers;

import com.Test.demo.controllers.models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

class TaskTest {

    @Test
    void testTaskConstructor() {
        Task task = new Task();
        Assertions.assertNotNull(task.getDate());
        Assertions.assertEquals(1, task.getId());
    }

    @Test
    void testTaskParameterizedConstructor() {
        String title = "Sample Title";
        String description = "Sample Description";
        String categories = "Sample Categories";
        String fileAttachment = "Sample File Attachment";

        Task task = new Task(1, title, "", "", fileAttachment, "High");
        task.setDescription(description);
        task.setCategories(categories);

        Assertions.assertEquals(title, task.getTitle());
        Assertions.assertEquals(description, task.getDescription());
        Assertions.assertEquals(categories, task.getCategories());
        Assertions.assertEquals(fileAttachment, task.getFileAttachment());
        Assertions.assertEquals("High", task.getPriority());  // Assuming "High" is the expected priority
        Assertions.assertNotNull(task.getDate());
    }



    @Test
    void testTaskGettersAndSetters() {
        Task task = new Task();

        task.setTitle("Updated Title");
        Assertions.assertEquals("Updated Title", task.getTitle());

        task.setDescription("Updated Description");
        Assertions.assertEquals("Updated Description", task.getDescription());

        task.setCategories("Updated Categories");
        Assertions.assertEquals("Updated Categories", task.getCategories());

        task.setFileAttachment("Updated File Attachment");
        Assertions.assertEquals("Updated File Attachment", task.getFileAttachment());

        task.setPriority("Low");
        Assertions.assertEquals("Low", task.getPriority());

        Date currentDate = new Date();
        task.setDate(currentDate);
        Assertions.assertEquals(currentDate, task.getDate());

        task.setPdfAttachments(Collections.singletonList("pdfPath"));
        Assertions.assertEquals(Collections.singletonList("pdfPath"), task.getPdfAttachments());
    }
}

