package com.learncity.tutor.jobs.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by DJ on 6/25/2017.
 */

public class JobRequest extends Job implements Parcelable {

    /**
     * The Time when the request was received.
     */
    private long requestedTimeStamp;

    public JobRequest(@NonNull String jobId, @NonNull String posterName, @NonNull String subjects, @NonNull String location, long requestedTimeStamp) {
        super(jobId, posterName, subjects, location);
        this.requestedTimeStamp = requestedTimeStamp;
    }

    public long getRequestedTimeStamp() {
        return requestedTimeStamp;
    }

    protected JobRequest(Parcel in) {
        super(in);
        requestedTimeStamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(requestedTimeStamp);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<JobRequest> CREATOR = new Parcelable.Creator<JobRequest>() {
        @Override
        public JobRequest createFromParcel(Parcel in) {
            return new JobRequest(in);
        }

        @Override
        public JobRequest[] newArray(int size) {
            return new JobRequest[size];
        }
    };
}
