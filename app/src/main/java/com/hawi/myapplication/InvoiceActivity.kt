package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class InvoiceActivity : AppCompatActivity() {

    private lateinit var t1: TableLayout
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        title = "Invoice List"
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PLEASE WAIT ...")
        progressDialog.show()

        t1 = findViewById<View>(R.id.t1Inv) as TableLayout
        t1.setColumnStretchable(0, true)
        t1.setColumnStretchable(1, true)
        t1.setColumnStretchable(2, true)

        getInvFromServer(SharedPrefManager(this).getUserID())
    }

    private fun getInvFromServer(userID: Int) {
        progressDialog.setMessage("WAIT INVOICE INFO...")
        progressDialog.show()
        val stringRequest = object : StringRequest(
                Request.Method.POST, Constant().URL_INVOICE,
                Response.Listener { response ->
                    try {
                        val jsonObj = JSONObject(response.toString())
                        if(!jsonObj.getBoolean("error")){
                            val jsonMSG = JSONArray(jsonObj.getString("msg"))
                            if (jsonMSG.length() > 0){
                                for (i in 0 until jsonMSG.length()){
                                    val msgObj:JSONObject = jsonMSG[i] as JSONObject
                                    val idInv = msgObj.getInt("id")
                                    val method = msgObj.getInt("method")
                                    val date = msgObj.getString("date")
                                    val total = msgObj.getDouble("total")
                                    val smName = msgObj.getString("sm")
                                    val desc = msgObj.getString("prod")
                                    setRowInvoice(idInv,date,total,smName,desc,method)
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
                        params["id"] = userID.toString()
                        return params
                    }
                }

        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }

    @SuppressLint( "SetTextI18n")
    private fun setRowInvoice(id: Int, date: String?, total: Double, smName: String?, desc: String?,method:Int) {
        val txtDescInv = TextView(this)
        val txtDateInv = TextView(this)
        val txtTotInv = TextView(this)
        val txtSmInv = TextView(this)

        val tr = TableRow(this)
        tr.setPadding(5,5,5,5)

        txtDescInv.text = "$desc ..."
        txtDescInv.textSize = 14f
        txtDescInv.gravity = Gravity.START
        txtDescInv.width = 150
        txtDescInv.setPadding(2, 0, 2, 0)

        txtDateInv.text = date
        txtDateInv.textSize = 14f
        txtDateInv.gravity = Gravity.CENTER
        txtDateInv.width = 40

        txtTotInv.text = "$total SR"
        txtTotInv.textSize = 14f
        txtTotInv.gravity = Gravity.CENTER
        txtTotInv.width = 50

        txtSmInv.text = smName
        txtSmInv.textSize = 14f
        txtSmInv.gravity = Gravity.CENTER
        txtSmInv.width = 90


        tr.setBackgroundResource(R.drawable.white_gray_border_bottom)

        tr.addView(txtDescInv)
        tr.addView(txtDateInv)
        tr.addView(txtTotInv)
        tr.addView(txtSmInv)

        tr.setOnClickListener{
            getNewIntent(id,total,date,smName,method)
        }
        t1.addView(tr)
    }

    private fun getNewIntent(id: Int, total: Double, date: String?, smName: String?,method:Int) {
        val intent = Intent(this@InvoiceActivity, InvoiceDetailActivity::class.java)
        intent.putExtra("ord", id)
        intent.putExtra("total", total)
        intent.putExtra("date",date)
        intent.putExtra("smName",smName)
        intent.putExtra("method",method)
        startActivity(intent)
        this.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            android.R.id.home-> {
                startActivity(Intent(this@InvoiceActivity,MapActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }



}
