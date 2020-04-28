package ddr.example.com.newretailandroidclient.entity.other;

public class HuoRecord {
    private String huoNum;//货物情况
    private String huoName;//货物名称
    private String huoUrl;//局域网货物图片地址
    private String huoWurl;//广域网图片地址
    private String huoID; //货物ID索引
    private String bitma;//货物实际地址

    public String getHuoNum() {
        return huoNum;
    }

    public void setHuoNum(String huoNum) {
        this.huoNum = huoNum;
    }

    public String getHuoName() {
        return huoName;
    }

    public void setHuoName(String huoName) {
        this.huoName = huoName;
    }

    public String getHuoUrl() {
        return huoUrl;
    }

    public void setHuoUrl(String huoUrl) {
        this.huoUrl = huoUrl;
    }

    public String getHuoID() {
        return huoID;
    }

    public void setHuoID(String huoID) {
        this.huoID = huoID;
    }

    public String getBitma() {
        return bitma;
    }

    public void setBitma(String bitma) {
        this.bitma = bitma;
    }

    public String getHuoWurl() {
        return huoWurl;
    }

    public void setHuoWurl(String huoWurl) {
        this.huoWurl = huoWurl;
    }
}
