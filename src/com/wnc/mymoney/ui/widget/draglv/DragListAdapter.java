package com.wnc.mymoney.ui.widget.draglv;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class DragListAdapter extends BaseAdapter
{
    protected Context context;
    public boolean isHidden;
    protected int invisilePosition = -1;
    protected boolean isChanged = true;
    protected boolean ShowItem = false;
    protected boolean isSameDragDirection = true;
    protected int lastFlag = -1;
    protected int height;
    protected int dragPosition = -1;

    public abstract void setInvisiblePosition(int position);

    public abstract void copyList();

    public abstract void showDropItem(boolean showItem);

    public abstract void exchangeCopy(int startPosition, int endPosition);

    public abstract void setHeight(int value);

    public abstract void setIsSameDragDirection(boolean value);

    public abstract void setLastFlag(int flag);

    public abstract void pastList();
}
