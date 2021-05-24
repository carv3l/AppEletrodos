package com.example.eletrodos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class CalcularActivity extends AppCompatActivity {

    Boolean status = true;
    String data = "";
    Button calculate;
    Button saveButton;
    EditText spacing;
    EditText rmedido;
    EditText notas;
    private static final String TAG = "MyActivity";
    CardView cardViewresult;
    LinearLayout mainLayout;

    TextView TextViewResult;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular);

        calculate = findViewById(R.id.calculateButton);
        saveButton = findViewById(R.id.SaveButton);

        spacing = (EditText)findViewById(R.id.EditTextSpacing);
        rmedido = (EditText)findViewById(R.id.EditTextMedida);

        cardViewresult = (CardView)findViewById(R.id.card_view2);
        TextViewResult = (TextView)findViewById(R.id.TextViewResult);

        mainLayout = (LinearLayout)findViewById(R.id.LinearLayoutMain);
        notas = (EditText)findViewById(R.id.EditTextNotas);


        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");
        builder = new AlertDialog.Builder(this);


        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String spacing_value = spacing.getText().toString();
                String measure_value = rmedido.getText().toString();


                PostCalculate(spacing_value, measure_value);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);



            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



              // Toast.makeText(CalcularActivity.this,user_id, Toast.LENGTH_SHORT).show();
                if (user_id.equals("0")){
                  //if(!Alert())
                   Alert();
                   //   Toast.makeText(CalcularActivity.this,""+ resukt , Toast.LENGTH_SHORT).show();
                }else
                    SaveMeasure(data,user_id);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);



            }
        });

    }


    public void PostCalculate(String  spacing,String medida){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String serverApi ="https://eletrodos.herokuapp.com/api/calculate";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("spacing", spacing); //Add the data you'd like to send to the server.
        params.put("rsolo", medida);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, serverApi, new JSONObject(params),new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){

                        try {
                                data = response.getString("data");
                           // Toast.makeText(CalcularActivity.this, "Response"+ response.getJSONObject("data"), Toast.LENGTH_LONG).show();
                           // Log.e(TAG,"Response"+response.getString("data"));
                            //Log.e(TAG,"Response"+response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,"Err "+e);
                        }

                        TextViewResult.setText(data+ "  KOhms");
                        cardViewresult.setVisibility(View.VISIBLE);


                    }
                    },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(CalcularActivity.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);

    }

   public void SaveMeasure(String result, String id){

       String spacing_value = spacing.getText().toString();
       String measure_value = rmedido.getText().toString();
       String nota = notas.getText().toString();

       RequestQueue queue = Volley.newRequestQueue(this);
       String serverApi ="https://eletrodos.herokuapp.com/api/medidas";

       HashMap<String, String> params = new HashMap<String, String>();
       params.put("espacamento", spacing_value); //Add the data you'd like to send to the server.
       params.put("rmedido", measure_value);
       params.put("resultado", result);
       params.put("notas", nota);
       params.put("id_user", id);



       JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, serverApi, new JSONObject(params),new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response){

               try {
                   data = response.getString("data");
                  // Toast.makeText(CalcularActivity.this, "Response"+ response.getJSONObject("data"), Toast.LENGTH_LONG).show();
                   Toast.makeText(CalcularActivity.this, "Medida Guardada com Sucesso!!! ", Toast.LENGTH_LONG).show();

                   ClearFields();

               } catch (JSONException e) {
                   e.printStackTrace();
                   Log.e(TAG,"Err "+e);
               }
           }
       },new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
           @Override
           public void onErrorResponse(VolleyError error) {
               //This code is executed if there is an error.
               Toast.makeText(CalcularActivity.this, "Erro"+ error, Toast.LENGTH_LONG).show();
           }
       }
       );
       queue.add(getRequest);







   }




   public void Alert1(){
     builder.setTitle("Atenção!!!")
               .setMessage("Está como convidado, pretende entrar como utilizador? Depois terá de fazer uma nova medida")
               .setCancelable(true)
               .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       finish();
                   }
               })
               .setNegativeButton("Não", new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();

                      status = false;
                   }
               }).show();

   }

    public void Alert()
    {
                builder.setTitle("Atenção!!!")
                        .setMessage("Está como convidado, pretende entrar como utilizador? Depois terá de fazer uma nova medida")
                        .setCancelable(true)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                SaveMeasure(data,"0");
                            }
                }).show();

    }





   public void ClearFields(){

        spacing.setText("");
        rmedido.setText("");
        notas.setText("");
        cardViewresult.setVisibility(View.GONE);
        TextViewResult.setText("");

   }







}