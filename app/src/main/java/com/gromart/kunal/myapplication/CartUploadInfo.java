package com.example.tony.myapplication;

/**
 * Created by Tony on 1/9/2018.
 */

public class CartUploadInfo {

    public String imageName;

    public String imageWeight;

    public String imagePrice;

    public String imageURL;

    public String imageQuantity;

    public String dateTime;

    public CartUploadInfo() {

    }

    public CartUploadInfo(String name, String url, String weight, String price,String quantity,String dateTime) {

        this.imageName = name;//
        this.imageURL= url;//
        this.imageWeight = weight;//
        this.imagePrice = price;//
        this.imageQuantity = quantity;//
        this.dateTime = dateTime;//
    }

    public String getImageName() {
        return imageName;
    }

    public String getDateTime() {
        return dateTime;
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

    public String getImageQuantity() {
        return imageQuantity;
    }

}
