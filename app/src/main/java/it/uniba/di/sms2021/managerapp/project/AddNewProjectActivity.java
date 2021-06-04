package it.uniba.di.sms2021.managerapp.project;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;

public class AddNewProjectActivity extends AppCompatActivity implements View.OnClickListener{

    interface SpecsCallback {
        void onCallback(ArrayList<Esame> examList, ArrayList<Studente> attendeesList);
    }

    private static Studente loggedStudent;
    private FirebaseFirestore db;
    Button addExam;
    Button addAttendees;
    TextView selectedExam;
    ListView selectedAttendees;
    LinearLayout selectedAttendeesView;
    // single item array instance to store
    // which element is selected by user
    // initially it should be set to zero meaning
    // none of the element is selected by default
    final int[] checkedItem = {-1};
    SpecsCallback myCallback;
    Progetto newProject;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        Toolbar toolbar = findViewById(R.id.top_register_project_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.create_project);

        db = FirebaseFirestore.getInstance();
        getLoggedStudent();

        addExam = findViewById(R.id.add_exam_btn);
        addExam.setOnClickListener(this);

        addAttendees = findViewById(R.id.add_attendees_btn);
        addAttendees.setOnClickListener(this);

        selectedExam = findViewById(R.id.exam_selected);
        selectedExam.setVisibility(View.GONE);

        selectedAttendees = findViewById(R.id.attendees_list);
        selectedAttendeesView = findViewById(R.id.attendees_list_view);

