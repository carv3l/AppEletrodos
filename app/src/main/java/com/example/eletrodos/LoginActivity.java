package com.example.eletrodos;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    String serverApi ="https://eletrodos.herokuapp.com/api/calculate";
    String uri_post_login ="https://eletrodos.herokuapp.com/api/users";

    Button login ;
    EditText mail;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail = (EditText)findViewById(R.id.EditTextMailLogin);
        password = (EditText)findViewById(R.id.EditTextPasswordLogin);
        login = findViewById(R.id.LoginButton);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUser(mail.getText().toString(),password.getText().toString());
            }
        });
    }

    public void AuthUser(String mail,String password){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", mail); //Add the data you'd like to send to the server.
        params.put("password", password);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, uri_post_login, new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                String user_name = "";
                String user_id = "";
                Intent returnIntent = new Intent();
                try {
                    Log.e("ResponseLogin","Response"+response.getString("status"));
                    Log.e("ResponseLogin","Response"+response);

                    if (response.getString("data") == "null" ){

                        Toast.makeText(LoginActivity.this, "ERRO, ESTE USER NÃO EXISTE", Toast.LENGTH_LONG).show();
                    }else {
                        user_id = response.getJSONObject("data").getString("_id");
                        user_name = response.getJSONObject("data").getString("name");

                      // Toast.makeText(LoginActivity.this, "BEM VINDO "+ user_id, Toast.LENGTH_LONG).show();


                        Log.e("ResponseLogin", "NAME: "+user_name);

                        returnIntent.putExtra("r_name", user_name);
                        returnIntent.putExtra("r_id", user_id);
                        setResult(LoginActivity.RESULT_OK,returnIntent);
                        finish();

                    }

                  //  Log.e("ResponseLogin","Response"+response);
                    //



                    // Log.e(TAG,"Response"+response.getString("data"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error","Err "+e);
                }

             //   TextViewResult.setText(data+ "  KOhms");
             //   cardViewresult.setVisibility(View.VISIBLE);


            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(LoginActivity.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);

    }



}