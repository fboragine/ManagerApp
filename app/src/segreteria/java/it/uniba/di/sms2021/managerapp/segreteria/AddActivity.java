package it.uniba.di.sms2021.managerapp.segreteria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;

public class AddActivity extends AppCompatActivity {

    private String[] items;
    private AddExamFragment examFragment;
    private AddCourseFragment courseFragment;
    private AddTeacherFragment teacherFragment;
    private Spinner spinner;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.top_register_item_toolbar);
        setSupportActionBar(toolbar);

        // Add the drop down menu
        items = new String[]{getString(R.string.teacher), getString(R.string.exam), getString(R.string.course)};
        spinner = findViewById(R.id.itemSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cambio del fragment
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (position) {
                    case 0:
                        teacherFragment = new AddTeacherFragment();
                        transaction.replace(R.id.currentFrame, teacherFragment);
                        break;
                    case 1:
                        examFragment = new AddExamFragment();
                        transaction.replace(R.id.currentFrame, examFragment);
                        break;
                    case 2:
                        courseFragment = new AddCourseFragment();
                        transaction.replace(R.id.currentFrame, courseFragment);
                        break;
                }
                transaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO implementare default?
            }
        });

        // Add the fragment
        teacherFragment = new AddTeacherFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.currentFrame, teacherFragment);
        transaction.commit();
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
                // TODO implementare aggiunta item al db
                switch (spinner.getSelectedItemPosition()) {
                    case 0:
                        Docente newTeacher = teacherFragment.getDocente();
                        EditText teacherPassword = teacherFragment.getView().findViewById(R.id.teacher_password);

                        //Controllo che l'utente non abbia lasciato valori nulli
                        if( !newTeacher.getNome().equals("")
                                && !newTeacher.getCognome().equals("")
                                && !newTeacher.getEmail().equals("")
                                && !newTeacher.getMatricola().equals("")
                                && !teacherPassword.getText().toString().equals("")) {

                            //Creazione dell'hash map per inserire il corso nel DB
                            Map<String ,Object> teacher = new HashMap<>();

                            teacher.put("id", "");
                            teacher.put("nome", newTeacher.getNome());
                            teacher.put("cognome", newTeacher.getCognome());
                            teacher.put("email", newTeacher.getEmail());
                            teacher.put("matricola", newTeacher.getMatricola());
                            teacher.put("percorsoImg", "");

                            //crea l'authentication e inserisce il docente nel db
                            mAuth.createUserWithEmailAndPassword(newTeacher.getEmail(), teacherPassword.getText().toString()).addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {

                                    teacher.put("id", mAuth.getCurrentUser().getUid());
                                    DocumentReference documentReference = db.collection("docenti").document(mAuth.getCurrentUser().getUid());
                                    documentReference.set(teacher);
                                }else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.register_field_void, Toast.LENGTH_LONG).show();
                        }
                        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getUid() != null)
                            Log.d("AAAAAAAAA", "Utente loggato: " + mAuth.getCurrentUser().getUid());
                        break;
                    case 1:
                        // TODO implementare aggiunta esame
                        break;
                    case 2:
                        CorsoDiStudio newCourse = courseFragment.getCourse();
                        //Controllo che l'utente non abbia lasciato valori nulli
                        if( !newCourse.getNome().equals("") && !newCourse.getDescrizione().equals("") ) {

                            //Creazione dell'hash map per inserire il corso nel DB
                            Map<String ,Object> course = new HashMap<>();

                            course.put("descrizione", newCourse.getDescrizione());
                            course.put("id", "");
                            course.put("nome", newCourse.getNome());

                            db.collection("corsiDiStudio").add(course).addOnSuccessListener(documentReference -> {
                                documentReference.update("id", documentReference.getId());
                                finish();
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.register_field_void, Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                break;
            case R.id.cancel_registration:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}