package com.jxust.qq.superquestionlib.util.parse.fileparse;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.*;

public class DocxParse implements Parse {


    /**
     * 读取Docx文件中的内容
     * @param file 文件
     * @return 读取出的内容
     * @throws IOException 读取文件内容失败抛出异常
     * @throws FileNotFoundException 文件不存在抛出异常
     */
    @Override
    public String readQuestionFile(String file) throws IOException {
        if (!new File(file).exists()) {
            throw new FileNotFoundException();
        }
        String text = "";
        try {
            if (file.endsWith(".doc")) {
                InputStream is = new FileInputStream(new File(file));
                WordExtractor ex = new WordExtractor(is);
                text = ex.getText();

            } else if (file.endsWith(".docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(file);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                text = extractor.getText();
            }
            return text;
        } catch (Exception e) {
            throw new IOException();
        }
    }
}
