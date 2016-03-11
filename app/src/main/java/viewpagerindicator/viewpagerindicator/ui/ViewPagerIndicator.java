package viewpagerindicator.viewpagerindicator.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import viewpagerindicator.viewpagerindicator.R;

/**
 * 创建者：黄凯军
 * 创建时间：2016/3/11 10:24
 * 类描述：
 */
public class ViewPagerIndicator extends LinearLayout {

    private Path mPath;

    /**
     * 三角形宽
     */
    private int mTriangleWidth;
    /**
     * 三角形高
     */
    private int mTriangleHeight;
    /**
     * 三角形x轴位移坐标
     */
    private int triangleTranslationX;
    /**
     * 三角形x轴位移坐标
     */
    private int defaultTranslationX;
    /**
     * 三角形y轴位移坐标
     */
    private int triangTranslationY;

    private Paint mPaint;
    /**
     * tab的宽度
     */
    private int tabWidth;
    /**
     * 显示的tab数量
     */
    private int visiableTabCount;
    /**
     * tab的数量
     */
    private int tabCount;
    private String Tag = "ViewPagerIndicator";
    private ViewPager mViewPger;
    private Context mContext;
    private int screenWidth;
    private Scroller mScroller;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        visiableTabCount = a.getInteger(R.styleable.ViewPagerIndicator_visiable_tab_count, 0);
        a.recycle();
        mContext = context;
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
       WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
//        tabCount = mViewPger.getAdapter().getCount();
//        Log.i(Tag, "tabCount: " + tabCount);
        mScroller = new Scroller(mContext);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mTriangleWidth = getChildAt(0).getMeasuredWidth() / 6;
        tabWidth = getChildAt(0).getMeasuredWidth();
        defaultTranslationX = tabWidth / 2 - mTriangleWidth / 2;
        triangTranslationY = getHeight();
        drawTriangle();
    }

    /**
     * 绘制三角形
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(defaultTranslationX + triangleTranslationX, getHeight());//位移画板
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    /**
     * 初始化三角形
     */
    private void drawTriangle() {
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -(mTriangleWidth / 2));
        mPath.close();
    }


    public void scoll(int position, float positionOffset) {
        triangleTranslationX = (int) ((tabWidth * positionOffset) + ((position) * tabWidth));
        Log.i(Tag, "positionOffset: " + positionOffset);
//        Log.i(Tag, "triangleTranslationX: " + triangleTranslationX);
        Log.i(Tag, "position: " + position);
        Log.i(Tag, "tabWidth * positionOffset: " + tabWidth * positionOffset);
        if (position > visiableTabCount - 2 && positionOffset > 0) {
            int toX = (int) (tabWidth * positionOffset) + (tabWidth * (position - (visiableTabCount - 1)));
            scrollTo((int) (tabWidth * positionOffset) + (tabWidth * (position - (visiableTabCount - 1))), 0);
            Log.i(Tag, "toX: " + toX);
        }




        invalidate();
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPger = viewPager;
        tabCount = mViewPger.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            TextView tv = new TextView(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.width =screenWidth / visiableTabCount;
            tv.setText(mViewPger.getAdapter().getPageTitle(i));
            tv.setTextColor(Color.RED);
            tv.setTextSize(20);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(lp);
            addView(tv);
            final int finalI = i;
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    triangleTranslationX = tabWidth * finalI;
                    mViewPger.setCurrentItem(finalI);
                    postInvalidate();
                }
            });
        }
    }


    /**
     * 慢慢的移动到某一点
     *
     * @param destX
     * @param destY
     */
    public void smoothScrollTo(int destX, int destY) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int dx = destX - scrollX;
        int dy = destY - scrollY;
        mScroller.startScroll(scrollX, scrollY, dx, dy);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
        }
        postInvalidate();
    }

}
