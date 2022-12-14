package com.example.locationreminder;




import android.content.Context;
import android.content.Intent;
//import android.support.v4.app.JobIntentService;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.JobIntentService;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;


public final class PreparingForLocationNotification extends JobIntentService {
    /*
    * in this class we prepare for showing a location notification for user
    * we see if we in a correct place if yes then we use a notification function from Services class
    * */
    public static final PreparingForLocationNotification.Companion Companion = new PreparingForLocationNotification.Companion();

    protected void onHandleWork( Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String error1 = ErrorMessagesForLocation.INSTANCE.errorService((Context)this, geofencingEvent.getErrorCode());
            Log.e("GeoTrIntentService", error1);
        } else {
            this.handleEvent(geofencingEvent);
        }
    }


    private final String currentUser(){
          FirebaseAuth fAuth= FirebaseAuth.getInstance();
          String userId = fAuth.getCurrentUser().getUid();
          return userId;
    }

    private final LocationDetails getTopLocation(List triggeringGeofences) {
        Geofence firstGeofence = (Geofence)triggeringGeofences.get(0);
        // return ((LocationApplication)this.getApplication()).getStoreHouse().search(firstGeofence.getRequestId());
        LocationApplication locationApplication =new LocationApplication(this.getApplication());
        return locationApplication.getStoreHouse().search(firstGeofence.getRequestId());
    }
    private final void handleEvent(GeofencingEvent event) {
        if (event.getGeofenceTransition() == 1) {
            LocationDetails locationDetails = this.getTopLocation(event.getTriggeringGeofences());
            String message = locationDetails != null ? locationDetails.getMessage() : null;
            LatLng latLng = locationDetails != null ? locationDetails.getLatLng() : null;
            if (message != null && latLng != null){// && locationDetails.getCurrentUser().equals(currentUser())) {
                Services.sendNotification((Context)this, message, latLng, locationDetails.getTitle(), locationDetails.getDescription());
                //removeReminderFromFirebase(locationDetails);
            }
        }

    }
    public  final void removeReminderFromFirebase(LocationDetails locationDetails)
    {  FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        fStore.collection("reminders").whereEqualTo("reminder[0]",locationDetails.getId()).get().getResult().getDocuments().get(0).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                new ServicesForLocation().removeReminder(locationDetails);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tag", "Error deleting document");
            }
        });
    }


    public static final class Companion {
        public final void enqueueWork( Context context,  Intent intent) {
            JobIntentService.enqueueWork(context, PreparingForLocationNotification.class, 573, intent);
        }
        public Companion() {}
    }

}

