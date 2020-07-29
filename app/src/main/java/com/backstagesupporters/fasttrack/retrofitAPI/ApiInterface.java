package com.backstagesupporters.fasttrack.retrofitAPI;

import com.backstagesupporters.fasttrack.models.Complaint;
import com.backstagesupporters.fasttrack.models.Driver;
import com.backstagesupporters.fasttrack.models.DriverEdit;
import com.backstagesupporters.fasttrack.models.SubUser2;
import com.backstagesupporters.fasttrack.models.Support;
import com.backstagesupporters.fasttrack.modules.RegistrationModule;
import com.backstagesupporters.fasttrack.responseClass.*;
import com.google.gson.JsonElement;


import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;

public interface ApiInterface {
    @POST(EndApi.REGISTER_USER)
    Call<JsonElement> registerUser(@Body RegistrationModule registrationModule);

    @POST(EndApi.LOGIN_USER)
    Call<LoginResponse> login(@Query("email") String email,
                              @Query("password") String password);

    @POST(EndApi.LOGIN_USER)
    Call<LoginResponse> login(@Query("email") String email,
                              @Query("password") String password,
                              @Query("mobile") String mobile,
                              @Query("device_type") String device_type,
                              @Query("device_token") String device_token);
    @POST(EndApi.LOGIN_USER)
    Call<LoginResponse> login(@Query("email") String email,
                                @Query("password") String password,
                                @Query("mobile") String mobile,
                                @Query("device_type") String device_type,
                                @Query("device_imei") String deviceIMEI,
                                @Query("device_token") String device_token);

    @POST(EndApi.LOGIN_USER)
    Call<LoginResponse2> login2(@Query("email") String email,
                                @Query("password") String password,
                                @Query("mobile") String mobile,
                                @Query("device_type") String device_type,
                                @Query("device_imei") String deviceIMEI,
                                @Query("device_token") String device_token);

    @POST(EndApi.LOGIN_USER)
    Call<LoginResponse> login2(@Query("email") String email,
                              @Query("password") String password,
                              @Query("mobile") String mobile );


    @GET(EndApi.PROFILE_INFO)
    Call<ProfileInfoResponse> profileInfo(@Query("token") String token);

    @POST(EndApi.LOGIN_USER)
    Call<LogoutResponse> logout(@Query("user_id ") String user_id);


    @POST(EndApi.UPDATE_PROFILE)
    Call<UpdateProfileResponse> updateUserProfile(
            @Query("token") String token,
            @Query("name") String name,
            @Query("mobile") String mobile,
            @Query("dob") String dob,
            @Query("gender") String gender,
            @Query("email") String profession,
            @Query("profile_pic") String profile_pic
//            @Part("profile_pic") RequestBody file
    );

    @Multipart
    @POST(EndApi.UPDATE_PROFILE)
    Call<UpdateProfileResponse> updateUserProfile2(
            @Part("token") RequestBody token,
            @Part("name") RequestBody name,
            @Part("mobile") RequestBody mobile,
            @Part("dob") RequestBody dob,
            @Part("gender") RequestBody gender,
            @Part("email") RequestBody profession,
            @Part("profile_pic") RequestBody file
            );


    @POST(EndApi.UPDATE_LOCATION)
    Call<ShowLocationResponse> updateLocation(@Query("token") String token,
                                   @Query("latitude") String latitude,
                                   @Query("longitude") String longitude,
                                   @Query("vehicle_id") String vehicle_id);

    @POST(EndApi.UPDATE_PROFILE_PICTURE)
    @FormUrlEncoded
    Call<UpdateProfilePicture> updateProfilePicture(
            @Query("token") String token,
            @Field("profile_pic") String profilePic);


