package it.uniba.di.sms2021.managerapp.loggedUser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.project.AddNewProjectActivity;
import it.uniba.di.sms2021.managerapp.service.RecyclerViewAdapter;
import it.uniba.di.sms2021.managerapp.entities.Progetto;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "SimpleToolbarTest";

    private ArrayList<Progetto> inProgressProject;
    private ArrayList<Progetto> closedProject;
    private RecyclerView inProgressRecyclerView;
    private RecyclerView closedRecyclerView;
    private RecyclerView.LayoutManager inProgressLayoutManager;
    private RecyclerView.LayoutManager closedLayoutManager;
    private RecyclerViewAdapter inProgressAdapter;
    private RecyclerViewAdapter closedAdapter;

    private Button addProjectBtn;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private View viewHome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewHome = inflater.inflate(R.layout.fragment_home, container, false);

        inProgressProject = new ArrayList<>();
        closedProject = new ArrayList<>();

        addProjectBtn = (Button) viewHome.findViewById(R.id.add_project_btn);
        addProjectBtn.setOnClickListener(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("progetti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    mAuth = FirebaseAuth.getInstance();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Progetto progetto = new Progetto(document.getString("id"), document.getString("nome"), document.getString("descrizione"),
                                document.getString("codiceEsame"), document.getString("dataCreazione"),
                                (ArrayList<String>) document.get("idStudenti"), document.getBoolean("stato"));

                        // sezione di assegnamento dei progetti chiusi
                        if(progetto.isClose()) {
                            ArrayList<String> idStudenti = progetto.getIdStudenti();
                            int count = 0;
                            boolean flag = false;

                            do {
                                if (idStudenti.get(count).equals(mAuth.getCurrentUser().getUid())) {
                                    closedProject.add(progetto);
                                    flag = true;
                                }
                                count++;
                            }while(count < idStudenti.size() && !flag);
                        } else { // sezione di assegnamento dei progetti aperti
                            ArrayList<String> idStudenti = progetto.getIdStudenti();
                            int count = 0;
                            boolean flag = false;

                            do {
                                if (idStudenti.get(count).equals(mAuth.getCurrentUser().getUid())) {
                                    inProgressProject.add(progetto);
                                    flag = true;
                                }
                                count++;
                            }while(count < idStudenti.size() && !flag);
                        }

                        inProgressRecyclerView = viewHome.findViewById(R.id.inProgressRecyclerView);
                        inProgressRecyclerView.setHasFixedSize(true);
                        closedRecyclerView = viewHome.findViewById(R.id.closedRecyclerView);
                        closedRecyclerView.setHasFixedSize(true);

                        inProgressLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        inProgressRecyclerView.setLayoutManager(inProgressLayoutManager);
                        closedLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        closedRecyclerView.setLayoutManager(closedLayoutManager);
                        inProgressAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), inProgressProject);
                        inProgressRecyclerView.setAdapter(inProgressAdapter);
                        closedAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), closedProject);
                        closedRecyclerView.setAdapter(closedAdapter);
                    }

                    if (closedProject.isEmpty()) {
                        RelativeLayout relativeLayout = viewHome.findViewById(R.id.closedContainer);
                        relativeLayout.setVisibility(View.INVISIBLE);
                    }
                    if (inProgressProject.isEmpty()) {
                        RelativeLayout relativeLayout = viewHome.findViewById(R.id.inProgressContainer);
                        relativeLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        return viewHome;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_project_btn) {
            Intent intent = new Intent(getActivity().getApplicationContext(), AddNewProjectActivity.class);;
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //ricerca
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                inProgressAdapter.filter(s);
                closedAdapter.filter(s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}