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
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class EditCourseActivity extends AppCompatActivity {

    public static final int SAVE_ITEM_ID = View.generateViewId();
    public static final int DELETE_ITEM_ID = View.generateViewId();
    public static final int SETTINGS_ITEM_ID = View.generateViewId();

    private EditText editName;
    private EditText editDescription;
    private CorsoDiStudio cDsSelected;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        final Intent src = getIntent();
        if(src != null) {
            cDsSelected = src.getParcelableExtra("cDs");
        }

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Edit Course");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(view -> finish());

        editName = findViewById(R.id.name_course);
        editName.setText(cDsSelected.getNome());

        editDescription = findViewById(R.id.description_course);
        editDescription.setText(cDsSelected.getDescrizione());

        db = FirebaseFirestore.getInstance();
    }

    /**
     * La rimozione del CDS NON VIENE EFFETTUATA se vi è la presenza di studenti iscritti al CDS
     * La rimozione del CDS causa le seguenti modifiche:
     * - Cancellazione del document dalla tabella corsiDiStudio;
     * - Cancellazione di tutti gli esami appartenenti al corso di studio;
     * - Cancellazione di tutti i progetti e le relative cartelle collegati agli esami rimossi;
     */
    private void deleteCds() {
        db.collection("studenti")
                .whereEqualTo("cDs", this.cDsSelected.getIdCorsoDiStudio())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(Objects.requireNonNull(task.getResult()).isEmpty()) {
                    rimozioneEsami();
                    db.collection("corsiDiStudio").document(this.cDsSelected.getIdCorsoDiStudio()).delete();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.cds_with_student_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void rimozioneEsami() {
        db.collection("esami")
                .whereEqualTo("cDs", this.cDsSelected.getIdCorsoDiStudio())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    rimozioneEsamiStudente(document.getId());
                    db.collection("esami").document(document.getId()).delete();
                }
            }
        });
    }

    private void rimozioneEsamiStudente(String idEsame) {
        db.collection("esamiStudente")
                .whereEqualTo("idEsame", idEsame)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    rimozioniProgetti(idEsame);
                    db.collection("esamiStudente").document(document.getId()).delete();
                }
            }
        });
    }

    private void rimozioniProgetti(String idEsame) {
        db.collection("progetti")
                .whereEqualTo("codiceEsame", idEsame)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();

                for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    rimozioniFileProgetto(storage, document.getId());
                    db.collection("progetti").document(document.getId()).delete();
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

    private void modificaFile() {
        if( !editName.getText().toString().matches("") &&
                !editDescription.getText().toString().matches("")) {
            Map<String ,Object> userModify = new HashMap<>();

            userModify.put("descrizione",editDescription.getText().toString());
            userModify.put("id", this.cDsSelected.getIdCorsoDiStudio());
            userModify.put("nome", editName.getText().toString());

            DocumentReference docUpdate = db.collection("corsiDiStudio").document(this.cDsSelected.getIdCorsoDiStudio());

            docUpdate
                    .update(userModify)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());
        }
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
                deleteCds();
                Toast.makeText(getApplicationContext(), R.string.course_deleted, Toast.LENGTH_SHORT).show();
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
}