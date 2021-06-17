package it.uniba.di.sms2021.managerapp.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.SpecsFile;
import it.uniba.di.sms2021.managerapp.service.FileListAdapter;
import it.uniba.di.sms2021.managerapp.service.Settings;

@RequiresApi(api = Build.VERSION_CODES.R)
public class ProjectDocumentsActivity extends AppCompatActivity {

    interface SpecsCallback {
        void onCallback(SpecsFile specsFile, boolean flag);
    }
    FirebaseStorage storage;
    private Progetto progetto;
    private ListView listViewFiles;
    private static List<SpecsFile> files;

    private static final int WRITE_ID = 324;
    private static final int READ_ID = 816;
    private static final int DELETE_ID = 496;

    Button btnDownload;
    Button btnUploadFile;
    Button btnSelectFile;
    Button shareBtn;
    Button deleteBtn;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchUpload;
    TextView selectedFileLab;

    //track Choosing Image Intent
    private static final int CHOOSING_IMAGE_REQUEST = 1234;
    private Uri fileUri;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    FileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_documents);

        final Intent src = getIntent();
        if(src != null) {
            progetto = src.getParcelableExtra("progetto");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(progetto.getNome());
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(view -> finish());

        // Get the default bucket from a custom FirebaseApp
        storage = FirebaseStorage.getInstance();
        files = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        String path = getExternalFilesDir(null).getPath() + "/projects files/";
        File f1 = new File(path);
        if(f1.mkdir()) {
            Toast.makeText(getApplicationContext(), getString(R.string.dir_create), Toast.LENGTH_SHORT).show();
        }

        btnDownload = findViewById(R.id.button_download);
        btnUploadFile = findViewById(R.id.button_add_file);
        btnSelectFile = findViewById(R.id.button_select_file);
        shareBtn = findViewById(R.id.button_share_file);
        deleteBtn = findViewById(R.id.button_delete);

        switchUpload = findViewById(R.id.switch_upload);
        selectedFileLab = findViewById(R.id.selected_file);
        selectedFileLab.setVisibility(View.INVISIBLE);
        createExplorerFile();
    }

    private void createExplorerFile() {
        btnUploadFile.setOnClickListener(v -> checkPermissionFuntion(Manifest.permission.READ_EXTERNAL_STORAGE, READ_ID));
        btnSelectFile.setOnClickListener(v -> showChoosingFile());
        shareBtn.setOnClickListener(v -> shareOnWhatsapp());
        
        getFileList(new SpecsCallback() {
            @Override
            public synchronized void onCallback(SpecsFile specsFile, boolean flag) {
                files.add(specsFile);
                if(flag) {
                    listViewFiles = findViewById(R.id.project_files);
                    adapter = new FileListAdapter(getApplicationContext(), files);
                    listViewFiles.setAdapter(adapter);

                    btnDownload.setOnClickListener(v -> {
                        if(adapter.selectedItem.size() == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_selection_file), Toast.LENGTH_LONG).show();
                        }else {
                            checkPermissionFuntion(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_ID);
                        }
                    });

                    deleteBtn.setOnClickListener(v -> checkPermissionFuntion(Manifest.permission.WRITE_EXTERNAL_STORAGE, DELETE_ID));
                }
            }
        });
    }


    // This function is called when user accept or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch(requestCode) {
                case READ_ID: {
                    if(fileUri != null) {
                        uploadFile(fileUri);
                    }else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_file_upload), Toast.LENGTH_LONG).show();
                    }
                }
                break;
                case WRITE_ID: {
                    for(int i = 0; i< adapter.selectedItem.size(); i++) {
                        downloadFile(adapter.getItem(adapter.selectedItem.get(i)).getNome(),
                                adapter.getItem(adapter.selectedItem.get(i)).getPercorso());
                    }
                }
                break;
                case DELETE_ID: {
                    if (adapter.selectedItem.size() == 0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_selection_file), Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < adapter.selectedItem.size(); i++) {
                            deleteSelectedFile(adapter.getItem(adapter.selectedItem.get(i)).getNome(),
                                    adapter.getItem(adapter.selectedItem.get(i)).getPercorso());
                        }
                    }
                }
                break;
            }
        } else {
            Toast.makeText(this, getString(R.string.allow_perm_msg), Toast.LENGTH_SHORT).show();
        }

    }

    private void requestStoragePermissionDialog(String permission, int permission_id)
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.perm_need_title)
                    .setMessage(R.string.perm_need_storage_msg)
                    .setPositiveButton("OK", (dialog, which) -> requestPermissionFunction(permission, permission_id))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss()).create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{permission},permission_id);
        }
    }

    private void requestPermissionFunction(String permission, int permission_id) {
        ActivityCompat.requestPermissions(this,new String[]{permission},permission_id);
    }

    private void checkPermissionFuntion(String permission, int permission_id) {
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
            requestStoragePermissionDialog(permission, permission_id);
        }else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                ActivityCompat.requestPermissions(this, new String[]{permission}, permission_id);
            }

        }
    }

    private void deleteSelectedFile(String nomeFile, String percorso) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(percorso);

        progressDialog.setTitle(getString(R.string.delete_select));
        progressDialog.show();

        islandRef.delete()
                 .addOnSuccessListener(aVoid -> {
                     progressDialog.dismiss();
                     Toast.makeText(getApplicationContext(), getString(R.string.delete_succ) + ": " + nomeFile, Toast.LENGTH_LONG).show();
                     finish();
                     startActivity(getIntent());
                 })
                 .addOnFailureListener(exception -> {
                     progressDialog.dismiss();
                     Toast.makeText(getApplicationContext(), getString(R.string.delete_error) + ": " + exception.getMessage(), Toast.LENGTH_LONG).show();
                 });
    }

    private synchronized void shareOnWhatsapp() {
        if(fileUri != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            startActivity(Intent.createChooser(intent, "Share file via"));
        }else {
            Toast.makeText(getApplicationContext(), getString(R.string.nofile_to_share), Toast.LENGTH_LONG).show();
        }
    }

    private void showChoosingFile() {
        Intent intent = new Intent();
        if(switchUpload.isChecked()) {
            intent.setTypeAndNormalize("image/*");
        }else {
            intent.setTypeAndNormalize("application/*");
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), CHOOSING_IMAGE_REQUEST);
    }

    private void createProjectDir() {
        String path = getExternalFilesDir(null).getPath() + "/projects files/" + progetto.getNome();
        File f1 = new File(path);
        if(f1.mkdir()) {
            Toast.makeText(getApplicationContext(), getString(R.string.dir_create), Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadFile(String nomeFile, String percorso) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(percorso);

        File localFile = new File(getExternalFilesDir(null) + "/projects files/" + progetto.getNome(), nomeFile);

        createProjectDir();

        progressDialog.setTitle(getString(R.string.download_select));
        progressDialog.show();

        islandRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), getString(R.string.download_succ) + ": " + nomeFile, Toast.LENGTH_LONG).show();
        }).addOnProgressListener(taskSnapshot -> {
            // progress percentage
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

            // percentage in progress dialog
            progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
        }).addOnFailureListener(exception -> progressDialog.dismiss()).addOnPausedListener(taskSnapshot -> Toast.makeText(getApplicationContext(), getString(R.string.donwload_stop), Toast.LENGTH_LONG).show());
    }

    public void uploadFile(@NonNull Uri fileUrl) {
        if (fileUrl != null) {
            String fileName = fileUrl.getPath().substring(fileUrl.getPath().lastIndexOf("/")+1, fileUrl.getPath().lastIndexOf("."));

            if (!validateInputFileName(fileName)) {
                return;
            }
            progressDialog.setTitle(getString(R.string.upload_label));
            progressDialog.show();

            StorageReference fileReference = FirebaseStorage.getInstance().getReference().child("progetti/" + progetto.getId());
            StorageReference fileRef = fileReference.child(fileName + "." + getFileExtension(fileUrl));

            fileRef.putFile(fileUrl)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.file_upload), Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(getIntent());
                    })
                    .addOnFailureListener(exception -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        // progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        // percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    })
                    .addOnPausedListener(taskSnapshot -> Toast.makeText(getApplicationContext(), getString(R.string.upload_stop), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_file_upload), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(getApplicationContext(), R.string.file_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (bitmap != null) {
            bitmap.recycle();
        }

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.fileUri = data.getData();
            try {
                if(switchUpload.isChecked()) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fileSelected = fileUri.getPath().substring(fileUri.getPath().lastIndexOf("/") + 1);
            if(fileSelected.contains(".") && fileUri != null) {
                selectedFileLab.setText(fileSelected);
                selectedFileLab.setVisibility(View.VISIBLE);
            } else {
                this.fileUri = null;
                Toast.makeText(getApplicationContext(), getString(R.string.not_valid_chosen), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        //Nascondo pulsante ricerca
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SimpleDateFormat")
    private synchronized void getFileList(SpecsCallback myCallback) {
        StorageReference storageRef = storage.getReference();
        // Get reference to the file
        StorageReference forestRef = storageRef.child("progetti/" + progetto.getId());

        forestRef.listAll()
        .addOnSuccessListener(listResult -> {

            AtomicInteger i = new AtomicInteger();

            for (StorageReference item : listResult.getItems()) {
                // All the items under listRef.
                String percorso = "progetti/" + progetto.getId() + "/" + item.getName();
                StorageReference forestRefFile = storageRef.child(percorso);
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                synchronized (this) {
                    forestRefFile.getMetadata().addOnSuccessListener((storageMetadata) -> {
                        SpecsFile auxFile = new SpecsFile(storageMetadata.getMd5Hash(),
                                storageMetadata.getName(),
                                formatter.format(new Date(storageMetadata.getUpdatedTimeMillis())),
                                (storageMetadata.getSizeBytes() / 1024),
                                storageMetadata.getContentType(),
                                percorso
                                );

                        if(i.get() < listResult.getItems().size()-1) {
                            myCallback.onCallback(auxFile, false);
                        }else {
                            myCallback.onCallback(auxFile, true);
                        }
                        i.getAndIncrement();
                    });
                }
            }
        })
        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
    }
}