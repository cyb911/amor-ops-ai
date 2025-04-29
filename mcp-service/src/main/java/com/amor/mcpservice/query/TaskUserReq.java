package com.amor.mcpservice.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;


@Data
public class TaskUserReq {
    /**
     * 每页显示条数，默认 10
     */
    @ToolParam(description = "每页显示条数，默认 999",required = false)
    protected long size = 999;

    /**
     * 当前页
     */
    @ToolParam(description = "当前页，默认 1",required = false)
    protected long current = 1;

    /**
     * 点检类型,dds:安全点检,5s:5s点检
     */
    @ToolParam(description = "点检类型,dds:安全点检,5s:5s点检")
    private String type;

}
