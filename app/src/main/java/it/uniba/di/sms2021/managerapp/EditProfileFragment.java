package it.uniba.di.sms2021.managerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import entities.Docente;
import entities.Studente;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener{

    View vistaModifica;
    EditText editName;
    EditText editMatricola;
    EditText editEmail;
    EditText editCourse;

    Button btnEdit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        vistaModifica = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        editName = (EditText) vistaModifica.findViewById(R.id.full_name);
        editName.setText(StudentActivity.loggedUser.getNome() + " " + StudentActivity.loggedUser.getCognome());

        editMatricola = (EditText) vistaModifica.findViewById(R.id.serial_number);
        editMatricola.setText(StudentActivity.loggedUser.getMatricola());

        editEmail = (EditText) vistaModifica.findViewById(R.id.email_txt);
        editEmail.setText(StudentActivity.loggedUser.getEmail());

        editCourse = (EditText) vistaModifica.findViewById(R.id.course_txt);
        editCourse.setText(StudentActivity.loggedStudent.getcDs());

        if(!StudentActivity.loginFile.getName().matches("studenti.srl")){
            vistaModifica.findViewById(R.id.course_txt).setVisibility(View.INVISIBLE);
            vistaModifica.findViewById(R.id.course_img).setVisibility(View.INVISIBLE);
        }

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
                    Toast.makeText(getActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show();
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

        if(StudentActivity.loginFile.getName().matches("studenti.srl")) {

            Map<String ,Object> userModify = new HashMap<>();
            Studente tempStudent = new Studente(editMatricola.getText().toString(),
                                                "nome",
                                                "cognome",
                                                editEmail.getText().toString(),
                                                editCourse.getText().toString());

            userModify.put("cDs",editCourse.getText().toString());
            //userModify.put("cognome",StudentActivity.loggedStudent.getCognome());
            userModify.put("email",editEmail.getText().toString());
            userModify.put("id",StudentActivity.loggedStudent.getId());
            userModify.put("matricola",editMatricola.getText().toString());
            //userModify.put("nome",StudentActivity.loggedStudent.getNome());
            userModify.put("percorsoImg", "");

            DocumentReference docUpdate = db.collection("studenti").document(StudentActivity.loggedStudent.getId());
            modifyFirebase(docUpdate, userModify);

            StudentActivity.loginFile.delete();
            saveFile("studenti.srl", tempStudent);

            getActivity().finish();
            getActivity().startActivity(getActivity().getIntent());

        }else if(StudentActivity.loginFile.getName().matches("docenti.srl")) {

            Map<String ,Object> userModify = new HashMap<>();
            Docente tempDocent = new Docente(editMatricola.getText().toString(),
                    "nome",
                    "cognome",
                    editEmail.getText().toString());

            //userModify.put("cognome",StudentActivity.loggedStudent.getCognome());
            userModify.put("email",StudentActivity.loggedStudent.getEmail());
            userModify.put("id",StudentActivity.loggedStudent.getId());
            userModify.put("matricola",StudentActivity.loggedStudent.getMatricola());
            //userModify.put("nome",StudentActivity.loggedStudent.getNome());
            userModify.put("percorsoImg", "");

            StudentActivity.loginFile.delete();
            saveFile("docenti.srl", tempDocent);

            DocumentReference docUpdate = db.collection("docenti").document(StudentActivity.loggedDocent.getId());
            modifyFirebase(docUpdate, userModify);


        }
    }

    private void modifyFirebase(DocumentReference docUpdate, Map<String, Object> userModify) {

        docUpdate
        .update(userModify)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
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