package it.uniba.di.sms2021.managerapp.service;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import it.uniba.di.sms2021.managerapp.project.ProjectActivity;
import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    Context context;
    LayoutInflater inflater;
    ArrayList<Progetto> project;
    ArrayList<Progetto> projectRicerca;

    public RecyclerViewAdapter(Context context, ArrayList<Progetto> project) {
        this.context = context;
        this.project = project;
        inflater = LayoutInflater.from(context);
        projectRicerca = new ArrayList<>();
        this.projectRicerca.addAll(this.project);
    }

    @Override
    public @NotNull ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.projectTextView.setText(project.get(i).getNome());
        getEsame(project.get(i).getCodiceEsame(), viewHolder);
        viewHolder.setClickListener((view, position, isLongClick) -> {
            if (isLongClick) {
                Toast.makeText(context, context.getString(R.string.project)+" : "+ project.get(position).getNome(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.project)+" : "+ project.get(position).getNome(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra("progetto",project.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return project.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        project.clear();
        if (charText.length() == 0) {
            project.addAll(projectRicerca);
        }
        else {
            for (Progetto progetti : projectRicerca) {
                if (progetti.getNome().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    project.add(progetti);
                }
            }
        }
        notifyDataSetChanged();
    }

    private final Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0) {
                project.addAll(projectRicerca);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Progetto progetti : projectRicerca) {
                    if (progetti.getNome().toLowerCase().contains(filterPattern)) {
                        project.add(progetti);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = project;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            project.clear();
            project.addAll((Collection<? extends Progetto>) results.values);
            notifyDataSetChanged();
        }
    };

    private void getEsame(String codiceEsame, ViewHolder viewHolder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("esami").document(codiceEsame).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    viewHolder.examTextView.setText(document.getString("nome"));
                }
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        TextView projectTextView;
        TextView examTextView;
        private ItemClickListener clickListener;
        ViewHolder(View itemView) {
            super(itemView);
            projectTextView = itemView.findViewById(R.id.projectNameCardTextView);
            examTextView = itemView.findViewById(R.id.projectExamCardTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition(), false);
        }
        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getLayoutPosition(), true);
            return true;
        }
    }
}
