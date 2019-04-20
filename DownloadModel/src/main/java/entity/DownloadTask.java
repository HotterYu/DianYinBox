package entity;

import android.os.AsyncTask;
import android.util.Log;

import com.znt.download.IDownloadListener;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.utils.CacheDirUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String,Integer,Integer>
{
    public static final int TYPE_SUCCESS=0;

    public static final int TYPE_FAILED=1;

    public static final int TYPE_PAUSED=2;

    public static final int TYPE_CANCELED=3;
    
    public static final int TYPE_REMOVE=4;

    private DownloadListener listener;

    private boolean isCanceled=false;

    private boolean isPaused=false;

    private int lastProgress;



    private String endTag = ".temp";
    
    private String removeFileUrl = "";
    
    private IDownloadListener mIDownloadListener = null;

    public DownloadTask(DownloadListener listener, IDownloadListener mIDownloadListener) 
    {
        this.listener = listener;
        this.mIDownloadListener = mIDownloadListener;
    }

    @Override
    protected Integer doInBackground(String... params) 
    {
        InputStream is=null;
        RandomAccessFile savedFile = null;
        File file = null;
        long downloadLength = 0;
        String downloadUrl = params[0];

        if(CacheDirUtils.getINSTANCE().getDownLoadDir() == null)
        {
            return TYPE_FAILED;
        }

        //downloadUrl = UrlUtil.decode(downloadUrl, "UTF-8");
        try
        {
            downloadUrl = URLDecoder.decode(downloadUrl, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return TYPE_FAILED;
        }
        if(downloadUrl == null)
        	return TYPE_FAILED;
        int lastIndex = downloadUrl.lastIndexOf("/");
        if(lastIndex < 0)
        	return TYPE_FAILED;
        String fileName = downloadUrl.substring(lastIndex);

        
        file = new File(CacheDirUtils.getINSTANCE().getDownLoadDirOld().getAbsolutePath()+fileName);
        if(file.exists())
        {
            return TYPE_SUCCESS;
        }
        file = new File(CacheDirUtils.getINSTANCE().getDownLoadDir().getAbsolutePath()+fileName);
        if(file.exists())
        {
        	return TYPE_SUCCESS;
        }

        File tempFile = new File(CacheDirUtils.getINSTANCE().getDownLoadDir().getAbsolutePath() + fileName + ".temp");
        if(tempFile.exists())
        	downloadLength = tempFile.length();
        

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("RANGE","bytes=" + downloadLength+"-")  //
                .url(downloadUrl)
                .build();
        try 
        {
            Response response=client.newCall(request).execute();

            if(response!=null)
            {
                long contentLength = response.body().contentLength();
                if(contentLength <= 0)
                {
                    return TYPE_FAILED;
                }
                if(contentLength > 1024*1024*650)
                {
                    removeFileUrl = downloadUrl;
                    Log.e("doInBackground", "remove lage size -->"+ contentLength + "   " + downloadUrl);
                    return TYPE_REMOVE;
                }

                if(mIDownloadListener != null)
                    mIDownloadListener.onDownloadSpaceCheck(contentLength);

                is = response.body().byteStream();
                
                savedFile = new RandomAccessFile(tempFile,"rw");
                savedFile.seek(downloadLength);
                byte[] b = new byte[1024 * 8];
                int len;
                while((len = is.read(b)) != -1)
                {
                    if(isCanceled)
                    {
                        return TYPE_CANCELED;
                    }
                    else if(isPaused)
                    {
                        return TYPE_PAUSED;
                    }
                    else 
                    {
                        savedFile.write(b,0,len);
                    }
                }
                
                response.body().close();
                
                if(tempFile.exists() && getTempFile(file).length() == contentLength)
                {

                	File newFile = renameFile(tempFile);
                	MediaInfor infor = new MediaInfor();
					infor.setMediaUrl(newFile.getAbsolutePath());
					infor.setMediaName(getNameFromPath(newFile.getAbsolutePath()));

					if(mIDownloadListener != null)
			        	mIDownloadListener.onDownloadRecordInsert(infor, newFile.lastModified());
                }
                else
                {
                	tempFile.delete();
                	return TYPE_FAILED;
                }
                
                return TYPE_SUCCESS;
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return TYPE_FAILED;
        }
        finally 
        {
            try
            {
                if(is != null)
                {
                    is.close();
                }
                if(savedFile != null)
                {
                    savedFile.close();
                }
                if(isCanceled && file != null)
                {
                    file.delete();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }
    
    private String getNameFromPath(String path)
	{
		String name = "";
		if(path.contains("/"))
			name = path.substring(path.lastIndexOf("/"));
		
		return name;
	}


    protected void onProgressUpdate(Integer...values){
        int progress=values[0];
        if(progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }


    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            case TYPE_REMOVE:
                listener.onRemoveLargeSize(removeFileUrl);
                break;
            default:
                break;
        }
    }
    
    public void  pauseDownload(){
        isPaused=true;
    }

    public void cancelDownload(){
        isCanceled=true;
    }


    private long getContentLength(String downloadUrl)
    {
    	if(downloadUrl.contains("`"))
    		downloadUrl = downloadUrl.replace("`", "");
        OkHttpClient client = new OkHttpClient();
        try
        {
            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = client.newCall(request).execute();
            if(response !=null && response.isSuccessful())
            {
                long contentLength=response.body().contentLength();
                response.body().close();
                return contentLength;
            
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return  0;
    }

    private File getTempFile(File file)
    {
    	String filePath = file.getAbsolutePath();
    	if(!filePath.endsWith(endTag))
    		filePath = filePath + endTag;
    	return new File(filePath);
    }
    
    private File renameFile(File file)
    {
    	String filePath = file.getAbsolutePath();
    	if(filePath.endsWith(endTag))
    		filePath = filePath.replace(endTag, "");
    	File newFile = new File(filePath);
    	file.renameTo(newFile);
    	
    	return newFile;
    }
}
