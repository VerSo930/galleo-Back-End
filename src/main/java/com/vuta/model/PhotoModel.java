package com.vuta.model;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class PhotoModel {

    private int id;
    private int userId;
    private int galleryId;
    private String name;
    private String description;
    private long createdAt;
    private long updatedAt;
    private boolean isPrivate;
    private String url;

    public PhotoModel(int id, int userId, int galleryId, String name, String description, long createdAt, long updatedAt, boolean isPrivate, String url) {
        this.id = id;
        this.userId = userId;
        this.galleryId = galleryId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPrivate = isPrivate;
        this.url = url;
    }

    public PhotoModel() {
    }

    @Override
    public String toString() {
        return "PhotoModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", galleryId=" + galleryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isPrivate=" + isPrivate +
                ", url='" + url + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(int galleryId) {
        this.galleryId = galleryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
