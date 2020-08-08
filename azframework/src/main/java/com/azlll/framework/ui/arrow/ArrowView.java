package com.azlll.framework.ui.arrow;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.azlll.framework.R;


/**
 * 箭头控件
 * 包含-> , > , >>
 * xmlns:app="http://schemas.android.com/apk/res-auto"
 * Created by zhul on 2016/11/23.
 */

public class ArrowView extends View {

    public ArrowView(Context context) {
        super(context);
        init(context, null);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private static final String TAG = "ArrowView";

    public static final int ARROW_TYPE_NODASH = 0;
    public static final int ARROW_TYPE_NODASH_DOUBLE = 1;
    public static final int ARROW_TYPE_DASH = 2;
    private int mArrowType = ARROW_TYPE_NODASH;

    public static final int ARROW_NODASH_GRAVITY_FORWARD = 1;
    public static final int ARROW_NODASH_GRAVITY_CENTER = 2;
    public static final int ARROW_NODASH_GRAVITY_BACKWARD = 3;
    private int mArrowNoDashGravity = ARROW_NODASH_GRAVITY_FORWARD;

    public static final int ARROW_WIDTH_DEFAULT_DP = 2;
    private int mArrowWidth = 4;
    private int mArrowWidthHalf = 2;
    private int mArrowWidthHalfExt = 1;

    public static final int ARROW_COLOR_DEFAULT = Color.argb(255, 174, 174, 174);
    private int mArrowColor = ARROW_COLOR_DEFAULT;

    public static final int ARROW_DIRECTION_LEFT = 1;
    public static final int ARROW_DIRECTION_UP = 2;
    public static final int ARROW_DIRECTION_RIGHT = 3;
    public static final int ARROW_DIRECTION_DOWN = 4;
    private int mArrowDirection = ARROW_DIRECTION_LEFT;


    private Paint mPaint;
    private Rect mRect;
    private Context context;
    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        if (null != attrs) {

            // 获取xml文件中的自定义属性
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArrowView);

            mArrowType = array.getInt(R.styleable.ArrowView_arrowType, ARROW_TYPE_NODASH);
            mArrowNoDashGravity = array.getInt(R.styleable.ArrowView_arrowNoDashGravity, ARROW_NODASH_GRAVITY_FORWARD);
            mArrowWidth = array.getDimensionPixelOffset(R.styleable.ArrowView_arrowWidth, ARROW_WIDTH_DEFAULT_DP);
            if(mArrowWidth % 2 !=0){
                mArrowWidth = mArrowWidth - 1;
            }
            mArrowWidthHalf = mArrowWidth / 2;
//            if(mArrowWidthHalf % 2 !=0){
//                mArrowWidthHalf = mArrowWidthHalf - 1;
//            }
            double ext = Math.sqrt(Double.valueOf(mArrowWidthHalf*mArrowWidthHalf));
            mArrowWidthHalfExt = (int)ext;

            mArrowColor = array.getColor(R.styleable.ArrowView_arrowColor, ARROW_COLOR_DEFAULT);
            mArrowDirection = array.getInt(R.styleable.ArrowView_arrowDirection, ARROW_DIRECTION_LEFT);

            array.recycle();


//            Log.d(TAG, "mArrowType = " + mArrowType);
//            Log.d(TAG, "mArrowNoDashGravity = " + mArrowNoDashGravity);
//            Log.d(TAG, "mArrowWidth = " + mArrowWidth);
//            Log.d(TAG, "mArrowWidthHalf = " + mArrowWidthHalf);
//            Log.d(TAG, "mArrowWidthHalfExt = " + mArrowWidthHalfExt);
//            Log.d(TAG, "mArrowColor = " + Color.red(mArrowColor) + "," + Color.green(mArrowColor) + "," +  + Color.blue(mArrowColor));
//            Log.d(TAG, "mArrowDirection = " + mArrowDirection);
        }
    }

    public void setArrowColor(int color){
        mArrowColor = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getWidth() <= 0 || getHeight() <= 0){
            // 控件没初始化完毕，或者设置的宽高太小，不绘制
            return;
        }
        // 初始化画笔等等
        if(mPaint == null){
            mPaint = new Paint();
        }
