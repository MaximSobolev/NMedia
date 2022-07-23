package ru.netology.firstask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.firstask.databinding.ActivityMainBinding
import kotlin.math.floor

class MainActivity : AppCompatActivity() {
    lateinit var post: Post
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBinding()
        loadData()
        showData()
        setupListeners()
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun loadData() {
        post = Post (
            1,
            getString(R.string.author),
            getString(R.string.content),
            getString(R.string.published)
        )
    }

    private fun showData() {
        binding?.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = largeNumberDisplay(post.like)
            shareCount.text = largeNumberDisplay(post.share)
            viewCount.text = largeNumberDisplay(post.view)
            if (post.likeByMe) {
                like.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            like.setOnClickListener {
                post = post.copy(likeByMe = !post.likeByMe)
                if (post.likeByMe) {
                    like.setImageResource(R.drawable.ic_baseline_favorite_24)
                    post = post.copy(like = post.like + 1)
                    likeCount.text = largeNumberDisplay(post.like)
                } else {
                    like.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    post = post.copy(like = post.like - 1)
                    likeCount.text = largeNumberDisplay(post.like)
                }
            }
            share.setOnClickListener {
                post = post.copy(share = post.share + 1)
                shareCount.text = largeNumberDisplay(post.share)
            }
        }
    }

    private fun largeNumberDisplay (number : Int) : String {
        val firstBorder = 1_000
        val secondBorder = 10_000
        val thirdBorder = 1_000_000
        val numberOfSings = 10.0
        val output = when {
            (number >= firstBorder) and (number < secondBorder) ->
                "${String.format("%.1f", 
                    floor((number/firstBorder.toDouble())*numberOfSings)/
                            numberOfSings)}K"
            (number >= secondBorder) and (number < thirdBorder) ->
                "${number/firstBorder}K"
            number >= thirdBorder -> "${String.format("%.1f",
                floor((number/thirdBorder.toDouble())*numberOfSings)/
                        numberOfSings)}M"
            else -> number.toString()
        }
        return output
    }

}