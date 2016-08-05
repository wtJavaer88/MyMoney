package com.wnc.train;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.util.app.ToastUtil;

public class RadioButtonListActivity extends Activity
{
    public static int RETURN_CODE = 100;
    private ListView radioButtonList;
    private String[] names
    // = new String[]
    // { "芥川龙之介", "三岛由纪夫", "川端康成", "村上春树", "东野圭吾", "张爱玲", "金庸", "钱钟书", "老舍",
    // "梁实秋", "亨利米勒", "海明威", "菲兹杰拉德", "凯鲁亚克", "杰克伦敦", "小仲马", "杜拉斯", "福楼拜",
    // "雨果", "巴尔扎克", "莎士比亚", "劳伦斯", "毛姆", "柯南道尔", "笛福" }
    ;
    public static final String EXTRA_TRAINS = "trains";
    public static final String EXTRA_CITIES = "cities_info";
    public static final String EXTRA_TRAIN_CODES = "selTrainCodes";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_list);

        names = getTrainNames();
        radioButtonList = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, names);
        radioButtonList.setAdapter(adapter);
        setChecked();
        setCitiesInfo();
    }

    private void setChecked()
    {
        if (CityTrainsHolder.lastSelIds != null)
        {
            if (sameQueryAsLast())
            {
                for (int id : CityTrainsHolder.lastSelIds)
                {
                    radioButtonList.setItemChecked(id, true);
                }
            }
            else
            {
                // 重新选择的情况下, 清空原有的
                CityTrainsHolder.lastSelIds = null;
            }
        }
        CityTrainsHolder.lastCityInfo = getIntent()
                .getStringExtra(EXTRA_CITIES);
    }

    private boolean sameQueryAsLast()
    {
        return CityTrainsHolder.lastCityInfo.equals(getIntent().getStringExtra(
                EXTRA_CITIES));
    }

    private void setCitiesInfo()
    {
        ((TextView) findViewById(R.id.cities_info))
                .setText(CityTrainsHolder.lastCityInfo);
    }

    private String[] getTrainNames()
    {
        return getIntent().getStringArrayExtra("trains");
    }

    public void showSelectTrains(View v)
    {
        int[] trainsId = getListSelectededItemIds(radioButtonList);
        String name = "";
        String message;
        if (trainsId.length > 0)
        {
            // 用户至少选择了一趟火车
            for (int i = 0; i < trainsId.length; i++)
            {
                String originalName = names[trainsId[i]];
                name += ", "
                        + originalName.substring(0, originalName.indexOf(" "));
            }
            // 将第一趟车前面的“，”去掉
            message = name.substring(1);
        }
        else
        {
            message = "请至少选择一趟车！";
            ToastUtil.showLongToast(this, message);
            return;
        }

        Intent intent = new Intent();
        CityTrainsHolder.lastSelIds = trainsId;
        intent.putExtra(EXTRA_TRAIN_CODES, message);// 放入返回值
        setResult(RETURN_CODE, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
        finish();
    }

    public void selAll(View v)
    {
        for (int i = 0; i < radioButtonList.getCount(); i++)
        {
            if (!radioButtonList.isItemChecked(i))
            {
                radioButtonList.setItemChecked(i, true);
            }
        }
    }

    public void diselAll(View v)
    {
        for (int i = 0; i < radioButtonList.getCount(); i++)
        {
            if (radioButtonList.isItemChecked(i))
            {
                radioButtonList.setItemChecked(i, false);
            }
        }
    }

    // 避免使用getCheckItemIds()方法
    public int[] getListSelectededItemIds(ListView listView)
    {
        int[] ids = new int[listView.getCount()];
        // 定义用户选中Item的总个数
        int checkedTotal = 0;
        for (int i = 0; i < listView.getCount(); i++)
        {
            if (listView.isItemChecked(i))
            {
                ids[checkedTotal++] = i;
            }
        }

        if (checkedTotal < listView.getCount())
        {
            // 定义选中的Item的ID数组
            final int[] selectedIds = new int[checkedTotal];
            // 数组复制 ids
            System.arraycopy(ids, 0, selectedIds, 0, checkedTotal);
            return selectedIds;
        }
        else
        {
            // 用户将所有的Item都选了
            return ids;
        }
    }

}
