package com.example.polyOder.chatPoly.AIChat;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;

import com.example.polyOder.databinding.ActivityPolyChatBinding;
import com.example.polyOder.model.Message;
import com.example.polyOder.pushNotification.NetworkStateReceiver;
;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatPolyAIActivity extends AppCompatActivity {
    public ActivityPolyChatBinding binding = null;
    private BroadcastReceiver myReceiver = null;
    private ArrayList<Message> messageList;
    private MessageAdapter messageAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPolyChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setStatusBarColor(R.color.white);
        myReceiver = new NetworkStateReceiver();
        broadcastIntent();
        messageList = new ArrayList<>();


        binding.icSend.setOnClickListener(ic ->{
            String question = binding.edMessenger.getText().toString().trim();
            addToChat(question, Message.SEND_BY_ME);
            binding.edMessenger.setText("");
            callApiChat(question);
            binding.boxWelcome.setVisibility(View.GONE);

        });
        messageAdapter = new MessageAdapter(messageList);
        binding.recViewChat.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        binding.recViewChat.setLayoutManager(llm);


        binding.icBack.setOnClickListener(ic ->{
            startActivity(new Intent(this, MainActivity.class));
        });

    }

    private void addToChat(String message, String sendBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sendBy));
                messageAdapter.notifyDataSetChanged();
                binding.recViewChat.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });

    }

    private void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response, Message.SEND_BY_BOT);
    }

    private void callApiChat(String question){

        messageList.add(new Message("Typing....",Message.SEND_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinci-003");
            jsonBody.put("prompt",question);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature",0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Lỗi kết nối, hãy thử lại sau.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject  jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    addResponse("Phản hồi không thành công, hãy thử lại!");
                }
            }
        });



       /* final String requestBody = jsonBody.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://api.openai.com/v1/completions", null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray choicesArray = response.getJSONArray("choices");
                            JSONObject firstChoice = choicesArray.getJSONObject(0);
                            String result = firstChoice.getString("text");
                            addResponse(result.trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                addResponse("Phản hồi không thành công, hãy thử lại!");
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + "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");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);*/

    }



    public void broadcastIntent() {
        registerReceiver(myReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }


    public void setStatusBarColor(int color){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getColor(color));
    }



}