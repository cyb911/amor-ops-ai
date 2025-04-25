package com.amor.mcpservice.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class OcrService implements ToolService {

    private final ITesseract tesseract = new Tesseract();


    @Tool(description = "对给定图片路径或 URL 执行 OCR 识别，返回文本内容")
    public String executeOcr(@ToolParam(description = "图片本地路径或网络 URL") String imageUri) {
        try {
            return doOCR(imageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "识别失败";
    }

    private String doOCR(String imageUri) throws Exception {
        BufferedImage img;
        if (imageUri.startsWith("http")) {
            img = ImageIO.read(new URL(imageUri));
        } else {
            img = ImageIO.read(new File(imageUri));
        }
        return tesseract.doOCR(img);
    }
}
