package com.pgbezerra.bezerras.entities.model;

import java.io.Serializable;

public class OrderAddress implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String street;
	private String number;
	private String complement;
	private String district;
	private String city;
	private String state;
	
	public OrderAddress() {
	}

	public OrderAddress(Long id, String street, String number, String district, String city, String state) {
		this.id = id;
		this.street = street;
		this.number = number;
		this.district = district;
		this.city = city;
		this.state = state;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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
		OrderAddress other = (OrderAddress) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderAddress [id=" + id + ", street=" + street + ", number=" + number + ", district=" + district
				+ ", city=" + city + ", state=" + state + "]";
	}


}
