package green.room.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import green.room.R
import green.room.auth.AuthActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {
    private val viewModel: OnboardingViewModel by viewModels()

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private val views = mutableListOf<List<View>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // ViewPager2 데이터 설정
        val images = listOf(
            R.drawable.ic_onboarding_seq_1,
            R.drawable.ic_onboarding_seq_2,
            R.drawable.ic_onboarding_seq_3,
            R.drawable.ic_onboarding_seq_4
        )

        // ViewPager2와 어댑터 설정
        viewPager = findViewById(R.id.viewpager)
        val adapter = ViewPagerAdapter(images)
        viewPager.adapter = adapter

        // DotsIndicator 설정
        dotsIndicator = findViewById(R.id.dots_indicator)
        dotsIndicator.attachTo(viewPager)

        // 각 페이지별 뷰 묶음 추가
        initializePageViews()

        // ViewPager2 슬라이드 이벤트 처리
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updatePageVisibility(position)
            }
        })

        // "시작하기" 버튼 클릭 리스너 설정
        val startButton = findViewById<Button>(R.id.onboarding_start_button)
        startButton.setOnClickListener {
            Log.d("OnboardingActivity", "Start button clicked")

            // ViewPager2를 다음 페이지로 이동
            val nextPosition = (viewPager.currentItem + 1).coerceAtMost(views.size - 1) // 마지막 페이지를 넘지 않도록 설정
            // 마지막 페이지인지 확인
            if (viewPager.currentItem == views.size - 1) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.setOnboadingDoneOnPref()
                }
                Log.d("OnboardingActivity", "Onboarding completed")
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            } else {
                viewPager.currentItem = nextPosition
                updatePageVisibility(nextPosition)
            }
        }
    }

    private fun initializePageViews() {
        views.add(
            listOf(
                findViewById(R.id.onboarding_seq_1_head),
                findViewById(R.id.onboarding_seq_1_body)
            )
        )

        views.add(
            listOf(
                findViewById(R.id.onboarding_seq_2_head),
                findViewById(R.id.onboarding_seq_2_body),
                findViewById(R.id.onboarding_seq_2_body_2),
                findViewById(R.id.onboarding_seq_2_body_3)
            )
        )

        views.add(
            listOf(
                findViewById(R.id.onboarding_seq_3_head),
                findViewById(R.id.onboarding_seq_3_body),
                findViewById(R.id.onboarding_seq_3_body_2),
                findViewById(R.id.onboarding_seq_3_body_3)
            )
        )

        views.add(
            listOf(
                findViewById(R.id.onboarding_seq_4_head),
                findViewById(R.id.onboarding_seq_4_body),
                findViewById(R.id.onboarding_seq_4_body_2),
                findViewById(R.id.onboarding_seq_4_body_3)
            )
        )
    }

    private fun updatePageVisibility(position: Int) {
        Log.d("Onboarding", "ViewPager position = $position")

        // 모든 뷰를 숨기기
        views.flatten().forEach { it.visibility = View.GONE }

        // 현재 페이지의 뷰만 보이도록 설정
        if (position in views.indices) {
            views[position].forEach { it.visibility = View.VISIBLE }
        }
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
