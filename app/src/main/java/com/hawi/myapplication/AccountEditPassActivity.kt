package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_edit_password.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class AccountEditPassActivity : AppCompatActivity() {

    private val spm:SharedPrefManager = SharedPrefManager(this)

    private var idUser = 0
    private var progressDialog: ProgressDialog? = null // تعريف جاري التحميل
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        title = "Edit User Password"

        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        idUser = if(SharedPrefManager(this).isLoggedIn())
            spm.getUserID()
        else
            0
        // ربط جاري التحميل
        progressDialog = ProgressDialog(this)

        btnSavePassEP.setOnClickListener{
            btnOnClick()
        }
    }

    @SuppressLint("LogNotTimber")
    private fun btnOnClick() {

        val pass = etUserPassAccEP.text.trim().toString()
        val passConf = etUserPassConfEP.text.trim().toString()

        txtAlertEP.visibility = View.GONE

        if (pass.isEmpty()|| passConf.isEmpty()){
            txtAlertEP.visibility = View.VISIBLE
            txtAlertEP.text = "All Field are Required !!! "
            txtAlertEP.setBackgroundResource(R.color.colorFF4B4A)
            return
        }

        if(pass != passConf){
            txtAlertEP.visibility = View.VISIBLE
            txtAlertEP.text = "The Confirm Password not match with Password  "
            txtAlertEP.setBackgroundResource(R.color.colorFF4B4A)
            return
        }

        updatePassword(idUser,pass)

    }

    private fun updatePassword(n_idUser: Int, n_pass: String) {
        progressDialog!!.setMessage("UPDATE USER INFO...")
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
                        txtAlertEP.visibility = View.VISIBLE
                        txtAlertEP.text = jsonObj.getString("msg").toString()
                        txtAlertEP.setBackgroundResource(R.color.color1e9c39)
                    }else{
                        txtAlertEP.visibility = View.VISIBLE
                        txtAlertEP.text = jsonObj.getString("msg").toString()
                        txtAlertEP.setBackgroundResource(R.color.colorFF4B4A)
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home-> {
                startActivity(Intent(this@AccountEditPassActivity,AccountActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
