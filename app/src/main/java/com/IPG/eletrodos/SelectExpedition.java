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

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectExpedition extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ArrayList<String> list_id_medidas = new ArrayList<>();


    private ArrayList<String> mId = new ArrayList<>();
    private ArrayList<String> mNome = new ArrayList<>();
    private ArrayList<String> mData= new ArrayList<>();
    private ArrayList<String> mNotas= new ArrayList<>();
    private ArrayList<String> mCoordenadas= new ArrayList<>();
    private ArrayList<String> mIduser = new ArrayList<>();



    SharedPreferences sp;


    Button createExpedition;
    Button associateExpedition;
    Spinner spinner;

    String expedition_id ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_expedition);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        Handler h =new Handler() ;
        Intent intent = getIntent();
        list_id_medidas = intent.getStringArrayListExtra("list_id_medidas");
        String user_id = intent.getStringExtra("user_id");


        loadExpeditions(user_id);


        List<String> categories = new ArrayList<String>();




       createExpedition = (Button) findViewById(R.id.createExButton);
       associateExpedition = (Button) findViewById(R.id.associateExButton);

       spinner = (Spinner)findViewById(R.id.spinner_Ex);
       spinner.setOnItemSelectedListener(this);


   //     categories.add("OI CUnt");
     //   categories.add("OI CUnt2");

        h.postDelayed(new Runnable() {

            public void run() {

                for(int i = 0;i<mNome.size();i++){
                    categories.add(mNome.get(i));
                }

              //  categories.add(mNome.get(0).toString());
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SelectExpedition.this, android.R.layout.simple_spinner_item,categories);



                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapter);
            }

        }, 2000);


        createExpedition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SelectExpedition.this, CriarExpedition.class);
                myIntent.putExtra("list_id_medidas",list_id_medidas); //Optional parameters
               SelectExpedition.this.startActivity(myIntent);
               finish();

            }
        });


        associateExpedition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                do {
                    associateExpedition(expedition_id,list_id_medidas.get(i));
                    i++;
                }while (i<list_id_medidas.size());

                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
       // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
         //   showToast(""+mId.get(position));
            expedition_id = mId.get(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void showToast(String message){

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private ArrayList<String> loadExpeditions(String user_id){
        RequestQueue queue = Volley.newRequestQueue(this);

        HashMap<String, String> params = new HashMap<String, String>();

        String uri_get_expedicao ="https://eletrodos.herokuapp.com/api/expeditions";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_expedicao+"/0/"+ user_id, new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {

                    //     Toast.makeText(MedidasListAdapter.this, "Response "+response, Toast.LENGTH_LONG).show();
                    //    Log.v("ResponseList","Response"+response.getString("status"));
//                    Log.v("ResponseList","Response"+response);

                    if (response.getString("data") == "null" ){

                        Toast.makeText(SelectExpedition.this, "ERRO,NÃO EXISTE NENHUMA EXPEDIÇÃO", Toast.LENGTH_LONG).show();
                    }else {

                        for (int i=0; i < response.getJSONArray("data").length();i++){
                            JSONObject jObject = response.getJSONArray("data").getJSONObject(i);
                            mId.add(jObject.getString("_id"));
                            mNome.add(jObject.getString("nome"));
                            //categories.add(jObject.getString("nome").toString());
                            mData.add(jObject.getString("data"));
                            mNotas.add(jObject.getString("notas"));
                            mCoordenadas.add(jObject.getString("coordenadas"));
                            mIduser.add(jObject.getString("id_user"));


                        }

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
                Toast.makeText(SelectExpedition.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);

        return mNome;
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

                        Toast.makeText(SelectExpedition.this, "ERRO,NÃO EXISTE NENHUMA EXPEDIÇÃO", Toast.LENGTH_LONG).show();
                    }else
                        showToast(response.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error","Err "+e);
                }




            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(SelectExpedition.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);

    }

}