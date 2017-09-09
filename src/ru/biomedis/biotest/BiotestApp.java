package ru.biomedis.biotest;

import android.app.Application;
import ru.biomedis.biotest.sql.DBHelper;
import ru.biomedis.biotest.sql.ModelDataApp;
import ru.biomedis.biotest.util.FileUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anama on 20.10.2014.
 */
public class BiotestApp extends Application
{



    /**
     * Инкрементируется при изменении структуры базы, если она требует скрипта обновления.
     */
    private static int BD_VERSION=9;
    private static String BD_NAME="biotest2db.db";
    public static final String dirData="BiomedisPulse";//папка на карте памяти для сохранения файлов

    private AppOptions appOptions;
    private DBHelper dbHelper;
    private ModelDataApp modelDataApp=null;

    public ModelDataApp getModelDataApp() {
        return modelDataApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        closeDB();
    }


    /**
     * Здесь прописываетс инициализация контекста.  initDB() тут пожизненно
     */
    private void initContext()
    {
        appOptions=AppOptions.instance(this);
        initDB();

        FileUtil.createDir(appOptions.getString("fileDirApp").getValue());

    }

    private void initDB()
    {
        this.dbHelper=new DBHelper(this,BD_VERSION,BD_NAME);
        this.dbHelper.open();
        this.modelDataApp=this.dbHelper.getMda();
    }


    public void closeDB(){dbHelper.close();}


    public AppOptions getMainOptions(){return appOptions;}

}
