package ru.biomedis.biotest.sql.DAO;

import android.database.sqlite.SQLiteDatabase;
import ru.biomedis.biotest.sql.GenericDAO;
import ru.biomedis.biotest.sql.entity.Measure;

import java.util.Date;
import java.util.List;

/**
 * Created by Anama on 21.10.2014.
 */
public class MeasureDAO extends GenericDAO<Measure> {
    public MeasureDAO(SQLiteDatabase db) {
        super(db);
    }


    public List<Measure> getMeasuresByIdProfile(int idProfile)
    {
     return this.genericSelect("idProfile=?",new String[]{Integer.toString(idProfile)},null,null,"dt desc",null);
    }

    public List<Measure> getMeasuresByIdProfile(int idProfile,int start,int max)
    {
        return this.genericSelect("idProfile=?",new String[]{Integer.toString(idProfile)},null,null,"dt desc",start+","+max);
    }

    public int getCountMeasureInProfile(int idProfile)
    {
        return this.genericCount("idProfile=?",new String[]{Integer.toString(idProfile)});
    }

    public Date findMaxDate()
    {
        List<Measure> measures = this.genericSelect(new String[]{"dt"}, false, null, null, null, null, "dt desc", "1");
        if(measures!=null)
        {
            if(measures.size()!=0)return measures.get(0).getDt();
            else return null;

        } else return null;

    }
    public Date findMinDate()
    {
        List<Measure> measures = this.genericSelect(new String[]{"dt"}, false, null, null, null, null, "dt asc", "1");
        if(measures!=null)
        {
            if(measures.size()!=0)return measures.get(0).getDt();
            else return null;

        } else return null;

    }
    public Date findMaxDate(int profileID)
    {
        List<Measure> measures = this.genericSelect(new String[]{"dt"}, false, "idProfile=?", new String[]{profileID+""}, null, null, "dt desc", "1");
        if(measures!=null)
        {
            if(measures.size()!=0)return measures.get(0).getDt();
            else return null;

        } else return null;

    }
    public Date findMinDate(int profileID)
    {
        List<Measure> measures = this.genericSelect(new String[]{"dt"}, false, "idProfile=?", new String[]{profileID+""}, null, null, "dt asc", "1");
        if(measures!=null)
        {
            if(measures.size()!=0)return measures.get(0).getDt();
            else return null;

        } else return null;

    }


    public int getIDProfileByMeasure(int idMeasure)
    {
        List<Measure> measures = this.genericSelect(new String[]{"idProfile"}, false, "id=?", new String[]{idMeasure+""}, null, null, "", "1");
        if(measures!=null)
        {
            if(measures.size()!=0)return measures.get(0).getId();
            else return -1;

        } else return -1;
    }




}
