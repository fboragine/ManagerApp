package it.uniba.di.sms2021.managerapp.loggedUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class EditProfileFragment extends Fragment implements View.OnClickListener{

    View vistaModifica;
    EditText editName;
    EditText editSurname;
    EditText editMatricola;
    EditText editEmail;
    EditText editPassword;
    ImageView profileImg;
    Button btnEdit;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String course;
    private final String defaultImgProfile = "imgProfile.png";
    private static final int CHOOSING_IMAGE_REQUEST = 1234;
    boolean changedImgProfile = false;

    private Uri fileUri;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(StudentActivity.loginFile.getName().matches("studenti.srl")){
            course = StudentActivity.loggedStudent.getcDs();
        }

        progressDialog = new ProgressDialog(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vistaModifica = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        editName = vistaModifica.findViewById(R.id.name);
        editName.setText(StudentActivity.loggedUser.getNome());

        editSurname = vistaModifica.findViewById(R.id.surname);
        editSurname.setText(StudentActivity.loggedUser.getCognome());

        editMatricola = vistaModifica.findViewById(R.id.serial_number);
        editMatricola.setText(StudentActivity.loggedUser.getMatricola());

        editPassword = vistaModifica.findViewById(R.id.pw_txt);

        editEmail = vistaModifica.findViewById(R.id.email_txt);
        editEmail.setText(StudentActivity.loggedUser.getEmail());

        btnEdit = vistaModifica.findViewById(R.id.edit_img_btn);
        btnEdit.setOnClickListener(this);

        profileImg = vistaModifica.findViewById(R.id.ic_action_name);
        viewImgProfile();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return vistaModifica;
    }

    private void viewImgProfile() {
        File localFile = new File(getActivity().getExternalFilesDir(null) + "/user media", defaultImgProfile);
        if(localFile.exists()) {
            profileImg.setImageURI(Uri.parse(localFile.getPath()));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);

        menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity().getApplicationContext(), Settings.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
                Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        // Add Save Menu Item
        int saveId = StudentActivity.SAVE_ITEM_ID;
        if (menu.findItem(saveId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem save = menu.add(
                    Menu.NONE,
                    saveId,
                    1,
                    getString(R.string.save)
            );

            // Set an icon for the new menu item
            save.setIcon(R.drawable.ic_save);

            // Set the show as action flags for new menu item
            save.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            save.setOnMenuItemClickListener(item -> {
                if(changedImgProfile) {
                    downloadFile(defaultImgProfile, "file user/" + StudentActivity.loggedUser.getId());

                    if(fileUri != null) {
                        String path = getActivity().getExternalFilesDir(null).getPath() +"/"+ fileUri.getPath().substring(fileUri.getPath().lastIndexOf("/")+1);
                        if (!path.contains(getActivity().getExternalFilesDir(null) + "/user media")) {
                            File deleteFileImg = new File(path);
                            if(deleteFileImg.delete()) {
                                if (deleteFileImg.exists()) {
                                    try {
                                        if(deleteFileImg.getCanonicalFile().delete()) {
                                            if (deleteFileImg.exists()) {
                                                getActivity().getApplicationContext().deleteFile(deleteFileImg.getName());
                                            }
                                        }else {
                                            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.not_delete), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.not_delete), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                modificaFile();
                NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
                Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                return true;
            });
        }

        // Add Cancel Menu Item
        int cancelId = StudentActivity.CANCEL_ITEM_ID;
        if (menu.findItem(cancelId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem cancel = menu.add(
                    Menu.NONE,
                    cancelId,
                    2,
                    getString(R.string.cancel)
            );

            // Set an icon for the new menu item
            cancel.setIcon(R.drawable.ic_edit_off);

            // Set the show as action flags for new menu item
            cancel.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            cancel.setOnMenuItemClickListener(item -> {
                Toast.makeText(getActivity().getApplicationContext(), R.string.undone_save, Toast.LENGTH_SHORT).show();
                NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
                Navigation.findNavController(getActivity(), R.id.fragment).navigate(action);
                return true;
            });
        }

        super.onPrepareOptionsMenu(menu);
    }

    private void showChoosingFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), CHOOSING_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (bitmap != null) {
            bitmap.recycle();
        }

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            this.fileUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
                profileImg.setImageURI(this.fileUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadFile(fileUri);
        }
    }

    private boolean validateInputFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.file_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadFile(Uri fileUrl) {
        if (fileUrl != null) {
            String fileName = fileUrl.getPath().substring(fileUrl.getPath().lastIndexOf("/")+1, fileUrl.getPath().lastIndexOf("."));

            if (!validateInputFileName(fileName)) {
                return;
            }
            progressDialog.setTitle(getString(R.string.upload_label));
            progressDialog.show();

            StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("file user/" + StudentActivity.loggedUser.getId());
            StorageReference fileRef = fileReference.child("imgProfile" + "." + getFileExtension(fileUrl));

            fileRef.putFile(fileUrl)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), R.string.img_upload, Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(exception -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        // progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        // percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    })
                    .addOnPausedListener(taskSnapshot -> Toast.makeText(getActivity().getApplicationContext(), getString(R.string.upload_stop), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_file_upload), Toast.LENGTH_LONG).show();
        }
    }

    public void downloadFile(String nomeFile, String percorso) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(percorso + "/" + nomeFile);

        File localFile = new File(getActivity().getExternalFilesDir(null) + "/user media", nomeFile);

        progressDialog.setTitle(getString(R.string.download_select));
        progressDialog.show();

        islandRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> progressDialog.dismiss()).addOnProgressListener(taskSnapshot -> {
            // progress percentage
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

            // percentage in progress dialog
            progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
        });
    }

    public void saveFile(String FILE_NAME, Object oggetto) {
        ObjectOutput out;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(getContext().getExternalFilesDir(null), FILE_NAME)));
            out.writeObject(oggetto);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modificaFile() {
        StudentActivity.loggedUser.setNome(editName.getText().toString());
        StudentActivity.loggedUser.setCognome(editSurname.getText().toString());
        StudentActivity.loggedUser.setMatricola(editMatricola.getText().toString());

        if( !StudentActivity.loggedUser.getEmail().matches(editEmail.getText().toString()) ) {
            modificaAuth(true,editEmail.getText().toString(), StudentActivity.loggedUser.getEmail());
            StudentActivity.loggedUser.setEmail(editEmail.getText().toString());

        }
        if( !editPassword.getText().toString().matches("") ) {
            modificaAuth(false, editPassword.getText().toString(),null);
        }

        Map<String ,Object> userModify = new HashMap<>();

        userModify.put("cognome",StudentActivity.loggedUser.getCognome());
        userModify.put("email",StudentActivity.loggedUser.getEmail());
        userModify.put("id",mAuth.getCurrentUser().getUid());
        userModify.put("matricola",StudentActivity.loggedUser.getMatricola());
        userModify.put("nome",StudentActivity.loggedUser.getNome());
        if(changedImgProfile) {
            if(fileUri != null)
                userModify.put("percorsoImg", fileUri.getPath());
        }else {
            userModify.put("percorsoImg", "");
        }


        if(StudentActivity.loginFile.getName().matches("studenti.srl")) {

            StudentActivity.loggedStudent = new Studente(mAuth.getCurrentUser().getUid(),
                                                         StudentActivity.loggedUser.getMatricola(),
                                                         StudentActivity.loggedUser.getNome(),
                                                         StudentActivity.loggedUser.getCognome(),
                                                         StudentActivity.loggedUser.getEmail(),
                                                         course);

            userModify.put("cDs",course);

            if(StudentActivity.loginFile.delete()) {
                saveFile("studenti.srl", StudentActivity.loggedStudent);

                DocumentReference docUpdate = db.collection("studenti").document(StudentActivity.loggedUser.getId());
                modifyFirebase(docUpdate, userModify);
            }else {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.not_delete), Toast.LENGTH_SHORT).show();
            }

        }else if(StudentActivity.loginFile.getName().matches("docenti.srl")) {

            StudentActivity.loggedDocent = new Docente(
                    mAuth.getCurrentUser().getUid(),
                    StudentActivity.loggedUser.getMatricola(),
                    StudentActivity.loggedUser.getNome(),
                    StudentActivity.loggedUser.getCognome(),
                    StudentActivity.loggedUser.getEmail());

            if(StudentActivity.loginFile.delete()) {
                saveFile("docenti.srl", StudentActivity.loggedDocent);

                DocumentReference docUpdate = db.collection("docenti").document(mAuth.getCurrentUser().getUid());
                modifyFirebase(docUpdate, userModify);
            }else {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.not_delete), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void modificaAuth( boolean flag, String replace, String originalMail) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(flag) {   //Modifica l'email
            user.updateEmail(replace)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.email_changed, Toast.LENGTH_LONG).show();
                }else
                {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.reauth_msg_email, Toast.LENGTH_LONG).show();
                    StudentActivity.loggedUser.setEmail(originalMail);
                }
            });

        }else {
            user.updatePassword(replace)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.password_changed, Toast.LENGTH_LONG).show();
                }else
                {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.weak_password, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void modifyFirebase(DocumentReference docUpdate, Map<String, Object> userModify) {
        docUpdate
        .update(userModify)
        .addOnSuccessListener(aVoid -> Toast.makeText(getActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
        .addOnFailureListener(e -> Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.edit_img_btn){
            changedImgProfile = true;
            showChoosingFile();
        }
    }
}