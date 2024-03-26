package com.cnidaria.ovbhafinal;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyServicesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NearbyServicesFragment() {

    }


    public static NearbyServicesFragment newInstance(String param1, String param2) {
        NearbyServicesFragment fragment = new NearbyServicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private LinearLayout rootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby_services, container, false);

        rootLayout = view.findViewById(R.id.root);

        DBHandler db = new DBHandler(requireContext());
        db.open();
        List<ContentValues> nearbyServices = db.getNearbyServices();
        db.close();


        for (ContentValues service : nearbyServices) {
            View serviceView = inflater.inflate(R.layout.nearby_service_layout, null);


            TextView textViewServiceName = serviceView.findViewById(R.id.textView7);
            textViewServiceName.setText(service.getAsString(ContactContract.ContactEntry.COLUMN_NAME));

            TextView textViewOpenHours = serviceView.findViewById(R.id.textViewOpenHours);
            textViewOpenHours.setText(service.getAsString(ContactContract.ContactEntry.COLUMN_OPEN_HOURS));

            TextView textViewServiceNumber = serviceView.findViewById(R.id.textViewServiceNumber);
            textViewServiceNumber.setText(service.getAsString(ContactContract.ContactEntry.COLUMN_PHONE_NUMBER));

            String imageUrl = service.getAsString(ContactContract.ContactEntry.COLUMN_IMAGE);
            if (!TextUtils.isEmpty(imageUrl)) {
                ImageView imageView = serviceView.findViewById(R.id.imageView2);
                Glide.with(this).load(imageUrl).into(imageView);
            }




            rootLayout.addView(serviceView);
        }

        return view;
    }
}