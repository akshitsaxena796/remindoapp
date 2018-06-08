package com.example.BasicMap;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemainderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RemainderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemainderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    };

    public static final String MyPREFERENCES = "MyPrefs";


    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RemainderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RemainderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemainderFragment newInstance(String param1, String param2) {
        RemainderFragment fragment = new RemainderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remainder, container, false);

        final RelativeLayout layout = view.findViewById(R.id.relative_rem);


        final int[] top = {40};

        SharedPreferences sharedpreferences;
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String email = sharedpreferences.getString("Emailkey", "hello");

        final RequestParams requestParams = new RequestParams();
        requestParams.add("user",email);
        Log.d("reminder", "rp" + requestParams.toString());

        HttpUtils.post("/FetchUserLocationData",requestParams,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                   JSONObject serverRespObj =null;

                    for(int i=0;i<response.length();i++) {

                         serverRespObj = response.getJSONObject(i);
                         final String place =  serverRespObj.getString("placeName");
                        final String nearby =  serverRespObj.getString("nearBy");
                        final String  lastdate =serverRespObj.getString("lastDate");
                         final String id = serverRespObj.getString("_id");

                        Log.d("reminder 2", "res 1"+ serverRespObj.toString());
                        Log.d("reminder 2", "res 1"+ place);

                            ImageView imageView = new ImageView(getContext());
                            RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,250);
                            imageView.setImageResource(R.drawable.rectangle);
                            rp.setMargins(15, top[0],15,20);

                            // context.getResources().getDisplayMetrics().density

                            imageView.setLayoutParams(rp);
                            layout.addView(imageView);

                            //imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
                            //  imageView.setMaxHeight(20);
                            //  imageView.setMaxWidth(20);

                            TextView textrem = new TextView(getContext());
                            RelativeLayout.LayoutParams rptxt = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            textrem.setText("Venue : "+nearby+","+place);
                            rptxt.setMargins(15, top[0] +10,15,20);
                            textrem.setPadding(20,0,0,0);
                            textrem.setTextColor(Color.BLACK);
                            textrem.setLayoutParams(rptxt);
                            layout.addView(textrem);

                            TextView textdate = new TextView(getContext());
                          RelativeLayout.LayoutParams rptxtdate = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                           textdate.setText("Last Visted: "+lastdate);
                           rptxtdate.setMargins(15,top[0]+50,15,20);
                          textdate.setPadding(20,0,0,0);
                          textdate.setTextColor(Color.BLACK);
                           textdate.setLayoutParams(rptxtdate);
                           layout.addView(textdate);

                        Button btnmore = new Button(getContext());
                        Button btndelete = new Button(getContext());

                            RelativeLayout.LayoutParams rpbtnmore = new RelativeLayout.LayoutParams(350, ViewGroup.LayoutParams.WRAP_CONTENT);
                            btnmore.setText("Show More");
                            rpbtnmore.setMargins(200, top[0] +100,10,20);
                            btnmore.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            btnmore.setLayoutParams(rpbtnmore);
                           if(btnmore.getParent()!=null)
                               ((ViewGroup) btnmore.getParent()).removeView(btnmore); // <- fix
                               layout.addView(btnmore);


                           RelativeLayout.LayoutParams rpbtndelete = new RelativeLayout.LayoutParams(350, ViewGroup.LayoutParams.WRAP_CONTENT);
                         btndelete.setText("Delete");
                         rpbtndelete.setMargins(600,top[0]+ 100,10,20);
                         btndelete.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                         btndelete.setLayoutParams(rpbtndelete);
                        if(btndelete.getParent()!=null)
                            ((ViewGroup) btndelete.getParent()).removeView(btndelete);
                            layout.addView(btndelete);

                        top[0] = top[0] + 240;
                        final int pos= i;
                        final JSONObject finalServerRespObj = serverRespObj;
                        btnmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent RemDetails = new Intent(getContext(), RemainderDetailsActivity.class);
                                RemDetails.putExtra("loadsPosition",id);
                                RemDetails.putExtra("Position",pos);
                                RemDetails.putExtra("Place",place);
                                RemDetails.putExtra("jsonObject", finalServerRespObj.toString());
                                startActivity(RemDetails);
                            }
                        });
                    }

                    Log.d("reminder 2", "res" + response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        checkPermissions();

        // NOTE : We are calling the onFragmentInteraction() declared in the MainActivity
        // ie we are sending "Fragment 1" as title parameter when fragment1 is activated
        if (mListener != null) {
            mListener.onFragmentInteraction("Reminders");
        }

        // Here we will can create click listners etc for all the gui elements on the fragment.
        // For eg: Button btn1= (Button) view.findViewById(R.id.frag1_btn1);
        // btn1.setOnclickListener(...

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkbattery() {

        PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        String name = getContext().getPackageName();

     //   Toast.makeText(getContext(), "name" + name, Toast.LENGTH_LONG).show();
      //  Toast.makeText(getContext(), "battery:" + powerManager.isIgnoringBatteryOptimizations(name), Toast.LENGTH_LONG).show();


        if (!powerManager.isIgnoringBatteryOptimizations(name) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

            Toast.makeText(getContext(), "Battery optimization -> All apps -> $name -> Don't optimize", Toast.LENGTH_LONG).show();

            buildAlertMessageBatOpt();
        }

    }


    private void buildAlertMessageBatOpt() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("To help improve Your Experience, please set Battery Optimisation of REMINDO to \"Dont Optimize\"...")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivity(intent);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(getContext(), "Required permission '" + permissions[index] + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                // all permissions were granted
                checkbattery();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
}
