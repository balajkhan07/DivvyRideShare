package com.example.balaj.divvyrideshare;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.personality_insights.v3.PersonalityInsights;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Content;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.Profile;
import com.ibm.watson.developer_cloud.personality_insights.v3.model.ProfileOptions;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.util.GsonSingleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PersonalityClassify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality_classify);

        IamOptions options = new IamOptions.Builder()
                .apiKey("{_L4Vh_i0OU3_MdX2yZ5FSRI9cwHRoOUP_bBra_JW1jJ3}")
                .build();

        PersonalityInsights personalityInsights = new PersonalityInsights("{version}",options);

         personalityInsights = new PersonalityInsights(
                "{2018-06-04}",
                "{8db748e5-639f-473f-83a0-a1c62644b1e9}",
                "{rchBzwDciARb}");

        personalityInsights = new PersonalityInsights("{version}");
        personalityInsights.setEndPoint("https://gateway-fra.watsonplatform.net/personality-insights/api");

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Watson-Learning-Opt-Out", "true");

        personalityInsights.setDefaultHeaders(headers);

        try {
            File folder = new File(getFilesDir() + "/resources");
            File file = new File(folder.getAbsolutePath() + "/haha.txt");
            JsonReader jReader = new JsonReader(new FileReader(file));
            Content content = GsonSingleton.getGson().fromJson(jReader, Content.class);
            ProfileOptions option = new ProfileOptions.Builder()
                    .content(content).consumptionPreferences(true)
                    .rawScores(true).build();
            Profile profile = personalityInsights.profile(option).execute();
            Log.i("ProfilePerson", profile.toString());
        } catch (FileNotFoundException e) {
            Log.i("Error", e.toString());
        }
    }
}
