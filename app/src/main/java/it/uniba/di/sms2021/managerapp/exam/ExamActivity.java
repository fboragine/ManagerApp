package it.uniba.di.sms2021.managerapp.exam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class ExamActivity extends AppCompatActivity {

    private Esame esame;
    private ListView listViewEsami;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        final Intent src = getIntent();
        if(src != null) {
            esame = src.getParcelableExtra("esame");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(esame.getNome());

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(view -> finish());

        TextView textViewNome = findViewById(R.id.exam_name);
        textViewNome.setText(esame.getNome());

        TextView textDescEsame = findViewById(R.id.exam_description);
        textDescEsame.setText(esame.getDescrizione());

        db = FirebaseFirestore.getInstance();

        getDisplayName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        //Nascondo pulsante ricerca
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDisplayName() {
        ArrayList<String> idDocentiPart = new ArrayList<>(esame.getIdDocenti());

        ArrayList<String> displayNameDocenti = new ArrayList<>();

        db.collection("docenti").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    boolean flag;
                    int count = 0;

                    do {
                        flag = false;

                        if(idDocentiPart.get(count).equals(document.getString("id"))) {
                            Docente docente = new Docente(document.getString("id"),
                                    document.getString("matricola"),
                                    document.getString("nome"),
                                    document.getString("cognome"),
                                    document.getString("email"));

                            displayNameDocenti.add(docente.getNome() + " " + docente.getCognome());
                        }
                        count ++;
                    }while(!flag  && count < idDocentiPart.size());
                }

                listViewEsami = findViewById(R.id.exam_teachers);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, displayNameDocenti);
                listViewEsami.setAdapter(adapter);
            }
        });
    }

    public void go_to_projects(View view) {
        Intent intent = new Intent(getApplicationContext(), ExamProjectActivity.class);
        intent.putExtra("esame",esame);
        startActivity(intent);
    }
}