package com.xbirder.bike.hummingbird.register;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xbirder.bike.hummingbird.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterFragment extends Fragment {

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
}
