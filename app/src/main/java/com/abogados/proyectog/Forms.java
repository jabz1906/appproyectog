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
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Forms extends AppCompatActivity {
    ListView lvForms;

    ArrayList<FormObject> items = new ArrayList<>();

    Util util;
    Integer user_id;
    String email, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        lvForms = findViewById(R.id.lvForms);

        util = new Util(Forms.this);
        Cursor cursor = util.getSession();
        user_id = cursor.getInt(1);
        email = cursor.getString(2);
        token = cursor.getString(3);

        forms("form");

        lvForms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FormObject form = (FormObject) adapterView.getItemAtPosition(i);
                Integer form_id = form.getId();

                Intent intent = new Intent(Forms.this, EventForm.class);
                intent.putExtra("form_id", form_id);
                intent.putExtra("parent_id", 0);
                startActivity(intent);
                finish();
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

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void forms(String direction) {

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
                        JSONArray forms = response.getJSONArray("data");
                        for (int i = 0; i < forms.length(); i++) {
                            JSONObject form = forms.getJSONObject(i);
                            Integer id = form.getInt("id");
                            Integer parent_id = null;
                            if(!form.getString("parent_id").equals("null"))
                            {
                                 parent_id = form.getInt("parent_id");
                            }
                            String color = "#0572C2";
                            String name = form.getString("name");
                            String description = form.getString("description");
                            String image = form.getString("image");

                            items.add(new FormObject(id, parent_id, color, name, description, image));
                        }
                        FormsAdapter formsAdapter = new FormsAdapter(Forms.this, items);
                        lvForms.setAdapter(formsAdapter);
                    } else {
                        Toast.makeText(Forms.this, "", Toast.LENGTH_LONG).show();
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Forms.this, Events.class);
        startActivity(intent);
        finish();
    }
}