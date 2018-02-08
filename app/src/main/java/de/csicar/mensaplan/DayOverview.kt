package de.csicar.mensaplan

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.Response
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DayOverview.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DayOverview.newInstance] factory method to
 * create an instance of this fragment.
 */
class DayOverview : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_day_overview, container, false)

        val context = container?.context

        // Inflate the layout for this fragment
        val items = mutableListOf<ListItem>()


        val recyclerView = view.findViewById<RecyclerView>(R.id.day_overview_listview)
        recyclerView.layoutManager = LinearLayoutManager(context)


        val adapter = ListExampleAdapter(items)

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

    private fun updateItems(canteens: List<Canteen>, dayIndex: Int, canteenName: String, items: MutableList<ListItem>, adapter: ListExampleAdapter) {
        val canteenValue = canteens.find { it.name == canteenName }
        if (canteenValue == null) {
            adapter.notifyDataSetChanged()
            return
        }
        items.clear()
        canteenValue.days[dayIndex].lines.forEach {
            items.add(ListItem.HeaderItem(it))
            it.meals.forEach {
                items.add(ListItem.MealItem(it))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private class ListExampleAdapter(val sList: List<ListItem>) : RecyclerView.Adapter<ListRowHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListRowHolder {
            val context = parent?.context
            val rowView = when(viewType) {
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
            return when(sList[position]) {
                is DayOverview.ListItem.HeaderItem -> 0
                is DayOverview.ListItem.MealItem -> 1
            }
        }

        override fun onBindViewHolder(holder: ListRowHolder?, position: Int) {
            val item = sList[position]
            when(item) {
                is ListItem.HeaderItem -> {
                    holder?.lineTitle?.text = item.line.getNiceName()
                }
                is ListItem.MealItem -> {
                    val context = holder?.row!!.context
                    holder.mealName?.text = item.meal.meal
                    holder.mealDesc?.text = item.meal.dish
                    holder.additives?.text = item.meal.additives.joinToString(", ")
                    holder.price?.text = item.meal.price.showPrice(holder.price!!.context)
                    holder.mealContainer?.alpha = when (item.meal.containsBadAdditives(context)) {
                        true -> 0.2f
                        else -> 1.0f
                    }
                    holder.additives?.visibility = when(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_additives", true)) {
                        true -> View.VISIBLE
                        else -> View.GONE
                    }
                }
            }

        }



    }

    private sealed class ListItem {
        class HeaderItem(val line: Line) : ListItem()

        class MealItem(val meal: Meal) : ListItem()
    }


    private class ListRowHolder(val row: View?) : RecyclerView.ViewHolder(row) {
        val mealName: TextView? = row?.findViewById(R.id.list_row_meal)
        val mealDesc: TextView?  = row?.findViewById(R.id.list_row_dish)
        val lineTitle: TextView? = row?.findViewById(R.id.list_header_row_title)
        val additives: TextView? = row?.findViewById(R.id.list_row_additives)
        val price: TextView? = row?.findViewById(R.id.list_row_price)
        val mealContainer : View?  = row?.findViewById(R.id.list_row_meal_container)

    }

    companion object {
        val DAY = "de.csicar.mensaplan.DayOverview.DAY"
        val CANTEEN = "de.csicar.mensaplan.DayOverview.CANTEEN"
    }
}// Required empty public constructor
