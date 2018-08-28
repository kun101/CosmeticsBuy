package com.example.tony.myapplication;

/**
 * Created by Tony on 1/9/2018.
 */

public class ImageUploadInfo {

    public String imageName;

    public String imagePacking;

    public String imageWeight;

    public String imageIngredients;

    public String imageFeatures;

    public String imagePrice;

    public String imageDisclaimer;

    public String imageURL;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url,String ingredients,String features,String disclaimer,String packing,String weight, String price) {

        this.imageName = name;//
        this.imageURL= url;//
        this.imagePacking = packing;//
        this.imageWeight = weight;//
        this.imageIngredients = ingredients;//
        this.imageFeatures = features;//
        this.imagePrice = price;//
        this.imageDisclaimer = disclaimer;
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
    public String getImageIngredients() {
        return imageIngredients;
    }
    public String getImagePacking() {
        return imagePacking;
    }
    public String getImageFeatures() {
        return imageFeatures;
    }
    public String getImageDisclaimer() {
        return imageDisclaimer;
    }

}
