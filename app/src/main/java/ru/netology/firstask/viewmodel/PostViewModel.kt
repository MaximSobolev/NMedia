package ru.netology.firstask.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.firstask.repository.PostRepository
import ru.netology.firstask.repository.PostReposytoryInMemoryImpl
import kotlin.math.floor

class PostViewModel : ViewModel() {
    private val repository : PostRepository = PostReposytoryInMemoryImpl()
    val data = repository.get()
    fun like() = repository.like()
    fun share() = repository.share()

    fun largeNumberDisplay (number : Int) : String {
        val firstBorder = 1_000
        val secondBorder = 10_000
        val thirdBorder = 1_000_000
        val numberOfSings = 10.0
        val output = when {
            (number >= firstBorder) and (number < secondBorder) ->
                "${String.format("%.1f",
                    floor((number/firstBorder.toDouble())*numberOfSings) /
                            numberOfSings)}K"
            (number >= secondBorder) and (number < thirdBorder) ->
                "${number/firstBorder}K"
            number >= thirdBorder -> "${String.format("%.1f",
                floor((number/thirdBorder.toDouble())*numberOfSings) /
                        numberOfSings)}M"
            else -> number.toString()
        }
        return output
    }
}