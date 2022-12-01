package com.example.prj1114

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.prj1114.databinding.Act5CreateBinding
import com.example.prj1114.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import io.grpc.InternalChannelz.id
import kotlinx.android.synthetic.main.act5_create.*
import java.text.SimpleDateFormat
import java.util.*


const val TAG = "FIRESTORE"
class Act05Create : AppCompatActivity(), View.OnClickListener {

    private var binding: Act5CreateBinding? = null

    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0


    private var cal = Calendar.getInstance()

    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act5_create)

        binding = Act5CreateBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        if (!Places.isInitialized()) {
            Places.initialize(
                this@Act05Create,
                resources.getString(R.string.google_maps_api_key), Locale.KOREA
            )
        }




        timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay: Int, minute: Int ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay) // HOUR_OF_DAY과 MINUTE은 사용자가 선택한 시와 분
            cal.set(Calendar.MINUTE, minute)
            updateDateInView()
            uploadData()
        }
        chosen_time.setOnClickListener(this)
        et_location.setOnClickListener(this)
        btn_save.setOnClickListener(this)
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

            R.id.et_location -> {
                try {
                    // These are the list of fields which we required is passed
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                        Place.Field.ADDRESS
                    )
                    // Start the autocomplete intent with a unique request code.
                    val intent =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(this@Act05Create)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

                val place: Place = Autocomplete.getPlaceFromIntent(data!!)

                et_location.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }
        }

    private fun uploadData() {
        binding!!.btnSave.setOnClickListener {

            // create a dummy data
            val hashMap = hashMapOf<String, Any>(
                "TIME" to chosen_time.text.toString(),
                "DEPARTURES" to et_location.text.toString()
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

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            mLatitude = mLastLocation.latitude
            Log.e("Current Latitude", "$mLatitude")
            mLongitude = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitude")

            // TODO(Step 2: Call the AsyncTask class fot getting an address from the latitude and longitude.)
            // START
            val addressTask =
                GetAddressFromLatLng(this@Act05Create, mLatitude, mLongitude)

            addressTask.setAddressListener(object :
                GetAddressFromLatLng.AddressListener {
                override fun onAddressFound(address: String?) {
                    Log.e("Address ::", "" + address)
                    et_location.setText(address) // Address is set to the edittext
                }

                override fun onError() {
                    Log.e("Get Address ::", "Something is wrong...")
                }
            })

            addressTask.getAddress()
            // END
        }
    }

    // 사용자가 다이얼로그에서 선택한 시간이 텍스트 상자에 입력되는 되도록 함.
    private fun updateDateInView() {
        val myFormat = "h:mm a" // 입력되는 형식을 지정.
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault()) // A date format
        chosen_time.setText(sdf.format(cal.time).toString())
    }

    companion object {
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }
}