package org.smartregister.chw.tbleprosy_sample.activity;


import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.chw.tbleprosy_sample.R;

public class TbLeprosyContactRegister  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_contact);


        findViewById(R.id.newClient);
        findViewById(R.id.existingClient);


    }


}