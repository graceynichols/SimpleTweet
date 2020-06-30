package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class ExtendedEntities {
    private List<Media> mediaList;
    public static String TAG = "ExtendedEntities";

    // Empty constructor for parcel library
    public ExtendedEntities() {}

    public static ExtendedEntities fromJson(JSONObject jsonObject) throws JSONException {
        ExtendedEntities extendedEntities = new ExtendedEntities();
        extendedEntities.mediaList = new ArrayList<>();
        Log.i(TAG, jsonObject.toString());
        // Convert each media object from json
        if (jsonObject.has("media")) {
            JSONArray mediaJson = jsonObject.getJSONArray("media");
            for (int i = 0; i < mediaJson.length(); i++) {
                // Convert each media object from JSON and add to media list
                Media media = Media.fromJson((JSONObject) (mediaJson.get(i)));
                Log.d(TAG, "Media Type: " + media.getType());
                extendedEntities.mediaList.add(media);
                // TODO only adds photos
                /*
                if (media.type.equals("photo")) {
                    extendedEntities.mediaList.add(media);
                }*/

            }
        }
        return extendedEntities;

    }

    public List<Media> getMediaList() {
        return mediaList;
    }
}
