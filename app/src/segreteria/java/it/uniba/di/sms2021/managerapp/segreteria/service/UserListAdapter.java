package it.uniba.di.sms2021.managerapp.segreteria.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Utente;

public class UserListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<Utente> utenti;
    ArrayList<Utente> utentiRicerca;

    public UserListAdapter(Context context, ArrayList<Utente> utenti) {
        this.context = context;
        this.utenti = utenti;
        inflater = LayoutInflater.from(this.context);
        utentiRicerca = new ArrayList<>();
        utentiRicerca.addAll(this.utenti);
    }

    private static class ViewHolder {
        TextView title, description;
    }

    @Override
    public int getCount() {
        return utenti.size();
    }

    @Override
    public Object getItem(int position) {
        return utenti.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row, null);

            //locate the views in row.xml
            viewHolder.title = convertView.findViewById(R.id.mainTitle);
            viewHolder.description = convertView.findViewById(R.id.mainDesc);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //set the results into textviews
        viewHolder.title.setText(utenti.get(position).getCognome() + " " + utenti.get(position).getNome());
        viewHolder.description.setText(utenti.get(position).getMatricola());

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        utenti.clear();
        if(charText.length() == 0) {
            utenti.addAll(utentiRicerca);
        } else  {
            for (Utente utente : utentiRicerca) {
                if (utente.getNome().toLowerCase(Locale.getDefault()).contains(charText) || utente.getCognome().toLowerCase(Locale.getDefault()).contains(charText)) {
                    utenti.add(utente);
                }
            }
        }
        notifyDataSetChanged();
    }
}
