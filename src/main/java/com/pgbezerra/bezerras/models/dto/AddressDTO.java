package com.pgbezerra.bezerras.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

import java.io.Serializable;

public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 2794844803758995795L;

    private String zipCode;
    private String street;
    private String complement;
    private String district;
    private String state;
    private String city;

    @JsonProperty(value = "zipcode")
    public String getZipCode() {
        return zipCode;
    }

    @JsonProperty(value = "cep")
    public void setZipCode(String zipCode) {
        this.zipCode = StringUtils.hasLength(zipCode) ? zipCode.replaceAll("[^\\d]", "") : "";
    }

    @JsonProperty(value = "street")
    public String getStreet() {
        return street;
    }

    @JsonProperty(value = "logradouro")
    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty(value = "complement")
    public String getComplement() {
        return complement;
    }

    @JsonProperty(value = "complemento")
    public void setComplement(String complement) {
        this.complement = complement;
    }

    @JsonProperty(value = "district")
    public String getDistrict() {
        return district;
    }

    @JsonProperty(value = "bairro")
    public void setDistrict(String district) {
        this.district = district;
    }

    @JsonProperty(value = "state")
    public String getState() {
        return state;
    }

    @JsonProperty(value = "uf")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty(value =  "city")
    public String getCity() {
        return city;
    }

    @JsonProperty(value = "localidade")
    public void setCity(String city) {
        this.city = city;
    }
}
