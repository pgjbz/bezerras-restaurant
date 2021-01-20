package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.entities.dto.ReportDTO;
import com.pgbezerra.bezerras.entities.model.Order;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends Repository<Order, Long> {
    List<Order> findPendingOrders();
    List<ReportDTO> report(Date initialDate, Date finalDate);
}
