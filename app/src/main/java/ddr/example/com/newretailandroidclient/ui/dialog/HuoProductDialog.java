package ddr.example.com.newretailandroidclient.ui.dialog;

import android.graphics.Bitmap;

import android.view.Gravity;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import ddr.example.com.newretailandroidclient.R;
import ddr.example.com.newretailandroidclient.base.BaseDialog;
import ddr.example.com.newretailandroidclient.common.MyDialogFragment;

public final class HuoProductDialog {

    public static final class Builder
            extends MyDialogFragment.Builder<Builder>
            implements View.OnClickListener,
            BaseDialog.OnShowListener,
            BaseDialog.OnDismissListener {

        private HuoProductDialog.OnListener mListener;
        private boolean mAutoDismiss = true;

        private TextView tv_name;
        private ImageView iv_imageLan;
        private ImageView iv_imageWan;
        private TextView tv_description;
        private TextView tv_price;
        private TextView tv_startdata;
        private TextView tv_lifedata;
        private ImageView iv_quit;

        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_good_product);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setGravity(Gravity.CENTER);

            iv_imageLan = findViewById(R.id.iv_product);
            tv_name = findViewById(R.id.tv_p_name);
            tv_description = findViewById(R.id.tv_description);
            tv_price = findViewById(R.id.tv_p_price);
            tv_startdata = findViewById(R.id.tv_p_data);
            tv_lifedata = findViewById(R.id.tv_live_data);
            iv_quit = findViewById(R.id.iv_product_quit);

            iv_quit.setOnClickListener(this);

            addOnShowListener(this);
            addOnDismissListener(this);
        }

        public Builder setName(String name){
            tv_name.setText(name);
            return this;
        }

        public Builder setDescription(String des){
            tv_description.setText(des);
            return this;
        }

        public Builder setStartData(String sDara){
            tv_startdata.setText(sDara);
            return this;
        }

        public Builder setLiveDat(String liveDat){
            tv_lifedata.setText(liveDat);
            return this;
        }

        public Builder setPrice(String price){
            tv_price.setText(price);
            return this;
        }

        public Builder setImage(Bitmap bitmap){
            iv_imageLan.setImageBitmap(bitmap);
            return this;
        }


        /**
         * 判断是否自动弹窗消失
         *
         * @param dismiss
         * @return
         */
        public HuoProductDialog.Builder setAutoDismiss(boolean dismiss) {
            mAutoDismiss = dismiss;
            return this;
        }

        public HuoProductDialog.Builder setListener(HuoProductDialog.OnListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        public HuoProductDialog.Builder setThemeStyle(int id) {
            return super.setThemeStyle(id);
        }


        @Override
        public void onClick(View v) {
            if (mAutoDismiss) {
                dismiss();
            }

            if (mListener != null) {
                if (v == iv_quit) {
                    // 判断输入是否为空
                    mListener.onCancel(getDialog());
                }
            }
        }

        @Override
        public void onShow(BaseDialog dialog) {

        }

        @Override
        public void onDismiss(BaseDialog dialog) {

        }
    }
    public interface OnListener {

        /**
         * 点击确定时回调
         */
        void onConfirm(BaseDialog dialog, String content);

        /**
         * 点击取消时回调
         */
        void onCancel(BaseDialog dialog);
    }
}
