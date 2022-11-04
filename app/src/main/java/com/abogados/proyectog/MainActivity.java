package com.abogados.proyectog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    EditText etEmail, etPassword;

    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        util = new Util(MainActivity.this);

        validatePermission();

        try {
            if (util.verifySession()) {
                Intent intent = new Intent(MainActivity.this, Events.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!util.connectionTest()) {
            Toast.makeText(MainActivity.this, "Debe contar con conexion a internet", Toast.LENGTH_LONG).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (util.connectionTest()) {
                    if (etEmail.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")) {
                        Toast.makeText(MainActivity.this, "Debe completar todos los datos", Toast.LENGTH_LONG).show();
                    } else {
                        if (!util.validateEmail(etEmail.getText().toString().trim())) {
                            Toast.makeText(MainActivity.this, "La direcion de correo no es valida", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                JSONObject sendData = new JSONObject();
                                sendData.put("email", etEmail.getText().toString().trim());
                                sendData.put("password", etPassword.getText().toString().trim());
                                Log.w("json", "" + sendData);
                                login("login", sendData, etEmail.getText().toString().trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Debe contar con conexion a internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login(String direction, JSONObject data, String email) {

        String url = getString(R.string.url) + "/" + direction;

        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        //progressDialog.setMax(100);
        progressDialog.setMessage("Cargando...");
        progressDialog.setTitle("Revisando la Informacion");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.w("response", "" + response);
                try {
                    if (response.getInt("code") == 200) {
                        Integer user_id = response.getInt("user_id");
                        String token = response.getString("access_token");
                        util.session(user_id, email, token);
                        Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, Events.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Usuario o Clave Incorrecto", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("error", "" + error);
                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                //params.put("Authorization", "Bearer " + Utils.readSharedSetting(context, "access_token", ""));
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private boolean validatePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) || (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) || (shouldShowRequestPermissionRationale(CAMERA)) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(RECORD_AUDIO))) {
            cargardialogo();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO}, 100);

        }

        return false;
    }

    private void cargardialogo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Permisos Desactivados");
        builder.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, WRITE_EXTERNAL_STORAGE, CAMERA, RECORD_AUDIO}, 100);
                }

            }
        });
        builder.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {

            if (grantResults.length == 5 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {


            } else {

                cargardialogo2();

            }

        }

    }

    private void cargardialogo2() {

        final CharSequence[] op = {"si", "no"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Desea configurar los permisos manualmente?");
        builder.setItems(op, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (op[which].equals("si")) {

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);

                } else {

                    Toast msj = Toast.makeText(MainActivity.this, "los permisos no fueron aceptados", Toast.LENGTH_LONG);
                    msj.show();
                    dialog.dismiss();

                }

            }
        });
        builder.show();

    }
}