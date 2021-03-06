package com.vuta.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class GalleryModel {

    private int id;
    private int userId;
    private String name;
    private String description;
    private long createdAt;
    private long updatedAt;
    private boolean isPrivate;
    private List<PhotoModel> photos;
    private PhotoModel coverImage;
    private int views;
    private int photosCount;

    public GalleryModel() {
        this.updatedAt = new Date(System.currentTimeMillis()).getTime();
        this.createdAt = new Date(System.currentTimeMillis()).getTime();
        this.photos = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "GalleryModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isPrivate=" + isPrivate +
                ", photos=" + photos +
                ", coverImage=" + coverImage +
                ", views=" + views +
                '}';
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public int getPhotosCount() {
        return photosCount;
    }

    public void setPhotosCount(int photosCount) {
        this.photosCount = photosCount;
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

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<PhotoModel> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoModel> photos) {
        this.photos = photos;
    }

    public PhotoModel getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(PhotoModel coverImage) {
        this.coverImage = coverImage;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
