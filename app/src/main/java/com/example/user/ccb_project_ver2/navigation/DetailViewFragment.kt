package com.example.user.ccb_project_ver2.navigation

import android.os.Bundle
import android.support.design.R.id.container
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.user.ccb_project_ver2.R
import com.example.user.ccb_project_ver2.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment(){
    var firestore : FirebaseFirestore? = null
    var uid : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail,container,false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.detailviewFragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewFragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()
        //create constructor
        init{
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail,p0,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView
            //user id
            viewholder.detailviewitem_profile_textview.text =  contentDTOs!![p1].userId
            //images
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.detailviewitem_imageview_content)
            //explian
            viewholder.detailview_explain_textview.text = contentDTOs!![p1].explain
            //like
            viewholder.detailview_item_favorite_counter.text = "Like"+contentDTOs!![p1].favoriteCount

            //Buttone Event
            viewholder.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(p1)
            }

            //page reload
            if(contentDTOs[p1]!!.favorites.containsKey(uid)){
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else{
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        fun favoriteEvent(position : Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    //button Clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                }else{
                    //button not Clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                }

                transaction.set(tsDoc,contentDTO)
            }
        }
    }
}