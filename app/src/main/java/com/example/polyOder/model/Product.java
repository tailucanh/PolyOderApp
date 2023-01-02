package com.example.polyOder.model;

public class Product {
    private String id ;
    private String nameProduct;
    private double price;
    private String describe;
    private TypeProduct typeProduct;
    private String imgProduct;
    private String note;
    boolean isSelected = false;
    int isClick;
    boolean isHidden;


    public Product() {
    }


    public Product(String id, String nameProduct, String describe, TypeProduct typeProduct, double price, String note) {
        this.id = id;
        this.nameProduct = nameProduct;
        this.price = price;
        this.note = note;
        this.describe = describe;
        this.typeProduct = typeProduct;
    }

    public Product(String id, String nameProduct, String describe, TypeProduct typeProduct, double price, String note, int isClick) {
        this.id = id;
        this.nameProduct = nameProduct;
        this.price = price;
        this.note = note;
        this.describe = describe;
        this.typeProduct = typeProduct;
        this.isClick = isClick;
    }
    public Product(String id, String nameProduct, String describe, TypeProduct typeProduct, double price, String note, boolean isHidden) {
        this.id = id;
        this.nameProduct = nameProduct;
        this.price = price;
        this.note = note;
        this.describe = describe;
        this.typeProduct = typeProduct;
        this.isHidden = isHidden;
    }

    public int getIsClick() {
        return isClick;
    }

    public void setIsClick(int isClick) {
        this.isClick = isClick;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public TypeProduct getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(TypeProduct typeProduct) {
        this.typeProduct = typeProduct;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getImgProduct() {
        return imgProduct;
    }

    public void setImgProduct(String imgProduct) {
        this.imgProduct = imgProduct;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
