package ddr.example.com.newretailandroidclient.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import ddr.example.com.newretailandroidclient.R;


/**
 * time: 2019/10/31
 * desc: 能改变状态字体颜色并带下划线的TextView
 */
@SuppressLint("AppCompatCustomView")
public class BasrTextView extends TextView {
    private int measureWidth, measureHeight;    //控件的宽高
    private boolean isSelected=false;
    private boolean isStitle=false;
    private Paint linePaint;

    public BasrTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        linePaint=new Paint();
        linePaint.setColor(Color.parseColor("#0399ff"));
        linePaint.setStrokeWidth(5);
    }

    public BasrTextView(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected){
            switch (typeView){
                case 0:
                    setTextColor(Color.parseColor("#0399ff"));
                    break;
                case 1:
                    setTextColor(Color.parseColor("#ffffff"));
                    setBackgroundResource(R.drawable.bt_bg_map_blue);
                    break;
            }
        }else {
            switch (typeView){
                case 0:
                    setTextColor(Color.parseColor("#ffffff"));
                    break;
                case 1:
                    setTextColor(Color.parseColor("#ccffffff"));
                    setBackgroundResource(R.drawable.bt_bg_text_n);
                    break;
            }

        }
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {

        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    public void isChecked(boolean isSelected){
        this.isSelected=isSelected;
        invalidate();
    }
    public void isStitle(boolean isStitle){
        this.isStitle=isStitle;
        invalidate();
    }
    private int typeView;
    public void setType(int type){
        switch (type){
            case 0:
                typeView=0;
                break;
            case 1:
                typeView=1;
                break;
        }
        invalidate();
    }

}
