package com.example.loginscreen;
import com.example.loginscreen.ui.gallery.GalleryFragment;
import com.example.loginscreen.ui.home.HomeFragment;
import com.example.loginscreen.ui.slideshow.SlideshowFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else if (position == 1) {
            return new GalleryFragment();
        } else return new SlideshowFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}

