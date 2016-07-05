package com.wnc.train;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wnc.mymoney.R;

public class RadioButtonListActivity extends Activity
{
    private ListView radioButtonList;
    private String[] names
    // = new String[]
    // { "芥川龙之介", "三岛由纪夫", "川端康成", "村上春树", "东野圭吾", "张爱玲", "金庸", "钱钟书", "老舍",
    // "梁实秋", "亨利米勒", "海明威", "菲兹杰拉德", "凯鲁亚克", "杰克伦敦", "小仲马", "杜拉斯", "福楼拜",
    // "雨果", "巴尔扎克", "莎士比亚", "劳伦斯", "毛姆", "柯南道尔", "笛福" }
    ;

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
    }

    private String[] getTrainNames()
    {
        return getIntent().getStringArrayExtra("trains");
    }

    public void showSelectAuthors(View v)
    {
        // long[] authorsId = radioButtonList.getCheckItemIds();
        long[] authorsId = getListSelectededItemIds(radioButtonList);
        String name = "";
        String message;
        if (authorsId.length > 0)
        {
            // 用户至少选择了一位作家
            for (int i = 0; i < authorsId.length; i++)
            {
                String originalName = names[(int) authorsId[i]];
                name += ", "
                        + originalName.substring(0, originalName.indexOf(" "));
            }
            // 将第一趟车前面的“，”去掉
            message = name.substring(1);
        }
        else
        {
            message = "请至少选择一趟车！";
            return;
        }
        // Toast.makeText(RadioButtonListActivity.this, message,
        // Toast.LENGTH_LONG)
        // .show();
        Intent intent = new Intent();
        intent.putExtra("selTrainCodes", message);// 放入返回值
        setResult(0, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
        finish();
    }

    // 避免使用getCheckItemIds()方法
    public long[] getListSelectededItemIds(ListView listView)
    {

        long[] ids = new long[listView.getCount()];// getCount()即获取到ListView所包含的item总个数
        // 定义用户选中Item的总个数
        int checkedTotal = 0;
        for (int i = 0; i < listView.getCount(); i++)
        {
            // 如果这个Item是被选中的
            if (listView.isItemChecked(i))
            {
                ids[checkedTotal++] = i;
            }
        }

        if (checkedTotal < listView.getCount())
        {
            // 定义选中的Item的ID数组
            final long[] selectedIds = new long[checkedTotal];
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
