package com.netsurf.quarterpillars.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("displayname")
    @Expose
    private String displayname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("img_url")
    @Expose
    private String imgUrl;
    @SerializedName("message")
    @Expose
    private String message;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
