package com.heima.utils.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.xmlgraphics.image.loader.impl.ImageBuffered;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/21 16:34
 */
@Component
@Data
@ConfigurationProperties(prefix = "tezz4j.ocr")
@Slf4j
public class OCRUtil {

    private String path;
    private String language;

    public String scanImage(BufferedImage bufferedImage){
        String result = null;
        try {
            //创建Tesseract对象
            ITesseract tesseract = new Tesseract();
            //设置字体库路径
            tesseract.setDatapath(path);
            //中文识别
            tesseract.setLanguage(language);
            log.info("path:{}", path);
            log.info("language:{}", language);
            //执行ocr识别
            result = tesseract.doOCR(bufferedImage);
            //替换回车和tal键  使结果为一行
            result = result.replaceAll("\\r|\\n", "-").replaceAll(" ", "");
            System.out.println("识别的结果为：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

//    public static void main(String[] args) {
//        File file = new File("C:\\Users\\49744\\Desktop\\textOCR.jpg");
//        path = "D:\\Code\\heima-leadnews\\heima-leadnews-utils\\src\\main\\resources\\tessdata";
//        language = "chi_sim";
//        scanImage(file);
//    }

}
