package com.feh.hiko;

import com.feh.hiko.db.Coord;
import com.feh.hiko.db.Hike;
import com.feh.hiko.db.HikeDataSource;
import com.feh.hiko.db.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.sql.SQLException;


public class CreateHikeDetailsActivity extends Activity
        implements  ConnectionCallbacks,
                    OnConnectionFailedListener{
    /**


    /*
     * Will contain the different Location Point
     */
    protected GoogleApiClient mGoogleApiClient;
    protected static final String TAG = "basic-location-sample";

    protected android.location.Location mLastLocation;
    protected String mLatitudeText;
    protected String mLongitudeText;

    Location locate = new Location();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_hike_details);
        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            ((EditText)findViewById(R.id.point1_editText)).setText(mLatitudeText);
            ((EditText)findViewById(R.id.point2_editText)).setText(mLongitudeText);
        }
        else {
            ((EditText)findViewById(R.id.point1_editText)).setText("-1");
            ((EditText)findViewById(R.id.point2_editText)).setText("-1");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addLocation(View view)
    {

        String point1 = ((EditText)findViewById(R.id.point1_editText)).getText().toString();
        String point2 = ((EditText)findViewById(R.id.point2_editText)).getText().toString();
        locate.addCoord(new Coord(Float.parseFloat(point1),Float.parseFloat(point2)));
        ((EditText)findViewById(R.id.point1_editText)).setText("");
        ((EditText)findViewById(R.id.point2_editText)).setText("");


    }

    public void addHikeDb(View view)
    {
        //We get information from the last page
        Intent intent = getIntent();
        int hikeId = intent.getIntExtra("hikeId",0);
        Log.w("hikeId in CREATE DETAILS ACTIVITY",Integer.toString(hikeId));
        String hikeName = intent.getStringExtra("hikeName");
        String hikeTime = intent.getStringExtra("hikeTime");
        String hikeDistance = intent.getStringExtra("hikeDistance");

        HikeDataSource dataSource = new HikeDataSource(this);
        try {
            dataSource.open();
        }
        catch(SQLException e)
        {
            Log.w("SQLException", e);
        }

        dataSource.createHike(new Hike(hikeId,hikeName,Float.parseFloat(hikeDistance), Float.parseFloat(hikeTime),locate));

        dataSource.close();

        Intent Nintent = new Intent(this,MenuActivity.class);
        startActivity(Nintent);

    }



}
