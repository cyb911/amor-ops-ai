package com.amor.mcpservice.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.amor.mcpservice.dto.R;
import com.amor.mcpservice.feign.MasSystemFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeService implements ToolService {

    private final MasSystemFeign masSystemFeign;

    @Tool(description = "根据员工工号获取员工信息")
    public String getUserByEmployeeCode(@ToolParam(description = "工号") List<String> employeeCodes) {

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

    @Tool(description = "查询员工信息")
    public R<Map<String, Object>> queryUser(@ToolParam(description = "工厂编号",required = false) String site,
                            @ToolParam(description = "关键字(工号或姓名)模糊查询",required = false) String keyword) {

        try {
            R<Map<String, Object>> result = masSystemFeign.searchUser(site,null,keyword);
            System.err.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.failed("查询异常");

    }
}
