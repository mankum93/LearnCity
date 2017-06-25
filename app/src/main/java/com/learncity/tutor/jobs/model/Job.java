package com.learncity.tutor.jobs.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by DJ on 6/25/2017.
 */

public class Job implements Parcelable {

    /**
     * The Job ID.
     */
    private String jobId;

    /**
     * The name of the Learner requesting for Tutoring.
     */
    private String posterName;

    /**
     * The subjects that the requester wants to be taught.
     */
    private String subjects;

    /**
     * The location of the Requester.
     */
    private String location;

    public Job(@NonNull String jobId,
               @NonNull String posterName,
               @NonNull String subjects,
               @NonNull String location) {
        this.jobId = jobId;
        this.posterName = posterName;
        this.subjects = subjects;
        this.location = location;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(@NonNull String subjects) {
        this.subjects = subjects;
    }

    public String getPosterName() {
        return posterName;
    }

    public String getLocation() {
        return location;
    }

    public String getJobId() {
        return jobId;
    }

    protected Job(Parcel in) {
        jobId = in.readString();
        posterName = in.readString();
        subjects = in.readString();
        location = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeString(posterName);
        dest.writeString(subjects);
        dest.writeString(location);
    }

    @SuppressWarnings("unused")
    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };
}
