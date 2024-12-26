package green.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val images = listOf(
            R.drawable.ic_onboarding_seq_1,
            R.drawable.ic_onboarding_seq_2,
            R.drawable.ic_onboarding_seq_3,
            R.drawable.ic_onboarding_seq_4
        )

        val viewPager: ViewPager2 = findViewById(R.id.viewpager)
        val adapter = ViewPagerAdapter(images)
        viewPager.adapter = adapter

        val dotsIndicator: DotsIndicator = findViewById(R.id.dots_indicator)
        dotsIndicator.attachTo(viewPager)
    }
}

class ViewPagerAdapter(private val images: List<Int>) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}
