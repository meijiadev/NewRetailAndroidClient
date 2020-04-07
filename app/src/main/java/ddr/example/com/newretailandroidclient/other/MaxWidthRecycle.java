package ddr.example.com.newretailandroidclient.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import ddr.example.com.newretailandroidclient.R;

public class MaxWidthRecycle extends RecyclerView {
    private int mMaxWidth;

    public MaxWidthRecycle(Context context) {
        super(context);
    }

    public MaxWidthRecycle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public MaxWidthRecycle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MaxWidthRecycle);
        mMaxWidth = arr.getLayoutDimension(R.styleable.MaxWidthRecycle_maxWidth, mMaxWidth);
        arr.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxWidth > 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

