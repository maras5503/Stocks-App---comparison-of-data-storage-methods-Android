package com.example.stocksApp;

public class Quote {
    String symbol;
    String change;
    String price;

    public Quote(String symbol, String change, String price){
        this.symbol=symbol;
        this.change=change;
        this.price=price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
