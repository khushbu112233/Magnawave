package com.jlouistechnology.magnawave.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref {
    /*---------------String--------------*/
    /*---------------int----------------*/
    /*---------------boolean----------------*/
    /*---------------XML----------------*/
    private static SharedPreferences sharedPreferences = null;

    public static void openPref(Context context) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public static String getValue(Context context, String key,
                                  String defaultValue) {
        try {
            Pref.openPref(context);
            String result = Pref.sharedPreferences.getString(key, defaultValue);
            Pref.sharedPreferences = null;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static void setValue(Context context, String key, int value) {
        try {
            Pref.openPref(context);
            SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
            prefsPrivateEditor.putInt(key, value);
            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            Pref.sharedPreferences = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getValue(Context context, String key,
                               int defaultValue) {
        try {
            Pref.openPref(context);
            int result = Pref.sharedPreferences.getInt(key, defaultValue);
            Pref.sharedPreferences = null;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static void setValue(Context context, String key, String value) {
        try {
            Pref.openPref(context);
            SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
            prefsPrivateEditor.putString(key, value);
            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            Pref.sharedPreferences = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getValue(Context context, String key,
                                   boolean defaultValue) {
        try {
            Pref.openPref(context);
            boolean result = Pref.sharedPreferences.getBoolean(key, defaultValue);
            Pref.sharedPreferences = null;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static void setValue(Context context, String key, boolean value) {
        try {
            Pref.openPref(context);
            SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
            prefsPrivateEditor.putBoolean(key, value);
            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            Pref.sharedPreferences = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getValue(Context context, String key,
                                 float defaultValue) {
        try {
            Pref.openPref(context);
            float result = Pref.sharedPreferences.getFloat(key, defaultValue);
            Pref.sharedPreferences = null;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static void setValue(Context context, String key, float value) {
        try {
            Pref.openPref(context);
            SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
            prefsPrivateEditor.putFloat(key, value);
            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            Pref.sharedPreferences = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAll(Context context) {
        try {
            Pref.openPref(context);
            Pref.sharedPreferences.edit().clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
