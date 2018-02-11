package de.csicar.mensaplan.kitcard

import android.nfc.Tag
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import de.csicar.mensaplan.R
import kotlinx.android.synthetic.main.activity_kit_card.*

class KitCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kit_card)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, packageManager.hasSystemFeature("com.nxp.mifare").toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        if ("android.nfc.action.TECH_DISCOVERED" == intent.action) {
            val tag = intent.getParcelableExtra("android.nfc.extra.TAG") as Tag
            ReadCardTask(tag, this::onTagInfo).execute()
        }
    }

    private fun onTagInfo(cardInfo: Wallet?) {
        if (cardInfo == null) {
            return
        }
        val contextTextView = findViewById<TextView>(R.id.kitcard_content)
        contextTextView.text = """Current: ${cardInfo.currentBalanceText}
             last: ${cardInfo.lastTransactionText}
             of id: ${cardInfo.cardNumber}
             by issuer ${cardInfo.cardIssuer}
             with role ${cardInfo.cardType}
             transaction count1 ${cardInfo.transactionCount1}
             transaction count2 ${cardInfo.transactionCount2}"""
    }


}
