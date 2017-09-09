package ru.biomedis.biotest.sql;

import android.database.sqlite.SQLiteDatabase;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.sql.DAO.MeasureDAO;
import ru.biomedis.biotest.sql.DAO.MeasureDataDAO;
import ru.biomedis.biotest.sql.DAO.ProfileDAO;
import ru.biomedis.biotest.sql.entity.Measure;
import ru.biomedis.biotest.sql.entity.MeasureData;
import ru.biomedis.biotest.sql.entity.Profile;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Инициализация до использования. Поэтому еее вставили в OnCreate и onOpen в DBHelper
 * Created by Anama on 21.10.2014.
 */
public class ModelDataApp implements IModelDataApp
{
    //Здесь необходимо добавлять DAO
 private ProfileDAO profileDAO=new ProfileDAO(null);
 private MeasureDAO measureDAO=new MeasureDAO(null);
 private MeasureDataDAO measureDataDAO=new MeasureDataDAO(null);

@Override
public void initDAO(SQLiteDatabase db)
{
    //Здесь од инициализации DAO
    profileDAO.setDb(db);
    measureDAO.setDb(db);
    measureDataDAO.setDb(db);

}
    @Override
 public void createTables()
 {

     profileDAO.createTable();
     measureDAO.createTable();
     measureDataDAO.createTable();
 }

    @Override
    public void deleteTables() {

        profileDAO.deleteTable();
        measureDAO.deleteTable();
        measureDataDAO.deleteTable();
    }

    public ModelDataApp() {

    }


    ////////   Здесь касттомный код модели данных, опирается на DAO. Код логики программы опирается на него(можно легко менять внутреннее представление данных)  /////

    /**
     * Создает профили. Первый созданный профиль станет активным
     * @param name
     * @param comment
     * @return
     */
    public Profile createProfile(String name,String comment){

        int i = this.profileDAO.countAll();

        Profile profile=new Profile();
        profile.setName(name);
        profile.setComment(comment);
        if(i==0)profile.setActiveProfile(true); else profile.setActiveProfile(false);

        int id= this.profileDAO.insert(profile);
        if(id>0) {profile.setId(id);return profile;}
        else return null;


    }

    public int removeProfile(Profile profile){ return removeProfile(profile.getId());}

    public int removeProfile(int id)
    {
/*
        List<Measure> measuresByIdProfile = this.measureDAO.getMeasuresByIdProfile(id);

        int res=-1;

        //удаление зависимостей
        this.measureDataDAO.beginTransaction();
        try
        {


            for (Measure measure : measuresByIdProfile) {

                this.measureDataDAO.genericRemove("idMeasure=?",new String[]{measure.getId().toString()},false);//удалим данные
                this.measureDAO.remove(measure,false);//  удалим измерение
            }

            res= this.profileDAO.remove(id,false);//удалим профиль

            this.measureDataDAO.setTransactionSuccessful();

        }catch (Exception e)
        {
            Log.v("Откат удаления профиля");
            e.printStackTrace();
        res=-1;
        }finally {
            this.measureDataDAO.endTransaction();
        }

        return res;
*/
        int res=-1;
        this.measureDataDAO.beginTransaction();
        try
        {
        //удалим данные измерений, измерения и сам профиль.

            this.measureDataDAO.genericRemove("idProfile=?",new String[]{id+""},false);
            this.measureDAO.genericRemove("idProfile=?",new String[]{id+""},false);
            res= this.profileDAO.remove(id,false);//удалим профиль

            this.measureDataDAO.setTransactionSuccessful();
        }catch (Exception e)
        {
            Log.v("Откат удаления профиля");
            e.printStackTrace();
            res=-1;
        }finally {
            this.measureDataDAO.endTransaction();
        }
        return res;
    }

    public List<Profile> getListProfiles(){return this.profileDAO.getProfilesList();}

    public void updateProfile(Profile profile) throws Exception {this.profileDAO.safelySave(profile);}

    public Profile getProfileById(int id){return this.profileDAO.getProfileById(id);}

     public Profile getActiveProfile(){return this.profileDAO.getActiveProfile();}

    public boolean setActiveProfile(Profile p)
    {
        p.setActiveProfile(true);
        Profile activeProfile = this.profileDAO.getActiveProfile();
        if(activeProfile!=null)
        {
            activeProfile.setActiveProfile(false);
            this.profileDAO.update(activeProfile,new String[]{"activeProfile"},true);
        }
        return this.profileDAO.update(p,new String[]{"activeProfile"},true);
    }

    public int getCountProfiles(){return this.profileDAO.countAll();}






    public Measure createMeasure(Profile profile,String comment,Date dt){
        return createMeasure(profile.getId(),comment,dt);
    }
    public Measure createMeasure(int idProfile,String comment,Date dt){
        Measure m=new Measure();
        m.setDt(dt);
        m.setComment(comment);
        m.setIdProfile(idProfile);

        int id= this.measureDAO.insert(m);
        if(id>0) {m.setId(id);return m;}
        else return null;
    }

