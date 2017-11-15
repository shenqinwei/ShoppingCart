package cn.sqw.shoppingcart;

import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements UpdateView, View.OnClickListener {

    private ExpandableListView mExpandableListView;
    private SmoothCheckBox mCbSelectAll;
    private TextView mTvAllMoney;
    private Button mBtnBuy;
    StringBuffer stringBuffer;
    GoodBean goodBean;
    ExpandableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mCbSelectAll.setOnClickListener(this);
    }

    private void initData() {
        //读取数据解析
        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open("data.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            stringBuffer = new StringBuffer();
            String str;
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        goodBean = gson.fromJson(stringBuffer.toString(), GoodBean.class);
        mAdapter = new ExpandableListAdapter(this, goodBean);
        mAdapter.setChangedListener(this);
        mExpandableListView.setAdapter(mAdapter);
        //展开所有的分组
        for (int i = 0; i < goodBean.getContent().size(); i++) {
            mExpandableListView.expandGroup(i);
        }
    }

    private void initView() {
        mExpandableListView = (ExpandableListView) findViewById( R.id.expandableListView);
        mCbSelectAll = (SmoothCheckBox) findViewById( R.id.cb_select_all);
        mTvAllMoney = (TextView) findViewById( R.id.tv_all_money);
        mBtnBuy = (Button) findViewById( R.id.btn_settlement);
        //去掉ExpandableListView 默认的箭头
        mExpandableListView.setGroupIndicator(null);

        //用于列表滑动时，EditText清除焦点，收起软键盘
        mExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity
                            .INPUT_METHOD_SERVICE);
                    View focusView = getCurrentFocus();
                    if (focusView != null) {
                        inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager
                                .HIDE_NOT_ALWAYS);
                        focusView.clearFocus();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

    }

    @Override
    public void update(boolean isAllSelected, int count, int price) {
        mBtnBuy.setText("结算(" + count + ")");
        mTvAllMoney.setText("¥" + price);
        mCbSelectAll.setChecked(isAllSelected);
    }

    @Override
    public void onClick(View view) {
        selectAll();
    }

    private void selectAll() {
        int allCount = goodBean.getAllCount();
        int allMoney = goodBean.getAllMoney();
        if (!mCbSelectAll.isChecked()) {
            goodBean.setAllSelect(true);
            for (int i = 0; i < goodBean.getContent().size(); i++) {
                goodBean.getContent().get(i).setIsSelected(true);
                for (int n = 0; n < goodBean.getContent().get(i).getGoodDetail().size(); n++) {
                    if (!goodBean.getContent().get(i).getGoodDetail().get(n).isSelected()) {
                        allCount++;
                        allMoney += Integer.valueOf(goodBean.getContent().get(i).getGoodDetail().get(n).getCount())
                                * Integer.valueOf(goodBean.getContent().get(i).getGoodDetail().get(n).getPrice());
                        goodBean.getContent().get(i).getGoodDetail().get(n).setIsSelected(true);
                    }
                }
            }
        } else {
            goodBean.setAllSelect(false);
            for (int i = 0; i < goodBean.getContent().size(); i++) {
                goodBean.getContent().get(i).setIsSelected(false);
                for (int n = 0; n < goodBean.getContent().get(i).getGoodDetail().size(); n++) {
                    goodBean.getContent().get(i).getGoodDetail().get(n).setIsSelected(false);
                }
                allCount = 0;
                allMoney = 0;
            }
        }
        goodBean.setAllMoney(allMoney);
        goodBean.setAllCount(allCount);
        update(goodBean.isAllSelect(), allCount, allMoney);
        mAdapter.notifyDataSetChanged();
    }
}
