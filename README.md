# spotify2
Spotify Streamer - stage 2

(1) ArtistFragment inflate(R.layout.fragment_artist, container, false) , and
    it will pick fragment_artist_grid.xml for tablet, and fragment_artist_list.xml
    for phone.

(2) I did two fragments : ArtistFragment and MediaPlayerDialogFragment. I didn't
    make the third, TrackFragment, yet. Previouly I tried to do generic 2-pane UI,
    left half screen for artist search and right half screen for popular tracks.
    Yet I realized left half for artist search on phone is too narrow and not
    convenient for users. Following guide from coaches in office hour, I use 1
    screen for artist search (SpotifyActivity + ArtistFragment), 1 screen for
    popular tracks of an artist (TracksActivity), and a DialogFragment for media
    player ( MediaPlayerDialogFragment ). To support tablet better, we may create
    a track fragment and do extended work to make 2-pane UI for tablet.

(3) For MediaPlayerDialogFragment , song playing and progress bar play or pause
    at the same pace. We can mixed play, pause, and a few phone roatations. Song
    playing and progress bar are synced. We use thread and synchronized to suppot
    progress bar.
    
(4) To avoid playing a song from beginning after rotations and reduce repeated
    slower operations (e.g., mediaPlayer.prepare() ), we save and restore previous
    state from ArtistTracksC, for TracksActivity and MediaPlayerDialogFragment.
    
(5) I added album name to DialogFragment in rev 2.
