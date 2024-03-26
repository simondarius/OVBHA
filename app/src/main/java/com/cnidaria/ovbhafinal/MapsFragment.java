package com.cnidaria.ovbhafinal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapsFragment extends Fragment {
    private final int FINE_PERMISSION_CODE = 1;
    private static final String SMS_PERMISSION = Manifest.permission.SEND_SMS;

    String last_chatroomId;
    Location currentLocation;
    GoogleMap gMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (currentLocation != null) {
                gMap=googleMap;
                LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f)); // Adjust the zoom level as needed
            }
        }
    };
    private void sendSMS() {
        Toast.makeText(requireContext(), "ImageButton clicked", Toast.LENGTH_SHORT).show();
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference chatroomsRef = db.collection("chatrooms");

            String chatroomId = generateRandomShortId();
            Map<String, Object> chatroomData = new HashMap<>();
            chatroomData.put("short_id", chatroomId);

            chatroomsRef.document(chatroomId).set(chatroomData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Chatroom created successfully!");
                            sendSMSWithChatroomId(chatroomId);
                            last_chatroomId=chatroomId;
                            startLocationUpdates();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            System.out.println("Failed to create chatroom!");
                        }
                    });

        } catch (Exception e) {
            System.out.println("Exception caught when sending sms! " + e.toString());
        }
    }

    private void sendSMSWithChatroomId(String chatroomId) {
        try {
            DBHandler db = new DBHandler(requireContext());
            db.open();

            List<String> numbersWithTrueStatus = db.getNumbersWithTrueStatus();
            String message = "SOS! Stuck in roadside incident. My location: ";
            message += "http://maps.google.com/maps?q=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
            System.out.println("HERE!");
            System.out.println(chatroomId);
            message += " in OVBHA join chatroom: " + chatroomId;
            System.out.println(message);
            for (String phoneNumber : numbersWithTrueStatus) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }

            Toast.makeText(requireContext(), "Messages sent to all contacts with true status.", Toast.LENGTH_SHORT).show();

            db.close();

        } catch (Exception e) {
            System.out.println("Exception caught when sending sms! " + e.toString());
        }
    }
    private void updateLocation(double latitude, double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatroomRef = db.collection("chatrooms").document(last_chatroomId);

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);

        chatroomRef.update(locationData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Location updated successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }

    private void trackLocation(String chatroomId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatroomRef = db.collection("chatrooms").document(chatroomId);

        chatroomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Double latitude = snapshot.getDouble("latitude");
                    Double longitude = snapshot.getDouble("longitude");

                    // Update marker on the map with new location
                    if (latitude != null && longitude != null) {
                        LatLng userLocation = new LatLng(latitude, longitude);
                        gMap.clear(); // Clear previous marker
                        gMap.addMarker(new MarkerOptions().position(userLocation).title("User's Location"));
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));
                    }
                }
            }
        });
    }
    private String generateRandomShortId() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("#");
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 7; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private void registerPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {

                        sendSMS();
                    } else {
                        // Permission denied
                        Toast.makeText(requireContext(), "SMS permission is required to send messages.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);

    }
    private LocationCallback locationCallback;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLastLocation();
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocation(location.getLatitude(), location.getLongitude());
                }
            }
        };

        ImageButton imageButton = view.findViewById(R.id.imageButton2);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if SMS permission is granted
                if (ContextCompat.checkSelfPermission(requireContext(), SMS_PERMISSION)
                        == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();

                } else {
                    // Permission is not granted, request permission
                    requestPermissionLauncher.launch(SMS_PERMISSION);
                }
            }
        });
        Button trackButton = view.findViewById(R.id.buttonTrack);

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText t1= view.findViewById(R.id.emergencyCodeInput);
                trackLocation(t1.getText().toString());
            }
        });
        // Register permission launcher
        registerPermissionLauncher();
    }

    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted, get the last location
                    getLastLocation();
                } else {
                    Toast.makeText(requireContext(), "Location permission is denied.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation=location;
                    SupportMapFragment mapFragment =
                            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(callback);
                    }
                }
            }
        });
    }
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000) // Update interval in milliseconds (e.g., 10 seconds)
                .setFastestInterval(5000) // Fastest update interval in milliseconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            return;
        }

        // Request location updates
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        // Stop requesting location updates
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop location updates when the activity is destroyed
        stopLocationUpdates();
    }
}