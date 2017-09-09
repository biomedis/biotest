package ru.biomedis.biotest.sql.entity;

import ru.biomedis.biotest.sql.annotations.Id;
import ru.biomedis.biotest.sql.annotations.Table;
import ru.biomedis.biotest.sql.annotations.TableField;


/**
 * Created by Anama on 20.10.2014.
 */
@Table()
public class Profile  implements IEntity
{


    @Id() private Integer id;
    @TableField() private String name;
    @TableField()  private String comment;
    @TableField(columnDefinition = "DEFAULT \"0\"")  private Boolean activeProfile;




    public Profile()
    {
    }

    public Profile(int id, String name, String comment) {
        this.id = id;
        this.name = name;
        this.comment = comment;

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Boolean getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(Boolean activeProfile) {
        this.activeProfile = activeProfile;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
