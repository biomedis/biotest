package ru.biomedis.biotest.sql.entity;

import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.sql.annotations.Id;
import ru.biomedis.biotest.sql.annotations.Table;
import ru.biomedis.biotest.sql.annotations.TableField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Классы сущностей БД
 * Created by Anama on 20.10.2014.
 */
@Table()
public class MeasureData implements IEntity
{
    @Id()    private Integer id;
    @TableField(foreignKey = true)  private Integer idMeasure;
    @TableField() private byte[] rawData;
    @TableField() private List<RawDataProcessor.Point<Integer>> rr;
    @TableField() private ArrayList<Double> spectrArray;
    @TableField() private Integer NI;
    @TableField() private Integer SI;
    @TableField() private Integer BAK;
    @TableField() private Integer BE;
    @TableField() private Double TP;
    @TableField() private Double HF;
    @TableField() private Double LF;
    @TableField() private Double VLF;
    @TableField() private Integer HR;//частота сердечных сокращений
    @TableField(foreignKey = true) private Integer idProfile;
    @TableField() private Date dt;


    public MeasureData()
    {
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<RawDataProcessor.Point<Integer>> getRr() {
        return rr;
    }

    public void setRr(List<RawDataProcessor.Point<Integer>> rr) {
        this.rr = rr;
    }


    public Integer getIdMeasure() {
        return idMeasure;
    }

    public void setIdMeasure(Integer idMeasure) {
        this.idMeasure = idMeasure;
    }

    /**
     * Сырые данные с датчика. Это байтовый массив.
     * @return
     */
    public byte[] getRawData()
    {
        return rawData;
    }

    public void setRawData(byte[] rawData)
    {
        this.rawData = rawData;
    }

    public Integer getNI() {
        return NI;
    }

    /**
     * Индекс напряженности
     * @param NI
     */
    public void setNI(Integer NI) {
        this.NI = NI;
    }

    /**
     * Стресс индекс
     * @return
     */

    public Integer getSI() {
        return SI;
    }

    public void setSI(Integer SI) {
        this.SI = SI;
    }

    public Integer getBAK() {
        return BAK;
    }

    public void setBAK(Integer BAK) {
        this.BAK = BAK;
    }

    public Integer getBE() {
        return BE;
    }

    public void setBE(Integer BE) {
        this.BE = BE;
    }

    public Double getTP() {
        return TP;
    }

    public void setTP(Double TP) {
        this.TP = TP;
    }

    public Double getHF() {
        return HF;
    }

    public void setHF(Double HF) {
        this.HF = HF;
    }

    public Double getLF() {
        return LF;
    }

    public void setLF(Double LF) {
        this.LF = LF;
    }

    public Double getVLF() {
        return VLF;
    }

    public void setVLF(Double VLF) {
        this.VLF = VLF;
    }

    public Integer getHR() {
        return HR;
    }

    public void setHR(Integer HR) {
        this.HR = HR;
    }

    public ArrayList<Double> getSpectrArray()
    {
        return spectrArray;
    }

    public void setSpectrArray(ArrayList<Double> spectrArray)
    {
        this.spectrArray = spectrArray;
    }

    public Date getDt()
    {
        return dt;
    }

    public void setDt(Date dt)
    {
        this.dt = dt;
    }

    public Integer getIdProfile()
    {
        return idProfile;
    }

    public void setIdProfile(Integer idProfile)
    {
        this.idProfile = idProfile;
    }
}
