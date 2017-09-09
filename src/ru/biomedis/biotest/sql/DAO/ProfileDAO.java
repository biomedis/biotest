package ru.biomedis.biotest.sql.DAO;

import android.database.sqlite.SQLiteDatabase;
import ru.biomedis.biotest.sql.GenericDAO;
import ru.biomedis.biotest.sql.entity.Profile;

import java.util.List;

/**
 * Created by Anama on 20.10.2014.
 */
public class ProfileDAO extends GenericDAO<Profile> {

    public ProfileDAO(SQLiteDatabase db) {
        super(db);
    }


    public List<Profile> getProfilesList()
    {
        return this.findAll();
    }

    public Profile getProfileById(int id)
    {
        return this.findById(id);
    }

    /**
     * Получить активный профиль
     * @return
     */
    public Profile getActiveProfile()
    {
        List<Profile> profiles = this.genericSelect("activeProfile=?", new String[]{"1"}, null, null, null, "1");
        if(profiles==null) return null;
        if(profiles.size()>0) return profiles.get(0);
        else return null;

    }



}
