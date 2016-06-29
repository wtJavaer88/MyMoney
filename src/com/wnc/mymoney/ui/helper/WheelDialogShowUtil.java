package com.wnc.mymoney.ui.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import cn.org.octopus.wheelview.widget.ArrayWheelAdapter;
import cn.org.octopus.wheelview.widget.OnWheelChangedListener;
import cn.org.octopus.wheelview.widget.WheelView;

import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.bean.MyWheelBean;
import com.wnc.mymoney.util.DateTimeSelectArrUtil;
import com.wnc.mymoney.util.TextFormatUtil;
import com.wnc.string.PatternUtil;

public class WheelDialogShowUtil
{
    public static void showCurrDateTimeDialog(Context context, String datetime,
            final AfterWheelChooseListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        // dialog.setTitle(title);

        final List<String[]> arrList = new ArrayList<String[]>();
        arrList.add(DateTimeSelectArrUtil.getYears());
        arrList.add(DateTimeSelectArrUtil.getMonths());
        arrList.add(DateTimeSelectArrUtil.getDays());
        arrList.add(DateTimeSelectArrUtil.getHours());
        arrList.add(DateTimeSelectArrUtil.getMinutes());
        arrList.add(DateTimeSelectArrUtil.getSeconds());

        LinearLayout llContent = new LinearLayout(context);
        llContent.setOrientation(LinearLayout.HORIZONTAL);

        final List<WheelView> wheelviews = new ArrayList<WheelView>();

        for (int i = 0; i < 6; i++)
        {
            WheelView wheelview = new WheelView(context);
            wheelview.setVisibleItems(7);
            if (i > 0)
            {
                wheelview.setCyclic(true);
            }
            else
            {
                wheelview.setCyclic(false);
            }
            String[] data = arrList.get(i);
            wheelview.setAdapter(new ArrayWheelAdapter<String>(data));

            wheelview.setTextSize(40);
            llContent.addView(wheelview, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
            wheelviews.add(wheelview);
        }
        setDefaultValue(wheelviews, arrList, datetime);

        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(getFormatDateTimeStr(
                                wheelviews, arrList));
                        dialog.dismiss();
                    }

                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }

    private static String getFormatDateTimeStr(List<WheelView> wheelviews,
            List<String[]> arrList)
    {

        String[] values = new String[6];
        for (int i = 0; i < 6; i++)
        {
            String value = PatternUtil.getFirstPattern(
                    arrList.get(i)[wheelviews.get(i).getCurrentItem()], "\\d+");
            value = BasicStringUtil.fillLeftStringNotruncate(value, 2, "0");
            values[i] = value;
        }
        return TextFormatUtil.addSeparatorToDay(values[0] + values[1]
                + values[2])
                + " "
                + TextFormatUtil.addSeparatorToTime(values[3] + values[4]
                        + values[5]);
    }

    private static void setDefaultValue(List<WheelView> wheelviews,
            List<String[]> arrList, String datetime)
    {
        Date date = TextFormatUtil.getFormatedDate(datetime);
        int month = date.getMonth();
        int day = date.getDate();
        int hour = date.getHours();
        int minute = date.getMinutes();
        int second = date.getSeconds();

        for (int i = 0; i < 6; i++)
        {
            switch (i)
            {
            case 0:
                wheelviews.get(i).setCurrentItem(1);
                break;
            case 1:
                wheelviews.get(i).setCurrentItem(month);
                break;
            case 2:
                wheelviews.get(i).setCurrentItem(day - 1);
                break;
            case 3:
                wheelviews.get(i).setCurrentItem(hour);
                break;
            case 4:
                wheelviews.get(i).setCurrentItem(minute);
                break;
            case 5:
                wheelviews.get(i).setCurrentItem(second);
                break;
            }
        }
    }

    /**
     * 
     * @param context
     * @param title
     * @param leftList
     *            理论上可以去掉, 但是要靠这个对元素排序
     * @param rightMap
     * 
     * @param listener
     */
    public static void showSelectDialog(Context context, String title,
            final List<MyWheelBean> leftList,
            final Map<MyWheelBean, List<? extends MyWheelBean>> rightMap,
            final AfterWheelChooseListener listener)
    {
        showSelectDialog(context, title, leftList, rightMap, 0, 0, listener);
    }

