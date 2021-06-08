package com.IPG.eletrodos;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MedidasRecyclerAdapter extends RecyclerView.Adapter<MedidasRecyclerAdapter.ViewHolder> {


    private String user_id;

    private ArrayList<String> mNotas = new ArrayList<>();
    private ArrayList<String> mRMedido = new ArrayList<>();
    private ArrayList<String> mResultado = new ArrayList<>();
    private ArrayList<String> medidasId = new ArrayList<>();
    private Context mContext;
    boolean isSelectMode = false;
    private ArrayList<String> selectedItems = new ArrayList<>();

    View view;
    Snackbar snackbar;

    public MedidasRecyclerAdapter(Context context ,ArrayList<String> mNotas, ArrayList<String> mRMedido, ArrayList<String> mResultado, ArrayList<String> medidas_id,String user_id) {
        this.mNotas = mNotas;
        this.mRMedido = mRMedido;
        this.mResultado = mResultado;
        this.medidasId = medidas_id;
        this.mContext = context;
        this.user_id = user_id;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent, false);
        ViewHolder holder = new ViewHolder(view);



        onViewAttachedToWindow(holder);
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

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
          //  @SuppressLint("ResourceAsColor")
            @Override
            public boolean onLongClick(View v) {
                //  Toast.makeText(mContext,"Long Click" , Toast.LENGTH_SHORT).show();
                isSelectMode = true;

                if (selectedItems.contains(medidasId.get(holder.getAdapterPosition()))){
                    holder.linearLayout.setBackgroundResource(R.color.white);
                    selectedItems.remove(medidasId.get(holder.getAdapterPosition()));

                }else {
                    holder.linearLayout.setBackgroundResource(R.color.LightGray);
                    selectedItems.add(medidasId.get(holder.getAdapterPosition()));
                }

                if (selectedItems.size()<= 1)
                    showSnack("Selecionado "+selectedItems.size()+" medida");
                else
                    showSnack("Selecionado "+selectedItems.size()+" medidas");



                if (selectedItems.size() == 0)
                    isSelectMode = false;

                return true;
            }
        });

    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return mResultado.size();
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

    private void showToast(String message){

        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

    }

    private void showSnack(String message){

        snackbar = Snackbar.make(view, ""+message+" para associar", Snackbar.LENGTH_INDEFINITE);

     //   snackbar.setText(""+message);

      //  showToast(""+selectedItems);


        snackbar.setAction("Associar", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call your action method here
                Intent myIntent = new Intent(mContext, SelectExpedition.class);
                myIntent.putExtra("list_id_medidas", selectedItems); //Optional parameters
                myIntent.putExtra("user_id", user_id); //Optional parameters
                mContext.startActivity(myIntent);
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }








}
