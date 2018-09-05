package in.ac.bkbiet.bkbiet.utils;

import android.content.Context;

import java.util.HashMap;

/**
 * SettingsVariables Created by Ashish on 12/16/2017.
 */

public class Sv {
    public static final String dIS_DEV_ON = "is_dev_on";
    public static final String dSHOW_STUDENT_ACCESS = "show_student_access";
    public static final String dSHOW_TEACHER_ACCESS = "show_teacher_access";
    public static final String dSHOW_AUDITS = "show_audits";
    public static final String dSHOW_DEV_SETTINGS = "show_dev_settings";
    public static final String dSHOW_RECEIPTS = "show_receipts";
    public static final String dSEND_RECEIPTS = "send_receipts";
    public static final String dSHOW_IMAGE_DOWNLOAD_BUTTON = "show_image_download_button";
    public static final String dDOWNLOAD_IMAGES_ON_LONG_PRESS = "download_image_on_long_press";
    public static final String dSAVE_AUDITS = "save_audits";
    public static final String dIS_FIRST_LAUNCH = "is_first_launch";

    public static final String dAD_TYPE_TO_SHOW = "ad_type_to_show";
    public static final String dBERRIES_COUNT = "berries_count";

    public static final String pIS_SAVING_SYLLABUSES = "is_saving_syllabuses";


    public static final String sLOAD_IMAGES = "load_images";
    public static final String sPINNED_SYLLABUS = "pinned_syllabus";
    public static HashMap<String, String> settings = new HashMap<String, String>();

    public static String getSetting(String key, String defaultValue) {
        if (settings.containsKey(key))
            try {
                return String.valueOf(settings.get(key));
            } catch (Exception e) {
                return defaultValue;
            }
        return defaultValue;
    }

    public static boolean getBooleanSetting(String key, boolean defaultValue) {
        if (settings.containsKey(key))
            try {
                return Boolean.parseBoolean(String.valueOf(settings.get(key)));
            } catch (Exception e) {
                return defaultValue;
            }
        return defaultValue;
    }

    public static void setIntSetting(Context context, String key, int value) {
        setSetting(context, key, String.valueOf(value));
    }

    public static int getIntSetting(String key, int defaultValue) {
        if (settings.containsKey(key))
            try {
                return Integer.parseInt(String.valueOf(settings.get(key)));
            } catch (Exception e) {
                return defaultValue;
            }
        return defaultValue;
    }

    public static void setBooleanSetting(Context context, String key, boolean value) {
        setSetting(context, key, String.valueOf(value));
    }

    public static void setSetting(Context context, String key, String value) {
        SQLiteHandler db = new SQLiteHandler(context);
        db.setPref(key, value);
        db.close();
        settings.put(key, value);
    }

    public static boolean refresh(Context context) {
        SQLiteHandler db = new SQLiteHandler(context);
        try {
            settings = db.getAllSettings();
        } catch (Exception ignored) {
            return false;
        }
        db.close();
        return true;
    }
}