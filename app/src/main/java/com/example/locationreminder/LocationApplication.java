package com.example.locationreminder;


import android.app.Application;

public final class LocationApplication extends Application {
    /*
       this Application class made to use a single LocationStoreHouse object
       from a different classes.
    */
    private LocationStoreHouse storeHouse;
    Application application;
    public LocationApplication(Application application){
        super();
        this.application=application;
        super.onCreate();
        this.storeHouse = new LocationStoreHouse(application);

    }
    /* public void onCreate() {
         super.onCreate();
         this.storeHouse = new LocationStoreHouse((LocationApplication)this);

     }*/
    public final LocationStoreHouse getStoreHouse() {
        return this.storeHouse;
    }
}
