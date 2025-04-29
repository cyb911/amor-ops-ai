package com.amor.mcpservice.po;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 检查任务人员名单
 */
@Data
public class TaskUserEntity {


	/**
	* ID
	*/
	@ToolParam(description = "ID")
    private Long id;


	/**
	* 工厂代码
	*/
	@ToolParam(description = "工厂代码")
    private String site;

	/**
	* 工厂名称
	*/
	@ToolParam(description = "工厂名称")
    private String siteName;

	/**
	 * 点检类型,dds:安全点检,5s:5s点检
	 */
	@ToolParam(description = "点检类型,dds:安全点检,5s:5s点检")
	private String type;

	/**
	 * 成员
	 */
	@ToolParam(description = "成员")
	private String userName;

	/**
	 * 工号
	 */
	@ToolParam(description = "工号")
	private String userId;

	/**
	* 部门编号
	*/
	@ToolParam(description = "部门编号")
    private String department;

	/**
	 * 部门
	 */
	@ToolParam(description = "部门")
	private String departmentName;

	/**
	* 课室编号
	*/
	@ToolParam(description = "课室编号")
    private String classroom;

	/**
	 * 课室
	 */
	@ToolParam(description = "课室")
	private String classroomName;

	/**
	 * 职务编号
	 */
	@ToolParam(description = "职务编号")
	private String jobCode;

	/**
	 * 职务名称
	 */
	@ToolParam(description = "职务名称")
	private String jobName;

	/**
	 * 频次
	 * <p>
	 * F01:一周一次<br/>
	 * F02:一班一次<br/>
	 * </p>
	 */
	@ToolParam(description = "周期频次，F01:一周一次,F02:一班一次,F03:一日一次,F04:两周一次")
	private String frequency;
	/**
	 * 人员角色，0:点检人,1:管理员,2:工厂级审核人
	 */
	@ToolParam(description = "人员角色，0:点检人,1:管理员,2:工厂级审核人")
	private String role;

	/**
	 * 人员任务触发状态，0:正常,1:暂停
	 */
	@ToolParam(description = "人员任务触发状态，0:正常,1:暂停")
	private String taskStatus;

	/**
	 * 来源，01:系统生成，02:手动录入
	 */
	@ToolParam(description = "来源，01:系统生成，02:手动录入")
	private String source;

	/**
	 * "创建时间
	 */
	@ToolParam(description = "创建时间(0时区时间)")
	private String createTime;

	/**
	 * "修改时间
	 */
	@ToolParam(description = "修改时间(0时区时间)")
	private String updateTime;
}
