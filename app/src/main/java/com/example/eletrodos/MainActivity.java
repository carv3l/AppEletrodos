package com.example.eletrodos;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    String result_user_id;
    CardView cardCalcular;
    CardView cardMedidas;
    CardView cardLogin;
    CardView cardExpeditions;
    CardView cardSettings;
    CardView cardLogout;
    TextView txUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);




        result_user_id = "0";

        cardCalcular = findViewById(R.id.cardCalculate);
        cardMedidas = findViewById(R.id.cardMedidas);
        cardLogin = findViewById(R.id.cardLogin);
        cardExpeditions = findViewById(R.id.cardExpeditions);
        cardSettings = findViewById(R.id.cardSettings);
        cardLogout = findViewById(R.id.cardLogout);
        txUsername = (TextView)findViewById(R.id.textViewUserName);

        cardCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //showToast("Home Clicked");

                Intent myIntent = new Intent(MainActivity.this, CalcularActivity.class);
                myIntent.putExtra("user_id", result_user_id); //Optional parameters
                MainActivity.this.startActivity(myIntent);

            }
        });
        cardMedidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // showToast("Chat Clicked");

                Intent myIntent = new Intent(MainActivity.this, ListAdapter.class);
                myIntent.putExtra("user_id", result_user_id); //Optional parameters
                MainActivity.this.startActivity(myIntent);


            }
        });
        cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                //  myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivityForResult(myIntent, 0);
             //   showToast("Profile Clicked");

            }
        });
        cardExpeditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MainActivity.this, ExpeditionsListAdapter.class);
                myIntent.putExtra("user_id", result_user_id); //Optional parameters
                MainActivity.this.startActivity(myIntent);



                // showToast("Widget Clicked");

            }
        });
        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Settings Clicked");

            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Logged Out Clicked");

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == LoginActivity.RESULT_OK){
                String result_name =  data.getStringExtra("r_name");
                result_user_id =  data.getStringExtra("r_id");
                //showToast("DONO É: "+ result);
                txUsername.setText(""+result_name);
            }
            if (resultCode == LoginActivity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


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
        wakeupApp();
    }




    private void wakeupApp(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String serverApi ="https://eletrodos.herokuapp.com/api/";

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, serverApi, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                String data = "";
                try {
                    data = response.getString("status");
                    // Toast.makeText(CalcularActivity.this, "Response"+ response.getJSONObject("data"), Toast.LENGTH_LONG).show();
                     Log.e("MyApp","Response é: "+response.getString("status"));
                    //Log.e(TAG,"Response"+response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("MyApp","Err "+e);
                }


            }
        },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(MainActivity.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}