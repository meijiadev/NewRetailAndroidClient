package ddr.example.com.newretailandroidclient.entity.other;

public class MapRecordClick {
    private String start_time;//开始时间
    private String end_time;//结束时间
    private String run_time;//运行时长
    private String retail_map;//售卖地图
    private String retail_num;//售卖次数
    private String handle;//操作
    private float posX;//X坐标
    private float posY;//Y坐标
    private String result;//结果
    private String name;//目标点名字

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRun_time() {
        return run_time;
    }

    public void setRun_time(String run_time) {
        this.run_time = run_time;
    }

    public String getRetail_map() {
        return retail_map;
    }

    public void setRetail_map(String retail_map) {
        this.retail_map = retail_map;
    }

    public String getRetail_num() {
        return retail_num;
    }

    public void setRetail_num(String retail_num) {
        this.retail_num = retail_num;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
