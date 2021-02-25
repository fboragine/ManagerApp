package it.uniba.di.sms2021.managerapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
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
                    "Search"
            );

            // Set an icon for the new menu item
            search.setIcon(R.drawable.ic_search);

            // Set the show as action flags for new menu item
            search.setShowAsActionFlags(
                    MenuItem.SHOW_AS_ACTION_WITH_TEXT |
                            MenuItem.SHOW_AS_ACTION_ALWAYS
            );

            // Set a click listener for the new menu item
            search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
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
                    "Filter"
            );

            // Set an icon for the new menu item
            filter.setIcon(R.drawable.ic_baseline_filter_alt_24);

            // Set the show as action flags for new menu item
            filter.setShowAsActionFlags(
                    MenuItem.SHOW_AS_ACTION_WITH_TEXT |
                            MenuItem.SHOW_AS_ACTION_ALWAYS
            );

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