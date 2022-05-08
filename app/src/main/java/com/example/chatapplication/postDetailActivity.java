package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.FileProvider;

import android.Manifest;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class postDetailActivity extends AppCompatActivity {
    private int post_id;
    private String id;
    private String user_name;
    DataBaseHandler db;
    private static final int IMAGE_CODE = 1001;
    ImageView image;
    Bitmap selectedImage;
    static Uri ImageUriFromCamera;
    Intent cameraInt;
    String imageFilePathCamera;
    Button fromCamera, fromGallery;
    BottomSheetDialog bottomSheetDialog;
    private static final int RESULT_LOAD_IMG = 1002;
    private static final int PERMISSION_CODE = 1000;
    private String message;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DataBaseHandler(this);
        setContentView(R.layout.activity_post_detail);
        TextView textView = findViewById(R.id.tv_Message);
        ImageView imageView = findViewById(R.id.tv_image);
        Bundle bundle = getIntent().getExtras();
        message = bundle.getString("message");
        textView.append(message);
        post_id = bundle.getInt("id");
        user_name = bundle.getString("username");
        id = String.valueOf(post_id);
        Intent intent = getIntent();
        bitmap = (Bitmap) intent.getParcelableExtra("Image");
        imageView.setImageBitmap(bitmap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }
        if (user_name.equals(DataBaseHandler.logged_username)) {
            MenuItem shareItem = menu.findItem(R.id.share);
            shareItem.setVisible(true);
            MenuItem editItem = menu.findItem(R.id.edit);
            editItem.setVisible(true);
            MenuItem deleteItem = menu.findItem(R.id.delete);
            deleteItem.setVisible(true);
        } else {
            MenuItem shareItem = menu.findItem(R.id.share);
            shareItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id_item = item.getItemId();
        switch (id_item) {
            case R.id.edit: {
                addData();
                return true;
            }
            case R.id.share: {
                sendEmail(bitmap, message);
                return true;
            }
            case R.id.delete: {
                db.deletePost(id);
                Toast.makeText(this, "Post Deleted Successfully", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void addData() {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.data_add_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(postDetailActivity.this);
        builder.setView(dialoglayout);
        AlertDialog dialog_1 = builder.create();
        //builder.show();
        dialog_1.show();
        EditText y_message = dialog_1.findViewById(R.id.your_message);
        y_message.setText(message);
        image = dialog_1.findViewById(R.id.image);
        image.setImageBitmap(bitmap);
        selectedImage = bitmap;
        Button getImages = dialog_1.findViewById(R.id.add_image);
        //selectedImage = image.getDrawingCache();
        Button submit = dialog_1.findViewById(R.id.submit);
        getImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(postDetailActivity.this);
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
                post.setID(post_id);
                post.setMessage(y_message.getText().toString());
                post.setUser_name(DataBaseHandler.logged_username);
                Boolean ln = false;
                if (selectedImage != null) {
                    ln = db.updatePost(post, getBitmapAsByteArray(selectedImage));

                } else {
                    Drawable d = getDrawable(R.drawable.logo);
                    selectedImage = ((BitmapDrawable) d).getBitmap();
                    ln = db.updatePost(post, getBitmapAsByteArray(selectedImage));
                }
                if (ln) {
                    dialog_1.dismiss();
                    Log.e("Post", "Updated");
                    Toast.makeText(getApplicationContext(), "Post Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
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

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
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

    public void sendEmail(Bitmap m, String message) {

        File file = BitmapSaver.saveImageToExternalStorage(this, m);

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, " ");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Post");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        emailIntent.setType("image/*"); // accept any image
        //attach the file to the intent
        Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                BuildConfig.APPLICATION_ID + ".provider", file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Send your email in:"));

    }
}