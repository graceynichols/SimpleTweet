package com.codepath.apps.restclienttemplate.models;


import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Media {

    public String mediaUrlHttps;
    public String type;
    // TODO how should I save size?
    //public JSONObject sizes;

    // Empty constructor for parcel library
    public Media() {}

    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.mediaUrlHttps = jsonObject.getString("media_url_https");
        media.type = jsonObject.getString("type");
        //media.sizes = jsonObject.getJSONObject("sizes");
        return media;
    }

}