package com.example.spotifywrapped;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.spotifywrapped.theme.MyAppTheme;

public class Utils {
    /**
     * Runs toRun in another thread, making sure to not block the current thread.
     * @param toRun the function to run in another thread
     */
    public static void unblock(Runnable toRun) {
        new Thread(toRun).start();
    }

    public static TextView dialogTitle(Context context, MyAppTheme theme, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setTextColor(theme.textColor(context));
        textView.setTextSize(22f);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return textView;
    }
}
