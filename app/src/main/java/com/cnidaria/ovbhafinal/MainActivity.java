package com.cnidaria.ovbhafinal;

import static java.security.AccessController.getContext;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cnidaria.ovbhafinal.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DBHandler dataSource = new DBHandler(this);
        FirebaseApp.initializeApp(this);
        /*dataSource.open();
        dataSource.insertNearbyService("https://moranfamilyofbrands.com/Files/Documents/Milex%20Auto%20Care%2058.jpg", 46.536456, 24.558932, "Speedy Auto Service", "0723456789", "9:00-18:00");
        dataSource.insertNearbyService("https://example.com/restaurant_image.jpg", 46.530123, 24.561234, "Tasty Bites Restaurant", "0756789123", "10:00-22:00");
        dataSource.insertNearbyService("https://example.com/hotel_image.jpg", 46.528765, 24.575432, "Comfort Inn Hotel", "0734567891", "24/7");
        dataSource.insertNearbyService("https://www.fantastikautorepair.com/wp-content/uploads/2020/07/Inside-of-Fantastik-Auto-min-scaled.jpg", 46.522345, 24.580987, "AutoFix Garage", "0745678912", "8:30-17:30");
        dataSource.insertNearbyService("https://dynamic-media-cdn.tripadvisor.com/media/photo-o/28/60/80/9b/details.jpg?w=600&h=-1&s=1", 46.521234, 24.575678, "Sunny Terrace Restaurant", "0765432198", "11:00-23:00");
        dataSource.insertNearbyService("https://cf.bstatic.com/xdata/images/hotel/max1024x768/4856002.jpg?k=b8bfb9824e5cdb351c8c4e94bc118e86534b5e7335b32fd1dff3b6e8d4b2e0b9&o=&hp=1", 46.525678, 24.570123, "Grand View Hotel", "0723456789", "24/7");
        dataSource.insertNearbyService("https://www.thurstontalk.com/wp-content/uploads/2019/11/Boss-Auto-Repair-in-Olympia-Four-Wheel-Drive-Repair.jpg", 46.530987, 24.573210, "Mechanic Master", "0798765432", "9:00-18:00");
        dataSource.insertNearbyService("https://bdc2020.o0bc.com/wp-content/uploads/2020/06/Bostonia-Public-House-768x432.jpg", 46.528012, 24.564567, "Casa Bella Italian Restaurant", "0712345678", "12:00-22:30");
        dataSource.insertNearbyService("https://media-cdn.tripadvisor.com/media/photo-s/0a/ec/b1/10/motel-outside.jpg", 46.536789, 24.575890, "Riverside Inn Hotel", "0789123456", "24/7");
        dataSource.insertNearbyService("https://brakemax.com/wp-content/uploads/sites/3/2021/06/auto-repair-in-Tucson.jpg", 46.533456, 24.562345, "QuickFix Auto Service", "0732109876", "8:00-17:00");


        dataSource.close();
         System.out.println("SUCCESS");*/
        Button buttonAddContact = findViewById(R.id.buttonAddContact);
        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_contact_from, null);

                builder.setView(dialogView);

                builder.setTitle("Add new contact")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText editText1 = dialogView.findViewById(R.id.newContactName);
                                EditText editText2 = dialogView.findViewById(R.id.newContactNumber);
                                String data1 = editText1.getText().toString();
                                String data2 = editText2.getText().toString();

                                dataSource.open();
                                long result = dataSource.insertContact(data1, data2);
                                dataSource.close();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.map_fragment,R.id.nearbyServicesFragment,R.id.chatFragment,R.id.weatherFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }



}