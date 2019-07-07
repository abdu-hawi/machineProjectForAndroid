package com.hawi.myapplication

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_bill.*


class BillActivity : AppCompatActivity() {

    private var totAmt = 0.0
    private val db = DBProductInShopping(this)
    private var totAmtSys = 0.0

    private val mapDblTotAmtSys = HashMap<Int,Double>()
    private val mapTxtTotAmtSys = HashMap<Int,TextView>()

    var act = 0
    var singleSysID = 0

    var smName = ""
    var spName = ""
    var spID = 0
    var prodID = 0
    var qty = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)

        act = intent.getIntExtra("activity",0)

        if(act != 1){
            singleSysID = intent.getIntExtra("sysID",0)
            when(act){
                2 ->{
                    smName = intent.getStringExtra("title")
                }
                3->{
                    spName = intent.getStringExtra("sp_name")
                    spID = intent.getIntExtra("sp_id",0)
                }
                4->{
                    prodID = intent.getIntExtra("prodID",0)
                    qty = intent.getIntExtra("qty",0)
                    spName = intent.getStringExtra("sp_name")
                }
            }
        }

        title = "Confirm Bill"

        BillTotAmt.text = totAmt.toString()
        getLayoutScroll()

        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private lateinit var systemMachineArrayList:ArrayList<SystemMachine>

    @SuppressLint("LogNotTimber")
    fun getLayoutScroll(){
        val itemCount = db.getItemCount()

        if(itemCount > 0){

            val scrollView = findViewById<ScrollView>(R.id.scrollBill)
            // this layout used to add to view scroll and contain all layout by vertical orientation
            val linearLayout = LinearLayout(this)
            val linearParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // width , height
            linearParam.setMargins(0,2,0,2)
            linearLayout.layoutParams = linearParam
            linearLayout.orientation = LinearLayout.VERTICAL

            if(act == 1){
                // this is come from shopping menu of map
                systemMachineArrayList = db.getSystemInShop()!!
                // this loop used to take all layout of system machine from DB and add to up layout
                for(i in 0 until systemMachineArrayList.size){
                    val sysID = systemMachineArrayList[i].sSysID
                    val sysName = systemMachineArrayList[i].sSysName
                    val linearSystem = getLayoutLinearOfSystem(sysID , sysName)
                    linearLayout.addView(linearSystem)
                    // TODO getLayoutTable() method and add to linearLayout
                    linearLayout.addView(getLayoutTable())
                    linearLayout.addView(getItemToTable(sysID))
                    linearLayout.addView(getBtnAndTotal(sysID))

                }
            } else{
                // this is come from shopping SM
                llBottom.visibility = View.VISIBLE
                payFav.visibility = View.VISIBLE
                bill_btnBuy.setOnClickListener{
                    btnConfirmClick(singleSysID)
                }
                linearLayout.addView(getLayoutTable())
                linearLayout.addView(getItemToTable(singleSysID))
            }

            scrollView.addView(linearLayout)
        }else{
            Log.d("TAGTest","nothing in db")
        }


    }

    private fun getBtnAndTotal(sysID: Int):LinearLayout{

        llBottom.visibility = View.GONE
        payFav.visibility = View.GONE

        val linearLayout = LinearLayout(this)
        val linearParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // width , height
        linearParam.setMargins(0,2,0,2)
        linearLayout.layoutParams = linearParam
        linearLayout.orientation = LinearLayout.VERTICAL

        val linearLayoutHor = LinearLayout(this)
        val linearParamHor = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // width , height
        linearParamHor.setMargins(0,1,0,1)
        linearLayoutHor.layoutParams = linearParamHor
        linearLayoutHor.orientation = LinearLayout.HORIZONTAL

        val txtTotalAmount = TextView(this)

        txtTotalAmount.setText(R.string.total_amount)
        val txtTotParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f)
        txtTotalAmount.layoutParams = txtTotParam
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtTotalAmount.setTextColor(resources.getColor(R.color.colorRed,null))
        }else{
            txtTotalAmount.setTextColor(resources.getColor(R.color.colorRed))
        }
        txtTotalAmount.gravity = Gravity.END
        txtTotalAmount.typeface = Typeface.DEFAULT_BOLD
        linearLayoutHor.addView(txtTotalAmount)

        //////////////////////////////////////
        // here price //////////////////////
        ///////////////////////////////////
        val txtTotPrice = TextView(this)
        txtTotPrice.text = db.getTotalPriceOfSystem(sysID,this.applicationContext).toString()
        txtTotPrice.gravity = Gravity.CENTER
        txtTotPrice.typeface = Typeface.DEFAULT_BOLD
        linearLayoutHor.addView(txtTotPrice)

        mapTxtTotAmtSys[sysID] = txtTotPrice
        mapDblTotAmtSys[sysID] = db.getTotalPriceOfSystem(sysID,this.applicationContext)

        ////////////////////////////////////////////////////////////
        val txtSR = TextView(this)
        txtSR.setText(R.string.sr)
        txtSR.gravity = Gravity.CENTER
        txtSR.typeface = Typeface.DEFAULT_BOLD
        linearLayoutHor.addView(txtSR)

        linearLayout.addView(linearLayoutHor)

        val relativeLayout = RelativeLayout(this)
        relativeLayout.gravity = Gravity.CENTER

        val btn = Button(this)
        btn.setText(R.string.confirm_bill)
        val btnParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, 100)
        btnParam.bottomMargin = 30
        btn.layoutParams = btnParam
        btn.gravity = Gravity.CENTER_VERTICAL
        btn.setPadding(30,0,30,0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btn.setBackgroundColor(resources.getColor(R.color.color1e9c39,null))
            btn.setTextColor(resources.getColor(R.color.colorWhite,null))
        }else{
            btn.setBackgroundColor(resources.getColor(R.color.color1e9c39))
            btn.setTextColor(resources.getColor(R.color.colorWhite))
        }

        btn.setOnClickListener{
            btnConfirmClick(sysID)
        }

        relativeLayout.addView(btn)
        linearLayout.addView(relativeLayout)

        return linearLayout
    }

    private fun getLayoutLinearOfSystem(sysID:Int,sysName:String):LinearLayout{
        val linearLayoutHor = LinearLayout(this)
        val linearParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // width , height
        linearParam.setMargins(0,1,0,1)
        linearLayoutHor.tag = sysID
        linearLayoutHor.layoutParams = linearParam
        linearLayoutHor.orientation = LinearLayout.HORIZONTAL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            linearLayoutHor.background = resources.getDrawable(R.drawable.side_nav_bar,null)
        }else{
            linearLayoutHor.background = resources.getDrawable(R.drawable.side_nav_bar)
        }
        val textMachine = TextView(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textMachine.setTextColor(resources.getColor(R.color.colorWhite,null))
        }else{
            textMachine.setTextColor(resources.getColor(R.color.colorWhite))
        }
        textMachine.setText(R.string.machine_name)
        textMachine.setPaddingRelative(5,0,0,0)
        linearLayoutHor.addView(textMachine)

        val machineName = TextView(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            machineName.setTextColor(resources.getColor(R.color.colorWhite,null))
        }else{
            machineName.setTextColor(resources.getColor(R.color.colorWhite))
        }
        machineName.text = sysName
        val params = LinearLayout.LayoutParams(0, ActionBar.LayoutParams.WRAP_CONTENT, 1f)
        params.setMargins(10,0,3,0)
        machineName.layoutParams = params
        machineName.gravity = Gravity.START
        linearLayoutHor.addView(machineName)

        linearLayoutHor.setPadding(5,5,5,5)

        return linearLayoutHor
    }

    // Layout Table Header
    @SuppressLint("ResourceAsColor", "LogNotTimber")
    private fun getLayoutTable():TableLayout{

        val tableHeader = TableLayout(this)

        tableHeader.setColumnStretchable(0, true)
        tableHeader.setColumnStretchable(1, true)
        tableHeader.setColumnStretchable(2, true)
        tableHeader.setColumnStretchable(3, true)
        tableHeader.setColumnStretchable(4, true)
        tableHeader.setColumnStretchable(5, true)

        // Header of table
        val txtName = TextView(this)
        val txtQty = TextView(this)
        val txtPrice = TextView(this)
        val txtTotal = TextView(this)
        val txtDelete = TextView(this)
        txtDelete.width = 10

        val tr = TableRow(this)
        tr.setPadding(2, 5, 0, 5)
        tr.setBackgroundResource(R.drawable.table_header_color)

        txtName.setText(R.string.desc)
        txtName.width = 300
        txtName.typeface = Typeface.DEFAULT_BOLD
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtName.setTextColor(resources.getColor(R.color.colorWhite,null))
        }else{
            txtName.setTextColor(resources.getColor(R.color.colorWhite))
        }
        txtName.gravity = Gravity.START

        txtQty.setText(R.string.qty)
        txtQty.width = 50
        txtQty.typeface = Typeface.DEFAULT_BOLD
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtQty.setTextColor(resources.getColor(R.color.colorWhite,null))
        }else{
            txtQty.setTextColor(resources.getColor(R.color.colorWhite))
        }
        txtQty.gravity = Gravity.CENTER

        txtPrice.setText(R.string.price)
        txtPrice.width = 60
        txtPrice.typeface = Typeface.DEFAULT_BOLD
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtPrice.setTextColor(resources.getColor(R.color.colorWhite,null))
        }else{
            txtPrice.setTextColor(resources.getColor(R.color.colorWhite))
        }
        txtPrice.gravity = Gravity.CENTER

        txtTotal.setText(R.string.total)
        txtTotal.width = 220
        txtTotal.typeface = Typeface.DEFAULT_BOLD
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtTotal.setTextColor(resources.getColor(R.color.colorWhite,null))
        }else{
            txtTotal.setTextColor(resources.getColor(R.color.colorWhite))
        }
        txtTotal.gravity = Gravity.CENTER

        tr.addView(txtName)
        tr.addView(txtQty)
        tr.addView(txtPrice)
        tr.addView(txtTotal)
        tr.addView(txtDelete)

        tableHeader.addView(tr)

        Log.d("TAGTEST","Table header")
        // TODO get item to table
        return tableHeader
    }

    @SuppressLint("LogNotTimber")
    private fun getItemToTable(sysID:Int):TableLayout{

        val t1 = TableLayout(this)

        val itemInMachine = db.getAllItemBySys(sysID,this)

        t1.setColumnStretchable(0, true)
        t1.setColumnStretchable(1, true)
        t1.setColumnStretchable(2, true)
        t1.setColumnStretchable(3, true)
        t1.setColumnStretchable(4, true)



        for(i in 0 until itemInMachine.size){
            val txtName = TextView(this)
            val etQty = EditText(this)
            val txtPrice = TextView(this)
            val txtTotal = TextView(this)
            val txtDelete = TextView(this)

            val tr = TableRow(this)
            tr.setPadding(2, 0, 2, 0)

            txtName.text = itemInMachine[i].p_name
            txtName.textSize = 14f
            txtName.gravity = Gravity.START
            txtName.width = 300

            etQty.setText(itemInMachine[i].p_qty.toString())
            etQty.textSize = 14f
            etQty.setEms(3)
            etQty.inputType = InputType.TYPE_CLASS_NUMBER
            etQty.gravity = Gravity.CENTER
            etQty.width = 50

            txtPrice.text = itemInMachine[i].p_price.toString()
            txtPrice.textSize = 14f
            txtPrice.gravity = Gravity.CENTER
            txtPrice.width = 60

            val qty = Integer.parseInt(etQty.text.toString())
            val total = itemInMachine[i].p_price * qty
            totAmt += total

            txtTotal.text = total.toString()
            txtTotal.textSize = 14f
            txtTotal.gravity = Gravity.CENTER
            txtTotal.width = 220

            txtDelete.text = "X"
            txtDelete.textSize = 14f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                txtDelete.setTextColor(resources.getColor(R.color.colorRed,null))
            }else{
                txtDelete.setTextColor(resources.getColor(R.color.colorRed))
            }
            txtDelete.typeface = Typeface.DEFAULT_BOLD
            txtDelete.gravity = Gravity.CENTER
            txtDelete.width = 10

            BillTotAmt.text = totAmt.toString()

            tr.setBackgroundResource(R.drawable.white_gray_border_bottom)

            tr.addView(txtName)
            tr.addView(etQty)
            tr.addView(txtPrice)
            tr.addView(txtTotal)
            tr.addView(txtDelete)

            t1.addView(tr)

            val priceBefore = arrayOfNulls<String>(1)
            etQty.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
                    if ((etQty.text.toString().trim { it <= ' ' }.length) != 0)
                        priceBefore[0] = etQty.text.toString()
                }

                override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                    //TODO: Fix when empty
                    if ((etQty.text.toString().trim { it <= ' ' }.length) == 0)
                        etQty.setText(0.toString())
                    else {
                        val txtQty = Integer.parseInt(etQty.text.toString())
                        if(act != 1){
                            txtTotal.text = (txtQty * itemInMachine[i].p_price).toString()
                            totAmt = totAmt + java.lang.Double.parseDouble(txtTotal.text.toString()) - Integer.parseInt(
                                priceBefore[0]!!
                            ) * itemInMachine[i].p_price
                            BillTotAmt.text = totAmt.toString()
                        }else{
                            txtTotal.text = (txtQty * itemInMachine[i].p_price).toString()
                            mapDblTotAmtSys[sysID] = mapDblTotAmtSys[sysID]!! + java.lang.Double.parseDouble(txtTotal.text.toString()) - Integer.parseInt(
                                priceBefore[0]!!
                            ) * itemInMachine[i].p_price
                            mapTxtTotAmtSys[sysID]!!.text = mapDblTotAmtSys[sysID].toString()
                        }

                        //
                        db.updateItemByID(itemInMachine[i].sysID,itemInMachine[i].prodItemID,txtQty,applicationContext)
                    }

                }

                override fun afterTextChanged(editable: Editable) {}
            })

            txtDelete.setOnClickListener {
                val tt = txtDelete.parent as TableRow
                val tv = tt.getChildAt(3) as TextView
                val ss = tv.text.toString()
                totAmt -= java.lang.Double.parseDouble(ss)
                BillTotAmt.text = totAmt.toString()
                tt.removeAllViews()
                db.deleteItemFromDB(itemInMachine[i].sysID,itemInMachine[i].prodItemID,applicationContext)
            }
        }
        return t1
    }

    @SuppressLint("LogNotTimber")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home-> {
                when(act){
                    1 ->{
                        startActivity(Intent(this@BillActivity,MapActivity::class.java))
                        finish()
                    }
                    2 ->{
                        val intent = Intent(this@BillActivity,SectionActivity::class.java)
                        intent.putExtra("title",smName)
                        intent.putExtra("id",singleSysID)
                        startActivity(intent)
                        finish()
                    }
                    3->{
                        val intent = Intent(this@BillActivity,ProductActivity::class.java)
                        intent.putExtra("sp_name",spName)
                        intent.putExtra("sm_id",singleSysID)
                        intent.putExtra("sp_id",spID)
                        startActivity(intent)
                        finish()
                    }
                    4->{
                        val intent = Intent(this@BillActivity,ProductActivity::class.java)
                        intent.putExtra("prodID",prodID)
                        intent.putExtra("sysID",singleSysID)
                        intent.putExtra("qty",qty)
                        intent.putExtra("secName",spName)
                        startActivity(intent)
                        finish()
                    }
                }
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun btnConfirmClick(sysID: Int){
        startActivity(Intent(this@BillActivity,ConfirmActivity::class.java).putExtra("sysID",sysID))
    }
}
