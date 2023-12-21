    package com.Test.demo.controllers.models;

    import lombok.Getter;
    import lombok.Setter;

    import java.util.Date;
    import java.util.List;

    @Getter
    public class Task {

        @Setter
        private List<String> pdfAttachments;
        private final int id;
        @Setter
        private String title;
        @Setter
        private String description;
        @Setter
        private String categories;
        @Setter
        private String fileAttachment;
        @Setter
        private String priority;
        @Setter
        private Date date;
        @Setter
        private boolean completed; // New field for completed status

        // Constructor with parameters
        public Task(int id, String title, String description, String categories, String fileAttachment, String priority) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.categories = categories;
            this.fileAttachment = fileAttachment;  // Setting fileAttachment in the constructor
            this.priority = priority;
            this.date = new Date();
            this.completed = false;
        }


        // Default constructor
        public Task() {
            this(1, "Sample Task 1", "", "", "", "");
        }

    }
