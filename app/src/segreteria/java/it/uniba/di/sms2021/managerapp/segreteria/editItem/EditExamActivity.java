package it.uniba.di.sms2021.managerapp.segreteria.editItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class EditExamActivity extends AppCompatActivity {

    public static final int SAVE_ITEM_ID = View.generateViewId();
    public static final int DELETE_ITEM_ID = View.generateViewId();
    public static final int SETTINGS_ITEM_ID = View.generateViewId();

    private EditText editName;
    private EditText editDescription;
    private Esame examSelected;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exam);

        final Intent src = getIntent();
        if(src != null) {
            examSelected = src.getParcelableExtra("esame");
        }

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Edit Exam");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(view -> finish());

        editName = findViewById(R.id.name_exam);
        editName.setText(examSelected.getNome());

        editDescription = findViewById(R.id.description_exam);
        editDescription.setText(examSelected.getDescrizione());

        db = FirebaseFirestore.getInstance();
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
        // Add Save Menu Item
        int saveId = SAVE_ITEM_ID;
        if (menu.findItem(saveId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem save = menu.add(
                    Menu.NONE,
                    saveId,
                    1,
                    getString(R.string.save)
            );

            // Set an icon for the new menu item
            save.setIcon(R.drawable.ic_save);

            // Set the show as action flags for new menu item
            save.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            save.setOnMenuItemClickListener(item -> {
                modificaFile();
                Toast.makeText(getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            });

        }

        // Add Delete Menu Item
        int deleteId = DELETE_ITEM_ID;
        if (menu.findItem(deleteId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem delete = menu.add(
                    Menu.NONE,
                    deleteId,
                    2,
                    getString(R.string.delete)
            );

            // Set an icon for the new menu item
            delete.setIcon(R.drawable.ic_delete);

            // Set the show as action flags for new menu item
            delete.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            delete.setOnMenuItemClickListener(item -> {
                deleteExam();
                Toast.makeText(getApplicationContext(), R.string.exam_deleted, Toast.LENGTH_SHORT).show();
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

    /**
     * L'esame può essere rimosso SOLO SE non possiede alcuno studente registrato o se tutti
     * gli studenti sono stati promossi.
     * La rimozione dell'esame causa le seguenti modifiche:
     * - Cancellazione del document dalla tabella esami;
     * - Cancellazione di tutti i progetti e i relativi collegamenti del esame agli studenti;
     */
    private synchronized void deleteExam() {
        db.collection("esami")
                .document(this.examSelected.getId())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                rimozioneEsamiStudenti();
            }
        });
    }

    private synchronized void rimozioneEsamiStudenti() {
        db.collection("esamiStudente")
                .whereEqualTo("idEsame", this.examSelected.getId())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(Objects.requireNonNull(task.getResult()).size() != 0) {
                    for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (Objects.requireNonNull(document.getBoolean("stato"))) {
                            rimozioneProgettiEsame(document.getId());
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.exam_delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    db.collection("esami").document(this.examSelected.getId()).delete();
                }
            }
        });
    }

    private synchronized void rimozioneProgettiEsame(String idEsameStudente) {

        db.collection("progetti")
                .whereEqualTo("codiceEsame", this.examSelected.getId())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(Objects.requireNonNull(task.getResult()).size() != 0) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        rimozioniFileProgetto(storage, document.getId());
                    }
                }else {
                    Toast.makeText(getApplicationContext(), R.string.no_project_linked_error, Toast.LENGTH_SHORT).show();
                }
                db.collection("esamiStudente").document(idEsameStudente).delete();
                db.collection("esami").document(this.examSelected.getId()).delete();
            }
        });
    }

    private synchronized void rimozioniFileProgetto(FirebaseStorage storage, String idProgetto) {
        StorageReference listRef = storage.getReference().child("progetti/" + idProgetto);
        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        prefix.delete();
                    }

                    for (StorageReference item : listResult.getItems()) {
                        item.delete();
                    }

                    db.collection("progetti").document(idProgetto).delete();
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "File project: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void modificaFile() {
        if( !editName.getText().toString().matches("") &&
                !editDescription.getText().toString().matches("")) {
            Map<String ,Object> userModify = new HashMap<>();

            userModify.put("descrizione",editDescription.getText().toString());
            userModify.put("id", this.examSelected.getId());
            userModify.put("nome", editName.getText().toString());

            DocumentReference docUpdate = db.collection("esami").document(this.examSelected.getId());

            docUpdate
                    .update(userModify)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());
        }
    }
}