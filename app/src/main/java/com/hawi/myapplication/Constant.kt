package com.hawi.myapplication

class Constant {
    private val URL_INV = "http://10.0.2.2/machine_project/"
    private val ROOT_URL = URL_INV + "includeJSON/"
    val URL_REGISTER = ROOT_URL + "register.php"
    val URL_LOGIN = ROOT_URL + "login.php"
    val URL_MACHINE_PLACE = ROOT_URL + "system_machine.php"
    val URL_SECTION = ROOT_URL + "section.php"
    val URL_PRODUCT = ROOT_URL + "product.php"
    val URL_PRODUCT_DETAIL = ROOT_URL + "productDetail.php"
    val URL_SYSTEM = ROOT_URL + "systemID.php"
    val URL_ADD_INVOICE = ROOT_URL + "newOrder.php"
    val URL_AD = ROOT_URL + "ad.php"
    val URL_IMG = URL_INV + "cms/upload/"
    val URL_IMG_AD = URL_INV + "cms/imgAD/"
    val URL_UPDATE_USER_INFO = ROOT_URL + "updateUser.php"
    val URL_UPDATE_USER_PASS = ROOT_URL + "updatePass.php"
    val URL_INVOICE = ROOT_URL + "invoice.php"
    val URL_INVOICE_DETAIL = ROOT_URL + "invoiceDetail.php"
    val URL_USER_ID = ROOT_URL + "userID.php"
}