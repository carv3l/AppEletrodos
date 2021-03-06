package com.IPG.eletrodos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.IPG.eletrodos.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    String uri_post_login ="https://eletrodos.herokuapp.com/api/users";

    Button login ;
    EditText mail;
    EditText password;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("login", MODE_PRIVATE);

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
                String user_email= "";
                Intent returnIntent = new Intent();
                try {
                    Log.e("ResponseLogin","Response"+response.getString("status"));
                    Log.e("ResponseLogin","Response"+response);

                    if (response.getString("data") == "null" ){

                        Toast.makeText(LoginActivity.this, "ERRO, ESTE USER N??O EXISTE", Toast.LENGTH_LONG).show();
                    }else {
                        user_id = response.getJSONObject("data").getString("_id");
                        user_name = response.getJSONObject("data").getString("name");
                        user_email = response.getJSONObject("data").getString("email");

                      // Toast.makeText(LoginActivity.this, "BEM VINDO "+ user_id, Toast.LENGTH_LONG).show();


                        Log.e("ResponseLogin", "NAME: "+user_name);

                        sp.edit().putString("user_id",user_id).apply();
                        sp.edit().putString("user_name",user_name).apply();
                        sp.edit().putString("user_email",user_email).apply();
                        sp.edit().putBoolean("logged",true).apply();


                        returnIntent.putExtra("r_name", user_name);
                        returnIntent.putExtra("r_id", user_id);
                        returnIntent.putExtra("r_mail",user_email);
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