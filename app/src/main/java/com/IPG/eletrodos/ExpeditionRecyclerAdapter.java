package com.IPG.eletrodos;

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

public class ExpeditionRecyclerAdapter extends RecyclerView.Adapter<ExpeditionRecyclerAdapter.ViewHolder> {


    private ArrayList<String> mNome = new ArrayList<>();
    private ArrayList<String> mData= new ArrayList<>();
    private ArrayList<String> mNotas = new ArrayList<>();
    private Context mContext;

    public ExpeditionRecyclerAdapter(Context context , ArrayList<String> mNome, ArrayList<String> mData, ArrayList<String> mNotas) {

        this.mNome = mNome;
        this.mData = mData;
        this.mNotas = mNotas;
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



        holder.txNome.setText(mNome.get(position));
        holder.txData.setText(mData.get(position));
        holder.txNota.setText(mNotas.get(position));

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
        TextView txNome;
        TextView txData;
        LinearLayout linearLayout;

        public ViewHolder(View itemView){
          super(itemView);

            txNome = (TextView)itemView.findViewById(R.id.TextView1);
            txData = (TextView)itemView.findViewById(R.id.TextView2);
            txNota = (TextView)itemView.findViewById(R.id.TextView3);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.ParentLinearLayout);
        }
    }


}
