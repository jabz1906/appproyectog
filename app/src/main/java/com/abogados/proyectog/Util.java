package com.abogados.proyectog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Util {
    private Context context;

    public Util(Context context) {
        this.context = context;
    }

    public boolean connectionTest() {
        boolean connected = false;
        ConnectivityManager connection = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networks = connection.getAllNetworkInfo();
        for (int i = 0; i < networks.length; i++) {
            if (networks[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void session(Integer user_id, String email, String token) {
        String date = getDateTime();
        bd connection = new bd(context);
        try {
            connection.open();
            connection.createSession(user_id, email, token, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
    }

    public boolean verifySession() throws Exception {
        bd connection = new bd(context);
        connection.open();
        Cursor cursor = connection.searchSessionActive();
        if (cursor.moveToFirst() != false) {
            return true;
        }
        connection.close();
        return false;
    }

    public Cursor getSession() {
        Cursor cursor = null;
        bd connection = new bd(context);
        try {
            connection.open();
            cursor = connection.searchSessionActive();
            cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
        return cursor;
    }

    public void closeSession(String email) {
        bd connection = new bd(context);
        try {
            connection.open();
            connection.closeSession(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection.close();
    }

    public void completeData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Importante");
        builder.setMessage("Debe completar todos los datos Requeridos o llenarlos correctamente");
        builder.setPositiveButton("Aceptar", null);
        builder.create();
        builder.show();
    }
}
