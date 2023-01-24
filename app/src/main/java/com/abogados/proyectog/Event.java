package com.abogados.proyectog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Event extends AppCompatActivity {
    LinearLayout llContainer;
    Button btnNext;
    Button btnFinish;

    Util util;
    Integer user_id;
    String email, token;
    Integer event_id;
    Generator generator;
    Integer form_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        llContainer = findViewById(R.id.llContainer);
        btnNext = findViewById(R.id.btnNext);
        btnFinish = findViewById(R.id.btnFinish);

        util = new Util(Event.this);
        Cursor cursor = util.getSession();
        user_id = cursor.getInt(1);
        email = cursor.getString(2);
        token = cursor.getString(3);

        Bundle selection = this.getIntent().getExtras();
        event_id = selection.getInt("event_id");

        generator = new Generator(Event.this, llContainer, 0, 0, user_id);

        event("event");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Event.this, ChildForms.class);
                intent.putExtra("form_id", form_id);
                intent.putExtra("parent_id", event_id);
                startActivity(intent);
                finish();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizeEvent("event");
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

    private void event(String direction) {

        String url = getString(R.string.url) + "/" + direction + "/" + event_id;

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
                        String name = form.getString("form_name");
                        String description = form.getString("form_description");
                        form_id = form.getInt("form_id");
                        int form_child = form.getInt("form_child");
                        if (form_child == 1) {
                            btnNext.setVisibility(View.VISIBLE);
                        }
                        generator.createdTitle(name);
                        generator.createdText(description);
                        JSONArray questions = form.getJSONArray("questions");
                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject question = questions.getJSONObject(i);
                            String questionText = question.getString("name");
                            String answer = question.getString("answer");

                            if (question.getInt("type_id") == 1) {
                                generator.createdQuestion(questionText, 0);
                            }

                            if (question.getInt("type_id") == 2) {
                                generator.createdText(questionText);
                            }

                            if (question.getInt("type_id") > 2 && question.getInt("type_id") < 8) {
                                generator.createdQuestion(questionText, 0);
                                generator.createdText(answer);
                            }

                            if (question.getInt("type_id") == 8 || question.getInt("type_id") == 9) {
                                generator.createdQuestion(questionText, 0);
                                generator.createdImage(answer);
                            }

                            if (question.getInt("type_id") == 10) {
                                generator.createdQuestion(questionText, 0);
                                generator.createdText(answer);
                            }
                        }
                    } else {
                        Toast.makeText(Event.this, "", Toast.LENGTH_LONG).show();
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

    private void finalizeEvent(String direction) {

        String url = getString(R.string.url) + "/" + direction + "/" + event_id;

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

        jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.w("response", "" + response);
                try {
                    if (response.getInt("code") == 200) {
                        Toast.makeText(Event.this, "Se finalizo correctamente", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Event.this, "", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Event.this, Events.class);
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

        requestQueue.add(jsonObjectRequest);
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
                        Toast.makeText(Event.this, "", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(Event.this, "", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Event.this, Events.class);
        startActivity(intent);
        finish();
    }
}