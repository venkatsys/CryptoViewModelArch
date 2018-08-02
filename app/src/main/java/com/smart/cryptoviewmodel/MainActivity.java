package com.smart.cryptoviewmodel;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.smart.cryptoviewmodel.recview.CoinModel;
import com.smart.cryptoviewmodel.recview.Divider;
import com.smart.cryptoviewmodel.recview.MyCryptoAdapter;
import com.smart.cryptoviewmodel.screens.MainScreen;
import com.smart.cryptoviewmodel.viewmodel.CryptoViewModel;
import com.smart.cryptoviewmodel.viewmodel.CryptoViewModelArch;

import java.util.List;

public class MainActivity extends TrackingActivity implements MainScreen{

    private MyCryptoAdapter mAdapter;
    private RecyclerView recView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private CryptoViewModelArch  mCryptoViewModelArch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        mCryptoViewModelArch = ViewModelProviders.of(this).get(CryptoViewModelArch.class);
        mCryptoViewModelArch.bind(this);
        mCryptoViewModelArch.fetchData();
    }

    private void bindViews() {
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        recView = this.findViewById(R.id.recView);
        mAdapter = new MyCryptoAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(lm);
        recView.setAdapter(mAdapter);
        recView.addItemDecoration(new Divider(this));
        setSupportActionBar(toolbar);
    }

    @Override
    public void updateData(List<CoinModel> data) {
        mAdapter.setItems(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setError(String msg) {

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "BEFORE onDestroy() called");
        //mCryptoViewModel.unbind();
        super.onDestroy();
        Log.d(TAG, "AFTER onDestroy() called");
    }
}
