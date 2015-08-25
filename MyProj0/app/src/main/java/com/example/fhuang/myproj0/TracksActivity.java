package com.example.fhuang.myproj0;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


public class TracksActivity extends ActionBarActivity {
    private ListView lvTracks;
    private ArrayList<TrackC> ltATrack; // list of artist tracks
    private TrackAdapter adapterATrack; // ArrayAdapter for list of artist tracks
    private int mStackLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStackLevel = 0;

        // requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_tracks);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle(ArtistTracksC.artist_name);

        lvTracks = (ListView) findViewById(R.id.lvTracks);

        // to restore previous state without internet search as rotation occurs
        if (ArtistTracksC.ltATrack == null) {
            ArtistTracksC.ltATrack = new ArrayList<TrackC>();
        }
        ltATrack = ArtistTracksC.ltATrack;

        adapterATrack = new TrackAdapter(this, ltATrack);
        lvTracks.setAdapter(adapterATrack);
        lvTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ArtistTracksC.currPos = position;
                ArtistTracksC.mediaPlayer = null;
                ArtistTracksC.mProgressStatus = 0;
                ArtistTracksC.set_playing( true );
                showMediaPlayerDialog(position);
            }
        });

        if (ltATrack.size() == 0) {
            // pull out artist's spotify Id from intent
            String artistId = getIntent().getStringExtra("artistId");

            // to send async requests and load tracks to ArtistTracksC.ltATrack (same as ltATrack)
            populate_artist_tracks(artistId);
        } else {
            adapterATrack.notifyDataSetChanged();
        }
    }
    public void showMediaPlayerDialog(int pos) {
        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment player = MediaPlayerDialogFragment.newInstance(ltATrack.get(pos).name);
        player.show(ft, "dialog");
    }
    // This requires API 16 or later.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        // add the clear top flag - which checks if the parent (main)
        // activity is already running and avoids recreating it
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public Context cnt = null;

    // to populate list of artist tracks
    public void populate_artist_tracks (String artistId) {
        if (isNetworkAvailable() == false) {
            Toast.makeText(this, "network is disconnected", Toast.LENGTH_SHORT).show();
            return;
        }

        String trackSearch = "https://api.spotify.com/v1/artists/" + artistId +
                "/top-tracks?country=US";
        cnt = (Context) this;

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(trackSearch, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int StatusCode, Header[] headers, JSONObject response) {
                // Log.d("DEBUG", response.toString());
                // Toast.makeText(cnt, response.toString(), Toast.LENGTH_LONG).show();
                try {
                    ltATrack.clear();
                    JSONArray arr1 = response.getJSONArray("tracks");
                    if (arr1.length() == 0) {
                        String not_found_msg = "no artist tracks are found";
                        Toast.makeText(cnt, not_found_msg, Toast.LENGTH_LONG).show();
                    } else {
                        for (int k = 0; k < arr1.length() && ltATrack.size() <= 10; ++k) {
                            JSONObject obj2 = arr1.getJSONObject(k);
                            TrackC track = new TrackC( obj2 );
                            ltATrack.add( track );
                        }
                        adapterATrack.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] h, String s, Throwable throwable) {
                String not_found_msg = "no artist tracks are found";
                Toast.makeText(cnt, not_found_msg, Toast.LENGTH_LONG).show();
                // Toast.makeText(cnt, "fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
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
        if (id == R.id.miGoBackToArtistSearch) {
            // to ensure <- in top action bar leads to the same behavior as system Back button
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
