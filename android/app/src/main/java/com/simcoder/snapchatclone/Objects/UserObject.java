package com.simcoder.snapchatclone.Objects;

import com.google.firebase.database.DataSnapshot;

/**
 * Object of a user containing all of the relavant info
 */
public class UserObject {
    private String id;
    private String name;
    private String image;
    private String email;

    private boolean receive;

    /**
     * Constructor of the user object initializing everything with null
     */
    public UserObject() {
        this.id = null;
        this.name = null;
        this.image = null;
        this.email = null;
        this.receive = false;
    }

    /**
     * Constructor of the user object
     *
     * @param id      - id of the user
     * @param name    - name of the user
     * @param image   - image url of the user
     * @param email   - email of the user
     * @param receive - boolean to check if the user will receive an image
     */
    public UserObject(String id, String name, String image, String email, boolean receive) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.email = email;
        this.receive = receive;
    }

    /**
     * Parse the datasnapshot and populate the user object
     * @param dataSnapshot - data from the database
     */
    public void parseData(DataSnapshot dataSnapshot){
        id = dataSnapshot.getRef().getKey();
        if (dataSnapshot.child("email").getValue() != null)
            email = dataSnapshot.child("email").getValue().toString();
        if (dataSnapshot.child("name").getValue() != null)
            name = dataSnapshot.child("name").getValue().toString();
        if (dataSnapshot.child("image").getValue() != null)
            image = dataSnapshot.child("image").getValue().toString();

        receive = false;
    }

    /**
     * Gets the id of the user.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the user.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the image url of the user.
     *
     * @return the image url.
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets the email of the user.
     *
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets if the user will receive an image.
     *
     * @return the boolean receive.
     */
    public Boolean getReceive() {
        return receive;
    }


    /**
     * Sets the name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the image of the user.
     */
    public void setImage(String image) {
        this.image = image;
    }


    /**
     * Sets the id of the user.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the email of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Sets the receive var of the user.
     */
    public void setReceive(Boolean receive) {
        this.receive = receive;
    }


    /**
     * Overrides the equals function so that it checks if two users match by the id
     * of said objects.
     *
     * @return true if equal objects
     */
    @Override
    public boolean equals(Object obj) {
        boolean same = false;
        if (obj instanceof UserObject) {
            same = this.id.equals(((UserObject) obj).getId());
        }
        return same;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }
}
