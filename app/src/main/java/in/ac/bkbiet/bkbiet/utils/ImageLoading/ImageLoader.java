package in.ac.bkbiet.bkbiet.utils.ImageLoading;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.ac.bkbiet.bkbiet.utils.Sv;
import in.ac.bkbiet.bkbiet.utils.Uv;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static in.ac.bkbiet.bkbiet.R.drawable.ic_profile;

/**
 * ImageLoader Created by Ashish on 12/2/2017.
 * Reference : http://www.technotalkative.com/android-load-images-from-web-and-caching/
 */

@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
public class ImageLoader {
    public static final int QUALITY_LOW = 70;
    public static final int QUALITY_MEDIUM = 200;
    public static final int QUALITY_HIGH = 600;
    public static final int QUALITY_MAX = 1600;
    private static boolean setTextBitmap = true;
    private final int default_stub_id = ic_profile;
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private ExecutorService executorService;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    public static String getInitialsFormString(String text) {
        text = getOptimisedName(text);
        if (text != null && text.length() > 0) {
            StringBuilder initials = new StringBuilder(text.charAt(0) + "");
            for (int i = 1; i < text.length(); i++) {
                if (text.charAt(i - 1) == ' ')
                    initials.append(text.charAt(i));
            }
            initials = new StringBuilder(initials.toString().trim());
            return initials.toString();
        } else {
            return "";
        }
    }

    private static String getOptimisedName(String name) {
        if (name != null) {
            String optimisedName = name.replace("Mr.", "");
            optimisedName = optimisedName.replace("Mrs.", "");
            optimisedName = optimisedName.replace("Ms.", "");
            return optimisedName;
        } else {
            return "BKBIET";
        }
    }

    public static Bitmap getBitmapFromString(String text, float textSize, int textColor) {
        return getBitmapFromString(text, textSize, textColor, Color.WHITE);
    }

    public static Bitmap getBitmapFromString(String text, float textSize, int textColor, int color) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent() + 200f; // ascent() is negative
        int width = (int) (paint.measureText(text) + 200f); // round
        int height = (int) (baseline + paint.descent() + 200f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(color);
        canvas.drawText(text, width / 2 - paint.measureText(text) / 2, baseline, paint);
        return image;
    }

    public void displayImage(String url, ImageView imageView) {
        displayImage(url, imageView, QUALITY_MEDIUM);
    }

    public void displayImage(String url, ImageView imageView, String name) {
        displayImage(url, imageView, name, QUALITY_MEDIUM);
    }

    public void displayImage(String url, ImageView imageView, int imageQuality) {
        displayImage(url, imageView, Uv.currUser.getName(), imageQuality);
    }

    public void displayImage(String url, ImageView imageView, String name, int imageQuality) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            if (Sv.getBooleanSetting(Sv.sLOAD_IMAGES, true)) {
                queuePhoto(url, imageView, getInitialsFormString(name), imageQuality);
            }
            if (setTextBitmap) {
                imageView.setImageBitmap(getBitmapFromString(getInitialsFormString(name), 200, Color.argb(255, 200, 0, 0)));
            } else {
                imageView.setImageResource(default_stub_id);
            }
        }
    }

    private void queuePhoto(String url, ImageView imageView, String defaultImageText, int bitmapQuality) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p, defaultImageText, bitmapQuality));
    }

    public void saveImage(Context context, String url, String name, String filepath, int imageQuality) {
        final Bitmap finalBitmap = getBitmap(url, imageQuality);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + filepath);
        boolean mkdirs = myDir.mkdirs();
        String filename = name + ".jpg";
        File file = new File(myDir, filename);
        if (file.exists()) {
            boolean delete = file.delete();
        }
        Log.i("LOAD", root + filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (finalBitmap != null) {
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Toast.makeText(context, "Saved image '" + filename + "' to " + filepath, Toast.LENGTH_LONG).show();
            } else {
                getBitmapFromString(getInitialsFormString(name), 200, Color.argb(255, 200, 0, 0)).compress(Bitmap.CompressFormat.JPEG, 100, out);
                Toast.makeText(context, "No image found, saved text image '" + filename + "' to " + filepath, Toast.LENGTH_LONG).show();
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmap(String url, int bitmapQuality) {
        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f, bitmapQuality);
        if (b != null)
            return b;

        //from web
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            ImageLoaderUtils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f, bitmapQuality);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f, int bitmapQuality) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = (bitmapQuality > 30 && bitmapQuality <= 2000) ? bitmapQuality : QUALITY_MEDIUM;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                //Log.i("ImageLoader", width_tmp + "--" + height_tmp + "=scale=" + scale);
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException ignored) {
        }
        return null;
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        String defaultImageText;
        int bitmapQuality;

        PhotosLoader(PhotoToLoad photoToLoad, String defaultImageText, int bitmapQuality) {
            this.photoToLoad = photoToLoad;
            this.defaultImageText = defaultImageText;
            this.bitmapQuality = bitmapQuality;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url, bitmapQuality);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad, defaultImageText);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        String defaultImageText = "BKBiet";

        public BitmapDisplayer(Bitmap b, PhotoToLoad p, String defaultImageText) {
            bitmap = b;
            photoToLoad = p;
            this.defaultImageText = defaultImageText;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else {
                if (setTextBitmap) {
                    photoToLoad.imageView.setImageBitmap(ImageLoader.getBitmapFromString(defaultImageText, 200, Color.argb(255, 200, 0, 0)));
                } else {
                    photoToLoad.imageView.setImageResource(default_stub_id);
                }
            }
        }
    }
}