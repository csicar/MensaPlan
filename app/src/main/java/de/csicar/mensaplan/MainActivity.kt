package de.csicar.mensaplan

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateUtils
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import com.android.volley.Response
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val CANTEEN = "de.csicar.mensaplan.CANTEEN"
    val NAVBAR_ID = "de.csicar.mensaplan.NAVBAR_ID"
    var selectedCanteenName : String = "adenauerring"

    private lateinit var pagerAdapter : DayPagesAdapter

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        tryExtractPathFromIndent()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // select the canteen that should be shown
        // lowest
        selectedCanteenName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("selected_canteen", "adenauerring")
        selectedCanteenName = savedInstanceState?.getString(CANTEEN) ?: selectedCanteenName
        selectedCanteenName = tryExtractPathFromIndent() ?: selectedCanteenName
        selectedCanteenName = intent.getStringExtra(CANTEEN) ?: selectedCanteenName


        title = Canteen.getNiceNameFor(selectedCanteenName)
        setSupportActionBar(toolbar)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.day_overview_swipe_refresher)
        swipeRefreshLayout.setOnRefreshListener {
            refreshFromBackend()
        }
        val pager = findViewById<ViewPager>(R.id.day_overview_pager)

        // Fix for: When swiping left to right: the refresh action get's triggered, making it impossible to
        // switch pager panes
        pager.setOnTouchListener { _, event ->
            swipeRefreshLayout.isEnabled = false
            when (event.action) {
                MotionEvent.ACTION_UP -> swipeRefreshLayout.isEnabled = true
            }
            false
        }
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(navigationIdCanteenNameMapping.entries.find { it.value == selectedCanteenName }?.key ?: R.id.canteen_adenauerring)


        pagerAdapter = DayPagesAdapter(selectedCanteenName, supportFragmentManager)

        var isInitialLoad = true

        val initialLoaderProgressbar = findViewById<ProgressBar>(R.id.emptyLoadingProgressBar)
        initialLoaderProgressbar.visibility = View.VISIBLE
        refreshFromBackend()
        BackendApi.onUpdate(Response.Listener {
            pagerAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
            if (isInitialLoad) {
                selectTodaysPane(pagerAdapter, selectedCanteenName)
                initialLoaderProgressbar.visibility = View.GONE
                isInitialLoad = false
            }
        })

    }

    fun tryExtractPathFromIndent() : String? {
        val fullPath = intent.data?.encodedSchemeSpecificPart ?: return null
        Log.v("asd", ""+fullPath)
        val url = Uri.parse(fullPath)

        val line = url.getQueryParameter("line")
        val canteen = url.getQueryParameter("canteen")

        Log.v("asd", "line: "+line)

        return canteen

    }

    override fun onResume() {
        super.onResume()

        selectTodaysPane(pagerAdapter, selectedCanteenName)
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        outState?.putString(CANTEEN, selectedCanteenName)
        super.onSaveInstanceState(outState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
        selectedCanteenName = savedInstanceState.getString(CANTEEN)
    }


    /**
     * selects the pane for today or if for today there is no pane available shows the next one
     * in the future
     */
    private fun selectTodaysPane(pagerAdapter: PagerAdapter, selectedCanteenName: String) {
        findViewById<ViewPager>(R.id.day_overview_pager).apply {
            adapter = pagerAdapter

            val canteen = BackendApi.canteens.find {
                it.name == selectedCanteenName
            }
            val now = Date()
            currentItem = canteen?.days?.withIndex()
                    // remove elements before today
                    ?.filter { it.value.date.let { it.after(now) || DateUtils.isToday(it.time) }}
                    // select the element that is the closest to now
                    ?.minBy { it.value.date.time-now.time }
                    // get it's index
                    ?.index
                ?: 0
        }
    }

    private fun refreshFromBackend() {
        BackendApi.refresh(this, Response.ErrorListener {
            showNetworkProblem(findViewById(R.id.day_overview_swipe_refresher), it)
        })
    }

    private fun showNetworkProblem(view: View, error: VolleyError) {
        Snackbar.make(view, "Could not connect to server:" + error.networkResponse?.statusCode, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    private class DayPagesAdapter(val canteenName: String, fm: android.support.v4.app.FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val fragment = DayOverview()
            fragment.arguments = Bundle().apply {
                putInt(DayOverview.DAY, position)
                putString(DayOverview.CANTEEN, canteenName)
            }
            return fragment
        }

        override fun getCount(): Int {
            return BackendApi.canteens.find { it.name == canteenName }?.days?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence {
            return BackendApi.canteens.find { it.name == canteenName }?.days?.get(position)?.getFormattedDate() ?: ""
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_canteen_info -> {
                val intent = Intent(this, CanteenDetail::class.java)
                intent.putExtra(CanteenDetail.CANTEEN, selectedCanteenName)

                val options = ActivityOptions.makeSceneTransitionAnimation(this,
                        Pair.create(toolbar!!, "appbar")
                )

                startActivity(intent, options.toBundle())
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(CANTEEN, navigationIdCanteenNameMapping.get(item.itemId))
            putExtra(NAVBAR_ID, item.itemId)
        }
        startActivity(intent)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


}
