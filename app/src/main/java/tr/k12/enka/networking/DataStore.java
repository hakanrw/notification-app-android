package tr.k12.enka.networking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import net.grandcentrix.tray.AppPreferences;

import java.util.Dictionary;
import java.util.HashMap;

public class DataStore {
    final static String SERVER_URL = "http://192.168.1.7:1337";

    static HashMap<String, String> dict = new HashMap<>();

    public static String getField(String field) {
        if(dict.get(field) == null) dict.put(field, getPreferences().getString(field, null));
        return dict.get(field);
    }

    public static void setField(String field, String entry) {
        dict.put(field, entry);
        getPreferences().put(field, entry);
    }

    public static String getToken() {
        return getField("token");
    }

    public static void setToken(String token) {
        setField("token", token);
    }

    public static String getMail() {
        return getField("mail");
    }

    public static void setMail(String mail) {
        setField("mail", mail);
    }

    public static String getReadDate() {
        return getField("readDate");
    }

    public static void setReadDate(String date) {
        setField("readDate", date);
    }

    public static String getReceiveDate() {
        return getField("receiveDate");
    }

    public static void setReceiveDate(String date) {
        setField("receiveDate", date);
    }

    //public static Activity getMainActivity() {
    //    return MainActivity.Companion.getAct();
    //}

    public static AppPreferences getPreferences() {
        return PreferenceSingleton.prefs;
    }
}
