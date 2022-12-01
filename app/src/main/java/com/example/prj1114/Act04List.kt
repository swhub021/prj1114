package com.example.prj1114

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prj1114.databinding.Act4ListBinding
import com.example.prj1114.databinding.Act5CreateBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.View
import kotlinx.android.synthetic.main.act4_list.*
import kotlinx.android.synthetic.main.item_group.view.*


class Act04List : AppCompatActivity() {

    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act4_list)

        firestore = FirebaseFirestore.getInstance()

        groups_recyclerview.adapter = RecyclerViewAdapter()
        groups_recyclerview.layoutManager = LinearLayoutManager(this)


        // Act05Create로 이동하는 플로팅 버튼.
        listToCreateFloatingActionButton.setOnClickListener {
            val intent = Intent(this@Act04List, Act05Create::class.java)

            startActivity(intent)
        }
    }
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var createdGroup : ArrayList<EachGroup> = arrayListOf()

        init {
            firestore?.collection("createdGroup")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                createdGroup.clear()

                for (snapshot in querySnapshot!!.documents) {
                    var item_group = snapshot.toObject(EachGroup::class.java)
                    createdGroup.add(item_group!!)
                }
                notifyDataSetChanged()
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        }

        // onCreateViewHolder에서 만든 view와 실제 데이터를 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView

            viewHolder.TIME.text = createdGroup[position].TIME
            viewHolder.DEPARTURES.text = createdGroup[position].DEPARTURES
            viewHolder.ARRIVALS.text = createdGroup[position].ARRIVALS
        }

        // 리사이클러뷰의 아이템 총 개수 반환
        override fun getItemCount(): Int {
            return createdGroup.size
        }
    }
}
