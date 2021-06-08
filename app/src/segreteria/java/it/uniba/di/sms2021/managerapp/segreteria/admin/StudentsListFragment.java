package it.uniba.di.sms2021.managerapp.segreteria.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.entities.Utente;
import it.uniba.di.sms2021.managerapp.segreteria.editItem.EditProfileActivity;
import it.uniba.di.sms2021.managerapp.segreteria.service.SettingsAdmin;
import it.uniba.di.sms2021.managerapp.segreteria.service.UserListAdapter;

public class StudentsListFragment extends Fragment {

    private View viewStudentList;
    private ListView studentListView;
    private UserListAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Utente> studenti;

    public StudentsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewStudentList = inflater.inflate(R.layout.fragment_students_list, container, false);

        return viewStudentList;
    }

    private synchronized void getStudents() {
        studenti = new ArrayList<>();
        db.collection("studenti").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Studente studente = new Studente(document.getString("id"),
                            document.getString("matricola"),
                            document.getString("nome"),
                            document.getString("cognome"),
                            document.getString("email"),
                            document.getString("cDs"));
                    studenti.add(studente);
                }

                studentListView = viewStudentList.findViewById(R.id.studentListView);

                //pass results to listViewAdapter class
                adapter = new UserListAdapter(requireActivity().getApplicationContext(), studenti);

                //bind the adapter to the listView
                studentListView.setAdapter(adapter);

                studentListView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent(requireActivity().getApplicationContext(), EditProfileActivity.class);
                    intent.putExtra("utente", (Parcelable) studenti.get(position));
                    intent.putExtra("isStudent", true);
                    startActivity(intent);
                });
            }
        });
    }

    @Override
    public void onResume() {
        getStudents();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    studentListView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), SettingsAdmin.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}