package com.example.eletrodos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;

public class ListAdapter extends AppCompatActivity {

    String uri_get_medidas ="https://eletrodos.herokuapp.com/api/medidas";

    private ArrayList<String> mNotas = new ArrayList<>();
    private ArrayList<String> mRMedido= new ArrayList<>();
    private ArrayList<String> mResultado = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_adapter);


        Intent intent = getIntent();
        String id = intent.getStringExtra("user_id");
    //    Toast.makeText(ListAdapter.this, "Id"+id, Toast.LENGTH_LONG).show();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);


        HashMap<String, String> params = new HashMap<String, String>();
   //     params.put("id_user",id); //Add the data you'd like to send to the server.
    //    params.put("espacamento", ""+0);
    //    params.put("r_solo", ""+0);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_medidas+"/"+id+"/0/0", new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {

               //     Toast.makeText(ListAdapter.this, "Response "+response, Toast.LENGTH_LONG).show();
                //    Log.v("ResponseList","Response"+response.getString("status"));
//                    Log.v("ResponseList","Response"+response);

                    if (response.getString("data") == "null" ){

                        Toast.makeText(ListAdapter.this, "ERRO, ESTE USER NÃO EXISTE", Toast.LENGTH_LONG).show();
                    }else {

                        for (int i=0; i < response.getJSONArray("data").length();i++){
                            JSONObject jObject = response.getJSONArray("data").getJSONObject(i);
                            mNotas.add(jObject.getString("nota"));
                            mRMedido.add(jObject.getString("r_medido"));
                            mResultado.add(jObject.getString("r_solo"));


                          //  Log.v("ResponseList","nota"+ jObject.getString("nota"));
                          //  Log.v("ResponseList","medido"+ jObject.getString("r_medido"));
                          //  Log.v("ResponseList","resultado"+ jObject.getString("r_solo"));

                         }
                        initRecyclerView();
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
                Toast.makeText(ListAdapter.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);
      //  Log.d("List","onCreate: Started");
    //  initRecyclerView();
    }

    private void initRecyclerView(){

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        MedidasRecyclerAdapter adapter = new MedidasRecyclerAdapter(this,mNotas,mRMedido,mResultado);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}