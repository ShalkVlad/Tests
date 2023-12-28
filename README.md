```markdown
# Project "NoteBook"


## Overview

The "NoteBook" project is a Spring Boot application designed for task management. It allows users to upload PDF files, export tasks, and perform other task-related operations. Below is information about the project.

## Table of Contents

- [Project Description](#project-description)
- [Dependencies](#dependencies)
- [Running the Project](#running-the-project)
- [Test Classes](#test-classes)
  - [MainControllerTest](#maincontrollertest)
  - [TaskExporterTest](#taskexportertest)
  - [TaskTest](#tasktest)
  - [PdfConverterTest](#pdfconvertertest)
  - [PdfProcessorTest](#pdfprocessortest)
  - [TaskServiceTest](#taskservicetest)

## Project Description

The "NoteBook" project is developed on the Spring Boot platform and is intended for task management. Key features include uploading PDF files, exporting tasks to files, and handling tasks such as adding, updating, and deleting.

## Dependencies

The project uses the following key dependencies:

- Spring Boot 2.6.3
- Jakarta Servlet API 5.0.0
- JUnit Jupiter 5.8.2
- Mockito 4.0.0
- Spring Web 6.1.0
- Jackson Databind 2.15.3
- Apache PDFBox 2.0.28
- PowerMock 2.0.9
- Lombok 1.18.30
- and others (see [pom.xml](pom.xml) file)

## Running the Project

To run the project, follow these steps:

1. Ensure that [Maven](https://maven.apache.org/) and [JDK](https://www.oracle.com/java/technologies/javase-downloads.html) are installed in your environment.
2. Clone the project repository.
3. Navigate to the project directory.
4. Run the command: `mvn spring-boot:run`.

The project will be accessible at [http://localhost:8080](http://localhost:8080).

## Test Classes

Test classes are designed to verify the correctness of various project components. You can run the tests using your development environment or by executing the `mvn test` command.

### MainControllerTest

Tests the `MainController`, focusing on the `uploadPdf` method.

### TaskExporterTest

Tests the `TaskExporter` service, covering the task export functionality.

### TaskTest

Tests the model class `Task`, including constructors, getters, and setters.

### PdfConverterTest

Tests the `PdfConverter` service, checking the conversion of a PDF file to images.

### PdfProcessorTest

Tests the service class `PdfProcessor`, covering the processing of PDF files.

### TaskServiceTest

Tests the service class `TaskService`, verifying various task management methods.
