package com.smart.cryptoviewmodel.screens;

import com.smart.cryptoviewmodel.recview.CoinModel;

import java.util.List;

public interface MainScreen {
    void updateData(List<CoinModel> data);
    void setError(String msg);
}
