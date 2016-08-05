package com.wnc.mymoney.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.backup.BackupFilesHolder;
import com.wnc.mymoney.bean.MyWheelBean;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.CategoryDao;
import com.wnc.mymoney.dao.MemberDao;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.uihelper.AfterWheelChooseListener;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.uihelper.WheelDialogShowUtil;
import com.wnc.mymoney.util.ClipBoardUtil;
import com.wnc.mymoney.util.CostTypeUtil;
import com.wnc.mymoney.util.FileTypeUtil;
import com.wnc.mymoney.util.GeneratorUtil;
import com.wnc.mymoney.util.TextFormatUtil;
import com.wnc.mymoney.util.ToastUtil;
import com.wnc.mymoney.util.UriUtil;
import com.wnc.mymoney.widget.ComboBox;
import com.wnc.mymoney.widget.ComboBox.ListViewItemClickListener;

public class AddOrEditTransActivity extends BaseActivity implements
        OnClickListener
{
    private static final String EQU_MODEL_NAME = "自转";
    private static final String IN_MODEL_NAME = "收入";
    private static final String OUT_MODEL_NAME = "支出";
    private static final int OUT_MODEL = -1;
    private static final int IN_MODEL = -2;
    private static final int EQU_MODEL = -3;

    private boolean isAdd = true;
    private Trade modifyTrade;

    private ComboBox m_combobox;
    TextView categoryTV;
    Button cancleBt;
    Button tradeTimeBT;
    Button tradeMemberBT;
    Button tranSaveBT;
    Button tranCostBT;
    EditText memoET;

    final String defaultOutLevelName = "食品酒水";
    final String defaultOutDescTypeName = "早中晚餐";
    final int defaultOutTradeType = 1;
    final int defaultOutDescTradeType = 11;

    final String defaultInLevelName = "资金收入";
    final String defaultInDescTypeName = "工资收入";
    final int defaultInTradeType = 6;
    final int defaultInDescTradeType = 61;

    int curTradeType = 1;// 代表食品酒水
    int curDescTradeType = 11;// 代表早中晚餐

    private Map<String, Integer> typeMap = new HashMap<String, Integer>();

    private List<MyWheelBean> outTradeTypes = new ArrayList<MyWheelBean>();
    private Map<MyWheelBean, List<? extends MyWheelBean>> outTradeDescTypes = new HashMap<MyWheelBean, List<? extends MyWheelBean>>();

    private List<MyWheelBean> inTradeTypes = new ArrayList<MyWheelBean>();
    private Map<MyWheelBean, List<? extends MyWheelBean>> inTradeDescTypes = new HashMap<MyWheelBean, List<? extends MyWheelBean>>();

    private final String[] members = MemberDao.getAllMembers().toArray(
            new String[MemberDao.getCounts()]);
    private final String[] picMenu = new String[]
    { "相机", "相册", "录音", "录像" };

    private File mPhotoFile;
    private final int COST_PANEL_RESULT = 1;
    private final int CAMERA_RESULT = 100;
    private final int LOAD_IMAGE_RESULT = 200;
    private final int VOICE_RESULT = 300;
    private final int VIDEO_RESULT = 400;

    private String savePicDir = MyAppParams.getInstance().getTmpPicPath();
    private String saveVoiceDir = MyAppParams.getInstance().getTmpVoicePath();

    private String lastMemoContent = "";
    private int selectedLeftIndex = 0;
    private int selectedRightIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_or_edit_trans_activity);

        Button bt = (Button) findViewById(R.id.pick_photo_btn);
        bt.setOnClickListener(this);

        initData();

        initBts();
        initCombobox();
        initEtAndTv();

        setViewsInCurrModel();
    }

    private void initData()
    {
        this.isAdd = getIntent().getBooleanExtra("isAdd", true);
        this.modifyTrade = (Trade) getIntent().getSerializableExtra("Trade");

        if (!this.isAdd && this.modifyTrade == null)
        {
            ToastUtil.showLongToast(this, "要修改的交易记录为空!");
            finish();
        }
        CategoryDao.initDb(this);
        this.inTradeTypes.addAll(CategoryDao.getAllLevels(-2));
        this.inTradeDescTypes.putAll(CategoryDao.getAllDescTypes(-2));
        this.outTradeTypes.addAll(CategoryDao.getAllLevels(-1));
        this.outTradeDescTypes.putAll(CategoryDao.getAllDescTypes(-1));

        CategoryDao.closeDb();
    }

    private void initBts()
    {
        this.cancleBt = (Button) findViewById(R.id.back_btn);
        cancleBt.setOnClickListener(this);
        this.tradeTimeBT = (Button) findViewById(R.id.trade_time_row_btn);
        this.tradeTimeBT.setOnClickListener(this);

        this.tradeMemberBT = (Button) findViewById(R.id.member_row_btn);
        this.tradeMemberBT.setOnClickListener(this);

        this.tranSaveBT = (Button) findViewById(R.id.add_trans_save_btn);
        this.tranSaveBT.setOnClickListener(this);

        this.tranCostBT = (Button) findViewById(R.id.cost_btn);
        this.tranCostBT.setOnClickListener(this);

        findViewById(R.id.pick_photo_btn).setOnClickListener(this);
        findViewById(R.id.add_desc_cancel_btn).setOnClickListener(this);
        findViewById(R.id.add_trans_save_and_new_btn).setOnClickListener(this);
        ((TextView) findViewById(R.id.titlebar_right_btn)).setText("导入");
        findViewById(R.id.titlebar_right_btn).setOnClickListener(this);
    }

    private void initCombobox()
    {
        List<String> list_types = new ArrayList<String>();
        this.typeMap.put(OUT_MODEL_NAME, OUT_MODEL);
        list_types.add(OUT_MODEL_NAME);
        this.typeMap.put(IN_MODEL_NAME, IN_MODEL);
        list_types.add(IN_MODEL_NAME);
        this.typeMap.put(EQU_MODEL_NAME, EQU_MODEL);
        list_types.add(EQU_MODEL_NAME);

        this.m_combobox = (ComboBox) findViewById(R.id.id_combobox);
        this.m_combobox.setData(list_types);

        this.m_combobox
                .setListViewOnClickListener(new ListViewItemClickListener()
                {

                    @Override
                    public void onItemClick(int position)
                    {
                        switch (position)
                        {
                        case 0:
                            reSetCategoryTV(defaultOutLevelName + "-->"
                                    + defaultOutDescTypeName);
                            curTradeType = defaultOutTradeType;
                            curDescTradeType = defaultOutDescTradeType;
                            break;
                        case 1:
                            reSetCategoryTV(defaultInLevelName + "-->"
                                    + defaultInDescTypeName);
                            curTradeType = defaultInTradeType;
                            curDescTradeType = defaultInDescTradeType;
                            break;
                        case 2:
                            reSetCategoryTV("内部转账");
                            break;
                        default:
                            break;
                        }
                    }

                });
    }

    private void initEtAndTv()
    {
        this.memoET = (EditText) findViewById(R.id.memo_et);
        this.categoryTV = (TextView) findViewById(R.id.category_name_tv);
        this.categoryTV.setOnClickListener(this);
    }

    private void setViewsInCurrModel()
    {
        if (this.isAdd)
        {
            this.tradeTimeBT.setText(BasicDateUtil.getCurrentDateTimeString());
            reSetCategoryTV(this.defaultOutLevelName + "-->"
                    + this.defaultOutDescTypeName);
            this.tradeMemberBT.setText(Setting.getLastMember());
            this.tranCostBT.setText("0.0");

            showMoneyPanel();
        }
        else
        {
            ((TextView) findViewById(R.id.titlebar_right_btn))
                    .setVisibility(View.INVISIBLE);
            findViewById(R.id.add_trans_save_and_new_btn).setVisibility(
                    View.GONE);
            setViewsByExistTrade(modifyTrade);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.back_btn)
        {
            finish();
        }
        else if (v.getId() == R.id.category_name_tv)
        {
            showCostTypeWheel();
        }
        else if (v.getId() == R.id.member_row_btn)
        {
            showMembersMenu();
        }
        else if (v.getId() == R.id.add_trans_save_btn)
        {
            addOrEdit();
        }
        else if (v.getId() == R.id.pick_photo_btn)
        {
            showPicMenu();
        }
        else if (v.getId() == R.id.add_desc_cancel_btn)
        {
            rollbackMemo();
        }
        else if (v.getId() == R.id.cost_btn)
        {
            showMoneyPanel();
        }
        else if (v.getId() == R.id.trade_time_row_btn)
        {
            showDateTimeWheel();
        }
        else if (v.getId() == R.id.add_trans_save_and_new_btn)
        {
            oneMoreRecord();
        }
        else if (v.getId() == R.id.titlebar_right_btn)
        {
            importDialogOpen();
        }
    }

    private void share()
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(
                Intent.EXTRA_STREAM,
                Uri.fromFile(new File(MyAppParams.getInstance().getTmpPicPath()
                        + "332680175.jpg")));

        share.setType("*/*");// 此处可发送多种文件

        startActivity(Intent.createChooser(share, "Share"));
    }

    protected void importDialogOpen()
    {

        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        dlg.getWindow().setGravity(Gravity.CENTER);
        dlg.getWindow().setLayout((int) (MyAppParams.getScreenWidth() * 0.8),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
        TextView add_tag_dialg_title = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_title);

        EditText add_tag_dialg_content = (EditText) dlg
                .findViewById(R.id.add_tag_dialg_content);
        add_tag_dialg_content.setEnabled(false);

        TextView add_tag_dialg_no = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_no);
        TextView add_tag_dialg_ok = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_ok);
        add_tag_dialg_title.setText("导入消费记录");

        add_tag_dialg_no.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dlg.dismiss();
            }
        });

        Trade parseTrade = null;
        try
        {
            parseTrade = getParsedTradeFromClipboard();
            add_tag_dialg_content.setText("时间:" + parseTrade.getCreatetime());
            add_tag_dialg_content.append(" 金额:" + parseTrade.getCost());
            add_tag_dialg_content.append(" 成员:" + parseTrade.getMember());
            final Trade parseTrade2 = parseTrade;
            add_tag_dialg_ok.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if (parseTrade2 != null)
                    {
                        // importTrade(parseTrade2);
                        try
                        {
                            TransactionsDao.initDb(getApplicationContext());
                            if (complictTrade(parseTrade2.getUuid()))
                            {
                                ToastUtil.showLongToast(
                                        getApplicationContext(), "该记录已经保存过!");
                            }
                            else
                            {
                                setViewsByExistTrade(parseTrade2);
                            }
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        finally
                        {
                            TransactionsDao.closeDb();
                        }
                    }
                    dlg.dismiss();
                }

                private boolean complictTrade(String uuid)
                {
                    // TODO 是否在数据库中重复了uuid
                    return TransactionsDao.checkTradeUUIDExist(uuid);
                }
            });
        }
        catch (Exception ex)
        {
            add_tag_dialg_content.setText("你复制的内容无效!");
            ToastUtil.showShortToast(getApplicationContext(), "记录格式不对!");
        }
    }

    private void rollbackMemo()
    {
        memoET.setText(lastMemoContent);
    }

    private void addOrEdit()
    {
        Trade trade = getTradeFromViews();

        try
        {
            if (checkBeforeSave(trade))
            {
                TransactionsDao.initDb(this);
                boolean b = false;
                if (isAdd)
                {
                    if (b = TransactionsDao.insert(trade))
                    {
                        Log.i("addTrade", getTradeJson(trade));
                        lockView();
                    }
                }
                else
                {
                    Log.i("updateTrade", "before:" + getTradeJson(modifyTrade));
                    if (b = TransactionsDao.update(trade))
                    {
                        Log.i("updateTrade", "after:" + getTradeJson(trade));
                        finish();
                    }
                }

                if (b)
                {
                    ToastUtil.showShortToast(this, "保存成功!");
                    Setting.setMember(getMember());
                    if (trade.getHaspicture() == 1)
                    {
                        backupFilesInTrade(trade);
                    }
                }
                else
                {
                    ToastUtil.showShortToast(this, "保存失败!");
                }
                TransactionsDao.closeDb();
            }
        }
        catch (Exception ex)
        {
            Log.e("saveErr", ex.getMessage());
            ToastUtil.showLongToast(this, "保存失败,出现异常!");
        }
    }

    private void oneMoreRecord()
    {
        if (this.isAdd)
        {
            this.tranCostBT.setText("0.0");
            showMoneyPanel();
            this.memoET.setText("");
            // this.tradeTimeBT.setText(BasicDateUtil.getCurrentDateTimeString());
            unlockView();
        }
    }

    private void showPicMenu()
    {
        AlertDialog.Builder builder = new Builder(this);

        builder.setItems(this.picMenu, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                if (arg1 == 0)
                {
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED))
                    {
                        mPhotoFile = new File(savePicDir, BasicDateUtil
                                .getCurrentDateTimeString() + ".jpg");
                        mPhotoFile.delete();
                        if (!mPhotoFile.exists())
                        {
                            try
                            {
                                mPhotoFile.createNewFile();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                                Toast.makeText(getApplication(), "照片创建失败!",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        Intent intent = new Intent(
                                "android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(mPhotoFile));
                        startActivityForResult(intent, CAMERA_RESULT);
                    }
                    else
                    {
                        Toast.makeText(getApplication(), "sdcard无效或没有插入!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else if (arg1 == 1)
                {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, LOAD_IMAGE_RESULT);
                }
                else if (arg1 == 2)
                {
                    Intent intent = new Intent(
                            MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    startActivityForResult(intent, VOICE_RESULT);
                }
                else if (arg1 == 3)
                {
                    Intent intent = new Intent(AddOrEditTransActivity.this,
                            MovieActivity.class);
                    startActivityForResult(intent, VIDEO_RESULT);
                }
                arg0.dismiss();
            }
        });
        builder.show();
    }

    private void showMembersMenu()
    {
        AlertDialog.Builder builder = new Builder(this);

        builder.setItems(this.members, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                tradeMemberBT.setText(members[arg1]);
                arg0.dismiss();
            }
        });
        builder.show();
    }

    private void showDateTimeWheel()
    {
        WheelDialogShowUtil.showCurrDateTimeDialog(this, this.tradeTimeBT
                .getText().toString(), new AfterWheelChooseListener()
        {

            @Override
            public void afterWheelChoose(Object... objs)
            {

                String day = objs[0].toString().substring(0, 10);
                if (!BasicDateUtil.isDateFormatTimeString(day, "yyyy-MM-dd"))
                {
                    ToastUtil.showLongToast(getApplicationContext(), "请检查是否有"
                            + TextFormatUtil.addChnToDay(day) + "这一天!");
                }
                else
                {
                    tradeTimeBT.setText(objs[0].toString());
                }

            }

        });
    }

    private void showCostTypeWheel()
    {
        List<MyWheelBean> leftData = null;
        Map<MyWheelBean, List<? extends MyWheelBean>> rightData = null;

        if (getSelRecordTypeId() == OUT_MODEL)
        {
            leftData = this.outTradeTypes;
            rightData = this.outTradeDescTypes;
        }
        else if (getSelRecordTypeId() == IN_MODEL)
        {
            leftData = this.inTradeTypes;
            rightData = this.inTradeDescTypes;
        }
        else if (getSelRecordTypeId() == EQU_MODEL)
        {
            return;
        }
        final List<MyWheelBean> leftData2 = leftData;
        final Map<MyWheelBean, List<? extends MyWheelBean>> rightData2 = rightData;
        WheelDialogShowUtil.showSelectDialog(this, "选择分类", leftData, rightData,
                selectedLeftIndex, selectedRightIndex,
                new AfterWheelChooseListener()
                {
                    @Override
                    public void afterWheelChoose(Object... objs)
                    {
                        selectedLeftIndex = Integer.valueOf(objs[0].toString());
                        selectedRightIndex = Integer.valueOf(objs[1].toString());
                        MyWheelBean lbean = leftData2.get(selectedLeftIndex);
                        MyWheelBean rbean = rightData2.get(lbean).get(
                                selectedRightIndex);
                        curTradeType = lbean.getId();
                        curDescTradeType = rbean.getId();

                        reSetCategoryTV(lbean.getName() + "-->"
                                + rbean.getName());

                    }

                });

    }

    private void showMoneyPanel()
    {
        Intent localIntent = new Intent(AddOrEditTransActivity.this,
                CostKeyboardActivity.class);
        localIntent.putExtra("cost", tranCostBT.getText().toString());
        startActivityForResult(localIntent, COST_PANEL_RESULT);
    }

    private String getTradeJson(Trade trade)
    {
        return JSON.toJSONString(trade);
    }

    private Trade getParsedTradeFromClipboard()
    {
        return JSONArray.parseObject(ClipBoardUtil.getClipBoardContent(this),
                Trade.class);
    }

    private boolean checkBeforeSave(Trade trade)
    {
        if (trade.getType_id() == EQU_MODEL)
        {
            ToastUtil.showShortToast(this, EQU_MODEL_NAME + "模式暂不支持");
            return false;
        }
        if (trade.getCost() <= 0)
        {
            ToastUtil.showLongToast(this, "请输入一个大于 0 的金额!");
            return false;
        }
        if (!Arrays.asList(members).contains(trade.getMember()))
        {
            ToastUtil.showLongToast(this, "[" + trade.getMember() + "]该成员无效!");
            return false;
        }
        return true;
    }

    private void backupFilesInTrade(Trade trade)
    {
        for (String segment : TextFormatUtil.getSegmentsInMemo(trade.getMemo()))
        {
            if (FileTypeUtil.isPicFile(segment.trim().toLowerCase()))
            {
                BackupFilesHolder.addBackupFile(MyAppParams.getInstance()
                        .getTmpPicPath() + segment);
            }
            else if (FileTypeUtil.isVoiceFile(segment.trim().toLowerCase()))
            {
                BackupFilesHolder.addBackupFile(MyAppParams.getInstance()
                        .getTmpVoicePath() + segment);
            }
            else if (FileTypeUtil.isVideoFile(segment.trim().toLowerCase()))
            {
                BackupFilesHolder.addBackupFile(MyAppParams.getInstance()
                        .getTmpVideoPath() + segment);
            }
        }
    }

    public void unlockView()
    {
        tranSaveBT.setEnabled(true);
        tranCostBT.setEnabled(true);
    }

    private void lockView()
    {
        tranSaveBT.setEnabled(false);
        tranCostBT.setEnabled(false);
    }

    private void setViewsByExistTrade(Trade existTrade)
    {

        this.tradeTimeBT.setText(BasicDateUtil.getDateTimeString(TextFormatUtil
                .getFormatedDate(existTrade.getCreatetime())));
        reSetCategoryTV(CostTypeUtil.getCostTypeName(existTrade
                .getCostlevel_id())
                + "-->"
                + CostTypeUtil.getCostTypeName(existTrade.getCostdesc_id()));
        this.tradeMemberBT.setText(existTrade.getMember());
        this.tranCostBT.setText(existTrade.getCost() + "");
        this.memoET.setText(existTrade.getMemo());
        switch (existTrade.getType_id())
        {
        case OUT_MODEL:
            this.m_combobox.setText(OUT_MODEL_NAME);
            break;
        case IN_MODEL:
            this.m_combobox.setText(IN_MODEL_NAME);
            break;
        case EQU_MODEL:
            this.m_combobox.setText(EQU_MODEL_NAME);
            break;
        }
        this.curTradeType = existTrade.getCostlevel_id();
        this.curDescTradeType = existTrade.getCostdesc_id();

        for (int i = 0; i < outTradeTypes.size(); i++)
        {
            if (outTradeTypes.get(i).getId() == curTradeType)
            {
                selectedLeftIndex = i;
                List<? extends MyWheelBean> list = outTradeDescTypes
                        .get(outTradeTypes.get(i));
                for (int j = 0; j < list.size(); j++)
                {
                    if (list.get(j).getId() == curDescTradeType)
                    {
                        selectedRightIndex = j;
                    }
                }
            }
        }
    }

    protected void importTrade(Trade parseTrade)
    {
        parseTrade.setCreatelongtime(TextFormatUtil.getFormatedDate(
                parseTrade.getCreatetime()).getTime()
                + "");
        if (checkBeforeSave(parseTrade))
        {
            TransactionsDao.initDb(this);
            Log.i("updateTrade", JSON.toJSONString(parseTrade));
            if (TransactionsDao.insert(parseTrade))
            {
                ToastUtil.showShortToast(this, "导入成功!");
                lockView();
            }
            else
            {
                ToastUtil.showShortToast(this, "导入失败!");
            }
        }
    }

    private Trade getTradeFromViews()
    {
        Trade trade = new Trade();
        if (modifyTrade != null)
        {
            trade.setId(modifyTrade.getId());
        }
        trade.setCost(getCost());
        trade.setType_id(getSelRecordTypeId());
        trade.setCostlevel_id(this.curTradeType);
        trade.setCostdesc_id(this.curDescTradeType);
        String timeStr = this.tradeTimeBT.getText().toString();
        trade.setCreatetime(TextFormatUtil.getDateStrForDb(timeStr));
        trade.setModifytime(TextFormatUtil.getDateStrForDb(BasicDateUtil
                .getCurrentDateTimeString()));
        trade.setCreatelongtime(""
                + TextFormatUtil.getFormatedDate(timeStr).getTime());

        trade.setMember(getMember());

        String memo = getMemo();
        trade.setMemo(memo);
        if (memo.contains("[") && memo.contains("]"))
        {
            trade.setHaspicture(1);
        }
        else
        {
            trade.setHaspicture(0);
        }
        trade.setUuid(GeneratorUtil.getUUID());
        return trade;
    }

    private String getMemo()
    {
        return this.memoET.getText().toString();
    }

    private String getMember()
    {
        return this.tradeMemberBT.getText().toString();
    }

    private double getCost()
    {
        return Double.parseDouble(this.tranCostBT.getText().toString());
    }

    public int getSelRecordTypeId()
    {
        return this.typeMap.get(this.m_combobox.getText());
    }

    private void reSetCategoryTV(String category)
    {
        this.categoryTV.setText(category.replace(" ", ""));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.CAMERA_RESULT && resultCode == RESULT_OK)
        {
            System.out.println("camera OK!");
            if (this.mPhotoFile != null && this.mPhotoFile.exists())
            {
                System.out.println("mPhotoFile::"
                        + this.mPhotoFile.getAbsolutePath());
                insertFileToMemo(this.mPhotoFile.getName());

            }
            else
            {
                System.out.println("img Err!");
            }
        }
        if (requestCode == this.LOAD_IMAGE_RESULT && resultCode == RESULT_OK
                && null != data)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn =
            { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            System.out.println("picturePath::" + picturePath);
            backupAndInsertMemo(savePicDir, picturePath, ".jpg");
        }

        if (requestCode == this.COST_PANEL_RESULT && data != null)
        {
            double cost = data.getDoubleExtra("cost", 0d);
            this.tranCostBT.setText(cost + "");
        }

        if (requestCode == this.VOICE_RESULT && data != null)
        {
            try
            {
                File amrFile = UriUtil.getFileByUri(data.getData(), this);
                if (amrFile != null)
                {
                    System.out.println("audio file:"
                            + amrFile.getAbsolutePath());
                    backupAndInsertMemo(saveVoiceDir,
                            amrFile.getAbsolutePath(), ".amr");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (requestCode == this.VIDEO_RESULT && data != null)
        {
            String moviePath = data.getStringExtra(MovieActivity.RECORDED_PATH);
            System.out.println("返回的moviePath:" + moviePath);
            if (BasicFileUtil.isExistFile(moviePath))
            {
                insertFileToMemo(BasicFileUtil.getFileName(moviePath));
            }
        }
    }

    /*
     * 将备注的文件拷贝一遍
     * 
     * @param moviePath
     */
    private void backupAndInsertMemo(String saveDir, String memoFilePath,
            String suffixType)
    {
        System.out.println("备注文件:" + memoFilePath);
        String fileName = BasicFileUtil.getFileName(memoFilePath);
        String destPicPath = saveDir + fileName;
        if (BasicFileUtil.isExistFile(destPicPath))
        {
            fileName = BasicDateUtil.getCurrentDateTime() + suffixType;
            destPicPath = saveDir + fileName;
        }
        if (!BasicFileUtil.CopyFile(memoFilePath, destPicPath))
        {
            fileName = "";
            ToastUtil.showLongToast(this, "拷贝过程中出错!destPath: " + destPicPath);
        }
        else
        {
            insertFileToMemo(new File(destPicPath).getName());
        }
    }

    private void insertFileToMemo(String imgPath)
    {
        if (BasicStringUtil.isNullString(imgPath))
        {
            return;
        }

        imgPath = " [" + imgPath + "] ";
        try
        {
            // 获得光标的位置
            int index = this.memoET.getSelectionStart();
            // 将字符串转换为StringBuffer
            StringBuffer sb = new StringBuffer(this.memoET.getText().toString());
            this.lastMemoContent = sb.toString();
            // 将字符插入光标所在的位置
            sb = sb.insert(index, imgPath);
            this.memoET.setText(sb.toString());
            // 设置光标的位置保持不变
            Selection.setSelection(
                    this.memoET.getText(),
                    Math.min(index + imgPath.length(),
                            sb.length() + imgPath.length()));
        }
        catch (Exception ex)
        {
            ToastUtil.showShortToast(this, "下标操作错误! " + ex.getMessage());
        }
    }

}
