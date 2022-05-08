package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostActivities extends AppCompatActivity implements actionOnPosts {
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private DataBaseHandler db;
    private Button fromCamera, fromGallery;
    private BottomSheetDialog bottomSheetDialog;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CODE = 1001;
    private static final int RESULT_LOAD_IMG = 1002;
    private String imageFilePathCamera, username;
    private ImageView image;
    private TextView itemCounter;
    private Intent cameraInt;
    private RecyclerView recyclerView;
    private Bitmap selectedImage;
    private EditText message_search;
    private PostRecyclerAdapter adapter;
    static Uri ImageUriFromCamera, ImageUriFromGallery;
    List<Post> posts_list, selected_posts;
    int counter = 0;
    boolean isContexualModelEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_activities);
        db = new DataBaseHandler(this);
        selected_posts = new ArrayList<>();
        itemCounter = findViewById(R.id.itemCounter);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");
        if (username.equals(DataBaseHandler.logged_username)) {
            fab.show();
        } else {
            fab.hide();
        }
        setRecyclerView();
        message_search = findViewById(R.id.message_search);
        message_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchData(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //////////////
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    private void searchData(CharSequence charSequence) {
        ArrayList<Post> mPosts = new ArrayList<>();
        //users = new ArrayList<>();


        for (Post user : posts_list) {
            //Log.e("TAG", "searchData: " +  searchText );
            if (user.getMessage().toLowerCase().contains(charSequence)) {
                Log.e("TAG", "searchData: " + charSequence);
                mPosts.add(user);

            }
        }
        adapter = new PostRecyclerAdapter(mPosts, this, PostActivities.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void setRecyclerView() {
        posts_list = db.getPosts(username);
        recyclerView = (RecyclerView) findViewById(R.id.posts_recycler_view);
        adapter = new PostRecyclerAdapter(posts_list, this, PostActivities.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void addData() {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.data_add_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivities.this);
        builder.setView(dialoglayout);
        AlertDialog dialog_1 = builder.create();
        //builder.show();
        dialog_1.show();
        EditText y_message = dialog_1.findViewById(R.id.your_message);
        image = dialog_1.findViewById(R.id.image);
        Button getImages = dialog_1.findViewById(R.id.add_image);
        selectedImage = image.getDrawingCache();
        Button submit = dialog_1.findViewById(R.id.submit);
        getImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(PostActivities.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_sheet);
                fromCamera = bottomSheetDialog.findViewById(R.id.openCamera);
                fromGallery = bottomSheetDialog.findViewById(R.id.openGallery);
                bottomSheetDialog.show();
                fromCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getImagesfromCamera();
                    }
                });
                fromGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                        bottomSheetDialog.dismiss();
                    }
                });


            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = new Post();
                post.setMessage(y_message.getText().toString());
                post.setUser_name(DataBaseHandler.logged_username);
                Boolean ln = false;
                if (selectedImage != null) {
                    Log.e("Image with", selectedImage.toString());
                    ln = db.savePost(post, getBitmapAsByteArray(selectedImage));

                } else {
                    Drawable d = getDrawable(R.drawable.logo);
                    selectedImage = ((BitmapDrawable) d).getBitmap();
                    ln = db.savePost(post, getBitmapAsByteArray(selectedImage));
                }
                if (ln) {
                    setRecyclerView();
                    dialog_1.dismiss();
                    Toast.makeText(getApplicationContext(), "Data inserted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data not inserted", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getImagesfromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                //If permission not enabled then request for it
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //Show popup for requesting for permissions
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                //permission already Granted


                openCamera();
                bottomSheetDialog.dismiss();
            }

        }
    }

    private void openCamera() {
        this.cameraInt = new Intent("android.media.action.IMAGE_CAPTURE");
        if (this.cameraInt.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = createFileImage();
            } catch (IOException unused) {
            }
            if (file != null) {
                // photoURI = FileProvider.getUriForFile(this, getPackageName(), file);
                ImageUriFromCamera = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                this.cameraInt.putExtra("output", ImageUriFromCamera);
                startActivityForResult(this.cameraInt, IMAGE_CODE);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {

                    openCamera();
                } else {
                    //Permission from pop-up denied
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_CODE) {
                //Set the image to imageView
                try {
                    //selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageUriFromCamera);
                    final InputStream imageStream = getContentResolver().openInputStream(ImageUriFromCamera);
                    selectedImage = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(imageStream), 96, 96, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setImageBitmap(selectedImage);
                //Toast.makeText(this,ImageUriFromCamera.toString(),Toast.LENGTH_SHORT).show();

            } else if (requestCode == RESULT_LOAD_IMG) {
                try {
                    final Uri imageUri = data.getData();
                    if (!imageUri.equals("")) {
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        selectedImage = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(imageStream), 96, 96, true);
                        image.setImageBitmap(selectedImage);
                    } else {
//                        Drawable d = getDrawable(R.drawable.logo);
//                        selectedImage = ((BitmapDrawable)d).getBitmap();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }


            }

        }
    }

    private File createFileImage() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File createTempFile = File.createTempFile("IMG_" + format + "_", ".jpg", Build.VERSION.SDK_INT >= 8 ? getExternalFilesDir(Environment.DIRECTORY_PICTURES) : null);
        imageFilePathCamera = createTempFile.getAbsolutePath();
        return createTempFile;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public void showDetail(Post post) {
        Intent i = new Intent(getApplicationContext(), postDetailActivity.class);
        Bundle b = new Bundle();
        b.putInt("id", post.getID());
        b.putString("message", post.getMessage());
        b.putString("username", post.getUser_name());
        i.putExtras(b);
        if (post.getImage() != null) {
            i.putExtra("Image", scaleDownBitmap(post.getImage(), 50, this));
        }
        startActivity(i);
    }

    @Override
    public void lonGPress() {
        if (username.equals(DataBaseHandler.logged_username)) {
            isContexualModelEnabled = true;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu);
            //getSupportActionBar().setTitle("0 Item Selected");
            itemCounter.setText("0 Item Selected");
            toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
            PostRecyclerAdapter adapter = new PostRecyclerAdapter(posts_list, this, PostActivities.this);
            recyclerView.setAdapter(adapter);
        }

    }


    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    @Override
    protected void onRestart() {
        setRecyclerView();
        super.onRestart();
    }

    public void makeSelection(View view, int adapterPosition) {
        if (((CheckBox) view).isChecked()) {
            selected_posts.add(posts_list.get(adapterPosition));
            this.counter++;
            updateCounter();
        } else {
            selected_posts.remove(posts_list.get(adapterPosition));
            this.counter--;
            updateCounter();
        }
    }

    public void updateCounter() {
        String sentense;
        if (counter <= 1) {
            sentense = " Item Selected";
        } else {
            sentense = " Items Selected";
        }
        itemCounter.setText(this.counter + sentense);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            adapter.removeItems(selected_posts);
            finish();
            RemoveContextualActionMenu();
        }
        if (item.getItemId() == R.id.share) {
//            adapter.sendSelectedPosts(selected_posts);
//            RemoveContextualActionMenu();
        }
        return true;
    }

    public void RemoveContextualActionMenu() {
        isContexualModelEnabled = false;
        itemCounter.setText("Posts");
        toolbar.getMenu().clear();
        counter = 0;
        selected_posts.clear();
        adapter.notifyDataSetChanged();
    }

}