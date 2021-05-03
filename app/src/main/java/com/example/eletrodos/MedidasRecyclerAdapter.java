package com.example.eletrodos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MedidasRecyclerAdapter extends RecyclerView.Adapter<MedidasRecyclerAdapter.ViewHolder> {


    private ArrayList<String> mNotas = new ArrayList<>();
    private ArrayList<String> mRMedido = new ArrayList<>();
    private ArrayList<String> mResultado = new ArrayList<>();
    private Context mContext;

    public MedidasRecyclerAdapter(Context context ,ArrayList<String> mNotas, ArrayList<String> mRMedido, ArrayList<String> mResultado) {
        this.mNotas = mNotas;
        this.mRMedido = mRMedido;
        this.mResultado = mResultado;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("RecyclerAdapter","onBindViewHolder: called");


        holder.txNota.setText(mNotas.get(position));
        holder.txRmedido.setText(mRMedido.get(position));
        holder.txResultado.setText(mResultado.get(position));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecyclerAdapter","onClick: called"+mNotas.get(position));

                Toast.makeText(mContext,mNotas.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txNota;
        TextView txRmedido;
        TextView txResultado;
        LinearLayout linearLayout;

        public ViewHolder(View itemView){
          super(itemView);

            txNota = (TextView)itemView.findViewById(R.id.TextView1);
            txRmedido = (TextView)itemView.findViewById(R.id.TextView2);
            txResultado = (TextView)itemView.findViewById(R.id.TextView3);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.ParentLinearLayout);
        }
    }


}
