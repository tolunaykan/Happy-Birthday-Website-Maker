package com.zisantolunay.happybirthday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class HelperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        String page_title = getString(R.string.can_set_link_name);
        String turnOnLight = getString(R.string.can_btn_turnon_light);
        String playMusic = getString(R.string.can_btn_play_music);
        String letsDecorate = getString(R.string.can_btn_decorate);
        String flyBalloons = getString(R.string.can_btn_fly_ballons);
        String cake = getString(R.string.can_btn_show_cake);
        String candle = getString(R.string.can_btn_light_canle);
        String happyBirthday = getString(R.string.can_btn_just_text);
        String messagesForYou = getString(R.string.can_btn_gets_messages);
        String addMessage = getString(R.string.can_btn_add_messages);
        String addPhoto = getString(R.string.can_btn_set_photo);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type",PagerItem.PAGETITLE);

        TextView textView = findViewById(R.id.textView_helper);
        ImageView imageView = findViewById(R.id.imageView2);

        if(type == PagerItem.PAGETITLE){
            textView.setText(page_title);
            imageView.setImageResource(R.drawable.page_title);
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
        }else if(type == PagerItem.TURNONLIGHT){
            textView.setText(turnOnLight);
            imageView.setImageResource(R.drawable.turn_on_light);
        }else if(type == PagerItem.PLAYMUSIC){
            textView.setText(playMusic);
            imageView.setImageResource(R.drawable.play_music);
        }else if(type == PagerItem.LETSDECORATE){
            textView.setText(letsDecorate);
            imageView.setImageResource(R.drawable.lets_decorate);
        }else if(type == PagerItem.FLYBALLOONS){
            textView.setText(flyBalloons);
            imageView.setImageResource(R.drawable.fly_balloons);
        }else if(type == PagerItem.CAKE){
            textView.setText(cake);
            imageView.setImageResource(R.drawable.show_cake);
        }else if(type == PagerItem.CANDLE){
            textView.setText(candle);
            imageView.setImageResource(R.drawable.light_candle);
        }else if(type == PagerItem.HAPPYBIRTHDAY){
            textView.setText(happyBirthday);
            imageView.setImageResource(R.drawable.happy_birthday);
        }else if(type == PagerItem.MESSAGESFORYOU){
            textView.setText(messagesForYou);
            imageView.setImageResource(R.drawable.messages_for_you);
        }else if(type == PagerItem.ADDMESSAGE){
            textView.setText(addMessage);
            imageView.setImageResource(R.drawable.message2);
        }else if(type == PagerItem.ADDPHOTO){
            textView.setText(addPhoto);
            imageView.setImageResource(R.drawable.add_photo);
        }



    }
}
