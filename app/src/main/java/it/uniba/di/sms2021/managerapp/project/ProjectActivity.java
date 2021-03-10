package it.uniba.di.sms2021.managerapp.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.Studente;

public class ProjectActivity extends AppCompatActivity {

    protected static final int EDIT_ITEM_ID = View.generateViewId();
    protected static final int SAVE_ITEM_ID = View.generateViewId();
    protected static final int CANCEL_ITEM_ID = View.generateViewId();
    private static final String TAG = "ProjectActivityLog";

    private Progetto progetto;
    private TextView textViewNome;
    private TextView textDescEsame;
    private ListView listViewStudenti;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        final Intent src = getIntent();
        if(src != null) {
            progetto = src.getParcelableExtra("progetto");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(progetto.getNome());

        textViewNome = findViewById(R.id.project_name);
        textViewNome.setText(progetto.getNome());

        textDescEsame = findViewById(R.id.project_description);
        textDescEsame.setText(progetto.getDescrizione());

        getDisplayName();
        getEsame();
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDisplayName() {
        ArrayList<String> idStudentiPart = progetto.getIdStudenti();
        ArrayList<Studente> studenti = new ArrayList<Studente>();
        ArrayList<String> displayNameStudenti = new ArrayList<String>();

        db.collection("studenti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean flag;
                        int count = 0;

                        do {
                            flag = false;
                            if(idStudentiPart.get(count).equals(document.getString("id"))) {
                                Studente studente = new Studente(document.getString("id"), document.getString("matricola"), document.getString("nome"), document.getString("cognome"),
                                        document.getString("email"), document.getString("cDs"));
                                studenti.add(studente);

                                displayNameStudenti.add(studente.getNome() + " " + studente.getCognome());
                            }
                            count ++;
                        }while(!flag  && count < idStudentiPart.size());
                    }

                    listViewStudenti = findViewById(R.id.project_students);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, displayNameStudenti);
                    listViewStudenti.setAdapter(adapter);
                }
            }
        });
    }

    private void getEsame() {

        db.collection("esami").document(progetto.getCodiceEsame()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        TextView textEsame = findViewById(R.id.project_exam);
                        textEsame.setText(document.getString("nome").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void go_to_documents(View view) {
        Intent intent = new Intent(getApplicationContext(), ProjectDocumentsActivity.class);
        intent.putExtra("progetto",progetto);
        startActivity(intent);
    }
}