package ru.easy.soc.hacks.hw7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.easy.soc.hacks.hw6.propertyanim.PropertyAnimation
import ru.easy.soc.hacks.hw7.posts.Post
import ru.easy.soc.hacks.hw7.posts.PostAdapter
import ru.easy.soc.hacks.hw7.posts.postList
import ru.easy.soc.hacks.hw7.posts.service.PostService
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    private var getPostListCall : Call<List<Post>>? = null
    private var addPostCall : Call<Post>? = null
    private var removePostCall : Call<Void>? = null

    companion object {
        lateinit var instance : MainActivity
            private set

        private val retrofit = Retrofit.Builder().
        baseUrl("https://jsonplaceholder.typicode.com/").
        addConverterFactory(MoshiConverterFactory.create()).
        build()

        private val postService = retrofit.create(PostService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        setContentView(R.layout.activity_main)

        val propertyAnimation = PropertyAnimation(animatedCircle_1, animatedCircle_2, animatedCircle_3)
        propertyAnimation.startAnimation()

        addPostButton.setOnClickListener {
            callAddPost()
        }

        if (postList.get() != null && postList.get()!!.isNotEmpty()) {
            val viewManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)

            recyclerView.apply {
                layoutManager = viewManager
                adapter = PostAdapter(postList.get()!!) {
                    callRemovePost(it)
                }
            }

            addPostButton.visibility = View.VISIBLE

            hildeLoadingAnimation()
        } else {
            showLoadingAnimation()

            callGetPostList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        cancelGetPostListCall()
        cancelAddPostCall()
        cancelRemovePostCall()
    }

    private fun cancelGetPostListCall() {
        getPostListCall?.cancel()
        getPostListCall = null
    }

    private fun cancelAddPostCall() {
        addPostCall?.cancel()
        addPostCall = null
    }

    private fun cancelRemovePostCall() {
        removePostCall?.cancel()
        removePostCall = null
    }

    private fun hildeLoadingAnimation() {
        animatedCircle_1.visibility = View.INVISIBLE
        animatedCircle_2.visibility = View.INVISIBLE
        animatedCircle_3.visibility = View.INVISIBLE
    }

    private fun showLoadingAnimation() {
        animatedCircle_1.visibility = View.VISIBLE
        animatedCircle_2.visibility = View.VISIBLE
        animatedCircle_3.visibility = View.VISIBLE
    }

    private fun callGetPostList() {
        getPostListCall = postService.getPostList()

        getPostListCall?.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                postList = WeakReference(response.body() as ArrayList<Post>)

                val viewManager = LinearLayoutManager(this@MainActivity)
                recyclerView.setHasFixedSize(true)

                recyclerView.apply {
                    layoutManager = viewManager
                    adapter = PostAdapter(postList.get()!!) {
                        callRemovePost(it)
                    }
                }

                addPostButton.visibility = View.VISIBLE

                hildeLoadingAnimation()

                Toast.makeText(
                    applicationContext,
                    "Code: " + response.code().toString() + " " + response.message(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                failureLoadListMessage.visibility = View.VISIBLE
                tryAgainMessage.visibility = View.VISIBLE

                hildeLoadingAnimation()

                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun callRemovePost(id : Int) {
        cancelRemovePostCall()
        showLoadingAnimation()

        removePostCall = postService.deletePost(id)

        removePostCall!!.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(
                    applicationContext,
                    "Code: " + response.code().toString() + " " + response.message(),
                    Toast.LENGTH_SHORT).show()

                hildeLoadingAnimation()

                hildeLoadingAnimation()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT).show()

                hildeLoadingAnimation()

                hildeLoadingAnimation()
            }
        })
    }

    private fun callAddPost() {
        cancelAddPostCall()
        showLoadingAnimation()

        val newPost = Post(1, "Title", "Body")

        val createPostCall = postService.createPost(newPost)
        createPostCall.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Toast.makeText(
                    applicationContext,
                    "Code: " + response.code().toString() + " " + response.message(),
                    Toast.LENGTH_SHORT).show()

                hildeLoadingAnimation()
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT).show()

                hildeLoadingAnimation()

                hildeLoadingAnimation()
            }
        })
    }
}