    @Multipart
    @POST("profile_pic")
    Call<String> uploadImage(
            @Part("token") RequestBody name,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST(EndApi.UPDATE_PROFILE_PICTURE)
    Call<UpdateProfilePicture> uploadImage1(
            @Part("token") RequestBody name,
            @Part MultipartBody.Part file
    );


    @POST(EndApi.FORGET_PASSWORD)
    Call<ForgetPasswordResponse> forgotPassword(@Query("email") String email);

    @GET(EndApi.SEND_OTP)
    Call<SendOTPResponse> sendOtp(@Query("token") String token);

    @POST(EndApi.VALIDATE_OTP)
    Call<SendOTPResponse> validateOtp(@Query("token") String token,
                                      @Query("otp") String otp);

    @POST(EndApi.CHANGE_PASSWORD)
    Call<SendOTPResponse> changePassword(@Query("token") String token,
                                         @Query("old_password") String old_password,
                                         @Query("new_password") String new_password);
    @POST(EndApi.VALIDATE_PASSWORD)
    Call<SendOTPResponse> validatePassword(@Query("otp") String otp,
                                         @Query("mobile") String mobile,
                                         @Query("password") String password);
    @POST(EndApi.VALIDATE_PASSWORD)
    Call<SendOTPResponse> validatePassword( @Query("mobile") String mobile,
                                           @Query("password") String password);
    @POST(EndApi.VALIDATE_PASSWORD)
    Call<SendOTPResponse> validatePassword1(  @Query("password") String password);

    @POST(EndApi.SOCIALLOGIN)
    Call<SocialLoginResponse> GoogleLogin(
            @Query("email") String email,
            @Query("name") String name,
            @Query("provider_id") String provider_id,
            @Query("provider") String provider);

    @GET(EndApi.SHOW_VEHICLE_LIST)
    Call<VehiclesResponse> showVehicles(@Query("token") String token);

    @GET(EndApi.SHOW_VEHICLE_LIST1)
    Call<VehiclesResponse> showVehiclesUserId(@Query("user_id") String user_id);

    @GET(EndApi.SHOW_DRIVER_LIST)
    Call<DriverResponse> showDrivers(@Query("token") String token);

    // show_devices
    @GET(EndApi.SHOW_DEVICES_LIST)
    Call<ShowDeviceResponse> showDevices(@Query("token") String token);

    // show_sub_users
    @GET(EndApi.SHOW_SUB_USERS)
    Call<ShowSubUserResponse> showSubUsers(@Query("token") String token);

    // show_location
    @GET(EndApi.SHOW_LOCATION)
    Call<ShowLocationResponse> showLocation(@Query("token") String token,
                                            @Query("vehicle_id") String vehicle_id);

    @GET(EndApi.SHOW_LOCATION1)
    Call<ShowLocationResponse> showLocation1(@Query("user_id") String token,
                                             @Query("vehicle_id") String vehicle_id);

    @GET(EndApi.SHOW_LOCATION2)
    Call<ShowLocationResponse> showLocation2(@Query("user_id") String token,
                                             @Query("vehicle_id") String vehicle_id);

    @POST(EndApi.ADD_DRIVER)
    Call<JsonElement> addDriver(@Body Driver driver);

    @POST(EndApi.EDIT_DRIVER)
    Call<JsonElement> editDriver(@Body DriverEdit driverEdit);

    // engine_status
    @GET(EndApi.ENGINE_STATUS)
    Call<EngineStatusResponse> engineStatus(@Query("vehicle_id") String vehicle_id,
                                            @Query("token") String token);
    @GET(EndApi.ENGINE_STATUS)
    Call<EngineStatusResponse> engineStatus(@Query("vehicle_id") String vehicle_id,
                                            @Query("engine_status") String engine_status,
                                            @Query("token") String token);
    @GET(EndApi.ENGINE_STATUS)
    Call<String> engineStatus2(@Query("vehicle_id") String vehicle_id,
                              @Query("engine_status") String engine_status,
                              @Query("token") String token);


    // car_parking
    @POST(EndApi.CAR_PARKING)
    Call<CarParkingResponse> carParking(@Query("vehicle_id") String vehicle_id,
                                        @Query("token") String token);

    // car_details
    @GET(EndApi.CAR_DETAILS)
    Call<CarDetailsResponse> carDetails(@Query("vehicle_id") String vehicle_id,
                                          @Query("token") String token);

    @POST(EndApi.SUPPORT)
    Call<JsonElement> support(@Body Support support);


    @POST(EndApi.COMPLAINT)
    Call<JsonElement> complaint(@Body Complaint complaint);


    @GET(EndApi.DISTANCE_REPORT)
    Call<DistanceReportResponse> distanceReport(@Query("token") String token,
                                                @Query("from_date") String from_date,
                                                @Query("to_date") String to_date,
                                                @Query("vehicle_id") String vehicle_id);


    @GET(EndApi.TRIP_REPORT)
    Call<TripReportResponse> tripReport(@Query("token") String token,
                                        @Query("from_date") String from_date,
                                        @Query("to_date") String to_date,
                                        @Query("vehicle_id") String vehicle_id);


    // HISTORY_REPLAY
    @GET(EndApi.HISTORY_REPLAY)
    Call<HistoryReplyResponse> historyReplay(@Query("token") String token,
                                             @Query("from_date") String from_date,
                                             @Query("to_date") String to_date,
                                             @Query("vehicle_id") String vehicle_id);
    @GET(EndApi.HISTORY_REPLAY)
    Call<HistoryReplyResponse> historyReplay(@Query("token") String token,
                                             @Query("date") String date,
                                             @Query("from_time") String from_time,
                                             @Query("to_time") String to_time,
                                             @Query("vehicle_id") String vehicle_id);


    @POST(EndApi.ADD_SUB_USERS)
    Call<JsonElement> addSubUser(@Body SubUser2 subUser2);


    @POST(EndApi.PROCESS_GPS_DATA)
    Call process_gps_data();


    @POST(EndApi.SUB_USER_STATUS)
    Call<SubUserStatusResponse> sub_user_status( @Query("token") String token,
                                                 @Query("user_id") String user_id,
                                                @Query("status") String status
                                                );
    // getSubUSerStatusCall(token,uId,accountStatus);

    @POST(EndApi.DELETE_SUB_USER)
    Call<SubUserStatusResponse> delete_sub_user(@Query("token") String token,
                                                @Query("user_id") String user_id);

    @POST(EndApi.SUB_USER_STATUS)
    Call<SubUserStatusResponse> sub_user_status(@Query("user_id") String vehicle_id,
                                                @Query("token") String token);

    @GET(EndApi.NOTIFICATION)
    Call<NotificationsResponse> notifications(@Query("token") String token);


    @GET(EndApi.CAR_ACTIVITY)
    Call<CarActivityResponse> car_activity(
            @Query("vehicle_id") String vehicle_id,
            @Query("token") String token);

    @GET(EndApi.CAR_ACTIVITY)
    Call<CarActivityResponse> car_activity2(
            @Query("token") String token,
            @Query("vehicle_id") String vehicle_id);

    @GET(EndApi.TICKER_MSG)
    Call<TickerResponse> tickerMessage(@Query("token") String token);

    @GET(EndApi.TICKER_MSG)
    Call<TickerResponse> tickerMessageUser(@Query("user_id") String user_id);

    @GET(EndApi.SPEED_LIMIT)
    Call<SpeedLimitResponse> speedLimit(
            @Query("vehicle_id") String vehicle_id,
            @Query("speed_max") String speed_max,
            @Query("token") String token
    );



}