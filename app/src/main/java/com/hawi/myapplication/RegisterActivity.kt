package com.hawi.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class RegisterActivity : AppCompatActivity() {
    // Log tag
    private val TAG = RegisterActivity::class.java.simpleName

    // هنا تعاريف تستخدم في الربط
    lateinit var txtName: EditText
    lateinit var txtPass:EditText
    lateinit var txtPhone:EditText

    private var progressDialog: ProgressDialog? = null // تعريف جاري التحميل

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "REGISTER"

//        if (SharedPrefManager(this).isLoggedIn()) {
//            finish()
//            startActivity(Intent(this, MapActivity::class.java))
//            return
//        }

        // ربط الجوال والاسم وكلمة المرور
        txtPhone = findViewById<View>(R.id.txtPhone) as EditText
        txtName = findViewById<View>(R.id.txtName) as EditText
        txtPass = findViewById<View>(R.id.txtPass) as EditText
        // ربط جاري التحميل
        progressDialog = ProgressDialog(this)


    }
    fun btnRegiEvent(view: View) {
        val phone = txtPhone!!.text.toString().trim{ it <= ' ' }
        val pass = txtPass!!.text.toString().trim{ it <= ' ' }
        val name = txtName!!.text.toString().trim { it <= ' ' }

        progressDialog!!.setMessage("Register user...")
        progressDialog!!.show()
        val stringRequest = object : StringRequest(
            Request.Method.POST, Constant().URL_REGISTER,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                    jsonObject.getBoolean("error")
                    Toast.makeText(applicationContext, jsonObject.getString("msg"), Toast.LENGTH_LONG).show()
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
                val pramas = HashMap<String, String>()
                pramas["name"] = name
                pramas["phone"] = phone
                pramas["password"] = pass
                return pramas
            }
        }

        AppControllerProf.getmInstance()!!.addToRequestQueue(stringRequest)
    }

    fun txtClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}
