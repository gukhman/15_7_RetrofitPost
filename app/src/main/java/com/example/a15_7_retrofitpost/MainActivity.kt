package com.example.a15_7_retrofitpost

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.a15_7_retrofitpost.utils.RetrofitInstance
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity() {

    private lateinit var rootView: View

    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var loadButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupWindowInsets(R.id.main)
        setupToolbar(R.id.toolbar, false)

        rootView = findViewById<View>(android.R.id.content)

        progressBar = findViewById(R.id.progressBar)
        imageView = findViewById(R.id.imageView)
        loadButton = findViewById(R.id.loadButton)

        loadButton.setOnClickListener {
            loadRandomDogImage()
        }
    }

    private fun loadRandomDogImage() {
        progressBar.visibility = View.VISIBLE
        imageView.visibility = View.INVISIBLE

        //С этой корутиной была проблема, ProgressBar исчезал раньше чем прогружалась картинка
        //в качестве решения был использован listener чтобы точно знать когда картинка загрузится
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getRandomDog()
                withContext(Dispatchers.Main) {
                    Glide.with(this@MainActivity)
                        .load(response.url)
                        .listener(object : RequestListener<Drawable> {

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility = View.GONE
                                imageView.visibility = View.VISIBLE
                                showSnackbar(false, e?.message.toString())
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility = View.GONE
                                imageView.visibility = View.VISIBLE
                                return false
                            }
                        })
                        .into(imageView)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    showSnackbar(false, e.message.toString())
                }
            }
        }
    }

    private fun showSnackbar(type: Boolean, message: String) {
        if (!type) {
            Log.e("ERROR", message)
        } else {
            Log.d("SUCCESS", message)
        }
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}