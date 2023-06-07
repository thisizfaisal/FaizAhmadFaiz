package com.example.faizahmadfaiz.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class Helper {

    public static String getClipboardData(Context context) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // Gets the clipboard data from the clipboard
        ClipData clip = clipboard.getPrimaryClip();
        ClipData.Item item = clip.getItemAt(0);
        return item != null ? item.getText().toString() : null;
    }

}