//        mPaint.setAntiAlias(true);
        mPaint.setColor(mArrowColor);
        mPaint.setStrokeWidth(mArrowWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        // 计算可绘图区域，最后加上线宽的一半，为了把斜线的边角画出来不被切掉
        int left = 0 + getPaddingLeft() + mArrowWidthHalf;
        int top = 0 + getPaddingTop() + mArrowWidthHalf;
        int right = getWidth() - getPaddingRight() - mArrowWidthHalf;
        int bottom = getHeight() - getPaddingBottom() - mArrowWidthHalf;
        int width = right - left;
        int height = bottom - top;
        boolean isWidthEqualsHeight = width == height;
        boolean isWidthBiggerThanHeight = width > height ? true : false;
        if(isWidthEqualsHeight) {
            mRect = new Rect(left, top, right, bottom);
        }else{
            if(isWidthBiggerThanHeight){
                // 宽大于高，宽度要缩小成与高度一致
                int differ = width - height;
                int differHalf = differ / 2;
                left += differHalf;
                right -= differHalf;
            }else{
                // 高大于宽，高度要缩小成与宽度一致
                int differ = height - width;
                int differHalf = differ / 2;
                top += differHalf;
                bottom -= differHalf;
            }
            mRect = new Rect(left, top, right, bottom);
        }
        // 设置指向方向
        switch (mArrowDirection){
            case ARROW_DIRECTION_UP:
                setRotation(-90);
                break;
            case ARROW_DIRECTION_RIGHT:
                setRotation(0);
                break;
            case ARROW_DIRECTION_DOWN:
                setRotation(90);
                break;
            case ARROW_DIRECTION_LEFT:
                setRotation(180);
                break;
        }
        // 真正画图
        switch (mArrowType){
            case ARROW_TYPE_DASH:
                // 有线
                drawArrowDash(canvas, mPaint, mRect);
                break;
            case ARROW_TYPE_NODASH_DOUBLE:
                // 无线两个
                drawArrowDashDouble(canvas, mPaint, mRect);
                break;
            case ARROW_TYPE_NODASH:
            default:
                // 无线
                drawArrowNoDash(canvas, mPaint, mRect);
                break;
        }
    }

    /**
     * 画有横线的箭头
     * ->
     * @param canvas 画布
     * @param paint 画笔
     * @param drawRect 可绘图区域
     */
    private void drawArrowDash(Canvas canvas, Paint paint, Rect drawRect) {
//        Log.d(TAG, "drawArrowDash()==>");

        float line1StartX = drawRect.centerX();
        float line1StartY = drawRect.top;

        float line1EndX = drawRect.right;
        float line1EndY = drawRect.centerY();

        float line2EndX = drawRect.centerX();
        float line2EndY = drawRect.bottom;

        Path path = new Path();
        path.moveTo(line1StartX, line1StartY);
        path.lineTo(line1EndX, line1EndY);
        path.lineTo(line2EndX, line2EndY);
        canvas.drawPath(path, paint);

        float line3StartX = drawRect.left;
        float line3StartY = drawRect.centerY();
        float line3EndX = drawRect.right;
        float line3EndY = drawRect.centerY();
        canvas.drawLine(line3StartX, line3StartY, line3EndX, line3EndY, paint);
    }

    /**
     * 画无横线的双箭头
     * >>
     * @param canvas 画布
     * @param paint 画笔
     * @param drawRect 可绘图区域
     */
    private void drawArrowDashDouble(Canvas canvas, Paint paint, Rect drawRect) {
//        Log.d(TAG, "drawArrowDashDouble()==>");

//        int xOffset1 = (drawRect.right-drawRect.left) / 4;
        int xOffset1 = 0;
        int xOffset2 = (drawRect.right-drawRect.left) / 2;

        float line1StartX = drawRect.centerX();
        float line1StartY = drawRect.top;

        float line1EndX = drawRect.right;
        float line1EndY = drawRect.centerY();

        float line2EndX = drawRect.centerX();
        float line2EndY = drawRect.bottom;

        Path path1 = new Path();
        path1.moveTo(line1StartX - xOffset1, line1StartY);
        path1.lineTo(line1EndX - xOffset1, line1EndY);
        path1.lineTo(line2EndX - xOffset1, line2EndY);
        canvas.drawPath(path1, paint);

        Path path2 = new Path();
        path2.moveTo(line1StartX - xOffset2, line1StartY);
        path2.lineTo(line1EndX - xOffset2, line1EndY);
        path2.lineTo(line2EndX - xOffset2, line2EndY);
        canvas.drawPath(path2, paint);

    }

    /**
     * 画无横线的单箭头
     * >
     * @param canvas 画布
     * @param paint 画笔
     * @param drawRect 可绘图区域
     */
    private void drawArrowNoDash(Canvas canvas, Paint paint, Rect drawRect) {
//        Log.d(TAG, "drawArrowNoDash()==>");

        int xOffset = 0;
        switch(mArrowNoDashGravity){
            case ARROW_NODASH_GRAVITY_FORWARD:
                xOffset = 0;
                break;
            case ARROW_NODASH_GRAVITY_CENTER:
                xOffset = (drawRect.right-drawRect.left) / 4;
                break;
            case ARROW_NODASH_GRAVITY_BACKWARD:
                xOffset = (drawRect.right-drawRect.left) / 2;
                break;
        }

        float line1StartX = drawRect.centerX() - xOffset;
        float line1StartY = drawRect.top;

        float line1EndX = drawRect.right - xOffset;
        float line1EndY = drawRect.centerY();

        float line2EndX = drawRect.centerX() - xOffset;
        float line2EndY = drawRect.bottom;

        Path path = new Path();
        path.moveTo(line1StartX, line1StartY);
        path.lineTo(line1EndX, line1EndY);
        path.lineTo(line2EndX, line2EndY);
        canvas.drawPath(path, paint);
    }

    public int getArrowDirection()
    {
        return mArrowDirection;
    }

    public void setArrowDirection(int direction)
    {
        mArrowDirection = direction;
        postInvalidate();
    }
}
