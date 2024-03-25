package com.example.spotifywrapped;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.spotifywrapped.XMPPTask;

public class MainActivity extends AppCompatActivity {
    private EditText messageEditText;
    private XMPPTask xmppTask;
    private TextView messageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageEditText = findViewById(R.id.messageEditText);
        Button sendButton = findViewById(R.id.sendButton);
        messageTextView = findViewById(R.id.messageTextView);
        xmppTask = new XMPPTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                xmppTask.setMessageListener(message -> showMessage(message));
            }
        };
        xmppTask.execute();
        sendButton.setOnClickListener(v -> sendMessage());
    }
    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            xmppTask.sendMessage(message);
            messageEditText.getText().clear();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (xmppTask != null) {
            xmppTask.disconnect();
        }
    }
    private void showMessage(String message) {
        runOnUiThread(() -> {
            //messageTextView.append(message + "\n");
            messageTextView.setText(message);
        });
    }
}