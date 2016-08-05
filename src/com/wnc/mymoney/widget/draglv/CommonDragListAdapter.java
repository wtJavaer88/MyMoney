package com.wnc.mymoney.widget.draglv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.wnc.mymoney.R;

public class CommonDragListAdapter extends DragListAdapter
{
    private List<Map<String, Object>> arrayTitles;
    private static final String TAG = "CommonDragListAdapter";

    public CommonDragListAdapter(Context context,
            List<Map<String, Object>> arrayTitles)
    {
        this.context = context;
        this.arrayTitles = arrayTitles;
    }

    @Override
    public void showDropItem(boolean showItem)
    {
        this.ShowItem = showItem;
    }

    @Override
    public void setInvisiblePosition(int position)
    {
        invisilePosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /***
         * 在这里尽可能每次都进行实例化新的，这样在拖拽ListView的时候不会出现错乱.
         * 具体原因不明，不过这样经过测试，目前没有发现错乱。虽说效率不高，但是做拖拽LisView足够了。
         */
        convertView = LayoutInflater.from(context).inflate(
                R.layout.drag_list_item, null);

        TextView textView = (TextView) convertView
                .findViewById(R.id.drag_list_item_text);
        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.drag_list_item_image);
        textView.setText(arrayTitles.get(position).get("name").toString());
        if (isChanged)
        {
            if (position == invisilePosition)
            {
                if (!ShowItem)
                {
                    convertView.findViewById(R.id.drag_list_item_text)
                            .setVisibility(View.INVISIBLE);
                    convertView.findViewById(R.id.drag_list_item_image)
                            .setVisibility(View.INVISIBLE);
                    convertView.findViewById(R.id.check_del).setVisibility(
                            View.INVISIBLE);
                }
            }
            if (lastFlag != -1)
            {
                if (lastFlag == 1)
                {
                    if (position > invisilePosition)
                    {
                        Animation animation;
                        animation = getFromSelfAnimation(0, -height);
                        convertView.startAnimation(animation);
                    }
                }
                else if (lastFlag == 0)
                {
                    if (position < invisilePosition)
                    {
                        Animation animation;
                        animation = getFromSelfAnimation(0, height);
                        convertView.startAnimation(animation);
                    }
                }
            }
        }
        return convertView;
    }

    public void exchange(int startPosition, int endPosition)
    {
        System.out.println(startPosition + "--" + endPosition);
        Object startObject = getItem(startPosition);
        System.out.println(startPosition + "========" + endPosition);
        if (startPosition < endPosition)
        {
            arrayTitles.add(endPosition + 1, (Map) startObject);
            arrayTitles.remove(startPosition);
        }
        else
        {
            arrayTitles.add(endPosition, (Map) startObject);
            arrayTitles.remove(startPosition + 1);
        }
        isChanged = true;
    }

    @Override
    public void exchangeCopy(int startPosition, int endPosition)
    {
        System.out.println(startPosition + "--" + endPosition);
        // holdPosition = endPosition;
        Object startObject = getCopyItem(startPosition);
        if (startPosition < endPosition)
        {
            mCopyList.add(endPosition + 1, (Map) startObject);
            mCopyList.remove(startPosition);
        }
        else
        {
            mCopyList.add(endPosition, (Map) startObject);
            mCopyList.remove(startPosition + 1);
        }
        isChanged = true;
        // notifyDataSetChanged();
    }

    public Object getCopyItem(int position)
    {
        return mCopyList.get(position);
    }

    @Override
    public int getCount()
    {
        return arrayTitles.size();
    }

    @Override
    public Object getItem(int position)
    {
        return arrayTitles.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void addDragItem(int start, Object obj)
    {
        Log.i(TAG, "start" + start);
        arrayTitles.remove(start);// 删除该项
        arrayTitles.add(start, (Map) obj);// 添加删除项
    }

    private List<Map<String, Object>> mCopyList = new ArrayList<Map<String, Object>>();

    @Override
    public void copyList()
    {
        mCopyList.clear();
        for (Map map : arrayTitles)
        {
            mCopyList.add(map);
        }
    }

    @Override
    public void pastList()
    {
        arrayTitles.clear();
        for (Map map : mCopyList)
        {
            arrayTitles.add(map);
        }
    }

    @Override
    public void setIsSameDragDirection(boolean value)
    {
        isSameDragDirection = value;
    }

    @Override
    public void setLastFlag(int flag)
    {
        lastFlag = flag;
    }

    @Override
    public void setHeight(int value)
    {
        height = value;
    }

    public void setCurrentDragPosition(int position)
    {
        dragPosition = position;
    }

    public Animation getFromSelfAnimation(int x, int y)
    {
        TranslateAnimation go = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
        go.setInterpolator(new AccelerateDecelerateInterpolator());
        go.setFillAfter(true);
        go.setDuration(100);
        go.setInterpolator(new AccelerateInterpolator());
        return go;
    }

    public Animation getToSelfAnimation(int x, int y)
    {
        TranslateAnimation go = new TranslateAnimation(Animation.ABSOLUTE, x,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y,
                Animation.RELATIVE_TO_SELF, 0);
        go.setInterpolator(new AccelerateDecelerateInterpolator());
        go.setFillAfter(true);
        go.setDuration(100);
        go.setInterpolator(new AccelerateInterpolator());
        return go;
    }

    public List<Map<String, Object>> getArrayTitles()
    {
        return arrayTitles;
    }
}
