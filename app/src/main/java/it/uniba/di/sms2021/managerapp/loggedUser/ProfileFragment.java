package it.uniba.di.sms2021.managerapp.loggedUser;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.di.sms2021.managerapp.guest.GuestActivity;
import it.uniba.di.sms2021.managerapp.loggedUser.ProfileFragmentDirections;
import it.uniba.di.sms2021.managerapp.R;

public class ProfileFragment extends Fragment {

    private View vistaProfilo;
    private GuestActivity callback;

    TextView label;

    public ProfileFragment() {
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
        vistaProfilo = inflater.inflate(R.layout.fragment_profile, container, false);

        label = (TextView) vistaProfilo.findViewById(R.id.name);
        label.setText(StudentActivity.loggedUser.getNome());

        label = (TextView) vistaProfilo.findViewById(R.id.surname);
        label.setText(StudentActivity.loggedUser.getCognome());

        label = (TextView) vistaProfilo.findViewById(R.id.serial_number);
        label.setText(StudentActivity.loggedUser.getMatricola());

        label = (TextView) vistaProfilo.findViewById(R.id.profile_email);
        label.setText(StudentActivity.loggedUser.getEmail());

        if(StudentActivity.loginFile.getName().matches("studenti.srl")){
            vistaProfilo.findViewById(R.id.profile_course).setVisibility(View.VISIBLE);
            vistaProfilo.findViewById(R.id.profile_img).setVisibility(View.VISIBLE);

            label = (TextView) vistaProfilo.findViewById(R.id.profile_course);
            label.setText(StudentActivity.loggedStudent.getcDs());
        }else {

            vistaProfilo.findViewById(R.id.profile_course).setVisibility(View.INVISIBLE);
            vistaProfilo.findViewById(R.id.profile_img).setVisibility(View.INVISIBLE);

        }
        return vistaProfilo;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
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
                    NavDirections action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment();
                    Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                    return true;
                }
            });
        }

        // Add Logout Menu Item
        int logoutId = StudentActivity.LOGOUT_ITEM_ID;
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
            logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    logout();
                    Intent intent = new Intent(getActivity().getApplicationContext(), GuestActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                }
            });
        }

        super.onPrepareOptionsMenu(menu);
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        StudentActivity.loginFile.delete();
        Toast.makeText(getContext(),R.string.logout, Toast.LENGTH_SHORT).show();
    }
}