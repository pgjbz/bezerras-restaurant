package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.models.entity.OrderAddress;
import com.pgbezerra.bezerras.repository.OrderAddressRepository;
import com.pgbezerra.bezerras.services.OrderAddressService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderAddressServiceImpl implements OrderAddressService {

    private static final Logger LOG = Logger.getLogger(OrderAddressServiceImpl.class);

    @Autowired
    private final OrderAddressRepository orderAddressesRepository;

    public OrderAddressServiceImpl(final OrderAddressRepository orderAddressesRepository) {
        this.orderAddressesRepository = orderAddressesRepository;
    }

    @Override
    public OrderAddress insert(OrderAddress orderAddress) {
        orderAddress.setId(null);
        return orderAddressesRepository.insert(orderAddress);
    }

    @Override
    public Boolean update(OrderAddress orderAddress) {
        OrderAddress oldObj = findById(orderAddress.getId());
        updateDate(oldObj, orderAddress);
        Boolean updated = orderAddressesRepository.update(oldObj);
        LOG.info(String.format("OrderAddress %s updated: %s", orderAddress, updated));
        return updated;
    }

    private void updateDate(OrderAddress oldObj, OrderAddress orderAddress) {
        oldObj.setCity(orderAddress.getCity());
        oldObj.setStreet(orderAddress.getStreet());
        oldObj.setClientName(orderAddress.getClientName());
        oldObj.setDistrict(orderAddress.getDistrict());
        oldObj.setState(orderAddress.getState());
        oldObj.setNumber(orderAddress.getNumber());
        oldObj.setComplement(orderAddress.getComplement());
    }

    @Override
    public List<OrderAddress> findAll() {
        List<OrderAddress> orderAddressess = orderAddressesRepository.findAll();
        LOG.info(String.format("%s orderAddressess found", orderAddressess.size()));
        if (!orderAddressess.isEmpty())
            return orderAddressess;
        throw new ResourceNotFoundException("No orderAddressess found");
    }

    @Override
    public OrderAddress findById(Long id) {
        Optional<OrderAddress> orderAddresses = orderAddressesRepository.findById(id);
        LOG.info(String.format("OrderAddress with id %s found: %s", id, orderAddresses.isPresent()));
        return orderAddresses.orElseThrow(() ->
                new ResourceNotFoundException(String.format("No orderAddressess found with id: %s", id)));
    }

    @Override
    public Boolean deleteById(Long id) {
        findById(id);
        Boolean deleted = orderAddressesRepository.deleteById(id);
        LOG.info(String.format("OrderAddress %s deleted: %s", id, deleted));
        return deleted;
    }

}
