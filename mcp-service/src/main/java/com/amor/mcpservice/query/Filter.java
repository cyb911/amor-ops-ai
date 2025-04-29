package com.amor.mcpservice.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class Filter {
    /**
     * 逻辑条件，当（if），且(and)，或(or)
     */
    @ToolParam(description = "逻辑条件，当（if），且(and)，或(or)",required = false)
    private String logic;
    /**
     * 查询字段
     */
    @ToolParam(description = "查询字段,查询实体类属性名称，如：userName",required = false)
    private String field;
    /**
     * 条件
     */
    @ToolParam(description = "条件(=,>,<,>=,<=,like,between)",required = false)
    private String operator;
    /**
     * 值
     */
    @ToolParam(description = "查询值,单值或包含两个元素的数组",required = false)
    private Object value;
}
