package com.pgbezerra.bezerras.services;

import java.util.Date;
import java.util.List;

import com.pgbezerra.bezerras.models.dto.ReportDTO;
import com.pgbezerra.bezerras.models.entity.Order;

public interface OrderService extends Service<Order, Long> {
    Boolean updateStatus(Order order);
    List<Order> findPendingOrders();
    List<ReportDTO> findReport(Date initialDate, Date finalDate);
}
