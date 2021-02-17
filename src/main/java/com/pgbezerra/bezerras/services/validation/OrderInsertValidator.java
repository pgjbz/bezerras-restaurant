package com.pgbezerra.bezerras.services.validation;

import com.pgbezerra.bezerras.models.dto.OrderDTO;
import com.pgbezerra.bezerras.models.enums.OrderType;
import com.pgbezerra.bezerras.resources.exception.FieldMessage;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderInsertValidator implements ConstraintValidator<OrderInsert, OrderDTO> {

    private static final Logger LOG = Logger.getLogger(OrderInsertValidator.class);

    @Override
    public void initialize(OrderInsert constraintAnnotation) {

    }

    @Override
    public boolean isValid(OrderDTO order, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        LOG.info("Validate order type");

        if(Objects.isNull(order.getOrderType()))
            list.add(new FieldMessage("orderType", "Order type not be null"));

        if(order.getOrderType() == OrderType.TABLE && order.getTable() <= 0) {
            LOG.error("Order type is table and don't have valid table id");
            list.add(new FieldMessage("table", "Invalid table id"));
        }
        else if(order.getOrderType() == OrderType.DELIVERY){
            LOG.info("Order type is delivery, checking non optional fields");
            List<Method> methods = Arrays.asList(order.getClass().getDeclaredMethods());
            methods = methods.stream().filter(method -> {
                String methodName = method.getName().toLowerCase();
                return method.getReturnType().equals(String.class) && methodName.startsWith("get") && !methodName.contains("complement");
            }).collect(Collectors.toList());
            for(Method method: methods){
                method.setAccessible(true);
                String fieldName = method.getName().replace("get", "").toLowerCase();
                LOG.info(String.format("Checking field %s", fieldName));
                String content;
                try {
                    content = (String) method.invoke(order);
                    if(!StringUtils.hasLength(content)) {
                        LOG.error(String.format("Invalid value in field %s", fieldName));
                        list.add(new FieldMessage(fieldName, String.format("%s not be empty or null", fieldName)));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.error("Error validating fields");
                    return false;
                }
            }

        }

        if(order.getItems().isEmpty())
            list.add(new FieldMessage("items", "Items not be empty"));

        for(FieldMessage fieldMessage: list){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(fieldMessage.getMessage())
                    .addPropertyNode(fieldMessage.getFieldName())
                    .addConstraintViolation();
        }

        return list.isEmpty();
    }
}
