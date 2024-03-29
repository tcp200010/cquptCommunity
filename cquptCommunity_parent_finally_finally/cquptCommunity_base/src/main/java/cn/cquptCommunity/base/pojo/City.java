package cn.cquptCommunity.base.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
/**
 * 城市模块的实体类
 */
@Entity(name = "b_city")
public class City implements Serializable{

    @Id
    private String id;//ID



    private String name;//城市名称
    private String ishot;//是否热门


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIshot() {
        return ishot;
    }
    public void setIshot(String ishot) {
        this.ishot = ishot;
    }



}