package com.example.eletrodos;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import java.util.ArrayList;

public class ListAdapter extends AppCompatActivity {

    private ArrayList<String> mNotas = new ArrayList<>();
    private ArrayList<String> mRMedido= new ArrayList<>();
    private ArrayList<String> mResultado = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_adapter);

        Log.d("List","onCreate: Started");
//        ListView mListView = (ListView)findViewById(R.id.listView);



        mNotas.add("Nota1");
        mNotas.add("Nota2");
        mNotas.add("Test3");
        mNotas.add("Igd1");
        mNotas.add("Teste 4");
        mNotas.add("Teste 5");


        mRMedido.add("1");
        mRMedido.add("45");
        mRMedido.add("2");
        mRMedido.add("33");
        mRMedido.add("54");
        mRMedido.add("376");

        mResultado.add("12312");
        mResultado.add("542");
        mResultado.add("1223");
        mResultado.add("12");
        mResultado.add("231");
        mResultado.add("133");

       initRecyclerView();

    }

    private void initRecyclerView(){

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        MedidasRecyclerAdapter adapter = new MedidasRecyclerAdapter(this,mNotas,mRMedido,mResultado);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}