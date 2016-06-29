package com.wnc.mymoney.ui.widget;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wnc.mymoney.R;

public class ComboBox extends Button
{

    private final static String TAG = "ComboBox";

    private ListViewItemClickListener m_listener;

    private View m_view;
    private ListView m_listView;
    private PopupWindow m_popupwindow;
    private ListViewAdapter m_adapter_listview;
    private String[] m_data;
    private Context m_context;

    public ComboBox(Context context)
    {
        super(context);
        this.m_context = context;
        setListeners();

        init();
    }

    public ComboBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.m_context = context;
        setListeners();

        init();
    }

    public ComboBox(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.m_context = context;
        setListeners();

        init();
    }

    private void init()
    {
        this.m_adapter_listview = new ListViewAdapter(this.m_context);
        this.m_view = LayoutInflater.from(this.m_context).inflate(
                R.layout.combobox_listview, null);
        if (!isInEditMode())
        {
            this.m_listView = (ListView) this.m_view
                    .findViewById(R.id.id_listview);
            this.m_listView.setAdapter(this.m_adapter_listview);
            this.m_listView.setClickable(true);
            this.m_listView.setOnItemClickListener(new OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id)
                {
                    // TODO Auto-generated method stub
                    ComboBox.this.m_popupwindow.dismiss();
                    ComboBox.this.setText(ComboBox.this.m_data[position]);

                    if (ComboBox.this.m_listener != null)
                    {
                        ComboBox.this.m_listener.onItemClick(position);
                    }
                }
            });
        }
        Drawable drawable = this.m_context.getResources().getDrawable(
                R.drawable.common_title_btn_arrow_down);

        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        // 设置左图标
        this.setCompoundDrawables(null, null, drawable, null);
        // this.setBackgroundColor(Color.WHITE);
    }

    public void setData(String[] data)
    {
        if (null == data || data.length <= 0)
        {
            return;
        }

        this.m_data = data;
        this.setText(data[0]);
    }

    public void setData(List<String> data)
    {
        if (null == data || data.size() <= 0)
        {
            return;
        }

        this.m_data = data.toArray(new String[data.size()]);
        this.setText(data.get(0));
    }

    public void setListViewOnClickListener(ListViewItemClickListener listener)
    {
        this.m_listener = listener;
    }

    private void setListeners()
    {

        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Log.d(TAG, "Touch......");
                return false;
            }
        });

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "Click......");
                if (ComboBox.this.m_popupwindow == null)
                {
                    ComboBox.this.m_popupwindow = new PopupWindow(
                            ComboBox.this.m_view, ComboBox.this.getWidth(), 300);// LayoutParams.WRAP_CONTENT);

                    // 点击PopUpWindow外面的控件也可以使得PopUpWindow dimiss。
                    // 需要顺利让PopUpWindow dimiss；PopUpWindow的背景不能为空。
                    ComboBox.this.m_popupwindow
                            .setBackgroundDrawable(new BitmapDrawable());

                    // 获得焦点，并且在调用setFocusable（true）方法后，可以通过Back(返回)菜单使PopUpWindow
                    // dimiss
                    // pop.setFocusable(true)
                    ComboBox.this.m_popupwindow.setFocusable(true);
                    ComboBox.this.m_popupwindow.setOutsideTouchable(true);
                    ComboBox.this.m_popupwindow.showAsDropDown(ComboBox.this,
                            0, 0);

                }
                else if (ComboBox.this.m_popupwindow.isShowing())
                {
                    ComboBox.this.m_popupwindow.dismiss();
                }
                else
                {
                    ComboBox.this.m_popupwindow.showAsDropDown(ComboBox.this);
                }
            }

        });
    }

    class ListViewAdapter extends BaseAdapter
    {
        private LayoutInflater m_inflate;

        public ListViewAdapter(Context context)
        {
            // TODO Auto-generated constructor stub
            this.m_inflate = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
            // TODO Auto-generated method stub
            return ComboBox.this.m_data == null ? 0
                    : ComboBox.this.m_data.length;
        }

        @Override
        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            TextView textview = null;

            if (convertView == null)
            {
                convertView = this.m_inflate.inflate(R.layout.combobox_item,
                        null);
                textview = (TextView) convertView.findViewById(R.id.id_txt);

                convertView.setTag(textview);
            }
            else
            {
                textview = (TextView) convertView.getTag();
            }

            textview.setText(ComboBox.this.m_data[position]);

            return convertView;
        }
    }

    public interface ListViewItemClickListener
    {
        void onItemClick(int position);
    }
}
