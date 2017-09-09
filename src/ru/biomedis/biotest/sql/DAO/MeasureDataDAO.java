package ru.biomedis.biotest.sql.DAO;

import android.database.sqlite.SQLiteDatabase;
import ru.biomedis.biotest.sql.GenericDAO;
import ru.biomedis.biotest.sql.entity.Measure;
import ru.biomedis.biotest.sql.entity.MeasureData;

import java.util.List;

/**
 * Created by Anama on 21.10.2014.
 */
public class MeasureDataDAO extends GenericDAO<MeasureData> {
    public MeasureDataDAO(SQLiteDatabase db) {
        super(db);
    }

    /**
     * Получить данные измерений по ID измерения.
     * Иссключая поле RawData. Его нужно отдельно получать
     * @param measureId
     * @return
     */
    public MeasureData getDataByMeasure(Integer measureId)
    {
       List<MeasureData> lst= this.genericSelect(new String[]{"rawData"}, true, "idMeasure=?", new String[]{Integer.toString(measureId)}, null, null, null, "1");
        if(lst!=null) return lst.get(0);
        else return null;
    }

    /**
     * Возвратит RAWDATA
     * @param measureId
     * @return
     */
    public byte[] getRawData(Integer measureId)
    {
        List<MeasureData> datas = this.genericSelect(new String[]{"rawData"}, false, "idMeasure=?", new String[]{Integer.toString(measureId)}, null, null, null, "1");
        if(datas!=null) return datas.get(0).getRawData();
        else return null;
    }




}
