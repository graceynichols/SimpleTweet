package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class ExtendedEntities {
    public List<Media> mediaList;

    // Empty constructor for parcel library
    public ExtendedEntities() {}

    public static ExtendedEntities fromJson(JSONObject jsonObject) throws JSONException {
        ExtendedEntities extendedEntities = new ExtendedEntities();
        extendedEntities.mediaList = new ArrayList<>();
        // Convert each media object from json
        JSONArray mediaJson = jsonObject.getJSONArray("media");
        for (int i = 0; i < mediaJson.length(); i++) {
            extendedEntities.mediaList.add(Media.fromJson((JSONObject) (mediaJson.get(i))));
        }
        return extendedEntities;
    }
}
