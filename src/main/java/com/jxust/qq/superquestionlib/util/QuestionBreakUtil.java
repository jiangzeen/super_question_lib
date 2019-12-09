package com.jxust.qq.superquestionlib.util;

import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class QuestionBreakUtil {

    /**
     * 调用init初始化word文档为text
     * @param name 文件url/一般为服务器上已经保存文件
     * @return text 解析文档
     * @throws FileNotFoundException 文件没找到
     * @throws IllegalArgumentException 文件解析错误
     */
    public static String init(String name) throws FileNotFoundException, IllegalArgumentException {
        assert name != null;
        File target = new File(name);
        if (!target.exists()) {
            throw new FileNotFoundException();
        }
        String text = readQuestionFile (name);
        if (text == null) {
            throw new IllegalArgumentException();
        }
        return text;
    }


    /**
     * 分解传入题库中的选择题
     * @param filename 文件名
     * @return list
     */
    public static List breakCQ(String filename, QuestionMark mark) throws IllegalArgumentException, FileNotFoundException {
        String text = init(filename);
        if (mark != null) {

        }
        return null;
    }


    /**
     * 使用Apache POI读取docx文件内容
     * @param file
     * @return 文件内容
     */
    public static String readQuestionFile(String file) {
        String text;
        try {
            OPCPackage opcPackage = POIXMLDocument.openPackage(file);
            POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
            text = extractor.getText();
            return text;
        } catch (IOException | XmlException | OpenXML4JException ignored) {

        }
        return null;
    }
}
