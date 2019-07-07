package com.hawi.myapplication

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_product.view.*


class ProductActivity : AppCompatActivity() {


    private var itemsInSection = ArrayList<ProductItems>()
    var progressDialog: ProgressDialog? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var productRecycleAdapter: ProductRecycleAdapter
    private lateinit var btn:Button
    private var shopAvilable = 0
    private var spID = 0
    private var smID = 0
    private var spName = ""

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)


        spID = intent.getIntExtra("sp_id",0)
        smID = intent.getIntExtra("sm_id",0)
        spName = intent.getStringExtra("sp_name")
        title = spName

        DBProductInShopping(this)

        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("PLEASE WAIT ...")
        btn = findViewById(R.id.btnProcess)
        recyclerView = findViewById(R.id.recycleView)
        // get product from db
        getProduct(smID,spID,spName)
    }// End onCreate method

    fun btnProcess(view:View) {
        /*
        if (shopAvilable == 0) {
            btn.isEnabled = false
        } else {
            btn.isEnabled = true
        }
         */
        view.btnProcess.isEnabled = shopAvilable != 0
    }


    @SuppressLint("LogNotTimber")
    private fun getProduct(systemID:Int, sectionID:Int,sectionName:String){

        progressDialog!!.show()
        val stringRequest = object : StringRequest(
            Request.Method.POST, Constant().URL_PRODUCT,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if(!jsonObj.getBoolean("error")){
                        val arrLength = JSONArray(jsonObj.getString("msg"))
                        if(arrLength.length() > 0 ){
                            for(i in 0 until arrLength.length()){
                                val obj = arrLength[i] as JSONObject
                                val pName = obj.getString("name")
                                val pDesc = obj.getString("description")
                                val pQty = obj.getInt("quantity")
                                val pImg = obj.getString("image")
                                val pSecID = obj.getInt("sectionID")
                                val pProItemID = obj.getInt("prodItemID")
                                val pSysID = obj.getInt("systemID")
                                val pPrice = obj.getDouble("price")
                                itemsInSection.add(ProductItems(pName,pDesc,pQty,pImg,pProItemID,pSysID,pSecID,pPrice))
                            }
                        }
                    }
                    recyclerView.layoutManager = LinearLayoutManager(baseContext)
                    productRecycleAdapter = ProductRecycleAdapter(this,itemsInSection,systemID,sectionID,sectionName )
                    recyclerView.adapter = productRecycleAdapter
                    recyclerView.itemAnimator = DefaultItemAnimator()
                    progressDialog!!.dismiss()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("TAGTest","catch: ${e.printStackTrace()}")
                    progressDialog!!.dismiss()
                }
            }, Response.ErrorListener { error ->
                Log.d("TAGTest","error: ${error.message}")
                progressDialog!!.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["system"] = systemID.toString()
                params["section"] = sectionID.toString()
                return params
            }
        }
        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)

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
                    intent.putExtra("activity",3)
                    intent.putExtra("sp_id",spID)
                    intent.putExtra("sp_name",spName)
                    intent.putExtra("sysID",smID)
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