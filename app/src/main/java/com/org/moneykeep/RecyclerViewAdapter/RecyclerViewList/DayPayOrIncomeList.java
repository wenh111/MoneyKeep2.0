package com.org.moneykeep.RecyclerViewAdapter.RecyclerViewList;

public class DayPayOrIncomeList {
    private int id;
    private String time;
    private String location;
    private String account;
    private String category;
    private String cost;
    private String remark;
    private String objectId;
    private String date;
    private int int_time;
    private double CountIncome;
    private double CountPay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCountIncome() {
        return CountIncome;
    }

    public void setCountIncome(double countIncome) {
        CountIncome = countIncome;
    }

    public double getCountPay() {
        return CountPay;
    }

    public void setCountPay(double countPay) {
        CountPay = countPay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getInt_time() {
        return int_time;
    }

    public void setInt_time(int int_time) {
        this.int_time = int_time;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
