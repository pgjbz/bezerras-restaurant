package com.pgbezerra.bezerras.entities.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.DayOfWeek;

public class MenuDTO implements Serializable {

    private static final long serialVersionUID = -6300831986771065097L;

    @NotBlank(message = "Menu name not be empty or null")
    private String name;
    @NotNull(message = "Day of week not be empty")
    private DayOfWeek dayOfWeek;

    public MenuDTO() {
    }

    public MenuDTO(String name, DayOfWeek dayOfWeek) {
        this.name = name;
        this.dayOfWeek = dayOfWeek;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
