package com.cnidaria.ovbhafinal.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cnidaria.ovbhafinal.ContactContract;
import com.cnidaria.ovbhafinal.DBHandler;
import com.cnidaria.ovbhafinal.R;
import com.cnidaria.ovbhafinal.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Cursor cursor;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        System.out.println("H");
        DBHandler db = new DBHandler(requireContext());
        db.open();
        cursor = db.getAllContacts();
        System.out.println("H");
        LinearLayout contactsContainer = root.findViewById(R.id.contactsContainer);
        System.out.println("H");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_NUMBER));
                boolean status = cursor.getInt(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_STATUS)) == 1;
                System.out.println("H");
                View contactView = getLayoutInflater().inflate(R.layout.contact_display, null);
                System.out.println("H");
                TextView textViewName = contactView.findViewById(R.id.textViewName);
                textViewName.setText(name);
                TextView textViewPhoneNumber = contactView.findViewById(R.id.textViewPhoneNumber);
                textViewPhoneNumber.setText(phoneNumber);
                ImageButton favouriteButton = contactView.findViewById(R.id.favouriteButton);
                boolean isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry.COLUMN_STATUS)) == 1;

                if (isFavourite) {
                    favouriteButton.setImageResource(R.drawable.baseline_star_24);
                } else {
                    favouriteButton.setImageResource(R.drawable.empty_star);
                }
                final boolean[] isFavouriteArray = {isFavourite};
                long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactContract.ContactEntry._ID));
                favouriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long contactId = (long) favouriteButton.getTag();

                        isFavouriteArray[0] = !isFavouriteArray[0];

                        if (isFavouriteArray[0]) {
                            favouriteButton.setImageResource(R.drawable.baseline_star_24);
                        } else {
                            favouriteButton.setImageResource(R.drawable.empty_star);
                        }

                        db.updateContactStatus(contactId, isFavouriteArray[0]);
                    }
                });

                favouriteButton.setTag(contactId);


                ImageButton deleteButton = contactView.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long contactId = (long) deleteButton.getTag();
                        db.deleteContact(contactId);
                        ((ViewGroup) contactView.getParent()).removeView(contactView);
                    }
                });

                deleteButton.setTag(contactId);
                contactsContainer.addView(contactView);
            } while (cursor.moveToNext());
        }


        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}