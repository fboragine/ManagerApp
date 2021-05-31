package it.uniba.di.sms2021.managerapp.segreteria.courses;

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
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;

public class EditCourseFragment extends Fragment {

    private EditText editName;
    private EditText editDescription;
    private Button deleteBtn;
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

        editName = vistaModifica.findViewById(R.id.name_course);
        editName.setText(cDsSelected.getNome());

        editDescription = vistaModifica.findViewById(R.id.description_course);
        editDescription.setText(cDsSelected.getDescrizione());

        deleteBtn = vistaModifica.findViewById(R.id.button_delete_course);
        deleteBtn.setOnClickListener((view) -> {
            deleteCds();
            getActivity().finish();
            getActivity().startActivity(getActivity().getIntent());
        });
        db = FirebaseFirestore.getInstance();

        return vistaModifica;
    }

    /**
     * La rimozione del CDS causa le seguenti modifiche:
     * - Cancellazione del document dalla tabella corsiDiStudio;
     * - Cancellazione di tutti gli esami appartenenti al corso di studio;
     * - Annullamento del corso di studio a cui partecipa lo studente (cDs = "")
     */
    private void deleteCds() {
        db.collection("corsiDiStudio").document(this.cDsSelected.getIdCorsoDiStudio()).delete();

        db.collection("esami")
                .whereEqualTo("cDs", this.cDsSelected.getIdCorsoDiStudio())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot document : task.getResult()){
                            db.collection("esami").document(document.getId()).delete();
                        }
                    }
                });

        db.collection("studenti")
                .whereEqualTo("cDs", this.cDsSelected.getIdCorsoDiStudio())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : task.getResult()){
                    db.collection("studenti").document(document.getId()).update("cDs","");
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_settings).setVisible(false);

        inflater.inflate(R.menu.toolbar_menu, menu);

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
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(R.id.action_editProfileAdminFragment_to_profileAdminFragment);
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
