package com.azlll.framework.ui.browser.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.azlll.framework.R;

public class SimpleToolbar extends Toolbar {
    private ImageView ivBack;//左边返回上一页按钮
    private ImageView ivClose;//左边关闭按钮
    private TextView tvTitle;//中间标题
    private ImageView ivFunction;//右边按钮

    public SimpleToolbar(Context context) {
        super(context);
        initView(context);
    }

    public SimpleToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SimpleToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.common_simple_toolbar, this);
        ivBack = findViewById(R.id.iv_back);
        ivClose = findViewById(R.id.iv_close);
        tvTitle = findViewById(R.id.tv_title);
        ivFunction = findViewById(R.id.iv_function);
    }

    //设置中间title的内容
    public void setMainTitle(String text) {
        this.setTitle(" ");
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(text);
    }
    public TextView getMainTitle() {
        return tvTitle;
    }
    public ImageView getIvBack(){
        return ivBack;
    }
    public ImageView getIvClose(){
        return ivClose;
    }
    public ImageView getIvFunction(){
        return ivFunction;
    }
   //设置中间title的内容文字的颜色
    public void setMainTitleColor(int color) {
        tvTitle.setTextColor(color);
    }
    //设置title左边图标
    public void setLeftIvBackDrawable(int res) {
        Drawable dwLeft = ContextCompat.getDrawable(getContext(), res);
        dwLeft.setBounds(0, 0, dwLeft.getMinimumWidth(), dwLeft.getMinimumHeight());
        ivBack.setImageDrawable(dwLeft);
    }
    public void setLeftIvBackBitmap (Bitmap bitmap) {
        Matrix matrix = new Matrix(); //接收图片之后放大 1.5倍
        matrix.postScale(3.5f, 3.5f);
        Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        ivBack.setImageBitmap(bit);
    }
    //设置title左边图标
    public void setLeftIvCloseDrawable(int res) {
        Drawable dwLeft = ContextCompat.getDrawable(getContext(), res);
        dwLeft.setBounds(0, 0, dwLeft.getMinimumWidth(), dwLeft.getMinimumHeight());
        ivClose.setImageDrawable(dwLeft);
    }
    public void setLeftIvCloseBitmap (Bitmap bitmap) {
        Matrix matrix = new Matrix(); //接收图片之后放大 1.5倍
        matrix.postScale(3.5f, 3.5f);
        Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        ivClose.setImageBitmap(bit);
    }
    //设置title右边图标
    public void setRightFunctionDrawable(int res) {
        Drawable dwLeft = ContextCompat.getDrawable(getContext(), res);
        dwLeft.setBounds(0, 0, dwLeft.getMinimumWidth(), dwLeft.getMinimumHeight());
        ivFunction.setImageDrawable(dwLeft);
    }
    public void setLeftFunctionBitmap (Bitmap bitmap) {
        Matrix matrix = new Matrix(); //接收图片之后放大 1.5倍
        matrix.postScale(3.5f, 3.5f);
        Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        ivFunction.setImageBitmap(bit);
    }
    //设置title左边图标点击事件
    public void setLeftIvBackClickListener(OnClickListener onClickListener){
        ivBack.setOnClickListener(onClickListener);
    }
    public void setLeftIvCloseClickListener(OnClickListener onClickListener){
        ivClose.setOnClickListener(onClickListener);
    }
    //设置title右边图标点击事件
    public void setLeftIvFunctionClickListener(OnClickListener onClickListener){
        ivFunction.setOnClickListener(onClickListener);
    }
}
