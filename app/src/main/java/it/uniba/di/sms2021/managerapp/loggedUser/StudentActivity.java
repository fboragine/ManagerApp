package it.uniba.di.sms2021.managerapp.loggedUser;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import it.uniba.di.sms2021.managerapp.guest.GuestActivity;
import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.entities.Utente;

public class StudentActivity extends AppCompatActivity {

    protected static final int EDIT_ITEM_ID = View.generateViewId();
    protected static final int SAVE_ITEM_ID = View.generateViewId();
    protected static final int CANCEL_ITEM_ID = View.generateViewId();
    protected static final int FILTER_ITEM_ID = View.generateViewId();
    protected static Utente loggedUser;
    protected static File loginFile;
    protected static Studente loggedStudent;
    protected static Docente loggedDocent;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Home");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(findViewById(R.id.fragment));
        AppBarConfiguration appBarConfiguration = (new AppBarConfiguration.Builder(Set.of(R.id.homeFragment, R.id.librettoFragment, R.id.profileFragment))).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        final Intent src = getIntent();
        if(src != null) {
            src.getParcelableArrayListExtra("progetti");
        }

        loginFile = new File(getApplicationContext().getExternalFilesDir(null), "studenti.srl");
        if(loginFile.exists()) {
            readFile("studenti.srl");
            createUserMediaDir();
        }else {
            loginFile = new File(getApplicationContext().getExternalFilesDir(null), "docenti.srl");
            if(loginFile.exists()) {
                readFile("docenti.srl");
                createUserMediaDir();
            }else {
                Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void createUserMediaDir() {
        String path = getExternalFilesDir(null).getPath() + "/user media/";
        File f1 = new File(path);
        f1.mkdir();
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
}