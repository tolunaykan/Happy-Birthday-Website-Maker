package com.zisantolunay.happybirthday;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Options {
    private static Options options;
    private String pageTitle, imageBase64=null, turnOnLight, playMusic, letsDecorate, flyBalloons, cake, candle, happyBirthday, messagesForYou;
    public ArrayList<String> messages;

    public Options(){
        messages = new ArrayList<>();
    }

    public static Options getInstance(){
        if(options == null){
            options = new Options();
        }
        return options;
    }

    public String getAsJson() throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page_title", pageTitle);
        jsonObject.put("turn_on_light", turnOnLight);
        jsonObject.put("play_music", playMusic);
        jsonObject.put("lets_decorate", letsDecorate);
        jsonObject.put("fly_ballons", flyBalloons);
        jsonObject.put("cake", cake);
        jsonObject.put("candle", candle);
        jsonObject.put("happy_birthday", happyBirthday);
        jsonObject.put("message_for_you", messagesForYou);
        JSONArray jsonMessages = new JSONArray();
        for(String message:messages){
            jsonMessages.put(message);
        }
        jsonObject.put("messages",jsonMessages);

        return jsonObject.toString();

    }

    public String getByType(int type){
        if(type == PagerItem.PAGETITLE){
            return pageTitle;
        }else if(type == PagerItem.TURNONLIGHT){
            return turnOnLight;
        }else if(type == PagerItem.PLAYMUSIC){
            return playMusic;
        }else if(type == PagerItem.LETSDECORATE){
            return letsDecorate;
        }else if(type == PagerItem.FLYBALLOONS){
            return flyBalloons;
        }else if(type == PagerItem.CAKE){
            return cake;
        }else if(type == PagerItem.CANDLE){
            return candle;
        }else if(type == PagerItem.HAPPYBIRTHDAY){
            return happyBirthday;
        }else if(type == PagerItem.MESSAGESFORYOU){
            return messagesForYou;
        }
        return " ";
    }

    public void setButtonEditOption(String input, int type){
        if(type == PagerItem.PAGETITLE){
            pageTitle = input;
        }else if(type == PagerItem.TURNONLIGHT){
            turnOnLight = input;
        }else if(type == PagerItem.PLAYMUSIC){
            playMusic = input;
        }else if(type == PagerItem.LETSDECORATE){
            letsDecorate = input;
        }else if(type == PagerItem.FLYBALLOONS){
            flyBalloons = input;
        }else if(type == PagerItem.CAKE){
            cake = input;
        }else if(type == PagerItem.CANDLE){
            candle = input;
        }else if(type == PagerItem.HAPPYBIRTHDAY){
            happyBirthday = input;
        }else if(type == PagerItem.MESSAGESFORYOU){
            messagesForYou = input;
        }
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public void addMessage(String message){
        messages.add(message);
    }

    public void removeMessage(int position){
        messages.remove(position);
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getTurnOnLight() {
        return turnOnLight;
    }

    public String getPlayMusic() {
        return playMusic;
    }

    public String getLetsDecorate() {
        return letsDecorate;
    }

    public String getFlyBalloons() {
        return flyBalloons;
    }

    public String getCake() {
        return cake;
    }

    public String getCandle() {
        return candle;
    }

    public String getHappyBirthday() {
        return happyBirthday;
    }

    public String getMessagesForYou() {
        return messagesForYou;
    }
}
