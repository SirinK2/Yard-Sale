package com.tuwaiq.finalproject.domain.model

import com.tuwaiq.finalproject.data.remote.dto.CurrentLocation

data class Post (

    var owner: String = "",
    var categoryName: String = "",
    var title: String = "",
    var description: String = "",
    var price: String = "",
    var location: CurrentLocation = CurrentLocation(0.0,0.0),
    var photoUrl: List<String> = listOf(),
    var id: String = ""
        )