        newProject = new Progetto();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_exam_btn) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNewProjectActivity.this);
            alertDialog.setTitle(R.string.exam_dialog_title);

            getEsamiCdS();

            myCallback = new SpecsCallback() {
                @Override
                public synchronized void onCallback(ArrayList<Esame> examList, ArrayList<Studente> attendeesList) {
                    viewExamList(examList, alertDialog);
                }
            };
        }else if (v.getId() == R.id.add_attendees_btn) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNewProjectActivity.this);
            alertDialog.setTitle(R.string.attendees_dialog_title);

            getPartecipanti();

            myCallback = new SpecsCallback() {
                @Override
                public synchronized void onCallback(ArrayList<Esame> examList, ArrayList<Studente> attendeesList) {
                    viewAttendeesList(attendeesList, alertDialog);
                }
            };
        }
    }

    private synchronized void viewAttendeesList(ArrayList<Studente> attendeesList, AlertDialog.Builder alertDialog) {
        String[] listItems = new String[attendeesList.size()];
        boolean[] selectedItems = new boolean[attendeesList.size()];


        //Ottengo i valori da visualizzare nella lista degli esami
        for (int i = 0; i < attendeesList.size(); i++) {
                listItems[i] = attendeesList.get(i).getNome() + " " + attendeesList.get(i).getCognome();
                selectedItems[i] = false;
        }

        //Avvia un alert dialog box impostandolo come scelta singola
        alertDialog.setMultiChoiceItems(listItems, selectedItems, (dialog, which, isChecked) -> {
            //Salva la posizione dell'elemento selezionato anche nella prossima apertura del dialog box
            selectedItems[which] = isChecked;
        });

        // Set the positive/yes button click listener
        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            ArrayList<String> idStudenti = new ArrayList<>();
            ArrayList<String> studentiSelezionati = new ArrayList<>();

            idStudenti.add(loggedStudent.getId());

            // Do something when click positive button
            Log.d(null, "Elementi selezionati: " + selectedItems.length);
            for (int i = 0; i < selectedItems.length; i++) {
                boolean checked = selectedItems[i];
                if (checked) {
                    idStudenti.add(attendeesList.get(i).getId());
                    studentiSelezionati.add(attendeesList.get(i).getNome() + " " + attendeesList.get(i).getCognome());
                }
            }

            newProject.setIdStudenti(idStudenti);

            selectedAttendees.setVisibility(View.VISIBLE);
            selectedAttendeesView.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, studentiSelezionati);
            selectedAttendees.setAdapter(adapter);

            // Chiude il dialog box e modifica il testo nel bottone
            dialog.dismiss();
            addAttendees.setText(R.string.change_select_attendees);
            int color = Color.parseColor("#63A4FF");
            addAttendees.setBackgroundColor(color);
        });

        // Imposta un'eventuale azione in caso di click su pulsante negativo quindi cancel
        alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {

        });

        // Crea il customAlertDialog
        AlertDialog customAlertDialog = alertDialog.create();

        // Mostra il dialog box
        customAlertDialog.show();
    }

    private synchronized void viewExamList(ArrayList<Esame> examList, AlertDialog.Builder alertDialog) {
        String[] listItems = new String[examList.size()];

        //Ottengo i valori da visualizzare nella lista degli esami
        for (int i = 0; i < examList.size(); i++)
            listItems[i] = examList.get(i).getDescrizione();

        //Avvia un alert dialog box impostandolo come scelta singola
        alertDialog.setSingleChoiceItems(listItems, checkedItem[0], (dialog, which) -> {

            //Salva la posizione dell'elemento selezionato anche nella prossima apertura del dialog box
            checkedItem[0] = which;

            // Aggiorna la textView e seleziona l'id dell'esame scelto
            selectedExam.setText(listItems[which]);
            newProject.setCodiceEsame(examList.get(which).getId());

            // Chiude il dialog box e modifica il testo nel bottone
            dialog.dismiss();

            addExam.setText(R.string.change_select_exam);
            int color = Color.parseColor("#63A4FF");
            addExam.setBackgroundColor(color);

            selectedExam.setVisibility(View.VISIBLE);

        });

        // Imposta un'eventuale azione in caso di click su pulsante negativo quindi cancel
        alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {
        });

        // Crea il customAlertDialog
        AlertDialog customAlertDialog = alertDialog.create();

        // Mostra il dialog box
        customAlertDialog.show();
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
            {
                Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                startActivity(intent);
                finish();
            }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SimpleDateFormat")
    private void register() {
        EditText nomeProgetto = findViewById(R.id.name_new_project);
        EditText descrizioneProgetto = findViewById(R.id.project_description);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        //Controllo che l'utente non abbia lasciato valori nulli
        if( !nomeProgetto.getText().toString().matches("") &&
                !descrizioneProgetto.getText().toString().matches("") &&
                !newProject.getCodiceEsame().matches("") &&
                newProject.getIdStudenti().size() != 0 ) {

            //Creazione dell'oggetto aux da inserire nell'hash map
            newProject.setNome(nomeProgetto.getText().toString());
            newProject.setDescrizione(descrizioneProgetto.getText().toString());
            newProject.setDataCreazione(formatter.format(Calendar.getInstance().getTime()));
            newProject.setStato(true);
            newProject.setValutato(false);

            //Creazione dell'hash map per inserire il progetto nel DB
            Map<String ,Object> project = new HashMap<>();

            project.put("codiceEsame",newProject.getCodiceEsame());
            project.put("data",newProject.getDataCreazione());
            project.put("descrizione",newProject.getDescrizione());
            project.put("id","");
            project.put("idStudenti",newProject.getIdStudenti());
            project.put("nome", newProject.getNome());
            project.put("stato", newProject.getStato());
            project.put("valutato", newProject.isValutato());

            db.collection("progetti").add(project).addOnSuccessListener(documentReference -> {
                documentReference.update("id", documentReference.getId());
                Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void getLoggedStudent() {
        ObjectInputStream input;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(getExternalFilesDir(null),"studenti.srl")));
            loggedStudent = new Studente();
            loggedStudent = (Studente) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getEsamiCdS() {
        db.collection("esami").whereEqualTo("cDs",loggedStudent.getcDs()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                ArrayList<Esame> displayEsami = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {

                    Esame newEsame = new Esame( document.getString("id"),
                                                document.getString("nome"),
                                                document.getString("descrizione"),
                                                document.getString("cDs"),
                            (ArrayList<String>) document.get("idDocenti"));

                    displayEsami.add(newEsame);
                }
                myCallback.onCallback(displayEsami,null);
            }
        });
    }

    private void getPartecipanti() {
        if(!addAttendees.getText().toString().matches(String.valueOf(R.string.change_select_attendees))) {
            db.collection("studenti").whereEqualTo("cDs",loggedStudent.getcDs()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    ArrayList<Studente> displayPartecipanti = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(!loggedStudent.getId().equals(document.getString("id"))) {
                            Studente newStudente = new Studente( document.getString("id"),
                                    document.getString("matricola"),
                                    document.getString("nome"),
                                    document.getString("cognome"),
                                    document.getString("email"),
                                    document.getString("cDs"));

                            displayPartecipanti.add(newStudente);
                        }
                    }
                    myCallback.onCallback(null,displayPartecipanti);
                }
            });
        }
    }
}
