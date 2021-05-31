package it.uniba.di.sms2021.managerapp.segreteria.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Utente;
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;
import it.uniba.di.sms2021.managerapp.segreteria.service.UserListAdapter;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class TeachersListFragment extends Fragment {

    private View viewTeachersList;
    private ListView teacherListView;
    private UserListAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Utente> docenti;

    public TeachersListFragment() {
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
        viewTeachersList = inflater.inflate(R.layout.fragment_teachers_list, container, false);

        ((HomeAdminActivity)requireActivity()).disableBackArrow();
        docenti = new ArrayList<>();

        db.collection("docenti").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Docente docente = new Docente(document.getString("id"),
                                                document.getString("matricola"),
                                                document.getString("nome"),
                                                document.getString("cognome"),
                                                document.getString("email"));
                    docenti.add(docente);
                }

                teacherListView = viewTeachersList.findViewById(R.id.teacherListView);

                //pass results to listViewAdapter class
                adapter = new UserListAdapter(requireActivity().getApplicationContext(), docenti);

                //bind the adapter to the listView
                teacherListView.setAdapter(adapter);

                teacherListView.setOnItemClickListener((parent, view, position, id) -> {
                    // TODO implementare la logica per il click sul docente
//                    ExamListFragment examListFragment = new ExamListFragment(corsiDiStudio.get(i).getIdCorsoDiStudio());
//                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment, examListFragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
                });
            }
        });

        return viewTeachersList;
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
                    teacherListView.clearTextFilter();
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
            Intent intent = new Intent(getActivity().getApplicationContext(), Settings.class);
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