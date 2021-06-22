package it.uniba.di.sms2021.managerapp.segreteria.addItem;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.project.AddNewProjectActivity;

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