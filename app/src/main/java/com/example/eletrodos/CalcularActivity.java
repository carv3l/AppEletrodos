package com.example.eletrodos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.util.Log;
;
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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class CalcularActivity extends AppCompatActivity {

    Boolean status = true;
    int convertedcfinal = 0;
    Button calculate;
    Button saveButton;
    EditText spacing;
    EditText rmedido;
    EditText notas;
    private static final String TAG = "MyActivity";
    CardView cardViewresult;
    LinearLayout mainLayout;

    TextView TextViewResult;

    HashMap<String, String> params = new HashMap<String, String>();


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

                String spacing_value = spacing.getText().toString();
                String measure_value = rmedido.getText().toString();
                String nota = notas.getText().toString();

                params.put("espacamento", spacing_value); //Add the data you'd like to send to the server.
                params.put("rmedido", measure_value);
                params.put("resultado", String.valueOf(convertedcfinal)); //Converter o resultado para string para ser guardado
                params.put("notas", nota);


              // Toast.makeText(CalcularActivity.this,user_id, Toast.LENGTH_SHORT).show();
                if (user_id.equals("0")){
                  //if(!Alert())
                   Alert();
                   //   Toast.makeText(CalcularActivity.this,""+ resukt , Toast.LENGTH_SHORT).show();
                }else
                    SaveMeasure(user_id);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

            }
        });

    }

    public void PostCalculate(String spacing,String medida){

        double cfinal = (2*Math.PI*Double.parseDouble(spacing)*Double.parseDouble(medida));

        convertedcfinal = (int )Math.round(cfinal*10)/10;

        TextViewResult.setText(convertedcfinal+ "  Ohms");
        cardViewresult.setVisibility(View.VISIBLE);

    }

   public void SaveMeasure(String id){



       RequestQueue queue = Volley.newRequestQueue(this);
       String serverApi ="https://eletrodos.herokuapp.com/api/medidas";

       params.put("id_user", id);

       JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, serverApi, new JSONObject(params),new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response){

               try {


                   String data = response.getJSONObject("data").toString();

                  // Toast.makeText(CalcularActivity.this, "Response"+ response.getJSONObject("data"), Toast.LENGTH_LONG).show();
                   Toast.makeText(CalcularActivity.this, "Medida Guardada com Sucesso!!!", Toast.LENGTH_LONG).show();

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

               //Detecta se Tem internet para guardar online
           //    Toast.makeText(CalcularActivity.this, "Erro"+ (error instanceof NetworkError), Toast.LENGTH_LONG).show();

               if(error instanceof NetworkError) { //this spits true
                  Alert1();
                 //  saveOffline(params);
               }
               }
       }
       );
       queue.add(getRequest);

   }

    public void Alert()
    {
        isNetworkAvailable(); // Ver se há ligação à internet
                builder.setTitle("Atenção!!!")
                        .setMessage("Está como convidado, pretende entrar como utilizador?\n Depois terá de fazer uma nova medida")
                        .setCancelable(true)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isNetworkAvailable();
                                if(!connStatus)
                                    Alert1();
                                else
                                finish();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                isNetworkAvailable();
                                if(!connStatus){

                                    saveOffline(params,"0");
                                }else
                                    SaveMeasure("0");
                            }
                }).show();

    }
    public void Alert1()
    {

        final EditText input = new EditText(this);


        builder.setTitle("Atenção!!!")
                .setMessage("Não tem Internet \nIntroduza o email para guardar offline")
                .setCancelable(true)
                .setView(input)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        saveOffline(params,value);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
                ).show();

    }




    public void saveOffline(HashMap params,String id){
        params.put("id_user", id);

        String temp_id = params.get("espacamento").toString()+params.get("rmedido").toString()+params.get("notas").toString();
        showToast("Temp id"+temp_id);
        params.put("temp_id",temp_id);

       JSONObject JsonParams = new JSONObject(params);

       String filename= getFilesDir()+"/temp.json";

       BufferedWriter writer = null;

       BufferedReader reader = null;



       //Se o ficheiro não existe, define que está vazio para assim acrescentar a virgula entre JSONObjects
       boolean isempty = true;
       try {
           reader = new BufferedReader(new FileReader(filename));

           if (reader.readLine() == null && new File(filename).length() == 0) {
               Log.d("Calcular","No errors, and file empty");
               reader.close();
               isempty = true;
           } else
               isempty = false;

       }catch (NoSuchFileException e){
           isempty = true;
       } catch (IOException e) {
           isempty = true;;
       }

        //Escreve No Ficheiro JSON
       try {
           writer = new BufferedWriter(new FileWriter(filename,true));
           if (isempty)
          writer.append(JsonParams.toString());
           else
               writer.append(","+JsonParams.toString());

         writer.close();
         ClearFields();
         showToast("Medida Guardada Offline");

       } catch (IOException e) {
           e.printStackTrace();
          // showToast("Erro"+e);
       }

//readfile();
   }


    public void readfile(){

        String filename= getFilesDir()+"/temp.json";

      //  JSONObject jsonObject = parseJSONFile(filename);

        JSONArray jsonArr= parseJSONFile(filename);

       // showToast(""+jsonObject);
        Log.d("Calcular","RESULT:"+jsonArr);

try {
    for (int i = 0; i< jsonArr.length();i++){


        Log.d("Calcular",""+jsonArr.get(i));


    }
} catch (JSONException e) {
    Log.e("Calcular",""+e);
}


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


    public void CustomAlert(String message)
    {
        builder.setTitle("Atenção!!!")
                .setMessage(message)
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
                        SaveMeasure("0");
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

    private void showToast(String message){

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }





    boolean connStatus = true;
    private void isNetworkAvailable(){
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
                        connStatus = true;
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
                //This code is executed if there is an error.

                //Detecta se Tem internet para guardar online
                //    Toast.makeText(CalcularActivity.this, "Erro"+ (error instanceof NetworkError), Toast.LENGTH_LONG).show();

               // showToast("IN HERE");
                if(error instanceof NetworkError)  //this spits true
                    connStatus = false;

            }
        }
        );
        queue.add(getRequest);

//        showToast(""+connStatus);

    }




}


