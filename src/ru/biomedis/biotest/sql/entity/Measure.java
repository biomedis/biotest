package ru.biomedis.biotest.sql.entity;

        import ru.biomedis.biotest.sql.annotations.TableField;
        import ru.biomedis.biotest.sql.annotations.Id;
        import ru.biomedis.biotest.sql.annotations.Table;

        import java.util.Date;

/**
 * Классы сущностей БД
 * Created by Anama on 20.10.2014.
 */
@Table()
public class Measure implements IEntity
{
    @Id()    private Integer id;
    @TableField(columnDefinition = "NOT NULL",foreignKey = true) private Integer idProfile;
    @TableField() private String comment;
    @TableField() private Date dt;



    public Measure()  {
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdProfile(Integer idProfile) {
        this.idProfile = idProfile;
    }

    public int getIdProfile() {
        return idProfile;
    }



    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }


}
