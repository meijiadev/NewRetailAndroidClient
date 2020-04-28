package ddr.example.com.newretailandroidclient.entity.other;

public class RetailRecord {
    private String id;//id
    private String name;//商品名称
    private int Settlement;//结算方式
    private String price;//单价
    private String number;//数量
    private String total;//总价
    private String b_num;//结算编号
    private int count_num;//总页数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getB_num() {
        return b_num;
    }

    public void setB_num(String b_num) {
        this.b_num = b_num;
    }

    public int getCount_num() {
        return count_num;
    }

    public void setCount_num(int count_num) {
        this.count_num = count_num;
    }

    public int getSettlement() {
        return Settlement;
    }

    public void setSettlement(int settlement) {
        Settlement = settlement;
    }
}
