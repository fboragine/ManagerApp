package it.uniba.di.sms2021.managerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import entities.*;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static final String fileCacheAuth= "authCredential";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Bottoni on listener
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //controllare l'evento del click del bottone, dopo cliccato prendi
        //email e password dal bottone e avvia la funzione login
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

        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    //salvaCacheFile((Object) user);
                    //Switch alla home passando l'utente ottenuto

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void register(Object newUser, boolean flag) throws NoSuchFieldException {

        if(!flag)   //Registrazione professore
        {
            //registrazione al database professore
        }else   //Registrazione studente
        {
            //registrazione al database Studente
        }
        String email = newUser.getClass().getField("email").toString();
        String password = newUser.getClass().getField("password").toString();

        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    //salvaCacheFile((Object) user, flag);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        deleteFile(fileCacheAuth);
        Toast.makeText(LoginActivity.this, "Logout effettuato con successo!",
                Toast.LENGTH_SHORT).show();
        //Richiama l'activity ospite.
    }
}
