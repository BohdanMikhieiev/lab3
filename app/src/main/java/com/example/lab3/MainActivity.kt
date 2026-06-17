package com.example.lab3

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class DbHelper(context: Context) : SQLiteOpenHelper(context, "passwords.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE passwords (id INTEGER PRIMARY KEY AUTOINCREMENT, value TEXT, saved_at TEXT)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS passwords")
        onCreate(db)
    }
}

class MainActivity : AppCompatActivity() {

    val dbHelper by lazy { DbHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, InputFragment(), "INPUT_FRAGMENT")
                .commit()
        }
    }

    fun onPasswordSubmit(password: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("value", password)
            put("saved_at", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()))
        }
        val result = db.insert("passwords", null, values)
        val message = if (result != -1L) "Збережено успішно" else "Помилка запису"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ResultFragment.newInstance(password))
            .addToBackStack(null)
            .commit()
    }

    fun onCancel() {
        supportFragmentManager.popBackStack()
        val f = supportFragmentManager.findFragmentByTag("INPUT_FRAGMENT") as? InputFragment
        f?.clearInput()
    }

    fun openHistory() {
        startActivity(Intent(this, HistoryActivity::class.java))
    }
}

class InputFragment : Fragment() {

    private lateinit var etPassword: EditText
    private lateinit var rgMode: RadioGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etPassword = view.findViewById(R.id.etPassword)
        rgMode = view.findViewById(R.id.rgMode)

        rgMode.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbShow) {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            etPassword.setSelection(etPassword.text.length)
        }

        view.findViewById<Button>(R.id.btnOk).setOnClickListener {
            (activity as? MainActivity)?.onPasswordSubmit(etPassword.text.toString())
        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            (activity as? MainActivity)?.openHistory()
        }
    }

    fun clearInput() {
        etPassword.text.clear()
        rgMode.check(R.id.rbHide)
    }
}

class ResultFragment : Fragment() {

    companion object {
        fun newInstance(password: String) = ResultFragment().apply {
            arguments = Bundle().apply { putString("password", password) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvResult).text =
            "Введений пароль: ${arguments?.getString("password") ?: ""}"

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            (activity as? MainActivity)?.onCancel()
        }
    }
}