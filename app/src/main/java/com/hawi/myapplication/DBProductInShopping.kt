package com.hawi.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DBProductInShopping(context: Context) : SQLiteOpenHelper(context, "production", null, 1) {

    private val tbProduct = "prod_in_ship"
    private val tbMachine = "machine_place"
    private val viewProductAndMachine = "prod_item"
    private var cmt: Context? = null

    @SuppressLint("LogNotTimber")
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE IF NOT EXISTS $tbProduct (pName TEXT, pDesc TEXT , pQTY INTEGER , pImg Text , pSectionID INTEGER ,pProductItemID INTEGER , pSystemID INTEGER, pPrice REAL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $tbMachine (mName TEXT, mID INTEGER)")
        db.execSQL("CREATE VIEW IF NOT EXISTS $viewProductAndMachine AS SELECT pName, pDesc, pQTY, pImg, pSectionID, pProductItemID, pSystemID, pPrice, mName FROM $tbProduct , $tbMachine WHERE $tbProduct.pSystemID = $tbMachine.mID")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $tbProduct ")
        db.execSQL("DROP TABLE IF EXISTS $tbMachine ")
        onCreate(db)
    }

    fun addMachine(machineName:String,machineID:Int){
        val db:SQLiteDatabase = this.writableDatabase
        val content = ContentValues()
        content.put("mName",machineName)
        content.put("mID",machineID)
        db.insert(tbMachine,null,content)
        db.close()
    }

    @SuppressLint("LogNotTimber", "Recycle")
    fun emptyMachineTable() {
        val dbRead = this.readableDatabase
        val qry = "SELECT COUNT(*) FROM $tbMachine"
        val mCursor = dbRead.rawQuery(qry,null)
        mCursor.moveToFirst()
        val iCount = mCursor.getInt(0)
        if(iCount > 0){
            // table is not empty
        val db = this.writableDatabase
        db.delete(tbMachine,"1",null) > 0
        }else{
            // table is empty
            Log.d("TAGTest","From db.emptyMachineTable")
        }
    }

    fun emptyDB():Boolean {
        val db = this.writableDatabase
        return db.delete(tbProduct,"1",null) > 0
    }

    @SuppressLint("LogNotTimber", "Recycle")
    fun getItemCount():Int {
        val dbRead = this.readableDatabase
        val qry = "SELECT COUNT(*) FROM $tbProduct"
        val mCursor = dbRead.rawQuery(qry,null)
        mCursor.moveToFirst()
//        val iCount = mCursor.getInt(0)
//        return iCount
        return mCursor.getInt(0)
    }

    @SuppressLint("Recycle", "LogNotTimber")
    fun getAllSystem(): ArrayList<SystemMachine>? {
        val dbRead = this.readableDatabase
        val mCursor = dbRead.rawQuery("SELECT COUNT(*) FROM $tbMachine",null)
        mCursor.moveToFirst()
        val iCNT = mCursor.getInt(0)
        val sysList = ArrayList<SystemMachine>()
        if(iCNT > 0){
            val db:SQLiteDatabase = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $tbMachine",null)
            if(cursor.moveToFirst()){
                do {
                    val sysItem = SystemMachine()
                    sysItem.sSysName = cursor.getString(cursor.getColumnIndex("mName"))
                    sysItem.sSysID = cursor.getInt(cursor.getColumnIndex("mID"))

                    sysList.add(sysItem)
                } while (cursor.moveToNext())
            }
        }
        return sysList
    }

    fun getSystemInShop(): ArrayList<SystemMachine>? {
        val dbRead = this.readableDatabase
        val mCursor = dbRead.rawQuery("SELECT COUNT(*) FROM $tbMachine",null)
        mCursor.moveToFirst()
        val iCNT = mCursor.getInt(0)
        val sysList = ArrayList<SystemMachine>()
        if(iCNT > 0){
            val db:SQLiteDatabase = this.readableDatabase
            val cursor = db.rawQuery("SELECT pSystemID , mName FROM $viewProductAndMachine GROUP BY pSystemID",null)
            if(cursor.moveToFirst()){
                do {
                    val sysItem = SystemMachine()
                    sysItem.sSysName = cursor.getString(cursor.getColumnIndex("mName"))
                    sysItem.sSysID = cursor.getInt(cursor.getColumnIndex("pSystemID"))

                    sysList.add(sysItem)
                } while (cursor.moveToNext())
            }
        }
        return sysList
    }

    @SuppressLint("Recycle")
    fun getAllFromDB():ArrayList<ProductItems>{

        val db:SQLiteDatabase = this.readableDatabase
        val qry = "SELECT * FROM $viewProductAndMachine"
        val cursor = db.rawQuery(qry,null)
        val itemList = ArrayList<ProductItems>()
        if(cursor.moveToFirst()){
            do {
                val item = ProductItems()
                item.p_name = cursor.getString(cursor.getColumnIndex("pName"))
                item.p_desc = cursor.getString(cursor.getColumnIndex("pDesc"))
                item.p_qty = cursor.getInt(cursor.getColumnIndex("pQTY"))
                item.p_img = cursor.getString(cursor.getColumnIndex("pImg"))
                item.prodItemID = cursor.getInt(cursor.getColumnIndex("pProductItemID"))
                item.sysID = cursor.getInt(cursor.getColumnIndex("pSystemID"))
                item.secID = cursor.getInt(cursor.getColumnIndex("pSectionID"))
                item.p_price = cursor.getDouble(cursor.getColumnIndex("pPrice"))
                item.pSysName = cursor.getString(cursor.getColumnIndex("mName"))

                itemList.add(item)
            } while (cursor.moveToNext())
        }
        return itemList
    }

    @SuppressLint("Recycle", "LogNotTimber")
    fun getAllItemByID(sysID:Int, prodID:Int, cmt:Context):ArrayList<ProductItems>{
        this.cmt = cmt
        val db:SQLiteDatabase = this.readableDatabase
        val qry = "SELECT * FROM $tbProduct WHERE pProductItemID = $prodID AND pSystemID = $sysID"
        val cursor = db.rawQuery(qry,null)
        val itemList = ArrayList<ProductItems>()
        if(cursor.moveToFirst()){
            do {
                val item = ProductItems()
                item.p_name = cursor.getString(cursor.getColumnIndex("pName"))
                item.p_desc = cursor.getString(cursor.getColumnIndex("pDesc"))
                item.p_qty = cursor.getInt(cursor.getColumnIndex("pQTY"))
                item.p_img = cursor.getString(cursor.getColumnIndex("pImg"))
                item.prodItemID = cursor.getInt(cursor.getColumnIndex("pProductItemID"))
                item.sysID = cursor.getInt(cursor.getColumnIndex("pSystemID"))
                item.secID = cursor.getInt(cursor.getColumnIndex("pSectionID"))
                item.p_price = cursor.getDouble(cursor.getColumnIndex("pPrice"))

                itemList.add(item)
            } while (cursor.moveToNext())
        }
        return itemList
    }

    @SuppressLint("Recycle", "LogNotTimber")
    fun getAllItemBySys(sysID:Int, cmt:Context):ArrayList<ProductItems>{
        this.cmt = cmt
        val db:SQLiteDatabase = this.readableDatabase
        val qry = "SELECT * FROM $tbProduct WHERE pSystemID = $sysID"
        val cursor = db.rawQuery(qry,null)
        val itemList = ArrayList<ProductItems>()
        if(cursor.moveToFirst()){
            do {
                val item = ProductItems()
                item.p_name = cursor.getString(cursor.getColumnIndex("pName"))
                item.p_desc = cursor.getString(cursor.getColumnIndex("pDesc"))
                item.p_qty = cursor.getInt(cursor.getColumnIndex("pQTY"))
                item.p_img = cursor.getString(cursor.getColumnIndex("pImg"))
                item.prodItemID = cursor.getInt(cursor.getColumnIndex("pProductItemID"))
                item.sysID = cursor.getInt(cursor.getColumnIndex("pSystemID"))
                item.secID = cursor.getInt(cursor.getColumnIndex("pSectionID"))
                item.p_price = cursor.getDouble(cursor.getColumnIndex("pPrice"))

                itemList.add(item)
            } while (cursor.moveToNext())
        }
        return itemList
    }

    @SuppressLint("LogNotTimber")
    fun addItemShopToDB(prodShop:ProductItems, secID:Int, sysID:Int, p_qty:Int, cmt:Context):Boolean{
        this.cmt = cmt
        val db:SQLiteDatabase = this.writableDatabase
        val content = ContentValues()
        content.put("pName",prodShop.p_name)
        content.put("pDesc",prodShop.p_desc)
        content.put("pQTY",p_qty)
        content.put("pImg",prodShop.p_img)
        content.put("pSectionID",secID)
        content.put("pProductItemID",prodShop.prodItemID)
        content.put("pSystemID",sysID)
        content.put("pPrice",prodShop.p_price)

        return db.insert(tbProduct,null,content) > 0
    }

    @SuppressLint("Recycle")
    fun getItemByID(sysID:Int, prodID:Int, cmt:Context):Boolean{
        this.cmt = cmt
        val db = this.readableDatabase
        val qry = ("SELECT * FROM $tbProduct WHERE pProductItemID = $prodID AND pSystemID = $sysID")
        return db.rawQuery(qry,null).count > 0
    }

    fun updateItemByID(sysID:Int, prodID:Int, pQTY:Int, cmt:Context):Boolean{
        this.cmt = cmt
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("pQTY" , pQTY)
        val str = "pProductItemID = $prodID AND pSystemID = $sysID"
        return db.update(tbProduct,contentValues,str,null) > 0
    }

    fun deleteItemFromDB(sysID:Int, prodID:Int, cmt:Context):Boolean{
        this.cmt = cmt
        val db = this.writableDatabase
        val str = "`pSystemID` = '$sysID' AND `pProductItemID` = '$prodID'"
        return db.delete(tbProduct,str,null) > 0
    }

    fun deleteItemFromSystem(sysID:Int):Boolean{
        val db = this.writableDatabase
        val str = "`pSystemID` = '$sysID'"
        return db.delete(tbProduct,str,null) > 0
    }

    @SuppressLint("Recycle", "LogNotTimber")
    fun getTotalPriceOfSystem(sysID:Int, cmt:Context):Double{
        this.cmt = cmt
        var totPrice = 0.0
        val db:SQLiteDatabase = this.readableDatabase
        val qry = "SELECT * FROM $tbProduct WHERE pSystemID = $sysID"
        val cursor = db.rawQuery(qry,null)
        if(cursor.moveToFirst()){
            do {
                val price = cursor.getDouble(cursor.getColumnIndex("pPrice"))
                val qty = cursor.getInt(cursor.getColumnIndex("pQTY"))
                totPrice += price * qty
            } while (cursor.moveToNext())
        }
        return totPrice
    }
}