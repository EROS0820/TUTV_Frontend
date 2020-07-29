package com.backstagesupporters.fasttrack.responseClass;

import com.google.gson.annotations.SerializedName;

public class ProfileInfoResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("profile")
    private Profile profile;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
//    ===============================================================================

    public static  class Profile {
        private String email;

        private String profession;

        private String dob;

        private String name;

        private String gender;

        private String profile_pic;

        private String mobile_verification;

        private String email_verification;

        private String mobile;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getProfile_pic() {
            return profile_pic;
        }

        public void setProfile_pic(String profile_pic) {
            this.profile_pic = profile_pic;
        }

        public String getMobile_verification() {
            return mobile_verification;
        }

        public void setMobile_verification(String mobile_verification) {
            this.mobile_verification = mobile_verification;
        }

        public String getEmail_verification() {
            return email_verification;
        }

        public void setEmail_verification(String email_verification) {
            this.email_verification = email_verification;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

}
