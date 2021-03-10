package it.uniba.di.sms2021.managerapp.guest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Studente;

public class SignActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.do_registration:
                register();
                break;
            case R.id.cancel_registration:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void register() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        EditText nome = (EditText) findViewById(R.id.name);
        EditText cognome = (EditText) findViewById(R.id.surname);
        EditText matricola = (EditText) findViewById(R.id.serial_number);
        EditText cDs = (EditText) findViewById(R.id.course);
        EditText email = (EditText) findViewById(R.id.email);
        EditText pw = (EditText) findViewById(R.id.password);

        Studente aux = new Studente("",
                matricola.getText().toString(),
                nome.getText().toString(),
                cognome.getText().toString(),
                email.getText().toString(),
                cDs.getText().toString());

        Map<String ,String> user = new HashMap<>();
        user.put("nome",aux.getNome());
        user.put("cognome",aux.getCognome());
        user.put("email",aux.getEmail());
        user.put("matricola",aux.getMatricola());
        user.put("cDs",aux.getcDs());
        user.put("percorsoImg", "");

        //Getting Reference to "studenti" collection
        CollectionReference collectionReference = db.collection("studenti");

        //crea l'autentication e inserisce l'utente nel firebase

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), pw.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                user.put("id", mAuth.getCurrentUser().getUid());

                DocumentReference documentReference = collectionReference.document(mAuth.getCurrentUser().getUid());
                documentReference.set(user);

                // entrare nella activity da loggato
                finish();
            }
        });
    }
}