package com.example.polyOder.PushNotification;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FMC {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SEVSER_TOKEN = "key=AAAAqb4ftJU:APA91bGNciBVrojeI_z6Fzx1IEbhc4U-rbOk1vwjkt5_MMWtNRDfSNvKpjbchE0Lihriea2TcmGsPG0SSgmbmBkh3JvJB_tnMkC2Rzt4XkCpc7CrLzBI5mpwI_SaGmWJ5ejIVVwIf5l3";

    public static void pushNotification(Context context, String token, String title, String message) {
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to", token);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("title", title);
            jsonObject1.put("body", message);
            jsonObject.put("data", jsonObject1);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("TAG", "onResponse: "+jsonObject);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "onErrorResponse: ");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Authorization", SEVSER_TOKEN);
                    map.put("Content-Type", "application/json");
                    return map;
                }
            };

            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
