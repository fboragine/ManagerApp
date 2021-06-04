package it.uniba.di.sms2021.managerapp.segreteria;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Esame;

public class AddExamFragment extends Fragment {

    private View addExamView;
    private Button addCdSButton;
    private Button addTeacherButton;
    private TextView selectedCourse;
    private ListView selectedTeachers;
    private LinearLayout selectedTeachersView;
    private FirebaseFirestore db;
    private SpecsCallback callback;
    private final int[] checkedItem = {-1};
    private Esame newExam;

    public AddExamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        newExam = new Esame("", "", "", "", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addExamView = inflater.inflate(R.layout.fragment_add_exam, container, false);

        addCdSButton = addExamView.findViewById(R.id.add_cds_btn);
        addCdSButton.setOnClickListener(v -> {
            if (v.getId() == R.id.add_cds_btn) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle(R.string.cds_dialog_title);

                db.collection("corsiDiStudio").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<CorsoDiStudio> displayCorsi = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CorsoDiStudio newCourse = new CorsoDiStudio(document.getString("id"), document.getString("nome"), document.getString("descrizione"));
                            displayCorsi.add(newCourse);
                        }
                        callback.onCallback(displayCorsi, null);
                    }
                });

                callback = (courseList, teacherList) -> {
                    String[] listItems = new String[courseList.size()];

                    // Ottengo i valori da visualizzare nella lista dei corsi
                    for (int i = 0; i < courseList.size(); i++) {
                        listItems[i] = courseList.get(i).getNome();
                    }

                    // Avvia un alert dialog box impostandolo come scelta singola
                    alertDialog.setSingleChoiceItems(listItems, checkedItem[0], (dialog, which) -> {
                        // Salva la posizione dell'elemento selezionato anche nella prossima apertura del dialog box
                        checkedItem[0] = which;

                        // Aggiorna la textView e seleziona l'id del corso scelto
                        selectedCourse.setText(listItems[which]);
                        newExam.setcDs(courseList.get(which).getIdCorsoDiStudio());

                        // Chiude il dialog box e modifica il testo nel bottone
                        dialog.dismiss();

                        addCdSButton.setText(R.string.change_select_course);
                        int color = Color.parseColor("#63A4FF");
                        addCdSButton.setBackgroundColor(color);

                        selectedCourse.setVisibility(View.VISIBLE);
                    });

                    // Imposta un'eventuale azione in caso di click su pulsante cancel
                    alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {

                    });

                    // Crea il customAlertDialog
                    AlertDialog customAlertDialog = alertDialog.create();

                    // Mostra il dialog box
                    customAlertDialog.show();
                };
            }
        });

        addTeacherButton = addExamView.findViewById(R.id.add_teacher_btn);
        addTeacherButton.setOnClickListener(v -> {
            if (v.getId() == R.id.add_teacher_btn) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle(R.string.teacher_dialog_title);

                if (!addTeacherButton.getText().equals(String.valueOf(R.string.change_select_teachers))) {
                    db.collection("docenti").get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<Docente> displayDocenti = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Docente newTeacher = new Docente( document.getString("id"), document.getString("matricola"), document.getString("nome"), document.getString("cognome"), document.getString("email"));
                                displayDocenti.add(newTeacher);
                            }
                            callback.onCallback(null, displayDocenti);
                        }
                    });
                }

                callback = (courseList, teacherList) -> {
                    String[] listItems = new String[teacherList.size()];
                    boolean[] selectedItems = new boolean[teacherList.size()];

                    // Ottengo i valori da visualizzare nella lista dei docenti
                    for (int i = 0; i < teacherList.size(); i++) {
                        listItems[i] = teacherList.get(i).getNome() + " " + teacherList.get(i).getCognome();
                        selectedItems[i] = false;
                    }

                    // Avvia un alert dialog box impostandolo come scelta multipla
                    alertDialog.setMultiChoiceItems(listItems, selectedItems, (dialog, which, isChecked) -> {
                        // Salva la posizione dell'elemento selezionate anche nella prossima apertura del dialog box
                        selectedItems[which] = isChecked;
                    });

                    // Set the positive/yes button click listener
                    alertDialog.setPositiveButton("OK", (dialog, which) -> {
                        ArrayList<String> idTeachers = new ArrayList<>();
                        ArrayList<String> selectedDocenti = new ArrayList<>();

                        // Do something when click positive button
                        for (int i = 0; i < selectedItems.length; i++) {
                            boolean checked = selectedItems[i];
                            if (checked) {
                                idTeachers.add(teacherList.get(i).getId());
                                selectedDocenti.add(teacherList.get(i).getNome() + " " + teacherList.get(i).getCognome());
                            }
                        }

                        newExam.setIdDocenti(idTeachers);
                        selectedTeachers.setVisibility(View.VISIBLE);
                        selectedTeachersView.setVisibility(View.VISIBLE);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, selectedDocenti);
                        selectedTeachers.setAdapter(adapter);

                        //Chiude il dialog box e modifica il testo nel bottone
                        dialog.dismiss();
                        addTeacherButton.setText(R.string.change_select_teachers);
                        int color = Color.parseColor("#63A4FF");
                        addTeacherButton.setBackgroundColor(color);
                    });

                    // Imposta un'eventuale azione in caso di click su pulsante negativo quindi cancel
                    alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {

                    });

                    // Crea il customAlertDialog
                    AlertDialog customAlertDialog = alertDialog.create();

                    // Mostra il dialog box
                    customAlertDialog.show();
                };
            }
        });

        selectedCourse = addExamView.findViewById(R.id.cds_selected);
        selectedCourse.setVisibility(View.GONE);

        selectedTeachers = addExamView.findViewById(R.id.teacher_list);
        selectedTeachers.setVisibility(View.GONE);
        selectedTeachersView = addExamView.findViewById(R.id.teacher_list_view);
        selectedTeachersView.setVisibility(View.GONE);

        return addExamView;
    }

    public Esame getExam() {
        EditText examNameEditText = addExamView.findViewById(R.id.name_new_exam);
        EditText examDescriptionEditText = addExamView.findViewById(R.id.exam_description);
        newExam.setNome(examNameEditText.getText().toString());
        newExam.setDescrizione(examDescriptionEditText.getText().toString());
        return newExam;
    }

    interface SpecsCallback {
        void onCallback(ArrayList<CorsoDiStudio> courseList, ArrayList<Docente> teacherList);
    }
}