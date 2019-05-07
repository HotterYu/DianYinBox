package com.znt.push.db;

import android.os.Environment;
import android.util.Log;

import com.znt.lib.bean.AdPlanInfor;
import com.znt.lib.bean.CurAdPlanInfor;
import com.znt.lib.bean.CurPlanInfor;
import com.znt.lib.bean.CurPlanSubInfor;
import com.znt.lib.bean.LocalMediaInfor;
import com.znt.lib.bean.MediaInfor;
import com.znt.lib.bean.ResponseBean;
import com.znt.lib.utils.SystemUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.List;


public class DBMediaHelper
{

    private String TAG = "DBMediaHelper";

    private DbManager db = null;
    private String dbDir = Environment.getExternalStorageDirectory() + "/DianYinBox/db/";
    private String dbName = "dianyin_box.db";

    private static DBMediaHelper INSTANCE = null;

    public static DBMediaHelper getInstance()
    {
        if(INSTANCE == null)
        {
            synchronized(DBMediaHelper.class) //让线程互斥的进入；注意if语句；
            {
                if(INSTANCE == null)
                    INSTANCE = new DBMediaHelper();
            }
        }
        return INSTANCE;
    }

    public DBMediaHelper()
    {
        if(db == null)
        {
            File f = new File(dbDir);
            if(!f.exists())
            {
                if(f.mkdirs())
                    initDb();
            }
            else
                initDb();
        }

    }

