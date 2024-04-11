package com.example.spotifywrapped.spotify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.DefaultLifecycleObserver;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Spotify implements DefaultLifecycleObserver {
    public static final String CLIENT_ID = "890fe3941c4d44398befe9085287bdb0";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private static final String TAG = "Spotify";
    private static final String REDIRECT_URI = "com.example.spotifywrapped://auth";
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson;
    private String accessToken;
    private String accessCode;
    private String refreshToken;
    private Call call;
    private Runnable onResult;

    public Spotify() {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken(Activity activity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(activity, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode(Activity activity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(activity, AUTH_CODE_REQUEST_CODE, request);
    }

    public void setOnResult(Runnable onResult) {
        this.onResult = onResult;
    }

    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            accessToken = response.getAccessToken();
            Log.i(TAG, "Request token done! " + accessToken);
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            accessCode = response.getCode();
            Log.i(TAG, "Request code done! " + accessCode);
        }

        if (onResult != null) {
            onResult.run();
        }
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void getUser(Context context, Consumer<Optional<SpotifyMe>> callback) {
        apiRequest(context, "/me", SpotifyMe.class, new HashMap<>(0), callback);
    }

    public void getTopTracks(Context context, Timeframe timeframe, Consumer<Optional<TopTracks>> callback) {
        HashMap<String, String> query = new HashMap<>();
        query.put("time_range", timeframe.toApiParam());
        apiRequest(context, "/me/top/tracks", TopTracks.class, query, callback);
    }

    private <T> void apiRequest(
            Context context,
            String endpoint,
            Class<T> classOfT,
            Map<String, String> query,
            Consumer<Optional<T>> callback
    ) {
        if (accessToken == null) {
            Toast.makeText(context, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        cancelCall();

        StringBuilder queryString = new StringBuilder(128);
        queryString.append('?');

        for (Map.Entry<String, String> pair : query.entrySet()) {
            queryString.append(pair.getKey());
            queryString.append('=');
            queryString.append(pair.getValue());
            queryString.append('&');
        }

        call = httpClient.newCall(new Request.Builder()
                .get()
                .url("https://api.spotify.com/v1" + endpoint + queryString.toString())
                .addHeader("Authorization", "Bearer " + accessToken)
                .build()
        );

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.accept(Optional.empty());
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(context, "Failed to fetch data, watch Logcat for more details", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.accept(Optional.empty());
                    Log.e(TAG, "Response failed with status: " + response.code() + " " + response.message());
                }

                try {
                    T res = gson.fromJson(response.body().string(), classOfT);
                    callback.accept(Optional.of(res));
                } catch (JsonSyntaxException e) {
                    callback.accept(Optional.empty());
                    Log.e(TAG, "Failed to parse data: " + e);
                    Toast.makeText(context, "Failed to parse data, watch Logcat for more details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email", "user-top-read", "user-follow-read", "user-read-recently-played"}) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token").build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (call != null) {
            call.cancel();
        }
    }
}
