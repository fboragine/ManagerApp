package it.uniba.di.sms2021.managerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import entities.Studente;

import static java.security.AccessController.getContext;

public class SignActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                register(v);
            case R.id.btn_already_reg:
                go_to_login(v);
        }
    }

    public void go_to_login(View v) {
        /*
        Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
        startActivity(intent);

         */
        finish();
    }

    public void register(View v) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText mat = (EditText) findViewById(R.id.serial_number);
        EditText nome = (EditText) findViewById(R.id.name);
        EditText cognome = (EditText) findViewById(R.id.surname);
        EditText email = (EditText) findViewById(R.id.email);
        EditText cDs = (EditText) findViewById(R.id.course);

        EditText pw = (EditText) findViewById(R.id.password);



        Studente aux = new Studente(mat.getText().toString(),
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



        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(aux.getEmail(), pw.getText().toString());

        user.put("id", mAuth.getUid());

        //Getting Reference to "users" collection
        //REMEMBER: at this point, no collection will be created
        CollectionReference collectionReference = db.collection("studenti");

        //Getting reference to "new doc" document
        //REMEMBER: at this point, no document will be created either

        DocumentReference documentReference = collectionReference.document(mAuth.getUid());
        documentReference.set(user);
    }
}