package com.wnc.mymoney.bean;

public class Category implements MyWheelBean
{
    private int id;
    private String name;
    private String parent_id;
    private int depth;
    private String icon;
    private String path;
    private String ordered;

    public Category()
    {

    }

    public Category(int id, String name)
    {
        setId(id);
        setName(name);
    }

    @Override
    public int getId()
    {
        // TODO Auto-generated method stub
        return this.id;
    }

    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return this.name;
    }

    public String getParent_id()
    {
        return this.parent_id;
    }

    public void setParent_id(String parent_id)
    {
        this.parent_id = parent_id;
    }

    public int getDepth()
    {
        return this.depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public String getIcon()
    {
        return this.icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getOrdered()
    {
        return this.ordered;
    }

    public void setOrdered(String ordered)
    {
        this.ordered = ordered;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "ID:" + this.id + " NAME:" + this.name + " parent_id:"
                + this.parent_id + " depth:" + this.depth + " icon:"
                + this.icon + " path:" + this.path + " ordered:" + this.ordered;
    }

}
