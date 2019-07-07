package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*
import kotlinx.android.synthetic.main.content_map.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MapActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback,
    PermissionsListener,
    LocationEngineListener,
    MapboxMap.OnInfoWindowClickListener{

// we need add ,
//    MapboxMap.OnInfoWindowClickListener( MapboxMap.OnMarkerClickListener ) to class and implement to use on click on mark

    private lateinit var mapView:MapView
    private lateinit var mapBoxMap: MapboxMap
    private var machineList = ArrayList<MachineLocation>()

    private lateinit var permissionManger:PermissionsManager
    private lateinit var originLocation:Location

    private var locationEngine:LocationEngine? = null
    private var locationLayerPlugin:LocationLayerPlugin? = null

    lateinit var progressDialog: ProgressDialog

    //Used for AD
    var ad =ArrayList<AD>()
    var cntImgArray = 0
    private var imgArraySize = 0

    @SuppressLint("LogNotTimber", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this,"pk.eyJ1IjoiYWJkdTdhd2kiLCJhIjoiY2puaW9mbmtxMG9tdDNxa2Y2anBhaW1xeiJ9.KHNqdjqNeLq8MlsMKuHkeQ")
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PLEASE WAIT ...")
        progressDialog.show()
        getMachineFromAPI()
        mapView.getMapAsync(this)

        title = "SYSTEM MACHINE"

        setSupportActionBar(toolbar)

        DBProductInShopping(this)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val navView:NavigationView = findViewById(R.id.nav_view)
        val headerView:View = navView.getHeaderView(0)
        val txtNameNavigationView:TextView = headerView.findViewById(R.id.nav_header_name)
        val txtMobileNavigationView:TextView = headerView.findViewById(R.id.nav_header_mobile)
        txtNameNavigationView.text = SharedPrefManager(this).getUserName()!!
        txtMobileNavigationView.text = "0${SharedPrefManager(this).getUserPhone()!!}"

        progressDialog.dismiss()

        getImgAD()
    }

    @SuppressLint("LogNotTimber")
    private fun getImgAD(){

        val volley = Volley.newRequestQueue(this)
        val strRequest = object :StringRequest(Request.Method.POST,Constant().URL_AD, Response.Listener {
                response ->
            if(!JSONObject(response).getBoolean("error")){
                try {
                    val jsonObj = JSONObject(response)
                    val arrLength = JSONArray(jsonObj.getString("msg"))
                    if(arrLength.length() > 0 ){
                        for(i in 0 until arrLength.length()){
                            val obj = arrLength[i] as JSONObject
                            val adName = obj.getString("name")
                            ad.add(AD(adName))
                        }
                        cntImgArray = arrLength.length()

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                this@MapActivity.runOnUiThread(runnable)
                            }

                        }, 0, 10000)
                    }
                }catch (e:JSONException){
                    Log.d("TAGTest","errorJSON: $e")
                }

            }
        }, Response.ErrorListener {
                error ->
            Log.d("TAGTest","error: $error")
        }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["dd"] = "dd"
                return params
            }
        }

        volley.add(strRequest)
    }


    private val runnable:Runnable = Runnable {
        //This method runs in the same thread as the UI.
        //Do something to the UI thread here
//        img.setImageResource(imgArray[cntImgArray])
        imgAD.setImageUrl(Constant().URL_IMG_AD+ad[imgArraySize].name,AppControllerProf.getmInstance().getmImageLoader())
        // Start the animation
//        img.startAnimation(AnimationUtils.loadAnimation(
//            applicationContext,
//            R.anim.anim))

        if (imgArraySize < cntImgArray-1){
            imgArraySize ++
        }else{
            imgArraySize = 0
        }
    }


    @SuppressLint("LogNotTimber")
    private fun getMachineFromAPI(){
        try {
            val jr:RequestQueue = Volley.newRequestQueue(this)
            val jsObj = JsonObjectRequest(Request.Method.POST,
                Constant().URL_MACHINE_PLACE,
                null,
                Response.Listener
                {
                        response ->
                    try{
                        val jsonObj = JSONObject(response.toString())
                        if(!jsonObj.getBoolean("error")){
                            val arrLength = JSONArray(jsonObj.getString("msg"))
                            if(arrLength.length() > 0 ){
                                DBProductInShopping(this).emptyMachineTable()
                                for(i in 0 until arrLength.length()){
                                    val msgObj:JSONObject = (response.getJSONArray("msg").get(i) as? JSONObject)!!
                                    val id = msgObj.getInt("sm_id")
                                    val name = msgObj.getString("sm_name")
                                    val dist = msgObj.getString("sm_dist")
                                    val lat = msgObj.getDouble("sm_lat")
                                    val long = msgObj.getDouble("sm_long")
                                    machineList.add(MachineLocation(id,name,dist,lat,long))
                                    DBProductInShopping(this).addMachine(name,id)
                                }
                            }
                        }
                    }catch (e:JSONException){
                        Log.d("TAGTest", "Error: ${e.message}")
                    }
                },
                Response.ErrorListener {
                        error ->
                    Log.d("TAGTest", "error: ${error.message}")
                })
            jr.add(jsObj)
        }catch (e:JSONException){

        }

    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        mapBoxMap = mapboxMap!!
        enableLocation()
        mapboxMap.uiSettings.isZoomControlsEnabled = true
        val icon: com.mapbox.mapboxsdk.annotations.Icon? =
            IconFactory.getInstance(this@MapActivity).fromResource(R.drawable.mapbox_marker_icon_default)

        for (i in 0..machineList.size){
            val locList = LatLng(machineList[i].ml_lat!!,machineList[i].ml_long!!)
            mapboxMap.addMarker(
                MarkerOptions()
                    .position(locList)
                    .icon(icon)!!
                    .title(machineList[i].ml_name)
                    .snippet(machineList[i].ml_dist)
            )
            mapboxMap.onInfoWindowClickListener = this
        }
    }// end onMapReady

    override fun onInfoWindowClick(marker: Marker): Boolean {
        getSysID(marker.title)
        return true
    }

    @SuppressLint("LogNotTimber")
    fun getSysID(markerName:String){
        progressDialog.show()
        val stringRequest = object : StringRequest(
            Request.Method.POST, Constant().URL_SYSTEM,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if(!jsonObj.getBoolean("error")){
                        val arr =  jsonObj.get("msg") as JSONObject
                        val sysID = arr.getInt("ID")
                        val intent = Intent(this@MapActivity,SectionActivity::class.java)
                        intent.putExtra("id",sysID)
                        intent.putExtra("title",markerName)
                        startActivity(intent)
                        progressDialog.dismiss()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("TAGTest","catch: ${e.printStackTrace()}")
                    progressDialog.dismiss()
                }
            }, Response.ErrorListener { error ->
                Log.d("TAGTest","error: ${error.message}")
                progressDialog.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["systemName"] = markerName
                return params
            }
        }
        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }


    private fun enableLocation(){
       if(PermissionsManager.areLocationPermissionsGranted(this)){
           // this block used when have permission granted
           initializeLocationEngine()
           initializeLocationLayer()
       }else{
           permissionManger = PermissionsManager(this)
           permissionManger.requestLocationPermissions(this)
       }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine(){
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation = locationEngine?.lastLocation

        if (lastLocation != null){ // if location exist
            originLocation = lastLocation
            // when location exist we will move camera to this location via setCameraPosition method
            setCameraPosition(lastLocation)
        }else{ // if location not exist
            locationEngine?.addLocationEngineListener(this)
        }
    }

    private fun setCameraPosition(location:Location){
        mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(location.latitude,location.longitude),15.0
        ))
    }

    @SuppressLint("WrongConstant")
    private fun initializeLocationLayer(){
        locationLayerPlugin = LocationLayerPlugin(mapView,mapBoxMap,locationEngine)
        // this method is response able for tracking position when user move
        locationLayerPlugin?.isLocationLayerEnabled = true
        locationLayerPlugin?.cameraMode = CameraMode.TRACKING
        locationLayerPlugin?.renderMode = RenderMode.NORMAL
    }

    // this method called when user deny permission
    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        // show toast or dialog to explain why need grant permission access
    }
    // this method called when app has grant permission
    override fun onPermissionResult(granted: Boolean) {
        if (granted){
            enableLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManger.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    // this method use when user move
    override fun onLocationChanged(location: Location?) {
        location?.let {
            originLocation = location
            setCameraPosition(location)
        }
    }

    // this method use when connect to location
    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()
        }
        mapView.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_shopping, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_shopping ->{
                val db = DBProductInShopping(this)
                if(db.getItemCount() > 0){
                    // cart is enable
                    val intent = Intent(this,BillActivity::class.java)
                    intent.putExtra("activity",1)
                    startActivity(intent)
                }else{
                    // no item has in cart
                    Toast.makeText(this,"You don't have any item in cart, Please Add item!",Toast.LENGTH_LONG).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_log_out -> {
                // Handle the camera action
                SharedPrefManager(this).logout()
                finish()
                startActivity(Intent(this,LoginActivity::class.java))
            }
            R.id.nav_account -> {
                startActivity(Intent(this@MapActivity,AccountActivity::class.java))
                finish()
            }
            R.id.nav_invoice -> {
                startActivity(Intent(this@MapActivity,InvoiceActivity::class.java))
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
