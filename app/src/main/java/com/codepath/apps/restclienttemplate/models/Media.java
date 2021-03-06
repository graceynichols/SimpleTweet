package com.codepath.apps.restclienttemplate.models;


import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Media {

    private String mediaUrlHttps;
    private String type;
    private int width;
    private int height;

    // Empty constructor for parcel library
    public Media() {}

    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.mediaUrlHttps = jsonObject.getString("media_url_https");
        media.type = jsonObject.getString("type");
        if (jsonObject.has("sizes")) {
            // Get the image size;
            media.width =  (jsonObject.getJSONObject("sizes")).getJSONObject("medium").getInt("w");
            media.height =  (jsonObject.getJSONObject("sizes")).getJSONObject("medium").getInt("h");
        }
        return media;
    }

    public String getMediaUrlHttps() {
        return mediaUrlHttps;
    }

    public String getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}