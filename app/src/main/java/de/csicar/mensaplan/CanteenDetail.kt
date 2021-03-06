package de.csicar.mensaplan

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_canteen_detail.*


class CanteenDetail : AppCompatActivity() {
    companion object {
        const val CANTEEN = "de.csicar.mensaplan.CanteenDetail.CANTEEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canteen_detail)
        setSupportActionBar(toolbar)
        val canteenName = intent.getStringExtra(CANTEEN)
        title = Canteen.getNiceNameFor(canteenName)
        fab.setOnClickListener { _ ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Canteen.getLocationUrlFor(canteenName)
            startActivity(intent)
        }
        if (Canteen.getLocationUrlFor(canteenName) == null) {
            fab.hide()
        }
        val openTimes = Canteen.getOpenTimesFor(canteenName)
        if (openTimes != null) {
            val textView = findViewById<TextView>(R.id.canteen_defail_open_times)
            textView.text = openTimes
        }
        val imageUrl = Canteen.getImageUrlFor(canteenName)
        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl)
                .into(findViewById<ImageView>(R.id.canteen_image))
        }

    }
}
