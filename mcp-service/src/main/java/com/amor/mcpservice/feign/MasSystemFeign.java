package com.amor.mcpservice.feign;

import com.amor.mcpservice.config.MasFeignConfig;
import com.amor.mcpservice.dto.Page;
import com.amor.mcpservice.dto.R;
import com.amor.mcpservice.po.TaskUserEntity;
import com.amor.mcpservice.query.DynamicQueryPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "masSystemFeign", url = "https://mas.minthgroup.com/api",
        configuration = {MasFeignConfig.class})
public interface MasSystemFeign {
    @GetMapping("/dap/base/common/user/search")
    R<Map<String,Object>> searchUser(@RequestParam(value = "site") String site,@RequestParam(value = "dept") String dept,@RequestParam(value = "keyword")  String keyword);

    @GetMapping("/dap/base/user/page")
    R<Page<TaskUserEntity>> queryTaskUser(@RequestBody DynamicQueryPage dynamicQueryPage);
}
