package com.example.lab3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val dbHelper = DbHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT value, saved_at FROM passwords ORDER BY id DESC", null)

        val lvHistory = findViewById<ListView>(R.id.lvHistory)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)

        if (cursor.count == 0) {
            tvEmpty.visibility = android.view.View.VISIBLE
            lvHistory.visibility = android.view.View.GONE
        } else {
            tvEmpty.visibility = android.view.View.GONE
            lvHistory.visibility = android.view.View.VISIBLE
            val items = mutableListOf<String>()
            while (cursor.moveToNext()) {
                val value = cursor.getString(0)
                val savedAt = cursor.getString(1)
                items.add("$value\n$savedAt")
            }
            lvHistory.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        }
        cursor.close()
    }
}