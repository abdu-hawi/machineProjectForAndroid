@file:Suppress("UNREACHABLE_CODE")

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
import kotlinx.android.synthetic.main.activity_edit_account.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class AccountEditInfoActivity : AppCompatActivity() {

    private val spm:SharedPrefManager = SharedPrefManager(this)

    private var progressDialog: ProgressDialog? = null // تعريف جاري التحميل


    private var idUser = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        title= "Edit User Info"
        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        // ربط جاري التحميل
        progressDialog = ProgressDialog(this)

        idUser = spm.getUserID()
        etUserNameAcc.setText(spm.getUserName()!!)
        etUserMobileAcc.setText("0${spm.getUserPhone()}")
        if(spm.getUserEmail() != null){
            etUserEmailAcc.setText(spm.getUserEmail())
        }

        btnSaveAcc.setOnClickListener{
            btnOnClick()
        }
    }

    @SuppressLint("LogNotTimber")
    private fun btnOnClick() {
        val name = etUserNameAcc.text.trim().toString()
        val email = etUserEmailAcc.text.trim().toString()
        val mob = etUserMobileAcc.text.trim().toString()
        val mobile = Integer.valueOf(mob)

        txtAlert.visibility = View.GONE

        if (name.isEmpty()||email.isEmpty()||mob.isEmpty()){
            txtAlert.visibility = View.VISIBLE
            txtAlert.text = "All fields are required !!!"
            txtAlert.setBackgroundResource(R.color.colorFF4B4A)
            return
        }
        if(spm.userUpdate(idUser,name,mobile,email)){
            updateUserInServer(idUser,name,mobile,email)
        }else{
            txtAlert.visibility = View.VISIBLE
            txtAlert.text = "Sorry, some thing missing, please try again !!!"
            txtAlert.setBackgroundResource(R.color.colorFF4B4A)
            return
        }

    }

    private fun updateUserInServer(n_idUser: Int, n_name: String, n_mobile: Int, n_email: String) {
        progressDialog!!.setMessage("UPDATE USER INFO...")
        progressDialog!!.show()
        val stringRequest = @SuppressLint("LogNotTimber")
        object : StringRequest(
            Request.Method.POST, Constant().URL_UPDATE_USER_INFO,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                Log.d("TAGTest","response: $response")
                try {
                    val jsonObj = JSONObject(response.toString())
                    if(!jsonObj.getBoolean("error")){
                        txtAlert.visibility = View.VISIBLE
                        txtAlert.text = jsonObj.getString("msg")
                        txtAlert.setBackgroundResource(R.color.color1e9c39)
                    }else{
                        txtAlert.visibility = View.VISIBLE
                        txtAlert.text = jsonObj.getString("msg")
                        txtAlert.setBackgroundResource(R.color.colorFF4B4A)
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
                params["name"] = n_name
                params["phone"] = n_mobile.toString()
                params["email"] = n_email
                return params
            }
        }

        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home-> {
                startActivity(Intent(this@AccountEditInfoActivity,AccountActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
