package com.zisantolunay.happybirthday;

import android.content.Context;
import android.util.Log;
import android.widget.Adapter;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdManager {
    private static AdManager adManager;
    private Context context;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "TAG_AdManager";

    private AdManager(Context context){
        this.context = context;
    }

    public static AdManager getInstance(Context context){
        if(adManager == null){
            adManager = new AdManager(context);
        }
        return adManager;
    }

    public void createInterstitialAd(){
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-8501566775769359/3748077393");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "Geçiş reklamı yüklenemedi = " + errorCode);
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                super.onAdClosed();
            }
        });
    }

    public void showInterstitialAd(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet.");
        }
    }


}
