package com.amor.mcpservice.utils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class OcrUtil {
    private final static ITesseract tesseract;

    static  {
        tesseract = new Tesseract();
        try {
            String tessDataDir = new File(
                    Objects.requireNonNull(OcrUtil.class.getClassLoader()
                                    .getResource("tessdata"))
                            .toURI()
            ).getParent();
            tesseract.setDatapath(tessDataDir + "/tessdata");
            tesseract.setLanguage("eng");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String doOCR(String imageUri) throws Exception {
        BufferedImage img;
        if (imageUri.startsWith("http")) {
            img = ImageIO.read(new URL(imageUri));
        } else {
            img = ImageIO.read(new File(imageUri));
        }
        return tesseract.doOCR(img);
    }

    public static String doOCR(BufferedImage img) throws Exception {
        return tesseract.doOCR(img);
    }
}
