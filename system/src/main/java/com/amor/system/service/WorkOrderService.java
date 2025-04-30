package com.amor.system.service;

import com.amor.system.po.WorkOrder;
import com.amor.system.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    @Transactional(rollbackFor = Exception.class)
    public void create(WorkOrder workOrder) {
        workOrderRepository.save(workOrder);
        if(true) {
            throw new RuntimeException("1");
        }
        workOrderRepository.save(workOrder);
    }
}
