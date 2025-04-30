package com.amor.system.controller;

import com.amor.system.po.WorkOrder;
import com.amor.system.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping("/test")
    public void test(@RequestBody WorkOrder workOrder) {
        workOrderService.create(workOrder);
    }
}
