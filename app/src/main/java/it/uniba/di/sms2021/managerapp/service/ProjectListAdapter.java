package it.uniba.di.sms2021.managerapp.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Locale;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;

public class ProjectListAdapter extends  BaseAdapter{

    private final LayoutInflater inflater;
    private final ArrayList<Progetto> progetti;
    private final ArrayList<Progetto> progettiRicerca;

    //constructor
    public ProjectListAdapter(Context context, ArrayList<Progetto> progetti) {
        this.progetti = progetti;
        inflater = LayoutInflater.from(context);
        progettiRicerca = new ArrayList<>();
        this.progettiRicerca.addAll(this.progetti);
    }

    @Override
    public int getCount() {
        return progetti.size();
    }

    @Override
    public Object getItem(int i) {
        return progetti.get(i);
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
        holder.mTitleTv.setText(progetti.get(position).getNome());

        return view;
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        progetti.clear();
        if (charText.length() == 0) {
            progetti.addAll(progettiRicerca);
        }
        else {
            for (Progetto progetto : progettiRicerca) {
                if (progetto.getNome().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    progetti.add(progetto);
                }
            }
        }
        notifyDataSetChanged();
    }
}
