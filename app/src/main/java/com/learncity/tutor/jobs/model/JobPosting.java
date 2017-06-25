package com.learncity.tutor.jobs.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by DJ on 6/25/2017.
 */

public class JobPosting extends Job implements Parcelable {

    /**
     * The Time when the request was received.
     */
    private long jobPostingTimeStamp;

    public JobPosting(@NonNull String jobId, @NonNull String posterName, @NonNull String subjects, @NonNull String location, long jobPostingTimeStamp) {
        super(jobId, posterName, subjects, location);
        this.jobPostingTimeStamp = jobPostingTimeStamp;
    }

    public long getJobPostingTimeStamp() {
        return jobPostingTimeStamp;
    }

    protected JobPosting(Parcel in) {
        super(in);
        jobPostingTimeStamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(jobPostingTimeStamp);
    }

    @SuppressWarnings("unused")
    public static final Creator<JobPosting> CREATOR = new Creator<JobPosting>() {
        @Override
        public JobPosting createFromParcel(Parcel in) {
            return new JobPosting(in);
        }

        @Override
        public JobPosting[] newArray(int size) {
            return new JobPosting[size];
        }
    };
}
