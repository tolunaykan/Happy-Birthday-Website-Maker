package com.zisantolunay.happybirthday;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateActivity extends AppCompatActivity {

    private ViewPagerAdapter adapter;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGE_PICK_GALLERY_REQUEST = 300;
    String[] storagePermissions;
    private static String TAG = "TAG_CreaetActivity";
    private LinearLayout indicatorsLayout;
    private ViewPager2 viewPager2;
    private Button prevButton;
    public static LinearLayout loadingLayout;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdManager.getInstance(getApplicationContext()).createInterstitialAd();



        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "Banner yüklendi");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "Banner yüklenemedi = " + errorCode);
            }

        });

        storagePermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        indicatorsLayout = findViewById(R.id.indicators_layout);


        loadingLayout = findViewById(R.id.loading_layout);
        showLoadingLayout(false);

        setupItem();

        viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setAdapter(adapter);

        setupIndicators();
        setCurrentIndicator(0);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        viewPager2.setUserInputEnabled(false);



        Button nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        prevButton.setVisibility(View.INVISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager2.getCurrentItem() == adapter.getItemCount()-1){
                    showLoadingLayout(true);
                    createWebsite();
                }else {
                    nextCheck();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevCheck();
            }
        });



    }



    public static void showLoadingLayout(boolean show){
        if(show){
            loadingLayout.setVisibility(View.VISIBLE);
        }else{
            loadingLayout.setVisibility(View.GONE);
        }
    }

    public void prevCheck(){
        if(viewPager2.getCurrentItem() > 0){
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()-1);
        }

        if(viewPager2.getCurrentItem() == 0){
            prevButton.setVisibility(View.INVISIBLE);
        }
    }

    public void nextCheck(){
        showLoadingLayout(true);
        Options options = Options.getInstance();
        if(viewPager2.getCurrentItem() < 9){
            View view = viewPager2.findViewWithTag("EDITBUTTONTAG");
            if(view != null){
                EditText editText = view.findViewById(R.id.editText_editButon);
                if(editText.getText().toString().trim().isEmpty()){
                    editText.setError(getString(R.string.here));
                    Toast.makeText(CreateActivity.this, R.string.set_btn_name,Toast.LENGTH_SHORT).show();
                    showLoadingLayout(false);
                    return;
                }else{
                    options.setButtonEditOption(editText.getText().toString().trim(),viewPager2.getCurrentItem());
                }
            }
        }else if(viewPager2.getCurrentItem() == 9){
            if(options.messages.isEmpty()){
                Toast.makeText(CreateActivity.this, R.string.add_atleast_one_message,Toast.LENGTH_SHORT).show();
                showLoadingLayout(false);
                return;
            }
        }else if(viewPager2.getCurrentItem() == 10){
            if(options.getImageBase64() == null){
                Toast.makeText(CreateActivity.this, R.string.didnt_set_background,Toast.LENGTH_SHORT).show();
            }
        }

        prevButton.setVisibility(View.VISIBLE);
        if(viewPager2.getCurrentItem() < adapter.getItemCount()-1){
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
        showLoadingLayout(false);
    }



    private void createWebsite(){
        SharedPreferences preferences = getSharedPreferences("com.zisantolunay.happybirthday.SHARED", MODE_PRIVATE);
        String uid = preferences.getString("uid",null);
        if(uid == null){
            uid = String.valueOf(UUID.randomUUID());
            preferences.edit().putString("uid",uid).commit();
        }
        String data = null;
        try {
            data = Options.getInstance().getAsJson();
        } catch (Exception e) {
            Log.d(TAG,"Ayarlar JSON'a çevrilemedi" + e.getLocalizedMessage());
            Toast.makeText(CreateActivity.this, R.string.please_try_again,Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(CreateActivity.this);

        final String finalData = data.replaceAll("'", "\''");
        final String finalUid = uid;
        StringRequest request = new StringRequest(Request.Method.POST, "https://tolunaykan.com/HappyBirthday/addNew.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    if(success == 1){
                        Intent demoActivity = new Intent(CreateActivity.this, DemoActivity.class);
                        startActivity(demoActivity);
                    }else{
                        Toast.makeText(CreateActivity.this, R.string.please_try_again_still,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(CreateActivity.this,R.string.please_try_again_still,Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, response);
                showLoadingLayout(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "POST işlemi yapılamadı " + error.getLocalizedMessage());
                Toast.makeText(CreateActivity.this,R.string.please_try_again_still,Toast.LENGTH_SHORT).show();
                showLoadingLayout(false);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Options options = Options.getInstance();
                HashMap<String,String> param = new HashMap<>();
                param.put("data", finalData);
                if(options.getImageBase64() == null){
                    param.put("image", "null");
                }else{
                    param.put("image", options.getImageBase64());
                }
                param.put("uid", finalUid);
                return param;
            }
        };

        queue.add(request);
    }

    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(CreateActivity.this,storagePermissions,STORAGE_REQUEST);
    }

    private void pickFromGallery() {
        Intent galleryPickIntent = new Intent(Intent.ACTION_PICK);
        galleryPickIntent.setType("image/*");
        startActivityForResult(galleryPickIntent,IMAGE_PICK_GALLERY_REQUEST);
    }

    private void setupItem(){
        ArrayList<PagerItem> list = new ArrayList<>();

        PagerItem pageTitle = new PagerItem();
        pageTitle.setType(PagerItem.EDITBUTTON);
        pageTitle.setImage(R.mipmap.ic_title);
        pageTitle.setTitle(getString(R.string.website_title));
        pageTitle.setDescription(getString(R.string.website_title_set));
        pageTitle.setHint(getString(R.string.website_title_hint));
        pageTitle.setSecondType(PagerItem.PAGETITLE);


        PagerItem turnOnLight = new PagerItem();
        turnOnLight.setType(PagerItem.EDITBUTTON);
        turnOnLight.setImage(R.mipmap.ic_light);
        turnOnLight.setTitle(getString(R.string.turnon_light));
        turnOnLight.setDescription(getString(R.string.turnon_light_set_name));
        turnOnLight.setHint(getString(R.string.turnon_light_hint));
        turnOnLight.setSecondType(PagerItem.TURNONLIGHT);


        PagerItem playMusic = new PagerItem();
        playMusic.setType(PagerItem.EDITBUTTON);
        playMusic.setImage(R.mipmap.ic_music);
        playMusic.setTitle(getString(R.string.play_music));
        playMusic.setDescription(getString(R.string.play_music_set_name));
        playMusic.setHint(getString(R.string.play_music_hint));
        playMusic.setSecondType(PagerItem.PLAYMUSIC);


        PagerItem letsDecorate = new PagerItem();
        letsDecorate.setType(PagerItem.EDITBUTTON);
        letsDecorate.setImage(R.mipmap.ic_decorate);
        letsDecorate.setTitle(getString(R.string.lets_decorate));
        letsDecorate.setDescription(getString(R.string.lets_decorate_set_name));
        letsDecorate.setHint(getString(R.string.lets_decorate_hint));
        letsDecorate.setSecondType(PagerItem.LETSDECORATE);


        PagerItem flyBalloons = new PagerItem();
        flyBalloons.setType(PagerItem.EDITBUTTON);
        flyBalloons.setImage(R.mipmap.ic_ballon);
        flyBalloons.setTitle(getString(R.string.fly_balloons));
        flyBalloons.setDescription(getString(R.string.fly_balloons_set_name));
        flyBalloons.setHint(getString(R.string.fly_ballons_hint));
        flyBalloons.setSecondType(PagerItem.FLYBALLOONS);


        PagerItem cake = new PagerItem();
        cake.setType(PagerItem.EDITBUTTON);
        cake.setImage(R.mipmap.ic_cake);
        cake.setTitle(getString(R.string.birthday_cake));
        cake.setDescription(getString(R.string.birthday_cake_set_name));
        cake.setHint(getString(R.string.birthday_cake_hint));
        cake.setSecondType(PagerItem.CAKE);


        PagerItem candle = new PagerItem();
        candle.setType(PagerItem.EDITBUTTON);
        candle.setImage(R.mipmap.ic_candle);
        candle.setTitle(getString(R.string.light_candle));
        candle.setDescription(getString(R.string.light_candle_set_name));
        candle.setHint(getString(R.string.light_candle_hint));
        candle.setSecondType(PagerItem.CANDLE);


        PagerItem happyBirthday = new PagerItem();
        happyBirthday.setType(PagerItem.EDITBUTTON);
        happyBirthday.setImage(R.mipmap.ic_cake);
        happyBirthday.setTitle(getString(R.string.happy_birthday));
        happyBirthday.setDescription(getString(R.string.happy_bithday_set_name));
        happyBirthday.setHint(getString(R.string.happy_birthday_hint));
        happyBirthday.setSecondType(PagerItem.HAPPYBIRTHDAY);


        PagerItem messagesForYou = new PagerItem();
        messagesForYou.setType(PagerItem.EDITBUTTON);
        messagesForYou.setImage(R.mipmap.ic_message);
        messagesForYou.setTitle(getString(R.string.messages_for_you));
        messagesForYou.setDescription(getString(R.string.messages_for_you_set_name));
        messagesForYou.setHint(getString(R.string.messages_for_you_hint));
        messagesForYou.setSecondType(PagerItem.MESSAGESFORYOU);


        PagerItem addMessage = new PagerItem();
        addMessage.setType(PagerItem.ADDMESSAGE);


        PagerItem addPhoto = new PagerItem();
        addPhoto.setType(PagerItem.ADDPHOTO);


        list.add(pageTitle);
        list.add(turnOnLight);
        list.add(playMusic);
        list.add(letsDecorate);
        list.add(flyBalloons);
        list.add(cake);
        list.add(candle);
        list.add(happyBirthday);
        list.add(messagesForYou);

        list.add(addMessage);
        list.add(addPhoto);


        adapter = new ViewPagerAdapter(list, new SelectPhotoListener() {
            @Override
            public void onPhotoSelected() {
                selectPhoto();
            }
        });
    }

    private void selectPhoto(){
        if(checkStoragePermission()){
            pickFromGallery();
        }else{
            requestStoragePermission();
        }
    }

    private void setupIndicators(){
        ImageView[] indicators = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,0,8,0);
        for(int i=0; i<indicators.length; i++){
            indicators[i] = new ImageView(CreateActivity.this);
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(layoutParams);
            indicatorsLayout.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int position){
        for(int i=0; i<indicatorsLayout.getChildCount(); i++){
            ImageView imageView = (ImageView) indicatorsLayout.getChildAt(i);
            if(i == position){
                imageView.setImageResource(R.drawable.indicator_active);
            }else{
                imageView.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_REQUEST){
                if (data != null) {
                    Uri imageUri = data.getData();
                    Bitmap bitmap = null;
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= 29){
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
                        } else{
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        }

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        Options.getInstance().setImageBase64(encodedImage);
                        View view = viewPager2.findViewWithTag("ADDPHOTOTAG");
                        if(view != null){
                            ImageView imageView = view.findViewById(R.id.pager_item_add_photo_image);
                            imageView.setImageBitmap(bitmap);
                        }
                        Toast.makeText(CreateActivity.this, R.string.photo_selected,Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.d(TAG, "Foto Base64e çevrilemedi = " + e.getLocalizedMessage());
                        Toast.makeText(CreateActivity.this, R.string.photo_not_selected,Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_REQUEST) {
            if (grantResults.length > 0) {
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    pickFromGallery();
                } else {
                    Toast.makeText(CreateActivity.this, R.string.enable_permission, Toast.LENGTH_SHORT).show();
                }
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.are_you_sure)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
