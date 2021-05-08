package it.uniba.di.sms2021.managerapp.exam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.loggedUser.ExamListFragment;
import it.uniba.di.sms2021.managerapp.project.ProjectActivity;
import it.uniba.di.sms2021.managerapp.service.ExamListAdapter;
import it.uniba.di.sms2021.managerapp.service.ListViewAdapter;
import it.uniba.di.sms2021.managerapp.service.ProjectListAdapter;

public class ExamProjectActivity extends AppCompatActivity {

    private Esame esame;
    private ArrayList<Progetto> progetti = new ArrayList<>();
    private ProjectListAdapter adapterProgetti;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_project);

        final Intent src = getIntent();
        if(src != null) {
            esame = src.getParcelableExtra("esame");
        }

        getProgetti();

    }

    public void getProgetti() {
        db.collection("progetti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                getApplicationContext();
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(esame.getId().equals(document.getString("codiceEsame"))) {
                            Progetto progetto = new Progetto(document.getString("id"),
                                    document.getString("nome"),
                                    document.getString("descrizione"),
                                    document.getString("codiceEsame"),
                                    document.getString("data"),
                                    (ArrayList<String>) document.get("idStudenti"),
                                    document.getBoolean("stato"));
                            progetti.add(progetto);
                        }
                    }

                    listView = (ListView)findViewById(R.id.listView);
                    adapterProgetti = new ProjectListAdapter(getApplicationContext(), progetti);
                    listView.setAdapter(adapterProgetti);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        }
                    });
                }
            }
        });
    }
}