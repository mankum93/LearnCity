package com.learncity.generic.learner.account.profile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learner.account.profile.model.LearnerProfile;
import com.learncity.tutor.account.profile.model.TeachingCredits;
import com.learncity.tutor.account.profile.model.Duration;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.tutor.account.profile.model.occupation.Occupation;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualification;
import com.learncity.tutor.account.profile.model.qualification.educational.SecondaryEducationalQualification;
import com.learncity.tutor.account.profile.model.qualification.educational.SeniorSecondaryEducationalQualification;
import com.learncity.util.ArrayUtils;

import java.io.File;
import java.util.Date;

/**
 * Created by DJ on 10/23/2016.
 */

public class ProfileDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ProfileDbHelper";

    private static final int VERSION  = 1;
    public static final String DATABASE_NAME = "userProfile.db";
    private int currentAccountStatus;

    public ProfileDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public void createProfileTables(SQLiteDatabase db, int accountStatus){
        this.currentAccountStatus = accountStatus;

        //Currently, as onCreate() is called only once. What if the user somehow initiates the
        // AC creation with a different status then chosen at first, see what's to be done in that case.
        //TODO: Solve the problem in the above comment

        //Depending on the current status, create the Learner or Tutor A/C
        if(currentAccountStatus == GenericLearnerProfile.STATUS_LEARNER){
            //-------------------------- LEARNER PROFILE TABLES--------------------------------------------------------------
            db.execSQL("create table " + ProfileDbSchema.LearnerProfileTable.NAME + "(" +
                    ProfileDbSchema.LearnerProfileTable.cols.NAME + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PASSWORD + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.DISPLAY_PIC_URI + ")"
            );
            Log.d(TAG, "1. Learner table creation complete");

        }
        else if(currentAccountStatus == GenericLearnerProfile.STATUS_TUTOR){
            //--------------------------TUTOR PROFILE TABLES--------------------------------------------------------------

            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.NAME + "(" +
                    ProfileDbSchema.LearnerProfileTable.cols.NAME + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.PASSWORD + "," +
                    ProfileDbSchema.LearnerProfileTable.cols.DISPLAY_PIC_URI + "," +
                    ProfileDbSchema.TutorProfileTable.cols.RATING + "," +
                    ProfileDbSchema.TutorProfileTable.cols.TUTOR_TYPES + "," +
                    ProfileDbSchema.TutorProfileTable.cols.SUBJECT_TYPES + ")"
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
            //--------------------TEACHING CREDITS TABLE--------------------------------------------------------------
            db.execSQL("create table " + ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.NAME + "(" +
                    ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.EMAIL_ID + "," +
                    ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.AVAILABLE_CREDITS + "," +
                    ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.DATE_OF_EXPIRY + ")"
            );
            Log.d(TAG, "8. Teaching TeachingCredits table creation complete");
        }
        else{
            throw new IllegalStateException("User status is undefined. Unable to create Profile tables");
        }

        //Tables common to both Learner and Tutor
        //-------------------------- LOCATION TABLE--------------------------------------------------------------
        db.execSQL("create table " + ProfileDbSchema.LearnerProfileTable.LocationTable.NAME + "(" +
                ProfileDbSchema.LearnerProfileTable.LocationTable.cols.EMAIL_ID + "," +
                ProfileDbSchema.LearnerProfileTable.LocationTable.cols.LATITUDE + "," +
                ProfileDbSchema.LearnerProfileTable.LocationTable.cols.LONGITUDE + ")"
        );
        Log.d(TAG, "User Location table creation complete");
    }

    /**@param myProfile The learner profile
     * @return Returns a content value representing a Generic learner profile
     * */
    private static ContentValues getContentValues(GenericLearnerProfile myProfile){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.LearnerProfileTable.cols.NAME, myProfile.getName());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID, myProfile.getEmailID());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO, myProfile.getPhoneNo());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS, myProfile.getCurrentStatus());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.PASSWORD, myProfile.getPassword());
        values.put(ProfileDbSchema.LearnerProfileTable.cols.DISPLAY_PIC_URI, myProfile.getDisplayPicturePath());

        return values;
    }

    /**@param myProfile The tutor profile
     * @return Returns a content value representing a Tutor profile
     * */
    private static ContentValues getContentValues(TutorProfile myProfile){

        //Get the base values
        ContentValues values = getContentValues((GenericLearnerProfile) myProfile);
        //Add the TutorProfile specific values.
        values.put(ProfileDbSchema.TutorProfileTable.cols.RATING, myProfile.getRating());
        values.put(ProfileDbSchema.TutorProfileTable.cols.TUTOR_TYPES, ArrayUtils.convertArrayToString(myProfile.getTutorTypes()));
        values.put(ProfileDbSchema.TutorProfileTable.cols.SUBJECT_TYPES, ArrayUtils.convertArrayToString(myProfile.getDisciplines()));
        //Add the educational qualification and occupation separately
        return values;
    }
    /**@param educationalQualification The educational qualification of the Tutor
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the educational qualification
     * */
    private static ContentValues getContentValues(EducationalQualification educationalQualification, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.QUALIFICATION_NAME, educationalQualification.getmQualificationName());
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME, educationalQualification.getInstitution());
        values.put(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING, educationalQualification.getYearOfPassing());
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
        values.put(ProfileDbSchema.TutorProfileTable.SecondaryEducationalQualificationTable.cols.BOARD_NAME, secondaryEducationalQualificationParcelable.getmBoard());
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
        values.put(ProfileDbSchema.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.cols.BOARD_NAME, seniorSecondaryEducationalQualificationParcelable.getmBoard());
        //Add duration table separately

        return values;
    }

    /**@param duration duration in Years/Months/Days
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing this duration
     * */
    private static ContentValues getContentValues(Duration duration, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS, duration.getNoOfYears());
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS, duration.getNoOfMonths());
        values.put(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS, duration.getNoOfDays());

        return values;
    }
    /**@param occupation The occupation of the tutor
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing this occupation
     * */
    private static ContentValues getContentValues(Occupation occupation, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.ORGANIZATION_NAME, occupation.getCurrentOrganization());
        values.put(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.DESIGNATION_NAME, occupation.getCurrentDesignation());
        //Add duration table separately

        return values;
    }
    /**@param teachingTeachingCredits The credits as per the credits model
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the teaching credits
     * */
    private static ContentValues getContentValues(TeachingCredits teachingTeachingCredits, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.AVAILABLE_CREDITS, teachingTeachingCredits.getAvailableCredits());
        values.put(ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.DATE_OF_EXPIRY, teachingTeachingCredits.getDateOfExpiryOfCredits().getTime());
        return values;
    }
    /**@param lastKnownLocation The last known  location(latitude & longitude) of the user
     * @param KEY_emailId This is the foreign key
     * @return Returns a content value representing the teaching credits
     * */
    private static ContentValues getContentValues(LatLng lastKnownLocation, String KEY_emailId){

        ContentValues values = new ContentValues();
        values.put(ProfileDbSchema.LearnerProfileTable.LocationTable.cols.EMAIL_ID, KEY_emailId);
        values.put(ProfileDbSchema.LearnerProfileTable.LocationTable.cols.LATITUDE, lastKnownLocation.latitude);
        values.put(ProfileDbSchema.LearnerProfileTable.LocationTable.cols.LONGITUDE, lastKnownLocation.longitude);
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
            database.insertOrThrow(ProfileDbSchema.TutorProfileTable.NAME, null, profileValues);

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
            if(tutorProfile.getTeachingTeachingCredits() != null){
                ContentValues teachingCredits = getContentValues(tutorProfile.getTeachingTeachingCredits(), tutorProfile.getEmailID());
                database.insertOrThrow(ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.NAME, null, teachingCredits);
            }

        }
        else{
            profileValues = getContentValues(profile);
            database.insertOrThrow(ProfileDbSchema.LearnerProfileTable.NAME, null, profileValues);
        }
        //Location has to be added in either case
        if(profile.getLastKnownGeoCoordinates() != null){
            ContentValues locationValues = getContentValues(profile.getLastKnownGeoCoordinates(), profile.getEmailID());
            database.insertOrThrow(ProfileDbSchema.LearnerProfileTable.LocationTable.NAME, null, locationValues);
        }

    }

    public static void updateProfileInDatabase(SQLiteDatabase database, GenericLearnerProfile profile){

        //Assumption is that the Email ID will be unique for the accounts.
        String emailId = profile.getEmailID();
        ContentValues values = getContentValues(profile);
        database.update(ProfileDbSchema.LearnerProfileTable.NAME, values,
                ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID + "=?", new String[]{emailId});
    }

    public static File isExistingDatabase(Context context, String databaseName) {

        File dbPath = context.getDatabasePath(databaseName);
        return dbPath;
    }

    public GenericLearnerProfile isExistingUserAccount(){
        SQLiteDatabase db = getWritableDatabase();
        //Check for Learner's table
        Cursor c = db.rawQuery("SELECT name FROM " + "sqlite_master" + " WHERE type = 'table' AND name = ?",
                new String[]{ProfileDbSchema.LearnerProfileTable.NAME});
        try{
            if(c.getCount() == 0){
                c.close();
                c = db.rawQuery("SELECT name FROM " + "sqlite_master" + " WHERE type = 'table' AND name = ?",
                        new String[]{ProfileDbSchema.TutorProfileTable.NAME});
                try{
                    if(c.getCount() == 0){
                        return null;
                    }
                    else{
                        return constructUserProfile(db, GenericLearnerProfile.STATUS_TUTOR);
                    }
                }
                finally{
                    c.close();
                }
            }
            else{
                c.close();
                return constructUserProfile(db, GenericLearnerProfile.STATUS_LEARNER);
            }
        }
        finally{
            db.close();
        }
    }


    public GenericLearnerProfile constructUserProfile(SQLiteDatabase db, int userStatus){
        if(!db.isOpen()){
            throw new SQLiteDatabaseLockedException("Database not open for reading");
        }
        Cursor c;
        GenericLearnerProfile profile;

        if(userStatus == GenericLearnerProfile.STATUS_LEARNER){
            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.LearnerProfileTable.NAME, null);
            if(!c .moveToFirst()){
                return null;
            }
            profile = new LearnerProfile.Builder(
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.NAME)),
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID)),
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO)),
                    Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS))),
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.PASSWORD)))
                    .withImagePath(c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.DISPLAY_PIC_URI)))
                    .build();

            c.close();
        }
        else if(userStatus == GenericLearnerProfile.STATUS_TUTOR){
            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.NAME, null);
            if(!c .moveToFirst()){
                return null;
            }

            TutorProfile.Builder builder;
            builder = new TutorProfile.Builder(
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.NAME)),
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.EMAIL_ID)),
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.PHONE_NO)),
                    Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.CURRENT_STATUS))),
                    c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.PASSWORD)))
                    .withImagePath(c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.cols.DISPLAY_PIC_URI)))
                    .withRating(Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.cols.RATING))))
                    .withTutorTypes(ArrayUtils.convertStringToArray(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.cols.TUTOR_TYPES))))
                    .withDisciplines(ArrayUtils.convertStringToArray(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.cols.SUBJECT_TYPES))));
            c.close();

            SecondaryEducationalQualification seq = null;
            Duration d;
            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.SecondaryEducationalQualificationTable.NAME, null);
            if(c .moveToFirst()){
                seq = new SecondaryEducationalQualification(
                        c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.SecondaryEducationalQualificationTable.cols.BOARD_NAME)),
                        c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME)),
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING))));
            }
            c.close();

            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.DurationTable.NAME_SECONDARYEDUCATIONALQUALIFICATION, null);
            if(c.moveToFirst()){
                d = new Duration(
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS))),
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS))),
                        Long.parseLong(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS)))
                );
                if(seq != null){
                    seq.setDuration(d);
                }
            }
            c.close();

            SeniorSecondaryEducationalQualification sseq = null;
            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.NAME, null);
            if(c .moveToFirst()){
                sseq = new SeniorSecondaryEducationalQualification(
                        c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.SeniorSecondaryEducationalQualificationTable.cols.BOARD_NAME)),
                        c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.INSTITUTION_NAME)),
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.EducationalQualificationTable.cols.YEAR_PASSING))));
            }
            c.close();

            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.DurationTable.NAME_SENIORSECONDARYEDUCATIONALQUALIFICATION, null);
            if(c.moveToFirst()){
                d = new Duration(
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS))),
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS))),
                        Long.parseLong(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS)))
                );
                if(sseq != null){
                    sseq.setDuration(d);
                }
            }
            c.close();

            builder.withEducationalQualifications(new EducationalQualification[]{seq, sseq});

            Occupation o = null;
            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.OccupationTable.NAME, null);
            if(c .moveToFirst()){
                o = new Occupation(
                        c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.ORGANIZATION_NAME)),
                        c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.OccupationTable.cols.DESIGNATION_NAME)));
            }
            c.close();

            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.DurationTable.NAME_OCCUPATION, null);
            if(c.moveToFirst()){
                d = new Duration(
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_YEARS))),
                        Integer.parseInt(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_MONTHS))),
                        Long.parseLong(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.DurationTable.cols.NO_OF_DAYS)))
                );
                if(o != null){
                    o.setCurrentExperience(d);
                }
            }
            c.close();

            builder.withOccupation(o);

            c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.NAME, null);
            if(c .moveToFirst()){
                builder.withTeachingCredits(new TeachingCredits(
                        Long.parseLong(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.AVAILABLE_CREDITS))),
                        new Date(Long.parseLong(c.getString(c.getColumnIndex(ProfileDbSchema.TutorProfileTable.TeachingCreditsTable.cols.DATE_OF_EXPIRY))))
                ));
            }
            c.close();

            profile = builder.build();

        }
        else{
            throw new RuntimeException("Invalid status");
        }

        //Location at last
        c = db.rawQuery("SELECT * FROM " + ProfileDbSchema.LearnerProfileTable.LocationTable.NAME, null);
        if(c.moveToFirst()) {
            profile.setLastKnownGeoCoordinates(new LatLng(
                    Double.parseDouble(c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.LocationTable.cols.LATITUDE))),
                    Double.parseDouble(c.getString(c.getColumnIndex(ProfileDbSchema.LearnerProfileTable.LocationTable.cols.LONGITUDE)))
            ));
        }
        c.close();


        return profile;
    }
}
