package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_product_item_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ProductItemDetailActivity : AppCompatActivity(),View.OnClickListener {


    private lateinit var progressDialog: ProgressDialog
    private val imageLoader = AppControllerProf.getmInstance().getmImageLoader()

    private var qty = 0
    private var sysID = 0
    private var prodID = 0
    private lateinit var db:DBProductInShopping
    private var productItemsInSection = ProductItems()
    private var qtyIntent = 0
    private var secName = ""

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_item_detail)

        db = DBProductInShopping(this)

        sysID = intent.getIntExtra("sysID",0)
        prodID = intent.getIntExtra("prodID",0)
        qtyIntent = intent.getIntExtra("qty",0)
        secName = intent.getStringExtra("secName")

        etQTYDetail.setText(qtyIntent.toString())

        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PLEASE WAIT ...")
        Log.d("TAGTest","onCrate:")
        getProduct(sysID, prodID)

        btnAddQtyDetail.setOnClickListener(this)
        btnMinQtyDetail.setOnClickListener(this)
        btnAddDetail.setOnClickListener(this)
        btnRemoveDetail.setOnClickListener(this)
    }

    @SuppressLint("LogNotTimber")
    override fun onClick(v: View?) {
        var etQty = Integer.parseInt(etQTYDetail.text.toString())
        when(v!!){
            btnAddQtyDetail ->{
                if(etQty < qty){
                    btnAddQtyDetail.isEnabled = true
                    etQty += 1
                    etQTYDetail.setText(etQty.toString())
                }else{
                    btnAddQtyDetail.isEnabled = false
                    Toast.makeText(this,"This quantity not has in stock",Toast.LENGTH_LONG).show()
                }
            }
            btnMinQtyDetail ->{
                if(etQty > 0){
                    btnMinQtyDetail.isEnabled = true
                    etQty -= 1
                    etQTYDetail.setText(etQty.toString())
                }else{
                    btnMinQtyDetail.isEnabled = false
                    Toast.makeText(this,"The quantity should be more than 0",Toast.LENGTH_LONG).show()
                }
            }
            btnAddDetail ->{
                if(etQty == 0 || etQty > qty){
                    Toast.makeText(this,"The quantity is wrong",Toast.LENGTH_LONG).show()
                }else{
                    if(db.getItemByID(sysID,prodID,this)){
                        // Update qty
                        if(db.updateItemByID(sysID,prodID,etQty,this)){
                            Toast.makeText(this,"This item update in cart successfully",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this,"Can not update",Toast.LENGTH_LONG).show()
                        }
                    }else{
                        // add item to db
                        if(db.addItemShopToDB(productItemsInSection,productItemsInSection.secID,sysID,etQty,this)){
                            Toast.makeText(this,"This item add : ${productItemsInSection.prodItemID}",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this,"can not add :: ${productItemsInSection.prodItemID}",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            btnRemoveDetail ->{
                Log.d("TAGTest","MIn:")
                if(db.deleteItemFromDB(sysID,productItemsInSection.prodItemID,this)){
                    Toast.makeText(this,"item remove: ${productItemsInSection.prodItemID}",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"can not remove: ${productItemsInSection.prodItemID}",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getProduct(systemID:Int, prodItemID:Int){

        progressDialog.show()
        val stringRequest = object : StringRequest(
            Request.Method.POST, Constant().URL_PRODUCT_DETAIL,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if(!jsonObj.getBoolean("error")){
                        val arrLength = JSONArray(jsonObj.getString("msg"))
                        if(arrLength.length() > 0 ){
                            Log.d("TAGTest",arrLength.toString())
                            for(i in 0 until arrLength.length()){
                                val obj = arrLength[i] as JSONObject
                                itemImgDetail.setImageUrl(Constant().URL_IMG+obj.getString("image"),imageLoader)
                                txtNameDetail.text = obj.getString("name")
                                txtDescDetail.text = obj.getString("description")
                                txtProDateDetail.text = obj.getString("production_prod")
                                txtExpDateDetail.text = obj.getString("expire_prod")
                                txtPriceDetail.text = obj.getInt("price").toString()
                                qty = obj.getInt("quantity")
                                title = obj.getString("name")


                                //////////////////////////
                                //     add to list      //
                                // used to insert to db //
                                /////////////////////////

                                productItemsInSection.p_name = obj.getString("name")
                                productItemsInSection.p_desc = obj.getString("description")
                                productItemsInSection.p_qty = obj.getInt("quantity")
                                productItemsInSection.p_img = obj.getString("image")
                                productItemsInSection.secID = obj.getInt("sectionID")
                                productItemsInSection.prodItemID = obj.getInt("prodItemID")
                                productItemsInSection.sysID = obj.getInt("systemID")
                                productItemsInSection.p_price = obj.getDouble("price")

                            }
                        }
                    }
                    progressDialog.dismiss()
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
                params["system"] = systemID.toString()
                params["product"] = prodItemID.toString()
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
                val intent = Intent(this@ProductItemDetailActivity,ProductActivity::class.java)
                intent.putExtra("sm_id",sysID)
                intent.putExtra("sp_id",productItemsInSection.secID)
                intent.putExtra("sp_name",secName)
                startActivity(intent)
                finish()
            }
            R.id.menu_shopping ->{
                val db = DBProductInShopping(this)
                if(db.getItemCount() > 0){
                    // cart is enable
                    val intent = Intent(this,BillActivity::class.java)
                    intent.putExtra("activity",4)
                    intent.putExtra("sysID",sysID)
                    intent.putExtra("prodID",prodID)
                    intent.putExtra("qty",qtyIntent)
                    intent.putExtra("secName",secName)
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
