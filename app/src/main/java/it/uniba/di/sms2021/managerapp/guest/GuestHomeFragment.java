package it.uniba.di.sms2021.managerapp.guest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.service.ListViewAdapter;
import it.uniba.di.sms2021.managerapp.service.Model;

public class GuestHomeFragment extends Fragment {

    View viewGuestHome;
    ListView listView;
    ListViewAdapter adapter;
    String[] title;
    String[] description;
    ArrayList<Model> arrayList = new ArrayList<>();


    public GuestHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGuestHome = inflater.inflate(R.layout.fragment_guest_home, container, false);
        title = new String[]{"Informatica", "Lingue", "Medicina", "Matematica", "Lettere", "Informatica", "Lingue", "Medicina", "Matematica", "Lettere"};
        description = new String[]{"Informatica description", "Lingue description", "Medicina description", "Matematica description", "Lettere description", "Informatica description", "Lingue description", "Medicina description", "Matematica description", "Lettere description"};

        listView = viewGuestHome.findViewById(R.id.listView);

        for (int i = 0; i<title.length; i++) {
            Model model = new Model(title[i], description[i]);
            //bind all strings in an array
            arrayList.add(model);
        }

        //pass results to listViewAdapter class
        adapter = new ListViewAdapter(getActivity().getApplicationContext(), arrayList);

        //bind the adapter to the listview
        listView.setAdapter(adapter);

        // Inflate the layout for this fragment
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
        super.onPrepareOptionsMenu(menu);
    }
}