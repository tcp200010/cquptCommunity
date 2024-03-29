package cn.cquptCommunity.user.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * Admin实体类：Admin指的是公司内部人员，具有自己的权限的用户人员
 */
@Entity
@Table(name="u_admin")
public class Admin implements Serializable{

	@Id
	private String id;//ID


	
	private String loginname;//登陆名称
	private String password;//密码
	private String state;//状态

	
	public String getId() {		
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getLoginname() {		
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getPassword() {		
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}


	
}
