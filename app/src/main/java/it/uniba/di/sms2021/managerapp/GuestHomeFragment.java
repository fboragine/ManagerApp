package it.uniba.di.sms2021.managerapp;

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

        // Add Search Menu Item
        int searchId = GuestActivity.SEARCH_ITEM_ID;
        if (menu.findItem(searchId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem search = menu.add(
                    Menu.NONE,
                    searchId,
                    1,
                    getString(R.string.search)
            );

            // Set an icon for the new menu item
            search.setIcon(R.drawable.ic_search);

            // Set the show as action flags for new menu item
            search.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            search.setActionView(new SearchView(getActivity().getApplicationContext()));

            // Set a click listener for the new menu item
            search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                    SearchView searchView = (SearchView) item.getActionView();
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
                    return true;
                }
            });
        }

        // Add Filter Menu Item
        int filterId = GuestActivity.FILTER_ITEM_ID;
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