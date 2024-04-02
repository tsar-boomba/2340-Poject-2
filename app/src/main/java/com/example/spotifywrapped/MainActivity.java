package com.example.spotifywrapped;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDelegate;
import android.widget.CompoundButton;
import androidx.appcompat.widget.SwitchCompat;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {
    private EditText messageEditText;
    private XMPPTask xmppTask;
    private TextView messageTextView;
    SwitchCompat switchCompat;
    SharedPreferences sharedPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchCompat = findViewById(R.id.switchCompat);
        sharedPreferences = getSharedPreferences("night",0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode",true);
        if (booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setChecked(true);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    switchCompat.setChecked(true);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    switchCompat.setChecked(false);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();

                }
            }
        });

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