package com.example.stocksApp;

public class Stockquotes {
    int _id;
    String _symbol;
    String _price;
    String _change;

    public Stockquotes(){};

    public Stockquotes(int id, String symbol, String price, String change){
        this._id=id;
        this._symbol=symbol;
        this._price=price;
        this._change=change;
    }
    public Stockquotes(String symbol, String price, String change){
        this._symbol=symbol;
        this._price=price;
        this._change=change;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_symbol() {
        return _symbol;
    }

    public void set_symbol(String _symbol) {
        this._symbol = _symbol;
    }

    public String get_price() {
        return _price;
    }

    public void set_price(String _price) {
        this._price = _price;
    }

    public String get_change() {
        return _change;
    }

    public void set_change(String _change) {
        this._change = _change;
    }
}
