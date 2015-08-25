package com.example.fhuang.myproj0;

import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_STR = "com.example.fhuang.main.KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        // }
        if (id == R.id.miGoBackFromMain) {
            // to ensure <- in top action bar leads to the same behavior as system Back button
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void spotifyStreamer(View v) { // v is the button pressed.
        // Toast.makeText(this, "This will launch SPOTIFY STREAMER !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SpotifyActivity.class);
        String message1 = ((Button) v).getText().toString();
        intent.putExtra(EXTRA_STR, message1);
        startActivity(intent);

    }

    public void scoresApp(View v) { // v is the button pressed.
        Toast.makeText(this, "This will launch SCORES APP !", Toast.LENGTH_SHORT).show();
    }

    public void libraryApp(View v) { // v is the button pressed.
        Toast.makeText(this, "This will launch LIBRARY APP !", Toast.LENGTH_SHORT).show();
    }

    public void buildItBigger(View v) { // v is the button pressed.
        Toast.makeText(this, "This will launch BUILD IT BIGGER !", Toast.LENGTH_SHORT).show();
    }

    public void xyzReader(View v) { // v is the button pressed.
        Toast.makeText(this, "This will launch XYZ READER !", Toast.LENGTH_SHORT).show();
    }

    public void capstoneMyOwnApp(View v) { // v is the button pressed.
        Toast.makeText(this, "This will launch CAPSTONE: MY OWN APP !", Toast.LENGTH_SHORT).show();
    }
}
