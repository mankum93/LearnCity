package com.learncity.learner.search.model.request;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Created by DJ on 6/17/2017.
 */

public class RequestRecord implements Parcelable {

    /**
     * UUID of the user(to whom request was sent)
     */
    private UUID to;
    /**
     * Timestamp when the request was sent.
     */
    private long timeStamp;
    /**
     * The type of the request that was sent.
     */
    private int requestType;

    protected RequestRecord(@NonNull UUID to, long timeStamp, int requestType) {
        this.to = to;
        this.timeStamp = timeStamp;
        this.requestType = requestType;
    }

    public UUID getTo() {
        return to;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getRequestType() {
        return requestType;
    }

    protected RequestRecord(Parcel in) {
        to = UUID.fromString(in.readString());
        timeStamp = in.readLong();
        requestType = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(to.toString());
        dest.writeLong(timeStamp);
        dest.writeInt(requestType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RequestRecord> CREATOR = new Parcelable.Creator<RequestRecord>() {
        @Override
        public RequestRecord createFromParcel(Parcel in) {
            return new RequestRecord(in);
        }

        @Override
        public RequestRecord[] newArray(int size) {
            return new RequestRecord[size];
        }
    };


    // Builder pattern-------------------------------------------------------------------------------------------------

    public static class Builder{

        /**
         * UUID of the user(to whom request was sent)
         */
        private UUID to;
        /**
         * Timestamp when the request was sent.
         */
        private long timeStamp;
        /**
         * The type of the request that was sent.
         */
        private int requestType;

        protected Builder(@NonNull UUID to, long timeStamp, int requestType) {
            this.to = to;
            this.timeStamp = timeStamp;
            this.requestType = requestType;
        }

        public UUID getTo() {
            return to;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public int getRequestType() {
            return requestType;
        }
    }
}
