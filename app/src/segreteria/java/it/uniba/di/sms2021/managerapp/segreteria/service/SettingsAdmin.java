package it.uniba.di.sms2021.managerapp.segreteria.service;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;
import it.uniba.di.sms2021.managerapp.segreteria.admin.LoginAdminActivity;

public class SettingsAdmin extends AppCompatActivity {

    protected static File loginFile;

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
        });

        loginFile = new File(getApplicationContext().getExternalFilesDir(null), "segreteria.srl");
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

        Intent intent = new Intent(getApplicationContext(), LoginAdminActivity.class);
        startActivity(intent);
        finish();
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

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        HomeAdminActivity.getLoginFile().delete();
        Toast.makeText(getApplicationContext(),R.string.logout, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginAdminActivity.class);
        startActivity(intent);
        finish();
    }
}