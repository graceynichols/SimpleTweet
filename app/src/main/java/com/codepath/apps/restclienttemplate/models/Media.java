package com.codepath.apps.restclienttemplate.models;


import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Media {

    public String mediaUrlHttps;
    public String type;
    // TODO how should I save size?
    public int width;
    public int height;
    //public JSONObject sizes;

    // Empty constructor for parcel library
    public Media() {}

    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.mediaUrlHttps = jsonObject.getString("media_url_https");
        media.type = jsonObject.getString("type");
        if (jsonObject.has("sizes")) {
            // Get the image size;
            media.width =  ((JSONObject) jsonObject.getJSONObject("sizes")).getJSONObject("medium").getInt("w");
            media.height =  ((JSONObject) jsonObject.getJSONObject("sizes")).getJSONObject("medium").getInt("h");
        }
        //media.sizes = jsonObject.getJSONObject("sizes");
        return media;
    }


}