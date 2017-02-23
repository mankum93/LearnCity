package com.learncity.generic.learner.account.profile.database.ver0;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learncity.generic.learner.account.profile.model.ver0.GenericLearnerProfileParcelable;
import com.learncity.tutor.account.profile.model.Duration;
import com.learncity.tutor.account.profile.model.occupation.Occupation;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualification;
import com.learncity.tutor.account.profile.model.qualification.educational.SecondaryEducationalQualification;
import com.learncity.tutor.account.profile.model.qualification.educational.SeniorSecondaryEducationalQualification;
import com.learncity.tutor.account.profile.model.qualification.educational.ver0.TutorProfileParcelable;

/**
 * Created by DJ on 10/23/2016.
 */

public class ProfileDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ProfileDbHelper";

    private static final int VERSION  = 1;
    public static final String DATABASE_NAME = "learnerProfileBase.db";
    private final int currentAccountStatus;

    public ProfileDbHelper(Context context, int currentAccountStatus){
        super(context, DATABASE_NAME, null, VERSION);
        this.currentAccountStatus = currentAccountStatus;
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        //Depending on the current status, create the Learner or Tutor A/C
        if(currentAccountStatus == GenericLearnerProfileParcelable.STATUS_LEARNER){
            //--------------------------PROFILE TABLES--------------------------------------------------------------
            db.execSQL("create table " + ProfileDbSchema.LearnerProfileTable.NAME + "(" +
                    ProfileDbSchema.LearnerProfileTable.cols.NAME + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PASSWORD + ")"
            );
            Log.d(TAG, "1. Learner table creation complete");
        }
        else{
            //--------------------------PROFILE TABLES--------------------------------------------------------------

            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.NAME + "(" +
                    ProfileDbSchema.LearnerProfileTable.cols.NAME + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PASSWORD + ")"
            );
            Log.d(TAG, "1. Tutor table creation complete");
            //-------------------EDUCATIONAL QUALIFICATION TABLES----------------------------------------------------
            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.SecondaryEducationalQualificationTable.NAME + "(" +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.QUALIFICATION_NAME+ "," +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME+ "," +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING+ "," +
                    ProfileDbSchema.TutorProfileTable.SecondaryEducationalQualificationTable.cols.BOARD_NAME + ")"
            );
            Log.d(TAG, "2. Secondary education table creation complete");
            //Duration table for Secondary education
            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.DurationTable.NAME_SECONDARYEDUCATIONALQUALIFICATION + "(" +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS+ "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS+ "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS + ")"
            );
            Log.d(TAG, "3. Secondary education Duration table creation complete");
            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.NAME + "(" +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.QUALIFICATION_NAME+ "," +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME+ "," +
                    ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING+ "," +
                    ProfileDbSchema.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.cols.BOARD_NAME + ")"
            );
            Log.d(TAG, "4. Senior Secondary education table creation complete");
            //Duration table for Senior Secondary education
            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.DurationTable.NAME_SENIORSECONDARYEDUCATIONALQUALIFICATION + "(" +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS+ "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS+ "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS + ")"
            );
            Log.d(TAG, "5. Senior Secondary education Duration table creation complete");
            //--------------------OCCUPATION TABLES--------------------------------------------------------------------
            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.OccupationTable.NAME + "(" +
                    ProfileDbSchema.TutorProfileTable.OccupationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.TutorProfileTable.OccupationTable.cols.ORGANIZATION_NAME+ "," +
                    ProfileDbSchema.TutorProfileTable.OccupationTable.cols.DESIGNATION_NAME + ")"
            );
            Log.d(TAG, "6. Occupation table creation complete");
            //Duration table for Occupation
            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.DurationTable.NAME_OCCUPATION + "(" +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS+ "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS+ "," +
                    ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS + ")"
            );
            Log.d(TAG, "7. Occupation Duration table creation complete");
        }

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
    private static ContentValues getContentValues(GenericLearnerProfileParcelable myProfile){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.LearnerProfileTable.cols.NAME, myProfile.getName());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID, myProfile.getEmailID());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO, myProfile.getPhoneNo());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS, myProfile.getCurrentStatus());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.PASSWORD, myProfile.getPassword());

        return values;
    }

    private static ContentValues getContentValues(TutorProfileParcelable myProfile){

        //Get the base values
        ContentValues values = getContentValues((GenericLearnerProfileParcelable) myProfile);
        //Add the TutorProfile specific values. For now, there are none

        //Add the educational qualification and occupation separately
        return values;
    }
    private static ContentValues getContentValues(EducationalQualification educationalQualification, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.QUALIFICATION_NAME, educationalQualification.getmQualificationName());
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME, educationalQualification.getInstitution());
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING, educationalQualification.getYearOfPassing());
        //Add duration table separately

        return values;
    }
    private static ContentValues getContentValues(SecondaryEducationalQualification secondaryEducationalQualificationParcelable, String KEY_emailId){

        ContentValues values;

        values = getContentValues((EducationalQualification)secondaryEducationalQualificationParcelable, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.SecondaryEducationalQualificationTable.cols.BOARD_NAME, secondaryEducationalQualificationParcelable.getmBoard());
        //Add duration table separately

        return values;
    }
    private static ContentValues getContentValues(SeniorSecondaryEducationalQualification seniorSecondaryEducationalQualificationParcelable, String KEY_emailId){

        ContentValues values;

        values = getContentValues((EducationalQualification)seniorSecondaryEducationalQualificationParcelable, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.cols.BOARD_NAME, seniorSecondaryEducationalQualificationParcelable.getmBoard());
        //Add duration table separately

        return values;
    }

    private static ContentValues getContentValues(Duration duration, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS, duration.getNoOfYears());
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS, duration.getNoOfMonths());
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS, duration.getNoOfDays());

        return values;
    }
    private static ContentValues getContentValues(Occupation occupation, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.ORGANIZATION_NAME, occupation.getCurrentOrganization());
        values.put(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.DESIGNATION_NAME, occupation.getCurrentDesignation());
        //Add duration table separately

        return values;
    }
    public static void addProfileToDatabase(SQLiteDatabase database, GenericLearnerProfileParcelable profile){

        ContentValues profileValues = null;

        if(profile instanceof TutorProfileParcelable){

            TutorProfileParcelable tutorProfile = (TutorProfileParcelable) profile;
            profileValues = getContentValues(tutorProfile);

            ContentValues secondaryEducationQualificationValues = null;
            ContentValues seniorSecondaryEducationQualificationValues = null;
            //First time we add the profile to the database, there won't be any valid educational qualification
            //It makes sense that they enter later if they have to. So, it will be null and therefore we have put
            //a null check
            if(tutorProfile.getEducationalQualifications() != null){
                secondaryEducationQualificationValues = getContentValues(tutorProfile.getEducationalQualifications()[0], profile.getEmailID());
                seniorSecondaryEducationQualificationValues = getContentValues(tutorProfile.getEducationalQualifications()[1], profile.getEmailID());
            }
            ContentValues occupationValues = null;
            //First time we add the profile to the database, there won't be any valid occupation
            //It makes sense that they enter later if they have to. So, it will be null and therefore we have put
            //a null check
            if(tutorProfile.getOccupation() != null){
                occupationValues = getContentValues(tutorProfile.getOccupation(), profile.getEmailID());
            }

            //Now, its time to insert the profile in the Db
            database.insert(ProfileDbSchema.TutorProfileTable.NAME, null, profileValues);

            /*
            NOTE: The Educational Qualification and Occupation values at the start will be null because
            we don't require the tutor to fill in those details initially. He or she is gonna have to fill in it later
            if he wants to teach for real
            */
            //Insert the educational qualifications
            //database.insert(ProfileDbSchema.TutorProfileTable.SecondaryEducationalQualificationTable.NAME, null, secondaryEducationQualificationValues);
            //database.insert(ProfileDbSchema.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.NAME, null, seniorSecondaryEducationQualificationValues);
            //Insert the occupation values
            //database.insert(ProfileDbSchema.TutorProfileTable.OccupationTable.NAME, null, occupationValues);

        }
        else{
            profileValues = getContentValues(profile);
            database.insert(ProfileDbSchema.LearnerProfileTable.NAME, null, profileValues);
        }


    }

    public static void updateProfileInDatabase(SQLiteDatabase database, GenericLearnerProfileParcelable profile){

        //Assumption is that the Email ID will be unique for the accounts.
        String emailId = profile.getEmailID();
        ContentValues values = getContentValues(profile);
        database.update(ProfileDbSchema.LearnerProfileTable.NAME, values,
                ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID + "=?", new String[]{emailId});
    }
}
