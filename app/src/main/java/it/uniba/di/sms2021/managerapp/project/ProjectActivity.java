package it.uniba.di.sms2021.managerapp.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.Studente;

public class ProjectActivity extends AppCompatActivity implements View.OnClickListener{

    protected static final int EDIT_ITEM_ID = View.generateViewId();
    protected static final int SAVE_ITEM_ID = View.generateViewId();
    protected static final int CANCEL_ITEM_ID = View.generateViewId();
    private static final String TAG = "ProjectActivityLog";

    private Progetto progetto;
    private TextView textViewNome;
    private TextView textDescEsame;
    private ListView listViewStudenti;
    private FirebaseFirestore db;

    private Button rateBtn;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        String pathStudente = getExternalFilesDir(null).getPath() + "/studenti.srl";
        String pathDocente = getExternalFilesDir(null).getPath() + "/docenti.srl";
        File loggedStudente = new File(pathStudente);
        File loggedDocente = new File(pathDocente);

        db = FirebaseFirestore.getInstance();

        final Intent src = getIntent();
        if(src != null) {
            progetto = src.getParcelableExtra("progetto");
        }

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(progetto.getNome());

        Button docButton = findViewById(R.id.project_files);

        rateBtn = findViewById(R.id.set_exam_result);
        rateBtn.setOnClickListener(this);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null).listFiles()).length == 0 ||
                !loggedStudente.exists() &&
                !loggedDocente.exists()) {
            docButton.setVisibility(View.GONE);
        }else {
            docButton.setVisibility(View.VISIBLE);
        }

        textViewNome = findViewById(R.id.project_name);
        textViewNome.setText(progetto.getNome());

        textDescEsame = findViewById(R.id.project_description);
        textDescEsame.setText(progetto.getDescrizione());

        getDisplayName();
        getEsame();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.set_exam_result) {
            // Set an EditText view to get user input
            final EditText voto = new EditText(ProjectActivity.this);
            voto.setInputType(InputType.TYPE_CLASS_NUMBER);
            showInputBox(voto);    //Visualizzazione inputBox dove true = primo avvio
        }

    }

    private void showInputBox(EditText textField) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProjectActivity.this);

        alertDialog.setTitle(R.string.project_rate_title_dialog);
        alertDialog.setView(textField);
        alertDialog.setMessage(R.string.project_rate_message_dialog);

        //ASSEGNAZIONE DEL VOTO
        alertDialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int voto = Integer.parseInt(textField.getText().toString());
                if (voto > 0 && voto <= 33) {
                    db.collection("esamiStudente").whereEqualTo("idEsame", progetto.getCodiceEsame()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    boolean isPromosso = false;
                                    if (Integer.parseInt(textField.getText().toString()) >= 18) {
                                        isPromosso = true;
                                    }
                                    db.collection("esamiStudente").document(document.getId()).update("stato", isPromosso).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection("esami").document(progetto.getCodiceEsame()).update("commento", textField.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                                aggiuntaCommento();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_insert_rate, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                db.collection("esami").document(progetto.getCodiceEsame()).update("commento", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void aggiuntaCommento() {
        //AGGIUNTA DEL VOTO
        final EditText descrizioneDialog = new EditText(ProjectActivity.this);
        descrizioneDialog.setEms(4);

        new AlertDialog.Builder(ProjectActivity.this)
                .setMessage(R.string.project_rate_message_dialog)
                .setView(descrizioneDialog)
                .setMessage(R.string.project_comment_message_dialog)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.collection("esami").document(progetto.getCodiceEsame()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String descrizione  = (String) documentSnapshot.get("commento");

                                db.collection("esami").document(progetto.getCodiceEsame()).update("commento", descrizione + " " + descrizioneDialog.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("progetti").document(progetto.getId()).update("valutato", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), R.string.succesful_rate, Toast.LENGTH_LONG).show();
                                                progetto.setValutato(true);
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.collection("esami").document(progetto.getCodiceEsame()).update("commento", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .show();
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
                                Studente studente = new Studente(document.getString("id"),
                                                                 document.getString("matricola"),
                                                                 document.getString("nome"),
                                                                 document.getString("cognome"),
                                                                 document.getString("email"),
                                                                 document.getString("cDs"));
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
                        textEsame.setText(document.getString("nome"));
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
}