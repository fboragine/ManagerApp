package it.uniba.di.sms2021.managerapp.service;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Locale;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.entities.Utente;
import it.uniba.di.sms2021.managerapp.guest.GuestActivity;

public class Settings extends AppCompatActivity {

    protected static File loginFile;
    protected static Studente loggedStudent;
    protected static Docente loggedDocent;
    protected static Utente loggedUser;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RadioButton linguaIta =  findViewById(R.id.radio_italian);
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.action_settings);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);

        toolbar.setNavigationOnClickListener(view -> goBackFragment());




        if(getBaseContext().getResources().getConfiguration().getLocales().get(0).equals(Locale.ITALIAN) || getBaseContext().getResources().getConfiguration().getLocales().get(0).equals(Locale.ITALY)) {
            linguaIta.setChecked(true);
        }

        linguaIta.setOnCheckedChangeListener((buttonView, isChecked) -> traduci(linguaIta.isChecked()));

        Button logout = findViewById(R.id.logout);
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

        Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
        startActivity(intent);
        finish();
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

        if(!loginFile.delete()) {
            Toast.makeText(getApplicationContext(), getString(R.string.not_delete), Toast.LENGTH_SHORT).show();
        }

        File projectFile = new File(getApplicationContext().getExternalFilesDir(null).getPath());
        deleteFolder(projectFile);
    }

    /**La funzione pulisce la cartella di sistema eliminando la cartella dei media dei progetti e il file di log
     * per evitare che un login diverso possa accedere a file appartenenti ad un altro utente*/
    void deleteFolder(File file){
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                if (!subFile.getName().equals("IT")) {
                    if (!subFile.delete()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.not_delete), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (!file.delete()) {
                Toast.makeText(getApplicationContext(), getString(R.string.not_delete), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goBackFragment() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
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