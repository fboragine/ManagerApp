package it.uniba.di.sms2021.managerapp.guest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.loggedUser.ExamListFragment;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.service.ListViewAdapter;

public class GuestHomeFragment extends Fragment {

    private View viewGuestHome;
    private ListView listView;
    private ListViewAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGuestHome = inflater.inflate(R.layout.fragment_guest_home, container, false);

        ((GuestActivity)getActivity()).disableBackArrow();

        ArrayList<CorsoDiStudio> corsiDiStudio = new ArrayList<>();

        db.collection("corsiDiStudio").get().addOnCompleteListener(task -> {

            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CorsoDiStudio corsoDiStudio = new CorsoDiStudio(document.getString("id"),
                                                                    document.getString("nome"),
                                                                    document.getString("descrizione"));
                    corsiDiStudio.add(corsoDiStudio);
                }

                listView = viewGuestHome.findViewById(R.id.listView);

                //pass results to listViewAdapter class
                adapter = new ListViewAdapter(getActivity().getApplicationContext(), corsiDiStudio);

                //bind the adapter to the listview
                listView.setAdapter(adapter);

                listView.setOnItemClickListener((adapterView, view, i, l) -> {
                    ExamListFragment examListFragment = new ExamListFragment(corsiDiStudio.get(i).getIdCorsoDiStudio());
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, examListFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
            }
        });

        return viewGuestHome;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
                if(TextUtils.isEmpty(s)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapter.filter(s);
                }
                return true;
            }
        });
    }
}