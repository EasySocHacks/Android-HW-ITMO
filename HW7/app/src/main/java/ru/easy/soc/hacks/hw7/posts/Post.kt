package ru.easy.soc.hacks.hw7.posts

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.lang.ref.WeakReference

@Parcelize
class Post (
    @Json(name = "id") val id : Int,
    @Json(name = "userId") val userId : Int,
    @Json(name = "title") val title : String,
    @Json(name = "body") val body : String
) : Parcelable

var postList = WeakReference(ArrayList<Post>())