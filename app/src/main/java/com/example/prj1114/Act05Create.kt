package com.example.prj1114

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.prj1114.databinding.Act5CreateBinding
import com.example.prj1114.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.act5_create.*
import java.text.SimpleDateFormat
import java.util.*


const val TAG = "FIRESTORE"
class Act05Create : AppCompatActivity(), View.OnClickListener {

    private var binding: Act5CreateBinding? = null


    private var cal = Calendar.getInstance()

    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act5_create)

        binding = Act5CreateBinding.inflate(layoutInflater)
        setContentView(binding?.root)




        timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay) // HOUR_OF_DAY과 MINUTE은 사용자가 선택한 시와 분
            cal.set(Calendar.MINUTE, minute)
            updateDateInView()
            uploadData()
        }
        chosen_time.setOnClickListener(this)
    }

    // 전체 코드를 구현하는 곳.
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.chosen_time -> { // 사용자가 chosen_time을 누르면 아래 다이얼로그가 뜨도록함.
                TimePickerDialog(
                    this@Act05Create, timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), false
                ).show()
            }
        }
    }

    private fun uploadData() {
        binding!!.btnSave.setOnClickListener {

            // create a dummy data
            val hashMap = hashMapOf<String, Any>(
                "TIME" to chosen_time.text.toString()
            )

            // use the add() method to create a document inside users collection
            FirebaseUtils().fireStoreDatabase.collection("users")
                .add(hashMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Added document with ID ${it.id}")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error adding document $exception")
                }
        }
    }

    // 사용자가 다이얼로그에서 선택한 시간이 텍스트 상자에 입력되는 되도록 함.
    private fun updateDateInView() {
        val myFormat = "h:mm a" // 입력되는 형식을 지정.
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault()) // A date format
        chosen_time.setText(sdf.format(cal.time).toString())
    }

}