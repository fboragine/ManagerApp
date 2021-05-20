package it.uniba.di.sms2021.managerapp.loggedUser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import it.uniba.di.sms2021.managerapp.guest.GuestActivity;
import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.service.Settings;

import static it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity.loggedStudent;

public class ProfileFragment extends Fragment {

    private View vistaProfilo;
    private FirebaseFirestore db;
    ImageView profileImg;
    TextView label;
    private final String defaultImgProfile = "imgProfile.png";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
    }

    private void viewImgProfile() {
        File localFile = new File(getActivity().getExternalFilesDir(null) + "/user media", defaultImgProfile);
        if(localFile.exists()) {
            profileImg.setImageURI(Uri.parse(localFile.getPath()));
        }else {
            downloadFile(defaultImgProfile, "file user/" + StudentActivity.loggedUser.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vistaProfilo = inflater.inflate(R.layout.fragment_profile, container, false);

        label = vistaProfilo.findViewById(R.id.name);
        label.setText(StudentActivity.loggedUser.getNome());

        label = vistaProfilo.findViewById(R.id.surname);
        label.setText(StudentActivity.loggedUser.getCognome());

        label = vistaProfilo.findViewById(R.id.serial_number);
        label.setText(StudentActivity.loggedUser.getMatricola());

        label = vistaProfilo.findViewById(R.id.profile_email);
        label.setText(StudentActivity.loggedUser.getEmail());

        profileImg = vistaProfilo.findViewById(R.id.ic_action_name);
        viewImgProfile();

        if(StudentActivity.loginFile.getName().matches("studenti.srl")){
            vistaProfilo.findViewById(R.id.profile_course).setVisibility(View.VISIBLE);
            vistaProfilo.findViewById(R.id.profile_img).setVisibility(View.VISIBLE);

            label = vistaProfilo.findViewById(R.id.profile_course);

            db.collection("corsiDiStudio").document(loggedStudent.getcDs()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    label.setText(documentSnapshot.getData().get("nome").toString());
                }
            });
        }else {
            vistaProfilo.findViewById(R.id.profile_course).setVisibility(View.INVISIBLE);
            vistaProfilo.findViewById(R.id.profile_img).setVisibility(View.INVISIBLE);
        }
        return vistaProfilo;
    }

    public void downloadFile(String nomeFile, String percorso) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(percorso + "/" + nomeFile);

        File localFile = new File(getActivity().getExternalFilesDir(null) + "/user media", nomeFile);

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                profileImg.setImageURI(Uri.parse(localFile.getPath()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnPausedListener(new OnPausedListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onPaused(FileDownloadTask.TaskSnapshot taskSnapshot) {
            }
        });
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
            Intent intent = new Intent(getActivity().getApplicationContext(), Settings.class);
            startActivity(intent);
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

        super.onPrepareOptionsMenu(menu);
    }
}