package com.example.polyOder.model;

import java.io.Serializable;

public class TypeProduct implements Serializable {
    private String id;
    private String nameType;
    private  boolean isHidden;

    public TypeProduct() {
    }

    public TypeProduct(String id, String nameType) {
        this.id = id;
        this.nameType = nameType;
    }

    public TypeProduct(String id, String nameType, boolean isHidden) {
        this.id = id;
        this.nameType = nameType;
        this.isHidden = isHidden;
    }

    public TypeProduct(String name_type) {
        this.nameType = name_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
