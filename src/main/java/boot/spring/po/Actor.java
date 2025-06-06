package boot.spring.po;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;



public class Actor implements Serializable {

	@Excel(name = "编号", width=10)
	private Integer actor_id;
	

	@Excel(name = "名字")
	private String first_name;
	

	@Excel(name = "姓氏")
	private String last_name;
	

	@Excel(name = "最后更新日期", width=20)
	private String last_update;
	
	public Actor() {
		super();
	}
	public Actor( String first_name, String last_name, String last_update) {
		super();
		this.first_name = first_name;
		this.last_name = last_name;
		this.last_update = last_update;
	}
	
	public Integer getActor_id() {
		return actor_id;
	}
	public void setActor_id(Integer actor_id) {
		this.actor_id = actor_id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getLast_update() {
		return last_update;
	}
	public void setLast_update(String last_update) {
		this.last_update = last_update;
	}
	@Override
	public String toString() {
		return "Actor [actor_id=" + actor_id + ", first_name=" + first_name + ", last_name=" + last_name
				+ ", last_update=" + last_update + "]";
	}
	
}
