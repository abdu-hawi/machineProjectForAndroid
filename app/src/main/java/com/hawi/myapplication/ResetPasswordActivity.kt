package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ResetPasswordActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    private var idReset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        title = "Reset Password"

        progressDialog = ProgressDialog(this)

        btnCheckPhone.setOnClickListener {
            txtAlertReset.visibility = View.GONE
            getPhone()
        }
        btnReset.setOnClickListener {
            startReset(idReset)
        }

        txtBackLoginReset.setOnClickListener {
            startActivity(Intent(this@ResetPasswordActivity,LoginActivity::class.java))
        }
    }

    private fun startReset(idReset: Int) {
        val pass = etOneReset.text.trim().toString()
        val passConf = etTowReset.text.trim().toString()

        txtAlertReset.visibility = View.GONE

        if (pass.isEmpty()|| passConf.isEmpty()){
            txtAlertReset.visibility = View.VISIBLE
            txtAlertReset.text = "All Field are Required !!! "
            txtAlertReset.setBackgroundResource(R.color.colorFF4B4A)
            return
        }

        if(pass != passConf){
            txtAlertReset.visibility = View.VISIBLE
            txtAlertReset.text = "The Confirm Password not match with Password "
            txtAlertReset.setBackgroundResource(R.color.colorFF4B4A)
            return
        }

        updatePassword(idReset,pass)

    }

    private fun updatePassword(n_idUser: Int, n_pass: String) {
        progressDialog!!.setMessage("Save User Password...")
        progressDialog!!.show()
        val stringRequest = @SuppressLint("LogNotTimber")
        object : StringRequest(
            Request.Method.POST, Constant().URL_UPDATE_USER_PASS,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                Log.d("TAGTest","response: $response")
                try {
                    val jsonObj = JSONObject(response.toString())
                    if(!jsonObj.getBoolean("error")){
                        txtAlertReset.visibility = View.VISIBLE
                        txtAlertReset.text = jsonObj.getString("msg").toString()
                        txtAlertReset.setBackgroundResource(R.color.color1e9c39)
                    }else{
                        txtAlertReset.visibility = View.VISIBLE
                        txtAlertReset.text = jsonObj.getString("msg").toString()
                        txtAlertReset.setBackgroundResource(R.color.colorFF4B4A)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                progressDialog!!.hide()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = n_idUser.toString()
                params["pass"] = n_pass
                return params
            }
        }

        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }


    private fun getPhone() {
        val phone = etOneReset.text.trim().toString()
        if (phone.isEmpty()){
            txtAlertReset.visibility = View.VISIBLE
            txtAlertReset.setBackgroundResource(R.color.colorFire)
            txtAlertReset.text = "Please Write Your Phone Number"
            return
        }
        progressDialog!!.setMessage("Please Wait...")
        progressDialog!!.show()

        val stringRequest = object : StringRequest(
            Request.Method.POST, Constant().URL_USER_ID,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    val jsonObj = JSONObject(response.toString())
                    if(!jsonObj.getBoolean("error")){
                        idReset = jsonObj.getInt("msg")
                        resetPassword()
                    }else{
                        txtAlertReset.visibility = View.VISIBLE
                        txtAlertReset.text = jsonObj.getString("msg").toString()
                        txtAlertReset.setBackgroundResource(R.color.colorFF4B4A)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                progressDialog!!.dismiss()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["phone"] = phone
                return params
            }
        }

        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }

    private fun resetPassword() {
        txtAlertReset.visibility = View.GONE
        btnCheckPhone.visibility = View.GONE
        btnReset.visibility = View.VISIBLE
        etOneReset.text.clear()
        etOneReset.hint = "Write New Password"
        etTowReset.visibility = View.VISIBLE
        etOneReset.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        etTowReset.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }
}
