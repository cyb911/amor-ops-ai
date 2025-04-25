package com.amor.mcpservice.service;

import com.amor.mcpservice.utils.OcrUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class OcrService implements ToolService {

    @Tool(description = "对给定图片路径或 URL 执行 OCR 识别，返回文本内容")
    public String executeOcr(@ToolParam(description = "图片本地路径或网络 URL") String imageUri) {
        try {
            return OcrUtil.doOCR(imageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "识别失败";
    }
}
