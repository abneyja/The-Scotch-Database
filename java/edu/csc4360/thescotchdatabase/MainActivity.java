package edu.csc4360.thescotchdatabase;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set a title for toolbar
        toolbar.setTitle("Scotch Database Main Menu");
        toolbar.setTitleTextColor(Color.WHITE);

        // Set support actionbar with toolbar
        setSupportActionBar(toolbar);

        // Change the toolbar background color
        toolbar.setBackgroundColor(Color.parseColor("#FFAE00"));
    }

    public void openAddScotchActivity(View view) {
        startActivity(new Intent(this, AddScotchActivity.class));
    }

    public void openInventoryActivity(View view) {
        startActivity(new Intent(this, InventoryActivity.class));
    }
}
