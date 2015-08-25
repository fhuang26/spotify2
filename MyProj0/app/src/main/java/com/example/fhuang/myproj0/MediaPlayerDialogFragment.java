package com.example.fhuang.myproj0;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;


public class MediaPlayerDialogFragment extends DialogFragment {
    private EditText mEditText;

    public MediaPlayerDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    public static MediaPlayerDialogFragment newInstance(String title) {
        MediaPlayerDialogFragment frag = new MediaPlayerDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    private View vPlayerDialogFragment;
    private int pos;
    private ImageView ivLarge;
    private MediaPlayer mediaPlayer;
    private ImageButton btPause;
    private ImageButton btNext;
    private ImageButton btPrev;
    public ProgressBar pgBar;
    public Handler mHandler;
    public Thread td;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        vPlayerDialogFragment = inflater.inflate(R.layout.dialog_fragment_player, container, false);
        pgBar = (ProgressBar) vPlayerDialogFragment.findViewById(R.id.progressBar);
        btPause = (ImageButton) vPlayerDialogFragment.findViewById(R.id.btPause);
        if (ArtistTracksC.get_playing()) {
            btPause.setImageResource(R.mipmap.ic_media_pause);
        } else {
            btPause.setImageResource(R.mipmap.ic_media_play);
        }

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlay(v);
            }
        });
        btNext = (ImageButton) vPlayerDialogFragment.findViewById(R.id.btNext);
        btNext.setImageResource(R.mipmap.ic_media_next);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrack(v);
            }
        });
        btPrev = (ImageButton) vPlayerDialogFragment.findViewById(R.id.btPrev);
        btPrev.setImageResource(R.mipmap.ic_media_previous);
        btPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevTrack(v);
            }
        });
        ivLarge = (ImageView) vPlayerDialogFragment.findViewById(R.id.ivLargeImage);
        if (ArtistTracksC.ltATrack.size() > 0) {
            pos = ArtistTracksC.currPos;
        } else {
            pos = -1;
        }

        // String tn = "track " + ArtistTracksC.currPos;
        // Toast.makeText(getActivity(), tn, Toast.LENGTH_LONG).show();

        setup_mediaPlayer();

        if (ArtistTracksC.get_playing()) {
            playSong();
        }
        // btPause.forceLayout();

        return vPlayerDialogFragment;
    }

    public void setup_mediaPlayer () {
        if (0 <= pos && pos < ArtistTracksC.ltATrack.size()) {
            String title = ArtistTracksC.artist_name;
            getDialog().setTitle(title);

            TextView tvTrack = (TextView) vPlayerDialogFragment.findViewById(R.id.tvTrack);
            tvTrack.setText(ArtistTracksC.ltATrack.get(pos).name);

            String imageUrl = ArtistTracksC.ltATrack.get(pos).album.imageUrl;

            // load image by url into image view using picasso
            Picasso.with(getActivity()).load(imageUrl).into(ivLarge);

            String url = ArtistTracksC.ltATrack.get(pos).preview_url;
            if (ArtistTracksC.mediaPlayer == null) {
                ArtistTracksC.mediaPlayer = new MediaPlayer();
                ArtistTracksC.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    ArtistTracksC.mediaPlayer.setDataSource(url);
                    ArtistTracksC.mediaPlayer.prepare(); // might take long! (for buffering, web communication, etc)

                    ArtistTracksC.mProgressStatus = 0;
                    ArtistTracksC.duration = ArtistTracksC.mediaPlayer.getDuration();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            ArtistTracksC.mediaPlayer = null;
            ArtistTracksC.set_playing( false );
        }

        mediaPlayer = ArtistTracksC.mediaPlayer;

        pgBar.setMax(ArtistTracksC.duration);
        pgBar.setProgress(ArtistTracksC.mProgressStatus);
    }

    public void playSong() {
        if (mediaPlayer == null) return;

        // mediaPlayer.start();
        ArtistTracksC.set_playing(true);
        btPause.setImageResource(R.mipmap.ic_media_pause);
        // btPause.forceLayout();

        pgBar.setMax(ArtistTracksC.duration);
        pgBar.setProgress(ArtistTracksC.mProgressStatus);

        mHandler = new Handler();
        td = new Thread(new Runnable() {
            public void run() {
                while (ArtistTracksC.mProgressStatus < ArtistTracksC.duration) {
                    try {
                        Thread.sleep(100, 0);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (ArtistTracksC.get_playing()) {
                        ArtistTracksC.mProgressStatus += 100;
                    } else {
                        return;
                    }

                    mHandler.post(new Runnable() {
                        public void run() {
                            if (ArtistTracksC.get_playing()) {
                                pgBar.setProgress(ArtistTracksC.mProgressStatus);
                            } else {
                                return;
                            }
                        }
                    });
                }
            }
        });
        td.start();
    }

    public void nextTrack (View v) {
        if (ArtistTracksC.ltATrack.size() <= 0) return;
        if (ArtistTracksC.mediaPlayer != null) {
            ArtistTracksC.mediaPlayer.stop();
            ArtistTracksC.mediaPlayer = null;
        }

        ++pos;
        if (pos >= ArtistTracksC.ltATrack.size()) {
            pos = 0;
        }
        ArtistTracksC.currPos = pos;

        setup_mediaPlayer();
        // ArtistTracksC.set_playing(true);
        playSong();
    }

    public void prevTrack (View v) {
        if (ArtistTracksC.ltATrack.size() <= 0) return;
        if (ArtistTracksC.mediaPlayer != null) {
            ArtistTracksC.mediaPlayer.stop();
            ArtistTracksC.mediaPlayer = null;
        }

        --pos;
        if (pos < 0) {
            pos = ArtistTracksC.ltATrack.size() - 1;
        }
        ArtistTracksC.currPos = pos;

        setup_mediaPlayer();
        // ArtistTracksC.set_playing(true);
        playSong();
    }

    public void pausePlay(View v) {
        if (mediaPlayer == null) return;

        if (ArtistTracksC.get_playing()) {
            // mediaPlayer.pause();

            ArtistTracksC.set_playing( false );
            btPause.setImageResource(R.mipmap.ic_media_play);
        } else {
            playSong();
        }

    }

    public void onBackPressed() {
        if (ArtistTracksC.mediaPlayer != null) {
            ArtistTracksC.mediaPlayer.stop();
        }
        ArtistTracksC.mediaPlayer = null;
        ArtistTracksC.mProgressStatus = 0;
        dismiss();
    }
}
