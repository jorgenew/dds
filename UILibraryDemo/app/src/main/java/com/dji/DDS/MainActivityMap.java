package com.dji.DDS;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dji.common.flightcontroller.FlightControllerState;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.common.error.DJIError;
import dji.sdk.mission.timeline.triggers.TriggerEvent;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

//UNIFAMMA -23.420855, -51.930074

public class MainActivityMap extends FragmentActivity implements View.OnClickListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {

    protected static final String TAG = "DDS";

    Boolean editarWaypointManual = false;
    Boolean editaraltitudeManual = false;
    Boolean editarspeedManual    = false;

    private GoogleMap gMap;

    private Button locate, add, clear, btn_list,btn_manual;
    private Button config, upload, start, stop,btn_camera,btn_linha;

    private Switch switch_altitude_padrao, switch_velocidade_padrao,switch_editar_waypoint;
    private EditText text_latitude,text_longitude,text_altitude,text_speed,text_numero_remover, text_numero_waypoint;
    private Button btn_ok_waypoint,btn_remover_waypoint;

    private TextView txt_lat_long;


    private boolean isAdd = false;

    private double droneLocationLat = -23.420855, droneLocationLng = -51.930074;
    private final Map<Integer, Marker> mMarkers = new ConcurrentHashMap<Integer, Marker>();
    private Marker droneMarker = null;

    private float altitude = 120.0f;
    private float mSpeed = 10.0f;

    private List<Waypoint> waypointList = new ArrayList<>();

    public static WaypointMission.Builder waypointMissionBuilder;
    private FlightController mFlightController;
    private WaypointMissionOperator instance;
    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;

    @Override
    protected void onResume(){
        super.onResume();
        initFlightController();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(mReceiver);
        removeListener();
        super.onDestroy();


    }

    /**
     * @Description : RETURN Button RESPONSE FUNCTION
     */
    public void onReturn(View view){
        Log.d(TAG, "onReturn");
        this.finish();
    }

    private void setResultToToast(final String string){
        MainActivityMap.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivityMap.this, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {

        locate = (Button) findViewById(R.id.locate);
        add = (Button) findViewById(R.id.add);
        clear = (Button) findViewById(R.id.clear);
        config = (Button) findViewById(R.id.config);
        upload = (Button) findViewById(R.id.upload);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        btn_list = (Button) findViewById(R.id.btn_list);
        txt_lat_long = (TextView) findViewById(R.id.txt_lat_long);
        btn_manual = (Button) findViewById(R.id.btn_manual);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_linha = (Button) findViewById(R.id.btn_linha);

        locate.setOnClickListener(this);
        add.setOnClickListener(this);
        clear.setOnClickListener(this);
        config.setOnClickListener(this);
        upload.setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        btn_list.setOnClickListener(this);
        btn_manual.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_linha.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // When the compile and target version is higher than 22, please request the
        // following permissions at runtime to ensure the
        // SDK work well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        setContentView(R.layout.activity_main_map);

        IntentFilter filter = new IntentFilter();
        filter.addAction(DJIDemoApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);

        initUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addListener();






    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onProductConnectionChange();
        }
    };

    private void onProductConnectionChange()
    {
        initFlightController();
        loginAccount();
    }

