package com.lynknow.api.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRequest {

    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String country;
    private UserPhoneDetailRequest whatsappNo;
    private UserPhoneDetailRequest mobileNo;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UserPhoneDetailRequest getWhatsappNo() {
        return whatsappNo;
    }

    public void setWhatsappNo(UserPhoneDetailRequest whatsappNo) {
        this.whatsappNo = whatsappNo;
    }

    public UserPhoneDetailRequest getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(UserPhoneDetailRequest mobileNo) {
        this.mobileNo = mobileNo;
    }
}
