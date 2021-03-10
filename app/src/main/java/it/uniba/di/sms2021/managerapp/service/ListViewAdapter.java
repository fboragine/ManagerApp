package it.uniba.di.sms2021.managerapp.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;

import static java.util.Collections.addAll;

public class ListViewAdapter extends BaseAdapter {

    //variables
    Context mContext;
    LayoutInflater inflater;
    ArrayList<CorsoDiStudio> corsiDiStudio;
    ArrayList<CorsoDiStudio> corsiDiStudioRicerca;

    //constructor
    public ListViewAdapter(Context context, ArrayList<CorsoDiStudio> corsiDiStudio) {
        mContext = context;
        this.corsiDiStudio = corsiDiStudio;
        inflater = LayoutInflater.from(mContext);
        corsiDiStudioRicerca = new ArrayList<>();
        this.corsiDiStudioRicerca.addAll(this.corsiDiStudio);
    }

    public static class ViewHolder{
        TextView mTitleTv, mDescTv;
    }

    @Override
    public int getCount() {
        return corsiDiStudio.size();
    }

    @Override
    public Object getItem(int i) {
        return corsiDiStudio.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row, null);

            //locate the views in row.xml
            holder.mTitleTv = view.findViewById(R.id.mainTitle);
            holder.mDescTv = view.findViewById(R.id.mainDesc);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }
        //set the results into textviews
        holder.mTitleTv.setText(corsiDiStudio.get(position).getNome());
        holder.mDescTv.setText(corsiDiStudio.get(position).getDescrizione());

        //listview item clicks
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code later
            }
        });
        return view;
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        corsiDiStudio.clear();
        if (charText.length() == 0) {
            corsiDiStudio.addAll(corsiDiStudioRicerca);
        }
        else {
            for (CorsoDiStudio corsi : corsiDiStudioRicerca) {
                if (corsi.getNome().toLowerCase(Locale.getDefault())
                    .contains(charText)) {
                    corsiDiStudio.add(corsi);
                }
            }
        }
        notifyDataSetChanged();
    }
}