    public int removeMeasure(Measure measure){

      return removeMeasure(measure.getId());



    }
    public int removeMeasure(Integer id){
        int res=-1;

        //удаление зависимостей
        this.measureDAO.beginTransaction();
        try
        {




                this.measureDataDAO.genericRemove("idMeasure=?",new String[]{id.toString()},false);//удалим данные
            res =   this.measureDAO.remove(id,false);//  удалим измерение




            this.measureDAO.setTransactionSuccessful();

        }catch (Exception e)
        {
            Log.v("Откат удаления измерения");
            e.printStackTrace();
            res=-1;
        }finally {
            this.measureDAO.endTransaction();
        }

        return res;
    }
    public List<Measure> getListMeasures(Profile profile){return getListMeasures(profile.getId()); }
    public List<Measure> getListMeasures(int profileId){return this.measureDAO.getMeasuresByIdProfile(profileId);}
    public void updateMeasure(Measure measure) throws Exception { this.measureDAO.safelySave(measure);}

public Measure getMeasure(int id)
{
    return this.measureDAO.findById(id);
}


    public int countMeasure(int profileID)
    {
        return this.measureDAO.getCountMeasureInProfile(profileID) ;  }


    public MeasureData createMeasureData(Measure measure,RawDataProcessor rdp)
    {
        return this.createMeasureData(measure.getId(),rdp);
    }

    /**
     * Создает данные измерения.
     * @param idMeasure
     * @param rdp данные с расчетами.
     * @return
     */
    public MeasureData createMeasureData(int idMeasure,RawDataProcessor rdp)
    {

       // int profileID = this.measureDAO.getIDProfileByMeasure(idMeasure);
        Measure measure = this.measureDAO.findById(idMeasure);

        if(measure==null) return null;

        MeasureData md=new MeasureData();
        md.setIdMeasure(idMeasure);
        md.setRr(rdp.getRR());
        md.setBAK(rdp.getBAK());
        md.setNI(rdp.getIN());
        md.setSI(rdp.getIS());
        md.setBE(rdp.getBE());
        md.setHF(rdp.getSpectrum().getHF());
        md.setLF(rdp.getSpectrum().getLF());
        md.setVLF(rdp.getSpectrum().getVLF());
        md.setTP(rdp.getSpectrum().getTP());
        md.setHR(rdp.getHR());
        md.setSpectrArray(rdp.getSpectrum().getSpector());
        md.setIdProfile(measure.getIdProfile());
        md.setDt(measure.getDt());

        byte[]arr=new byte[rdp.getRawData().size()];
        int i=0;
        for (Short itm : rdp.getRawData()) {
            arr[i++]=itm.byteValue();
        }

        md.setRawData(arr);



        int id= this.measureDataDAO.insert(md);
        if(id>0) {md.setId(id);return md;}
        else return null;

    }

    public MeasureData getMeasureData(Measure measure){return getMeasureData(measure.getId());}
    public MeasureData getMeasureData(int measureId){return this.measureDataDAO.getDataByMeasure(measureId);}
    public void saveMeasureData(MeasureData data) throws Exception {this.measureDataDAO.safelySave(data);}

    public byte[] getRawData(int measureID){return this.measureDataDAO.getRawData(measureID);}

    /**
     * Обновление указанных полей сущности. Названия полей берется из названия методов setComment -> поле comment. Если задать неверное имя программа грохнется с RuntimeException
     * @param data Сущность для обновления
     * @param fieldsToUpdate поля который обновить
     * @return true если все норм
     */
    public boolean updateMeasureDataFields(MeasureData data,String[] fieldsToUpdate){
      return  this.measureDataDAO.update(data,fieldsToUpdate,true);

    }


    public Date findMaxDate()
    {
        return this.measureDAO.findMaxDate();
    }
    public Date findMinDate()
    {
        return this.measureDAO.findMinDate();
    }
    public Date findMaxDate(int profileID)
    {
        return this.measureDAO.findMaxDate(profileID);
    }
    public Date findMinDate(int profileID)
    {
        return this.measureDAO.findMinDate(profileID);
    }

    /**
     * Возвратит список результатов измерений в интервале дат. Там будет отсутствовать RR и спектр, тк занимает много места.
     * @param profileId
     * @param min
     * @param max
     * @return
     */
    public List<MeasureData> findDataByDateFilter(int profileId,Date min,Date max)
    {
        //нужно доделать функцию generic полезная очень
        //здесь попробывать придумать чтото-типа сырого запроса чтобы провести многотабличный запрос, ервым извлеч измерения а потом их использовать для извлечения данных




       // List<Measure> measures = this.measureDAO.genericSelect("(dt>=? and dt<=?) and idProfile=?",new String[]{min.getTime()+"",max.getTime()+"",profileId+""},null,null,"dt",null);

       // TODO: нужно удостовериться что данные из Data извлекаются именно в порядке дат а не в разброс. Как вариант можно их сортировать по ID тк даты идут в любом случае подряд
        //return this.measureDataDAO.genericSelect(new String[]{"rr","spectrArray"},true,"idMeasure IN (select id from Measure where (dt>=? and dt<=?) and idProfile=? order by id)",new String[]{min.getTime()+"",max.getTime()+"",profileId+""},null,null,null,null);
        return this.measureDataDAO.genericSelect(new String[]{"rr","spectrArray"},true,"(dt>=? and dt<=?) and idProfile=? order by id",new String[]{min.getTime()+"",max.getTime()+"",profileId+""},null,null,null,null);

    }




}
