package com.hawi.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class LoginActivity : AppCompatActivity() {

    // Log tag
    private val TAG = LoginActivity::class.java.simpleName

    lateinit var etuserPhone: EditText
    lateinit var etpassword:EditText
    lateinit var btnLogin: Button
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "LOGIN"

//        if(SharedPrefManager(this).isLoggedIn()){
//            //TODO: MyThread to don't hanging when download from server
//            startActivity(Intent(this, MapActivity::class.java))
////            finish()
//            return
//        }

        etuserPhone = findViewById(R.id.txtPhoneL)
        etpassword = findViewById(R.id.txtPassL)
        btnLogin = findViewById(R.id.btnLogin)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("PLEASE WAIT ...")
    }

    private fun userLogin() {
        val phone = etuserPhone.text.toString().trim { it <= ' ' }
        val pass = etpassword.text.toString().trim { it <= ' ' }
        progressDialog.show()
        val stringRequest = object : StringRequest(
            Request.Method.POST, Constant().URL_LOGIN,
            Response.Listener { response ->
                progressDialog.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    if (!jsonObject.getBoolean("error")) {
                        //Log.d("TAGMapActivity",jsonObject.getJSONArray("msg").toString())
                        SharedPrefManager(applicationContext)
                            .userLogin(
                                jsonObject.getInt("id"),
                                jsonObject.getString("name"),
                                jsonObject.getInt("phone")
                            )
                        //TODO: MyThread to don't hanging when download from server
                        val intent = Intent(this@LoginActivity, MapActivity::class.java)
                        intent.putExtra("noActiv", 1)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            jsonObject.getString("msg"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    error.message,
                    Toast.LENGTH_LONG
                ).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["phone"] = phone
                params["password"] = pass
                return params
            }
        }
        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }

    fun btnLoginEvent(view: View) {
        userLogin()
    }

    fun txtClick(view: View) {
        if(view is TextView){
            when(view.getId()){
                R.id.txtRegisterLogin->
                    startActivity(Intent(this, RegisterActivity::class.java))
                R.id.txtResetLogin->
                    startActivity(Intent(this, ResetPasswordActivity::class.java))
            }
        }
    }
}
