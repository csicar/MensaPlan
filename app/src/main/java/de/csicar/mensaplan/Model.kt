package de.csicar.mensaplan

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.text.format.DateUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by csicar on 06.02.18.
 */
class Canteen(val name: String, val days: List<FoodDay>){
    companion object {
        val excludedCanteenNames : List<String> = listOf("date", "import_date")

        fun getNiceNameFor(name: String) = Canteens.default[name]?.niceName ?: name

        fun getImageUrlFor(name: String) = Canteens.default[name]?.image

        fun getLocationUrlFor(name: String) = Canteens.default[name]?.maps

        fun getOpenTimesFor(name: String) = Canteens.default[name]?.openTimes

        fun canteensFromJson(content: JSONObject) : List<Canteen> {
            val canteens = mutableListOf<Canteen>()
            content.keys().forEach {
                if (!excludedCanteenNames.contains(it)) {
                    val foodJson = content.getJSONObject(it)
                    val canteen = Canteen(it, FoodDay.daysFromJson(foodJson))
                    canteens.add(canteen)
                }
            }

            return canteens
        }
    }

    fun getNiceName() : String = getNiceNameFor(name)
    fun getImageUrl() : String? = getImageUrlFor(name)
    fun getLocationUrl() : Uri? = getLocationUrlFor(name)
    fun getOpenTimes() : String? = getOpenTimesFor(name)
}

class FoodDay(val date: Date, val lines: List<Line>) {
    companion object {
        fun daysFromJson(content: JSONObject) : List<FoodDay> {
            val days = mutableListOf<FoodDay>()
            content.keys().forEach {
                val lines = Line.linesFromJson(content.getJSONObject(it))
                val day = FoodDay(Date(it.toLong()*1000), lines)
                days.add(day)
            }
            return days
        }
    }

    fun getFormattedDate() : String {
        if (DateUtils.isToday(date.time)) {
            return SimpleDateFormat("E").format(date) + " (Heute)"
        }

        return SimpleDateFormat("E (d.M.)").format(date)
    }
}

sealed class Line(val name: String) {
    companion object {
        val niceNames = mapOf<String, String>(
                "l1" to "Linie 1",
                "l2" to "Linie 2",
                "l3" to "Linie 3",
                "l45" to "Linie 4/5",
                "schnitzelbar" to "Schnitzelbar",
                "update" to "L6 Update",
                "abend" to "Abend",
                "aktion" to "[Kœri]werk",
                "heisstheke" to "Cafeteria Heiße Theke",
                "nmtisch" to "Cafeteria ab 14:30",
                "wahl1" to "Wahlessen 1",
                "wahl2" to "Wahlessen 2",
                "wahl3" to "Wahlessen 3",
                "gut" to "Gut & Günstig",
                "gut2" to "Gut & Günstig 2",
                "buffet" to "Buffet",
                "curryqueen" to "[Kœri]werk")
        fun linesFromJson(content: JSONObject) : List<Line> {
            val lines = mutableListOf<Line>()
            content.keys().forEach {
                //TODO when "closing_start" is the only meal, set line to closed
                val children = content.getJSONArray(it)
                if (children.length() != 0
                        && children.getJSONObject(0).has("closing_start")
                        && children.getJSONObject(0).has("closing_end")) {
                    val closingStart = children.getJSONObject(0).getLong("closing_start")
                    val closingEnd = children.getJSONObject(0).getLong("closing_end")

                    val line = ClosedLine(it, Date(closingStart*1000), Date(closingEnd*1000))
                    lines.add(line)
                } else {
                    val meals = Meal.mealsFromJson(children)
                    val line = OpenLine(it, meals)
                    lines.add(line)
                }
            }
            return lines
        }
    }

    class OpenLine(name: String, val meals: List<Meal>) : Line(name)
    class ClosedLine(name: String, val closingStart: Date, val closingEnd: Date) : Line(name) {
        fun getFormattetClosed() : String {
            val start = SimpleDateFormat("E (d.M.)").format(closingStart)
            val end = SimpleDateFormat("E (d.M.)").format(closingEnd)
            return "$start - $end"
        }
    }

    fun getNiceName() : String = niceNames.getOrDefault(name, name)
}

class Meal(
        val meal: String,
        val dish: String,
        val additives: List<String>,
        val price: Price) {
    companion object {

        fun mealsFromJson(content: JSONArray) : List<Meal> {
            val meals = mutableListOf<Meal>()
            var index = 0
            while(index < content.length()) {
                val meal = mealFromJson(content.getJSONObject(index))
                if (meal != null) {
                    meals.add(meal)
                }
                index++
            }
            return meals
        }

        fun mealFromJson(content: JSONObject) : Meal? {
            if (content.has("nodata") || content.has("closing_start")) {
                return null
            }
            return Meal(
                    content.getString("meal"),
                    content.getString("dish"),
                    additivesFromJson(content.getJSONArray("add")),
                    Price.priceFromJson(content)
                    )
        }

        fun additivesFromJson(content: JSONArray) : List<String> {
            val additives = mutableListOf<String>()
            var index = 0
            while(index < content.length()) {
                val additive = content.getString(index)
                additives.add(additive)
                index++
            }
            return additives
        }

    }
    fun containsBadAdditives(context: Context) : Boolean {
        val badAdditives = PreferenceManager.getDefaultSharedPreferences(context).getStringSet("additives", emptySet<String>())
        return additives.any { badAdditives.contains(it) }
    }
}

class Price(val price1: Double, val price2: Double, val price3: Double, val price4: Double, val priceFlag: Int) {
    companion object {
        fun priceFromJson(content: JSONObject) : Price {
            return Price(
                    content.getDouble("price_1"),
                    content.getDouble("price_2"),
                    content.getDouble("price_3"),
                    content.getDouble("price_4"),
                    content.getInt("price_flag")
                    )
        }
    }

    fun showPrice(context: Context) : String {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val group = pref.getString("price_group", "0").toInt()
        val price = when(group) {
            1 -> price1
            2 -> price2
            3 -> price3
            4 -> price4
            else -> price1
        }
        val format = NumberFormat.getCurrencyInstance()

        return format.format(price)
    }
}