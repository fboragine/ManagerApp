package it.uniba.di.sms2021.managerapp.service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.entities.Utente;
import it.uniba.di.sms2021.managerapp.guest.GuestActivity;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;

public class Settings extends AppCompatActivity {

    private Toolbar toolbar;
    protected static File loginFile;
    private Button logout;
    protected static Studente loggedStudent;
    protected static Docente loggedDocent;
    protected static Utente loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.action_settings);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackFragment();
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            logout();
            Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
            startActivity(intent);
            finish();
        });

        loginFile = new File(getApplicationContext().getExternalFilesDir(null), "studenti.srl");
        if(loginFile.exists()) {
            readFile("studenti.srl");
            logout.setVisibility(View.VISIBLE);
        }else {
            loginFile = new File(getApplicationContext().getExternalFilesDir(null), "docenti.srl");
            if(loginFile.exists()) {
                readFile("docenti.srl");
                logout.setVisibility(View.VISIBLE);
            }
            else {
                logout.setVisibility(View.GONE);
            }
        }
    }

    protected void readFile(String filename){
        ObjectInputStream input;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(getExternalFilesDir(null),filename)));
            if(filename.matches("studenti.srl")) {
                loggedUser = new Studente();
                loggedUser = (Studente) input.readObject();

                loggedStudent = (Studente) loggedUser;
            }else if(filename.matches("docenti.srl")) {
                loggedUser = new Docente();
                loggedUser = (Docente) input.readObject();

                loggedDocent = (Docente) loggedUser;
            }
            input.close();
            Toast.makeText(getApplicationContext(), String.format("%s %s %s", getString(R.string.welcome_msg), loggedUser.getNome(), loggedUser.getCognome()),Toast.LENGTH_LONG).show();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        loginFile.delete();
        File projectFile = new File(getApplicationContext().getExternalFilesDir(null).getPath());
        deleteFolder(projectFile);
        Toast.makeText(this ,R.string.logout, Toast.LENGTH_SHORT).show();
    }

    /**La funzione pulisce la cartella di sistema eliminando la cartella dei media dei progetti e il file di log
     * per evitare che un login diverso possa accedere a file appartenenti ad un altro utente*/
    static void deleteFolder(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }

    public void goBackFragment() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}