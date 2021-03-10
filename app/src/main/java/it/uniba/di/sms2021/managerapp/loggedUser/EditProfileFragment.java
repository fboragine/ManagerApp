package it.uniba.di.sms2021.managerapp.loggedUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms2021.managerapp.loggedUser.EditProfileFragmentDirections;
import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Studente;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener{

    View vistaModifica;
    EditText editName;
    EditText editSurname;
    EditText editMatricola;
    EditText editEmail;
    EditText editPassword;

    Button btnEdit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String course;

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(StudentActivity.loginFile.getName().matches("studenti.srl")){
            course = StudentActivity.loggedStudent.getcDs();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vistaModifica = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        editName = (EditText) vistaModifica.findViewById(R.id.name);
        editName.setText(StudentActivity.loggedUser.getNome());

        editSurname = (EditText) vistaModifica.findViewById(R.id.surname);
        editSurname.setText(StudentActivity.loggedUser.getCognome());

        editMatricola = (EditText) vistaModifica.findViewById(R.id.serial_number);
        editMatricola.setText(StudentActivity.loggedUser.getMatricola());

        editPassword = (EditText) vistaModifica.findViewById(R.id.pw_txt);


        editEmail = (EditText) vistaModifica.findViewById(R.id.email_txt);
        editEmail.setText(StudentActivity.loggedUser.getEmail());

        btnEdit = (Button) vistaModifica.findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return vistaModifica;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
                Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        // Add Save Menu Item
        int saveId = StudentActivity.SAVE_ITEM_ID;
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
            save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    modificaFile();
                    NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
                    Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                    return true;
                }
            });

        }

        // Add Cancel Menu Item
        int cancelId = StudentActivity.CANCEL_ITEM_ID;
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
            cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.undone_save, Toast.LENGTH_SHORT).show();
                    NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
                    Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                    return true;
                }
            });
        }

        super.onPrepareOptionsMenu(menu);
    }

    public void saveFile(String FILE_NAME, Object oggetto) {

        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getContext().getExternalFilesDir(null), FILE_NAME)));
            out.writeObject(oggetto);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modificaFile() {

        StudentActivity.loggedUser.setNome(editName.getText().toString());
        StudentActivity.loggedUser.setCognome(editSurname.getText().toString());
        StudentActivity.loggedUser.setMatricola(editMatricola.getText().toString());

        if( !StudentActivity.loggedUser.getEmail().matches(editEmail.getText().toString()) ) {
            modificaAuth(true,editEmail.getText().toString(), StudentActivity.loggedUser.getEmail());
            StudentActivity.loggedUser.setEmail(editEmail.getText().toString());

        }
        if( !editPassword.getText().toString().matches("") ) {
            modificaAuth(false, editPassword.getText().toString(),null);
        }

        Map<String ,Object> userModify = new HashMap<>();

        userModify.put("cognome",StudentActivity.loggedUser.getCognome());
        userModify.put("email",StudentActivity.loggedUser.getEmail());
        userModify.put("id",mAuth.getCurrentUser().getUid());
        userModify.put("matricola",StudentActivity.loggedUser.getMatricola());
        userModify.put("nome",StudentActivity.loggedUser.getNome());
        userModify.put("percorsoImg", "");

        if(StudentActivity.loginFile.getName().matches("studenti.srl")) {

            StudentActivity.loggedStudent = new Studente(mAuth.getCurrentUser().getUid(),
                                                         StudentActivity.loggedUser.getMatricola(),
                                                         StudentActivity.loggedUser.getNome(),
                                                         StudentActivity.loggedUser.getCognome(),
                                                         StudentActivity.loggedUser.getEmail(),
                                                         course);

            userModify.put("cDs",course);

            StudentActivity.loginFile.delete();
            saveFile("studenti.srl", StudentActivity.loggedStudent);

            DocumentReference docUpdate = db.collection("studenti").document(StudentActivity.loggedUser.getId());
            modifyFirebase(docUpdate, userModify);

        }else if(StudentActivity.loginFile.getName().matches("docenti.srl")) {

            StudentActivity.loggedDocent = new Docente(
                    mAuth.getCurrentUser().getUid(),
                    StudentActivity.loggedUser.getMatricola(),
                    StudentActivity.loggedUser.getNome(),
                    StudentActivity.loggedUser.getCognome(),
                    StudentActivity.loggedUser.getEmail());

            StudentActivity.loginFile.delete();
            saveFile("docenti.srl", StudentActivity.loggedDocent);

            DocumentReference docUpdate = db.collection("docenti").document(mAuth.getCurrentUser().getUid());
            modifyFirebase(docUpdate, userModify);
        }

    }

    private void modificaAuth( boolean flag, String replace, String originalMail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(flag) {   //Modifica l'email
            user.updateEmail(replace)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()) {
                     Toast.makeText(getActivity().getApplicationContext(), R.string.email_changed, Toast.LENGTH_LONG).show();
                 }else
                 {
                     Toast.makeText(getActivity().getApplicationContext(), R.string.reauth_msg_email, Toast.LENGTH_LONG).show();
                     StudentActivity.loggedUser.setEmail(originalMail);
                 }
             }
            });

        }else {
            user.updatePassword(replace)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.password_changed, Toast.LENGTH_LONG).show();
                    }else
                    {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.weak_password, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void modifyFirebase(DocumentReference docUpdate, Map<String, Object> userModify) {

        docUpdate
        .update(userModify)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_edit){
            modificaFile();
            Toast.makeText(getActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show();
            NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
            Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
        }
    }
}