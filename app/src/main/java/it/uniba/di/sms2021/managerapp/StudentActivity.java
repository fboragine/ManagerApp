package it.uniba.di.sms2021.managerapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Set;

import entities.Docente;
import entities.Progetto;
import entities.Studente;
import entities.Utente;

import static android.content.ContentValues.TAG;

public class StudentActivity extends AppCompatActivity {

    protected static final int EDIT_ITEM_ID = View.generateViewId();
    protected static final int SAVE_ITEM_ID = View.generateViewId();
    protected static final int CANCEL_ITEM_ID = View.generateViewId();
    protected static final int LOGOUT_ITEM_ID = View.generateViewId();
    protected static Utente loggedUser;
    protected static File loginFile;
    protected static Studente loggedStudent;
    protected static Docente loggedDocent;
    private ArrayList<Progetto> progetti;

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
            progetti = src.getParcelableArrayListExtra("progetti");
        }

        /*
        loginFile = new File(getApplicationContext().getExternalFilesDir(null), "studenti.srl");
        if(loginFile.exists()) {
            readFile("studenti.srl");
        }else {
            loginFile = new File(getApplicationContext().getExternalFilesDir(null), "docenti.srl");
            if(loginFile.exists()) {
                readFile("docenti.srl");
            }else {
                Intent intent = new Intent(getApplicationContext(), GuestActivity.class);;
                startActivity(intent);
                finish();
            }
        }

         */

       /* Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra("MyClass", obj);*/

    }

    public void readFile(String filename){
        ObjectInputStream input;

        try {
            input = new ObjectInputStream(new FileInputStream(new File(getExternalFilesDir(null),filename)));
            if(filename.toString().matches("studenti.srl")) {
                loggedUser = new Studente();
                loggedUser = (Studente) input.readObject();

                loggedStudent = (Studente) loggedUser;
            }else if(filename.toString().matches("docenti.srl")) {
                loggedUser = new Docente();
                loggedUser = (Docente) input.readObject();
            }
            input.close();
            Toast.makeText(getApplicationContext(), String.format("%s %s %s", getString(R.string.welcome_msg), loggedUser.getNome(), loggedUser.getCognome()),Toast.LENGTH_LONG).show();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(StudentActivity.this,R.string.logout, Toast.LENGTH_SHORT).show();
        //Richiama l'activity ospite.
    }
}