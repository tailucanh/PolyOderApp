package com.example.polyOder.model;

import java.util.List;

public class Receipt {
    private String idReceipt;
    private String idTable;
    private String timeOder;
    private Double money;
    private List<String> listIdProduct;
    private List<Integer> listCountProduct;
    private String noteOder;
    private boolean statusOder;

    public Receipt() {
    }


    public Receipt(String idReceipt, String idTable, String timeOder, Double money, List<String> listIdProduct, List<Integer> listCountProduct, String noteOder,boolean statusOder) {
        this.idReceipt = idReceipt;
        this.idTable = idTable;
        this.timeOder = timeOder;
        this.money = money;
        this.listIdProduct = listIdProduct;
        this.listCountProduct = listCountProduct;
        this.noteOder = noteOder;
        this.statusOder = statusOder;
    }



    public String getIdReceipt() {
        return idReceipt;
    }

    public void setIdReceipt(String idReceipt) {
        this.idReceipt = idReceipt;
    }

    public String getIdTable() {
        return idTable;
    }

    public void setIdTable(String idTable) {
        this.idTable = idTable;
    }

    public String getTimeOder() {
        return timeOder;
    }

    public void setTimeOder(String timeOder) {
        this.timeOder = timeOder;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public List<String> getListIdProduct() {
        return listIdProduct;
    }

    public void setListIdProduct(List<String> listIdProduct) {
        this.listIdProduct = listIdProduct;
    }

    public List<Integer> getListCountProduct() {
        return listCountProduct;
    }

    public void setListCountProduct(List<Integer> listCountProduct) {
        this.listCountProduct = listCountProduct;
    }

    public String getNoteOder() {
        return noteOder;
    }

    public void setNoteOder(String noteOder) {
        this.noteOder = noteOder;
    }

    public boolean isStatusOder() {
        return statusOder;
    }

    public void setStatusOder(boolean statusOder) {
        this.statusOder = statusOder;
    }
 }
