package it.uniba.di.sms2021.managerapp.segreteria.courses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;
import it.uniba.di.sms2021.managerapp.service.Settings;

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

        editName = vistaModifica.findViewById(R.id.name_course);
        editName.setText(cDsSelected.getNome());

        editDescription = vistaModifica.findViewById(R.id.description_course);
        editDescription.setText(cDsSelected.getDescrizione());

        db = FirebaseFirestore.getInstance();

        return vistaModifica;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity().getApplicationContext(), Settings.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(R.id.action_editProfileAdminFragment_to_profileAdminFragment);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
