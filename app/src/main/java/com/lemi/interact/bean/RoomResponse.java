package com.lemi.interact.bean;

/**
 * Created by Administrator on 2019/10/11.
 */
public class RoomResponse {

    private Integer roomId;

    private String num;

    private Integer createUserId;

    private String nickName;

    private Integer gender;

    private String photo;

    private String joinNickName;

    private Integer joinGender;

    private String joinPhoto;

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getJoinNickName() {
        return joinNickName;
    }

    public void setJoinNickName(String joinNickName) {
        this.joinNickName = joinNickName;
    }

    public Integer getJoinGender() {
        return joinGender;
    }

    public void setJoinGender(Integer joinGender) {
        this.joinGender = joinGender;
    }

    public String getJoinPhoto() {
        return joinPhoto;
    }

    public void setJoinPhoto(String joinPhoto) {
        this.joinPhoto = joinPhoto;
    }
}
