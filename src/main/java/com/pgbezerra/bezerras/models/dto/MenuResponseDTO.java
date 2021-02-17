package com.pgbezerra.bezerras.models.dto;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class MenuResponseDTO implements Serializable {

    private static final long serialVersionUID = -5342003626886412842L;

    private String name;
    private DayOfWeek dayOfWeek;
    private final List<ProductDTO> items = new ArrayList<>();

    public MenuResponseDTO() {
    }

    public MenuResponseDTO(String name, DayOfWeek dayOfWeek) {
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

    public List<ProductDTO> getItems() {
        return items;
    }

}
