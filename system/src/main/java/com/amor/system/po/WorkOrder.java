package com.amor.system.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "work_order")
public class WorkOrder {
    /**
     * 工单编号
     */
    @Id
    private String orderNo;
    /**
     * 工单标题
     */
    private String title;
    /**
     * 工单描述描述
     */
    private String description;
    /**
     * 工单类型（例如：故障、维护、咨询等）
     */
    private String type;
    /**
     * 工单状态（例如：待处理、处理中、已完成、已关闭等）
     */
    private String status;
    /**
     * 优先级,0:低,1:中,2高
     */
    private Integer priority;

    /**
     * 创建人姓名
     */
    private String createdByName;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 指派给用户ID
     */
    private Long assignedTo;
    /**
     * 指派给用户姓名
     */
    private String assignedToName;
    /**
     * 指派时间
     */
    private LocalDateTime assignedTime;
    /**
     * 完成时间
     */
    private LocalDateTime completedTime;
    /**
     *  处理备注
     */
    private String remarks;
}
