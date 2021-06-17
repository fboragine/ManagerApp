package it.uniba.di.sms2021.managerapp.guest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.entities.Studente;


public class SignActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Studente newStudent;
    Button addCourse;
    CdsCallback myCallback;
    TextView selectedCds;
    private String cds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Toolbar toolbar = findViewById(R.id.top_register_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.registration);

        db = FirebaseFirestore.getInstance();

        newStudent = new Studente();

        addCourse = findViewById(R.id.add_course);
        addCourse.setOnClickListener(this);

        selectedCds = findViewById(R.id.course);
    }

    interface CdsCallback {
        void onCallback(ArrayList<CorsoDiStudio> cDsList);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_course) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignActivity.this);
            alertDialog.setTitle(R.string.select_course);

            getCdS();

            myCallback = new CdsCallback() {
                @Override
                public synchronized void onCallback(ArrayList<CorsoDiStudio> cDsList) {
                    viewCdsList(cDsList, alertDialog);
                }
            };
        }
    }

    private void getCdS() {
        db.collection("corsiDiStudio").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<CorsoDiStudio> displayCds = new ArrayList<>();
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                    CorsoDiStudio newCorso = new CorsoDiStudio( document.getString("id"),
                            document.getString("nome"),
                            document.getString("descrizione"));

                    displayCds.add(newCorso);
                }
                myCallback.onCallback(displayCds);
            } else {
                Toast.makeText(getApplicationContext(),getString(R.string.error_get_doc) + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private synchronized void viewCdsList(ArrayList<CorsoDiStudio> cDsList, AlertDialog.Builder alertDialog) {
        String[] listItems = new String[cDsList.size()];
        final int[] checkedItem = {-1};

        //Ottengo i valori da visualizzare nella lista degli esami
        for (int i = 0; i < cDsList.size(); i++)
            listItems[i] = cDsList.get(i).getNome();

        //Avvia un alert dialog box impostandolo come scelta singola
        alertDialog.setSingleChoiceItems(listItems, checkedItem[0], (dialog, which) -> {

            //Salva la posizione dell'elemento selezionato anche nella prossima apertura del dialog box
            checkedItem[0] = which;

            // Aggiorna la textView e seleziona l'id dell'esame scelto
            selectedCds.setText(listItems[which]);
            newStudent.setcDs(cDsList.get(which).getIdCorsoDiStudio());

            // Chiude il dialog box e modifica il testo nel bottone
            dialog.dismiss();
            addCourse.setText(R.string.change_select_course);

            selectedCds.setVisibility(View.VISIBLE);

        });

        // Imposta un'eventuale azione in caso di click su pulsante negativo quindi cancel
        alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {

        });

        // Crea il customAlertDialog
        AlertDialog customAlertDialog = alertDialog.create();

        // Mostra il dialog box
        customAlertDialog.show();
    }

    private void register() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        EditText nome = findViewById(R.id.name);
        EditText cognome = findViewById(R.id.surname);
        EditText matricola = findViewById(R.id.serial_number);
        EditText email = findViewById(R.id.email);
        EditText pw =  findViewById(R.id.password);

        if( !matricola.getText().toString().matches("") &&
                !nome.getText().toString().matches("") &&
                !cognome.getText().toString().matches("") &&
                !email.getText().toString().matches("") &&
                !selectedCds.toString().matches("Select Course") &&
                !email.getText().toString().matches("") &&
                !pw.getText().toString().matches("")) {

            newStudent.setId("");
            newStudent.setMatricola(matricola.getText().toString());
            newStudent.setNome(nome.getText().toString());
            newStudent.setCognome(cognome.getText().toString());
            newStudent.setEmail(email.getText().toString());

            Map<String ,String> user = new HashMap<>();
            user.put("nome", newStudent.getNome());
            user.put("cognome", newStudent.getCognome());
            user.put("email", newStudent.getEmail());
            user.put("matricola", newStudent.getMatricola());
            user.put("cDs", newStudent.getcDs());
            user.put("percorsoImg", "");

            cds = newStudent.getcDs();

            //Getting Reference to "studenti" collection
            CollectionReference collectionReference = db.collection("studenti");

            //crea l'autentication e inserisce l'utente nel firebase
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), pw.getText().toString()).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    user.put("id", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    DocumentReference documentReference = collectionReference.document(mAuth.getCurrentUser().getUid());
                    documentReference.set(user);

                    insertData();

                    Toast.makeText(getApplicationContext(), R.string.signin_success, Toast.LENGTH_LONG).show();
                    // entrare nella activity da loggato
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(getApplicationContext(), R.string.register_field_void, Toast.LENGTH_LONG).show();
        }
    }

    private void insertData() {
        db.collection("esami").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    if (cds.equals(document.getString("cDs"))) {

                        Map<String, Object> link = new HashMap<>();
                        link.put("id", "");
                        link.put("idEsame", document.get("id"));
                        link.put("idStudente", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                        link.put("stato", false);

                        db.collection("esamiStudente").add(link).addOnSuccessListener(documentReference -> documentReference.update("id", documentReference.getId()));
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_register_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.do_registration:
                register();
                break;
            case R.id.cancel_registration:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}