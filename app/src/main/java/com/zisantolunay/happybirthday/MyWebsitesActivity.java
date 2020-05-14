package com.zisantolunay.happybirthday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MyWebsitesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_websites);

        TextView noWebsite = findViewById(R.id.textView4);
        ListView listView = findViewById(R.id.listview);


        SharedPreferences preferences = getSharedPreferences("com.zisantolunay.happybirthday.SHARED",MODE_PRIVATE);
        Set<String> prefSet = preferences.getStringSet("orders",null);
        if(prefSet == null){
            noWebsite.setVisibility(View.VISIBLE);
        }else{
            final ArrayList<String> data = new ArrayList<>(prefSet);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, data);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://tolunaykan.com/HappyBirthday/user-"+data.get(position)));
                    startActivity(intent);
                }
            });
        }



    }
}
