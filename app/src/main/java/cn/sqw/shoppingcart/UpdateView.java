package cn.sqw.shoppingcart;

/**
 * 更新数据的回调接口
 */
public interface UpdateView {
    void update(boolean isAllSelected, int count, int price);
}
