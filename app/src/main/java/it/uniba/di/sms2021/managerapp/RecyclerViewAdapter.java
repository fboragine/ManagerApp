package it.uniba.di.sms2021.managerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import entities.Progetto;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Progetto> project;
    private Context context;
    RecyclerViewAdapter(Context context, ArrayList<Progetto> project) {
        super();
        this.context = context;
        this.project = project;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_card_view, viewGroup, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.projectTextView.setText(project.get(i).getNome());
        viewHolder.examTextView.setText(project.get(i).getCodiceEsame());
        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + project.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "#" + position + " - " + project.get(position), Toast.LENGTH_SHORT).show();
                    /*
                    String[] studenti = new String[]{"Mario Rossi", "Luigi Verdi", "Rosa Neri", "Filippo Neri"};
                    Progetto progetto = new Progetto("ciao",projectName.get(position), "Descrizione progetto", projectExam.get(position), "01-01-2020", studenti);
                    */
                    Intent intent = new Intent(context, ProjectActivity.class);
                    intent.putExtra("progetto",project.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        // TODO risolvere il return della size: project.size()
        return 0;
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