    private void loginAccount(){

        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        Log.e(TAG, "Login Success");
                    }
                    @Override
                    public void onFailure(DJIError error) {
                        setResultToToast("Login Error:"
                                + error.getDescription());
                    }
                });
    }

    private void initFlightController() {

        BaseProduct product = DJIDemoApplication.getProductInstance();
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                mFlightController = ((Aircraft) product).getFlightController();
            }
        }

        if (mFlightController != null) {
            mFlightController.setStateCallback(new FlightControllerState.Callback() {

                @Override
                public void onUpdate(FlightControllerState djiFlightControllerCurrentState) {
                    droneLocationLat = djiFlightControllerCurrentState.getAircraftLocation().getLatitude();//passa a cordenada em tempo real
                    droneLocationLng = djiFlightControllerCurrentState.getAircraftLocation().getLongitude();
                    updateDroneLocation();// recebe cordenada para ficar constantemente atualizando no mapa a localizacao do drone
                }
            });
        }
    }

    //Add Listener for WaypointMissionOperator
    private void addListener() {
        if (getWaypointMissionOperator() != null){
            getWaypointMissionOperator().addListener(eventNotificationListener);
        }
    }

    private void removeListener() {
        if (getWaypointMissionOperator() != null) {
            getWaypointMissionOperator().removeListener(eventNotificationListener);
        }
    }

    private WaypointMissionOperatorListener eventNotificationListener = new WaypointMissionOperatorListener() {
        @Override
        public void onDownloadUpdate(WaypointMissionDownloadEvent downloadEvent) {

        }

        @Override
        public void onUploadUpdate(WaypointMissionUploadEvent uploadEvent) {

        }

        /*@Override
        public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
            missionActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (waypointMissionOperator.getCurrentState().equals(WaypointMissionState.READY_TO_EXECUTE)) {
                        onMissionReadyToStart();
                    }
                }
            });
        }*/
        //http://forum.dev.dji.com/forum.php?mod=viewthread&tid=33780&extra=page%3D1%26filter%3Dtypeid%26typeid%3D307

        @Override
        public void onExecutionUpdate(WaypointMissionExecutionEvent executionEvent) {

        }

        @Override
        public void onExecutionStart() {

        }

        @Override
        public void onExecutionFinish(@Nullable final DJIError error) {
            setResultToToast("Execution finished: " + (error == null ? "Success!" : error.getDescription()));
        }
    };

    public WaypointMissionOperator getWaypointMissionOperator() {
        if (instance == null) {
            instance = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
        }
        return instance;
    }

    private void setUpMap() {
        gMap.setOnMapClickListener(this);// add the listener for click for amap object

    }

    @Override
    public void onMapClick(LatLng point) {

        txt_lat_long.setText("La:"+point.latitude+"Lo:"+point.longitude);
        if (isAdd == true){
            markWaypoint(point);
            Waypoint mWaypoint = new Waypoint(point.latitude, point.longitude, altitude);
            //Add Waypoints to TratamentoWaypoint arraylist;
            if (waypointMissionBuilder != null) {
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }else
            {
                waypointMissionBuilder = new WaypointMission.Builder();
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }
        }else{
            setResultToToast("Cannot AddWaypoint");
        }
    }



    public void addWaypoint(LatLng point) {


        if (isAdd == true){
            markWaypoint(point);
            Waypoint mWaypoint = new Waypoint(point.latitude, point.longitude, altitude);
            //Add Waypoints to TratamentoWaypoint arraylist;
            if (waypointMissionBuilder != null) {
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }else
            {
                waypointMissionBuilder = new WaypointMission.Builder();
                waypointList.add(mWaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }
        }else{
            setResultToToast("Cannot Add TratamentoWaypoint");
        }
    }

    public static boolean checkGpsCoordination(double latitude, double longitude) {
        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
    }

    // Update the drone location based on states from MCU.
    private void updateDroneLocation(){

        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
        //Create MarkerOptions object
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.aircraft));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (droneMarker != null) {
                    droneMarker.remove();
                }

                if (checkGpsCoordination(droneLocationLat, droneLocationLng)) {
                    droneMarker = gMap.addMarker(markerOptions);
                }
            }
        });
    }

    public void listWaypoint(){
        LinearLayout k = (LinearLayout)getLayoutInflater().inflate(R.layout.lista_waypoint, null);




        ListView list = (ListView) k.findViewById(R.id.listViewWaypoint);

        List<TratamentoWaypoint> pointo = todosWaypoints();
        ArrayAdapter<TratamentoWaypoint> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pointo);
        list.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(k)
                .setPositiveButton("Finish",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {

                    }

                })

                .create()
                .show();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setResultToToast(" " +i+"  "+l+"  "+view);
            }
        });

        //setResultToToast("selecionado");
    }

    public void manualWaypoint(){
        LinearLayout manualWaypoint = (LinearLayout)getLayoutInflater().inflate(R.layout.manual_waypoint, null);



        switch_altitude_padrao   = (Switch)manualWaypoint.findViewById(R.id.switch_altitude_padrao);
        switch_velocidade_padrao = (Switch)manualWaypoint.findViewById(R.id.switch_velocidade_padrao);
        switch_editar_waypoint   = (Switch)manualWaypoint.findViewById(R.id.switch_editar_waypoint);

        text_latitude            = (EditText) manualWaypoint.findViewById(R.id.text_latitude);
        text_longitude           = (EditText) manualWaypoint.findViewById(R.id.text_longitude);
        text_altitude            = (EditText) manualWaypoint.findViewById(R.id.text_altitude);
        text_speed               = (EditText) manualWaypoint.findViewById(R.id.text_speed);
        text_numero_remover      = (EditText) manualWaypoint.findViewById(R.id.text_numero_remover);
        text_numero_waypoint     = (EditText) manualWaypoint.findViewById(R.id.text_numero_waypoint);

        btn_ok_waypoint          = (Button) manualWaypoint.findViewById(R.id.btn_ok_waypoint);
        btn_remover_waypoint     = (Button) manualWaypoint.findViewById(R.id.btn_remover_waypoint);

        switch_altitude_padrao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),"habilitado altitude padrao",Toast.LENGTH_LONG).show();
                    text_altitude.setEnabled(false);
                    text_altitude.setClickable(false);
                    editaraltitudeManual = true;
                }else{
                    text_altitude.setEnabled(true);
                    text_altitude.setClickable(true);
                    editaraltitudeManual = false;

                    Toast.makeText(getApplicationContext(),"desabilitado altitude padrao",Toast.LENGTH_LONG).show();
                }

            }
        });


        switch_velocidade_padrao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(),"habilitado speed padrao",Toast.LENGTH_LONG).show();
                    text_speed.setEnabled(false);
                    text_speed.setClickable(false);
                    editarspeedManual = true;
                }else{
                    text_speed.setEnabled(true);
                    text_speed.setClickable(true);
                    editarspeedManual = false;
                    Toast.makeText(getApplicationContext(),"desabilitado speed padrao",Toast.LENGTH_LONG).show();
                }

            }
        });

        switch_editar_waypoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked && "".equals(text_numero_waypoint.getText().toString())){
                    switch_editar_waypoint.toggle();
                    Toast.makeText(getApplicationContext(),"informe o numero",Toast.LENGTH_LONG).show();
                }else{
                    if(isChecked){
                        Toast.makeText(getApplicationContext(),"habilitado editar waypoint",Toast.LENGTH_LONG).show();
                        editarWaypointManual = true;

                    }else{

                        editarWaypointManual = false;
                        Toast.makeText(getApplicationContext(),"desabilitado editar waypoint",Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
        btn_remover_waypoint.setOnClickListener(this);
        btn_ok_waypoint.setOnClickListener(this);


        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(manualWaypoint)
                .setPositiveButton("Finish",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {

                    }

                })

                .create()
                .show();

    }



    private  List<TratamentoWaypoint> todosWaypoints() {
        List<TratamentoWaypoint> lista =new ArrayList<>();


        if (waypointMissionBuilder.getWaypointList().size() > 0){

            for (int i=0; i< waypointMissionBuilder.getWaypointList().size(); i++) {
                lista.add(new TratamentoWaypoint(i,waypointMissionBuilder.getWaypointList().get(i).coordinate.getLatitude(),
                        waypointMissionBuilder.getWaypointList().get(i).coordinate.getLongitude(),
                        waypointMissionBuilder.getWaypointList().get(i).altitude,
                        waypointMissionBuilder.getWaypointList().get(i).speed));
            }}

        
        return lista;


    }



    private void markWaypoint(LatLng point){
        //Create MarkerOptions object
        LatLng ponto = null;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title("P");

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = gMap.addMarker(markerOptions);


                 // North of the previous point, but at the same longitude
        mMarkers.put(mMarkers.size(), marker);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locate:{
                try{
                    updateDroneLocation();
                    cameraUpdate();
                }catch (Exception er){
                    setResultToToast("Erro ao centralizar");
                }

                break;
            }
            case R.id.add:{

                try{
                    enableDisableAdd();
                }catch (Exception er){
                    setResultToToast("Erro ao adicionar");
                }

                break;
            }
            case R.id.clear:{

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gMap.clear();
                        }

                    });
                    waypointList.clear();
                    waypointMissionBuilder.waypointList(waypointList);
                    updateDroneLocation();
                }catch (Exception er){
                    setResultToToast("Erro ao limpar mapa");
                }

                break;
            }
            case R.id.config:{
                try{
                    showSettingDialog();
                }catch (Exception er){
                    setResultToToast("Erro ao iniciar config");
                }
                break;
            }
            case R.id.upload:{
                try{
                    uploadWayPointMission();
                }catch (Exception er){
                    setResultToToast("Erro ao adicionar");
                }

                break;
            }
            case R.id.start:{
                try{
                    startWaypointMission();
                }catch (Exception er){
                    setResultToToast("Erro ao startar");
                }

                break;
            }
            case R.id.stop:{
                try{
                    stopWaypointMission();
                }catch (Exception er){
                    setResultToToast("Erro ao stop");
                }

                break;
            }
            case R.id.btn_list:{
                try{
                    listWaypoint();
                }catch (Exception er){
                    setResultToToast("Erro ao listar");
                }



                break;
            }
            case R.id.btn_manual:{
                try{
                    manualWaypoint();
                }catch (Exception er){
                    setResultToToast("Erro ao iniciar manual");
                }
                break;
            }
            case R.id.btn_remover_waypoint:{
                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gMap.clear();
                        }

                    });
                    removeWaypoint();
                }catch (Exception er){
                    setResultToToast("Erro ao remover");
                }

                    break;
            }

            case R.id.btn_ok_waypoint:{
                try{
                    manual_waypoint();
                }catch (Exception er){
                    setResultToToast("Erro manual");
                }
                break;
            }

            case R.id.btn_camera:{
                try{
                    Intent intent = new Intent(this, MainActivityCam.class);
                    startActivity(intent);
                }catch (Exception er){
                    setResultToToast("Erro ao iniciar camera");
                }
                break;

            }
            case R.id.btn_linha:{
                try{
                    tracarlinha();
                }catch (Exception er){
                    setResultToToast("Erro ao tracar linha");
                }

                break;
            }

            default:
                break;
        }
    }

    public void manual_waypoint(){
        float altitude;
        float speed;

        double latitude = Double.parseDouble(text_latitude.getText().toString());
        double longitude = Double.parseDouble(text_longitude.getText().toString());

        int waypointSelecionado;

        if(editarWaypointManual == true && waypointMissionBuilder.getWaypointList().size() > 0){
            waypointSelecionado = Integer.parseInt(text_numero_waypoint.getText().toString());
        }else{
            waypointSelecionado = 0;
        }

        if(editaraltitudeManual ==true){
            altitude = waypointMissionBuilder.getWaypointList().get(waypointSelecionado).altitude;
        }
        else{
            altitude = Float.parseFloat(text_altitude.getText().toString());
        }
        if(editarspeedManual == true){
            speed = waypointMissionBuilder.getWaypointList().get(waypointSelecionado).speed;
        }
        else{
            speed = Float.parseFloat(text_speed.getText().toString());
        }

        Waypoint mwaypoint = new Waypoint(latitude,longitude,altitude);
        LatLng point = new LatLng(latitude,longitude);

        if(editarWaypointManual == true && waypointMissionBuilder.getWaypointList().size() > 0){

            waypointMissionBuilder.getWaypointList().set(waypointSelecionado,mwaypoint);
            waypointMissionBuilder.getWaypointList().set(waypointSelecionado,mwaypoint).speed =1;

        }else{


            //Add Waypoints to TratamentoWaypoint arraylist;
            if (waypointMissionBuilder != null) {
                waypointList.add(mwaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }else
            {
                waypointMissionBuilder = new WaypointMission.Builder();
                waypointList.add(mwaypoint);
                waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
            }
            waypointMissionBuilder.getWaypointList().get(waypointList.size()-1).speed = speed;
            //waypointMissionBuilder.getWaypointList().set(waypointList.size()-1,mwaypoint).speed = speed;

        }
        markWaypoint(point);

    }

    public void removeWaypoint(){
        int waypointSelecionado = Integer.parseInt(text_numero_remover.getText().toString());
        waypointList.remove(waypointSelecionado);
        LatLng ponto;
        if (waypointMissionBuilder.getWaypointList().size() > 0){

            for (int i=0; i< waypointMissionBuilder.getWaypointList().size(); i++) {
                double lat = waypointMissionBuilder.getWaypointList().get(i).coordinate.getLatitude();
                double lon = waypointMissionBuilder.getWaypointList().get(i).coordinate.getLongitude();
                ponto = new LatLng(lat,lon);
                markWaypoint(ponto);


            }}

    }

    public void tracarlinha(){

        PolylineOptions rectOptions = new PolylineOptions();

// Get back the mutable Polyline
        LatLng ponto = null;



        if (waypointMissionBuilder.getWaypointList().size() > 0) {

            for (int i = 0; i < waypointMissionBuilder.getWaypointList().size(); i++) {
                rectOptions.add(new LatLng(waypointMissionBuilder.getWaypointList().get(i).coordinate.getLatitude(),
                waypointMissionBuilder.getWaypointList().get(i).coordinate.getLongitude()));




            }
        }
        Polyline polyline = gMap.addPolyline(rectOptions);
    }

    private void cameraUpdate(){
        LatLng pos = new LatLng(droneLocationLat, droneLocationLng);
        float zoomlevel = (float) 18.0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pos, zoomlevel);
        gMap.moveCamera(cu);

    }

    private void enableDisableAdd(){
        if (isAdd == false) {
            isAdd = true;
            add.setText("Exit");
        }else{
            isAdd = false;
            add.setText("Add");
        }
    }

    private void showSettingDialog(){
        LinearLayout wayPointSettings = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_waypointsetting, null);

        final TextView wpAltitude_TV = (TextView) wayPointSettings.findViewById(R.id.altitude);
        RadioGroup speed_RG = (RadioGroup) wayPointSettings.findViewById(R.id.speed);
        RadioGroup actionAfterFinished_RG = (RadioGroup) wayPointSettings.findViewById(R.id.actionAfterFinished);
        RadioGroup heading_RG = (RadioGroup) wayPointSettings.findViewById(R.id.heading);

        speed_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lowSpeed){
                    mSpeed = 3.0f;
                } else if (checkedId == R.id.MidSpeed){
                    mSpeed = 5.0f;
                } else if (checkedId == R.id.HighSpeed){
                    mSpeed = 10.0f;
                }
            }

        });

        actionAfterFinished_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "Select finish action");
                if (checkedId == R.id.finishNone){
                    mFinishedAction = WaypointMissionFinishedAction.NO_ACTION;
                } else if (checkedId == R.id.finishGoHome){
                    mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
                } else if (checkedId == R.id.finishAutoLanding){
                    mFinishedAction = WaypointMissionFinishedAction.AUTO_LAND;
                } else if (checkedId == R.id.finishToFirst){
                    mFinishedAction = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
                }
            }
        });

        heading_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "Select heading");

                if (checkedId == R.id.headingNext) {
                    mHeadingMode = WaypointMissionHeadingMode.AUTO;
                } else if (checkedId == R.id.headingInitDirec) {
                    mHeadingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
                } else if (checkedId == R.id.headingRC) {
                    mHeadingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
                } else if (checkedId == R.id.headingWP) {
                    mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
                }
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("")
                .setView(wayPointSettings)
                .setPositiveButton("Finish",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {

                        String altitudeString = wpAltitude_TV.getText().toString();
                        altitude = Integer.parseInt(nulltoIntegerDefalt(altitudeString));
                        Log.e(TAG,"altitude "+altitude);
                        Log.e(TAG,"speed "+mSpeed);
                        Log.e(TAG, "mFinishedAction "+mFinishedAction);
                        Log.e(TAG, "mHeadingMode "+mHeadingMode);
                        configWayPointMission();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                })
                .create()
                .show();
    }

    String nulltoIntegerDefalt(String value){
        if(!isIntValue(value)) value="0";
        return value;
    }

    boolean isIntValue(String val)
    {
        try {
            val=val.replace(" ","");
            Integer.parseInt(val);
        } catch (Exception e) {return false;}
        return true;
    }

    private void configWayPointMission(){

        if (waypointMissionBuilder == null){

            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);

        }else
        {
            waypointMissionBuilder.finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(mSpeed)
                    .maxFlightSpeed(mSpeed)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);

        }

        if (waypointMissionBuilder.getWaypointList().size() > 0){

            for (int i=0; i< waypointMissionBuilder.getWaypointList().size(); i++){
                waypointMissionBuilder.getWaypointList().get(i).altitude = altitude;

            }

            setResultToToast("Set Waypoint attitude successfully");
        }

        DJIError error = getWaypointMissionOperator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
            setResultToToast("loadWaypoint succeeded");
        } else {
            setResultToToast("loadWaypoint failed " + error.getDescription());
        }
    }

    private void uploadWayPointMission(){

        getWaypointMissionOperator().uploadMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    setResultToToast("Mission upload successfully!");
                } else {
                    setResultToToast("Mission upload failed, error: " + error.getDescription() + " retrying...");
                    getWaypointMissionOperator().retryUploadMission(null);
                }
            }
        });

    }

    private void startWaypointMission(){

        getWaypointMissionOperator().startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                setResultToToast("Mission Start: " + (error == null ? "Successfully" : error.getDescription()));
            }
        });
    }

    private void stopWaypointMission(){
        
        getWaypointMissionOperator().stopMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {

                setResultToToast("Mission Stop: " + (error == null ? "Successfully" : error.getDescription()));
            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (gMap == null) {
            gMap = googleMap;
            setUpMap();
        }

        LatLng maringa = new LatLng(-23.420855, -51.930074);
        gMap.addMarker(new MarkerOptions().position(maringa).title("Maringa"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(maringa));
    }

}
