package com.example.polyOder.model;

public class DataBarChart {
    private String months;
    private float totalMoney;

    public DataBarChart(String months, float totalMoney) {
        this.months = months;
        this.totalMoney = totalMoney;
    }

    public DataBarChart() {
    }

    public float getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(float totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }
}
