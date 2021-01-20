package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.dto.ReportDTO;
import com.pgbezerra.bezerras.entities.model.Order;

import java.util.Date;
import java.util.List;

public interface OrderService extends Service<Order, Long> {
    Boolean updateStatus(Order order);
    List<Order> findPendingOrders();
    List<ReportDTO> findReport(Date initialDate, Date finalDate);
}
