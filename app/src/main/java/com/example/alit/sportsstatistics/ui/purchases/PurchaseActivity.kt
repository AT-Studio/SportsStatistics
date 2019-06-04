package com.example.alit.sportsstatistics.ui.purchases

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.alit.sportsstatistics.R
import kotlinx.android.synthetic.main.activity_purchase.*

class PurchaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        iv_activity_purchase_back.setOnClickListener {
            finish()
        }
    }
}
