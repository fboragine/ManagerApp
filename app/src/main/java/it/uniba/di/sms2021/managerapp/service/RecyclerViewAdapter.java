package it.uniba.di.sms2021.managerapp.service;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.project.ProjectActivity;
import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private ArrayList<Progetto> project;
    private Context context;
    public RecyclerViewAdapter(Context context, ArrayList<Progetto> project) {
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
        getEsame(project.get(i).getCodiceEsame(), i, viewHolder);
        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + project.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "#" + position + " - " + project.get(position), Toast.LENGTH_SHORT).show();

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
        return project.size();
    }

    private void getEsame(String codiceEsame, int i, ViewHolder viewHolder) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("esami").document(codiceEsame).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    String esame = "";

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        viewHolder.examTextView.setText(document.getString("nome"));
                    }
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
