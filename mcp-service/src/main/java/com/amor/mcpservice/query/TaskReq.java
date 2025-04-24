package com.amor.mcpservice.query;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * DDS点检表
 *
 * @author yunbin.chen
 * @date 2024-12-21 16:27:55
 */
@Data
public class TaskReq {

	/**
	 * 员工工号
	 */
	@ToolParam(description = "员工工号")
	private String userId;


	/**
	 * 点检任务状态，0:待办，1：已办
	 */
	@ToolParam(description = "点检任务状态，0:待办，1：已办")
	private String status;

	@ToolParam(description = "关键字模糊匹配", required = false)
	private String keyword;
}
