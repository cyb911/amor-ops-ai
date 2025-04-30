package com.amor.system.repository;

import com.amor.system.po.WorkOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 工单
 */
public interface WorkOrderRepository extends MongoRepository<WorkOrder,String> {

}
