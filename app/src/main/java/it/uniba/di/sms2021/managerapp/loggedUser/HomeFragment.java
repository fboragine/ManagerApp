package it.uniba.di.sms2021.managerapp.loggedUser;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.service.RecyclerViewAdapter;
import it.uniba.di.sms2021.managerapp.entities.Progetto;

public class HomeFragment extends Fragment {

    private static final String TAG = "SimpleToolbarTest";

    private ArrayList<Progetto> inProgressProject;
    private ArrayList<Progetto> closedProject;
    private RecyclerView inProgressRecyclerView;
    private RecyclerView closedRecyclerView;
    private RecyclerView.LayoutManager inProgressLayoutManager;
    private RecyclerView.LayoutManager closedLayoutManager;
    private RecyclerView.Adapter inProgressAdapter;
    private RecyclerView.Adapter closedAdapter;

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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
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

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        // Add Filter Menu Item
        int filterId = StudentActivity.FILTER_ITEM_ID;
        if (menu.findItem(filterId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem filter = menu.add(
                    Menu.NONE,
                    filterId,
                    2,
                    getString(R.string.action_filter)
            );

            // Set an icon for the new menu item
            filter.setIcon(R.drawable.ic_baseline_filter_alt_24);

            // Set the show as action flags for new menu item
            filter.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        super.onPrepareOptionsMenu(menu);
    }
}