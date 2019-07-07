package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_confirm.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ConfirmActivity : AppCompatActivity() {

    private var sysID:Int = 0
    private lateinit var progressDialog: ProgressDialog
    private lateinit var t1: TableLayout
    private lateinit var txtTotAmt:TextView
    private var totAmt: Double? = 0.0
    private lateinit var db:DBProductInShopping
    private var jsonItems = JSONObject()
    var payMethod = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        sysID = intent.getIntExtra("sysID",0)

        this.title = "Confirm Order"
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PLEASE WAIT ...")
        progressDialog.show()
        txtTotAmt = findViewById(R.id.confTotAmt)
        db = DBProductInShopping(this)

        t1 = findViewById<View>(R.id.t1) as TableLayout
        t1.setColumnStretchable(0, true)
        t1.setColumnStretchable(1, true)
        t1.setColumnStretchable(2, true)
        t1.setColumnStretchable(3, true)

        rbVisa.isChecked =true

        getItemFromDB(sysID)
        progressDialog.dismiss()
    }

    private fun getItemFromDB(sysID:Int){
        val itemFromDB = db.getAllItemBySys(sysID,this)
        for (i in 0 until itemFromDB.size){
            val name = itemFromDB[i].p_name
            val qty = itemFromDB[i].p_qty
            val price = itemFromDB[i].p_price

            setItem(name, price, qty)
        }
    }

    private fun setItem( name: String, price: Double?,  qty: Int) {
        val txtName = TextView(this)
        val txtQty = TextView(this)
        val txtPrice = TextView(this)
        val txtTotal = TextView(this)

        val tr = TableRow(this)
        //tr.setPadding(5,0,5,0);

        txtName.text = name
        txtName.textSize = 14f
        txtName.gravity = Gravity.START
        txtName.width = 150
        txtName.setPadding(2, 0, 2, 0)

        txtQty.text = qty.toString()
        txtQty.textSize = 14f
        txtQty.gravity = Gravity.START
        txtQty.width = 40

        txtPrice.text = price.toString()
        txtPrice.textSize = 14f
        txtPrice.gravity = Gravity.CENTER
        txtPrice.width = 50

        val total = price!! * qty
        totAmt = totAmt!! + total

        txtTotal.text = total.toString()
        txtTotal.textSize = 14f
        txtTotal.gravity = Gravity.CENTER
        txtTotal.width = 90

        txtTotAmt.text = totAmt.toString()

        tr.setBackgroundResource(R.drawable.white_gray_border_bottom)

        tr.addView(txtName)
        tr.addView(txtQty)
        tr.addView(txtPrice)
        tr.addView(txtTotal)

        t1.addView(tr)
    }

    @SuppressLint("LogNotTimber")
    fun onButtonClick (v:View){
        if(v is Button){
            when(v.getId()){
                R.id.btnSendData ->{
                    progressDialog.show()
                    changeItemsToJsonOb()
                    sendData()
                    progressDialog.dismiss()
                }
                R.id.btnBackToMarket ->{
                    startActivity(Intent(this@ConfirmActivity,MapActivity::class.java))
                }
            }
        }
    }

    fun onRadioButtonClick(view: View){
        if(view is RadioButton){
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.rbVisa ->
                    if (checked) {
                        payMethod = 0
                    }
                R.id.rbCash ->
                    if (checked) {
                        payMethod = 1
                    }
            }
        }
    }

    @SuppressLint("LogNotTimber")
    private fun changeItemsToJsonOb() {
        val jsonArray = JSONArray()
        val itemFromDB = db.getAllItemBySys(sysID,this)
        for (i in 0 until itemFromDB.size) {


            val prodItemID = itemFromDB[i].prodItemID
            val qty = itemFromDB[i].p_qty

            try {
                val jsonRow = JSONObject()
                jsonRow.put("IID", prodItemID)
                jsonRow.put("QTY", qty)

                jsonArray.put(jsonRow)
                jsonItems.put("recent", jsonArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    private fun sendData() {
        val stringRequest = @SuppressLint("LogNotTimber")
        object : StringRequest(
            Request.Method.POST, Constant().URL_ADD_INVOICE,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response.toString())
                    if(!jsonObject.getBoolean("error")){
                        db.deleteItemFromSystem(sysID)
                        val idIn = jsonObject.get("msg")

                        val multiFormatWriter = MultiFormatWriter()
                        try {
                            val bitMatrix =
                                multiFormatWriter.encode(idIn.toString(), BarcodeFormat.QR_CODE, 200, 200)
                            val barcodeEncoder = BarcodeEncoder()
                            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

                            val intentQR = Intent(this@ConfirmActivity, EndPaymentActivity::class.java)
                            intentQR.putExtra("pic", bitmap)
                            intentQR.putExtra("ord", idIn.toString())
                            startActivity(intentQR)
                            this.finish()

                        } catch (e: WriterException) {
                            e.printStackTrace()
                        }

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = SharedPrefManager(this@ConfirmActivity).getUserID().toString()
                params["sys_id"] = sysID.toString()
                params["date"] = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(Date())
                params["final_total_amt"] = totAmt.toString()
                params["json_items"] = jsonItems.toString()
                params["payMethod"] = payMethod.toString()
                return params
            }
        }

        AppControllerProf.getmInstance().addToRequestQueue(stringRequest)
    }

}
