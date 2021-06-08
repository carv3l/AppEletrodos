package com.example.eletrodos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eletrodos.databinding.ActivityCriarExpeditionBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CriarExpedition extends AppCompatActivity {


    EditText createTextNome;
    EditText createTextData;
    EditText createTextNotas;
    EditText createTextCoordenadas;

    Button CreateButton;

    SharedPreferences sp;

    String uri_post_expedicao ="https://eletrodos.herokuapp.com/api/expeditions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_expedition);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        createTextNome = (EditText)findViewById(R.id.createExTexNome);
        createTextData= (EditText)findViewById(R.id.createExTexData);
        createTextNotas = (EditText)findViewById(R.id.createExTexNota);
        createTextCoordenadas = (EditText)findViewById(R.id.createExTexCoordenadas);

        CreateButton = findViewById(R.id.createExButton);

        Intent intent = getIntent();
        ArrayList<String> mDadosMedidas = intent.getStringArrayListExtra("list_id_medidas");




        CreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue queue = Volley.newRequestQueue(CriarExpedition.this);


                HashMap<String, String> params = new HashMap<String, String>();
                params.put("ex_name",createTextNome.getText().toString()); //Add the data you'd like to send to the server.
                params.put("ex_data", createTextData.getText().toString());
                params.put("ex_coordenadas", createTextCoordenadas.getText().toString());
                params.put("ex_notas", createTextNotas.getText().toString());
                params.put("ex_id_user", sp.getString("user_id","0"));


                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, uri_post_expedicao, new JSONObject(params),new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        try {

                            if (response.getString("message") == "null" ){

                                Toast.makeText(CriarExpedition.this, "ERRO", Toast.LENGTH_LONG).show();
                            }else {

                             //  Toast.makeText(CriarExpedition.this, ""+response.getJSONObject("data").getString("_id"), Toast.LENGTH_LONG).show();

                                JSONObject jObject = response.getJSONObject("data");


                                for (int i=0; i < mDadosMedidas.size();i++){
                                    associateExpedition(jObject.getString("_id"),mDadosMedidas.get(i));
                                }


                            }
                            Toast.makeText(CriarExpedition.this, ""+response.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Error","Err "+e);
                        }

                    }
                },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Toast.makeText(CriarExpedition.this, "Erro"+ error, Toast.LENGTH_LONG).show();
                    }
                }
                );
                queue.add(getRequest);
                //  Log.d("List","onCreate: Started");
                finish();

            }
        });

    }

    public void associateExpedition(String expedition_id,String id_medida){
        String uri_post_medidas ="https://eletrodos.herokuapp.com/api/medidas";

        RequestQueue queue = Volley.newRequestQueue(this);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("id_medida",id_medida); //
        params.put("id_expedicao", expedition_id); //Add the data you'd like to send to the server.
        params.put("type", ""+2);  //0 é passar de user para expedicao



        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.PUT, uri_post_medidas, new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {

                    if (response.getString("message") == "null" ){

                        Toast.makeText(CriarExpedition.this, "ERRO,NÃO EXISTE NENHUMA EXPEDIÇÃO", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error","Err "+e);
                }




            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(CriarExpedition.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);

    }

    private void showToast(String message){
        Toast.makeText(CriarExpedition.this, message, Toast.LENGTH_SHORT).show();
    }


}