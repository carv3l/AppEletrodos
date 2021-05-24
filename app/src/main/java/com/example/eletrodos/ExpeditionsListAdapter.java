package com.example.eletrodos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpeditionsListAdapter extends AppCompatActivity {

    String uri_get_expedicao ="https://eletrodos.herokuapp.com/api/expeditions";

    private ArrayList<String> mNome = new ArrayList<>();
    private ArrayList<String> mData= new ArrayList<>();
    private ArrayList<String> mNotas= new ArrayList<>();


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

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_expedicao, new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {

               //     Toast.makeText(ListAdapter.this, "Response "+response, Toast.LENGTH_LONG).show();
                //    Log.v("ResponseList","Response"+response.getString("status"));
//                    Log.v("ResponseList","Response"+response);

                    if (response.getString("data") == "null" ){

                        Toast.makeText(ExpeditionsListAdapter.this, "ERRO,NÃO EXISTE NENHUMA EXPEDIÇÃO", Toast.LENGTH_LONG).show();
                    }else {

                        for (int i=0; i < response.getJSONArray("data").length();i++){
                            JSONObject jObject = response.getJSONArray("data").getJSONObject(i);
                            mNome.add(jObject.getString("nome"));
                            mData.add(jObject.getString("data"));
                            mNotas.add(jObject.getString("notas"));


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
                Toast.makeText(ExpeditionsListAdapter.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);
      //  Log.d("List","onCreate: Started");
    //  initRecyclerView();
    }

    private void initRecyclerView(){

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ExpeditionRecyclerAdapter adapter = new ExpeditionRecyclerAdapter(this,mNome,mData,mNotas);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}