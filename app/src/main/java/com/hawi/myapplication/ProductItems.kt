package com.hawi.myapplication

class ProductItems {
    var p_name = ""
    var p_desc = ""
    var p_qty = 0
    var p_img = ""
    var prodItemID = 0
    var sysID = 0
    var secID = 0
    var p_price = 0.0
    var pSysName = ""

    constructor(p_name: String, p_desc: String, p_qty: Int, p_img: String, prodItemID: Int, sysID: Int, secID: Int ,p_price:Double) {
        this.p_name = p_name
        this.p_desc = p_desc
        this.p_qty = p_qty
        this.p_img = p_img
        this.prodItemID = prodItemID
        this.sysID = sysID
        this.secID = secID
        this.p_price = p_price
    }
    constructor(p_name: String, p_desc: String, p_qty: Int, p_img: String, prodItemID: Int, sysID: Int, secID: Int ,p_price:Double ,pSysName:String) {
        this.p_name = p_name
        this.p_desc = p_desc
        this.p_qty = p_qty
        this.p_img = p_img
        this.prodItemID = prodItemID
        this.sysID = sysID
        this.secID = secID
        this.p_price = p_price
        this.pSysName = pSysName
    }
    constructor()
}