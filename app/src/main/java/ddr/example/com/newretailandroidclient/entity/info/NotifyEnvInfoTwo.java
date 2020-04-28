package ddr.example.com.newretailandroidclient.entity.info;

public class NotifyEnvInfoTwo {
    public static NotifyEnvInfoTwo notifyEnvInfo;

    public static NotifyEnvInfoTwo getInstance(){
        if (notifyEnvInfo==null){
            synchronized (NotifyEnvInfoTwo.class){
                if (notifyEnvInfo==null){
                    notifyEnvInfo=new NotifyEnvInfoTwo();
                }
            }
        }
        return notifyEnvInfo;
    }
    private float batt;
    private float temp;
    private float hum;
    private float pm25;
    private float co2;
    private float ch2o;


    public float getBatt() {
        return batt;
    }

    public void setBatt(float batt) {
        this.batt = batt;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getHum() {
        return hum;
    }

    public void setHum(float hum) {
        this.hum = hum;
    }

    public float getPm25() {
        return pm25;
    }

    public void setPm25(float pm25) {
        this.pm25 = pm25;
    }

    public float getCo2() {
        return co2;
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }

    public float getCh2o() {
        return ch2o;
    }

    public void setCh2o(float ch2o) {
        this.ch2o = ch2o;
    }

    public void setNull(){
        notifyEnvInfo=null;
    }
}
