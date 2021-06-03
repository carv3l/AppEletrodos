package com.example.eletrodos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class MedidasListAdapter extends AppCompatActivity {

    String uri_get_medidas ="https://eletrodos.herokuapp.com/api/medidas";

    public String g_id = "0";
    public String g_mail = "0";

    public boolean connStatus = true;

    SwipeRefreshLayout swipeRefreshLayout;

    MedidasRecyclerAdapter adapter;
    RecyclerView recyclerView;

    private ArrayList<String> mNotas = new ArrayList<>();
    private ArrayList<String> mRMedido= new ArrayList<>();
    private ArrayList<String> mResultado = new ArrayList<>();


    @Override
    protected void onResume() {

        Handler h =new Handler() ;
        Intent intent = getIntent();
        g_id = intent.getStringExtra("user_id");
        g_mail = intent.getStringExtra("user_mail");

        loadData(g_id);


        h.postDelayed(new Runnable() {

            public void run() {

                if(detectFile()){ //Detectar se existe o ficheiro
                    if (connStatus) { //Se existir Net
                        if (mailtoid(g_mail)) { //Detectar se está o id do user no ficheiro para fazer sync
                            showSnack();
                        }
                    }
                }
            }

        }, 2000);
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_adapter);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMedidas);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //  loadData(g_id);
               recyclerView.removeAllViews();
               // recyclerView.removeViewsInLayout(0,adapter.getItemCount());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void loadData(String id){


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
                    connStatus = true;


                    if (response.getString("data") == "null" ){

                        Toast.makeText(MedidasListAdapter.this, "ERRO, ESTE USER NÃO EXISTE", Toast.LENGTH_LONG).show();
                    }else {

                        for (int i=0; i < response.getJSONArray("data").length();i++){
                            JSONObject jObject = response.getJSONArray("data").getJSONObject(i);
                            mNotas.add(jObject.getString("nota"));
                            mRMedido.add(jObject.getString("r_medido"));
                            mResultado.add(jObject.getString("r_solo"));


                          //   Log.v("ResponseList","This"+ response.getJSONArray("data").getJSONObject(i));

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
                // Toast.makeText(MedidasListAdapter.this, "Erro No net "+ error, Toast.LENGTH_LONG).show();

                //Caso detecte que não há Internet

                if(error instanceof NetworkError) { //this spits true
                    //saveOffline(params);
                //    showToast("into here");
                    connStatus = false;
                    if (detectFile())
                        readfile();

                }


            }
        }
        );
        queue.add(getRequest);
        //  Log.d("List","onCreate: Started");
        //  initRecyclerView();



    }

    private void initRecyclerView(){


        adapter = new MedidasRecyclerAdapter(this,mNotas,mRMedido,mResultado);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void readfile(){

        String filename= getFilesDir()+"/temp.json";

        JSONObject jsonObject = null;//parseJSONFile(filename);

        JSONArray jsonArr= parseJSONFile(filename);

        // showToast(""+jsonObject);
        Log.d("Calcular","RESULT:"+jsonArr);

        try {
            for (int i = 0; i< jsonArr.length();i++){

            jsonObject = jsonArr.getJSONObject(i);

            mNotas.add(jsonObject.getString("notas"));
            mRMedido.add(jsonObject.getString("rmedido"));
            mResultado.add(jsonObject.getString("resultado"));
              //  Log.d("Calcular",""+jsonArr.get(i));


            }
        } catch (JSONException e) {
            Log.e("Calcular",""+e);
        }

        initRecyclerView();
        adapter.notifyDataSetChanged();
    }

    public static JSONArray parseJSONFile(String filename){

        JSONObject jobj = null;

        JSONArray jArr = null;

        String content = null;
        try {
            content = String.valueOf(Files.readAllLines(Paths.get(filename)));
            jArr = new JSONArray(content);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("Calcular",""+e);
        }
        return jArr;
    }

    private void showToast(String message){

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    public boolean detectFile(){

        String filename= getFilesDir()+"/temp.json";
        File f = new File(filename);
        if(f.exists() && !f.isDirectory()) {
            return true;
        }else
            return false;
    }

    public void mailtoid1(String mail){
        RequestQueue queue = Volley.newRequestQueue(this);
        String uri_get_user ="https://eletrodos.herokuapp.com/api/user/";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_user+mail, null,new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response){

                try {
                    JSONObject jobj = response.getJSONArray("data").getJSONObject(1);

                    showToast(""+jobj.getString("id_user"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MyApp","Err "+e);
                }
            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
            }
        }
        );
        queue.add(getRequest);
    }

    public boolean mailtoid(String mail) {

        boolean ismatch = false;

        String filename = getFilesDir() + "/temp.json";

        JSONObject jsonObject = null;//parseJSONFile(filename);

        JSONArray jsonArr = parseJSONFile(filename);

        // showToast(""+jsonObject);
        Log.d("Calcular", "RESULT:" + jsonArr);

        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                jsonObject = jsonArr.getJSONObject(i);

                if (jsonObject.getString("id_user").equals(mail)){
                    ismatch = true;
                }

            }
        } catch (JSONException e) {
            Log.e("Medidas", "" + e);
        }
        return ismatch;
    }

    private void showSnack(){

        final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Medidas Offline Encontradas , deseja sincronizar?", Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction("Sync", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your action method here
                sync(g_mail);
                snackBar.dismiss();
            }
        });
        snackBar.show();

    }

    public void sync(String mail) {



        String filename = getFilesDir() + "/temp.json";

        JSONObject jsonObject = null;//parseJSONFile(filename);

        JSONArray jsonArr = parseJSONFile(filename);

        // showToast(""+jsonObject);
        Log.d("Calcular", "RESULT:" + jsonArr);

        try {


            for (int i = 0; i < jsonArr.length(); i++) {
                jsonObject = jsonArr.getJSONObject(i);

                if (jsonObject.getString("id_user").equals(mail)){

                    HashMap<String, String> params = new HashMap<String, String>();

                    params.put("espacamento", jsonObject.getString("espacamento")); //Add the data you'd like to send to the server.
                    params.put("rmedido",jsonObject.getString("rmedido"));
                    params.put("resultado", jsonObject.getString("resultado")); //Converter o resultado para string para ser guardado
                    params.put("notas", jsonObject.getString("notas"));
                    params.put("id_user", g_id);

                    SaveMeasure(params);
                }

            }
        } catch (JSONException e) {
            Log.e("Medidas", "" + e);
        }

        deletefile(filename);

    }



    public void SaveMeasure(HashMap params){

        RequestQueue queue = Volley.newRequestQueue(this);
        String serverApi ="https://eletrodos.herokuapp.com/api/medidas";



        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, serverApi, new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){

                try {
                    String data = response.getJSONObject("data").toString();

                    showToast("Atualize Página");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Tag","Err "+e);
                }
            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
            }
        }
        );
        queue.add(getRequest);
    }

    public void deletefile(String filename){

        File myObj = new File(filename);
        if (myObj.delete()) {
            Log.d("Calcular","Deleted the file: " + myObj.getName());
        } else {
            Log.d("Calcular","Failed to delete the file.");
        }


    }

}