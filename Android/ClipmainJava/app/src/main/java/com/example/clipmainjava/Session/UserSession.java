package com.example.clipmainjava.Session;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREFS_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static UserSession instance;
    private final SharedPreferences prefs;

    private UserSession(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
    }

    public static synchronized UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(Long userId, String phone) {
        prefs.edit()
                .putLong(KEY_USER_ID ,userId)
                .putString(KEY_PHONE, phone)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getPhone() {
        return prefs.getString(KEY_PHONE, "");
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
