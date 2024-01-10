package org.example;

public class Trade {
    private int tradeNumber;
    private String Name;
    private String orderType;
    private int quantity;

    public Trade(int tradeNumber, String stockName, String orderType, int quantity) {
        this.tradeNumber = tradeNumber;
        this.Name = stockName;
        this.orderType = orderType;
        this.quantity = quantity;
    }

    public int getTradeNumber() {
        return tradeNumber;
    }

    public void setTradeNumber(int tradeNumber) {
        this.tradeNumber = tradeNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String stockName) {
        this.Name = stockName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
