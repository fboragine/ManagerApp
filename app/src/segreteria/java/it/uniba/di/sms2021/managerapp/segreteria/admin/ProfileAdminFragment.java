package it.uniba.di.sms2021.managerapp.segreteria.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.di.sms2021.managerapp.R;

public class ProfileAdminFragment extends Fragment {

    public ProfileAdminFragment() {
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
        View vistaProfilo = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        TextView label = vistaProfilo.findViewById(R.id.profile_email);
        label.setText(HomeAdminActivity.getLoggedAdmin().getEmail());

        return vistaProfilo;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Add Edit Menu Item
        int editId = HomeAdminActivity.EDIT_ITEM_ID;
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
            edit.setOnMenuItemClickListener(item -> {
                Navigation.findNavController(requireActivity(), R.id.fragment).navigate(R.id.action_profileAdminFragment_to_editProfileAdminFragment);
                return true;
            });
        }

        // Add Logout Menu Item
        int logoutId = HomeAdminActivity.LOGOUT_ITEM_ID;
        if (menu.findItem(logoutId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem logout = menu.add(
                    Menu.NONE,
                    logoutId,
                    2,
                    getString(R.string.logout)
            );

            // Set an icon for the new menu item
            logout.setIcon(R.drawable.ic_logout);

            // Set the show as action flags for new menu item
            logout.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            logout.setOnMenuItemClickListener(item -> {
               logout();
                return true;
            });
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        HomeAdminActivity.getLoginFile().delete();
        Toast.makeText(getContext(),R.string.logout, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireActivity().getApplicationContext(), LoginAdminActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}