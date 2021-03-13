package it.uniba.di.sms2021.managerapp.segreteria;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;

public class EditProfileAdminFragment extends Fragment {

    private EditText editEmail;
    private EditText editPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public EditProfileAdminFragment() {
        // Required empty public constructor
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
        View vistaModifica = inflater.inflate(R.layout.fragment_edit_profile_admin, container, false);

        editPassword = vistaModifica.findViewById(R.id.pw_txt);

        editEmail = vistaModifica.findViewById(R.id.email_txt);
        editEmail.setText(HomeAdminActivity.getLoggedAdmin().getEmail());

        mAuth = FirebaseAuth.getInstance();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(requireActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
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
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(R.id.action_editProfileAdminFragment_to_profileAdminFragment);
                return true;
            });
        }
    }

    public void saveFile(String FILE_NAME, Object oggetto) {
        ObjectOutput out;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(requireContext().getExternalFilesDir(null), FILE_NAME)));
            out.writeObject(oggetto);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modificaFile() {
        if( !HomeAdminActivity.getLoggedAdmin().getEmail().matches(editEmail.getText().toString()) ) {
            modificaAuth(true, editEmail.getText().toString(), HomeAdminActivity.getLoggedAdmin().getEmail());
            HomeAdminActivity.setLoggedAdmin(new Segreteria(HomeAdminActivity.getLoggedAdmin().getId(), editEmail.getText().toString()));
        }
        if( !editPassword.getText().toString().matches("") ) {
            modificaAuth(false, editPassword.getText().toString(),null);
        }

        Map<String ,Object> userModify = new HashMap<>();

        userModify.put("email",HomeAdminActivity.getLoggedAdmin().getEmail());
        userModify.put("id", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        if(HomeAdminActivity.getLoginFile().getName().matches("segreteria.srl")) {
            HomeAdminActivity.setLoggedAdmin(new Segreteria(mAuth.getCurrentUser().getUid(), HomeAdminActivity.getLoggedAdmin().getEmail()));

            HomeAdminActivity.getLoginFile().delete();
            saveFile("segreteria.srl", HomeAdminActivity.getLoggedAdmin());

            DocumentReference docUpdate = db.collection("segreteria").document(HomeAdminActivity.getLoggedAdmin().getId());
            modifyFirebase(docUpdate, userModify);
        }
    }

    private void modificaAuth( boolean flag, String replace, String originalMail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(flag) {   //Modifica l'email
            user.updateEmail(replace)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity().getApplicationContext(), R.string.email_changed, Toast.LENGTH_LONG).show();
                        }else
                        {
                            Toast.makeText(requireActivity().getApplicationContext(), R.string.reauth_msg_email, Toast.LENGTH_LONG).show();
                            HomeAdminActivity.setLoggedAdmin(new Segreteria(HomeAdminActivity.getLoggedAdmin().getId(), originalMail));
                        }
                    });
        }else {
            user.updatePassword(replace)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity().getApplicationContext(), R.string.password_changed, Toast.LENGTH_LONG).show();
                        }else
                        {
                            Toast.makeText(requireActivity().getApplicationContext(), R.string.weak_password, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void modifyFirebase(DocumentReference docUpdate, Map<String, Object> userModify) {
        docUpdate
                .update(userModify)
                .addOnSuccessListener(aVoid -> Toast.makeText(requireActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());
    }
}