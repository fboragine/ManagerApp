package it.uniba.di.sms2021.managerapp.guest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;

public class LoginFragment extends Fragment implements View.OnClickListener {

    View vistaLogin;
    Button btnLogin;
    Button btnResetPw;
    Button btnConfirmResetPw;
    EditText email;
    EditText password;
    LinearLayout passwordLayout;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((GuestActivity)getActivity()).disableBackArrow();

        // Inflate the layout for this
        vistaLogin = inflater.inflate(R.layout.fragment_login, container, false);

        btnLogin = (Button) vistaLogin.findViewById(R.id.buttonLogin);
        btnResetPw = (Button) vistaLogin.findViewById(R.id.btn_reset_password);
        email = (EditText)vistaLogin.findViewById(R.id.emailTxt);
        password = (EditText)vistaLogin.findViewById(R.id.passwordTxt);
        btnConfirmResetPw = (Button) vistaLogin.findViewById(R.id.reset_password_btn);
        passwordLayout = vistaLogin.findViewById(R.id.password_layout);

        btnLogin.setOnClickListener(this);
        btnResetPw.setOnClickListener(this);
        btnConfirmResetPw.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return vistaLogin;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonLogin)    //Click sul pulsante login
        {
            if(!email.getText().toString().matches("") &&
               !password.getText().toString().matches("")){
                login(email.getText().toString(), password.getText().toString());
            }
        }
        else if(view.getId() == R.id.btn_reset_password)    //Click sul pulsante di reset della pw
        {
            if(btnConfirmResetPw.getVisibility() == View.GONE){
                passwordLayout.setVisibility(View.INVISIBLE);

                btnConfirmResetPw.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.GONE);

                btnResetPw.setText(R.string.login_btn);

                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.msg_reset), Toast.LENGTH_LONG).show();
            }else {
                passwordLayout.setVisibility(View.VISIBLE);

                btnConfirmResetPw.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);

                btnResetPw.setText(R.string.btn_forgot_password);
            }
        }else if(view.getId() == R.id.reset_password_btn) {
            String emailAddress = email.getText().toString();

            if(!emailAddress.matches("")) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.email_sent), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }else {
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.error_email_sent), Toast.LENGTH_SHORT).show();
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
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) getContext(), task -> {
            if(task.isSuccessful()) //Login avvenuto con successo
            {
                FirebaseUser user;

                user = mAuth.getCurrentUser();
                getDataFromFireStore(user.getUid());
            }
            else {
                // If sign in fails, display a message to the user.
                Toast.makeText(getActivity().getApplicationContext(),getString(R.string.login_error_msg) + task.getException(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getDataFromFireStore(String id) {
        DocumentReference docRefStudenti = db.collection("studenti").document(id);

        docRefStudenti.get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Studente risultato;
                    risultato = new Studente(
                            (String) document.get("id"),
                            (String) document.get("matricola"),
                            (String) document.get("nome"),
                            (String) document.get("cognome"),
                            (String) document.get("email"),
                            (String) document.get("cDs"));
                    salvaSessione((Object)risultato, "studenti");
                } else {
                    DocumentReference docRefDocenti = db.collection("docenti").document(id);

                    docRefDocenti.get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task2 -> {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot document2 = task2.getResult();
                            if (document2.exists()) {
                                Docente risultato;
                                risultato = new Docente(
                                        (String) document2.get("id"),
                                        (String) document2.get("matricola"),
                                        (String) document2.get("nome"),
                                        (String) document2.get("cognome"),
                                        (String) document2.get("email"));
                                salvaSessione((Object) risultato, "docenti");
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.doc_not_found, Toast.LENGTH_LONG).show();
                            FirebaseUser userDelete = FirebaseAuth.getInstance().getCurrentUser();
                            userDelete.delete();

                            Intent intent = new Intent(getActivity().getApplicationContext(), GuestActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveFile(String FILE_NAME, Object oggetto) {
        ObjectOutput out;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getContext().getExternalFilesDir(null), FILE_NAME)));
            out.writeObject(oggetto);
            out.close();
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
        Intent intent = new Intent(getActivity().getApplicationContext(), StudentActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
        menu.findItem(R.id.action_search).setVisible(false);
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