package com.pgbezerra.bezerras.models.entity;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

public class Menu implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private DayOfWeek dayOfWeek;
	private final Set<MenuItem> items = new HashSet<>();

	public Menu() {
	}

	public Menu(Long id, String name, DayOfWeek dayOfWeek) {
		this.id = id;
		this.name = name;
		this.dayOfWeek = dayOfWeek;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = DayOfWeek.of(dayOfWeek);
	}

	public Set<MenuItem> getItems() {
		return items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Menu other = (Menu) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Menu [id=" + id + ", name=" + name + ", dayOfWeek=" + dayOfWeek + "]";
	}
	

}
