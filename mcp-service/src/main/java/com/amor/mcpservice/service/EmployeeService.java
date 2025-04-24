package com.amor.mcpservice.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class EmployeeService implements ToolService {
    @Tool(description = "根据员工工号获取员工信息")
    public String getSimpleDetailsBatch(@ToolParam(description = "工号") List<String> employeeCodes) {

        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("employeeCodes", employeeCodes);
            String result= HttpUtil.post("https://mas.minthgroup.com/api/ees/empInfo/simpleDetail/batch", JSONUtil.toJsonStr(paramMap));
            System.err.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "查询异常";

    }
}
