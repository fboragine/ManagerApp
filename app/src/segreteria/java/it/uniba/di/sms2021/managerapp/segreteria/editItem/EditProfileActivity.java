package it.uniba.di.sms2021.managerapp.segreteria.editItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.Utente;
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class EditProfileActivity extends AppCompatActivity {

    public static final int DELETE_ITEM_ID = View.generateViewId();
    public static final int SETTINGS_ITEM_ID = View.generateViewId();

    private FirebaseFirestore db;
    private ImageView profileImg;
    private Utente utente;
    private Boolean isStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();

        final Intent src = getIntent();
        if(src != null) {
            utente = src.getParcelableExtra("utente");
            isStudent = src.getBooleanExtra("isStudent", true);
        }

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Edit Profile");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(view -> finish());

        TextView label = findViewById(R.id.name);
        label.setText(utente.getNome());

        label = findViewById(R.id.surname);
        label.setText(utente.getCognome());

        label = findViewById(R.id.serial_number);
        label.setText(utente.getMatricola());

        label = findViewById(R.id.profile_email);
        label.setText(utente.getEmail());

        profileImg = findViewById(R.id.ic_action_name);
        viewImgProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_search);
        if (menuItem != null)
            menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Add Delete Menu Item
        int deleteId  = DELETE_ITEM_ID;
        if (menu.findItem(deleteId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem delete = menu.add(
                    Menu.NONE,
                    deleteId,
                    1,
                    getString(R.string.delete)
            );

            delete.setIcon(R.drawable.ic_delete);
            delete.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            delete.setOnMenuItemClickListener(item -> {
                delete();
                finish();
                return true;
            });
        }

        // Add Settings Menu Item
        int settingsId = SETTINGS_ITEM_ID;
        if (menu.findItem(settingsId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem settings = menu.add(
                    Menu.NONE,
                    settingsId,
                    100,
                    getString(R.string.action_settings)
            );

            // Set the show as action flags for new menu item
            settings.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);

            // Set a click listener for the new menu item
            settings.setOnMenuItemClickListener(item -> {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
                return true;
            });
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void delete() {
        DocumentReference docUpdate;

        if(isStudent) {
            rimozioneEsamiStudente(utente.getId());
            docUpdate = db.collection("studenti").document(utente.getId());
        }else {
            rimozioneEsamiDocente(utente.getId());
            docUpdate = db.collection("docenti").document(utente.getId());
        }

        docUpdate
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());

        Intent intent = new Intent(getApplicationContext(), HomeAdminActivity.class);
        startActivity(intent);
    }

    private void rimozioneEsamiDocente(String idDocente) {
        db.collection("esami").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Esame esame = new Esame(document.getString("id"),
                            document.getString("nome"),
                            document.getString("descrizione"),
                            document.getString("cDs"),
                            (ArrayList<String>) document.get("idDocenti"));

                    for (int i = 0; i < esame.getIdDocenti().size(); i++) {
                        if((esame.getIdDocenti().get(i)).equals(idDocente)) {

                            esame.getIdDocenti().remove(i);
                            Map<String ,Object> exam = new HashMap<>();

                            exam.put("id",esame.getId());
                            exam.put("descrizione",esame.getDescrizione());
                            exam.put("cDs",esame.getcDs());
                            exam.put("idDocenti",esame.getIdDocenti());
                            exam.put("nome", esame.getNome());

                            db.collection("esami").document(document.getId()).update(exam);
                        }
                    }
                }
            }
        });
    }

    private void rimozioneEsamiStudente(String idStudente) {
        db.collection("esamiStudente")
                .whereEqualTo("idStudente", idStudente)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    rimozioniProgetti(idStudente);
                    db.collection("esamiStudente").document(document.getId()).delete();
                }
            }
        });
    }

    private void rimozioniProgetti(String idStudente) {
        db.collection("progetti").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Progetto progetto = new Progetto(document.getString("id"),
                            document.getString("nome"),
                            document.getString("descrizione"),
                            document.getString("codiceEsame"),
                            document.getString("data"),
                            (ArrayList<String>) document.get("idStudenti"),
                            document.getBoolean("stato"),
                            document.getBoolean("valutato"));

                    for (int i = 0; i < progetto.getIdStudenti().size(); i++) {
                        if((progetto.getIdStudenti().get(i)).equals(idStudente)) {
                            if(progetto.getIdStudenti().size() == 1) {
                                rimozioniFileProgetto(storage, document.getId());
                                db.collection("progetti").document(document.getId()).delete();
                            } else {
                                progetto.getIdStudenti().remove(i);
                                Map<String ,Object> project = new HashMap<>();

                                project.put("codiceEsame",progetto.getCodiceEsame());
                                project.put("data",progetto.getDataCreazione());
                                project.put("descrizione",progetto.getDescrizione());
                                project.put("id",progetto.getId());
                                project.put("idStudenti",progetto.getIdStudenti());
                                project.put("nome", progetto.getNome());
                                project.put("stato", progetto.getStato());
                                project.put("valutato", progetto.isValutato());

                                db.collection("progetti").document(document.getId()).update(project);
                            }
                        }
                    }
                }
            }
        });
    }

    private void rimozioniFileProgetto(FirebaseStorage storage, String idProgetto) {
        StorageReference listRef = storage.getReference().child("progetti/" + idProgetto);
        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        prefix.delete();
                    }

                    for (StorageReference item : listResult.getItems()) {
                        item.delete();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "File project: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void viewImgProfile() {
        String defaultImgProfile = "imgProfile.png";
        File localFile = new File(getExternalFilesDir(null) + "/user media", defaultImgProfile);
        if(localFile.exists()) {
            profileImg.setImageURI(Uri.parse(localFile.getPath()));
        }else {
            downloadFile(defaultImgProfile, "file user/" + utente.getId());
        }
    }

    public void downloadFile(String nomeFile, String percorso) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(percorso + "/" + nomeFile);

        File localFile = new File(getExternalFilesDir(null) + "/user media", nomeFile);

        islandRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> profileImg.setImageURI(Uri.parse(localFile.getPath()))).addOnFailureListener(exception -> {
        }).addOnPausedListener(taskSnapshot -> {
        });
    }
}