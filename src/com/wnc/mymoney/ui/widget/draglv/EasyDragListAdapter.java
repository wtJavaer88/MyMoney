package com.wnc.mymoney.ui.widget.draglv;

import java.util.ArrayList;
import java.util.List;

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

public class EasyDragListAdapter extends DragListAdapter
{
    private static final String TAG = "EasyDragListAdapter";
    private List<String> arrayTitles;

    // private ArrayList<Integer> arrayDrawables;

    public EasyDragListAdapter(Context context, List<String> arrayTitles)
    {
        this.context = context;
        this.arrayTitles = arrayTitles;
        // this.arrayDrawables = arrayDrawables;
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
        // imageView.setImageResource(arrayDrawables.get(position));
        textView.setText(arrayTitles.get(position));
        if (isChanged)
        {
            // Log.i("wanggang", "position == " + position);
            // Log.i("wanggang", "holdPosition == " + invisilePosition);
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
                    // convertView.setVisibility(View.INVISIBLE);
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

    /***
     * 动态修改ListVIiw的方位.
     * 
     * @param start
     *            点击移动的position
     * @param down
     *            松开时候的position
     */

    public void exchange(int startPosition, int endPosition)
    {
        System.out.println(startPosition + "--" + endPosition);
        Object startObject = getItem(startPosition);
        System.out.println(startPosition + "========" + endPosition);
        Log.d("ON", "startPostion ==== " + startPosition);
        Log.d("ON", "endPosition ==== " + endPosition);
        if (startPosition < endPosition)
        {
            arrayTitles.add(endPosition + 1, (String) startObject);
            arrayTitles.remove(startPosition);
        }
        else
        {
            arrayTitles.add(endPosition, (String) startObject);
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
        System.out.println(startPosition + "========" + endPosition);
        Log.d("ON", "startPostion ==== " + startPosition);
        Log.d("ON", "endPosition ==== " + endPosition);
        if (startPosition < endPosition)
        {
            mCopyList.add(endPosition + 1, (String) startObject);
            mCopyList.remove(startPosition);
        }
        else
        {
            mCopyList.add(endPosition, (String) startObject);
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
        String title = arrayTitles.get(start);
        arrayTitles.remove(start);// 删除该项
        arrayTitles.add(start, (String) obj);// 添加删除项
    }

    private ArrayList<String> mCopyList = new ArrayList<String>();

    @Override
    public void copyList()
    {
        mCopyList.clear();
        for (String str : arrayTitles)
        {
            mCopyList.add(str);
        }
    }

    @Override
    public void pastList()
    {
        arrayTitles.clear();
        for (String str : mCopyList)
        {
            arrayTitles.add(str);
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

    public List<String> getArrayTitles()
    {
        return arrayTitles;
    }
}
