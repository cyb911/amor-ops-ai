package com.amor.mcpservice.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * 动态查询条件参数
 *
 * @author yunbin.chen
 * @date 2025-01-14 16:16
 */
@Data
public class DynamicQueryPage  {
    /**
     * 每页显示条数，默认 10
     */
    @ToolParam(description = "每页显示条数，默认 10",required = false)
    protected long size = 10;

    /**
     * 当前页
     */
    @ToolParam(description = "当前页，默认 1",required = false)
    protected long current = 1;

    @ToolParam(description = "筛选条件",required = false)
    private List<Filter> filters;
}
