package edu.csc4360.thescotchdatabase;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddScotchActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText tastingNotesEditT;
    private RatingBar ratingBar;
    private ImageView imageView;
    private CheckBox checkbox;
    private String pathToFile;

    private Button btnAdd, btnChoose, btnCamera;
    private ScotchDBManager scotchDB;
    private float ratingValue = -1.0f;
    private boolean favorite = false;
    final int REQUEST_CODE_GALLERY = 50;

    private ImageUtil convertImg;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_scotch);

        init();

        if(savedInstanceState != null){
            String name = savedInstanceState.getString("Name");
            nameEditText.setText(name);
            String notes = savedInstanceState.getString("Notes");
            tastingNotesEditT.setText(notes);
            ratingValue = savedInstanceState.getFloat("Rating");
            ratingBar.setRating(ratingValue);
            favorite = savedInstanceState.getBoolean("Favorite");
            checkbox.setChecked(favorite);
            String path = savedInstanceState.getString("Image");
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            imageView.setImageURI(uri);
            //Bitmap bitmap = getIntent().getExtras().getParcelable("Image");

            //String image = savedInstanceState.getString("Image");
            //imageView.setImageBitmap(bitmap);
        }

        addListenerOnAdd();
        addListenerOnFavorite();
        addListenerOnChooseImg();
        addListenerOnRatingBar();
        addListenerOnCameraBtn();
    }

    public void addListenerOnAdd(){
        btnAdd = findViewById(R.id.btnAdd2);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please provide name of scotch", Toast.LENGTH_SHORT).show();
                } else if (ratingValue == -1.0f) {
                    Toast.makeText(getApplicationContext(), "Please provide a rating", Toast.LENGTH_SHORT).show();
                } else if (tastingNotesEditT.getText().toString().trim().length() > 45) {
                    Toast.makeText(getApplicationContext(), "Too many characters in tasting notes!", Toast.LENGTH_SHORT).show();
                } else
                    try {
                        if (tastingNotesEditT.getText().toString().trim().length() != 0) {
                            final String name = nameEditText.getText().toString().trim();
                            final String notes = tastingNotesEditT.getText().toString().trim();

                            final Bitmap bitmap = getBitmapFromDrawable(imageView.getDrawable());

                            new Thread(new Runnable(){
                                @Override
                                public void run(){
                                    scotchDB.open().insertScotch(name, ratingValue, notes, favorite, convertImg.convert(bitmap));
                                    //scotchDB.close();
                                    AddScotchActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                                            nameEditText.setText("");
                                            tastingNotesEditT.setText("");
                                            ratingBar.setRating(4.0f);
                                            checkbox.setChecked(false);
                                            favorite = false;
                                            imageView.setImageResource(R.drawable.ricksanchezscotch);
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please provide tasting notes!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        if (nameEditText.getText().toString().trim().length() != 0)
            savedInstanceState.putString("Name", nameEditText.getText().toString().trim());
        if (tastingNotesEditT.getText().toString().trim().length() != 0)
            savedInstanceState.putString("Notes", tastingNotesEditT.getText().toString().trim());
        if(ratingValue > -1.0f)
            savedInstanceState.putFloat("Rating", ratingValue);
        savedInstanceState.putBoolean("Favorite", favorite);

        Bitmap bitmap = getBitmapFromDrawable(imageView.getDrawable());
        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), bitmap);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));
        //itmap bitmap = getBitmapFromDrawable(imageView.getDrawable());B
        savedInstanceState.putString("Image", finalFile.getAbsolutePath());
        //Bitmap bitmap = getBitmapFromDrawable(imageView.getDrawable());
        //savedInstanceState.putString("Image", convertImg.convert(bitmap));

        // etc.
    }

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public void addListenerOnRatingBar() {

        ratingBar = findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratingValue = rating;
                if(fromUser)
                    Toast.makeText(getApplicationContext(), "Rating changed to " + ratingValue, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addListenerOnFavorite(){

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    favorite = true;
                    Toast.makeText(getApplicationContext(), "Set as favorite!", Toast.LENGTH_SHORT).show();
                }else{
                    favorite = false;
                    Toast.makeText(getApplicationContext(), "No longer set as favorite!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addListenerOnChooseImg() {
        btnChoose = findViewById(R.id.btnChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        AddScotchActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
    }

    public void addListenerOnCameraBtn() {
        btnCamera = findViewById(R.id.camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchPhotoTakenAction();
            }
        });
    }

    private void dispatchPhotoTakenAction() {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhoto.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if(photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(AddScotchActivity.this, "edu.csc4360.thescotchdatabase.fileprovider", photoFile);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhoto, 1);
            }
        }
    }

    private File createPhotoFile(){
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("scotchPhoto", "Exception: " + e.toString());
        }
        return image;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
            imageView.setImageBitmap(bitmap);
        }
        else if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){

            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void init() {
        nameEditText  = findViewById(R.id.edtName );
        tastingNotesEditT  = findViewById(R.id.edtPrice );
        ratingBar = findViewById(R.id.ratingBar);
        imageView = findViewById(R.id.imageView);
        checkbox = findViewById(R.id.checkBox);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set a title for toolbar
        toolbar.setTitle("Add to Scotch Database");
        toolbar.setTitleTextColor(Color.WHITE);

        // Set support actionbar with toolbar
        setSupportActionBar(toolbar);

        // Enable up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Change the toolbar background color
        toolbar.setBackgroundColor(Color.parseColor("#FFAE00"));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator stateListAnimator = AnimatorInflater.loadStateListAnimator(this,
                    R.animator.scale);
            checkbox.setStateListAnimator(stateListAnimator);
        }

        convertImg = new ImageUtil();
        scotchDB = new ScotchDBManager(this);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

}
