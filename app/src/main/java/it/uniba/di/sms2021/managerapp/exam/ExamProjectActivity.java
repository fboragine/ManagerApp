package it.uniba.di.sms2021.managerapp.exam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.project.ProjectActivity;
import it.uniba.di.sms2021.managerapp.service.ProjectListAdapter;

public class ExamProjectActivity extends AppCompatActivity {

    private Esame esame;
    private ArrayList<Progetto> progetti;
    private ProjectListAdapter adapterProgetti;
    private FirebaseFirestore db;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_project);

        final Intent src = getIntent();
        if(src != null) {
            esame = src.getParcelableExtra("esame");
        }

        listView = findViewById(R.id.listView);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(esame.getNome());

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(view -> finish());

        progetti = new ArrayList<>();

        getProgetti();
        setProjectsVisibility();
    }

    public void getProgetti() {
        db.collection("progetti").get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
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
                                document.getBoolean("stato"),
                                false);
                        progetti.add(progetto);
                    }
                }

                adapterProgetti = new ProjectListAdapter(getApplicationContext(), progetti);
                listView.setAdapter(adapterProgetti);
                setProjectsVisibility();

                listView.setOnItemClickListener((adapterView, view, i, l) -> {
                    Intent intent = new Intent(getApplicationContext(), ProjectActivity.class);
                    intent.putExtra("progetto", progetti.get(i));
                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull  Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)) {
                    adapterProgetti.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapterProgetti.filter(s);
                }
                return true;
            }
        });

        return true;
    }

    private void setProjectsVisibility() {
        TextView textView = findViewById(R.id.noProjectsView);
        if (progetti.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}