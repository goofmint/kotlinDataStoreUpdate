package jp.moongift.ncmbupdatedatastore

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import jp.moongift.ncmbupdatedatastore.BuildConfig
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.nifcloud.mbaas.core.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NCMB.initialize(applicationContext, BuildConfig.APPLICATION_KEY, BuildConfig.CLIENT_KEY)

        var user = NCMBUser.getCurrentUser()
        if (user == null || user.objectId == null) {
            NCMBUser.loginWithAnonymousInBackground {user, e ->
                if (e != null) {
                    Log.d("[ERROR]", e.toString())
                } else {
                    Log.d("[DEBUG]", user.toString())
                }
            }
        } else {
            Log.d("[DEBUG]", user.objectId)
        }

        val query = NCMBQuery<NCMBObject>("Memo")
        var txtMemo : TextView = findViewById(R.id.txtMemo)
        var objMemo = NCMBObject("Memo")
        query.findInBackground {objects, error ->
            if (error != null) {
                Log.d("[Error]", error.toString())
            } else {
                objMemo = objects[0]
                if (objects.size == 0) {
                    txtMemo.text = objects[0].getString("Memo")
                } else {
                    txtMemo.text =  "メモを書きます"
                }
            }
        }
        var btnSave : Button = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            objMemo.put("Memo", txtMemo.text.toString())
            var acl = NCMBAcl()
            acl.setReadAccess(NCMBUser.getCurrentUser().objectId, true)
            acl.setWriteAccess(NCMBUser.getCurrentUser().objectId, true)
            objMemo.acl = acl
            objMemo.saveInBackground { e ->
                if (e != null) {
                    Log.d("[Error]", e.toString())
                }
            }
        }
    }
}
