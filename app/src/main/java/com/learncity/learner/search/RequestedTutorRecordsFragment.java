package com.learncity.learner.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.learncity.learncity.R;
import com.learncity.learner.Repository;
import com.learncity.learner.search.model.request.TutorRequestRecord;
import com.learncity.util.ArraysUtil;
import com.learncity.util.DataSetObserver;
import com.learncity.util.DateTimeUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by DJ on 6/17/2017.
 */

public class RequestedTutorRecordsFragment extends Fragment {

    private Repository repo;
    private DataSetObserver tutorRequestRecordsObserver;
    private TutorRequestsRecyclerViewAdapter adapter;
    private RecyclerView tutorRequestRecords;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        repo = Repository.getRepository(getContext().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_requested_tutors_records, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the Recycler View for the records
        tutorRequestRecords = ((RecyclerView) view.findViewById(R.id.requested_tutors_records));

        // Retrieve the list of Tutor request records.
        List<TutorRequestRecord> records = repo.getTutorRequestRecords();

        // Adapter setup
        adapter = new TutorRequestsRecyclerViewAdapter(records, getContext().getApplicationContext());
        tutorRequestRecords.setAdapter(adapter);

        tutorRequestRecords.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

        if(this.tutorRequestRecordsObserver == null){
            // Register Tutor request records observers with the Repo.
            tutorRequestRecordsObserver = new DataSetObserver() {
                @Override
                public void onChanged() {
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    adapter.notifyItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                    // Do nothing
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    adapter.notifyItemRangeInserted(positionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    adapter.notifyItemRangeRemoved(positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    // TODO: Yet to implement.
                }
            };

            repo.registerTutorRequestRecordsObserver(tutorRequestRecordsObserver);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        repo.unregisterTutorRequestRecordsObserver(tutorRequestRecordsObserver);

        super.onStop();
    }

    // Recycler View Adapter----------------------------------------------------------------------------------------------

    private static class TutorRequestsRecyclerViewAdapter extends RecyclerView.Adapter<TutorRequestsViewHolder>{

        private List<TutorRequestRecord> tutorRequestRecordList;
        private Context context;

        public TutorRequestsRecyclerViewAdapter(List<TutorRequestRecord> tutorRequestRecordList, @NonNull Context context) {
            if(tutorRequestRecordList == null){
                this.tutorRequestRecordList = new LinkedList<>();
            }
            else{
                this.tutorRequestRecordList = tutorRequestRecordList;
            }

            this.context = context;
        }

        @Override
        public TutorRequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Create the view that shall hold the result
            View v = LayoutInflater.from(context).inflate(R.layout.item_tutor_request_record, parent, false);
            return new TutorRequestsViewHolder(context, v);
        }


        @Override
        public void onBindViewHolder(TutorRequestsViewHolder holder, int position) {
            holder.bindTutorRequestRecordToView(tutorRequestRecordList.get(position));
        }

        @Override
        public int getItemCount() {
            return tutorRequestRecordList.size();
        }
    }

    private static class TutorRequestsViewHolder extends RecyclerView.ViewHolder{

        private static Drawable profilePicPlaceholderDrawable;

        private TextView tutorName;
        private TextView skillSet;
        private TextView tutorTypes;
        private TextView location;
        private TextView timeRequested;
        private ImageView displayPic;
        private SimpleRatingBar rating;

        private Context context;

        static void clearStatics(){
            profilePicPlaceholderDrawable = null;
        }

        public TutorRequestsViewHolder(@NonNull Context context, @NonNull View itemView){
            super(itemView);

            this.context = context;

            // Dissect the view
            tutorName = (TextView) itemView.findViewById(R.id.tutor_name);
            skillSet = (TextView) itemView.findViewById(R.id.skill_set);
            tutorTypes = (TextView) itemView.findViewById(R.id.tutor_types);
            location = (TextView) itemView.findViewById(R.id.location);
            timeRequested = (TextView) itemView.findViewById(R.id.tutor_requested_time);

            displayPic = (ImageView) itemView.findViewById(R.id.display_pic);

            rating = (SimpleRatingBar) itemView.findViewById(R.id.tutor_rating);

            if(profilePicPlaceholderDrawable == null){
                profilePicPlaceholderDrawable = ContextCompat.getDrawable(context, R.drawable.user_pic_placeholder);
            }

        }

        public void bindTutorRequestRecordToView(@NonNull final TutorRequestRecord requestRecord){

            // Now, bind each part
            tutorName.setText(requestRecord.getTutorName());

            final String skillSet1 = requestRecord.getSubjects();
            skillSet.setText(skillSet1 == null || skillSet1.isEmpty() ? "" : ArraysUtil.convertArrayToString(skillSet1.split("__,__"), ", "));

            final String tutorTypes1 = requestRecord.getTutorTypes();
            tutorTypes.setText(tutorTypes1 == null || tutorTypes1.isEmpty() ? "" : ArraysUtil.convertArrayToString(tutorTypes1.split("__,__"), ", "));

            final String shortFormattedAddress = requestRecord.getTutorLocation();
            location.setText(shortFormattedAddress == null || shortFormattedAddress.isEmpty() ? "" : shortFormattedAddress);

            timeRequested.setText(context.getResources().getString(R.string.time_requested,
                    DateTimeUtils.timeMillisToHH_MMFormat(requestRecord.getTimeStamp(), "hh:mm a EEE, MMM d, YYYY")));

            final float r = requestRecord.getTutorRating();
            rating.setRating(r);

            // Binding the placeholder pic
            displayPic.setImageDrawable(profilePicPlaceholderDrawable);
            // TODO: Bind the display pic to actual user DP
        }
    }
}
