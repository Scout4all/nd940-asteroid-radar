package me.bigad.asteroidradar.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import me.bigad.asteroidradar.R
import me.bigad.asteroidradar.domain.Asteroid
import me.bigad.asteroidradar.domain.PhotoOfDay
import me.bigad.asteroidradar.repository.ApiStatus
import me.bigad.asteroidradar.ui.main.MainFragmentRecyclerAdapter


@BindingAdapter("photoOfDay")
fun bindPhotoOfDay(imageView: ImageView, data: PhotoOfDay?) {
    data?.let {
        if (data.mediaType == "image") {
            Picasso.with(imageView.context)
                .load(data.url)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(imageView)
            imageView.contentDescription = data.title
        }else{
            imageView.visibility = View.GONE
        }

    }

}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as MainFragmentRecyclerAdapter

    adapter.submitList(data)
}

@BindingAdapter("showProgress")
fun bindProgress(progressBar: ConstraintLayout, data: ApiStatus?) {
    when (data) {
        ApiStatus.LOADING -> progressBar.visibility = View.VISIBLE
        else -> {
            progressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("showNoNetwork")
fun bindNoNetwork(errorImage: ConstraintLayout, data: ApiStatus?) {
    when (data) {
        ApiStatus.ERROR -> errorImage.visibility = View.VISIBLE
        else -> {
            errorImage.visibility = View.GONE
        }
    }
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)

    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription =
            imageView.context.getString(R.string.potentially_hazardous_asteroid_image)

    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription =
            imageView.context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
