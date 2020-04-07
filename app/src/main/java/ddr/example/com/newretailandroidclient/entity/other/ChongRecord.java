package ddr.example.com.newretailandroidclient.entity.other;

public class ChongRecord {
    private String id;//id
    private String cd_order;//充电指令
    private String dj_result;//对接结果
    private String start_cd_time;//开始充电时间
    private String start_cd_num;//开始充电电量
    private String end_cd_time;//结束充电时间
    private String ens_cd_num;//结束充电电量
    private String cd_h_time;//充电耗时

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCd_order() {
        return cd_order;
    }

    public void setCd_order(String cd_order) {
        this.cd_order = cd_order;
    }

    public String getDj_result() {
        return dj_result;
    }

    public void setDj_result(String dj_result) {
        this.dj_result = dj_result;
    }

    public String getStart_cd_time() {
        return start_cd_time;
    }

    public void setStart_cd_time(String start_cd_time) {
        this.start_cd_time = start_cd_time;
    }

    public String getStart_cd_num() {
        return start_cd_num;
    }

    public void setStart_cd_num(String start_cd_num) {
        this.start_cd_num = start_cd_num;
    }

    public String getEnd_cd_time() {
        return end_cd_time;
    }

    public void setEnd_cd_time(String end_cd_time) {
        this.end_cd_time = end_cd_time;
    }

    public String getEns_cd_num() {
        return ens_cd_num;
    }

    public void setEns_cd_num(String ens_cd_num) {
        this.ens_cd_num = ens_cd_num;
    }

    public String getCd_h_time() {
        return cd_h_time;
    }

    public void setCd_h_time(String cd_h_time) {
        this.cd_h_time = cd_h_time;
    }
}
