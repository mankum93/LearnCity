package com.learncity.generic.learner.main.model;

/**
 * Created by DJ on 10/13/2016.
 */

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.learncity.learncity.R;

/**
 * This class stores the Drawer List items for different user types on the User Home Page.
 */
public final class GenericDrawerLayoutListItems {

    public static final GenericDrawerLayoutItem[] LEARNER_DRAWER_LAYOUT_ITEMS = new GenericDrawerLayoutItem[]{
            new GenericDrawerLayoutItem("Home", R.drawable.ic_home_black_24dp),
            new GenericDrawerLayoutItem("My Material", R.drawable.ic_text_book_opened_from_top_view),
            new GenericDrawerLayoutItem("My Profile", R.drawable.ic_person_black_24dp),
            new GenericDrawerLayoutItem("Settings", R.drawable.ic_settings_24px)
    };
    public static final GenericDrawerLayoutItem[] TUTOR_DRAWER_LAYOUT_ITEMS = new GenericDrawerLayoutItem[]{
            new GenericDrawerLayoutItem("Home", R.drawable.ic_home_black_24dp),
            new GenericDrawerLayoutItem("My Material", R.drawable.ic_text_book_opened_from_top_view),
            new GenericDrawerLayoutItem("My Profile", R.drawable.ic_person_black_24dp),
            new GenericDrawerLayoutItem("Settings", R.drawable.ic_settings_24px)
    };


    /**
     * Class representing a Drawer List item on the User Home Page.
     */
    public static class GenericDrawerLayoutItem {

        private String navItemName;
        @DrawableRes
        private int navItemIconRes;

        public String getNavItemName() {
            return navItemName;
        }

        public void setNavItemName(@NonNull String mSearchFeatureName) {
            this.navItemName = mSearchFeatureName;
        }

        public int getNavItemIconRes() {
            return navItemIconRes;
        }

        public void setNavItemIconRes(@DrawableRes int navItemIconRes) {
            this.navItemIconRes = navItemIconRes;
        }

        public GenericDrawerLayoutItem(@NonNull String navItemName, @DrawableRes int navItemIconRes) {
            this.navItemName = navItemName;
            this.navItemIconRes = navItemIconRes;
        }
    }
}
