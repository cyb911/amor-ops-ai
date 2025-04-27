package com.amor.mcpservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class HcmSchedulingResp {
        @ToolParam(description = "工号")
        @JsonProperty("USER_ID")
        private String USER_ID;
        @ToolParam(description = "员工姓名")
        @JsonProperty("TRUENAME")
        private String TRUENAME;
        @ToolParam(description = "排班日期")
        @JsonProperty("PRO_DAATE")
        private String PRO_DAATE;
        @JsonProperty("SHIFT_TYPE")
        @ToolParam(description = "班次类型")
        private String SHIFT_TYPE;
        @ToolParam(description = "工作类型")
        @JsonProperty("WORK_TYPE")
        private String WORK_TYPE;
        @ToolParam(description = "开始时间")
        @JsonProperty("START_TIME")
        private String START_TIME;
        @ToolParam(description = "结束时间")
        @JsonProperty("END_TIME")
        private String END_TIME;

}
