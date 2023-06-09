package com.coderstory.flyme10.tools

import android.content.Context
import android.content.SharedPreferences
import kotlin.math.roundToInt

class Utils {
    companion object {
        fun convertDpToPixel(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return (dp.toFloat() * density).roundToInt()
        }

        fun getMySharedPreferences(
            context: Context?,
            fileName: String?
        ): SharedPreferences {
            var result: SharedPreferences
            try {
                result = context!!.getSharedPreferences(fileName, Context.MODE_WORLD_READABLE)
            } catch (exp: Exception) {
                result = context!!.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            }
            return result
        }
    }
}