    public void initDb()
    {
        //本地数据的初始化
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName(dbName) //设置数据库名
                // 不设置dbDir时, 默认存储在app的私有目录.
                .setDbDir(new File(dbDir)) // 数据库存储路径
                .setDbVersion(15) //设置数据库版本,每次启动应用时将会检查该版本号,
                // 发现数据库版本低于这里设置的值将进行数据库升级并触发DbUpgradeListener
                .setAllowTransaction(true) //设置是否开启事务,默认为false关闭事务
                .setTableCreateListener(new DbManager.TableCreateListener()
                {
                    @Override
                    public void onTableCreated(DbManager dbManager, TableEntity<?> tableEntity)
                    {
                        Log.d(TAG, "onTableCreated: ");
                    }
                })
                .setDbOpenListener(new DbManager.DbOpenListener()
                {
                    @Override
                    public void onDbOpened(DbManager db)
                    {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                // 设置数据库创建时的Listener
                .setDbUpgradeListener(new DbManager.DbUpgradeListener()
                {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion)
                    {
                        Log.d(TAG, "onUpgrade: ");
                        // TODO: ...
                        /*db.addColumn(...);
                        db.dropTable(...);*/

                        try {
                            db.dropDb();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }); //设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
        // .setDbDir(null);//设置数据库.db文件存放的目录,默认为包名下databases目录下

        db = x.getDb(daoConfig);
    }

    public void deleteDbFile()
    {
        File dbFile = new File(dbDir + dbName);
        if(dbFile.exists())
            dbFile.delete();
    }
    public synchronized void addResponseInfo(ResponseBean bean)
    {
        try
        {
            //db.save(bean);
            db.saveOrUpdate(bean);
            /*String test = getResposeInfo(bean.getKey());
            ResponseBean person = db.selector(ResponseBean.class).where("key", "=", bean.getKey()).findFirst();
            String value = person.getValue();*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public synchronized String getResposeInfo(String key)
    {
        try
        {
            //List<ResponseBean> beans = db.selector(ResponseBean.class).where("key", "=", key).findAll();
            ResponseBean tempBean = db.findById(ResponseBean.class,key);
            if(tempBean != null)
                return tempBean.getValue();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
        //return db.findAll(ResponseBean.class);
    }


    public void addPlanInfor(CurPlanInfor planInfor) throws DbException
    {
        //加载数据
        //db.save(list); // 保存实体类或者实体类的List到数据库
        db.saveOrUpdate(planInfor); // 保存或更新实体类或者实体类的List到数据库，根据id对应的数据是否存在
        //db.saveBindingId(list); // 保存实体类或实体类的List到数据库，如果该类型的id是自动生成的，则保存完后会给id赋值
    }
    public List<CurPlanInfor> getCurPlanInfor() throws DbException
    {
        return db.findAll(CurPlanInfor.class);
    }
    public void deleteCurplan(){
        try {
            db.delete(CurPlanInfor.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCurSubPlanInfors(List<CurPlanSubInfor> subPlanInfos)
    {
        try
        {
            db.saveOrUpdate(subPlanInfos); // 保存或更新实体类或者实体类的List到数据库，根据id对应的数据是否存在
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<CurPlanSubInfor> getCurSubPlanInfors()
    {
        try
        {
            Selector<CurPlanSubInfor> selector = db.selector(CurPlanSubInfor.class);
            selector.orderBy("start_time_sort", false);
            return selector.findAll();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteCurSubPlanInfors(){
        try
        {
            db.delete(CurPlanSubInfor.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*****************************ad plan *****************************/

    public void addAdPlanInfor(AdPlanInfor planInfor)
    {
        try
        {
            //加载数据
            //db.save(list); // 保存实体类或者实体类的List到数据库
            db.saveOrUpdate(planInfor); // 保存或更新实体类或者实体类的List到数据库，根据id对应的数据是否存在
            //db.saveBindingId(list); // 保存实体类或实体类的List到数据库，如果该类型的id是自动生成的，则保存完后会给id赋值
        }
        catch (Exception e)
        {
            Log.d(TAG, "addDatas: ");
        }
    }
    /**
     * 获取当前广告计划
     * @return
     */
    public List<CurAdPlanInfor> getCurAdPlanInfor()
    {
        try
        {
            return db.findAll(CurAdPlanInfor.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除全部广告计划
     */
    public void deleteCurAdPlan()
    {
        try
        {
            db.delete(AdPlanInfor.class);
        }
        catch (Exception e)
        {

        }
    }


    /******************************media ******************************/

    /**
     * 添加歌曲
     * @param medias
     */
    public synchronized void addMedias(List<MediaInfor> medias){
        //加载数据
        try {
            db.save(medias); // 保存实体类或者实体类的List到数据库
        } catch (Exception e) {
            e.printStackTrace();
        }
        //db.saveOrUpdate(medias); // 保存或更新实体类或者实体类的List到数据库，根据id对应的数据是否存在
        //db.saveBindingId(medias); // 保存实体类或实体类的List到数据库，如果该类型的id是自动生成的，则保存完后会给id赋值
    }

    public void addMedia(MediaInfor media){
        //加载数据
        //db.save(list); // 保存实体类或者实体类的List到数据库
        try {
            db.saveOrUpdate(media); // 保存或更新实体类或者实体类的List到数据库，根据id对应的数据是否存在
        } catch (Exception e) {
            e.printStackTrace();
        }
        //db.saveBindingId(list); // 保存实体类或实体类的List到数据库，如果该类型的id是自动生成的，则保存完后会给id赋值
    }

    public synchronized void deleteLargeMedias(long allMediaSize) throws DbException
    {
        if(allMediaSize <= 0)
            return;
        long localStorageSize = SystemUtils.getTotalExternalMemorySize();
        if(allMediaSize > localStorageSize)
        {
            long deleteSize = (allMediaSize - localStorageSize) + 100 * 1024 * 1024;
            List<MediaInfor> allMedias = getAllMedias();
            int allSize = allMedias.size();
            if(allSize > 1)
            {
                for(int i=0;i<allSize - 1;i++)
                {
                    MediaInfor mediaInfor = allMedias.get(i);
                    long fileSize = mediaInfor.getMediaSize();
                    deleteSize = deleteSize - fileSize;
                    if(deleteSize <= 0)
                        break;
                    else
                        db.delete(MediaInfor.class, WhereBuilder.b("MEDIA_URL", "=", mediaInfor.getMediaUrl()));//根据where语句的条件进行删除操作
                }
            }

        }
    }

    /**
     * 删除全部的计划歌曲列表
     */
    public synchronized void deleteAllMedias() throws DbException {
        db.delete(MediaInfor.class);//该方法是删除表中的全部数据
        //db.deleteById(Person.class, 12);//该方法主要是根据表的主键(id)进行单条记录的删除
        //db.delete(Person.class, WhereBuilder.b("age", ">", "20"));//根据where语句的条件进行删除操作
        //List<Person> findAll = db.selector(Person.class).expr("age > 20").findAll();
        //db.delete(findAll);//根据实体bean进行对表里面的一条或多条数据进行删除
    }

    public synchronized List<MediaInfor> getCurPlayMedias(String planId, String scheId, String startTime, String endTime, String week)
    {
        try
        {
            //List<MediaInfor> findAll = db.findAll(MediaInfor.class);
            List<MediaInfor> medias = db.selector(MediaInfor.class).where("plan_id", "=", planId)
                    .and("start_play_time", "=", startTime)
                    .and("end_play_time", "=", endTime)
                    .and("play_week", "=", week)
                    .and("sche_id", "=", scheId)
                    .findAll();

            return medias;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "getData: ");
        }
        return null;
    }

    /**
     * 获取计划中的全部歌曲列表
     * @return
     */
    public List<MediaInfor> getAllMedias()
    {
        try
        {
            if(db == null)
                initDb();
            List<MediaInfor> findAll = db.findAll(MediaInfor.class);
            return findAll;
        }
        catch (DbException e)
        {
            e.printStackTrace();
            Log.d(TAG, "getData: ");
        }
        return null;
    }


    /***************************************************************************/
    public void addLocalMedias(List<LocalMediaInfor> medias)
    {
        try
        {
            //加载数据
            //db.save(list); // 保存实体类或者实体类的List到数据库
            db.saveOrUpdate(medias); // 保存或更新实体类或者实体类的List到数据库，根据id对应的数据是否存在
            //db.saveBindingId(list); // 保存实体类或实体类的List到数据库，如果该类型的id是自动生成的，则保存完后会给id赋值
        }
        catch (DbException e)
        {
            Log.d(TAG, "addDatas: ");
        }
    }

    public void addLocalMedia(LocalMediaInfor localMediaInfor)
    {
        try
        {
            //加载数据
            //db.save(list); // 保存实体类或者实体类的List到数据库
            db.saveOrUpdate(localMediaInfor); // 保存或更新实体类或者实体类的List到数据库，根据id对应的数据是否存在
            //db.saveBindingId(list); // 保存实体类或实体类的List到数据库，如果该类型的id是自动生成的，则保存完后会给id赋值
        }
        catch (DbException e)
        {
            Log.d(TAG, "addDatas: ");
        }
    }

    public void deleteAllLocalMedias()
    {
        try
        {
            db.delete(LocalMediaInfor.class);//该方法是删除表中的全部数据
            //db.deleteById(Person.class, 12);//该方法主要是根据表的主键(id)进行单条记录的删除
            //db.delete(Person.class, WhereBuilder.b("age", ">", "20"));//根据where语句的条件进行删除操作
            //List<Person> findAll = db.selector(Person.class).expr("age > 20").findAll();
            //db.delete(findAll);//根据实体bean进行对表里面的一条或多条数据进行删除
        }
        catch (DbException e)
        {

        }
    }

    public void deleteSongRecordByUrl(String url)
    {
        try
        {
            db.delete(LocalMediaInfor.class, WhereBuilder.b("MEDIA_URL", "=", url));//根据where语句的条件进行删除操作
        }
        catch (DbException e)
        {

        }
    }

    public List<LocalMediaInfor> getAllLocalMedias()
    {
        try
        {
            List<LocalMediaInfor> findAll = db.findAll(LocalMediaInfor.class);
            return findAll;
        }
        catch (DbException e)
        {
            e.printStackTrace();
            Log.d(TAG, "getData: ");
        }
        return null;
    }


    public long minRemainSize = 230;//200M
    //public long minRemainSize = 52 * 1024;//52G
    public void checkAndReleaseSpace(long desFileSize)
    {
        desFileSize = desFileSize/(1024*1024);
        try
        {
            long localRemainSize = SystemUtils.getAvailableExternalMemorySize()/(1024*1024);
            if(minRemainSize == 0 || localRemainSize <= minRemainSize || (localRemainSize - minRemainSize) < desFileSize)
            {
                long deleteSize = 0;
                if(desFileSize == 0)
                    deleteSize = minRemainSize - localRemainSize;
                else
                    deleteSize = desFileSize - (localRemainSize - minRemainSize);
                deleteLastSongRecordBySize(deleteSize);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void deleteLastSongRecordBySize(long deleteSize)
    {
        try
        {
            long deletedSize = 0;
            Selector<LocalMediaInfor> selector = db.selector(LocalMediaInfor.class);
            selector.orderBy("MODIFY_TIME", false);
            //selector.limit(20);
            List<LocalMediaInfor> medias = selector.findAll();
            //如果要使用条件查询的话可以使用
            //Selector<LocalMediaInfor> selector = db.selector(LocalMediaInfor.class).where("id","=",id).and("age",">",10);
            int count = medias.size();
            if(count > 0 && deleteSize > 0)
            {
                for(int i=0;i<count;i++)
                {
                    LocalMediaInfor tempInfo = medias.get(i);
                    String path = tempInfo.getMediaUrl();

                    File file = new File(path);
                    if(file.exists())
                    {
                        deletedSize += file.length()/(1024*1024);
                        if(file.delete())
                            deleteSongRecordByUrl(file.getAbsolutePath());
                        if(deletedSize >= deleteSize)
                        {
                            Log.e("", "release space==>" + deletedSize / 1024 / 1024);
                            break;
                        }

                    }
                    else
                        deleteSongRecordByUrl(file.getAbsolutePath());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void deleteLastSongRecordByCount(int count)
    {
        try
        {
            Selector<LocalMediaInfor> selector = db.selector(LocalMediaInfor.class);
            selector.orderBy("MODIFY_TIME", false);
            selector.limit(count);
            List<LocalMediaInfor> medias = selector.findAll();
            //如果要使用条件查询的话可以使用
            //Selector<LocalMediaInfor> selector = db.selector(LocalMediaInfor.class).where("id","=",id).and("age",">",10);
            int tempSize = medias.size();
            for(int i=0;i<tempSize;i++)
            {
                LocalMediaInfor tempInfo = medias.get(i);
                String url = tempInfo.getMediaUrl();
                File file = new File(url);
                if(file.exists())
                {
                    if(file.delete())
                        deleteSongRecordByUrl(file.getAbsolutePath());
                }
                else
                    deleteSongRecordByUrl(file.getAbsolutePath());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 鑾峰彇鏈湴璁″垝鐨勭涓�涓紑濮嬫椂闂�
     * @return
     */
    /*public synchronized long getFirstPlanTime()
    {
        long time = 0;
        Cursor cur = queryNormal(TBL_CUR_PLAN_LIST);
        if(cur == null || cur.getCount() == 0)
            return time;

        if(cur != null && cur.getCount() > 0)
        {
            while(cur.moveToLast())
            {
                String startTime = cur.getString(cur.getColumnIndex("startTime"));
                if(!TextUtils.isEmpty(startTime))
                {//"yyyy-MM-dd HH:mm:ss"
                    startTime = "2018-10-1 " + startTime;
                    //time = DateUtils.timeToInt(startTime, ":") ;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        time = formatter.parse(startTime).getTime() + 2 * 60 * 1000;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
                //String endTime = cur.getString(cur.getColumnIndex("endTime"));
            }
        }
        if(cur != null )
            cur.close();
        return time;
    }*/

}
