package com.hawi.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    private val spm: SharedPrefManager = SharedPrefManager(this)

    @SuppressLint("SetTextI18n", "LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        title = "User Account"

        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        txtUserEmailAcc.visibility = View.GONE

        txtUserNameAcc.text = spm.getUserName()!!.toString()
        txtUserMobileAcc.text = "0${spm.getUserPhone()}"
        if (spm.getUserEmail() != null) {
            txtUserEmailAcc.visibility = View.VISIBLE
            txtUserEmailAcc.text = spm.getUserEmail().toString()
        }else{
            txtUserEmailAcc.visibility = View.GONE
        }

        btnChangeUserInfo.setOnClickListener {
            startActivity(Intent(this@AccountActivity,AccountEditInfoActivity::class.java))
            finish()
        }
        btnChangePass.setOnClickListener {
            startActivity(Intent(this@AccountActivity,AccountEditPassActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home-> {
                startActivity(Intent(this@AccountActivity,MapActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}