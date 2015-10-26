package com.lab11.nolram.cadernocamera;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.lab11.nolram.components.TutorialAdapter;
import com.lab11.nolram.components.TutorialPageTransform;

public class TutorialActivity extends AppCompatActivity {

    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private boolean isOpaque = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(new TutorialAdapter(getSupportFragmentManager(),
                getApplicationContext()));

        // Set a PageTransformer
        mViewPager.setPageTransformer(false, new TutorialPageTransform());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //See note above for why this is needed
                if (position == TutorialAdapter.NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        mViewPager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        mViewPager.setBackgroundColor(getResources().getColor(
                                R.color.preto_transp_40));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
               /* setIndicator(position);
                if (position == NUM_PAGES - 2) {
                    skip.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                } else if (position < NUM_PAGES - 2) {
                    skip.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    done.setVisibility(View.GONE);
                } else if (position == NUM_PAGES - 1) {
                    endTutorial();
                }
                */
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Unused
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
