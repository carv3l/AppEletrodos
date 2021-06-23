package com.IPG.eletrodos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.IPG.eletrodos.databinding.FragmentFirstBinding;
import com.IPG.eletrodos.databinding.FragmentSecondBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SecondFragment extends Fragment implements OnChartValueSelectedListener {

    private FragmentSecondBinding binding;



    private LineChart lineChart;
    public Context mContext;
    SharedPreferences sp;
    LoadingDialog loadingDialog;


    private final ArrayList<String> mEspacamento = new ArrayList<>();
    private final ArrayList<String> mRMedido= new ArrayList<>();
    private final ArrayList<String> mResultado = new ArrayList<>();
    private final ArrayList<String> mNotas = new ArrayList<>();
    private final ArrayList<Float> mdesvio = new ArrayList<>();
//    private ArrayList<String> mId_Medida = new ArrayList<>();



    YAxis leftAxis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog= new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        sp = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
        getdata();

        lineChart = view.findViewById(R.id.line_chartDesvio);
        lineChart.setOnChartValueSelectedListener(this);

        lineChart.setBackgroundColor(Color.WHITE);

        lineChart.setTouchEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
        //lineChart.setBackgroundColor(Color.LTGRAY);

        lineChart.animateX(1500);


        //get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        // l.setTypeface(tfLight);
        l.setTextSize(14f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        Description des = lineChart.getDescription();

        des.setTextSize(16f);
        des.setText("");

//        l.setYOffset(11f);

        XAxis xAxis = lineChart.getXAxis();
        //xAxis.setTypeface(tfLight);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //Mostrar em baixo
        xAxis.setTextSize(16f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f); //Numeros Inteiros

        leftAxis = lineChart.getAxisLeft();

        // leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setGranularityEnabled(false);


    }


    private void setData(int count, float range) {


        ArrayList<Entry> values_desvio = new ArrayList<>();

        LineDataSet setDesvio;

        Log.i("ESPACAMENTO","ARRAY ESP: "+mEspacamento);


        //Determinar a média para passar para dataset


        float somamedia= 0;

        for(int i=0; i< mResultado.size(); i++ ) {
            somamedia += Float.parseFloat(mResultado.get(i));
        }
        float media = Math.round((somamedia/mResultado.size()) *100 ) /100;



        for (int i=0; i< mResultado.size(); i++ ){
            float desvio =  Math.round((((Float.parseFloat(mResultado.get(i)) - media)/media ) *100 ) *100 ) /100;
            mdesvio.add(desvio);
            values_desvio.add(new Entry(Float.parseFloat(mEspacamento.get(i)),desvio));
        }


        //Ciclo para determinar o valor maximo do array para definir no Y axis
        float max = 0;
        for(int i=0; i< mdesvio.size(); i++ ) {
            if(mdesvio.get(i)>max) {
                max = mdesvio.get(i);
            }
        }


        //Ciclo para determinar o valor minimo do array para definir no Y axis
        float min = 0;
        for(int i=0; i< mdesvio.size(); i++ ) {
            if(mdesvio.get(i)<min) {
                min = mdesvio.get(i);
            }
        }

        leftAxis.setAxisMaximum(max+50); //50 de threshold (margem) para não ficar no limite do gráfico
        leftAxis.setAxisMinimum(min+20); //20 de threshold (margem) para não ficar no limite do gráfico


        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            setDesvio = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            setDesvio.setValues(values_desvio);


            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            setDesvio = new LineDataSet(values_desvio, "Desvio de Medidas");
            setDesvio.setAxisDependency(YAxis.AxisDependency.LEFT);
            setDesvio.setColor(ColorTemplate.getHoloBlue());
            setDesvio.setCircleColor(ColorTemplate.getHoloBlue());
            setDesvio.setLineWidth(2f);
            setDesvio.setCircleRadius(5f);
            setDesvio.setFillAlpha(65);
            setDesvio.setFillColor(ColorTemplate.getHoloBlue());
            setDesvio.setHighLightColor(Color.rgb(244, 117, 117));
            setDesvio.setDrawCircleHole(false);



            // create a data object with the data sets
            LineData data = new LineData(setDesvio);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(9f);

            // set data
            lineChart.setData(data);
            loadingDialog.dismissDialog();

        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        lineChart.centerViewToAnimated(e.getX(), e.getY(), lineChart.getData().getDataSetByIndex(h.getDataSetIndex()).getAxisDependency(), 500);

        Description des = lineChart.getDescription();


        //h.getDataIndex();
        int index =  lineChart.getData().getDataSets().get(0).getEntryIndex(e);


        try {
            des.setText("Nota: "+mNotas.get(index)+" \n Desvio: "+mdesvio.get(index)+ " %");
        }
        catch (Exception ex){

            Log.e("ERRO", ""+ex);
        }


    }

    @Override
    public void onNothingSelected() {

    }

    public void getdata(){


        String uri_get_medidas ="https://eletrodos.herokuapp.com/api/medidas";

        RequestQueue queue = Volley.newRequestQueue(getContext());


        HashMap<String, String> params = new HashMap<String, String>();


        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, uri_get_medidas+"/"+ sp.getString("user_id","0"), new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                try {

                    if (response.getString("data") == "null" ){

                        Toast.makeText(mContext, "ERRO,NÃO EXISTE NENHUMA MEDIDA", Toast.LENGTH_LONG).show();
                    }else {

                        for (int i=0; i < response.getJSONArray("data").length();i++){

                            JSONObject jObject = response.getJSONArray("data").getJSONObject(i);


                            Log.i("MESSAGE","espacamento"+Float.parseFloat(jObject.getString("espacamento")) );
                            // Log.i("MESSAGE","R_medido"+Float.parseFloat( jObject.getString("r_medido")) );


                            mNotas.add(jObject.getString("nota"));
                            mEspacamento.add(jObject.getString("espacamento"));
                            mRMedido.add(jObject.getString("r_medido"));
                            mResultado.add(jObject.getString("r_solo"));



//                            values_desvio.add(new Entry(Float.parseFloat(jObject.getString("espacamento")), Float.parseFloat( jObject.getString("r_medido") ) ));

                            //       Log.i("MESSAGE","ARRAY"+values_desvio);



                        }
                        setData(4, 9);
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
                Toast.makeText(mContext, "Erro"+ error, Toast.LENGTH_LONG).show();
            }
        }
        );
        queue.add(getRequest);




    }
}