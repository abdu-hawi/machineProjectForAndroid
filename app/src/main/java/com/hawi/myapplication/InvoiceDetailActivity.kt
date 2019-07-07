package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_invoice_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class InvoiceDetailActivity : AppCompatActivity() {

    private lateinit var t1: TableLayout
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_detail)

        title = "Invoice Detail"
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PLEASE WAIT ...")
        progressDialog.show()

        t1 = findViewById<View>(R.id.tableInvDetail) as TableLayout
        t1.setColumnStretchable(0, true)
        t1.setColumnStretchable(1, true)
        t1.setColumnStretchable(2, true)
        t1.setColumnStretchable(3, true)

        progressDialog.show()

        val idInvDet = intent.getIntExtra("ord", 0)
        txtInvNoDetail.text = idInvDet.toString()
        txtSmInvDet.text = intent.getStringExtra("smName")
        txtDateInvDet.text = intent.getStringExtra("date")
        txtTotInvDet.text = intent.getDoubleExtra("total",0.0).toString()
        if(intent.getIntExtra("method",0) == 0)
            txtInvMethod.text = "VISA"
        else if (intent.getIntExtra("method",0) == 1)
            txtInvMethod.text = "CASH"
        getProdFromServer(idInvDet)
    }

    private fun getProdFromServer(idInvDet: Int) {
        progressDialog.setMessage("WAIT INVOICE DETAIL ...")
        val stringRequest = object : StringRequest(
            Request.Method.POST, Constant().URL_INVOICE_DETAIL,
            Response.Listener { response ->
                try {
                    val jsonObj = JSONObject(response.toString())
                    if(!jsonObj.getBoolean("error")){
                        val jsonMSG = JSONArray(jsonObj.getString("msg"))
                        if (jsonMSG.length() > 0){
                            for (i in 0 until jsonMSG.length()){
                                val msgObj: JSONObject = jsonMSG[i] as JSONObject
                                val qty = msgObj.getInt("qty")
                                val name = msgObj.getString("prodName")
                                val price = msgObj.getDouble("price")
                                setRowInvoiceDetail(name,qty,price)
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                progressDialog.dismiss()
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = idInvDet.toString()
                return params
            }
        }

        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }

    private fun setRowInvoiceDetail(name: String?, qty: Int, price: Double) {
        val txtName = TextView(this)
        val txtQty = TextView(this)
        val txtPrice = TextView(this)
        val txtTotal = TextView(this)

        val tr = TableRow(this)
        tr.setPadding(5,0,5,0);

        txtName.text = name
        txtName.textSize = 14f
        txtName.gravity = Gravity.START
        txtName.width = 150
        txtName.setPadding(2, 0, 2, 0)

        txtQty.text = qty.toString()
        txtQty.textSize = 14f
        txtQty.gravity = Gravity.CENTER
        txtQty.width = 40

        txtPrice.text = price.toString()
        txtPrice.textSize = 14f
        txtPrice.gravity = Gravity.CENTER
        txtPrice.width = 50

        val total = price * qty

        txtTotal.text = total.toString()
        txtTotal.textSize = 14f
        txtTotal.gravity = Gravity.CENTER
        txtTotal.width = 90

        tr.setBackgroundResource(R.drawable.white_gray_border_bottom)

        tr.addView(txtName)
        tr.addView(txtQty)
        tr.addView(txtPrice)
        tr.addView(txtTotal)

        t1.addView(tr)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            android.R.id.home-> {
                startActivity(Intent(this@InvoiceDetailActivity,InvoiceActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
