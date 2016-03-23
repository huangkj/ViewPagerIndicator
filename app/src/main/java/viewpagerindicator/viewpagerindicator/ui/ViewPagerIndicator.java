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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
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
     * 单个tab的宽度
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
    private VelocityTracker mVelocityTracker;
    private int touchSlop;

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
        mVelocityTracker = VelocityTracker.obtain();
        touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mTriangleWidth = getChildAt(0).getMeasuredWidth() / 6;
        tabWidth = getChildAt(0).getMeasuredWidth();
        defaultTranslationX = tabWidth / 2 - mTriangleWidth / 2;
        triangTranslationY = getHeight();
        drawTriangle();
        childWidthTotal = 0;
        for (int i = 0; i < getChildCount(); i++) {
            childWidthTotal += getChildAt(0).getWidth();
        }
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

    int lastX = 0;
    int lastY = 0;
    int childWidthTotal = 0;
    int interceptLastX = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(interceptLastX - x) > touchSlop) {//如果是滑动，则拦截
                    intercept = true;
                } else {
                    intercept = false;

                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        interceptLastX = (int) ev.getX();
        lastX = (int) ev.getX();//这行代码很关键！！！
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = lastX - x;
                int dy = lastY - y;
                if (getScrollX() < 0) {
                    dx = 0;
                }
                if (getScrollX() > childWidthTotal - getWidth()) {
                    dx = 0;
                }
                scrollBy(dx, 0);
//                Log.i(Tag, "dx:  " + dx);
//                Log.i(Tag, " getScrollX():  " + getScrollX());
//                Log.i(Tag, "childWidthTotal - getWidth()  " + (childWidthTotal - getWidth()));
//                Log.i(Tag, "childWidthTotal " + (childWidthTotal - getWidth()));
                break;
            case MotionEvent.ACTION_UP:
                //计算速度
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
//                使滑动有惯性，fling方法想要生效，需重写computeScroll()方法!
//                mScroller.fling(getScrollX(), 0, (int) -xVelocity, 0, 0, childWidthTotal - getWidth(), 0, 0);
                //修正左右尽头
                if (getScrollX() < 0) {
                    smoothScrollTo(0, 0);
                }
                if (getScrollX() > childWidthTotal - getWidth()) {
                    smoothScrollTo(childWidthTotal - getWidth(), 0);
                }


                mVelocityTracker.clear();
                break;
        }
        lastX = (int) event.getX();
        lastY = (int) event.getY();

        return true;
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
        int endPos = 0;//当右边tab都可见时，tab不再往右滑动
        if (visiableTabCount % 2 == 0) {
            endPos = tabCount - (visiableTabCount / 2);
        } else {
            endPos = tabCount - ((visiableTabCount / 2 + 1));
        }
        if (position >= visiableTabCount / 2 - 0.5 && positionOffset > 0 && position < endPos) {
            scrollTo((int) ((int) (tabWidth * positionOffset) + Math.max(0, (tabWidth * (position - Math.ceil(visiableTabCount / 2))))), 0);
        }else {
            if(position < visiableTabCount/2-0.5){
                smoothScrollTo(0,0);
            }else if(position >= endPos){
                smoothScrollTo(childWidthTotal-getWidth(),0);
            }
        }
        invalidate();
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPger = viewPager;
        tabCount = mViewPger.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            TextView tv = new TextView(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lp.width = screenWidth / visiableTabCount;
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
                                          //根据postion确定滑动的目的地dx;
                                          int dx = 0;
                                          if(finalI <=visiableTabCount/2){//当pos很小，滑动到(0,0)
                                              dx = 0;
                                          }else if(finalI == tabCount-1){//当点击最后一个的时候，滑动到最后一个tab
                                              dx = childWidthTotal - getWidth();
                                          }else {//当点击中间一些tab时 目的地dx的确定由pos 可见tab数量    *单个tab宽度决定
                                              dx = (finalI+1 -(visiableTabCount-1)) *tabWidth;
                                          }
                                          smoothScrollTo(dx,0);
                                          mViewPger.setCurrentItem(finalI);
                                      }


                                  }

            );

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()

                                              {
                                                  @Override
                                                  public void onPageScrolled(int position, float positionOffset,
                                                                             int positionOffsetPixels) {
                                                      scoll(position, positionOffset);
                                                  }

                                                  @Override
                                                  public void onPageSelected(int position) {

                                                  }

                                                  @Override
                                                  public void onPageScrollStateChanged(int state) {

                                                  }
                                              }

            );

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
            postInvalidate();
        }
    }

}
