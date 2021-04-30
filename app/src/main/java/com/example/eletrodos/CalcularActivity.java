package com.example.eletrodos;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class CalcularActivity extends AppCompatActivity {

    Button calculate;
    EditText spacing;
    EditText rmedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular);

        calculate = findViewById(R.id.calculateButton);

        spacing = (EditText)findViewById(R.id.EditTextSpacing);
        rmedido = (EditText)findViewById(R.id.EditTextMedida);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String spacing_value = spacing.getText().toString();
                String measure_value = rmedido.getText().toString();


                PostCalculate(spacing_value, measure_value);



            }
        });

    }


    public void PostCalculate(String  spacing,String medida){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String serverApi ="https://eletrodos.herokuapp.com/api/calculate";

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, serverApi, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



//                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
 //               intent.putExtra("dados",response);
  //              startActivity(intent);

                Toast.makeText(CalcularActivity.this, "Sucesso:"+ response, Toast.LENGTH_LONG).show();

                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(CalcularActivity.this, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("spacing", spacing); //Add the data you'd like to send to the server.
                MyData.put("rsolo", medida);
                return MyData;
            }
        };
        queue.add(MyStringRequest);
        //

    }


}