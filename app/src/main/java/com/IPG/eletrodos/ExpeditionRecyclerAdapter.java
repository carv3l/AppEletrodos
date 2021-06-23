package com.IPG.eletrodos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ExpeditionRecyclerAdapter extends RecyclerView.Adapter<ExpeditionRecyclerAdapter.ViewHolder> {


    private ArrayList<String> mNome = new ArrayList<>();
    private ArrayList<String> mData= new ArrayList<>();
    private ArrayList<String> mNotas = new ArrayList<>();
    private ArrayList<String> expeditionId = new ArrayList<>();
    private final Context mContext;
    boolean isSelectMode = false;
    int previous_holder = 0;
    private final ArrayList<String> selectedItems = new ArrayList<>();
    String selectedLoadEx = "";
    SharedPreferences sp;
    RecyclerView recyclerView;


    View view;
    Snackbar snackbar;


    public ExpeditionRecyclerAdapter(Context context , ArrayList<String> mNome, ArrayList<String> mData, ArrayList<String> mNotas,ArrayList<String> mIdExpeditions,RecyclerView rcview) {

        this.mNome = mNome;
        this.mData = mData;
        this.mNotas = mNotas;
        this.expeditionId = mIdExpeditions;
        this.mContext = context;
        this.recyclerView = rcview;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent, false);
        ViewHolder holder = new ViewHolder(view);

        sp = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
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
             //   Log.d("RecyclerAdapter","onClick: called"+mNotas.get(position));

               // Toast.makeText(mContext,mNotas.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //  Toast.makeText(mContext,"Long Click" , Toast.LENGTH_SHORT).show();
                isSelectMode = true;


              //  if (selectedItems.contains(expeditionId.get(holder.getAdapterPosition()))){
                //    holder.linearLayout.setBackgroundResource(R.color.white);
                //    selectedItems.remove(expeditionId.get(holder.getAdapterPosition()));
              //  }else {


try {
                    ViewHolder oldh = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(previous_holder);

                    oldh.linearLayout.setBackgroundResource(R.color.white);

                }catch (Exception e){


}


                    holder.linearLayout.setBackgroundResource(R.color.LightGray);
                    //selectedItems.add(expeditionId.get(holder.getAdapterPosition()));

                    selectedLoadEx = expeditionId.get(holder.getAdapterPosition());

                    previous_holder = holder.getAdapterPosition();


                 //   selectedItems.remove(expeditionId.get(holder.getAdapterPosition()-1));

           //     }

                    //showSnack("Selecionado "+selectedLoadEx+" medida");

                    showSnack(mNome.get(holder.getAdapterPosition())+" Selecionado");



                if (selectedItems.size() == 0)
                    isSelectMode = false;

                return true;
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

    private void showSnack(String message){

        snackbar = Snackbar.make(view, ""+message+" para carregar em gráfico ", Snackbar.LENGTH_INDEFINITE);

        //   snackbar.setText(""+message);

        //  showToast(""+selectedItems);


        snackbar.setAction("Carregar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your action method here

                sp.edit().putBoolean("graph",true).apply(); //Colocar um boolean para no grafico carregar a partir da exped
                sp.edit().putString("expedition_id",selectedLoadEx).apply(); //Colocar um boolean para no grafico carregar a partir da exped

                Intent myIntent = new Intent(mContext, GraficoActivity.class);
              //  myIntent.putExtra("list_id_medidas", selectedItems); //Optional parameters
                //myIntent.putExtra("user_id", user_id); //Optional parameters
                mContext.startActivity(myIntent);
                snackbar.dismiss();
            } });
        snackbar.show();
    }



}
