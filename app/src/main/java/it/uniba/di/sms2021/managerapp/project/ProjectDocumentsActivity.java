package it.uniba.di.sms2021.managerapp.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private FileListAdapter adapter;
    private static List<SpecsFile> files;
    protected static final int DOWNLOAD_ITEM_ID = View.generateViewId();
    protected static final int WHATSAPP_ITEM_ID = View.generateViewId();
    protected static final int DELETE_ITEM_ID = View.generateViewId();

    private Boolean clicked = false;

    Button btnUploadFile;
    FloatingActionButton btnAdd;
    FloatingActionButton btnAddFile;
    FloatingActionButton btnAddImage;
    Boolean switchUpload = false;
    TextView selectedFileLab;

    //track Choosing Image Intent
    private static final int CHOOSING_IMAGE_REQUEST = 1234;
    private Uri fileUri;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;

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


        btnUploadFile = findViewById(R.id.button_add_file);
        btnAdd = findViewById(R.id.add_btn);
        btnAddFile = findViewById(R.id.add_file_btn);
        btnAddImage = findViewById(R.id.add_image_btn);

        selectedFileLab = findViewById(R.id.selected_file);
        selectedFileLab.setVisibility(View.INVISIBLE);
        createExplorerFile();
    }

    private void createExplorerFile() {
        btnUploadFile.setOnClickListener(v -> uploadFile(fileUri));

        btnAdd.setOnClickListener(v -> onAddButtonClicked());
        btnAddFile.setOnClickListener(v -> showChoosingFile());
        btnAddImage.setOnClickListener(v -> showChoosingImage());
        
        getFileList(new SpecsCallback() {
            @Override
            public synchronized void onCallback(SpecsFile specsFile, boolean flag) {
                files.add(specsFile);
                if(flag) {
                    listViewFiles = findViewById(R.id.project_files);
                    adapter = new FileListAdapter(getApplicationContext(), files);
                    listViewFiles.setAdapter(adapter);
                }
            }
        });
    }

    private void onAddButtonClicked() {
        visibility(clicked);
        animation(clicked);

        if(!clicked) clicked = true;
        else clicked = false;
    }

    private void visibility(Boolean clicked) {
        if(!clicked) {
            btnAddFile.setVisibility(View.VISIBLE);
            btnAddImage.setVisibility(View.VISIBLE);
        } else {
            btnAddFile.setVisibility(View.INVISIBLE);
            btnAddImage.setVisibility(View.INVISIBLE);
        }
    }

    private void animation(Boolean clicked) {
        if(!clicked) {
            btnAddFile.startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim));
            btnAddImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim));
            btnAdd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim));
        } else {
            btnAddFile.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim));
            btnAddImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim));
            btnAdd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim));
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
        switchUpload = false;
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), CHOOSING_IMAGE_REQUEST);
    }

    private void showChoosingImage() {
        switchUpload = true;
        Intent intent = new Intent();
        intent.setType("image/*");
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

    public void uploadFile(Uri fileUrl) {
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
                if(switchUpload) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            selectedFileLab.setText(fileUri.getPath().substring(fileUri.getPath().lastIndexOf("/")+1));
            selectedFileLab.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        // Add Download Menu Item
        int downloadId = DOWNLOAD_ITEM_ID;
        if (menu.findItem(downloadId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem download = menu.add(
                    Menu.NONE,
                    downloadId,
                    1,
                    getString(R.string.download_bt_doc)
            );

            // Set an icon for the new menu item
            download.setIcon(R.drawable.ic_baseline_download_24);

            // Set the show as action flags for new menu item
            download.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            download.setOnMenuItemClickListener(item -> {
                if(adapter.selectedItem.size() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_selection_file), Toast.LENGTH_LONG).show();
                }else {
                    for(int i = 0; i< adapter.selectedItem.size(); i++) {
                        downloadFile(adapter.getItem(adapter.selectedItem.get(i)).getNome(),
                                adapter.getItem(adapter.selectedItem.get(i)).getPercorso());
                    }
                }
                return true;
            });
        }

        // Add Delete Menu Item
        int deleteId = DELETE_ITEM_ID;
        if (menu.findItem(deleteId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem delete = menu.add(
                    Menu.NONE,
                    deleteId,
                    2,
                    getString(R.string.delete_bt_doc)
            );

            // Set an icon for the new menu item
            delete.setIcon(R.drawable.ic_baseline_delete_24);

            // Set the show as action flags for new menu item
            delete.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            delete.setOnMenuItemClickListener(item -> {
                if (adapter.selectedItem.size() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_selection_file_del), Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < adapter.selectedItem.size(); i++) {
                        deleteSelectedFile(adapter.getItem(adapter.selectedItem.get(i)).getNome(),
                                adapter.getItem(adapter.selectedItem.get(i)).getPercorso());
                    }
                }
                return true;
            });
        }

        // Add Whatsapp Menu Item
        int whatsappId = WHATSAPP_ITEM_ID;
        if (menu.findItem(whatsappId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem whatsapp = menu.add(
                    Menu.NONE,
                    whatsappId,
                    3,
                    getString(R.string.whatsapp)
            );

            // Set an icon for the new menu item
            whatsapp.setIcon(R.drawable.ic_whatsapp);

            // Set the show as action flags for new menu item
            whatsapp.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            whatsapp.setOnMenuItemClickListener(item -> {
                shareOnWhatsapp();
                return true;
            });
        }

        super.onPrepareOptionsMenu(menu);
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