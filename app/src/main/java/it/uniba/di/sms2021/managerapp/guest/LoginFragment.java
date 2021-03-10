package it.uniba.di.sms2021.managerapp.guest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    View vistaLogin;
    Button btnLogin;
    Button btnResetPw;
    EditText email;
    EditText password;
    RadioButton studenteLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public LoginFragment() {
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

        // Inflate the layout for this
        vistaLogin = inflater.inflate(R.layout.fragment_login, container, false);

        btnLogin = (Button) vistaLogin.findViewById(R.id.buttonLogin);
        btnResetPw = (Button) vistaLogin.findViewById(R.id.btn_reset_password);

        studenteLogin = (RadioButton) vistaLogin.findViewById(R.id.radio_student);

        btnLogin.setOnClickListener(this);
        btnResetPw.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        return vistaLogin;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.buttonLogin)    //Click sul pulsante login
        {
            email = (EditText)vistaLogin.findViewById(R.id.emailTxt);
            password = (EditText)vistaLogin.findViewById(R.id.passwordTxt);
            if(!email.getText().toString().matches("") &&
               !password.getText().toString().matches("")){

                login(email.getText().toString(), password.getText().toString());
            }
        }
        else if(view.getId() == R.id.btn_reset_password)    //Click sul pulsante di reset della pw
        {
            //reset della password
        }
    }

    /**
     * Funzione di login al database Firebase
     * La funzione confonta i dati relativi al login (parametri) e controlla se l'utente
     * corrispondente alle credenziali è nel database, in caso di successo le credenziali e il token
     * di accesso viene salvato nei file cache (vedi slide persistenza), in caso di login fallito
     * visualizza un toast message che informa l'utente.
     * N.B: In caso di logout i file di cache delle credenziali vengono cancellati
     *
     * @param email: email registrata nel database;
     * @param password : password utilizzata per l'accesso; "password non in chiaro nel database"
     */
    public void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) //Login avvenuto con successo
                {
                    FirebaseUser user;

                    user = mAuth.getCurrentUser();
                    if(studenteLogin.isChecked()){
                        getDataFromFireStore(user.getUid(),"studenti");
                    }else {
                        getDataFromFireStore(user.getUid(),"docenti");
                    }

                }
                else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity().getApplicationContext(),getString(R.string.login_error_msg) + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getDataFromFireStore(String id, String collectionPath) {

        DocumentReference docRef = db.collection(collectionPath).document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        if(collectionPath.toString().matches("studenti")){
                            Studente risultato;
                            risultato = new Studente(
                                    (String) document.get("id"),
                                    (String) document.get("matricola"),
                                    (String) document.get("nome"),
                                    (String) document.get("cognome"),
                                    (String) document.get("email"),
                                    (String) document.get("cDs"));
                            salvaSessione((Object)risultato, collectionPath);

                        } else if(collectionPath.toString().matches("docenti")){
                            Docente risultato;
                            risultato = new Docente(
                                    (String) document.get("id"),
                                    (String) document.get("matricola"),
                                    (String) document.get("nome"),
                                    (String) document.get("cognome"),
                                    (String) document.get("email"));
                            salvaSessione((Object)risultato, collectionPath);
                        }

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.doc_not_found, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, getString(R.string.firebase_studente_error), task.getException());
                }

            }
        });
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

    public void salvaSessione(Object logged, String collectionPath) {
        String FILENAME = String.format("%s.srl", collectionPath);

        saveFile(FILENAME, logged);
        trasferisciIstanza();
    }

    public void trasferisciIstanza() {
        Intent intent = new Intent(getActivity().getApplicationContext(), StudentActivity.class);;
        startActivity(intent);
        getActivity().finish();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}