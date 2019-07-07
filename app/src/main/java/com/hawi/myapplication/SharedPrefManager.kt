package com.hawi.myapplication

import android.content.Context
import java.io.File

class SharedPrefManager (context: Context) {
    //هذه المتغيرات لكي نستخدمها لحفظ بيانات المستخدم وتعمل هنا كمفاتيح
    private val SHARED_PREF_NAME = "mysharedpref12"
    private val KEY_USER_ID = "username"
    private val KEY_USER_NAME = "userid"
    private val KEY_USER_PHONE = "userephone"
    private val KEY_USER_MAIL = "useremail"

    val mCtx = context

    // هذه الدالة تستخدم لحفظ معلومات المستخدم عند تسجيل الدخول
    fun userLogin(user_id: Int, username: String, phone: Int): Boolean {
        // نأخذ مثيل من الكلاس SharedPreferences ويحمل عدد 2 براميتر الاول اسم من عندنا يميزه والثاني نجعله برايفت اي خاص بالتطبيق فقط
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        // نقوم الان بالتعديل على بيانات الكلاس SharedPreferences ونجعله يحفظ لنا المعلومات التي نعطيه
        val editor = sharedPreferences.edit()

        editor.putInt(KEY_USER_ID, user_id)
        editor.putString(KEY_USER_NAME, username)
        editor.putInt(KEY_USER_PHONE, phone)

        // ثم نعمل تطبيق على الكلاس ليتم حفظ البيانات التي اعطيناه
        editor.apply()

        return true
    }

    // هذه الدالة تستخدم لحفظ معلومات المستخدم عند تسجيل الدخول
    fun userUpdate(user_id: Int, username: String, phone: Int ,mail: String): Boolean {
        // نأخذ مثيل من الكلاس SharedPreferences ويحمل عدد 2 براميتر الاول اسم من عندنا يميزه والثاني نجعله برايفت اي خاص بالتطبيق فقط
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        // نقوم الان بالتعديل على بيانات الكلاس SharedPreferences ونجعله يحفظ لنا المعلومات التي نعطيه
        val editor = sharedPreferences.edit()

        editor.putInt(KEY_USER_ID, user_id)
        editor.putString(KEY_USER_NAME, username)
        editor.putInt(KEY_USER_PHONE, phone)
        editor.putString(KEY_USER_MAIL, mail)

        // ثم نعمل تطبيق على الكلاس ليتم حفظ البيانات التي اعطيناه
        editor.apply()

        return true
    }

    // هذه الدالة تستخدم للتاكد هل تم تسجيل الدخول ام لا
    fun isLoggedIn(): Boolean {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_NAME, null) != null
    }

    // هذه الدالة لعمل تسجيل الخروج
    fun logout(): Boolean {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear().apply()
        deleteCache(mCtx)
        return true
    }

    private fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
        }

    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            return dir.delete()
        } else return if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    fun getUserName(): String? {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun getUserPhone(): Int? {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_USER_PHONE,0)
    }

    fun getUserID(): Int {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_USER_ID, 0)
    }

    fun getUserEmail(): String? {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_MAIL, null)
    }

}