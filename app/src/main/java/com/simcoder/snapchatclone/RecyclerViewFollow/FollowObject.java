package com.simcoder.snapchatclone.RecyclerViewFollow;

/**
 * Created by simco on 1/24/2018.
 */

public class FollowObject {
    private String email;
    private String uid;

    public FollowObject(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public String getUid(){
        return uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
}
