package it.uniba.di.sms2021.managerapp.segreteria.addItem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;

public class AddTeacherFragment extends Fragment {

    private View addTeacherView;

    public AddTeacherFragment() {
        // Required empty public constructor
    }

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addTeacherView = inflater.inflate(R.layout.fragment_add_teacher, container, false);

        return addTeacherView;
    }

    public Docente getDocente() {
        EditText teacherMatricolaEditText = addTeacherView.findViewById(R.id.teacher_matricola);
        EditText teacherNameEditText = addTeacherView.findViewById(R.id.teacher_name);
        EditText teacherSurnameEditText = addTeacherView.findViewById(R.id.teacher_surname);
        EditText teacherEmailEditText = addTeacherView.findViewById(R.id.teacher_email);

        return new Docente("", teacherMatricolaEditText.getText().toString(), teacherNameEditText.getText().toString(), teacherSurnameEditText.getText().toString(), teacherEmailEditText.getText().toString());
    }
}