package com.zisantolunay.happybirthday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DemoActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private static final String TAG = "TAG_DemoActivity";
    private LinearLayout loadingLayout;

    private BillingClient billingClient;
    private String uid,name,surname,email;
    private WebView webView;
    private String orderId = "null";
    private String orderJson = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        AdManager.getInstance(getApplicationContext()).showInterstitialAd();

        loadingLayout = findViewById(R.id.loading_layout_demo);
        showLoadingLayout(true);

        Button button = findViewById(R.id.button);
        Button editButton = findViewById(R.id.button2);
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        SharedPreferences preferences = getSharedPreferences("com.zisantolunay.happybirthday.SHARED", MODE_PRIVATE);
        uid = preferences.getString("uid",null);

        if(uid == null){
            Toast.makeText(DemoActivity.this,getString(R.string.please_try_again_still),Toast.LENGTH_SHORT).show();
            Log.d(TAG, "uid is null");
            finish();
        }


        String loadUrl = "https://tolunaykan.com/HappyBirthday/user-" + uid;

        webView.loadUrl(loadUrl);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d(TAG, "Webview error =" + error.getErrorCode());
                if(error.getErrorCode() == ERROR_CONNECT){
                    Log.d(TAG,"Webview hata " + error.getErrorCode());
                    Toast.makeText(DemoActivity.this, R.string.please_check_internet,Toast.LENGTH_LONG).show();
                    finish();
                }
                showLoadingLayout(false);
            }

            public void onPageFinished(WebView view, String url) {
                showLoadingLayout(false);
            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showBottomSheet(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DemoActivity.this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_layout, (RelativeLayout) findViewById(R.id.bottomSheetContainer));
        final EditText nameET = view.findViewById(R.id.editText1_bottom_sheet);
        final EditText surnameET = view.findViewById(R.id.editText2_bottom_sheet);
        final EditText emailET = view.findViewById(R.id.editText3_bottom_sheet);
        Button button = view.findViewById(R.id.button_bottom_sheet);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameET.getText().toString().trim();
                surname = surnameET.getText().toString().trim();
                email = emailET.getText().toString().trim();


                if(name.isEmpty()){
                    nameET.setError(getString(R.string.please_enter_name));
                    return;
                }

                if(surname.isEmpty()){
                    surnameET.setError(getString(R.string.please_enter_surname));
                    return;
                }

                if(email.isEmpty()){
                    emailET.setError(getString(R.string.please_enter_mail));
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailET.setError(getString(R.string.please_enter_valid_mail));
                    return;
                }
                showLoadingLayout(true);
                connectGooglePlay();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();



    }

    private void showLoadingLayout(boolean show){
        if(show){
            loadingLayout.setVisibility(View.VISIBLE);
        }else{
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void showLinkLayout(final String uid){
        Dialog dialog = new Dialog(DemoActivity.this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.show_link_layout, (LinearLayout) findViewById(R.id.linkLayoutContainer));
        dialog.setContentView(view);

        TextView textView = view.findViewById(R.id.textView6);
        Button copyButton = view.findViewById(R.id.button3);
        textView.setText("https://tolunaykan.com/HappyBirthday/user-" + uid);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("Url", "https://tolunaykan.com/HappyBirthday/user-" + uid);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(data);
                    Toast.makeText(DemoActivity.this, R.string.url_copied,Toast.LENGTH_SHORT).show();
                }
            }
        });



        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent mainActivityIntent = new Intent(DemoActivity.this, MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("com.zisantolunay.happybirthday.SHARED", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> orders = sharedPreferences.getStringSet("orders",null);
        Set<String> newOrders;
        if(orders == null){
            newOrders = new HashSet<>();
        }else{
            newOrders = orders;
        }
        newOrders.add(uid);

        editor.putStringSet("orders",newOrders);
        editor.putString("uid", String.valueOf(UUID.randomUUID()));
        editor.apply();

        dialog.show();

    }

    private void createWebsite(){
        showLoadingLayout(true);
        RequestQueue queue = Volley.newRequestQueue(DemoActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://tolunaykan.com/HappyBirthday/addNew.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showLoadingLayout(false);
                showLinkLayout(uid);
                Log.d(TAG,"Response = "+ response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadingLayout(false);
                showLinkLayout(uid);
                Log.d(TAG, "Ödeme sonrası POST yapılamadı" + error.getLocalizedMessage());
                Toast.makeText(DemoActivity.this, R.string.contact_dev_get_mail,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param = new HashMap<>();
                param.put("uid",uid);
                param.put("name",name);
                param.put("surname",surname);
                param.put("email",email);
                param.put("orderId",orderId);
                param.put("orderJson",orderJson);
                return param;
            }
        };
        queue.add(request);
    }

    private void connectGooglePlay(){


        billingClient = BillingClient.newBuilder(DemoActivity.this).enablePendingPurchases().setListener(DemoActivity.this).build();
        Log.d(TAG, "Feature not supported " + billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).getResponseCode());
        Log.d(TAG, "Feature not supported " + billingClient.isFeatureSupported(BillingClient.FeatureType.IN_APP_ITEMS_ON_VR).getResponseCode());


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                Log.d(TAG, "response code= " + billingResult.getResponseCode());
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG,"Google playle bağlantı kuruldu");
                    getSkuDetails();
                }else if(billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.BILLING_UNAVAILABLE){
                    Log.d(TAG,"Billing unavailable");
                    Toast.makeText(DemoActivity.this, R.string.billing_not_available,Toast.LENGTH_LONG).show();
                    showLoadingLayout(false);
                }else{
                    Log.d(TAG,"Billing Other error" + billingResult.getResponseCode());
                    Toast.makeText(DemoActivity.this, R.string.there_is_mistake,Toast.LENGTH_LONG).show();
                    showLoadingLayout(false);
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(DemoActivity.this, R.string.there_is_error_try_again,Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Google playle bağlantı kurulamadı");

                showLoadingLayout(false);
                return;
            }

        });
    }

    private void getSkuDetails() {
        List<String> skuList = new ArrayList<>();
        skuList.add("create_website");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        sendPurchaseRequest(skuDetails);
                    }
                }else{
                    Log.d(TAG,"Sku bilgileri alınamadı");
                    showLoadingLayout(false);
                    Toast.makeText(DemoActivity.this, getString(R.string.there_is_error_try_again),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(skuDetailsList.isEmpty()){
                    Log.d(TAG,"Skulist boş");
                    Toast.makeText(DemoActivity.this, getString(R.string.there_is_error_try_again),Toast.LENGTH_SHORT).show();
                    showLoadingLayout(false);
                }
            }
        });

    }

    private void sendPurchaseRequest(SkuDetails skuDetails){
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        billingClient.launchBillingFlow(DemoActivity.this, flowParams);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            for (Purchase purchase : list) {
                handlePurchase(purchase);
            }

            if(list.isEmpty()){
                showLoadingLayout(false);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(DemoActivity.this, R.string.purchase_cancelled,Toast.LENGTH_SHORT).show();
            showLoadingLayout(false);
        } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            Log.d(TAG,"onpurchaseupdated item zaten var");
            Toast.makeText(DemoActivity.this, getString(R.string.there_is_error_try_again),Toast.LENGTH_SHORT).show();
        } else{
            Log.d(TAG,"onpurchaseupdated some error");
            Toast.makeText(DemoActivity.this, getString(R.string.there_is_error_try_again),Toast.LENGTH_SHORT).show();
            showLoadingLayout(false);
        }
    }


    private void handlePurchase(Purchase purchase) {
        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
            // Grant entitlement to the user.

            Log.d(TAG, "JSON= " + purchase.getOriginalJson());
            Log.d(TAG,"Order ID= " + purchase.getOrderId());

            orderId = purchase.getOrderId();
            orderJson = purchase.getOriginalJson();


            acknowledgePurchase(purchase);

        }else if(purchase.getPurchaseState() == Purchase.PurchaseState.PENDING){
            Toast.makeText(DemoActivity.this, R.string.payment_pending,Toast.LENGTH_SHORT).show();
            showLoadingLayout(false);
        }else{
            Toast.makeText(DemoActivity.this, getString(R.string.there_is_error_try_again),Toast.LENGTH_SHORT).show();
            showLoadingLayout(false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.resumeTimers();
        webView.onResume();
    }


    @Override
    protected void onDestroy() {
        webView.destroy();
        webView = null;
        super.onDestroy();
    }


    private void acknowledgePurchase(Purchase purchase){
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String s) {
                Log.d(TAG,"Consume edildi sipariş sonrası");
                createWebsite();
            }
        });
    }

}

