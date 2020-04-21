package ddr.example.com.newretailandroidclient.entity.other;

import java.util.List;

public class ErrorRecordS {
    public static ErrorRecordS errorRecordS;
    private List<ErrorRecord> errorRecordList;
    private int countNum;
    private int postion;
    public static ErrorRecordS getInstance(){
        if (errorRecordS==null){
            synchronized (ErrorRecordS.class){
                if (errorRecordS==null){
                    errorRecordS=new ErrorRecordS();
                }
            }
        }
        return errorRecordS;
    }

    public List<ErrorRecord> getErrorRecordList() {
        return errorRecordList;
    }

    public void setErrorRecordList(List<ErrorRecord> errorRecordList) {
        this.errorRecordList = errorRecordList;
    }

    public int getCountNum() {
        return countNum;
    }

    public void setCountNum(int countNum) {
        this.countNum = countNum;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }
}
