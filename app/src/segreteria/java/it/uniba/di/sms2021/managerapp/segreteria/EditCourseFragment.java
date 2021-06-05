package it.uniba.di.sms2021.managerapp.segreteria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;

public class EditCourseFragment extends Fragment {

    private EditText editName;
    private EditText editDescription;
    private final CorsoDiStudio cDsSelected;
    private FirebaseFirestore db;

    public EditCourseFragment(CorsoDiStudio corsoDiStudio) {
        this.cDsSelected = corsoDiStudio;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vistaModifica = inflater.inflate(R.layout.fragment_edit_course, container, false);

        ((HomeAdminActivity)requireActivity()).enableBackArrow();

        editName = vistaModifica.findViewById(R.id.name_course);
        editName.setText(cDsSelected.getNome());

        editDescription = vistaModifica.findViewById(R.id.description_course);
        editDescription.setText(cDsSelected.getDescrizione());

        Button deleteBtn = vistaModifica.findViewById(R.id.button_delete_course);
        deleteBtn.setOnClickListener((view) -> deleteCds());
        db = FirebaseFirestore.getInstance();

        return vistaModifica;
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
                    Toast.makeText(requireActivity().getApplicationContext(), R.string.cds_with_student_error, Toast.LENGTH_SHORT).show();
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

                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());
                })
                .addOnFailureListener(e -> Toast.makeText(requireActivity().getApplicationContext(), "File project: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Add Save Menu Item
        int saveId = HomeAdminActivity.SAVE_ITEM_ID;
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
                Toast.makeText(requireActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show();
                getActivity().finish();
                getActivity().startActivity(getActivity().getIntent());
                return true;
            });

        }

        // Add Cancel Menu Item
        int cancelId = HomeAdminActivity.CANCEL_ITEM_ID;
        if (menu.findItem(cancelId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem cancel = menu.add(
                    Menu.NONE,
                    cancelId,
                    2,
                    getString(R.string.cancel)
            );

            // Set an icon for the new menu item
            cancel.setIcon(R.drawable.ic_edit_off);

            // Set the show as action flags for new menu item
            cancel.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            cancel.setOnMenuItemClickListener(item -> {
                Toast.makeText(requireContext().getApplicationContext(), R.string.undone_save, Toast.LENGTH_SHORT).show();
                getActivity().finish();
                getActivity().startActivity(getActivity().getIntent());
                return true;
            });
        }
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
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());
        }
    }
}
