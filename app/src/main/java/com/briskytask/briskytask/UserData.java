package com.briskytask.briskytask;

/**
 * Created by root on 28/1/17.
 */
public class UserData {
    private String id;
    private String name, username, email, address, phone, website, company;

    public UserData(String id, String name, String username, String email, String address, String website, String phone, String company) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
        this.website = website;
        this.phone = phone;
        this.company = company;
        //this.time_to_travel = time_to_travel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    /*public String getTime_to_travel(){
        return time_to_travel;
    }

    public void setTime_to_travel(String time_to_travel){
        this.time_to_travel = time_to_travel;
    }*/
}
