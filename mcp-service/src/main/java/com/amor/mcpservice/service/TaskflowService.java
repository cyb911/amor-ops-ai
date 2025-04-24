package com.amor.mcpservice.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.amor.mcpservice.query.TaskReq;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TaskflowService {

    @Value("${taskflow.auth}")
    private String auth;

    private final String baseUrl = "https://mas.minthgroup.com/api/dap/taskflow/";

    @Tool(description = "查询点检人待办的点检任务")
    public String queryTask(@ToolParam(description = "请求参数") TaskReq req) {

        try {
            String urlString = baseUrl + "/group/page";
            String result= HttpRequest.get(urlString)
                    .header("authorization",auth)
                    .form(JSONUtil.parseObj(JSONUtil.toJsonStr(req)))
                    .timeout(-1)
                    .execute().body();
            System.err.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "查询异常";

    }
}
