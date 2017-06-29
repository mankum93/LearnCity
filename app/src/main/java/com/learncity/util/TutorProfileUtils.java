package com.learncity.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.DateTime;
import com.learncity.backend.tutorApi.model.DurationVer1;
import com.learncity.backend.tutorApi.model.EducationalQualificationVer1;
import com.learncity.backend.tutorApi.model.LocationInfo;
import com.learncity.backend.tutorApi.model.OccupationVer1;
import com.learncity.backend.tutorApi.model.TutorAccount;
import com.learncity.backend.tutorApi.model.TutorProfileVer1;
import com.learncity.generic.learner.account.Account;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.tutor.account.profile.model.Duration;
import com.learncity.tutor.account.profile.model.TeachingCredits;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.tutor.account.profile.model.occupation.Occupation;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by DJ on 6/29/2017.
 */

public final class TutorProfileUtils {

    private static final String TAG = TutorProfileUtils.class.getSimpleName();

    public static TutorProfileVer1 populateProfileEntity(@NonNull TutorProfile profile, @Nullable TutorProfileVer1 profileEntity) {
        //Populate the entity object with the profile info.

        if (profile == null) {
            Log.d(TAG, "There is no profile to populate the entity.");
            return null;
        }
        if (profileEntity == null) {
            profileEntity = new TutorProfileVer1();
        }
        com.learncity.backend.tutorApi.model.LatLng ll = null;
        if (profile.getLastKnownGeoCoordinates() != null) {
            ll = new com.learncity.backend.tutorApi.model.LatLng();
            ll.setLatitude(profile.getLastKnownGeoCoordinates().latitude);
            ll.setLongitude(profile.getLastKnownGeoCoordinates().longitude);
        }

        if (profile.getEducationalQualifications() != null) {
            EducationalQualificationVer1[] ed1 = new EducationalQualificationVer1[profile.getEducationalQualifications().length];
            int i = 0;
            for (EducationalQualification ed : profile.getEducationalQualifications()) {
                Duration d = ed.getDuration();
                DurationVer1 d1 = new DurationVer1();
                d1.setNoOfYears(d.getNoOfYears());
                d1.setNoOfMonths(d.getNoOfMonths());
                d1.setNoOfDays(d.getNoOfDays());

                ed1[i] = new EducationalQualificationVer1();
                ed1[i].setInstitution(ed.getInstitution());
                ed1[i].setQualificationName(ed.getmQualificationName());
                ed1[i].setDuration(d1);
                ed1[i].setYearOfPassing(ed.getYearOfPassing());
            }
            profileEntity.setEducationalQualifications(Arrays.asList(ed1));
        }

        // Occupation
        if (profile.getOccupation() != null) {
            OccupationVer1 o = new OccupationVer1();
            Duration d = profile.getOccupation().getCurrentExperience();
            DurationVer1 d1 = new DurationVer1();
            d1.setNoOfYears(d.getNoOfYears());
            d1.setNoOfMonths(d.getNoOfMonths());
            d1.setNoOfDays(d.getNoOfDays());

            o.setCurrentDesignation(profile.getOccupation().getCurrentDesignation());
            o.setCurrentOrganization(profile.getOccupation().getCurrentOrganization());
            o.setCurrentExperience(d1);

            profileEntity.setOccupation(o);
        }

        // Tutor types
        String[] tutorTypes = profile.getTutorTypes();
        if (tutorTypes != null) {
            profileEntity.setTutorTypes(Arrays.asList(tutorTypes));
        }

        // Disciplines
        String[] disciplines = profile.getDisciplines();
        if (disciplines != null) {
            profileEntity.setDisciplines(Arrays.asList(disciplines));
        }

        // Teaching credits
        // NOTE: Won't be null because of being set explicitly.
        TeachingCredits c = profile.getTeachingTeachingCredits();
        // Entity
        com.learncity.backend.tutorApi.model.TeachingCredits teachingCredits = new com.learncity.backend.tutorApi.model.TeachingCredits();
        teachingCredits.setAvailableCredits(c.getAvailableCredits());
        teachingCredits.setDateOfExpiryOfCredits(new DateTime(c.getDateOfExpiryOfCredits()));
        profileEntity.setTeachingCredits(teachingCredits);

        profileEntity.setName(profile.getName());
        profileEntity.setEmailID(profile.getEmailID());
        profileEntity.setPhoneNo(profile.getPhoneNo());
        profileEntity.setPassword(profile.getPassword());
        profileEntity.setCurrentStatus(profile.getCurrentStatus());
        profileEntity.setLastKnownGeoCoordinates(ll);
        profileEntity.setDisplayPicturePath(profile.getDisplayPicturePath());

        return profileEntity;
    }

