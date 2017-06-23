package com.coderalone.admin.funnyringtones.util;

public class Constant {

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String IMAGE = "image";

    // Max timeout request miniseconde.
    public static final int MAX_TIMEOUT_REQUEST = 5000;

    // API url.
    public static final String CATEGORY_LIST_API_URL = "http://ringtonespecial.esy.es/ringtone_special/ringtone_api/category_list.php";
    public static final String SUB_CATEGORY_LIST_API_URL = "http://ringtonespecial.esy.es/ringtone_special/ringtone_api/sub_category_list.php?category=%s&page=%s";
    public static final String RINGTONE_LIST_API_URL = "http://ringtonespecial.esy.es/ringtone_special/ringtone_api/ringtone_list.php?subCategory=%s&page=%s";
    public static final String DATA_UPLOAD_URL = "http://ringtonespecial.esy.es/ringtone_special/ringtone_api/data/uploads/";

    // Bundle key
    public static final String CATEGORY = "Category";
    public static final String SUB_CATEGORY = "SubCategory";
    public static final String SERVER_MAINTENANCE = "ServerMaintennace";

    // Category code
    public static final int CARTOON_ID = 1;
    public static final int GAME_ID = 2;
    public static final int MOVIE_ID = 3;
    public static final int MUSIC_ID = 4;

    // Category name
    public static final String CARTOON = "Cartoon";
    public static final String GAME = "Game";
    public static final String MOVIE = "Movie";
    public static final String MUSIC = "Music";

    // Permistion request code.
    public static final int MY_PERMISSIONS_REQUEST_SETTING = 100;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;

    // Audio downloaded file local path
    public static final String PATH_LOCAL_AUDIO_DOWNLOAD = "ringtone_special/media/audio/";
    // Audio downloaded file extension
    public static final String MP3_EXTENSION = ".mp3";

    public static final int MAX_PROGRESS = 100;
    public static final String PACKAGE = "package:";
    public static final String MESSENGER = "Messenger";
    public static final String KEY_TOTAL_DURATION = "TotalDuration";
    public static final String KEY_CURRENT_DURATION = "CurrentDuration";


    public static final int REQUEST_DISCONNECT_CODE = 1;
    public static final int RESULT_CODE_CANCLE = 0;
    public static final int RESULT_CODE_OK = 1;

}