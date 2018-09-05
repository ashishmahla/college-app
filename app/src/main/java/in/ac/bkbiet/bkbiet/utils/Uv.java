package in.ac.bkbiet.bkbiet.utils;

import in.ac.bkbiet.bkbiet.models.User;

/**
 * Universal Variable Created by Ashish on 8/31/2017.
 */

@SuppressWarnings("unused")
public class Uv {
    public static final String ACTION_NOTY_COUNT_BROADCAST = "noty_count_broadcast";
    public static final String NOTIFICATION_DATE_FORMAT = "MMMM dd, yyyy hh:mm";
    public static final int DEFAULT_NOTIFICATION_ID = 242311;
    public static final int NEW_CHAT_NOTY_ID = 245623;

    public static final String sADMOB_ID = "ca-app-pub-9780748488703696~5991686808";
    public static final String sADMOB_DEV_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String sADMOB_BANNER_AD_ID = "ca-app-pub-9780748488703696/8594509856";
    public static final String sADMOB_DEV_BANNER_AD_ID = "ca-app-pub-3940256099942544/5224354917";
    //Login SignUp Configurations
    public static final String sURL_LOGIN = "http://sunilmedicose.com/ashish/login.php";
    public static final String sURL_SIGNUP = "http://sunilmedicose.com/ashish/register.php";
    public static final String sURL_EXTRA_FUNCTIONS = "http://sunilmedicose.com/ashish/extras_with_function_name.php";
    public static final String pIS_DEV_ON = "is_dev_on";
    public static String currChatWith = "none";
    public static boolean isDevOn = false;

    public static boolean isUpdateLinkAvailable = false;
    public static String updateLink = "no-link-available";
    public static boolean isNewVersionAvailable = false;
    public static String updateMsg = "New version available.";

    public static User currUser = new User();

    //public static boolean isStudentPresent = false;
    //public static Student currentStudent=new Student();
    public static boolean isLoggedIn = false;

    public static boolean hasClearanceLevel(int requiredClearance) {
        return (requiredClearance <= currUser.getType() || isDevOn);
    }
}