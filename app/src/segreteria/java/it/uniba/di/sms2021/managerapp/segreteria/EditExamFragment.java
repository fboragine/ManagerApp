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
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;

public class EditExamFragment extends Fragment {

    private EditText editName;
    private EditText editDescription;
    private final Esame examSelected;
    private FirebaseFirestore db;

    public EditExamFragment(Esame esame) {
        this.examSelected = esame;
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
        View vistaModifica = inflater.inflate(R.layout.fragment_edit_exam, container, false);

        ((HomeAdminActivity)requireActivity()).enableBackArrow();

        editName = vistaModifica.findViewById(R.id.name_exam);
        editName.setText(examSelected.getNome());

        editDescription = vistaModifica.findViewById(R.id.description_exam);
        editDescription.setText(examSelected.getDescrizione());

        Button deleteBtn = vistaModifica.findViewById(R.id.button_delete_exam);
        deleteBtn.setOnClickListener((view) -> deleteExam());
        db = FirebaseFirestore.getInstance();

        return vistaModifica;
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
                            Toast.makeText(requireActivity().getApplicationContext(), R.string.exam_delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(requireActivity().getApplicationContext(), R.string.no_exam_linked_error, Toast.LENGTH_SHORT).show();
                    db.collection("esami").document(this.examSelected.getId()).delete();
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());
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
                    Toast.makeText(requireActivity().getApplicationContext(), R.string.no_project_linked_error, Toast.LENGTH_SHORT).show();
                }
                db.collection("esamiStudente").document(idEsameStudente).delete();
                db.collection("esami").document(this.examSelected.getId()).delete();
                getActivity().finish();
                getActivity().startActivity(getActivity().getIntent());
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
            userModify.put("id", this.examSelected.getId());
            userModify.put("nome", editName.getText().toString());

            DocumentReference docUpdate = db.collection("esami").document(this.examSelected.getId());

            docUpdate
                    .update(userModify)
                    .addOnSuccessListener(aVoid -> Toast.makeText(requireActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(requireActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());
        }
    }
}
