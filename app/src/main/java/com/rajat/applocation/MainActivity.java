package com.rajat.applocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rajat.applocation.service.LocationService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    Button park,home,save;
    Intent intent=new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intentService=new Intent(MainActivity.this, LocationService.class);
        startService(intentService);

        park=(Button) findViewById(R.id.btn_park);
        home=(Button) findViewById(R.id.btn_home);
        save=(Button) findViewById(R.id.btn_save);

        park.setOnClickListener(this);
        home.setOnClickListener(this);
        save.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        String msg = "";
        switch (id) {
            case R.id.sub_list:
                msg = "List";
                intent=new Intent(MainActivity.this,HistoryListActivity.class);
                startActivity(intent);
                break;
            case R.id.sub_map:
                msg = "Map";
                intent=new Intent(MainActivity.this,HistoryMapActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        Toast.makeText(MainActivity.this, msg + " Selected.", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_park:intent=new Intent(MainActivity.this,ParkActivity.class);
                startActivity(intent);
                break;
//            case R.id.btn_home:intent=new Intent(MainActivity.this,HomeActivity.class);
//
//                break;
            case R.id.btn_save:intent=new Intent(MainActivity.this,SaveActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
