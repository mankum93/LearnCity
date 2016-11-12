package com.learncity.learn.main.model;

/**
 * Created by DJ on 10/13/2016.
 */

/**
 * This class stores the app related features as an array of AppFeature class
 */
public class AppFeatures {

    public static final AppFeature[] appFeatures = new AppFeature[]{
            new AppFeature("SEARCH TUTORS", "PATH"),
            new AppFeature("MY MATERIAL", "PATH"),
            new AppFeature("MY PROFILE", "PATH"),
            new AppFeature("SETTINGS", "PATH")
    };
    /*
     * class encapsulating a feature/module of the app.
     * TODO: The getters and setters are not required probably. Remove them
     */
    public static class AppFeature {

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

        AppFeature(String searchFeatureName, String searchFeatureIcon){
            mSearchFeatureName = searchFeatureName;
            mSearchFeatureIcon = searchFeatureIcon;
        }


    }
}
