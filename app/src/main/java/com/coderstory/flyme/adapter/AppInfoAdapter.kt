package com.coderstory.flyme.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.coderstory.flyme.R
import com.coderstory.flyme.R.id

class AppInfoAdapter(context: Context?, private val resourceId: Int, objects: List<AppInfo?>?) :
    ArrayAdapter<Any?>(context!!, resourceId, objects!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val appInfo = getItem(position) as AppInfo?
        val view: View
        val vh: ViewHolder

        if (convertView != null) { //查询布局是否已经缓存
            view = convertView
            vh = view.tag as ViewHolder //重新获取ViewHolder
        } else {
            view = LayoutInflater.from(context).inflate(resourceId, null) //读取items.xml文件并实例化
            vh = ViewHolder()
            vh.myImage = view.findViewById(id.app_image) //查找items实例中的myimage
            vh.myText = view.findViewById(id.app_name) //查找items实例中的mytext
            view.tag = vh //保存到view中
        }

        vh.myText!!.tag = appInfo!!.packageName
        if (appInfo.imageId != null) {
            vh.myImage!!.setImageDrawable(appInfo.imageId)
            vh.myText!!.text =
                String.format(context.getString(R.string.appname), appInfo.name, appInfo.version)
        } else {
            vh.myText!!.text = String.format(
                context.getString(R.string.app_info),
                appInfo.name,
                appInfo.fileSize,
                appInfo.releaseDate
            )
            vh.myImage!!.visibility = View.GONE
        }

        if (appInfo.disable) {
            view.setBackgroundColor(Color.parseColor("#d0d7d7d7")) //冻结的颜色
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary)) //正常的的颜色
        }

        return view
    }

    private inner class ViewHolder {
        var myImage: ImageView? = null
        var myText: TextView? = null
    }
}