package com.znt.lib.utils;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;

public class CacheDirUtils implements Serializable {

    private File downLoadDir = null;

    public static CacheDirUtils INSTANCE = null;

    public static CacheDirUtils getINSTANCE()
    {
        if(INSTANCE == null)
        {
            synchronized(CacheDirUtils.class) //让线程互斥的进入；注意if语句；
            {
                if(INSTANCE == null)
                    INSTANCE = new CacheDirUtils();
            }
        }
        return INSTANCE;
    }

    public CacheDirUtils()
    {
        init();
    }

    private void init()
    {
        downLoadDir = new File(Environment.getExternalStorageDirectory() + "/DianYinBox/download");
        if(!downLoadDir.exists())
            downLoadDir.mkdirs();
    }

    public File getDownLoadDir()
    {
        if(downLoadDir != null && downLoadDir.exists())
            return downLoadDir;
        else
            init();
        if(downLoadDir != null && downLoadDir.exists())
            return downLoadDir;
        return null;
    }

    public File getDownLoadDirOld()
    {
        return Environment.getExternalStorageDirectory();
    }


}
