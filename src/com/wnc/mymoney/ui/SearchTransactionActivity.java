package com.wnc.mymoney.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.Category;
import com.wnc.mymoney.bean.DayRangePoint;
import com.wnc.mymoney.bean.MyWheelBean;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.CategoryDao;
import com.wnc.mymoney.dao.MemberDao;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.uihelper.AfterWheelChooseListener;
import com.wnc.mymoney.uihelper.TransItemClickListener;
import com.wnc.mymoney.util.app.ToastUtil;
import com.wnc.mymoney.util.app.WheelDialogShowUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.CostTypeUtil;

public class SearchTransactionActivity extends DataViewActivity implements
        View.OnClickListener
{

    private EditText search_keyword_et;
    private Button search_clear_btn;
    private Button search_btn;

    private Button typeSelBtn;
    private Button memberSelBtn;
    private Button picSelBtn;

    private ListView search_expense_lv;
    private Handler mHandler = new Handler();

    DayRangePoint dayRange;

    private final String[] members = MemberDao.getAllMembersForSearch()
            .toArray(new String[MemberDao.getCounts() + 1]);
    private final String[] picItems = new String[]
    { "全部备注", "无图", "有图" };

    List<MyWheelBean> leftData = new ArrayList<MyWheelBean>();
    Map<MyWheelBean, List<? extends MyWheelBean>> rightData = new HashMap<MyWheelBean, List<? extends MyWheelBean>>();
    private static final int NONE_TYPE_ID = 0;

    int selectedLeftIndex = 0;
    int selectedRightIndex = 0;
    String selectedPath = "/";

    static final Category EMPTY_LEVEL_CAT = new Category(NONE_TYPE_ID, "所有类别");
    static final Category EMPTY_DESC_CAT = new Category(NONE_TYPE_ID, "所有子类");

    SimpleAdapter adapter;
    String curKeyWord = "";
    ORDER_FIELD curOrderField = ORDER_FIELD.FIELD_TIME;
    List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.search_nav_transaction_activity);

        initDateRange();
        initViews();

        initWheelData();

        curKeyWord = getIntent().getStringExtra("keyword");
        createNewAdapter(curKeyWord);
    }

    private void initDateRange()
    {
        Intent localIntent = getIntent();
        this.dayRange = (DayRangePoint) localIntent
                .getSerializableExtra("dayrange");
        // System.out.println("dayRange:" + this.dayRange);
    }

    private void initViews()
    {
        this.search_keyword_et = (EditText) findViewById(R.id.search_keyword_et);
        this.search_clear_btn = (Button) findViewById(R.id.search_clear_btn);
        this.search_btn = (Button) findViewById(R.id.search_btn);
        this.typeSelBtn = (Button) findViewById(R.id.category_name_btn);
        this.memberSelBtn = (Button) findViewById(R.id.member_row_btn);
        this.picSelBtn = (Button) findViewById(R.id.haspic_btn);
        this.search_expense_lv = (ListView) findViewById(R.id.search_expense_lv);

        this.search_clear_btn.setOnClickListener(this);
        this.search_btn.setOnClickListener(this);
        this.typeSelBtn.setOnClickListener(this);
        this.memberSelBtn.setOnClickListener(this);
        this.picSelBtn.setOnClickListener(this);

        this.search_keyword_et.setFocusable(true);
        this.search_keyword_et.setFocusableInTouchMode(true);
        this.search_keyword_et.requestFocus();

        this.search_keyword_et.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // System.out.println("Text has changed");
                final String word = s.toString();
                SearchTransactionActivity.this.curKeyWord = word;
                SearchTransactionActivity.this.mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (word.length() > 0)
                        {
                            SearchTransactionActivity.this.search_clear_btn
                                    .setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            SearchTransactionActivity.this.search_clear_btn
                                    .setVisibility(View.INVISIBLE);
                        }
                        createNewAdapter(word);
                    }
                });
            }
        });

        // 根据ID找到RadioGroup实例
        RadioGroup group = (RadioGroup) this.findViewById(R.id.rb_group);
        // 绑定一个匿名监听器
        group.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1)
            {
                // TODO Auto-generated method stub
                // 获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                // 根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton) SearchTransactionActivity.this
                        .findViewById(radioButtonId);
                // 更新文本内容，以符合选中项
                if (rb.getId() == R.id.time_rb)
                {
                    curOrderField = ORDER_FIELD.FIELD_TIME;
                }
                else if (rb.getId() == R.id.cost_rb)
                {
                    curOrderField = ORDER_FIELD.FIELD_COST;
                }
                else if (rb.getId() == R.id.type_rb)
                {
                    curOrderField = ORDER_FIELD.FIELD_TYPE;
                }
                dataReOrder();
            }
        });
    }

    private void initWheelData()
    {

        leftData.addAll(CategoryDao.getAllLevels(-1));
        leftData.addAll(CategoryDao.getAllLevels(-2));
        leftData.add(0, EMPTY_LEVEL_CAT);

        // 这儿要深度复制,因为map的value值是个list引用的还是原来的
        computeRightMap(CategoryDao.getAllDescTypes(-1));
        computeRightMap(CategoryDao.getAllDescTypes(-2));

        // List<? extends MyWheelBean> tmpList = Arrays.asList(EMPTY_DESC_CAT);
        // tmpList.add(EMPTY_DESC_CAT);
        rightData.put(EMPTY_LEVEL_CAT, Arrays.asList(EMPTY_DESC_CAT));

    }

    private void computeRightMap(Map<MyWheelBean, List<MyWheelBean>> map)
    {
        for (Map.Entry<MyWheelBean, List<MyWheelBean>> entry : map.entrySet())
        {
            List<MyWheelBean> list = new ArrayList<MyWheelBean>();
            list.addAll(entry.getValue());
            if (!list.contains(EMPTY_DESC_CAT))
            {
                list.add(0, EMPTY_DESC_CAT);
            }
            rightData.put(entry.getKey(), list);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == this.search_btn)
        {
            createNewAdapter();
        }
        else if (v == this.search_clear_btn)
        {
            cleatSearchContent();
        }
        else if (v == this.typeSelBtn)
        {
            showCostTypeWheel();
        }
        else if (v == this.memberSelBtn)
        {
            showMembersMenu();
        }
        else if (v == this.picSelBtn)
        {
            showPicMenu();
        }
    }

    private void cleatSearchContent()
    {
        this.search_keyword_et.setText("");
    }

    private void showMembersMenu()
    {
        AlertDialog.Builder builder = new Builder(this);

        builder.setItems(this.members, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                memberSelBtn.setText(members[arg1]);
                arg0.dismiss();
            }
        });
        builder.show();
    }

    private void showPicMenu()
    {
        AlertDialog.Builder builder = new Builder(this);

        builder.setItems(this.picItems, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                picSelBtn.setText(picItems[arg1]);
                arg0.dismiss();
            }
        });
        builder.show();
    }

    private void showCostTypeWheel()
    {
        WheelDialogShowUtil.showRelativeDialog(this, "选择分类", leftData,
                rightData, selectedLeftIndex, selectedRightIndex,
                new AfterWheelChooseListener()
                {
                    @Override
                    public void afterWheelChoose(Object... objs)
                    {
                        selectedLeftIndex = Integer.valueOf(objs[0].toString());
                        selectedRightIndex = Integer.valueOf(objs[1].toString());
                        MyWheelBean lbean = leftData.get(selectedLeftIndex);
                        MyWheelBean rbean = rightData.get(lbean).get(
                                selectedRightIndex);
                        if (rbean.getId() != NONE_TYPE_ID)
                        {
                            typeSelBtn.setText(lbean.getName() + "-->"
                                    + rbean.getName());
                        }
                        else
                        {
                            typeSelBtn.setText(lbean.getName());
                        }
                        selectedPath = "/" + lbean.getId() + "/"
                                + rbean.getId();
                        selectedPath = selectedPath.replace("/" + NONE_TYPE_ID,
                                "");
                        // System.out.println("selectedPath::" +
                        // selectedPath);
                    }

                });
    }

    /**
     * 外部调用
     */
    public void createNewAdapter()
    {
        createNewAdapter(this.curKeyWord);
    }

    private void createNewAdapter(String keyword)
    {
        this.search_expense_lv.setVisibility(View.VISIBLE);
        getSearchMapData(keyword);
        this.adapter = new SimpleAdapter(this, mData,
                R.layout.nav_year_trans_lv_item, new String[]
                { "icon", "name", "photo", "memo", "cost" }, new int[]
                { R.id.item_icon_iv, R.id.item_name_tv, R.id.photo_flag_iv,
                        R.id.memo_tv, R.id.cost_tv });
        this.search_expense_lv.setAdapter(this.adapter);
        TransItemClickListener transItemClickListener = new TransItemClickListener(
                this, this.search_expense_lv);
        this.search_expense_lv.setOnItemClickListener(transItemClickListener);
        this.search_expense_lv
                .setOnItemLongClickListener(transItemClickListener);

        dataReOrder();
        if (mData.size() > 0)
        {
            ToastUtil.showLongToast(this, "记录数:" + mData.size() + " 消费总额:"
                    + TextFormatUtil.getFormatMoneyStr(COST_SUM));
        }
    }

    private double COST_SUM = 0;

    private void getSearchMapData(String keyword)
    {
        mData.clear();
        COST_SUM = 0;

        TransactionsDao.initDb(this);
        String haspic = picSelBtn.getText().toString();
        if (haspic.equals(picItems[0]))
        {
            haspic = "";
        }
        String member = memberSelBtn.getText().toString();
        if (member.equals(members[0]))
        {
            member = "";
        }
        List<Trade> tradeItems = TransactionsDao
                .getSearchTransactionWithDateRange(haspic, member,
                        selectedPath, keyword, this.dayRange.getStartDay(),
                        this.dayRange.getEndDay());
        for (Trade trade : tradeItems)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Trade", trade);
            map.put("id", trade.getId());// 获取交易ID
            map.put("icon", CostTypeUtil.getIcon(trade.getCostdesc_id()));
            map.put("costlevelid", trade.getCostlevel_id());
            map.put("costdescid", trade.getCostdesc_id());
            map.put("name",
                    CostTypeUtil.getCostTypeName(trade.getCostdesc_id()));
            map.put("photo", CostTypeUtil.getFlagIcon(trade.getHaspicture()));
            map.put("memo", trade.getMemo());
            map.put("cost", trade.getCost());
            map.put("searchtime", trade.getCreatelongtime());// 方便后期的排序
            if (trade.getType_id() == -1)
            {
                COST_SUM += trade.getCost();
            }
            mData.add(map);
        }
        TransactionsDao.closeDb();
    }

    private void lvDataRefresh()
    {
        adapter.notifyDataSetChanged();
    }

    private void dataReOrder()
    {
        Collections.sort(mData, new Comparator<Map<String, Object>>()
        {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2)
            {
                if (curOrderField == ORDER_FIELD.FIELD_COST)
                {
                    Double com1 = BasicNumberUtil.getDouble(o1.get("cost") + "");
                    Double com2 = BasicNumberUtil.getDouble(o2.get("cost") + "");
                    return com1.compareTo(com2);
                }
                else if (curOrderField == ORDER_FIELD.FIELD_TIME)
                {
                    String com1 = o1.get("searchtime") + "";
                    String com2 = o2.get("searchtime") + "";
                    return com1.compareTo(com2);
                }
                else if (curOrderField == ORDER_FIELD.FIELD_TYPE)
                {
                    String com1 = o1.get("costlevelid") + ""
                            + o1.get("costdescid") + "";
                    String com2 = o2.get("costlevelid") + ""
                            + o2.get("costdescid") + "";
                    return com1.compareTo(com2);
                }
                return 0;
            }
        });
        lvDataRefresh();
    }

    public enum ORDER_FIELD
    {
        FIELD_TIME, FIELD_COST, FIELD_TYPE
    }

    @Override
    public void delTrigger(ListView arg0, int arg2)
    {
        this.mData.remove(arg2);
        this.adapter = new SimpleAdapter(this, mData,
                R.layout.nav_year_trans_lv_item, new String[]
                { "icon", "name", "photo", "memo", "cost" }, new int[]
                { R.id.item_icon_iv, R.id.item_name_tv, R.id.photo_flag_iv,
                        R.id.memo_tv, R.id.cost_tv });
        this.search_expense_lv.setAdapter(this.adapter);
        TransItemClickListener transItemClickListener = new TransItemClickListener(
                this, this.search_expense_lv);
        this.search_expense_lv.setOnItemClickListener(transItemClickListener);
        this.search_expense_lv
                .setOnItemLongClickListener(transItemClickListener);

        dataReOrder();
        isModify = true;
    }

    public boolean isModify = false;
    public static int SEARCH_MODIFY_RESULT = 2001;

    // 返回键事件
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        if (isModify)
        {
            setResult(SEARCH_MODIFY_RESULT, intent);
        }
        else
        {
            setResult(RESULT_CANCELED, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果经过修改
        if (requestCode == DataViewActivity.MODIFY_REQUEST_CODE)
        {
            modifyTrigger();
        }
    }

    @Override
    public void modifyTrigger()
    {
        createNewAdapter();
        isModify = true;
    }
}