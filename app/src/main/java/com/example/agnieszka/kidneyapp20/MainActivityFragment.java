package com.example.agnieszka.kidneyapp20;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Button addFood;
    Context context;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        addFood = (Button) rootView.findViewById(R.id.add_food_button);
        View.OnClickListener clicking = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = getActivity().getApplicationContext();
                Intent intent = new Intent (context, AddingFood.class);
                startActivity(intent);
            }

        };
        addFood.setOnClickListener(clicking);

        return rootView;
    }
}
