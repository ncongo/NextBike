package com.bp.project.nextbike;

/**
 * Created by Congo on 10.12.2016.
 */
public class Config {
    public static final String URL = "http://192.168.0.20:80";
    public static final String URL_GET_USER_BY_ID = URL+"/nextbike/getUserById.php";
    //public static final String URL_LOGIN = "http://192.168.0.20/login.php?uname=";
    public static final String URL_INDEX_NEW = URL+"/nextbike/indexNew.php"; //"http://192.168.0.20:80/indexNew.php"
    public static final String URL_SAVE_CHANGES_USER_DATA = URL+"/nextbike/saveChangesUserData.php";
    public static final String URL_GET_ALL_RACKS =URL+ "/nextbike/getAllRacks.php";
    public static final String URL_GET_AVAILABLE_BIKES_BY_RACKID = URL+"/nextbike/getAvailableBikesByRackID.php";
    public static final String URL_TAKE_BIKE = URL+"/nextbike/takeBike.php";
    public static final String URL_RETURN_BIKE = URL+"/nextbike/returnBike.php";
    public static final String URL_HAS_USER_TAKEN_BIKE = URL+"/nextbike/hasUserTakeBike.php";

    //Keys that will be used to send the request to php scripts
    public static final String KEY_USER_ID = "id";


    //JSON Tags
    public static final String TAG_JSON_ARRAY="result";
    //public static final String TAG_ID = "id";
    //public static final String TAG_NAME = "name";
    //public static final String TAG_SURNAME = "surname";
    //public static final String TAG_USERNAME = "username";
    //public static final String TAG_PASSWORD = "password";

    //user id to pass with intent
    public static final String USER_ID = "user_id";

}
