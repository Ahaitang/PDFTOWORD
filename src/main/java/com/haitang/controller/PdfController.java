package com.haitang.controller;

import com.haitang.utils.PdfToWordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@RestController
public class PdfController {

    // Pdf转文字 不带格式
    @GetMapping("/convert-pdf-to-word")
    public String convertPdfToWordTest() {
        String pdfFilePath = "E:\\CODE\\Java\\IDEA\\PdfTest\\src\\main\\resources\\pdf\\HADOOP 思考题.pdf";

        String wordFilePath = pdfFilePath.substring(0, pdfFilePath.length() - 4) + ".docx";

        try (PDDocument pdfDocument = PDDocument.load(new File(pdfFilePath))) {
            XWPFDocument wordDocument = new XWPFDocument();

            PDFTextStripper textStripper = new PDFTextStripper();
            for (int pageNumber = 0; pageNumber < pdfDocument.getNumberOfPages(); pageNumber++) {
                textStripper.setStartPage(pageNumber + 1);
                textStripper.setEndPage(pageNumber + 1);
                String pageText = textStripper.getText(pdfDocument);
                XWPFParagraph paragraph = wordDocument.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(pageText);
            }

            try (OutputStream out = new java.io.FileOutputStream(wordFilePath)) {
                wordDocument.write(out);
            }

            System.out.println("PDF to Word conversion completed successfully!");
            return "200";
        } catch (IOException e) {
            e.printStackTrace();
            return "400";
        }
    }

    // Pdf转文字 带格式
    @GetMapping("/pdf")
    public String convertPdfToWord() {
        String pdfFilePath = "E:\\CODE\\Java\\IDEA\\PdfTest\\src\\main\\resources\\pdf\\HADOOP 思考题.pdf";
        String res = new PdfToWordUtils().PdfToWord(pdfFilePath);
        System.out.println(res);
        return res;
    }

}