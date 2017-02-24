package com.learncity.generic.learner.account.profile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.tutor.account.profile.model.Credits;
import com.learncity.tutor.account.profile.model.Duration;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.tutor.account.profile.model.occupation.Occupation;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualification;
import com.learncity.tutor.account.profile.model.qualification.educational.SecondaryEducationalQualification;
import com.learncity.tutor.account.profile.model.qualification.educational.SeniorSecondaryEducationalQualification;
import com.learncity.util.ArraysUtil;

import java.io.File;

/**
 * Created by DJ on 10/23/2016.
 */

public class ProfileDbHelperVer1 extends SQLiteOpenHelper {

    private static final String TAG = "ProfileDbHelper";

    private static final int VERSION  = 1;
    public static final String DATABASE_NAME = "learnerProfileBase.db";
    private final int currentAccountStatus;

    public ProfileDbHelperVer1(Context context, int currentAccountStatus){
        super(context, DATABASE_NAME, null, VERSION);
        this.currentAccountStatus = currentAccountStatus;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //Currently, as onCreate() is called only once. What if the user somehow initiates the
        // AC creation with a different status then chosen at first, see what's to be done in that case.
        //TODO: Solve the problem in the above comment

        //Depending on the current status, create the Learner or Tutor A/C
        if(currentAccountStatus == GenericLearnerProfile.STATUS_LEARNER){
            //-------------------------- LEARNER PROFILE TABLES--------------------------------------------------------------
            db.execSQL("create table " + ProfileDbSchemaVer1.LearnerProfileTable.NAME + "(" +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.NAME + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.PHONE_NO + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.CURRENT_STATUS + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.PASSWORD + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.DISPLAY_PIC_URI + ")"
            );
            Log.d(TAG, "1. Learner table creation complete");

        }
        else{
            //--------------------------TUTOR PROFILE TABLES--------------------------------------------------------------

            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.NAME + "(" +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.NAME + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.PHONE_NO + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.CURRENT_STATUS + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.PASSWORD + "," +
                    ProfileDbSchemaVer1.LearnerProfileTable.cols.DISPLAY_PIC_URI + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.cols.RATING + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.cols.TUTOR_TYPES + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.cols.SUBJECT_TYPES + ")"
            );
            Log.d(TAG, "1. Tutor table creation complete");
            //-------------------EDUCATIONAL QUALIFICATION TABLES----------------------------------------------------
            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.SecondaryEducationalQualificationTable.NAME + "(" +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.QUALIFICATION_NAME+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.SecondaryEducationalQualificationTable.cols.BOARD_NAME + ")"
            );
            Log.d(TAG, "2. Secondary education table creation complete");
            //Duration table for Secondary education
            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.DurationTable.NAME_SECONDARYEDUCATIONALQUALIFICATION + "(" +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_YEARS+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_DAYS + ")"
            );
            Log.d(TAG, "3. Secondary education Duration table creation complete");
            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.NAME + "(" +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.QUALIFICATION_NAME+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.cols.BOARD_NAME + ")"
            );
            Log.d(TAG, "4. Senior Secondary education table creation complete");
            //Duration table for Senior Secondary education
            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.DurationTable.NAME_SENIORSECONDARYEDUCATIONALQUALIFICATION + "(" +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_YEARS+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_DAYS + ")"
            );
            Log.d(TAG, "5. Senior Secondary education Duration table creation complete");
            //--------------------OCCUPATION TABLES--------------------------------------------------------------------
            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.OccupationTable.NAME + "(" +
                    ProfileDbSchemaVer1.TutorProfileTable.OccupationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.OccupationTable.cols.ORGANIZATION_NAME+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.OccupationTable.cols.DESIGNATION_NAME + ")"
            );
            Log.d(TAG, "6. Occupation table creation complete");
            //Duration table for Occupation
            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.DurationTable.NAME_OCCUPATION + "(" +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_YEARS+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS+ "," +
                    ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_DAYS + ")"
            );
            Log.d(TAG, "7. Occupation Duration table creation complete");
            //--------------------TEACHING CREDITS TABLE--------------------------------------------------------------
            db.execSQL("create table " + ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.NAME + "(" +
                    ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.cols.EMAIL_ID + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.cols.AVAILABLE_CREDITS + "," +
                    ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.cols.DATE_OF_EXPIRY + ")"
            );
            Log.d(TAG, "8. Teaching Credits table creation complete");
        }

        //Tables common to both Learner and Tutor
        //-------------------------- LOCATION TABLE--------------------------------------------------------------
        db.execSQL("create table " + ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.NAME + "(" +
                ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.cols.EMAIL_ID + "," +
                ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.cols.LATITUDE + "," +
                ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.cols.LONGITUDE + ")"
        );
        Log.d(TAG, "User Location table creation complete");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    /**@param myProfile The learner profile
     * @return Returns a content value representing a Generic learner profile
     * */
    private static ContentValues getContentValues(GenericLearnerProfile myProfile){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.cols.NAME, myProfile.getName());
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.cols.EMAIL_ID, myProfile.getEmailID());
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.cols.PHONE_NO, myProfile.getPhoneNo());
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.cols.CURRENT_STATUS, myProfile.getCurrentStatus());
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.cols.PASSWORD, myProfile.getPassword());
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.cols.DISPLAY_PIC_URI, myProfile.getDisplayPicturePath());

        return values;
    }

    /**@param myProfile The tutor profile
     * @return Returns a content value representing a Tutor profile
     * */
    private static ContentValues getContentValues(TutorProfile myProfile){

        //Get the base values
        ContentValues values = getContentValues((GenericLearnerProfile) myProfile);
        //Add the TutorProfile specific values.
        values.put(ProfileDbSchemaVer1.TutorProfileTable.cols.RATING, myProfile.getRating());
        values.put(ProfileDbSchemaVer1.TutorProfileTable.cols.TUTOR_TYPES, ArraysUtil.convertArrayToString(myProfile.getTutorTypes()));
        values.put(ProfileDbSchemaVer1.TutorProfileTable.cols.SUBJECT_TYPES, ArraysUtil.convertArrayToString(myProfile.getDisciplines()));
        //Add the educational qualification and occupation separately
        return values;
    }
    /**@param educationalQualification The educational qualification of the Tutor
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the educational qualification
     * */
    private static ContentValues getContentValues(EducationalQualification educationalQualification, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.QUALIFICATION_NAME, educationalQualification.getmQualificationName());
        values.put(ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME, educationalQualification.getInstitution());
        values.put(ProfileDbSchemaVer1.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING, educationalQualification.getYearOfPassing());
        //Add duration table separately

        return values;
    }
    /**@param secondaryEducationalQualificationParcelable The secondary educational qualification of the Tutor which has a Board Name on the base educational params
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the educational qualification
     * */
    private static ContentValues getContentValues(SecondaryEducationalQualification secondaryEducationalQualificationParcelable, String KEY_emailId){

        ContentValues values;

        values = getContentValues((EducationalQualification)secondaryEducationalQualificationParcelable, KEY_emailId);
        values.put(ProfileDbSchemaVer1.TutorProfileTable.SecondaryEducationalQualificationTable.cols.BOARD_NAME, secondaryEducationalQualificationParcelable.getmBoard());
        //Add duration table separately

        return values;
    }
    /**@param seniorSecondaryEducationalQualificationParcelable The senior secondary educational qualification of the Tutor which has a Board Name on the base educational params
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the educational qualification
     * */
    private static ContentValues getContentValues(SeniorSecondaryEducationalQualification seniorSecondaryEducationalQualificationParcelable, String KEY_emailId){

        ContentValues values;

        values = getContentValues((EducationalQualification)seniorSecondaryEducationalQualificationParcelable, KEY_emailId);
        values.put(ProfileDbSchemaVer1.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.cols.BOARD_NAME, seniorSecondaryEducationalQualificationParcelable.getmBoard());
        //Add duration table separately

        return values;
    }

    /**@param duration duration in Years/Months/Days
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing this duration
     * */
    private static ContentValues getContentValues(Duration duration, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_YEARS, duration.getNoOfYears());
        values.put(ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS, duration.getNoOfMonths());
        values.put(ProfileDbSchemaVer1.TutorProfileTable.DurationTable.cols.NO_OF_DAYS, duration.getNoOfDays());

        return values;
    }
    /**@param occupation The occupation of the tutor
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing this occupation
     * */
    private static ContentValues getContentValues(Occupation occupation, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchemaVer1.TutorProfileTable.OccupationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchemaVer1.TutorProfileTable.OccupationTable.cols.ORGANIZATION_NAME, occupation.getCurrentOrganization());
        values.put(ProfileDbSchemaVer1.TutorProfileTable.OccupationTable.cols.DESIGNATION_NAME, occupation.getCurrentDesignation());
        //Add duration table separately

        return values;
    }
    /**@param teachingCredits The credits as per the credits model
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the teaching credits
     * */
    private static ContentValues getContentValues(Credits teachingCredits, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.cols.AVAILABLE_CREDITS, teachingCredits.getAvailableCredits());
        values.put(ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.cols.DATE_OF_EXPIRY, teachingCredits.getDateOfExpiryOfCredits().getTime());
        return values;
    }
    /**@param lastKnownLocation The last known  location(latitude & longitude) of the user
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the teaching credits
     * */
    private static ContentValues getContentValues(LatLng lastKnownLocation, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.cols.LATITUDE, lastKnownLocation.latitude);
        values.put(ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.cols.LONGITUDE, lastKnownLocation.longitude);
        return values;
    }

    public static void addProfileToDatabase(SQLiteDatabase database, GenericLearnerProfile profile){

        ContentValues profileValues = null;

        if(profile instanceof TutorProfile){

            TutorProfile tutorProfile = (TutorProfile) profile;
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
            database.insertOrThrow(ProfileDbSchemaVer1.TutorProfileTable.NAME, null, profileValues);

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


            //The teaching credits for the Tutor
            if(tutorProfile.getTeachingCredits() != null){
                ContentValues teachingCredits = getContentValues(tutorProfile.getTeachingCredits(), tutorProfile.getEmailID());
                database.insertOrThrow(ProfileDbSchemaVer1.TutorProfileTable.TeachingCreditsTable.NAME, null, teachingCredits);
            }

        }
        else{
            profileValues = getContentValues(profile);
            database.insertOrThrow(ProfileDbSchemaVer1.LearnerProfileTable.NAME, null, profileValues);
        }
        //Location has to be added in either case
        if(profile.getLastKnownGeoCoordinates() != null){
            ContentValues locationValues = getContentValues(profile.getLastKnownGeoCoordinates(), profile.getEmailID());
            database.insertOrThrow(ProfileDbSchemaVer1.LearnerProfileTable.LocationTable.NAME, null, locationValues);
        }

    }

    public static void updateProfileInDatabase(SQLiteDatabase database, GenericLearnerProfile profile){

        //Assumption is that the Email ID will be unique for the accounts.
        String emailId = profile.getEmailID();
        ContentValues values = getContentValues(profile);
        database.update(ProfileDbSchemaVer1.LearnerProfileTable.NAME, values,
                ProfileDbSchemaVer1.LearnerProfileTable.cols.EMAIL_ID + "=?", new String[]{emailId});
    }

    private static boolean isExistingDatabase(Context context, String databaseName) {

        File dbPath = context.getDatabasePath(databaseName);
        if(dbPath != null){
            return true;
        }
        return false;
    }

    public static boolean isExistingUserAccount(Context context){
        return isExistingDatabase(context, ProfileDbHelperVer1.DATABASE_NAME);
    }
}
