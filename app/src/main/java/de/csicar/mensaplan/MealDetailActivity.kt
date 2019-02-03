package de.csicar.mensaplan

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Response
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.csicar.mensaplan.imageapi.GoogleImageApi
import kotlinx.android.synthetic.main.activity_meal_detail.*
import java.net.URLEncoder

class MealDetailActivity : AppCompatActivity() {
    companion object {
        const val MEALDATA = "de.csicar.mensaplan.MEALDATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_detail)
        setSupportActionBar(toolbar)
        val mealJson = intent.getStringExtra(MEALDATA)
        val meal = Gson().fromJson(mealJson, Meal::class.java)
        title = meal.meal
        fab.setOnClickListener { view ->
            searchWeb(meal.meal + " " + meal.dish)
        }

        fetchImage(meal)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        getMealPropertyImages().forEach {
            it.value.visibility =
                    if (meal.properties.contains(it.key))
                        View.VISIBLE
                    else View.GONE

        }

        findViewById<TextView>(R.id.dishTextView)?.text = meal.dish
        findViewById<TextView>(R.id.additives_text)?.text = meal.longAdditiveNames(this).joinToString { "${it.first} (${it.second}) " }
        findViewById<TextView>(R.id.price_text)?.text = meal.price.showFlag() + (1..4).map { Price.priceGroupName(this, it) + " " + meal.price.showPrice(it) }.joinToString { "$it " }
    }

    private fun fetchImage(meal: Meal) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val googleCx = pref.getString("google_search_cx", "")
        val googleKey = pref.getString("google_search_key", "")

        if (googleCx == "" || googleKey == "") return

        GoogleImageApi(googleCx, googleKey).fetch(this, meal.meal, Response.Listener {
            Picasso.with(this).load(it.toExternalForm())
                    .into(findViewById<ImageView>(R.id.mealImage))
        }, Response.ErrorListener {
            Snackbar.make(findViewById(R.id.toolbar), it.toString(), 1000)
            Log.v("error", it.toString())
        })
    }

    private fun getMealPropertyImages(): Map<MealProperty, ImageView> {
        return mapOf(
                MealProperty.BIO to this.findViewById(R.id.list_row_bio),
                MealProperty.PORK to this.findViewById(R.id.list_row_pig),
                MealProperty.PORK_AW to this.findViewById(R.id.list_row_pig_a),
                MealProperty.COW to this.findViewById(R.id.list_row_rind),
                MealProperty.COW_AW to this.findViewById(R.id.list_row_rind_a),
                MealProperty.FISH to this.findViewById(R.id.list_row_fisch),
                MealProperty.MENSA_VIT to this.findViewById(R.id.list_row_vital),
                MealProperty.VEG to this.findViewById(R.id.list_row_vegetarian),
                MealProperty.VEGAN to this.findViewById(R.id.list_row_vegan)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun searchWeb(query: String) {
        val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
        }

        if (searchIntent.resolveActivity(this.packageManager) != null) {
            startActivity(searchIntent)
        } else {
            val escapedQuery = URLEncoder.encode(query, "UTF-8")
            val uri = Uri.parse("http://www.google.com/search?q=$escapedQuery")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

}
