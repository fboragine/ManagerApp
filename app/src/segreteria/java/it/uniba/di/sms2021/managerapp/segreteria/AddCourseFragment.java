package it.uniba.di.sms2021.managerapp.segreteria;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;

public class AddCourseFragment extends Fragment {

   private View addCourseView;

    public AddCourseFragment() {
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
        addCourseView = inflater.inflate(R.layout.fragment_add_course, container, false);

        return addCourseView;
    }

    public CorsoDiStudio getCourse() {
        EditText courseNameEditText = addCourseView.findViewById(R.id.course_name);
        EditText courseDescriptionEditText = addCourseView.findViewById(R.id.course_description);
        return new CorsoDiStudio("", courseNameEditText.getText().toString(), courseDescriptionEditText.getText().toString());
    }
}