package com.example.user.ccb_project_ver2.navigation.model

//Content Data Model

data class ContentDTO(var explain : String? = null,var imageUrl : String? = null,
                      var uid : String?= null, var userId : String? = null,
                      var timestamp: Long? = null,var favoriteCount : Int = 0,
                      var favorites : Map<String,Boolean> = HashMap()) {
    data class  Comment(var uid: String? = null, var userId: String? = null,
                        var comment: String ?= null, var timestamp: Long? = null)
}