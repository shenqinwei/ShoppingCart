package cn.sqw.shoppingcart;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "ExpandableListAdapter";
    private Context mContext;
    private GoodBean goodBean;
    private UpdateView updateViewListener;
    protected static final int KEY_DATA = 0xFFF11133;

    public ExpandableListAdapter(Context context, GoodBean goodBean) {
        this.mContext = context;
        this.goodBean = goodBean;
    }

    @Override
    public int getGroupCount() {
        return goodBean.getContent().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return goodBean.getContent().get(groupPosition).getGoodDetail().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return goodBean.getContent().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return goodBean.getContent().get(groupPosition).getGoodDetail().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_shopingcart_group, parent, false);
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.cbGroupItem.setTag(groupPosition);
        holder.cbGroupItem.setOnClickListener(listener);
        holder.tvPosition.setText(goodBean.getContent().get(groupPosition).getAddress());
        //根据获取的状态设置是否被选中
        if (goodBean.getContent().get(groupPosition).isSelected()) {
            if (!holder.cbGroupItem.isChecked()) {
                holder.cbGroupItem.setChecked(true);
            }
        } else {
            holder.cbGroupItem.setChecked(false);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate( R.layout.item_shopingcart_child, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        String tag = groupPosition + "," + childPosition;
        holder.cbItem.setTag(tag);
        holder.tvReduce.setTag(tag);
        holder.tvAdd.setTag(tag);
        holder.imgDelete.setTag(tag);
        holder.imgIcon.setTag(tag);
        holder.cbItem.setOnClickListener(listener);
        holder.tvReduce.setOnClickListener(listener);
        //添加商品数量
        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = view.getTag().toString();
                String[] split;
                int groupId = 0;
                int childId = 0;
                int allCount = goodBean.getAllCount();
                int allMoney;
                if (tag.contains(",")) {
                    split = tag.split(",");
                    groupId = Integer.parseInt(split[0]);
                    childId = Integer.parseInt(split[1]);
                }
                String goodCount = goodBean.getContent().get(groupId).getGoodDetail().get(childId).getCount();
                goodBean.getContent().get(groupId).getGoodDetail().get(childId).setCount(addCount(goodCount));
                allMoney = goodBean.getAllMoney();
                if (goodBean.getContent().get(groupId).getGoodDetail().get(childId).isSelected()) {
                    allMoney += Integer.valueOf(goodBean.getContent().get(groupId).getGoodDetail().get(childId).getPrice());
                    updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                }
                goodBean.setAllMoney(allMoney);
                notifyDataSetChanged();
            }
        });

        holder.imgDelete.setOnClickListener(listener);
        //根据获取的状态设置是否被选中
        if (goodBean.getContent().get(groupPosition).getGoodDetail().get(childPosition).isSelected()) {
            holder.cbItem.setChecked(true);
        } else {
            holder.cbItem.setChecked(false);
        }
        //设置数据
        holder.tvPrice.setText("¥" + goodBean.getContent().get(groupPosition).getGoodDetail().get(childPosition).getPrice());
        holder.tvGoodName.setText(goodBean.getContent().get(groupPosition).getGoodDetail().get(childPosition).getName());
        //对商品数量的监听
        EditTextWatcher textWatcher = (EditTextWatcher) holder.etCount.getTag(KEY_DATA);
        if (textWatcher != null) {
            holder.etCount.removeTextChangedListener(textWatcher);
        }
        holder.etCount.setText(String.valueOf(goodBean.getContent().get(groupPosition).getGoodDetail().get(childPosition).getCount()));
        EditTextWatcher watcher = new EditTextWatcher(goodBean.getContent().get(groupPosition).getGoodDetail().get(childPosition));
        holder.etCount.setTag(KEY_DATA, watcher);
        holder.etCount.addTextChangedListener(watcher);

        holder.etCount.setText(goodBean.getContent().get(groupPosition).getGoodDetail().get(childPosition).getCount());

        return convertView;

    }

    /**
     * 商品数量EditText内容改变的监听
     */
    class EditTextWatcher implements TextWatcher {

        private GoodBean.ContentBean.GoodDetailBean GoodDetail;

        public EditTextWatcher(GoodBean.ContentBean.GoodDetailBean item) {
            this.GoodDetail = item;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString().trim())) {
                String textNum = s.toString().trim();
                GoodDetail.setCount(textNum);
            }
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SmoothCheckBox checkBox;
            String tag = v.getTag().toString();
            String[] split;
            int groupId = 0;
            int childId = 0;
            int childSize = 0;
            int groupPosition = 0;
            int allCount = goodBean.getAllCount();//被选中的item数量
            int allMoney = goodBean.getAllMoney();
            if (tag.contains(",")) {
                split = tag.split(",");
                groupId = Integer.parseInt(split[0]);
                childId = Integer.parseInt(split[1]);
            } else {
                groupPosition = Integer.parseInt(tag);
                childSize = goodBean.getContent().get(groupPosition).getGoodDetail().size();
            }
            switch (v.getId()) {
                case  R.id.cb_group_item:
                    checkBox = (SmoothCheckBox) v;
                    //根据父checkbox的选中状态设置存储数据里面商品是否被选中
                    goodBean.getContent().get(groupPosition).setIsSelected(!checkBox.isChecked());
                    if (!checkBox.isChecked()) {
                        for (int i = 0; i < childSize; i++) {
                            if (!goodBean.getContent().get(groupPosition).getGoodDetail().get(i).isSelected()) {
                                allCount++;
                                goodBean.getContent().get(groupPosition).getGoodDetail().get(i).setIsSelected(!checkBox.isChecked());

                            }
                        }
                    } else {
                        allCount -= childSize;
                        for (int i = 0; i < childSize; i++) {
                            goodBean.getContent().get(groupPosition).getGoodDetail().get(i).setIsSelected(!checkBox.isChecked());

                        }
                    }
                  /*  //父item选中的数量
                    int fCount = 0;
                    //判断是否所有的父item都被选中，决定全选按钮状态
                    for (int i = 0; i < goodBean.getContent().size(); i++) {
                        if (goodBean.getContent().get(i).isSelected()) {
                            fCount++;
                        }
                    }
                    if (fCount == goodBean.getContent().size()) {
                        goodBean.setAllSelect(true);
                    } else {
                        goodBean.setAllSelect(false);
                    }
                    goodBean.setAllCount(allCount);*/
                    notifyDataSetChanged();
                    updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                    break;
                //单个子项item被点击
                case  R.id.cb_item:
                    checkBox = (SmoothCheckBox) v;
                    int cCount = 0;//子item被选中的数量
                    int fcCount = 0;//父item被选中的数量
                    goodBean.getContent().get(groupId).getGoodDetail().get(childId).setIsSelected(!checkBox.isChecked());
                    //遍历父item所有数据，统计被选中的item数量
                    for (int i = 0; i < goodBean.getContent().get(groupId).getGoodDetail().size(); i++) {
                        if (goodBean.getContent().get(groupId).getGoodDetail().get(i).isSelected()) {
                            cCount++;
                        }
                    }
                    //判断是否所有的子item都被选中，决定父item状态
                    if (cCount == goodBean.getContent().get(groupId).getGoodDetail().size()) {
                        goodBean.getContent().get(groupId).setIsSelected(true);
                    } else {
                        goodBean.getContent().get(groupId).setIsSelected(false);
                    }
                    //判断是否所有的父item都被选中，决定全选按钮状态
                    for (int i = 0; i < goodBean.getContent().size(); i++) {
                        if (goodBean.getContent().get(i).isSelected()) {
                            fcCount++;
                        }
                    }
                    if (fcCount == goodBean.getContent().size()) {
                        goodBean.setAllSelect(true);
                    } else {
                        goodBean.setAllSelect(false);
                    }
                 /*   //判断子item状态，更新结算总商品数和合计Money
                    if (!checkBox.isChecked()) {
                        allCount++;
                        allMoney += Integer.valueOf(goodBean.getContent().get(groupId).getGoodDetail().get(childId).getCount())
                                * Integer.valueOf(goodBean.getContent().get(groupId).getGoodDetail().get(childId).getPrice());
                    } else {
                        allCount--;
                        allMoney -= Integer.valueOf(goodBean.getContent().get(groupId).getGoodDetail().get(childId).getCount())
                                * Integer.valueOf(goodBean.getContent().get(groupId).getGoodDetail().get(childId).getPrice());
                    }
                    goodBean.setAllCount(allCount);
                    goodBean.setAllMoney(allMoney);*/
                    notifyDataSetChanged();
                    //updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                    break;
                case  R.id.tv_reduce:
                    //减少商品数量
                    String goodCount = goodBean.getContent().get(groupId).getGoodDetail().get(childId).getCount();
                    if (Integer.valueOf(goodCount) > 1) {
                        goodBean.getContent().get(groupId).getGoodDetail().get(childId).setCount(reduceCount(goodCount));
                        if (goodBean.getContent().get(groupId).getGoodDetail().get(childId).isSelected()) {
                            allMoney -= Integer.valueOf(goodBean.getContent().get(groupId).getGoodDetail().get(childId).getPrice());
                            updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                        }
                        goodBean.setAllMoney(allMoney);
                        notifyDataSetChanged();
                    }
                    break;
                case  R.id.img_delete:
                    goodBean.getContent().get(groupId).getGoodDetail().remove(childId);
                    if (goodBean.getContent().get(groupId).getGoodDetail().size() == 0) {
                        goodBean.getContent().remove(groupId);
                    }
                    notifyDataSetChanged();
                    break;
            }
        }
    };

    public void setChangedListener(UpdateView listener) {
        if (updateViewListener == null) {
            this.updateViewListener = listener;
        }
    }

    private String addCount(String var) {
        Integer integer = Integer.valueOf(var);
        integer++;
        return integer + "";
    }

    private String reduceCount(String var) {
        Integer integer = Integer.valueOf(var);
        if (integer > 1) {
            integer--;
        }
        return integer + "";
    }

    static class GroupViewHolder {
        SmoothCheckBox cbGroupItem;
        TextView tvPosition;

        GroupViewHolder(View view) {
            cbGroupItem = (SmoothCheckBox) view.findViewById( R.id.cb_group_item);
            tvPosition = (TextView) view.findViewById( R.id.tv_position);
        }
    }

    static class ChildViewHolder {
        SmoothCheckBox cbItem;
        TextView tvPrice,tvGoodName;
        EditText etCount;
        TextView tvReduce;
        TextView tvAdd;
        ImageView imgDelete;
        ImageView imgIcon;

        ChildViewHolder(View view) {
            cbItem = (SmoothCheckBox) view.findViewById( R.id.cb_item);
            tvPrice = (TextView) view.findViewById( R.id.tv_price);
            tvGoodName = (TextView) view.findViewById( R.id.tv_good_name);
            etCount = (EditText) view.findViewById( R.id.et_count);
            tvReduce = (TextView) view.findViewById( R.id.tv_reduce);
            tvAdd = (TextView) view.findViewById( R.id.tv_add);
            imgDelete = (ImageView) view.findViewById( R.id.img_delete);
            imgIcon = (ImageView) view.findViewById( R.id.img_icon);
        }
    }
}
