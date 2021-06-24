package it.uniba.di.sms2021.managerapp.segreteria.admin;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Locale;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.segreteria.entities.Segreteria;

import static android.content.ContentValues.TAG;

public class LoginAdminActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        File file = new File(getApplicationContext().getExternalFilesDir(null), "IT");
        traduci(file.exists());

        String pathSegreteria = getExternalFilesDir(null).getPath() + "/segreteria.srl";
        File loggedSegreteria = new File(pathSegreteria);

        if(Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null).listFiles()).length == 0 || !loggedSegreteria.exists()){
            Button btnLogin = findViewById(R.id.buttonLogin);
            Button btnResetPsw = findViewById(R.id.btn_reset_password_admin);
            Button btnResetPswConfirm = findViewById(R.id.btn_reset_password_confirm);
            LinearLayout layout = findViewById(R.id.pw_layout);

            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            btnResetPswConfirm.setVisibility(View.GONE);
            email = findViewById(R.id.emailTxt);
            password = findViewById(R.id.passwordTxt);

            View.OnClickListener listener = v -> {
                if (v.getId() == R.id.buttonLogin) {
                    if (!email.getText().toString().matches("") &&
                            !password.getText().toString().matches("")) {
                        login(email.getText().toString(), password.getText().toString());
                    }
                } else if (v.getId() == R.id.btn_reset_password_admin) {
                    if(layout.getVisibility() == View.VISIBLE) {
                        layout.setVisibility(View.INVISIBLE);
                        btnResetPsw.setText(R.string.login_btn);
                        btnLogin.setVisibility(View.GONE);
                        btnResetPswConfirm.setVisibility(View.VISIBLE);
                    }else {
                        layout.setVisibility(View.VISIBLE);
                        btnLogin.setVisibility(View.VISIBLE);
                        btnResetPswConfirm.setVisibility(View.GONE);
                        btnResetPsw.setText(R.string.btn_forgot_password);
                        email.setText("");
                        password.setText("");
                    }
                }else if(v.getId() == R.id.btn_reset_password_confirm) {
                    String emailAddress = email.getText().toString();
                    if(!emailAddress.matches("")) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.email_sent), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else {
                        Toast.makeText(getApplicationContext(),getString(R.string.error_email_sent), Toast.LENGTH_SHORT).show();
                    }
                }
            };

            btnLogin.setOnClickListener(listener);
            btnResetPsw.setOnClickListener(listener);
            btnResetPswConfirm.setOnClickListener(listener);
        } else {
            Intent intent = new Intent(getApplicationContext(), HomeAdminActivity.class);
            startActivity(intent);
            finish();
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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) //Login avvenuto con successo
            {
                FirebaseUser user;

                user = mAuth.getCurrentUser();
                getDataFromFireStore(user.getUid(),"segreteria");
            }
            else {
                // If sign in fails, display a message to the user.
                Toast.makeText(getApplicationContext(),getString(R.string.login_error_msg) + task.getException(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getDataFromFireStore(String id, String collectionPath) {

        DocumentReference docRef = db.collection(collectionPath).document(id);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    if(collectionPath.matches("segreteria")){
                        Segreteria risultato;
                        risultato = new Segreteria(
                                (String) document.get("id"),
                                (String) document.get("email"));
                        salvaSessione(risultato, collectionPath);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.doc_not_found, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, getString(R.string.firebase_studente_error), task.getException());
            }
        });
    }

    public void saveFile(String FILE_NAME, Object oggetto) {

        ObjectOutput out;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getExternalFilesDir(null), FILE_NAME)));
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
        Intent intent = new Intent(getApplicationContext(), HomeAdminActivity.class);
        startActivity(intent);
        finish();
    }

    public void traduci(Boolean flag) {
        Locale locale;
        if (!flag) {
            File file = new File(getApplicationContext().getExternalFilesDir(null), "IT");
            locale = Locale.ENGLISH;
            file.delete();
            saveFile("EN");
        } else {
            File file = new File(getApplicationContext().getExternalFilesDir(null), "EN");
            locale = Locale.ITALIAN;
            file.delete();
            saveFile("IT");
        }
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    public void saveFile(String FILE_NAME) {
        ObjectOutput out;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getBaseContext().getExternalFilesDir(null), FILE_NAME)));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}