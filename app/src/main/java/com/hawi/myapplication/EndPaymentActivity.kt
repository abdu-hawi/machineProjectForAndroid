package com.hawi.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_end_payment.*

class EndPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_payment)

        title = "Thank You"

        val imageView = findViewById<View>(R.id.img_code) as ImageView
        val bitmap = intent.getParcelableExtra<Bitmap>("pic")
        val x = intent.getIntExtra("ord", 0)
        imageView.setImageBitmap(bitmap)

        btnFinishPayment.setOnClickListener {
            startActivity(Intent(this@EndPaymentActivity,MapActivity::class.java))
            finish()
        }
    }
}
