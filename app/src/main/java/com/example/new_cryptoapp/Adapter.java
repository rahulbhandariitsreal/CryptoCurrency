package com.example.new_cryptoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Viewholder> {


    private  ArrayList<CryptoCurrency> arrayList;

    public Adapter(ArrayList<CryptoCurrency> arrayList) {
        this.arrayList = arrayList;
    }




    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        CryptoCurrency currency=arrayList.get(position);
        String indian_rate= getindianrate(currency.getRate());
        holder.rate_view.setText("₹"+indian_rate);
        holder.name_view.setText(currency.getName_crupto());

    }

    private String getindianrate(double rate) {
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
       return decimalFormat.format(rate*82.09);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        TextView name_view,rate_view;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            name_view=itemView.findViewById(R.id.name_view);
            rate_view=itemView.findViewById(R.id.rate_view);
        }
    }

    public  void addCryptocurrency(ArrayList<CryptoCurrency> arrayList){
        this.arrayList=arrayList;
        notifyDataSetChanged();
    }


}
