package com.hawi.myapplication

import android.annotation.SuppressLint
import com.android.volley.toolbox.NetworkImageView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.LayoutInflater
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import android.widget.*


class ProductRecycleAdapter(
    private var activity: Activity?,
    private var items: ArrayList<ProductItems>,
    private val sysID:Int,
    private val secID:Int, private val secName:String) :
    RecyclerView.Adapter<ProductRecycleAdapter.ViewHolder>() {

    private var imageLoader = AppControllerProf.getmInstance().getmImageLoader()
    private var shopAvailable = 0
    private val db = DBProductInShopping(activity!!.applicationContext)


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.single_item_product_recycle, null)
        return ViewHolder(itemView)
    }

    @SuppressLint("LogNotTimber")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val qty = 1

        if(imageLoader == null){
            imageLoader = AppControllerProf.getmInstance().getmImageLoader()
        }
        p0.img.setImageUrl(Constant().URL_IMG+items[p1].p_img,imageLoader)
        p0.details.text = items[p1].p_desc
        p0.name.text = items[p1].p_name
        p0.price.text = items[p1].p_price.toString()
        p0.qty.setText(qty.toString())

        p0.add.setOnClickListener {
            val itQTY = items[p1].p_qty // qty in db
            var qtyAdd = Integer.parseInt(p0.qty.text.toString()) // qty in editText
            if (qtyAdd < itQTY){
                qtyAdd += 1
                p0.qty.setText(qtyAdd.toString())
            }else if (qtyAdd == 0){
                qtyAdd += qtyAdd
                p0.qty.setText(qtyAdd.toString())
            }
        }

        p0.min.setOnClickListener {
            var qtyMin = Integer.parseInt(p0.qty.text.toString()) // qty in editText
            if(qtyMin > 1){
                qtyMin -= 1
                p0.qty.setText(qtyMin.toString())
            }
        }

        val prodInShop = db.getAllItemByID(sysID,items[p1].prodItemID,activity!!.applicationContext)

        if(db.getAllItemBySys(sysID,activity!!.applicationContext).isEmpty()){
            shopAvailable = 0
            btnProc()
        }else{
            shopAvailable = 1
            btnProc()
        }

        if(db.getAllFromDB().size > 0){
            if(prodInShop.size > 0){
                for(i in 0 until prodInShop.size){
                    val pID = prodInShop[i].prodItemID
                    val sID = prodInShop[i].sysID

                    if(pID == items[p1].prodItemID && sID == items[p1].sysID ){
                        p0.qty.setText(prodInShop[i].p_qty.toString())
                        p0.shop.setImageResource(R.drawable.ic_shopping_blue)
                        //TODO change menu_shopping icon menu color
                        shopAvailable = 1
                        btnProc()
                    }
                }
            }
        }


        p0.shop.setOnClickListener {

            if(db.getAllFromDB().size == 0){
                p0.shop.setImageResource(R.drawable.ic_shopping_blue)
                // TODO add to shared pref
                db.addItemShopToDB(items[p1],secID,sysID,Integer.parseInt(p0.qty.text.toString()),activity!!.applicationContext)
                shopAvailable = 1
                btnProc()

                return@setOnClickListener
            }

            if(db.getItemByID(sysID,items[p1].prodItemID,activity!!.applicationContext)){
                p0.shop.setImageResource(R.drawable.ic_shopping)
                db.deleteItemFromDB(sysID,items[p1].prodItemID,activity!!.applicationContext)
                if(db.getAllItemBySys(sysID,activity!!.applicationContext).size == 0){
                    shopAvailable = 0
                    btnProc()
                }
            }else{
                p0.shop.setImageResource(R.drawable.ic_shopping_blue)
                db.addItemShopToDB(items[p1],secID,sysID,Integer.parseInt(p0.qty.text.toString()),activity!!.applicationContext)
                shopAvailable = 1
                btnProc()

            }
        }

        p0.itemView.setOnClickListener {
            val intent = Intent(activity!!,ProductItemDetailActivity::class.java)
            intent.putExtra("qty",Integer.parseInt(p0.qty.text.toString()))
            intent.putExtra("sysID",sysID)
            intent.putExtra("prodID",items[p1].prodItemID)
            intent.putExtra("secName",secName)
            activity!!.startActivity(intent)
            activity!!.finish()
        }
    }

    @Suppress("DEPRECATION")
    private fun btnProc() {
        val btnProc = activity!!.findViewById<Button>(R.id.btnProcess)!!
        if (shopAvailable == 0) {
            btnProc.isEnabled = false
            btnProc.setBackgroundResource(R.color.colorGreen)
            btnProc.resources.getColor(R.color.colorWhite)
            btnProc.setText(R.string.btn_disable)
        } else {
            btnProc.isEnabled = true
            btnProc.setBackgroundResource(R.color.colorPrimary)
            btnProc.resources.getColor(R.color.colorWhite)
            btnProc.setText(R.string.btn_enable)
            btnProc.setOnClickListener{
                val db = DBProductInShopping(activity!!.applicationContext)
                if(db.getItemCount() > 0){
                    // cart is enable
                    val intent = Intent(activity!!.applicationContext,BillActivity::class.java)
                    intent.putExtra("activity",3)
                    intent.putExtra("sp_id",secID)
                    intent.putExtra("sp_name",secName)
                    intent.putExtra("sysID",sysID)
                    activity!!.startActivity(intent)
                }else{
                    // no item has in cart
                    Toast.makeText(activity!!.applicationContext,"You don't have any item in cart, Please Add item!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var img: NetworkImageView = itemView.findViewById(R.id.itemImg)
        var shop: ImageView = itemView.findViewById(R.id.itemShopping) as ImageView
        var name: TextView = itemView.findViewById(R.id.itemName)
        var price: TextView = itemView.findViewById(R.id.itemPrice)
        var details: TextView = itemView.findViewById(R.id.itemDetail)
        var qty:EditText = itemView.findViewById(R.id.etQTY)
        var add:TextView = itemView.findViewById(R.id.btnAdd)
        var min:TextView = itemView.findViewById(R.id.btnMin)

    }
}