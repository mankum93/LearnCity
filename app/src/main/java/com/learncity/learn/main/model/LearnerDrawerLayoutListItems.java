package com.learncity.learn.main.model;

/**
 * Created by DJ on 10/13/2016.
 */

/**
 * This class stores the app related features as an array of LearnerDrawerLayoutItem class
 */
public class LearnerDrawerLayoutListItems {

    public static final LearnerDrawerLayoutItem[] LEARNER_DRAWER_LAYOUT_ITEMs = new LearnerDrawerLayoutItem[]{
            new LearnerDrawerLayoutItem("SEARCH TUTORS", "PATH"),
            new LearnerDrawerLayoutItem("MY MATERIAL", "PATH"),
            new LearnerDrawerLayoutItem("MY PROFILE", "PATH"),
            new LearnerDrawerLayoutItem("SETTINGS", "PATH")
    };
    /*
     * class encapsulating a feature/module of the app.
     * TODO: The getters and setters are not required probably. Remove them
     */
    public static class LearnerDrawerLayoutItem {

        public String mSearchFeatureName;
        public String mSearchFeatureIcon;

        public String getSearchFeatureName() {
            return mSearchFeatureName;
        }

        public void setSearchFeatureName(String mSearchFeatureName) {
            this.mSearchFeatureName = mSearchFeatureName;
        }

        public String getSearchFeatureIcon() {
            return mSearchFeatureIcon;
        }

        public void setSearchFeatureIcon(String mSearchFeatureIcon) {
            this.mSearchFeatureIcon = mSearchFeatureIcon;
        }

        LearnerDrawerLayoutItem(String searchFeatureName, String searchFeatureIcon){
            mSearchFeatureName = searchFeatureName;
            mSearchFeatureIcon = searchFeatureIcon;
        }


    }
}
