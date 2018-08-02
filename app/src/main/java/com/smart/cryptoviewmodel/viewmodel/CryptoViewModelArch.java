package com.smart.cryptoviewmodel.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.cryptoviewmodel.MainActivity;
import com.smart.cryptoviewmodel.entities.CryptoCoinEntity;
import com.smart.cryptoviewmodel.recview.CoinModel;
import com.smart.cryptoviewmodel.screens.MainScreen;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CryptoViewModelArch  extends ViewModel{
    private static final String TAG = CryptoViewModelArch.class.getSimpleName();
    private RequestQueue mQueue;
    public final String CRYPTO_URL_PATH = "https://files.coinmarketcap.com/static/img/coins/128x128/%s.png";
    public final String ENDPOINT_FETCH_CRYPTO_DATA = "https://api.coinmarketcap.com/v1/ticker/?limit=3";
    private String DATA_FILE_NAME = "crypto.data";
    private final ObjectMapper mObjMapper = new ObjectMapper();
    private MainScreen mView;
    private Context mAppContext;

    public CryptoViewModelArch() {
        Log.d(TAG, "CryptoViewModelArch() Constructor called");
    }

    public void bind(MainActivity view){
        this.mView = view;
        this.mAppContext = view.getApplicationContext();
        if(mQueue == null) {
            mQueue = Volley.newRequestQueue(this.mAppContext);
        }
    }

    public void unbind(){
        this.mView = null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared() called");
    }

    public void fetchData() {
        Log.d(TAG, "fetchData() called" + "fetchData");
        final JsonArrayRequest jsonObjReq = new JsonArrayRequest(ENDPOINT_FETCH_CRYPTO_DATA,
                response -> {
                    writeDataToInternalStorage(response);
                    //Log.d(TAG, "fetchData() called" + response);
                    ArrayList<CryptoCoinEntity> data = parseJSON(response.toString());
                    Log.d(TAG, "fetchData() called" + data);
                    new EntityToModelMapperTask().execute(data);
                },
                error ->{
                    try {
                        JSONArray data =  readDataFromStorage();
                        ArrayList<CryptoCoinEntity> entities = parseJSON(data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        mQueue.add(jsonObjReq);
    }


    public ArrayList<CryptoCoinEntity> parseJSON(String jsonStr){
        ArrayList<CryptoCoinEntity> data = null;

        try{
            data = mObjMapper.readValue(jsonStr, new TypeReference<ArrayList<CryptoCoinEntity>>() {
            });
        }catch (Exception e){
        }

        return data;
    }

    private void writeDataToInternalStorage(JSONArray data){
        FileOutputStream fos = null;

        try{
            fos = mAppContext.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        try{
            fos.write(data.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray readDataFromStorage() throws JSONException{
        FileInputStream fis = null;
        try{
            fis = mAppContext.openFileInput(DATA_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return new JSONArray(sb.toString());
    }

    private class EntityToModelMapperTask extends AsyncTask<List<CryptoCoinEntity>,Void,List<CoinModel>> {
        @Override
        protected List<CoinModel> doInBackground(List<CryptoCoinEntity>... data) {
            final ArrayList<CoinModel> listData = new ArrayList<>();
            CryptoCoinEntity entity;
            for(int i = 0 ; i < data[0].size(); i++){
                entity = data[0].get(i);
                listData.add(new CoinModel(entity.getName() , entity.getSymbol() , String.format(CRYPTO_URL_PATH , entity.getId()) , entity.getPriceUsd() , entity.getPercentChange24h()));
            }
            return listData;
        }

        @Override
        protected void onPostExecute(List<CoinModel> data) {
            Log.d(TAG, "onPostExecute() called with: data = [" + data + "]");
            if(mView!=null){
                mView.updateData(data);
            }
            super.onPostExecute(data);
        }
    }
}
