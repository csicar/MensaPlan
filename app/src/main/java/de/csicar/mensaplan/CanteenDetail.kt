package de.csicar.mensaplan

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_canteen_detail.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.squareup.picasso.Callback


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
            val image = ImageView(this)
            Picasso.with(this).load(imageUrl)
                .into(image, object : Callback {
                    override fun onSuccess() {

                        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).setBackgroundDrawable(image.drawable);
                    }

                    override fun onError() {

                    }

                })
        }

    }
}
