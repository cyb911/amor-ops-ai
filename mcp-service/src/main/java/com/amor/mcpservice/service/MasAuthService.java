package com.amor.mcpservice.service;

import cn.hutool.http.HttpUtil;
import com.amor.mcpservice.utils.OcrUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Service
public class MasAuthService implements ToolService {

    private final String baseUrl = "https://mas.minthgroup.com/api/auth";

    @Tool(description = "获取系统验证码，返回验证码")
    public String getCodeImage(@ToolParam(description = "验证码请求随机uuid") String randomStr) {
        String url = baseUrl + "/code/image?randomStr=" + randomStr;
        try {
            byte[] imageBytes = HttpUtil.downloadBytes(url);
            BufferedImage img = null;
            try (ByteArrayInputStream in = new ByteArrayInputStream(imageBytes)) {
                img = ImageIO.read(in);
            }
            String result = OcrUtil.doOCR(img);
            System.err.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "查询异常";

    }
}
