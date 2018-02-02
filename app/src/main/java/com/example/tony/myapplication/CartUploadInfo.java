package com.example.tony.myapplication;

/**
 * Created by Tony on 1/9/2018.
 */

public class CartUploadInfo {

    public String imageName;

    public String imageWeight;

    public String imagePrice;

    public String imageURL;

    public CartUploadInfo() {

    }

    public CartUploadInfo(String name, String url, String weight, String price) {

        this.imageName = name;//
        this.imageURL= url;//
        this.imageWeight = weight;//
        this.imagePrice = price;//
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageWeight() {
        return imageWeight;
    }

    public String getImagePrice() {
        return imagePrice;
    }

}