    public static List<TutorProfile> populateProfilesFromEntities(List<TutorProfileVer1> profiles) {
        List<TutorProfile> pi = new ArrayList<TutorProfile>(profiles.size());
        for (TutorProfileVer1 p : profiles) {
            pi.add(populateProfileFromEntity(null, p));
        }
        return pi;
    }

    public static TutorProfile populateProfileFromEntity(@Nullable TutorProfile profile, @NonNull TutorProfileVer1 profileEntity) {

        if (profileEntity == null) {
            Log.d(TAG, "There is no profile entity to populate the profile.");
            return null;
        }
        if (profile == null) {
            // Extracting educational qualifications
            EducationalQualification[] ed1 = null;
            if (profileEntity.getEducationalQualifications() != null) {
                ed1 = new EducationalQualification[profileEntity.getEducationalQualifications().size()];

                int i = 0;
                for (EducationalQualificationVer1 ed : profileEntity.getEducationalQualifications()) {
                    DurationVer1 d1 = ed.getDuration();
                    Duration d = null;
                    if (d1 != null) {
                        d = new Duration(d1.getNoOfYears(), d1.getNoOfMonths(), d1.getNoOfDays());
                    }

                    ed1[i] = new EducationalQualification(ed.getQualificationName(), ed.getInstitution(), d);
                    ed1[i].setYearOfPassing(ed.getYearOfPassing());
                    i++;
                }
            }

            // Extracting Occupation
            Occupation o = null;
            if (profileEntity.getOccupation() != null) {
                DurationVer1 d2 = profileEntity.getOccupation().getCurrentExperience();
                Duration d3 = null;
                if (d2 != null) {
                    d3 = new Duration(d2.getNoOfYears(), d2.getNoOfMonths(), d2.getNoOfDays());
                }

                o = new Occupation(profileEntity.getOccupation().getCurrentOrganization(), d3,
                        profileEntity.getOccupation().getCurrentDesignation());
            }

            LatLng l = null;
            if ((profileEntity.getLastKnownGeoCoordinates() != null)) {
                l = new LatLng(profileEntity.getLastKnownGeoCoordinates().getLatitude()
                        , profileEntity.getLastKnownGeoCoordinates().getLongitude());
            }

            TeachingCredits c = null;
            if (profileEntity.getTeachingCredits() != null) {
                try {
                    c = new TeachingCredits(profileEntity.getTeachingCredits().getAvailableCredits(),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                    .parse(profileEntity.getTeachingCredits().getDateOfExpiryOfCredits().toStringRfc3339()));
                } catch (ParseException p) {
                    throw new RuntimeException("Date format parse error");
                }
            }
            Integer _r = profileEntity.getRating();
            int r = _r == null ? -1 : _r;

            Integer s;
            profile = new TutorProfile.Builder(
                    profileEntity.getName() == null ? "" : profileEntity.getName(),
                    profileEntity.getEmailID() == null ? "" : profileEntity.getEmailID(),
                    profileEntity.getPhoneNo() == null ? "" : profileEntity.getPhoneNo(),
                    (s = profileEntity.getCurrentStatus()) == null ? GenericLearnerProfile.STATUS_UNDEFINED :
                            (s != GenericLearnerProfile.STATUS_LEARNER || s != GenericLearnerProfile.STATUS_TUTOR ? GenericLearnerProfile.STATUS_UNDEFINED : s),
                    profileEntity.getPassword() == null ? GenericLearnerProfile.PASSWORD_NULL : profileEntity.getPassword())
                    .withImagePath(profileEntity.getDisplayPicturePath())
                    .withGeoCoordinates(l)
                    .withEducationalQualifications(ed1)
                    .withOccupation(o)
                    .withDisciplines(profileEntity.getDisciplines() != null ? profileEntity.getDisciplines().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTutorTypes(profileEntity.getTutorTypes() != null ? profileEntity.getTutorTypes().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTeachingCredits(c)
                    .withRating(r <= 0 ? 0 : r >= 5 ? 5 : r)
                    .build();
        } else {
            // Extracting educational qualifications
            EducationalQualification[] ed1 = null;
            if (profileEntity.getEducationalQualifications() != null) {
                ed1 = new EducationalQualification[profileEntity.getEducationalQualifications().size()];

                int i = 0;
                for (EducationalQualificationVer1 ed : profileEntity.getEducationalQualifications()) {
                    DurationVer1 d1 = ed.getDuration();
                    Duration d = null;
                    if (d1 != null) {
                        d = new Duration(d1.getNoOfYears(), d1.getNoOfMonths(), d1.getNoOfDays());
                    }

                    ed1[i] = new EducationalQualification(ed.getQualificationName(), ed.getInstitution(), d);
                    ed1[i].setYearOfPassing(ed.getYearOfPassing());
                    i++;
                }
            }

            // Extracting Occupation
            Occupation o = null;
            if (profileEntity.getOccupation() != null) {
                DurationVer1 d2 = profileEntity.getOccupation().getCurrentExperience();
                Duration d3 = null;
                if (d2 != null) {
                    d3 = new Duration(d2.getNoOfYears(), d2.getNoOfMonths(), d2.getNoOfDays());
                }

                o = new Occupation(profileEntity.getOccupation().getCurrentOrganization(), d3,
                        profileEntity.getOccupation().getCurrentDesignation());
            }

            LatLng l = null;
            if ((profileEntity.getLastKnownGeoCoordinates() == null)) {
                l = new LatLng(profileEntity.getLastKnownGeoCoordinates().getLatitude()
                        , profileEntity.getLastKnownGeoCoordinates().getLongitude());
            }

            TeachingCredits c = null;
            if (profileEntity.getTeachingCredits() != null) {
                try {
                    c = new TeachingCredits(profileEntity.getTeachingCredits().getAvailableCredits(),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                    .parse(profileEntity.getTeachingCredits().getDateOfExpiryOfCredits().toStringRfc3339()));
                } catch (ParseException p) {
                    throw new RuntimeException("Date format parse error");
                }
            }
            Integer _r = profileEntity.getRating();
            int r = _r == null ? -1 : _r;

            Integer s;
            profile.getTutorProfileBuilder()
                    .withName(profileEntity.getName() == null ? "" : profileEntity.getName())
                    .withEmailID(profileEntity.getEmailID() == null ? "" : profileEntity.getEmailID())
                    .withPhoneNo(profileEntity.getPhoneNo() == null ? "" : profileEntity.getPhoneNo())
                    .withCurrentStatus((s = profileEntity.getCurrentStatus()) == null ? GenericLearnerProfile.STATUS_UNDEFINED :
                            (s != GenericLearnerProfile.STATUS_LEARNER || s != GenericLearnerProfile.STATUS_TUTOR ? GenericLearnerProfile.STATUS_UNDEFINED : s))
                    .withPassword(profileEntity.getPassword() == null ? GenericLearnerProfile.PASSWORD_NULL : profileEntity.getPassword())
                    .withImagePath(profileEntity.getDisplayPicturePath())
                    .withGeoCoordinates(l)
                    .withEducationalQualifications(ed1)
                    .withOccupation(o)
                    .withDisciplines(profileEntity.getDisciplines() != null ? profileEntity.getDisciplines().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTutorTypes(profileEntity.getTutorTypes() != null ? profileEntity.getTutorTypes().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTeachingCredits(c)
                    .withRating(r <= 0 ? 0 : r >= 5 ? 5 : r)
                    .build();
        }

        return profile;
    }

    public static List<com.learncity.tutor.account.TutorAccount> refactorAccountsToAccountEntities(List<TutorAccount> accounts){

        if(accounts == null || accounts.isEmpty()){
            return null;
        }

        // Extract the list of tutorRequestRecordList from backend
        List<TutorProfileVer1> profiles = new ArrayList<TutorProfileVer1>(accounts.size());
        List<Account.LocationInfo> locationInfos = new ArrayList<Account.LocationInfo>(accounts.size());
        List<UUID> userUUIDs = new ArrayList<UUID>(accounts.size());
        for(TutorAccount acc : accounts){
            profiles.add(acc.getProfile());

            LocationInfo li = acc.getLocationInfo();
            if(li != null){
                locationInfos.add(new Account.LocationInfo(li.getShortFormattedAddress()));
            }
            else{
                locationInfos.add(null);
            }

            userUUIDs.add(UUID.fromString(acc.getEmailBasedUUID()));
        }

        // Populate with Account fields
        List<com.learncity.tutor.account.TutorAccount> acc = new ArrayList<com.learncity.tutor.account.TutorAccount>(accounts.size());
        List<TutorProfile> refactoredProfiles = populateProfilesFromEntities(profiles);
        int i = 0;
        for(TutorProfile p : refactoredProfiles){
            com.learncity.tutor.account.TutorAccount t = new com.learncity.tutor.account.TutorAccount(p);
            t.setLocationInfo(locationInfos.get(i));
            t.setEmailBasedUUID(userUUIDs.get(i));
            acc.add(t);
            i++;
        }
        return acc;
    }

    public static com.learncity.tutor.account.TutorAccount[] refactorAccountsToArray(List<TutorAccount> accounts){

        List<com.learncity.tutor.account.TutorAccount> refactoredProfiles = refactorAccountsToAccountEntities(accounts);
        return refactoredProfiles.toArray(new com.learncity.tutor.account.TutorAccount[refactoredProfiles.size()]);
    }
}
