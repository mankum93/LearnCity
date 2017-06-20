package com.learncity.learner.search.model.request;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by DJ on 6/17/2017.
 */

public class TutorRequestRecord extends RequestRecord implements Parcelable {

    /**
     * Name of the Tutor.
     */
    private String tutorName;
    /**
     * Location of the Tutor.
     */
    private String tutorLocation;
    /**
     * Rating of the Tutor.
     */
    private float tutorRating;
    /**
     * Subjects taught by the Tutor.(see the valid list of subjects)
     */
    private String subjects;
    /**
     * Type of the Tutor.(see the valid Tutor types.)
     */
    private String tutorType;

    // Getters, Setters & Ctrs---------------------------------------------------------------------------------------

    protected TutorRequestRecord(@NonNull UUID to, long timeStamp, int messageType) {
        super(to, timeStamp, messageType);
    }

    protected TutorRequestRecord(@NonNull UUID to,
                                 long timeStamp,
                                 int requestType,
                                 @NonNull String tutorName,
                                 @NonNull String subjects,
                                 @NonNull String tutorType) {
        super(to, timeStamp, requestType);
        this.tutorName = tutorName;
        this.subjects = subjects;
        this.tutorType = tutorType;
    }

    public TutorRequestRecord(@NonNull UUID to, long timeStamp, int requestType, String tutorName, String tutorLocation, float tutorRating, String subjects, String tutorType) {
        super(to, timeStamp, requestType);
        this.tutorName = tutorName;
        this.tutorLocation = tutorLocation;
        this.tutorRating = tutorRating;
        this.subjects = subjects;
        this.tutorType = tutorType;
    }

    public String getTutorName() {
        return tutorName;
    }

    public String getTutorLocation() {
        return tutorLocation;
    }

    public void setTutorLocation(String tutorLocation) {
        this.tutorLocation = tutorLocation;
    }

    public float getTutorRating() {
        return tutorRating;
    }

    public void setTutorRating(float tutorRating) {
        this.tutorRating = tutorRating;
    }

    public String getSubjects() {
        return subjects;
    }

    public String getTutorTypes() {
        return tutorType;
    }

    protected TutorRequestRecord(Parcel in) {
        super(in);
        tutorName = in.readString();
        tutorLocation = in.readString();
        tutorRating = in.readFloat();
        subjects = in.readString();
        tutorType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(tutorName);
        dest.writeString(tutorLocation);
        dest.writeFloat(tutorRating);
        dest.writeString(subjects);
        dest.writeString(tutorType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TutorRequestRecord> CREATOR = new Parcelable.Creator<TutorRequestRecord>() {
        @Override
        public TutorRequestRecord createFromParcel(Parcel in) {
            return new TutorRequestRecord(in);
        }

        @Override
        public TutorRequestRecord[] newArray(int size) {
            return new TutorRequestRecord[size];
        }
    };

    // Builder pattern-------------------------------------------------------------------------------------------------

    public static class Builder extends RequestRecord.Builder{

        /**
         * Name of the Tutor.
         */
        private String tutorName;
        /**
         * Location of the Tutor.
         */
        private String tutorLocation;
        /**
         * Rating of the Tutor.
         */
        private float tutorRating;
        /**
         * Subjects taught by the Tutor.(see the valid list of subjects)
         */
        private String subjects;
        /**
         * Type of the Tutor.(see the valid Tutor types.)
         */
        private String tutorType;

        protected Builder(@NonNull UUID to, long timeStamp, int requestType) {
            super(to, timeStamp, requestType);
        }

        protected Builder(@NonNull UUID to, long timeStamp, int requestType, String tutorName, String subjects, String tutorType) {
            super(to, timeStamp, requestType);
            this.tutorName = tutorName;
            this.subjects = subjects;
            this.tutorType = tutorType;
        }

        public static Builder newInstance(@NonNull UUID to, long timeStamp, int requestType, String tutorName, String subjects, String tutorType){
            return new Builder(to, timeStamp, requestType, tutorName, subjects, tutorType);
        }

        public Builder withTutorLocation(String tutorLocation) {
            this.tutorLocation = tutorLocation;
            return this;
        }

        public Builder withTutorRating(float tutorRating) {
            this.tutorRating = tutorRating;
            return this;
        }

        public TutorRequestRecord build(){
            return new TutorRequestRecord(getTo(), getTimeStamp(),
                    getRequestType(), this.tutorName,
                    this.tutorLocation, this.tutorRating,
                    this.subjects, this.tutorType);
        }
    }
}
