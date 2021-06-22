package com.IPG.eletrodos;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MedidasListAdapter extends AppCompatActivity {

    String uri_get_medidas ="https://eletrodos.herokuapp.com/api/medidas";

    public String g_id = "0";
    public String g_mail = "0";

    public boolean connStatus = true;

    SharedPreferences sp;
    SwipeRefreshLayout swipeRefreshLayout;

    MedidasRecyclerAdapter adapter;
    RecyclerView recyclerView;

    private final ArrayList<String> mNotas = new ArrayList<>();
    private final ArrayList<String> mRMedido= new ArrayList<>();
    private final ArrayList<String> mResultado = new ArrayList<>();
    private final ArrayList<String> mId_Medida = new ArrayList<>();


    @Override
    protected void onResume() {

        Handler h =new Handler() ;

        g_id = sp.getString("user_id","0");
        g_mail = sp.getString("user_email","0");

        loadData(g_id);

// Isto atrasa 2 ms para que primeiro se receba uma resposta se há net ou não
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
        showSnackIntro();

        sp = getSharedPreferences("login",MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMedidas);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = null;
                recyclerView.refreshDrawableState();

                //loadData(g_id);
             //     recyclerView.removeViewsInLayout(0,adapter.getItemCount());
              //  adapter.notifyAll();
            //    adapter.notifyItemRangeChanged(0,adapter.getItemCount());
           //     recyclerView.destroyDrawingCache();
               // recyclerView.removeViewsInLayout(0,adapter.getItemCount());
              //  adapter.notifyDataSetChanged();
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

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_medidas+"/"+id, new JSONObject(params),new Response.Listener<JSONObject>() {
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
                            mId_Medida.add(jObject.getString("_id"));


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
        adapter = new MedidasRecyclerAdapter(this,mNotas,mRMedido,mResultado,mId_Medida,g_id);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
            mId_Medida.add(jsonObject.getString("temp_id"));
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
        return f.exists() && !f.isDirectory();
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

    private void showSnackIntro(){

            final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Apagar, deslizar para a esquerda \nLongo Clique para associar expedição", Snackbar.LENGTH_INDEFINITE);

            snackBar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
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

    // ----------------------  SWIPE DIREITA E ESQUERDA -------------------
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {


            int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT: //SWIPE DA ESQUERDA PARA A DIREITA --- APAGAR
                    // Toast.makeText(ExpeditionsListAdapter.this, "Ola "+mId.get(position), Toast.LENGTH_LONG).show();

                    String id_to_delete = mId_Medida.get(position);

                    //Apagar da Lista Visualmente
                    mRMedido.remove(position);
                    mResultado.remove(position);
                    mNotas.remove(position);

                    deleteMedida(id_to_delete); //Apagar no mongoDB

                    adapter.notifyItemRemoved(position);
                    recyclerView.removeViewAt(position);

                    break;
                case ItemTouchHelper.RIGHT: //SWIPE DA DIREITA PARA A ESQUERDA   --- EDITAR

                    break;

            }

        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MedidasListAdapter.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("Apagar")
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(MedidasListAdapter.this,R.color.Yellow))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .addSwipeRightLabel("Editar")
                    .create()
                    .decorate();


            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteMedida(String id_medida){

        String uri_apagar_expedicao = "https://eletrodos.herokuapp.com/api/medidas/";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.DELETE, uri_apagar_expedicao+id_medida, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                String data = "";
                try {
                    data = response.getString("data");
                    //Toast.makeText(ExpeditionsListAdapter.this, "Response"+ response.getJSONObject("data"), Toast.LENGTH_LONG).show();
                    //Log.e("MyApp","Response é: "+response.getString("status"));
                    Log.e("List","Response"+response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MyApp","Err "+e);
                }


            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.

                if(error instanceof NetworkError) { //this spits true
                    deleteINfile(id_medida);
                }

            }
        }
        );
        queue.add(getRequest);

    }

    public void deleteINfile(String temporary_id) {

        String filename= getFilesDir()+"/temp.json";

        JSONObject jsonObject = null;//parseJSONFile(filename);

        JSONArray jsonArr= parseJSONFile(filename);

        // showToast(""+jsonObject);
        Log.d("Calcular","RESULT:"+jsonArr);

        try {
            for (int i = 0; i< jsonArr.length();i++){

                jsonObject = jsonArr.getJSONObject(i);


                if(jsonObject.getString("temp_id").equals(temporary_id)){

                    jsonArr.remove(i);

                }

            }
        } catch (JSONException e) {
            Log.e("Calcular",""+e);
        }
        HashMap<String,String> obj = new HashMap<>();


try {
    for (int i = 0; i < jsonArr.length(); i++) {
        obj.put("resultado",jsonArr.getJSONObject(i).getString("resultado"));
        obj.put("temp_id",jsonArr.getJSONObject(i).getString("temp_id"));
        obj.put("notas",jsonArr.getJSONObject(i).getString("notas"));
        obj.put("id_user",jsonArr.getJSONObject(i).getString("id_user"));
        obj.put("espacamento",jsonArr.getJSONObject(i).getString("espacamento"));
        obj.put("rmedido",jsonArr.getJSONObject(i).getString("rmedido"));

    }
}catch (Exception e){

    e.printStackTrace();
}

        overWriteFile(obj); //Apaga a medida no ficheiro ao sobreescrever

        initRecyclerView();
        adapter.notifyDataSetChanged();
    }

    public void overWriteFile(HashMap obj){

        String filename= getFilesDir()+"/temp.json";

        BufferedWriter writer = null;

        BufferedReader reader = null;

        //Escreve No Ficheiro JSON
        try {
            writer = new BufferedWriter(new FileWriter(filename,false));

                writer.write(""+obj);

            writer.close();
            showToast("Medida Apagada");

        } catch (IOException e) {
            e.printStackTrace();
            // showToast("Erro"+e);
        }

    }







}