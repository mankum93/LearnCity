package com.learncity.generic.learner.main.model;

/**
 * Created by DJ on 10/13/2016.
 */

import android.graphics.drawable.Drawable;

/**
 * This class stores the Drawer List items for different user types on the User Home Page.
 */
public class GenericDrawerLayoutListItems {

    public static final GenericDrawerLayoutItem[] LEARNER_DRAWER_LAYOUT_ITEMS = new GenericDrawerLayoutItem[]{
            new GenericDrawerLayoutItem("MY MATERIAL", null),
            new GenericDrawerLayoutItem("MY PROFILE", null),
            new GenericDrawerLayoutItem("SETTINGS", null)
    };
    public static final GenericDrawerLayoutItem[] TUTOR_DRAWER_LAYOUT_ITEMS = new GenericDrawerLayoutItem[]{
            new GenericDrawerLayoutItem("MY MATERIAL", null),
            new GenericDrawerLayoutItem("MY PROFILE", null),
            new GenericDrawerLayoutItem("SETTINGS", null)
    };


    /**
     * Class representing a Drawer List item on the User Home Page.
     */
    public static class GenericDrawerLayoutItem {

        private String featureName;
        private Drawable featureIcon;

        public String getFeatureName() {
            return featureName;
        }

        public void setFeatureName(String mSearchFeatureName) {
            this.featureName = mSearchFeatureName;
        }

        public Drawable getFeatureIcon() {
            return featureIcon;
        }

        public void setFeatureIcon(Drawable featureIcon) {
            this.featureIcon = featureIcon;
        }

        public GenericDrawerLayoutItem(String featureName, Drawable featureIcon) {
            this.featureName = featureName;
            this.featureIcon = featureIcon;
        }
    }
}
