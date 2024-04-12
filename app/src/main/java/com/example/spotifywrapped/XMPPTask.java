package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.unblock;

import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

public class XMPPTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "XMPPTask";
    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private EntityBareJid jid;
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("user", "GTCS2340")
                    .setXmppDomain("natecarr.xyz")
                    .setHost("natecarr.xyz")
                    .build();
            connection = new XMPPTCPConnection(config);
            connection.connect();
            Log.i(TAG, "XMPP connecting...");
            connection.login();
            Log.i(TAG, "XMPP logging in...");
            chatManager = ChatManager.getInstanceFor(connection);
            jid = JidCreate.entityBareFrom("ai@natecarr.xyz");
            Log.i(TAG, "Ready to chat!");
            chatManager.addIncomingListener((from, message, chat) -> {
                if (messageListener != null) {
                    messageListener.onMessageReceived(message.getBody());
                }
            });
        } catch (Exception e) {
            messageListener.onMessageReceived(e.toString());
        }
        return null;
    }
    public void sendMessage(String messageBody) {
        if (connection != null && connection.isConnected()) {
            try {
                Chat chat = chatManager.chatWith(jid);
                chat.send(messageBody);
                Log.i(TAG, "Sent prompt: " + messageBody);
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
            }
        } else {
            Log.e(TAG, "Connection is not established");
        }
    }

    public void disconnect() {
        if (connection != null && connection.isConnected()) {
            unblock(() -> {
                connection.disconnect();
            });
        }
    }
    public interface MessageListener {
        void onMessageReceived(String message);
    }

    private MessageListener messageListener;

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
}
