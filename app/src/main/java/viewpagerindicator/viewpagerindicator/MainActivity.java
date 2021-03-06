package viewpagerindicator.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import viewpagerindicator.viewpagerindicator.ui.ViewPagerIndicator;

public class MainActivity extends FragmentActivity {

    private String[] mTitles = {"pager1", "pager2", "pager3", "pager4","pager5", "pager6", "pager7", "pager8"};
    private ViewPager viewPager;
    private ViewPagerIndicator viewPagerIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initDatas();
    }


    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.vp_indicator);


    }

    private void initDatas() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return PagerFragment.newInstance(mTitles[position]);
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        viewPagerIndicator.setViewPager(viewPager);
    }

}
