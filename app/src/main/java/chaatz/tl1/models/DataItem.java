package chaatz.tl1.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by SHEILD on 3/3/2017.
 */

public class DataItem {
    public static final int IMAGE_WITH_DETAIL = 1;
    public static final int IMAGE_ONLY = 2;
    public static final int LOADING_FOOTER = 3;

    // type: 1. Image with title and description 2. Image only
    private int mType = IMAGE_WITH_DETAIL;
    private String mImageUrl = "";
    private String mTitle = "";
    private String mDescription = "";

    public DataItem(JSONObject obj) {
        if (obj == null) {
            mType = LOADING_FOOTER;
        } else {
            try {
                mType = obj.getInt("type");
                mImageUrl = obj.getString("url");
                if (mType == IMAGE_WITH_DETAIL) {
                    mTitle = obj.getString("title");
                    mDescription = obj.getString("description");
                }
            } catch (JSONException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    public String getImageUrl() {
        return mImageUrl;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getDescription() {
        return mDescription;
    }
    public int getType() { return mType; }
}
