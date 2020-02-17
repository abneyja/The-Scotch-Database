package edu.csc4360.thescotchdatabase;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class InventoryActivity extends AppCompatActivity
        implements ScotchSearchFragment.ScotchSearchAdapterListener, ScotchRangeSeekBarFragment.ScotchRangeSeekBarAdapterListener {

    private Toolbar toolbar;
    GridView gridView;
    ArrayList<Scotch> list;
    ScotchListAdapter adapter = null;
    final ScotchDBManager scotchDB = new ScotchDBManager(this);
    private Scotch updateScotch;
    private EditText nameEditText;
    private EditText tastingNotesEditT;
    private RatingBar ratingBar;
    private ImageView imageView;
    private CheckBox checkbox;
    private CheckBoxTriStates checkBoxTriStates;
    private Button btnCamera;
    private String text = "";
    private String pathToFile;
    private int Position = -1;
    private float ratingValue = -1.0f;
    private float leftValue = 0.0f;
    private float rightValue = 5.0f;
    private boolean favorite = false;
    private boolean ad = true;
    private boolean nr = true;
    private boolean dialogUpdate = false;

    private MenuItem asc_desc;
    private MenuItem name_rating;

    private Bundle savedState;

    private int state = -1;
    static private final int UNKNOWN = -1;
    static private final int UNCHECKED = 0;
    static private final int CHECKED = 1;
    static private final int CAMERA_REQUEST = 9999;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventare);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set a title for toolbar
        toolbar.setTitle("Scotch Database Inventory");
        toolbar.setTitleTextColor(Color.WHITE);

        // Set support actionbar with toolbar
        setSupportActionBar(toolbar);

        // Enable up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Change the toolbar background color
        toolbar.setBackgroundColor(Color.parseColor("#FFAE00")); //FFAE00 720000

        gridView = (GridView) findViewById(R.id.gridView);
        //searchView = findViewById(R.id.searchView);
        list = new ArrayList<>();
        adapter = new ScotchListAdapter(this, R.layout.items, list);
        checkBoxTriStates = findViewById(R.id.triBox);
        state = getFromSPState("tri");
        leftValue = getFromSPFloat("LeftValue");
        rightValue = getFromSPFloat("RightValue");
        updateBtn();
        gridView.setAdapter(adapter);

        if(savedInstanceState != null){
            state = savedInstanceState.getInt("tri");
            updateBtn();

            dialogUpdate = savedInstanceState.getBoolean("Dialog");

            if (dialogUpdate) {
                savedState = savedInstanceState;
                Position = savedInstanceState.getInt("Position");
                showDialogUpdate(InventoryActivity.this, Position);
            }

        }

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        updateScotchList();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm1 = getSupportFragmentManager();
        FragmentPager pagerAdapter = new FragmentPager(fm1);
        // Here you would declare which page to visit on creation
        pager.setAdapter(pagerAdapter);

        checkBoxTriStates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            // checkbox status is changed from uncheck to checked.
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //state = getFromSPState("tri");
                if (buttonView.isPressed()) {
                    switch (state) {
                        case UNKNOWN:
                            state = UNCHECKED;
                            saveInSpState("tri", state);
                            Toast.makeText(getApplicationContext(), "Showing only non-favorite scotch", Toast.LENGTH_SHORT).show();
                            break;
                        case UNCHECKED:
                            state = CHECKED;
                            saveInSpState("tri", state);
                            Toast.makeText(getApplicationContext(), "Showing only favorite scotch", Toast.LENGTH_SHORT).show();
                            break;
                        case CHECKED:
                            state = UNKNOWN;
                            saveInSpState("tri", state);
                            Toast.makeText(getApplicationContext(), "Showing all scotch", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                updateScotchList();
                updateBtn();
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                hideKeyboard(InventoryActivity.this);
                //Toast.makeText(getApplicationContext(), "Position " + position,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "list position " + list.get(position),Toast.LENGTH_LONG).show();

                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        Scotch scotch = new Scotch();

                        Cursor cursor = scotchDB.getScotch(list.get(position).getId());
                        while(cursor.moveToNext()) {
                            int scotch_id = cursor.getInt(0);
                            String name = cursor.getString(1);
                            float rating = cursor.getFloat(2);
                            String notes = cursor.getString(3);
                            boolean favorite = cursor.getInt(4) > 0;
                            String image = cursor.getString(5);

                            scotch = new Scotch(scotch_id, name, notes, rating, favorite, image);
                        }
                       // scotchDB.close();

                        final Bundle args = new Bundle();
                        args.putString("name", scotch.getName());
                        args.putString("notes", scotch.getTasting_notes());
                        args.putFloat("rating", scotch.getStars());
                        args.putBoolean("favorite", scotch.getFavorite());
                        args.putString("image", scotch.getImage());

                        InventoryActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentManager fm = getSupportFragmentManager();
                                ScotchFragment fragment = new ScotchFragment();
                                fragment.setArguments(args);
                                fm.beginTransaction().replace(R.id.activity_inv, fragment).addToBackStack(null).commit();
                            }
                        });
                    }
                }).start();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {

                CharSequence[] items = {"Update","Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            dialogUpdate = true;
                            showDialogUpdate(InventoryActivity.this, list.get(position).getId());
                        } else {
                            // delete
                            showDialogDelete(list.get(position).getId());
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        if(dialogUpdate) {
            if (nameEditText.getText().toString().trim().length() != 0)
                savedInstanceState.putString("Name", nameEditText.getText().toString().trim());
            if (tastingNotesEditT.getText().toString().trim().length() != 0)
                savedInstanceState.putString("Notes", tastingNotesEditT.getText().toString().trim());
            if (ratingValue > -1.0f)
                savedInstanceState.putFloat("Rating", ratingValue);
            savedInstanceState.putBoolean("Favorite", favorite);
            savedInstanceState.putBoolean("Dialog", dialogUpdate);
            savedInstanceState.putInt("Position", Position);
            saveInSp("LeftValue", leftValue);
            saveInSp("RightValue", rightValue);

            Bitmap bitmap = getBitmapFromDrawable(imageView.getDrawable());
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), bitmap);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            savedInstanceState.putString("Image", finalFile.getAbsolutePath());
        }

        savedInstanceState.putInt("tri", state);
        // etc.
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

    private void updateBtn()
    {
        int btnDrawable = R.drawable.ic_star_half_yellow_24dp;
        switch (state)
        {
            case UNKNOWN:
                btnDrawable = R.drawable.ic_star_half_yellow_24dp;
                break;
            case UNCHECKED:
                btnDrawable = R.drawable.ic_star_border_black_24dp;
                break;
            case CHECKED:
                btnDrawable = R.drawable.ic_star_yellow_24dp;
                break;
        }
        checkBoxTriStates.setButtonDrawable(btnDrawable);

    }

