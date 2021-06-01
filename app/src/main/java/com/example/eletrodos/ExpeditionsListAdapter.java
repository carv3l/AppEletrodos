package com.example.eletrodos;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ExpeditionsListAdapter extends AppCompatActivity {

    RecyclerView recyclerView;
    ExpeditionRecyclerAdapter adapter;
    String id = "0";


    String uri_get_expedicao ="https://eletrodos.herokuapp.com/api/expeditions";

    private ArrayList<String> mId = new ArrayList<>();
    private ArrayList<String> mNome = new ArrayList<>();
    private ArrayList<String> mData= new ArrayList<>();
    private ArrayList<String> mNotas= new ArrayList<>();
    private ArrayList<String> mCoordenadas= new ArrayList<>();
    private ArrayList<String> mIduser = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_adapter);


        Intent intent = getIntent();
        id = intent.getStringExtra("user_id");
    //    Toast.makeText(ListAdapter.this, "Id"+id, Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(this);


        HashMap<String, String> params = new HashMap<String, String>();
        //     params.put("id_user",id); //Add the data you'd like to send to the server.
        //    params.put("espacamento", ""+0);
        //    params.put("r_solo", ""+0);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_expedicao+"/0/"+ id, new JSONObject(params),new Response.Listener<JSONObject>() {
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
                            mId.add(jObject.getString("_id"));
                            mNome.add(jObject.getString("nome"));
                            mData.add(jObject.getString("data"));
                            mNotas.add(jObject.getString("notas"));
                            mCoordenadas.add(jObject.getString("coordenadas"));
                            mIduser.add(jObject.getString("id_user"));


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

                    //Apagar da Lista Visualmente
                    mNome.remove(position);
                    mData.remove(position);
                    mNotas.remove(position);

                    deleteExpedition(mId.get(position)); //Apagar no mongoDB

                    recyclerView.removeViewAt(position);
                    adapter.notifyItemRemoved(position);

                   // Toast.makeText(ExpeditionsListAdapter.this, "Size "+mId.size(), Toast.LENGTH_LONG).show();
                    break;
                case ItemTouchHelper.RIGHT: //SWIPE DA DIREITA PARA A ESQUERDA   --- EDITAR

                    ArrayList<String> mDados = new ArrayList<>();
                    mDados.add(mId.get(position));
                    mDados.add(mNome.get(position));
                    mDados.add(mData.get(position));
                    mDados.add(mNotas.get(position));
                    mDados.add(mCoordenadas.get(position));
                    mDados.add(mIduser.get(position));

                    adapter.notifyItemChanged(position);

                    Intent myIntent = new Intent(ExpeditionsListAdapter.this, EditExpedition.class);
                    myIntent.putExtra("expedition_data", mDados); //Optional parameters
                    ExpeditionsListAdapter.this.startActivity(myIntent);
                    finish();

                    break;

            }
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ExpeditionsListAdapter.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ExpeditionsListAdapter.this,R.color.Yellow))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .create()
                    .decorate();


            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ExpeditionRecyclerAdapter(this,mNome,mData,mNotas);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

      //  adapter.notifyDataSetChanged();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void deleteExpedition(String idExpedition){

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.DELETE, uri_get_expedicao+"/"+idExpedition+"/0", null,new Response.Listener<JSONObject>() {
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
                Toast.makeText(ExpeditionsListAdapter.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);

    }


}