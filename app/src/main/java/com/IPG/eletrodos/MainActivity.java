package com.IPG.eletrodos;

import android.content.DialogInterface;
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
import com.IPG.eletrodos.MedidasListAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    String result_user_id;
    String result_user_mail;
    CardView cardCalcular;
    CardView cardMedidas;
    CardView cardLogin;
    CardView cardExpeditions;
    CardView cardGrafico;
    CardView cardLogout;
    TextView txUsername;

    Boolean connStatus = true;
    SharedPreferences sp;
    AlertDialog.Builder builder;

    @Override
    protected void onResume() {
        super.onResume();
        if(sp.getBoolean("logged",false)){
            txUsername.setText(""+sp.getString("user_name","Convidado"));
        }else
            txUsername.setText("Convidado");
        wakeupApp();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);


        builder = new AlertDialog.Builder(this);

        result_user_id = "0";

        cardCalcular = findViewById(R.id.cardCalculate);
        cardMedidas = findViewById(R.id.cardMedidas);
        cardLogin = findViewById(R.id.cardLogin);
        cardExpeditions = findViewById(R.id.cardExpeditions);
        cardGrafico = findViewById(R.id.cardGrafico);
        cardLogout = findViewById(R.id.cardLogout);
        txUsername = (TextView)findViewById(R.id.textViewUserName);




        cardCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent myIntent = new Intent(MainActivity.this, com.IPG.eletrodos.CalcularActivity.class);
                MainActivity.this.startActivity(myIntent);

            }
        });
        cardMedidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showToast("Chat Clicked");

                Intent myIntent = new Intent(MainActivity.this, MedidasListAdapter.class);
                MainActivity.this.startActivity(myIntent);


            }
        });
        cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);

            }
        });
        cardExpeditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MainActivity.this, ExpeditionsListAdapter.class);
                MainActivity.this.startActivity(myIntent);

            }
        });
        cardGrafico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, GraficoActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSharedPreferences("login", MODE_PRIVATE).edit().clear().apply();
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);


            }
        });

    }

    private void showToast(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

///DECTECTAR SE APP ESTÁ ABERTA OU NÃO


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d("MyApp", "App in background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {

        Log.d("MyApp", "App in foreground");
     //   wakeupApp();
    }

    private void wakeupApp(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String serverApi ="https://eletrodos.herokuapp.com/api/";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, serverApi, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                String status = "";
                try {
                    status = response.getString("status");

                //    showToast(status);

                    if (status.equals("OK")){
                        if (!connStatus)
                            setOfflineMode(false);


                    }
                        //showToast("COM INTERNET");


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MyApp","Err "+e);
                }
            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error
             //  showToast("Sem Ligação à Internet");


                if (connStatus) {
           //         showToast("" + error.networkResponse);
                    setOfflineMode(true);
                  //  Alert("" + error.networkResponse);
                }
            }
        }
        );
        queue.add(getRequest);

    }




    public void Alert(String message)
    {

        builder.setTitle("Atenção!!!")
                .setMessage("Não tem ligação à Internet. \n Pretende usar a app no modo offline?"+message)
                .setCancelable(true)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();


                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();


                    }
                }).show();

    }

    public void setOfflineMode(boolean mode){

        if (mode){
            connStatus = false;

            cardLogin.setEnabled(false);
            cardLogin.setCardBackgroundColor(getColor(R.color.LightGray));

            cardExpeditions.setEnabled(false);
            cardExpeditions.setCardBackgroundColor(getColor(R.color.LightGray));

            cardGrafico.setEnabled(false);
            cardGrafico.setCardBackgroundColor(getColor(R.color.LightGray));

            cardLogout.setEnabled(false);
            cardLogout.setCardBackgroundColor(getColor(R.color.LightGray));



        }else if(!mode){

            connStatus = true;

            cardLogin.setEnabled(true);
            cardLogin.setCardBackgroundColor(getColor(R.color.white));

            cardExpeditions.setEnabled(true);
            cardExpeditions.setCardBackgroundColor(getColor(R.color.white));

            cardGrafico.setEnabled(true);
            cardGrafico.setCardBackgroundColor(getColor(R.color.white));

            cardLogout.setEnabled(true);
            cardLogout.setCardBackgroundColor(getColor(R.color.white));


        }




    }


}

