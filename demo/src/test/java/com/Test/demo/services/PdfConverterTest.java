package com.Test.demo.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PdfConverterTest.class)
public class PdfConverterTest {

    @Test
    void convertPdfToImages_ShouldReturnListOfImagePaths() throws Exception {
        // Arrange
        String pdfPath = "C:/Users/shalk/OneDrive/Рабочий стол/demo/src/test/resources/test.pdf";
        PDDocument mockPDDocument = mock(PDDocument.class);
        PDFRenderer mockPdfRenderer = mock(PDFRenderer.class);

        // Mocking PDDocument.load() method
        File pdfFile = new File(pdfPath);
        try {
            when(PDDocument.load(pdfFile)).thenReturn(mockPDDocument);
        } catch (IOException e) {
            fail("Exception thrown while mocking PDDocument.load(): " + e.getMessage());
        }

        // Mocking PDFRenderer class and setting up behavior
        whenNew(PDFRenderer.class).withArguments(mockPDDocument).thenReturn(mockPdfRenderer);

        // Mocking rendering of images
        when(mockPdfRenderer.renderImageWithDPI(any(int.class), any(float.class), any(ImageType.class)))
                .thenReturn(mock(BufferedImage.class));

        // Create an instance of PdfProcessor
        PdfProcessor pdfConverter = new PdfProcessor();

        // Act
        try {
            List<String> result = pdfConverter.convertPdfToImages(pdfPath);

            // Assert
            assertEquals(3, result.size());  // Assuming there are 3 pages in the mock PDF
        } catch (IOException e) {
            // Handle or log the exception appropriately
            fail("Exception thrown during test: " + e.getMessage());
        }
    }
}
