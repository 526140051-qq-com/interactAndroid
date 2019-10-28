package com.lemi.interact.bean;

import java.util.Date;

public class Room {
    private Integer id;

    private String num;

    private Integer categoryId;

    private Integer isFree;

    private Double price;

    private Integer createUserId;

    private User createUser;

    private Integer joinUserId;

    private User joinUser;

    private Date createTime;

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getJoinUser() {
        return joinUser;
    }

    public void setJoinUser(User joinUser) {
        this.joinUser = joinUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num == null ? null : num.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getJoinUserId() {
        return joinUserId;
    }

    public void setJoinUserId(Integer joinUserId) {
        this.joinUserId = joinUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}