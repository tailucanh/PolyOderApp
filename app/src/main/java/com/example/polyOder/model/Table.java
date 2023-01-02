package com.example.polyOder.model;

import java.io.Serializable;

public class Table implements Serializable {
    private String id_table;
    private String name_table;
    private String status = "false";
    private  boolean isHidden;

    public Table() {
    }

    public Table(String id_table, String name_table, String status) {
        this.id_table = id_table;
        this.name_table = name_table;
        this.status = status;
    }

    public Table(String id_table, String name_table, String status, boolean isHidden) {
        this.id_table = id_table;
        this.name_table = name_table;
        this.status = status;
        this.isHidden = isHidden;
    }



    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getId_table() {
        return id_table;
    }

    public void setId_table(String id_table) {
        this.id_table = id_table;
    }

    public String getName_table() {
        return name_table;
    }

    public void setName_table(String name_table) {
        this.name_table = name_table;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
