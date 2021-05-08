package it.uniba.di.sms2021.managerapp.service;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Locale;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.entities.Progetto;

import static java.util.Collections.addAll;

public class ProjectListAdapter extends  BaseAdapter{

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Progetto> progetti;

    //constructor
    public ProjectListAdapter(Context context, ArrayList<Progetto> progetti) {
        mContext = context;
        this.progetti = progetti;
        inflater = LayoutInflater.from(mContext);
    }

    public static class ViewHolder{
        TextView mTitleTv, mDescTv;
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

    /*public void filter(String charText){
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
    }*/
}
