package com.amor.mcpservice.service;

import cn.hutool.core.util.StrUtil;
import com.amor.mcpservice.dto.Page;
import com.amor.mcpservice.dto.R;
import com.amor.mcpservice.feign.MasSystemFeign;
import com.amor.mcpservice.po.TaskUserEntity;
import com.amor.mcpservice.query.DynamicQueryPage;
import com.amor.mcpservice.query.Filter;
import com.amor.mcpservice.query.TaskUserReq;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MasService implements ToolService {

    private final MasSystemFeign masSystemFeign;

    @Tool(description = "查询点检任务人员名单")
    public Page<TaskUserEntity> queryTaskUser(@ToolParam(description = "点检任务人员名单分页查询参数") TaskUserReq req) {

        try {
            DynamicQueryPage dynamicQueryPage = new DynamicQueryPage();
            dynamicQueryPage.setSize(req.getSize());
            dynamicQueryPage.setCurrent(req.getCurrent());
            List<Filter> filters = new ArrayList<>();
            if(StrUtil.isNotBlank(req.getType())) {
                Filter filter = new Filter();
                filter.setLogic("and");
                filter.setField("type");
                filter.setOperator("=");
                filter.setValue(req.getType());
                filters.add(filter);
            }

            dynamicQueryPage.setFilters(filters);
            R<Page<TaskUserEntity>> result = masSystemFeign.queryTaskUser(dynamicQueryPage);
            System.err.println("查询点检任务人员名单");
            return result.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
