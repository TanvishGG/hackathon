package com.tanvish.vsu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import java.util.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transport_activity)
        fun secondsToLocalDateTime(seconds: Long): String {
            val milliseconds = seconds * 1000
            val instant = Instant.ofEpochMilli(milliseconds)
            val zoneId = ZoneId.systemDefault() // Replace with desired time zone
            val localDateTime = instant.atZone(zoneId).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val formattedDateTime = localDateTime.format(formatter)
            return formattedDateTime;
        }

        fun addElement(ele: JSONObject) {
            val elementContainer = findViewById<LinearLayout>(R.id.elements)
            val elementView = LayoutInflater.from(this).inflate(R.layout.element, elementContainer, false)
            elementView.findViewById<TextView>(R.id.text1).text =
                "Travel Time: ${secondsToLocalDateTime(ele.get("timestamp").toString().toLong())}"
            elementView.findViewById<TextView>(R.id.text2).text =
                "Name: ${ele.get("name").toString() + " | Mobile: " + ele.get("mobile").toString()}"
            elementView.findViewById<TextView>(R.id.text3).text = "Location: ${
                ele.get("source").toString() + " --> " + ele.get("destination").toString()
            }"
            runOnUiThread {
                elementContainer.addView(elementView);
            }
        }
        val queue = Volley.newRequestQueue(this)
        val request = JsonArrayRequest(Request.Method.GET,"https://vsu.tanvish.fun/transport", null,
            { data: JSONArray ->
                for (i in 0 until data.length()) {
                    val msg = JSONObject(data[i].toString())
                    println(data[i])
                    addElement(msg)
                }
            },
            { error ->
                println(error)
            }
        )
        queue.add(request)
        findViewById<Button>(R.id.new_post).setOnClickListener {
            setContentView(R.layout.add_element)
            findViewById<Button>(R.id.submit).setOnClickListener {
                val name = findViewById<EditText>(R.id.name).text
                val mobile = findViewById<EditText>(R.id.mobile).text
                val source = findViewById<EditText>(R.id.source).text
                val destination = findViewById<EditText>(R.id.destination).text
                val calendar = Calendar.getInstance()
                val date = findViewById<DatePicker>(R.id.date)
                val time = findViewById<TimePicker>(R.id.time)
                calendar.set(date.year, date.month, date.dayOfMonth, time.hour, time.minute)
                val timestamp = calendar.timeInMillis.toString()


                val queue = Volley.newRequestQueue(this)
                val request = JsonObjectRequest(Request.Method.POST,
                    "https://vsu.tanvish.fun/transport?name=$name&mobile=$mobile&source=$source&destination=$destination&timestamp=$timestamp", null,
                    { response: JSONObject ->
                        println(response)
                        val intent = Intent(this,MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    },
                    { error ->
                        println(error)
                    }
                )
                queue.add(request)
            }
        }

    }
}

