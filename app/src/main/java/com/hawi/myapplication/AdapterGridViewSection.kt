package com.hawi.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.single_item_gridview_section.view.*


class AdapterGridViewSection(private var sectionList: ArrayList<SectionItem>, var context: Context?, var sm_id:Int,var sm_name:String) : BaseAdapter() {

    var view: View? = null
    lateinit var layoutInflater: LayoutInflater
    private val imageLoader = AppControllerProf.getmInstance().getmImageLoader()

    @SuppressLint("LogNotTimber")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if(p1 == null) {
            view = View(context)
            view = layoutInflater.inflate(R.layout.single_item_gridview_section, null)
            view!!.img_single_grid_view_main.setImageUrl(Constant().URL_IMG+sectionList[p0].s_img,imageLoader)

            view!!.tv_single_grid_view_main.text = sectionList[p0].s_name


            // pass activity
            view!!.setOnClickListener {
                val spID = sectionList[p0].s_id
                val spName = sectionList[p0].s_name

                val intent = Intent(context!!,ProductActivity::class.java)
                intent.putExtra("sm_id",sm_id)
                intent.putExtra("sp_id",spID)
                intent.putExtra("sp_name",spName)
                context!!.startActivity(intent)
            }
        }
        return view!!
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return sectionList.size
    }

}