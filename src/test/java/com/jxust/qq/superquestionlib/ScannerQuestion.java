package com.jxust.qq.superquestionlib;

import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScannerQuestion {

    @Test
    public void test_scanner() {
        String text = getDocxText("english.docx");
        assert text != null;
        List<String> questions = new ArrayList<>();
        Pattern p = Pattern.compile("(\\d[\\s\\S]*?)[$]");
        Matcher m = p.matcher(text);
        while (m.find()) {
            questions.add(m.group());
        }
        System.out.println("questions.size() = " + questions.size());
        System.out.println(questions);
        //去掉所有的空行
        // text = text.replaceAll("(?m)^\\s*$(\\n|\\r\\n)", "");
        // System.out.println("text = \n" + text);


    }

    @Test
    public void test_english() {
        String etext = getDocxText("english.docx");
        // System.out.println(etext);
        Pattern ep = Pattern.compile("(\\d+([.])[\\s\\S]*?)([.]\\s\\S*?)");
        /*匹配选项:1.匹配[A-D] */
        Pattern ap = Pattern.compile("([ABCD]([.])[\\s\\S]*?)(?=[\\s*?]([ABCD\\d+\\s*]))");
        assert etext != null;
        Matcher em = ep.matcher(etext);
        Matcher am = ap.matcher(etext);
        List<String> englishs = new ArrayList<>();
        List<String> englisha = new ArrayList<>();
        while (em.find()) {
            englishs.add(em.group());
        }
        while (am.find()) {
            englisha.add(am.group());
        }
        System.out.println("englishs.size() = " + englishs.size());
        englishs.forEach(en -> System.out.println((englishs.indexOf(en) +1) + ":" + en));
        System.out.println("============================================================");
        System.out.println("englisha.size() = " + englisha.size());
        englisha.forEach(en -> System.out.println((englisha.indexOf(en) +1) + ":" + en));
    }

    @Test
    public void test_reg() {
        String s = "The fat cAt sat on the mat.";
        Pattern p = Pattern.compile(".(at)");
        Matcher m = p.matcher(s);
        List<String> la = new ArrayList<>();
        while (m.find()) {
            la.add(m.group());
        }
        System.out.println(la);
    }


    private String getDocxText(String file) {
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
