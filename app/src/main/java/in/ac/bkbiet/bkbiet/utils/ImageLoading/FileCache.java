package in.ac.bkbiet.bkbiet.utils.ImageLoading;

import android.content.Context;

import java.io.File;

/**
 * Created by Ashish on 12/2/2017.
 * Reference in ImageLoader
 */

public class FileCache {

    private File cacheDir;

    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"Android/data/.scarecrow");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url){
        //Images identified by hashcode. Not a perfect solution.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution
        //String filename = URLEncoder.encode(url);
        return new File(cacheDir, filename);

    }

    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}