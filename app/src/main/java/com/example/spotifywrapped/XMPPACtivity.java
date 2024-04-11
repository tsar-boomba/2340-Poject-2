package com.example.spotifywrapped;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.spotifywrapped.XMPPTask;

public class XMPPACtivity extends AppCompatActivity {
    private EditText messageEditText;
    private XMPPTask xmppTask;
    private TextView messageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xmppTask = new XMPPTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                xmppTask.setMessageListener(message -> showMessage(message));
            }
        };
        xmppTask.execute();
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