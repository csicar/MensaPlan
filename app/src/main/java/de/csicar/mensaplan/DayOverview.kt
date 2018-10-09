package de.csicar.mensaplan

import android.app.SearchManager
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Response
import java.net.URLEncoder


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DayOverview.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DayOverview.newInstance] factory method to
 * create an instance of this fragment.
 */
class DayOverview : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var adapter: ListRowAdapter
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_day_overview, container, false)

        val context = container?.context

        // Inflate the layout for this fragment
        val items = mutableListOf<ListItem>()


        val recyclerView = view.findViewById<RecyclerView>(R.id.day_overview_listview)
        recyclerView.layoutManager = LinearLayoutManager(context)



        adapter = ListRowAdapter(items, this::onListItemClick)

        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        val day = arguments.getInt(DAY)
        val canteenName = arguments.getString(CANTEEN)
        updateItems(BackendApi.canteens, day, canteenName, items, adapter)
        BackendApi.onUpdate(Response.Listener {
            updateItems(it, day, canteenName, items, adapter)
        })

        return view
    }

    private fun onListItemClick(item: ListItem, position: Int, view: View) {
        when(item) {
            is ListItem.MealItem -> {
                    searchWeb(item.meal.meal + item.meal.dish)
            }
            is ListItem.HeaderItem -> {
                shareText("Ich gehe zu ${item.line.getNiceName()}")
            }
        }
    }

    private fun shareText(text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share '$text' with"))
    }

    private fun searchWeb(query: String) {
        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
        }

        if (searchIntent.resolveActivity(this.context.packageManager) != null) {
            startActivity(searchIntent)
        } else {
            val escapedQuery = URLEncoder.encode(query, "UTF-8")
            val uri = Uri.parse("http://www.google.com/search?q=$escapedQuery")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun updateItems(canteens: List<Canteen>, dayIndex: Int, canteenName: String, items: MutableList<ListItem>, adapter: ListRowAdapter) {
        val canteenValue = canteens.find { it.name == canteenName }
        if (canteenValue == null) {
            adapter.notifyDataSetChanged()
            return
        }
        items.clear()
        canteenValue.days[dayIndex].lines.forEach {
            items.add(ListItem.HeaderItem(it))
            when (it) {
                is Line.ClosedLine -> {
                }
                is Line.OpenLine -> it.meals.forEach {
                    items.add(ListItem.MealItem(it))
                }
            }

        }
        adapter.notifyDataSetChanged()
    }

    private class ListRowAdapter(val sList: List<ListItem>, val onItemClickListener: (ListItem, Int, View) -> Unit) : RecyclerView.Adapter<ListRowHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListRowHolder {
            val context = parent?.context
            val rowView = when (viewType) {
                1 -> LayoutInflater.from(context).inflate(R.layout.list_row, parent, false)
                else -> LayoutInflater.from(context).inflate(R.layout.list_header_row, parent, false)
            }
            val rowHolder = ListRowHolder(rowView)
            return rowHolder
        }

        override fun getItemCount(): Int {
            return sList.size
        }

        override fun getItemViewType(position: Int): Int {
            return when (sList[position]) {
                is DayOverview.ListItem.HeaderItem -> 0
                is DayOverview.ListItem.MealItem -> 1
            }
        }

        fun setDisplayedImages(properties: List<MealProperty>, holder: ListRowHolder) {
            holder.images.forEach {
                it.value?.visibility = when(properties.contains(it.key)) {
                    true -> View.VISIBLE
                    false -> View.GONE
                }
            }
        }

        override fun onBindViewHolder(holder: ListRowHolder?, position: Int) {
            val item = sList[position]
            when (item) {
                is ListItem.HeaderItem -> {
                    holder?.headerClosedInfo?.visibility = View.GONE
                    when (item.line) {
                        is Line.OpenLine -> holder?.lineTitle?.text = item.line.getNiceName()
                        is Line.ClosedLine -> {
                            holder?.lineTitle?.text = item.line.getNiceName()
                            holder?.headerClosedInfo?.text = "geschlossen von ${item.line.getFormattetClosed()}"
                            holder?.headerClosedInfo?.visibility = View.VISIBLE
                        }
                    }

                }
                is ListItem.MealItem -> {
                    val context = holder?.row!!.context
                    holder.mealName?.text = item.meal.meal
                    holder.mealDesc?.text = item.meal.dish
                    holder.additives?.text = item.meal.additives.joinToString(", ")
                    holder.price?.text = item.meal.price.showPrice(holder.price!!.context)

                    val greyOutUnwanted = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("unwanted_food_grey_out", true)
                    val foodUnwanted = item.meal.containsBadAdditives(context) || item.meal.containsBadProperties(context);
                    holder.mealContainer?.alpha = if (foodUnwanted && greyOutUnwanted) 0.2f else 1.0f

                    setDisplayedImages(item.meal.properties, holder)

                    val showAdditives = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_additives", true)
                    holder.additives?.visibility = if (showAdditives) View.VISIBLE else View.GONE
                }
            }
            holder?.row?.setOnClickListener { onItemClickListener(item, position, it) }


        }

    }

    private sealed class ListItem {
        class HeaderItem(val line: Line) : ListItem()
        class MealItem(val meal: Meal) : ListItem()
    }

    private class ListRowHolder(val row: View?) : RecyclerView.ViewHolder(row) {
        val mealName: TextView? = row?.findViewById(R.id.list_row_meal)
        val mealDesc: TextView? = row?.findViewById(R.id.list_row_dish)
        val lineTitle: TextView? = row?.findViewById(R.id.list_header_row_title)
        val additives: TextView? = row?.findViewById(R.id.list_row_additives)
        val price: TextView? = row?.findViewById(R.id.list_row_price)
        val mealContainer: View? = row?.findViewById(R.id.list_row_meal_container)
        val headerClosedInfo: TextView? = row?.findViewById(R.id.list_header_closed_info)

        val images: Map<MealProperty, ImageView?> = mapOf(
                MealProperty.BIO to row?.findViewById(R.id.list_row_bio),
                MealProperty.PORK to row?.findViewById(R.id.list_row_pig),
                MealProperty.PORK_AW to row?.findViewById(R.id.list_row_pig_a),
                MealProperty.COW to row?.findViewById(R.id.list_row_rind),
                MealProperty.COW_AW to row?.findViewById(R.id.list_row_rind_a),
                MealProperty.FISH to row?.findViewById(R.id.list_row_fisch),
                MealProperty.MENSA_VIT to row?.findViewById(R.id.list_row_vital),
                MealProperty.VEG to row?.findViewById(R.id.list_row_vegetarian),
                MealProperty.VEGAN to row?.findViewById(R.id.list_row_vegan)
        )
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        adapter.notifyDataSetChanged()
    }

    companion object {
        const val DAY = "de.csicar.mensaplan.DayOverview.DAY"
        const val CANTEEN = "de.csicar.mensaplan.DayOverview.CANTEEN"
    }
}
