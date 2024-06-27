package com.haitang.utils;

import com.spire.doc.Document;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.widget.PdfPageCollection;

import java.io.File;

public class PdfToWordUtils {
    // 涉及到的路径
    // 1、pdf所在的路径，真实测试种是从外部引入的

    // 2、如果是大文件，需要进行切分，保存的子pdf路径
    String splitPath = "./split/";

    // 3、如果是大文件，需要对子pdf文件一个一个进行转化
    String docPath = "./doc/";

    public String PdfToWord(String srcPath) {


        // 4、最终生成的doc所在的目录，默认是和引入的一个地方，开源时对外提供下载的接口。
        String desPath = srcPath.substring(0, srcPath.length() - 4) + ".docx";
        boolean result = false;
        try {
            // 0、判断输入的是否是pdf文件
            //第一步：判断输入的是否合法
            boolean flag = isPDFFile(srcPath);
            //第二步：在输入的路径下新建文件夹
            boolean flag1 = create();

            if (flag && flag1) {
                // 1、加载pdf
                PdfDocument pdf = new PdfDocument();
                pdf.loadFromFile(srcPath);
                PdfPageCollection num = pdf.getPages();

                // 2、如果pdf的页数小于11，那么直接进行转化
                if (num.getCount() <= 10) {
                    pdf.saveToFile(desPath, com.spire.pdf.FileFormat.DOCX);
                }
                // 3、否则输入的页数比较多，就开始进行切分再转化
                else {
                    // 第一步：将其进行切分,每页一张pdf
                    pdf.split(splitPath + "test{0}.pdf", 0);

                    // 第二步：将切分的pdf，一个一个进行转换
                    File[] fs = getSplitFiles(splitPath);
                    if (fs != null) {
                        for (File f : fs) {
                            PdfDocument sonpdf = new PdfDocument();
                            sonpdf.loadFromFile(f.getAbsolutePath());
                            sonpdf.saveToFile(docPath + f.getName().substring(0, f.getName().length() - 4) + ".docx", FileFormat.DOCX);
                        }
                    }
                    //第三步：对转化的doc文档进行合并，合并成一个大的word
                    try {
                        result = merge(docPath, desPath);
                        System.out.println(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {
                System.out.println("输入的不是pdf文件");
                return "输入的不是pdf文件";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //4、把刚刚缓存的split和doc删除
            if (result) {
                clearFiles(splitPath);
                clearFiles(docPath);
            }
        }
        return "转换成功";
    }


    private boolean create() {
        File f = new File(splitPath);
        File f1 = new File(docPath);
        if (!f.exists()) f.mkdirs();
        if (!f.exists()) f1.mkdirs();
        return true;
    }

    // 判断是否是pdf文件
    private boolean isPDFFile(String srcPath2) {
        File file = new File(srcPath2);
        String filename = file.getName();
        return filename.endsWith(".pdf");
    }

    public static boolean merge(String docPath, String desPath) {
        File[] fs = getSplitFiles(docPath);
        System.out.println(docPath);
        Document document = new Document(docPath + "test0.docx");

        if (fs != null) {
            for (int i = 1; i < fs.length; i++) {
                document.insertTextFromFile(docPath + "test" + i + ".docx", com.spire.doc.FileFormat.Docx);
            }
        }

        // 保存合并后的文档
        document.saveToFile(desPath);
        return true;
    }

    // 取得某一路径下所有的pdf
    private static File[] getSplitFiles(String path) {
        File f = new File(path);
        return f.listFiles();

    }

    public void clearFiles(String workspaceRootPath) {
        File file = new File(workspaceRootPath);
        if (file.exists()) {
            deleteFile(file);
        }
    }

    public void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    deleteFile(value);
                }
            }
        }
        file.delete();
    }
}
