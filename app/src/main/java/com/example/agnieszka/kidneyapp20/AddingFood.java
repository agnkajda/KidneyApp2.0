package com.example.agnieszka.kidneyapp20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.widget.Toast;

public class AddingFood extends AppCompatActivity {

    Button clickToSearch;
    EditText typeToSearch;
    Context context;
    //private ArrayAdapter<String> mKidneyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_food);

        clickToSearch = (Button) findViewById(R.id.search_button);
        typeToSearch = (EditText) findViewById(R.id.search_for_food);
        //setHasOptionsMenu(true);

        View.OnClickListener clicking = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = getApplicationContext();
                String foodName = typeToSearch.getText().toString();

                Intent intent = new Intent(context, TestActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, foodName);
                Toast.makeText(context, "co zostalo inserted: " + foodName, Toast.LENGTH_SHORT).show();
                Log.d("LOG", "Co tu się kryje? " + foodName + " <- To.");
                startActivity(intent);
            }
        };
        clickToSearch.setOnClickListener(clicking);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
