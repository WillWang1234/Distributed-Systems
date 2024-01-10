package org.example;

public class Stock {

    String Name;
    float price;
    int quantity;

    public Stock(String stockName, float price, int quantity){
        this.Name = stockName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getStockName() {
        return Name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setStockName(String stockName) {
        this.Name = stockName;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}