package de.csicar.mensaplan.kitcard

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.AsyncTask
import android.util.Log
import kotlin.reflect.KFunction1


/**
 * Created by csicar on 08.02.18.
 */
class ReadCardTask(private val tag: Tag, private val callback: KFunction1<@ParameterName(name = "cardInfo") Wallet?, Unit>): AsyncTask<Void, Void, Wallet?>() {
    override fun doInBackground(vararg params: Void?): Wallet? {
        val card = try {
            MifareClassic.get(tag) //TODO repair
        } catch (ex : NullPointerException) {
            Log.v("aadd", "transformTag")
            MifareClassic.get(MifareUtils.repairTag(tag))
        }
        if (card == null) {
            Log.v("aadd", "card == null")
            return null
        }
        val wallet = Wallet(card)
        val readCardResult = wallet.readCard()
        if (readCardResult != Wallet.ReadCardResult.SUCCESS) {
            return null
        }
        return wallet
    }


    override fun onPostExecute(result: Wallet?) {
        callback(result)
    }

}
