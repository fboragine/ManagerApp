package it.uniba.di.sms2021.managerapp.segreteria.addItem;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Esame;

public class AddActivity extends AppCompatActivity {

    private AddExamFragment examFragment;
    private AddCourseFragment courseFragment;
    private AddTeacherFragment teacherFragment;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.top_register_item_toolbar);
        setSupportActionBar(toolbar);

        // Add the drop down menu
        String[] items = new String[]{getString(R.string.teacher), getString(R.string.exam), getString(R.string.course)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_dropdown, items);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.dropdown_textView);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(items[0], false);
        selected = 0;
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            // Cambio del fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (position) {
                case 0:
                    teacherFragment = new AddTeacherFragment();
                    transaction.replace(R.id.currentFrame, teacherFragment);
                    selected = 0;
                    break;
                case 1:
                    examFragment = new AddExamFragment();
                    transaction.replace(R.id.currentFrame, examFragment);
                    selected = 1;
                    break;
                case 2:
                    courseFragment = new AddCourseFragment();
                    transaction.replace(R.id.currentFrame, courseFragment);
                    selected = 2;
                    break;
            }
            transaction.commit();
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
                switch (selected) {
                    case 0:
                        Docente newTeacher = teacherFragment.getDocente();
                        EditText teacherPassword = teacherFragment.requireView().findViewById(R.id.teacher_password);

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

                                    teacher.put("id", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                                    DocumentReference documentReference = db.collection("docenti").document(mAuth.getCurrentUser().getUid());
                                    documentReference.set(teacher);
                                }else {
                                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.register_field_void, Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1:
                        Esame newExam = examFragment.getExam();

                        // Controllo che l'utente non abbia lasciato valori nulli
                        if (!newExam.getcDs().equals("") && !newExam.getDescrizione().equals("") && !newExam.getNome().equals("")) {
                            //Creazione dell'hash map per inserire l'esame nel DB
                            Map<String ,Object> exam = new HashMap<>();

                            exam.put("descrizione", newExam.getDescrizione());
                            exam.put("id", "");
                            exam.put("nome", newExam.getNome());
                            exam.put("cDs", newExam.getcDs());
                            exam.put("idDocenti", newExam.getIdDocenti());

                            db.collection("esami").add(exam).addOnSuccessListener(documentReference -> {
                                documentReference.update("id", documentReference.getId());
                                finish();
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.register_field_void, Toast.LENGTH_LONG).show();
                        }
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