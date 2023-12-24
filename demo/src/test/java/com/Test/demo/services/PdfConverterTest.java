package com.Test.demo.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@ExtendWith(MockitoExtension.class)
public class PdfConverterTest {

    @Mock
    private PDDocument mockPDDocument;

    @Mock
    private PDFRenderer mockPdfRenderer;

    @InjectMocks
    private PdfProcessor pdfConverter;

    @Test
    void convertPdfToImages_ShouldReturnListOfImagePaths() throws IOException, Exception {
        // Arrange
        URL resource = getClass().getClassLoader().getResource("test.pdf");
        if (resource == null) {
            throw new IllegalStateException("Test PDF file not found in resources.");
        }
        String pdfPath = new File(resource.getFile()).getAbsolutePath();

        // Mocking PDDocument.load() method
        File pdfFile = new File(pdfPath);
        when(mockPDDocument.load(pdfFile)).thenReturn(mockPDDocument);

        // Mocking PDFRenderer class and setting up behavior
        whenNew(PDFRenderer.class).withArguments(mockPDDocument).thenReturn(mockPdfRenderer);

        // Mocking rendering of images
        when(mockPdfRenderer.renderImageWithDPI(any(int.class), any(float.class), any(ImageType.class)))
                .thenReturn(mock(BufferedImage.class));

        // Act
        List<String> result = pdfConverter.convertPdfToImages(pdfPath);

        // Assert
        assertEquals(3, result.size());  // Assuming there are 3 pages in the mock PDF
    }
}