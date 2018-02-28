package com.simcoder.snapchatclone.RecyclerViewReceiver;

/**
 * Created by simco on 1/24/2018.
 */

public class ReceiverObject {
    private String email;
    private String uid;
    private Boolean receive;

    public ReceiverObject(String email, String uid, Boolean receive){
        this.email = email;
        this.uid = uid;
        this.receive = receive;
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

    public Boolean getReceive(){
        return receive;
    }
    public void setReceive(Boolean receive){
        this.receive = receive;
    }
}
