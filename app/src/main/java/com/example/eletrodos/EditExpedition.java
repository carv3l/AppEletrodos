package com.example.eletrodos;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eletrodos.databinding.ActivityEditExpeditionBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EditExpedition extends AppCompatActivity {

    EditText editTextNome;
    EditText editTextData;
    EditText editTextNotas;
    EditText editTextCoordenadas;

    Button SaveButton;

    String uri_get_expedicao ="https://eletrodos.herokuapp.com/api/expeditions";
    String id_Expedicao = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expedition);

        editTextNome = (EditText)findViewById(R.id.editExTexNome);
        editTextData= (EditText)findViewById(R.id.editExTexData);
        editTextNotas = (EditText)findViewById(R.id.editExTexNota);
        editTextCoordenadas = (EditText)findViewById(R.id.editExTexCoordenadas);

        SaveButton = findViewById(R.id.SaveExButton);



        Intent intent = getIntent();
        ArrayList<String> mDados = intent.getStringArrayListExtra("expedition_data");

        id_Expedicao = mDados.get(0);

        //Log.d("List","mDados"+mDados.get(1));
        //Log.d("List","mDados2"+mDados.get(2));
      //  Log.d("List","mDados3"+mDados.get(3));
      //  Log.d("List","mDados4"+mDados.get(4));

        editTextNome.setText(""+mDados.get(1));
        editTextData.setText(""+mDados.get(2));
        editTextNotas.setText(""+mDados.get(3));
        editTextCoordenadas.setText(""+mDados.get(4));


        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RequestQueue queue = Volley.newRequestQueue(EditExpedition.this);


                HashMap<String, String> params = new HashMap<String, String>();
                    params.put("ex_name",editTextNome.getText().toString()); //Add the data you'd like to send to the server.
                    params.put("ex_data", editTextData.getText().toString());
                    params.put("ex_coordenadas", editTextCoordenadas.getText().toString());
                    params.put("ex_notas", editTextNotas.getText().toString());


                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.PUT, uri_get_expedicao+"/"+id_Expedicao+"/0", new JSONObject(params),new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        try {

                                 Toast.makeText(EditExpedition.this, ""+response.getString("message"), Toast.LENGTH_LONG).show();
                                // Log.v("ResponseList", ""+response.getString("message"));


                            if (response.getString("message") == "null" ){

                                Toast.makeText(EditExpedition.this, "ERRO", Toast.LENGTH_LONG).show();
                            }else {

                                Intent myIntent = new Intent(EditExpedition.this, ExpeditionsListAdapter.class);
                                myIntent.putExtra("user_id", mDados.get(5)); //Optional parameters
                                EditExpedition.this.startActivity(myIntent);
                                finish();

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
                        Toast.makeText(EditExpedition.this, "Erro"+ error, Toast.LENGTH_LONG).show();
                    }
                }
                );
                queue.add(getRequest);
                //  Log.d("List","onCreate: Started");





            }
        });







    }

}