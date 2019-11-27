package com.example.topup

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.app.PendingIntent
import android.nfc.Tag
import android.os.Build
import android.text.Html
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Spanned
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.example.paymenttopup.DBhelper.DBhelper
import com.example.paymenttopup.Model.Topup
import android.widget.Toast
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    internal var helper = DBhelper(this)
    internal var showLog:List<Topup> = ArrayList<Topup>()
    private var nfcAdapter : NfcAdapter? = null
    private var topupCard: Topup? = null


    // Pending intent for NFC intent foreground dispatch.
    // Used to read all NDEF tags while the app is running in the foreground.
    private var nfcPendingIntent: PendingIntent? = null
    // Optional: filter NDEF tags this app receives through the pending intent.
    //private var nfcIntentFilters: Array<IntentFilter>? = null

    private val KEY_LOG_TEXT = "logText"

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)

    override fun onCreate(savedInstanceState: Bundle?) {
//        helper.deleteAll()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helper = DBhelper(this)
//        refreshData()
        // Restore saved text if available
        if (savedInstanceState != null) {
            tv_messages.text = savedInstanceState.getCharSequence(KEY_LOG_TEXT)
        }

        // Check if NFC is supported and enabled
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        logMessage("NFC supported", (nfcAdapter != null).toString())
        logMessage("NFC enabled", (nfcAdapter?.isEnabled).toString())
        var textChange = findViewById(R.id.txt_money) as EditText;
//        var showID = findViewById(R.id.show_id) as TextView;
//        var showMoney = findViewById(R.id.show_money) as TextView;
//        showID.setText(topupCard!!.id)
//        showMoney.setText(topupCard!!.money)
//        btn_getdata.setOnClickListener {
//            if(topupCard == null){
//                val toast = Toast.makeText(this,"กรุณาแตะบัตร",Toast.LENGTH_LONG)
//                toast.show()
//            }else{
//                val toast = Toast.makeText(this,"ID : " + topupCard!!.id + "\nยอดเงินคงเหลือ : " + topupCard!!.money,Toast.LENGTH_LONG)
//                toast.show()
//                textChange.text.clear()
//            }
//
//        }
        btn_addmoney.setOnClickListener{
            val mAlertDialog = AlertDialog.Builder(this@MainActivity)
            if(topupCard == null){
                val toast = Toast.makeText(this,"กรุณาแตะบัตร",Toast.LENGTH_LONG)
                toast.show()
            }else{
                var money = txt_money.text.toString().toIntOrNull()
                var moneyIncard = topupCard!!.money
                if(money == null){
                    val toast = Toast.makeText(this,"กรุณาใส่จำนวนเงิน",Toast.LENGTH_LONG)
                    toast.show()
                }else{
                    mAlertDialog.setTitle("ID :"+ topupCard!!.id)
                    mAlertDialog.setMessage("ต้องการเติมเงินจำนวน :"+ money +" ?")
                    mAlertDialog.setIcon(R.mipmap.ic_launcher)
                    mAlertDialog.setPositiveButton("YES"){dialog, id ->
                        topupCard!!.money = moneyIncard + money
                        helper.updateMoney(topupCard!!)
                        val toast = Toast.makeText(this, "ID :"+ topupCard!!.id + "\nเติมเงินจำนวน : "+money+" เรียบร้อย" + "\nยอดเงินคงเหลือ : "+topupCard!!.money, Toast.LENGTH_LONG)
                        toast.show()
                        textChange.text.clear()
                        var showID = findViewById(R.id.show_id) as TextView;
                        var showMoney = findViewById(R.id.show_money) as TextView;
                        showID.setText(topupCard!!.id)
                        showMoney.setText(topupCard!!.money.toString()+" ฿")
                        logMessage("ID ",topupCard!!.id)
                        logMessage("ทำการเติมเงิน ", money.toString())
                        logMessage("ยอดเงินคงเหลือ ",topupCard!!.money.toString())
                    }
                    mAlertDialog.setNegativeButton("NO"){dialog , id ->
                        dialog.dismiss()
                    }
                    mAlertDialog.show()
                }
            }
        }
        btn_deletemoney.setOnClickListener{
            val mAlertDialog = AlertDialog.Builder(this@MainActivity)
            if(topupCard == null){
                val toast = Toast.makeText(this,"กรุณาแตะบัตร",Toast.LENGTH_LONG)
                toast.show()
            }else{
                var money = txt_money.text.toString().toIntOrNull()
                var moneyIncard = topupCard!!.money
                if(money == null){
                    val toast = Toast.makeText(this,"กรุณาใส่จำนวนเงิน",Toast.LENGTH_LONG)
                    toast.show()
                }else{
                    if(money > moneyIncard){
                        val toast = Toast.makeText(this,"ยอดเงินคงเหลือไม่เพียงพอ",Toast.LENGTH_LONG)
                        toast.show()
                    }else{
                        mAlertDialog.setTitle("ID :"+ topupCard!!.id)
                        mAlertDialog.setMessage("ต้องการเชำระเงินจำนวน :"+ money +" ?")
                        mAlertDialog.setIcon(R.mipmap.ic_launcher)
                        mAlertDialog.setPositiveButton("YES") { dialog, id ->
                            topupCard!!.money = moneyIncard - money
                            helper.updateMoney(topupCard!!)
                            var showID = findViewById(R.id.show_id) as TextView;
                            var showMoney = findViewById(R.id.show_money) as TextView;
                            showID.setText(topupCard!!.id)
                            showMoney.setText(topupCard!!.money.toString())
                            val toast = Toast.makeText(this, "ID :"+ topupCard!!.id + "\nชำระเงินจำนวน : "+money+" เรียบร้อย" + "\nยอดเงินคงเหลือ : " +topupCard!!.money, Toast.LENGTH_LONG)
                            toast.show()
                            textChange.text.clear()
                            logMessage("ID ",topupCard!!.id)
                            logMessage("ทำการชำระเงิน ", money.toString())
                            logMessage("ยอดเงินคงเหลือ ",topupCard!!.money.toString()+" ฿")

                        }
                        mAlertDialog.setNegativeButton("NO"){dialog , id ->
                            dialog.dismiss()
                        }
                        mAlertDialog.show()
                    }
                }
            }
        }
        btn_cleardata.setOnClickListener{
            val mAlertDialog = AlertDialog.Builder(this@MainActivity)
            val toast = Toast.makeText(this, "ลบข้อมูลทั้งหมดเรียบร้อยแล้ว", Toast.LENGTH_LONG)
                        mAlertDialog.setTitle("ต้องการรีเซ็ตข้อมูล ?")
                        mAlertDialog.setIcon(R.mipmap.ic_launcher)
                        mAlertDialog.setPositiveButton("YES") { dialog, id ->
                            helper.deleteAll()
                            toast.show()
                            var showID = findViewById(R.id.show_id) as TextView;
                            var showMoney = findViewById(R.id.show_money) as TextView;
                            showID.setText("")
                            showMoney.setText("")
                            logMessage("รีเซ็ตข้อมูลทั้งหมดเรียบร้อย!!","")
                        }
                        mAlertDialog.setNegativeButton("NO"){dialog , id ->
                            dialog.dismiss()
                        }
                        mAlertDialog.show()
        }

        // Read all tags when app is running and in the foreground
        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        // Optional: Setup an intent filter from code for a specific NDEF intent
        // Use this code if you are only interested in a specific intent and don't want to
        // interfere with other NFC tags.
        // In this example, the code is commented out so that we get all NDEF messages,
        // in order to analyze different NDEF-formatted NFC tag contents.
        //val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        //ndef.addCategory(Intent.CATEGORY_DEFAULT)
        //ndef.addDataScheme("https")
        //ndef.addDataAuthority("*.andreasjakl.com", null)
        //ndef.addDataPath("/", PatternMatcher.PATTERN_PREFIX)
        // More information: https://stackoverflow.com/questions/30642465/nfc-tag-is-not-discovered-for-action-ndef-discovered-action-even-if-it-contains
        //nfcIntentFilters = arrayOf(ndef)

        if (intent != null) {
            // Check if the app was started via an NDEF intent
//            logMessage("Found intent in onCreate", intent.action.toString())
            processIntent(intent)
        }

        // Make sure the text view is scrolled down so that the latest messages are visible
        scrollDown()
    }

