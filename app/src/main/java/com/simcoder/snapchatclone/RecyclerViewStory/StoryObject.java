package com.simcoder.snapchatclone.RecyclerViewStory;

/**
 * Created by simco on 1/24/2018.
 */

public class StoryObject {
    private String email;
    private String uid;
    private String charOrStory;

    public StoryObject(String email, String uid, String charOrStory){
        this.email = email;
        this.uid = uid;
        this.charOrStory = charOrStory;
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

    public String getCharOrStory(){
        return charOrStory;
    }
    public void setCharOrStory(String charOrStory){
        this.charOrStory = charOrStory;
    }

    @Override
    public boolean equals(Object obj) {

        boolean same = false;
        if(obj != null && obj instanceof StoryObject){
            same = this.uid == ((StoryObject) obj ).uid;
        }
        return same;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.uid == null ? 0 : this.uid.hashCode());
        return result;
    }
}
