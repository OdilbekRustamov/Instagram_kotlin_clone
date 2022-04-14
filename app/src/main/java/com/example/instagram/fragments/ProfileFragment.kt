package com.example.instagram.fragments

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.adapter.ProfileAdapter
import com.example.instagram.manager.AuthManager
import com.example.instagram.manager.DatabaseManager
import com.example.instagram.manager.StorageManager
import com.example.instagram.manager.handler.DBUserHandler
import com.example.instagram.manager.handler.StorageHandler
import com.example.instagram.model.Post
import com.example.instagram.model.User
import com.example.instagram.utils.Logger
import com.google.android.material.imageview.ShapeableImageView
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

/**
 * In ProfileFragment, posts that user uploadwd can be seen and user is ab;e to change his/her profile photo.
 */

class ProfileFragment : Fragment() {
    val TAG = ProfileFragment::class.java.simpleName
    lateinit var rv_profile: RecyclerView
    lateinit var ic_logout: ImageView
    lateinit var iv_profile: ImageView
    lateinit var tv_fullname: TextView
    lateinit var tv_email: TextView

    var pickedPhoto: Uri? = null
    var allPhotos = ArrayList<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initViews(view)
        return view
    }

    fun initViews(view: View) {
        tv_fullname = view.findViewById(R.id.tv_fullname)
        tv_email = view.findViewById(R.id.tv_email)

        rv_profile = view.findViewById(R.id.rv_profile)
        rv_profile.layoutManager = GridLayoutManager(activity, 2)

        loadUserInfo()

        iv_profile = view.findViewById<ShapeableImageView>(R.id.iv_profile)
        iv_profile.setOnClickListener {
            pickFishBunPhoto()
        }

        refreshAdapter(loadPosts())

        ic_logout = view.findViewById(R.id.ic_logout)
        ic_logout.setOnClickListener {
            AuthManager.signOut()
            activity?.finish()
        }
    }

    /**
     * Pick photo using FishBun library
     */
    private fun pickFishBunPhoto(){
        FishBun.with(this)
            .setImageAdapter(GlideAdapter())
            .setMaxCount(1)
            .setMinCount(1)
            .setSelectedImages(allPhotos)
            .startAlbumWithActivityResultCallback(photoLauncher)
    }

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK){
                allPhotos =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf()
                pickedPhoto = allPhotos.get(0)
                uploadPickedPhoto()
            }
        }

    private fun uploadPickedPhoto(){
        StorageManager.uploadUserPhoto(pickedPhoto!!, object : StorageHandler{
            override fun onSuccess(userImg: String) {
                DatabaseManager.updateUserImage(userImg)
                iv_profile.setImageURI(pickedPhoto)
            }

            override fun onError(e: Exception) {

            }

        })
    }

    private fun loadUserInfo(){
        DatabaseManager.loadUser(AuthManager.currentUser()!!.uid, object : DBUserHandler{
            override fun onSuccess(user: User?) {
                if (user != null){
                    showUserInfo(user)
                }
            }

            override fun onError(e: Exception) {

            }
        })
    }

    private fun showUserInfo(user: User) {
        tv_fullname.text = user.fullname
        tv_email.text = user.email
        Glide.with(this).load(user.userImg)
            .placeholder(R.drawable.iv_percon)
            .error(R.drawable.iv_percon)
            .into(iv_profile)
    }

    private fun refreshAdapter(items: ArrayList<Post>){
        val adapter = ProfileAdapter(this, items)
        rv_profile.adapter = adapter
    }

    private fun loadPosts(): ArrayList<Post>{
        val posts = ArrayList<Post>()
        posts.add(Post("https://images.unsplash.com/photo-1648737155328-0c0012cf2f20?ixlib=rb-1.2.1&ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"))
        posts.add(Post("https://images.unsplash.com/photo-1474293507615-951863a0f942?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1169&q=80"))
        posts.add(Post("https://images.unsplash.com/photo-1511895426328-dc8714191300?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80"))
        posts.add(Post("https://images.unsplash.com/photo-1552664730-d307ca884978?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8cGVvcGxlJTIwYXQlMjB3b3JrfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        posts.add(Post("https://images.unsplash.com/photo-1472162072942-cd5147eb3902?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8MTd8fHBlb3BsZSUyMGF0JTIwZWF0aW5nfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        posts.add(Post("https://images.unsplash.com/photo-1528605248644-14dd04022da1?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nnx8ZWF0aW5nfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60"))
        posts.add(Post("https://images.unsplash.com/photo-1523050854058-8df90110c9f1?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nnx8c3R1ZGVudHxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60"))

        return posts
    }
}