//    private fun refreshData(){
//        showLog = helper.TopupLog
//        val adapter = LogAdapter(this@MainActivity,showLog, edt_id, edt_money)
//        list_topup.adapter = adapter
//    }

    override fun onResume() {
        super.onResume()
        // Get all NDEF discovered intents
        // Makes sure the app gets all discovered NDEF messages as long as it's in the foreground.
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null);
        // Alternative: only get specific HTTP NDEF intent
        //nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilters, null);
    }

    override fun onPause() {
        super.onPause()
        // Disable foreground dispatch, as this activity is no longer in the foreground
        nfcAdapter?.disableForegroundDispatch(this);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        logMessage("Found intent in onNewIntent", intent?.action.toString())
        // If we got an intent while the app is running, also check if it's a new NDEF message
        // that was discovered
        if (intent != null) processIntent(intent)
    }

    /**
     * Check if the Intent has the action "ACTION_NDEF_DISCOVERED". If yes, handle it
     * accordingly and parse the NDEF messages.
     * @param checkIntent the intent to parse and handle if it's the right type
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun processIntent(checkIntent: Intent) {
        // Check if intent has the action of a discovered NFC tag
        // with NDEF formatted contents
        if (checkIntent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            logMessage("New NDEF intent", checkIntent.toString())

            // Retrieve the raw NDEF message from the tag
            val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            logMessage("Raw messages", rawMessages.size.toString())

            // Complete variant: parse NDEF messages
            if (rawMessages != null) {
                val messages = arrayOfNulls<NdefMessage?>(rawMessages.size)// Array<NdefMessage>(rawMessages.size, {})
                for (i in rawMessages.indices) {
                    messages[i] = rawMessages[i] as NdefMessage;
                }
                // Process the messages array.
                processNdefMessages(messages)
            }

            // Simple variant: assume we have 1x URI record
            //if (rawMessages != null && rawMessages.isNotEmpty()) {
            //    val ndefMsg = rawMessages[0] as NdefMessage
            //    if (ndefMsg.records != null && ndefMsg.records.isNotEmpty()) {
            //        val ndefRecord = ndefMsg.records[0]
            //        if (ndefRecord.toUri() != null) {
            //            logMessage("URI detected", ndefRecord.toUri().toString())
            //        } else {
            //            // Other NFC Tags
            //            logMessage("Payload", ndefRecord.payload.contentToString())
            //        }
            //    }
            //}

        }else if (checkIntent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = checkIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag
            val techList = tag.techList
            var ID = ByteArray(20)
            var data = tag.toString()
            ID = tag.id
            val uid = Utils.bytesToHexString(ID)!!
            val toast = Toast.makeText(this, "$uid", Toast.LENGTH_LONG)
            toast.show()
            val listCardID = helper.checkID(uid)

            if(listCardID.isEmpty()){
                topupCard =  helper.addUser(uid)
            }else{
                topupCard = listCardID.get(0)
            }
            var dataFormat = ""
            for (tech in techList) {
                dataFormat += "\n" + tech
            }
//            logMessage("UID", uid)
//            logMessage("dataFormat", dataFormat)
//            logMessage("tag", data)
            var showID = findViewById(R.id.show_id) as TextView;
            var showMoney = findViewById(R.id.show_money) as TextView;
            showID.setText(topupCard!!.id)
            showMoney.setText(topupCard!!.money.toString()+" ฿")
        }
    }class MainActivity


    /**
     * Parse the NDEF message contents and print these to the on-screen log.
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun processNdefMessages(ndefMessages: Array<NdefMessage?>) {
        // Go through all NDEF messages found on the NFC tag
        for (curMsg in ndefMessages) {
            if (curMsg != null) {
                // Print generic information about the NDEF message
                logMessage("Message", curMsg.toString())
                // The NDEF message usually contains 1+ records - print the number of recoreds
                logMessage("Records", curMsg.records.size.toString())

                // Loop through all the records contained in the message
                for (curRecord in curMsg.records) {
                    if (curRecord.toUri() != null) {
                        // URI NDEF Tag
                        logMessage("- URI", curRecord.toUri().toString())
                    } else {
                        // Other NDEF Tags - simply print the payload
                        logMessage("- Contents", curRecord.payload.contentToString())
                    }
                }
            }
        }
    }

    // --------------------------------------------------------------------------------
    // Utility functions

    /**
     * Save contents of the text view to the state. Ensures the text view contents survive
     * screen rotation.
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putCharSequence(KEY_LOG_TEXT, tv_messages.text)
        super.onSaveInstanceState(outState)
    }

    /**
     * LogAdapter a message to the debug text view.
     * @param header title text of the message, printed in bold
     * @param text optional parameter containing details about the message. Printed in plain text.
     */
    private fun logMessage(header: String, text: String?) {
        tv_messages.append(if (text.isNullOrBlank()) fromHtml("<b>$header</b><br>") else fromHtml("<b>$header</b>: $text<br>"))
        scrollDown()
    }

    /**
     * Convert HTML formatted strings to spanned (styled) text, for inserting to the TextView.
     * Externalized into an own function as the fromHtml(html) method was deprecated with
     * Android N. This method chooses the right variant depending on the OS.
     * @param html HTML-formatted string to convert to a Spanned text.
     */
    private fun fromHtml(html: String): Spanned {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
    }

    /**
     * Scroll the ScrollView to the bottom, so that the latest appended messages are visible.
     */
    private fun scrollDown() {
        sv_messages.post({ sv_messages.smoothScrollTo(0, sv_messages.bottom) })
    }
}
