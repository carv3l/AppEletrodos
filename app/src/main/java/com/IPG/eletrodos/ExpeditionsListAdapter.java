package com.IPG.eletrodos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.IPG.eletrodos.ExpeditionRecyclerAdapter;
import com.IPG.eletrodos.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ExpeditionsListAdapter extends AppCompatActivity {

    SharedPreferences sp;

    RecyclerView recyclerView;
    ExpeditionRecyclerAdapter adapter;
    String id = "0";


    String uri_get_expedicao ="https://eletrodos.herokuapp.com/api/expeditions";

    private final ArrayList<String> mId = new ArrayList<>();
    private final ArrayList<String> mNome = new ArrayList<>();
    private final ArrayList<String> mData= new ArrayList<>();
    private final ArrayList<String> mNotas= new ArrayList<>();
    private final ArrayList<String> mCoordenadas= new ArrayList<>();
    private final ArrayList<String> mIduser = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_adapter);



        sp = getSharedPreferences("login",MODE_PRIVATE);

        showSnack();

        id = sp.getString("user_id","0");


        RequestQueue queue = Volley.newRequestQueue(this);


        HashMap<String, String> params = new HashMap<String, String>();


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_expedicao+"/0/"+ id, new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {

                    //     Toast.makeText(MedidasListAdapter.this, "Response "+response, Toast.LENGTH_LONG).show();
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

                   String id_to_delete = mId.get(position);

                    //Apagar da Lista Visualmente
                    mNome.remove(position);
                    mData.remove(position);
                    mNotas.remove(position);
                    mCoordenadas.remove(position);
                    mIduser.remove(position);
                    mId.remove(position);

                   deleteExpedition(id_to_delete); //Apagar no mongoDB


                    adapter.notifyItemRemoved(position);
                    recyclerView.removeViewAt(position);


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

          //  adapter.notifyItemChanged(position);
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

          new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ExpeditionsListAdapter.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("Apagar")
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ExpeditionsListAdapter.this,R.color.Yellow))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                    .addSwipeRightLabel("Editar")
                    .create()
                    .decorate();


            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ExpeditionRecyclerAdapter(this,mNome,mData,mNotas,mId,recyclerView);
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


    private void showToast(String message){

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showSnack(){

        final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Editar, deslizar para a direita.\nApagar, para a esquerda. Longo clique \n para carregar em Gráfico", Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your action method here
                snackBar.dismiss();
            }
        });

        //Altura do SnackBar
      //  Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout)snackBar.getView();
      //  layout.setMinimumHeight(1000);//your custom height.


        snackBar.show();

    }




}