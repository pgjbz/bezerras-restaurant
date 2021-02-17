package com.pgbezerra.bezerras.repository;

import java.util.Date;
import java.util.List;

import com.pgbezerra.bezerras.models.dto.ReportDTO;
import com.pgbezerra.bezerras.models.entity.Order;

public interface OrderRepository extends Repository<Order, Long> {
    List<Order> findPendingOrders();
    List<ReportDTO> report(Date initialDate, Date finalDate);
}