    /**
     * 
     * @param context
     * @param title
     * @param leftList
     *            理论上可以去掉, 但是要靠这个对元素排序
     * @param rightMap
     * 
     * @param listener
     */
    public static void showSelectDialog(Context context, String title,
            final List<MyWheelBean> leftList,
            final Map<MyWheelBean, List<? extends MyWheelBean>> rightMap,
            int leftId, int rightId, final AfterWheelChooseListener listener)
    {

        final String[] leftArr = new String[leftList.size()];
        final String[][] rightArr = new String[leftList.size()][];

        int i = 0;
        for (MyWheelBean mybean : leftList)
        {
            leftArr[i] = mybean.getName();
            if (rightMap.containsKey(mybean))
            {
                int j = 0;
                String[] arr = new String[rightMap.get(mybean).size()];
                for (MyWheelBean bean : rightMap.get(mybean))
                {
                    arr[j] = bean.getName();
                    j++;

                }
                rightArr[i] = arr;
            }
            i++;
        }

        showSelectDialog(context, title, leftArr, rightArr, leftId, rightId,
                listener);
    }

    public static void showSelectDialog(Context context, String title,
            final String[] left, final String[][] right, int defaultLeftId,
            int defaultRightId, final AfterWheelChooseListener listener)
    {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        // dialog.setTitle(title);

        // 创建对话框内容, 创建一个 LinearLayout
        LinearLayout llContent = new LinearLayout(context);
        // 将创建的 LinearLayout 设置成横向的
        llContent.setOrientation(LinearLayout.HORIZONTAL);

        // 创建 WheelView 组件
        final WheelView wheelLeft = new WheelView(context);
        // 设置 WheelView 组件最多显示元素
        wheelLeft.setVisibleItems(5);
        // 设置 WheelView 元素是否循环滚动
        wheelLeft.setCyclic(false);
        // 设置 WheelView 适配器
        wheelLeft.setAdapter(new ArrayWheelAdapter<String>(left));
        wheelLeft.setTextSize(60);

        // 设置右侧的 WheelView
        final WheelView wheelRight = new WheelView(context);
        // 设置右侧 WheelView 显示个数
        wheelRight.setVisibleItems(5);
        // 设置右侧 WheelView 元素是否循环滚动
        wheelRight.setCyclic(false);
        // 设置右侧 WheelView 的元素适配器
        wheelRight.setAdapter(new ArrayWheelAdapter<String>(right[0]));
        wheelRight.setTextSize(60);
        // 设置 LinearLayout 的布局参数
        LinearLayout.LayoutParams paramsLeft = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 4);
        paramsLeft.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams paramsRight = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 6);
        paramsRight.gravity = Gravity.RIGHT;
        // 将 WheelView 对象放到左侧 LinearLayout 中
        llContent.addView(wheelLeft, paramsLeft);
        // 将 WheelView 对象放到 右侧 LinearLayout 中
        llContent.addView(wheelRight, paramsRight);

        wheelLeft.setCurrentItem(defaultLeftId);
        wheelRight.setAdapter(new ArrayWheelAdapter<String>(
                right[defaultLeftId]));
        wheelRight.setCurrentItem(defaultRightId);
        System.out
                .println("选中项: l: " + defaultLeftId + " r: " + defaultRightId);

        // 为左侧的 WheelView 设置条目改变监听器
        wheelLeft.addChangingListener(new OnWheelChangedListener()
        {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue)
            {

                // 设置右侧的 WheelView 的适配器
                wheelRight.setAdapter(new ArrayWheelAdapter<String>(
                        right[newValue]));
                wheelRight.setCurrentItem(right[newValue].length / 2);
            }
        });
        // 设置对话框点击事件 积极
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.afterWheelChoose(wheelLeft.getCurrentItem(),
                                wheelRight.getCurrentItem());
                        System.out.println("回调: l: "
                                + wheelLeft.getCurrentItem() + " r: "
                                + wheelRight.getCurrentItem());
                        dialog.dismiss();
                    }
                });

        // 设置对话框点击事件 消极
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        dialog.setView(llContent);
        dialog.show();
    }
}