/*    @Override
    public void onCheckBoxTriStatesSent(int state){
        this.state = state;
        updateScotchList();
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options_menu, menu);
        asc_desc = menu.findItem(R.id.asc_desc);
        asc_desc.setChecked(getFromSP("ad"));
        name_rating = menu.findItem(R.id.name_rating);
        name_rating.setChecked(getFromSP("nr"));

        if(asc_desc.isChecked())
            asc_desc.setTitle("Ascending");
        else
            asc_desc.setTitle("Descending");

        if(name_rating.isChecked())
            name_rating.setTitle("Sort by Name");
        else
            name_rating.setTitle("Sort by Rating");

        updateScotchList();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.asc_desc:
                if(asc_desc.isChecked()){
                    // If item already checked then unchecked it
                    asc_desc.setChecked(false);
                    saveInSp("ad", false);
                    ad = false;
                    asc_desc.setTitle("Descending");
                    Toast.makeText(getBaseContext(), "Sorting in descending order.",Toast.LENGTH_SHORT).show();
                }else{
                    // If item is unchecked then checked it
                    //item.setChecked(true);
                    asc_desc.setChecked(true);
                    saveInSp("ad", true);
                    ad = true;
                    asc_desc.setTitle("Ascending");
                    Toast.makeText(getBaseContext(), "Sorting in ascending order.",Toast.LENGTH_SHORT).show();
                }
                // Update the text view text style
                updateScotchList();
                return true;
            case R.id.name_rating:
                if(name_rating.isChecked()){
                    // If item already checked then unchecked it
                    //item.setChecked(false);
                    name_rating.setChecked(false);
                    saveInSp("nr", false);
                    nr = false;
                    name_rating.setTitle("Sort by Rating");
                    Toast.makeText(getBaseContext(), "Sorting by rating.",Toast.LENGTH_SHORT).show();
                }else {
                    // If item is unchecked then checked it
                    //item.setChecked(true);
                    name_rating.setChecked(true);
                    saveInSp("nr", true);
                    nr = true;
                    name_rating.setTitle("Sort by Name");
                    Toast.makeText(getBaseContext(), "Sorting by name.",Toast.LENGTH_SHORT).show();
                }
                // Update the text view text style
                updateScotchList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private float getFromSPFloat(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCOTCH", android.content.Context.MODE_PRIVATE);
        return preferences.getFloat(key, 1.0f);
    }

    private void saveInSp(String key, float value){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCOTCH", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    private boolean getFromSP(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCOTCH", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    private void saveInSp(String key, boolean value){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCOTCH", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private int getFromSPState(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCOTCH", android.content.Context.MODE_PRIVATE);
        return preferences.getInt(key, -1);
    }

    private void saveInSpState(String key, int value){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("SCOTCH", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    @Override
    public void onScotchSearchAdapterSent(String newText) {
        adapter.getFilter().filter(newText);
        this.text = newText;
    }

    @Override
    public void onScotchRangeSeekBarAdapterSent(float leftValue, float rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        saveInSp("LeftValue", leftValue);
        saveInSp("RightValue", rightValue);

        if ((leftValue % 0.5f) == 0 && (rightValue % 0.5f) == 0) {
            updateScotchList();
            adapter.getFilter().filter(text);
        }
    }

    private void showDialogUpdate(final Activity activity, final int position){

        final Dialog dialog = new Dialog(activity);
        dialog.setCanceledOnTouchOutside(true);
        dialogUpdate = true;
        Position = position;
        dialog.setContentView(R.layout.update_scotch);
        dialog.setTitle("Update");

        nameEditText = (EditText) dialog.findViewById(R.id.edtName1);
        tastingNotesEditT = (EditText) dialog.findViewById(R.id.edtPrice1);
        ratingBar = dialog.findViewById(R.id.ratingBar1);
        checkbox = dialog.findViewById(R.id.checkBox);
        imageView = dialog.findViewById(R.id.imageView1);
        btnCamera = dialog.findViewById(R.id.camera);

        new Thread(new Runnable(){
            Scotch scotch = new Scotch();

            @Override
            public void run(){

                Cursor cursor = scotchDB.open().getScotch(position);
                while(cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    float rating = cursor.getFloat(2);
                    String notes = cursor.getString(3);
                    boolean favorite = cursor.getInt(4) > 0;
                    String image = cursor.getString(5);

                    scotch = new Scotch(id, name, notes, rating, favorite, image);
                }
                //scotchDB.close();
                updateScotch = scotch;

                InventoryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nameEditText.setText(scotch.getName());
                        tastingNotesEditT.setText(scotch.getTasting_notes());
                        ratingValue = scotch.getStars();
                        ratingBar.setRating(ratingValue);

                        favorite = scotch.getFavorite();

                        imageView.setImageBitmap(ImageUtil.convert(scotch.getImage()));
                        checkbox.setChecked(favorite);

                        if(savedState != null){
                            String name = savedState.getString("Name");
                            nameEditText.setText(name);
                            String notes = savedState.getString("Notes");
                            tastingNotesEditT.setText(notes);
                            ratingValue = savedState.getFloat("Rating");
                            ratingBar.setRating(ratingValue);
                            favorite = savedState.getBoolean("Favorite");
                            checkbox.setChecked(favorite);
                            String path = savedState.getString("Image");
                            if(path != null) {
                                File file = new File(path);
                                Uri uri = Uri.fromFile(file);
                                imageView.setImageURI(uri);
                            }
                            savedState = null;
                        }

                        // set width for dialog
                        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
                        // set height for dialog
                        int height;
                        if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        {
                            // Portrait Mode
                            height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
                        } else {
                            // Landscape Mode
                            height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.85);
                        }

                        dialog.getWindow().setLayout(width, height);
                        dialog.show();
                    }
                });
            }
        }).start();

        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);
        Button btnChoose = (Button) dialog.findViewById(R.id.btnChoose1);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                dialogUpdate = false;
                savedState = null;
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please provide name of scotch", Toast.LENGTH_SHORT).show();
                } else if (ratingValue == -1.0f) {
                    Toast.makeText(getApplicationContext(), "Please provide a rating", Toast.LENGTH_SHORT).show();
                } else if (imageView.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please provide an image", Toast.LENGTH_SHORT).show();
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
                                    scotchDB.open().updateScotch(updateScotch.getId(), name, ratingValue, notes, favorite, ImageUtil.convert(bitmap));
                                    //scotchDB.close();
                                    InventoryActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Updated successfully!", Toast.LENGTH_SHORT).show();
                                            dialogUpdate = false;
                                            dialog.dismiss();
                                            updateScotchList();
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

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                ratingValue = rating;
                if(fromUser)
                    Toast.makeText(getApplicationContext(), "Rating changed to " + ratingValue, Toast.LENGTH_SHORT).show();
            }
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && checkbox.isPressed()){
                    favorite = true;
                    Toast.makeText(getApplicationContext(), "Set as favorite!", Toast.LENGTH_SHORT).show();
                }else if(!isChecked && checkbox.isPressed()){
                    favorite = false;
                    Toast.makeText(getApplicationContext(), "No longer set as favorite!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        InventoryActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

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
                Uri photoURI = FileProvider.getUriForFile(InventoryActivity.this, "edu.csc4360.thescotchdatabase.fileprovider", photoFile);
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

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }


    private void showDialogDelete(final int idScotch){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(InventoryActivity.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            try {
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        scotchDB.open().deleteScotch(idScotch);
                        //scotchDB.close();

                        InventoryActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Delete successfully!!!",Toast.LENGTH_SHORT).show();
                                updateScotchList();
                            }
                        });
                    }
                }).start();

            } catch (Exception e){
                Log.e("error", e.getMessage());
            }
            ;
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateScotchList(){
        // get all data from sqlite
        Cursor cursor = null;

        try {
            cursor = scotchDB.open().getAllScotch();
            list.clear();
            if(cursor != null && cursor.getCount()>0) {
                do {
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    float rating = cursor.getFloat(2);
                    String notes = cursor.getString(3);
                    boolean favorite = cursor.getInt(4) > 0;
                    String image = cursor.getString(5);

                    if (rating >= leftValue && rating <= rightValue) {
                        switch (state) {
                            case UNKNOWN:
                                list.add(new Scotch(id, name, notes, rating, favorite, image));
                                break;
                            case UNCHECKED:
                                if (!favorite)
                                    list.add(new Scotch(id, name, notes, rating, favorite, image));
                                break;
                            case CHECKED:
                                if (favorite)
                                    list.add(new Scotch(id, name, notes, rating, favorite, image));
                                break;
                        }

                    }
                } while (cursor.moveToNext());
                if(asc_desc != null && name_rating != null) {
                    if (asc_desc.isChecked() && name_rating.isChecked()) {
                        list.sort(Comparator.comparing(e -> e.getName().replaceAll("^(?i)The ", "")));
                    } else if (!asc_desc.isChecked() && name_rating.isChecked()) {
                        Comparator<Scotch> comparator = Comparator.comparing(e -> e.getName().replaceAll("^(?i)The ", ""));
                        list.sort(comparator.reversed());
                    } else if (asc_desc.isChecked() && !name_rating.isChecked()) {
                        list.sort(Comparator.comparing(e -> e.getStars()));
                    } else {
                        Comparator<Scotch> comparator = Comparator.comparing(e -> e.getStars());
                        list.sort(comparator.reversed());
                    }
                }else {
                    list.sort(Comparator.comparing(e -> e.getName().replaceAll("^(?i)The ", "")));
                }
            }

            adapter.getFilter().filter(text);
            adapter.notifyDataSetChanged();
        } finally {
            if(cursor != null)
                cursor.close();
        }
/*
        new Thread(new Runnable(){
            @Override
            public void run(){
                Cursor cursor = null;

                try {
                    cursor = scotchDB.open().getAllScotch();
                    list.clear();
                    do {
                        int id = cursor.getInt(0);
                        String name = cursor.getString(1);
                        float rating = cursor.getFloat(2);
                        String notes = cursor.getString(3);
                        boolean favorite = cursor.getInt(4) > 0;
                        String image = cursor.getString(5);

                        if(rating >= leftValue && rating <= rightValue) {
                            switch (state)
                            {
                                case UNKNOWN:
                                    list.add(new Scotch(id, name, notes, rating, favorite, image));
                                    break;
                                case UNCHECKED:
                                    if(!favorite)
                                        list.add(new Scotch(id, name, notes, rating, favorite, image));
                                    break;
                                case CHECKED:
                                    if(favorite)
                                        list.add(new Scotch(id, name, notes, rating, favorite, image));
                                    break;
                            }

                        }
                    }while(cursor.moveToNext());

                    } finally {
                        if(cursor != null)
                            cursor.close();
                    }

                InventoryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.getFilter().filter(text);
                    }
                });
            }
        }).start();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
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
        }else if(requestCode == 888 && resultCode == RESULT_OK && data != null){
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}



