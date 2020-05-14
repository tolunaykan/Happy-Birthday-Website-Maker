package com.zisantolunay.happybirthday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private BillingClient billingClient;
    private static final String TAG = "TAG_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        List<String> testDeviceIds = Arrays.asList(AdRequest.DEVICE_ID_EMULATOR,"63E8E92876795D3EAD2DCDB8A6D373CF");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);


        billingClient = BillingClient.newBuilder(MainActivity.this).enablePendingPurchases().setListener(MainActivity.this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Log.d(TAG,"Playa bağlandım");
                Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
                if(purchasesResult.getPurchasesList() != null && !purchasesResult.getPurchasesList().isEmpty()){
                    Log.d(TAG,"PurchasesList null değil");
                    for ( Purchase purchase : purchasesResult.getPurchasesList()){
                        consumePurchase(purchase);
                    }
                }else{
                    Log.d(TAG,"PurchasesList boş");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });

        Button myWebsites = findViewById(R.id.button4);
        myWebsites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myWebsitesIntent = new Intent(MainActivity.this, MyWebsitesActivity.class);
                startActivity(myWebsitesIntent);
            }
        });

        LinearLayout openVideo = findViewById(R.id.playLayout);
        openVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v=xqAMKL0wF94"));
                startActivity(intent);
            }
        });






        final LottieAnimationView startButton = findViewById(R.id.lottieStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createActivity = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(createActivity);
            }
        });
    }

    private void consumePurchase(Purchase purchase){
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String s) {
                Log.d(TAG,"consume edildi");
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}
