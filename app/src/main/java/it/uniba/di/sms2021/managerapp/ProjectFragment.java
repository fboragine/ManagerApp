package it.uniba.di.sms2021.managerapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ProjectFragment extends Fragment {

    private View view;
    private ArrayList<String> studenti;
    private ListView listViewStudenti;

    public ProjectFragment() {
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_project, container, false);
        studenti = new ArrayList<>(Arrays.asList("Mario Rossi", "Luigi Verdi", "Rosa Neri", "Filippo Neri"));
        listViewStudenti = view.findViewById(R.id.project_students);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.list_item, studenti);
        listViewStudenti.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);

        //Nascondo pulsante ricerca
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        // Add Edit Menu Item
        int editId = StudentActivity.EDIT_ITEM_ID;
        if (menu.findItem(editId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem edit = menu.add(
                    Menu.NONE,
                    editId,
                    1,
                    getString(R.string.edit)
            );

            // Set an icon for the new menu item
            edit.setIcon(R.drawable.ic_edit);

            // Set the show as action flags for new menu item
            edit.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                    //Sostituire con navigation alla modifica del progetto
                    /*
                    NavDirections action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment();
                    Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                     */
                    return true;
                }
            });
        }

        super.onPrepareOptionsMenu(menu);
    }
}