package com.github.calve.test.part.observer.application.shared;

import java.io.Serializable;

public class PartDto implements Serializable {

    private long id;
    private String name;
    private Integer quantity;
    private boolean required;


    public PartDto() {
    }


    public PartDto(long id, String name, Integer quantity, boolean required) {
        super();
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.required = required;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public Boolean isRequired() {
        return required;
    }


    public void setRequired(boolean required) {
        this.required = required;
    }


    @Override
    public String toString() {
        return "PartDto [id=" + id + ", name=" + name + ", quantity=" + quantity + ", required=" + required + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
        result = prime * result + (required ? 1231 : 1237);
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
        PartDto other = (PartDto) obj;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (quantity == null) {
            if (other.quantity != null)
                return false;
        } else if (!quantity.equals(other.quantity))
            return false;
        if (required != other.required)
            return false;
        return true;
    }


}