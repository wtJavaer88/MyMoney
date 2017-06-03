package com.wnc.mymoney.bean;

import java.io.Serializable;

public class Trade implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3910584680855031271L;
	/**
	 * 
	 */
	private int id;
	private int type_id;
	private double cost;

	private String member;
	private int costlevel_id;
	private int costdesc_id;

	private String memo;
	private String project;
	private String shop;
	private int haspicture;

	private String createtime;
	private String modifytime;
	private long createlongtime;

	private String uuid;

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getType_id()
	{
		return this.type_id;
	}

	public void setType_id(int type_id)
	{
		this.type_id = type_id;
	}

	public double getCost()
	{
		return this.cost;
	}

	public void setCost(double cost)
	{
		this.cost = cost;
	}

	public String getMember()
	{
		return this.member;
	}

	public void setMember(String member)
	{
		this.member = member;
	}

	public int getCostlevel_id()
	{
		return this.costlevel_id;
	}

	public void setCostlevel_id(int costlevel_id)
	{
		this.costlevel_id = costlevel_id;
	}

	public int getCostdesc_id()
	{
		return this.costdesc_id;
	}

	public void setCostdesc_id(int costdesc_id)
	{
		this.costdesc_id = costdesc_id;
	}

	public String getMemo()
	{
		return this.memo;
	}

	public void setMemo(String memo)
	{
		this.memo = memo;
	}

	public String getProject()
	{
		return this.project;
	}

	public void setProject(String project)
	{
		this.project = project;
	}

	public String getShop()
	{
		return this.shop;
	}

	public void setShop(String shop)
	{
		this.shop = shop;
	}

	public int getHaspicture()
	{
		return this.haspicture;
	}

	public void setHaspicture(int haspicture)
	{
		this.haspicture = haspicture;
	}

	public String getCreatetime()
	{
		return this.createtime;
	}

	public void setCreatetime(String createtime)
	{
		this.createtime = createtime;
	}

	public String getModifytime()
	{
		return this.modifytime;
	}

	public void setModifytime(String modifytime)
	{
		this.modifytime = modifytime;
	}

	public long getCreatelongtime()
	{
		return this.createlongtime;
	}

	public void setCreatelongtime(long createlongtime)
	{
		this.createlongtime = createlongtime;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	@Override
	public String toString()
	{
		return "Trade [id=" + id + ", type_id=" + type_id + ", cost=" + cost
				+ ", member=" + member + ", costlevel_id=" + costlevel_id
				+ ", costdesc_id=" + costdesc_id + ", memo=" + memo
				+ ", project=" + project + ", shop=" + shop + ", haspicture="
				+ haspicture + ", createtime=" + createtime + ", modifytime="
				+ modifytime + ", createlongtime=" + createlongtime + ", uuid="
				+ uuid + "]";
	}

}
