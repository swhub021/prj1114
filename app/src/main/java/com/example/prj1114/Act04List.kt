package com.example.prj1114

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prj1114.databinding.Act4ListBinding
import com.example.prj1114.databinding.Act5CreateBinding
import kotlinx.android.synthetic.main.act4_list.*


class Act04List : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act4_list)


        // Act05Create로 이동하는 플로팅 버튼.
        listToCreateFloatingActionButton.setOnClickListener {
            val intent = Intent(this@Act04List, Act05Create::class.java)

            startActivity(intent)
        }
    }
}