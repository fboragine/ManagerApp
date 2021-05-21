package it.uniba.di.sms2021.managerapp.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;


public class ExamListAdapter extends  BaseAdapter{

    private final LayoutInflater inflater;
    private final ArrayList<Esame> esami;
    private final ArrayList<Esame> esamiRicerca;

    //constructor
    public ExamListAdapter(Context context, ArrayList<Esame> esami) {
        this.esami = esami;
        inflater = LayoutInflater.from(context);
        esamiRicerca = new ArrayList<>();
        esamiRicerca.addAll(esami);
    }

    public static class ViewHolder{
        TextView mTitleTv, mDescTv;
    }

    @Override
    public int getCount() {
        return esami.size();
    }

    @Override
    public Object getItem(int i) {
        return esami.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        it.uniba.di.sms2021.managerapp.service.ExamListAdapter.ViewHolder holder;
        if(view == null) {
            holder = new it.uniba.di.sms2021.managerapp.service.ExamListAdapter.ViewHolder();
            view = inflater.inflate(R.layout.row, null);

            //locate the views in row.xml
            holder.mTitleTv = view.findViewById(R.id.mainTitle);

            holder.mDescTv = view.findViewById(R.id.mainDesc);
            holder.mDescTv.setVisibility(View.INVISIBLE);


            view.setTag(holder);
        }
        else {
            holder = (it.uniba.di.sms2021.managerapp.service.ExamListAdapter.ViewHolder)view.getTag();
        }
        //set the results into textviews
        holder.mTitleTv.setText(esami.get(position).getNome());

        return view;
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        esami.clear();
        if (charText.length() == 0) {
            esami.addAll(esamiRicerca);
        }
        else {
            for (Esame esame : esamiRicerca) {
                if (esame.getNome().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    esami.add(esame);
                }
            }
        }
        notifyDataSetChanged();
    }
}
