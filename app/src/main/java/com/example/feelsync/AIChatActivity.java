package com.example.feelsync;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText messageInput;
    Button sendButton;
    BottomNavigationView bottomNavigationView;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        messageList = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_ai) return true;
            else if (itemId == R.id.nav_calendar)
                startActivity(new Intent(this, CalendarActivity.class));
            else if (itemId == R.id.nav_settings)
                startActivity(new Intent(this, SettingsActivity.class));
            else if (itemId == R.id.nav_home)
                startActivity(new Intent(this, MainPageActivity.class));
            else if (itemId == R.id.nav_music)
                startActivity(new Intent(this, MusicActivity.class));

            return true;
        });

        recyclerView = findViewById(R.id.recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);


        sendButton.setOnClickListener(v -> {
            String question = messageInput.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            messageInput.setText("");
            callAPI(question);

        });
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response) {
        addToChat(response, Message.SENT_BY_BOT);
    }

    void callAPI(String question) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo-instruct");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);

        } catch (JSONException e) {
            throw new RuntimeException(e);
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
                addResponse("Failed to load response due to " + e.getMessage());

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string(); // <-- читаем тело ответа
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        addResponse("Error parsing response");
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string()); // <-- исправлено
                }
            }

        });
    }
}

