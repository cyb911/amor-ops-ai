package com.amor.mcpservice.dto;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

@Data
public class Page<T> {
    @ToolParam(description = "总数")
    private int total;

    @ToolParam(description = "返回行数")
    private int size;

    @ToolParam(description = "当前页码")
    private int current;

    @ToolParam(description = "总页数")
    private int pages;

    @ToolParam(description = "返回的数据集")
    private List<T> records;
}
