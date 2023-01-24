package com.abogados.proyectog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventForm extends AppCompatActivity {
    LinearLayout llContainer;
    Button btnSave;

    Util util;
    Integer user_id;
    String email, token;
    Integer form_id;
    Integer parent_id;
    Generator generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        llContainer = findViewById(R.id.llContainer);
        btnSave = findViewById(R.id.btnSave);

        util = new Util(EventForm.this);
        Cursor cursor = util.getSession();
        user_id = cursor.getInt(1);
        email = cursor.getString(2);
        token = cursor.getString(3);

        Bundle selection = this.getIntent().getExtras();
        form_id = selection.getInt("form_id");
        parent_id = selection.getInt("parent_id");

        generator = new Generator(EventForm.this, llContainer, parent_id, form_id, user_id);

        eventForm("event-form");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (generator.validate()) {
                    //sendEventForm("event-form");
                    saveData();
                } else {
                    util.completeData();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem User = menu.findItem(R.id.userEmail);
        User.setTitle(email);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:

                logout("logout");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void eventForm(String direction) {

        String url = getString(R.string.url) + "/" + direction + "/" + form_id;

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

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.w("response", "" + response);
                try {
                    if (response.getInt("code") == 200) {
                        JSONObject form = response.getJSONObject("data");
                        String name = form.getString("name");
                        String description = form.getString("description");
                        generator.createdTitle(name);
                        generator.createdText(description);
                        JSONArray questions = form.getJSONArray("questions");
                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject question = questions.getJSONObject(i);
                            Integer id = question.getInt("id");
                            String questionText = question.getString("name");
                            int mandatory = question.getInt("mandatory");
                            JSONArray options = question.getJSONArray("options");

                            switch (question.getInt("type_id")) {
                                case 1:
                                    generator.createdQuestion(questionText, mandatory);
                                    break;
                                case 2:
                                    generator.createdText(questionText);
                                    break;
                                case 3:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdEditText(id, mandatory);
                                    break;
                                case 4:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdEditTextMultiline(id, mandatory);
                                    break;
                                case 5:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdSpinner(id, options);
                                    break;
                                case 6:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdTextViewDate(id, mandatory);
                                    break;
                                case 7:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdTextViewHour(id, mandatory);
                                    break;
                                case 8:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdImageView(id, mandatory);
                                    break;
                                case 9:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdSignature(id, mandatory);
                                    break;
                                case 10:
                                    generator.createdQuestion(questionText, mandatory);
                                    generator.createdSpinnerSpecial(id, options);
                                    break;
                            }
                        }
                    } else {
                        Toast.makeText(EventForm.this, "", Toast.LENGTH_LONG).show();
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
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void sendEventForm(String direction) {
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

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, generator.eventForm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.w("response", "" + response);
                try {
                    if (response.getInt("code") == 200) {
                        Toast.makeText(EventForm.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EventForm.this, "", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(EventForm.this, Events.class);
                startActivity(intent);
                finish();
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
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == generator.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView imageView = findViewById(generator.imageId);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400, 400);
            imageView.setLayoutParams(layoutParams);

            Uri imageUri = Uri.parse(generator.currentPhotoPath);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = 457;
                int newHeight = 305;

                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        width, height, matrix, true);

                generator.imageObjectSelected.setImage(resizedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logout(String direction) {

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

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.w("response", "" + response);
                try {
                    if (response.getInt("code") == 200) {
                        util.closeSession(email);
                        Toast.makeText(EventForm.this, "", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(EventForm.this, "", Toast.LENGTH_LONG).show();
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
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public void saveData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage("Los datos no podrán ser cambiados una vez se guarde la información");
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendEventForm("event-form");
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EventForm.this, Forms.class);
        startActivity(intent);
        finish();
    }
}