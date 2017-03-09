package chaatz.tl1.network;

import okhttp3.OkHttpClient;

/**
 * Created by SHEILD on 7/3/2017.
 */

public class OkHttpClientSingleton {

    private static OkHttpClient mClient = null;

    public static OkHttpClient getInstance() {
        if (mClient == null) {
            mClient = new OkHttpClient();
        }
        return mClient;
    }
}
