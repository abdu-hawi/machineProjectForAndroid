package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SectionActivity : AppCompatActivity() {

    private var sectionList = ArrayList<SectionItem>()
    private var gridViewMain: GridView? = null
    private var smID:Int = 0
    private var smName = ""

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PLEASE WAIT ...")
        smID = intent.getIntExtra("id",0)
        smName = intent.getStringExtra("title")
        title = smName
        getSection()


        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    @SuppressLint("LogNotTimber")
    fun getSection(){
//        progressDialog.show()
        val jr: RequestQueue = Volley.newRequestQueue(this)
        val strReq = JsonObjectRequest(Request.Method.POST,Constant().URL_SECTION,null,
            Response.Listener {
                response ->
                try{
                    val jsonObj = JSONObject(response.toString())
                    if(!jsonObj.getBoolean("error")){
                        val arrLength = JSONArray(jsonObj.getString("msg"))
                        if(arrLength.length() > 0 ){
                            val len = arrLength.length() - 1
                            for(i in 0..len){
                                val msgObj: JSONObject = (response.getJSONArray("msg").get(i) as? JSONObject)!!
                                val id = msgObj.getInt("ps_id")
                                val name = msgObj.getString("ps_name")
                                val img = msgObj.getString("ps_image")
                                sectionList.add(SectionItem(id,name,img))
                            }
                        }
                    }
                    gridViewMain = findViewById(R.id.grid_view_main)
                    val adGradView = AdapterGridViewSection(sectionList,this,smID,smName)
                    gridViewMain!!.adapter = adGradView
                    progressDialog.dismiss()
                }catch (e: JSONException){
                    Log.d("TAGTest", "Error: ${e.message}")
                    progressDialog.dismiss()
                }
            }, Response.ErrorListener {
                error ->
                Log.d("TAGTest", "error: ${error.message}")
                progressDialog.dismiss()
            })
        jr.add(strReq)
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_shopping, menu)
        return true
    }

    @SuppressLint("LogNotTimber")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            android.R.id.home-> {
                finish()
            }
            R.id.menu_shopping ->{
                val db = DBProductInShopping(this)
                if(db.getItemCount() > 0){
                    // cart is enable
                    val intent = Intent(this,BillActivity::class.java)
                    intent.putExtra("activity",2)
                    intent.putExtra("sysID",smID)
//                    intent.getStringExtra("title")
                    intent.putExtra("title",smName)
                    startActivity(intent)
                }else{
                    // no item has in cart
                    Toast.makeText(this,"You don't have any item in cart, Please Add item!", Toast.LENGTH_LONG).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

