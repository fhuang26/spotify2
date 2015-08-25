package com.example.fhuang.myproj0;

import java.util.ArrayList;

public class ArtistPhotosC {
    public static ArrayList<ArtistPhoto> ltAPhoto = null; // list of artist photos
    // This will help retain list of artists with photos. When rotation (configuration change)
    // occurs on SpotifyActivity, or coming back from TracksActivity (popular track display),
    // artist list ( ltAPhoto ) in SpotifyActivity can be retrieved from here. It won't be
    // needed to send async requests on internet to fetch artist list again. So this helps
    // improve performance when rotation occurs during SpotifyActivity or it goes back from
    // next activity.

    public static String name_to_search = null; // keyword of artist name to search
}
