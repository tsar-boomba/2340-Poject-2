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

import java.io.IOException;
import java.time.Duration;
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
    private final OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(5)).callTimeout(Duration.ofSeconds(10)).build();
    private final Gson gson;
    private String accessToken;
    private String accessCode;
    private String refreshToken;
    private Call call;
    private Consumer<Optional<String>> onAuthResponse;

    public Spotify() {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private static AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
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
    private static Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
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

    public void setOnAuthResponse(Consumer<Optional<String>> onAuthResponse) {
        this.onAuthResponse = onAuthResponse;
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
            if (onAuthResponse != null) {
                onAuthResponse.accept(Optional.ofNullable(accessToken));
            }
            Log.i(TAG, "Request token done! " + accessToken);
        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            accessCode = response.getCode();
            if (onAuthResponse != null) {
                onAuthResponse.accept(Optional.ofNullable(accessCode));
            }
            Log.i(TAG, "Request code done!");
        }

    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void getUser(Context context, Consumer<Optional<Me>> callback) {
        apiRequest(context, "/me", Me.class, new HashMap<>(0), callback);
    }

    public void getTopTracks(Context context, Timeframe timeframe, Consumer<Optional<TopTracks>> callback) {
        HashMap<String, String> query = new HashMap<>();
        query.put("time_range", timeframe.toApiParam());
        query.put("limit", "50");
        apiRequest(context, "/me/top/tracks", TopTracks.class, query, callback);
    }

    public Optional<Artist> getArtistBlocking(String href) {
        cancelCall();

        call = httpClient.newCall(new Request.Builder()
                .get()
                .url(href)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build()
        );

        try {
            Response res = call.execute();
            return Optional.of(gson.fromJson(res.body().charStream(), Artist.class));
        } catch (Exception ex) {
            Log.e(TAG, "getArtistBlocking: ", ex);
            return Optional.empty();
        }
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

        String fullUrl = "https://api.spotify.com/v1" + endpoint + queryString;

        call = httpClient.newCall(new Request.Builder()
                .get()
                .url(fullUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build()
        );

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "Failed to fetch data: " + e);
                callback.accept(Optional.empty());
                Toast.makeText(context, "Failed to fetch data, watch Logcat for more details", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.accept(Optional.empty());
                    Log.e(TAG, "Response failed with status: " + response.code() + " " + response.message());
                }

                try {
                    T res = gson.fromJson(response.body().charStream(), classOfT);
                    Log.i(TAG, "Completed request successfully: " + fullUrl);
                    callback.accept(Optional.of(res));
                } catch (JsonSyntaxException e) {
                    callback.accept(Optional.empty());
                    Log.e(TAG, "Failed to parse data: " + e);
                    Toast.makeText(context, "Failed to parse data, watch Logcat for more details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelCall() {
        if (call != null) {
            call.cancel();
        }
    }

    public void setAccessToken(String token) {
        accessToken = token;
    }
}
