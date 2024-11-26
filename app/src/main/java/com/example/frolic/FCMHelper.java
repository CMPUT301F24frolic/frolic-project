package com.example.frolic;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.json.JSONObject;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FCMHelper {

    private static final String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

    public static String generateJWT(Context context) throws Exception {
        // Load and parse the JSON key file
        InputStream inputStream = context.getResources().openRawResource(R.raw.service_account);
        byte[] jsonBytes = new byte[inputStream.available()];
        inputStream.read(jsonBytes);
        String json = new String(jsonBytes);

        JSONObject jsonObject = new JSONObject(json);
        String clientEmail = jsonObject.getString("client_email");
        String privateKeyPem = jsonObject.getString("private_key");

        // Convert the private key from PEM format
        privateKeyPem = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] privateKeyBytes = Base64.decode(privateKeyPem, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // Generate the JWT
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) null, (RSAPrivateKey) privateKey);
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + 3600 * 1000); // 1 hour expiration

        String token = JWT.create()
                .withIssuer(clientEmail)
                .withAudience("https://oauth2.googleapis.com/token")
                .withClaim("scope", SCOPE)
                .withIssuedAt(now)
                .withExpiresAt(expirationTime)
                .sign(algorithm);

        return token;
    }

    public static String getAccessToken(String jwtToken) throws Exception {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + jwtToken);

        Request request = new Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);

        return jsonObject.getString("access_token");
    }

    public static void sendFCMNotification(String accessToken, String fcmToken, String title, String body) throws Exception {
        Log.d("FCMHelper", "Sending FCM notification to token: " + fcmToken);
        OkHttpClient client = new OkHttpClient();

        // Create JSON payload
        JSONObject message = new JSONObject();
        message.put("token", fcmToken);
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", body);
        message.put("notification", notification);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);

        // Send request
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/frolic-925b1/messages:send")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        Log.d("FCMHelper", "FCM Response: " + response.body().string());
    }

    public void sendNotificationToFCMToken(final Context context, final String fcmToken, String title, String body) {
        ExecutorService executor = Executors.newSingleThreadExecutor();  // Create a single thread executor
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Generate JWT and access token in the background
                    String jwtToken = FCMHelper.generateJWT(context);
                    String accessToken = FCMHelper.getAccessToken(jwtToken);

                    // Send the FCM notification in the background
                    FCMHelper.sendFCMNotification(accessToken, fcmToken, title, body);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("FCMHelper", "Error sending FCM notification", e);
                }
            }
        });
    }

}
