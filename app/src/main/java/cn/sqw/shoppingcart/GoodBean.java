package cn.sqw.shoppingcart;

import java.util.List;

public class GoodBean {

    private List<ContentBean> content;
    private int allMoney;
    private int allCount;
    private boolean isAllSelect;

    public boolean isAllSelect() {
        return isAllSelect;
    }

    public void setAllSelect(boolean allSelect) {
        isAllSelect = allSelect;
    }

    public int getAllMoney() {
        return allMoney;
    }

    public void setAllMoney(int allMoney) {
        this.allMoney = allMoney;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        private String id;
        private String address;
        private boolean isSelected;

        private List<GoodDetailBean> goodDetail;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public void setAdress(String address) {
            this.address = address;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public List<GoodDetailBean> getGoodDetail() {
            return goodDetail;
        }

        public void setGoodDetail(List<GoodDetailBean> goodDetail) {
            this.goodDetail = goodDetail;
        }

        public static class GoodDetailBean {
            private String id;
            private String pic;
            private String count;
            private String name;
            private String price;
            private boolean isEdit;
            private boolean isSelected;

            public boolean isEdit() {
                return isEdit;
            }

            public boolean isSelected() {
                return isSelected;
            }

            public void setIsSelected(boolean isSelected) {
                this.isSelected = isSelected;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }


            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public boolean isIsedit() {
                return isEdit;
            }

            public void setIsEdit(boolean isEdit) {
                this.isEdit = isEdit;
            }
        }
    }
}
