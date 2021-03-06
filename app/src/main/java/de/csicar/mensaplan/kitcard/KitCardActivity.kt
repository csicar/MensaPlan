package de.csicar.mensaplan.kitcard

import android.content.DialogInterface
import android.nfc.Tag
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import de.csicar.mensaplan.R
import kotlinx.android.synthetic.main.activity_kit_card.*

class KitCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kit_card)
        setSupportActionBar(toolbar)
        title = "Mensa Guthaben"

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
            val errorDialog = android.support.v7.app.AlertDialog.Builder(this)
            errorDialog.setTitle("Alles Kaputt!");
            errorDialog.setMessage("Karte wurde nicht lang genug an den Leser gehalten")
                    .setCancelable(false)
                    .setPositiveButton("ok", { dialog, which -> this.finish() })

            errorDialog.create()
            errorDialog.show()
            return
        }

        val kitCardBalance = findViewById<TextView>(R.id.kitcard_balance)
        val kitCardLastTransaction = findViewById<TextView>(R.id.kitcard_last_transaction)
        val kitCardAdditionalInfo = findViewById<TextView>(R.id.kitcard_additional_info)

        kitCardBalance.text = cardInfo.currentBalanceText
        kitCardLastTransaction.text = cardInfo.lastTransactionText
        kitCardAdditionalInfo.text = "${cardInfo.cardType} an ${cardInfo.cardIssuer}\n 1: ${cardInfo.transactionCount1} 2: ${cardInfo.transactionCount2}"
    }


}
