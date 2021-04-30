package com.example.eletrodos;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class CalcularActivity extends AppCompatActivity {

    Button calculate;
    EditText spacing;
    EditText rmedido;
    private static final String TAG = "MyActivity";
    CardView cardViewresult;

    TextView TextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcular);

        calculate = findViewById(R.id.calculateButton);

        spacing = (EditText)findViewById(R.id.EditTextSpacing);
        rmedido = (EditText)findViewById(R.id.EditTextMedida);

        cardViewresult = (CardView)findViewById(R.id.card_view2);

        TextViewResult = (TextView)findViewById(R.id.TextViewResult);

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

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("spacing", spacing); //Add the data you'd like to send to the server.
        params.put("rsolo", medida);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, serverApi, new JSONObject(params),new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                    String data = "";
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


}