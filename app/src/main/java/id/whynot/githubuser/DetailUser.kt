package id.whynot.githubuser


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import id.whynot.githubuser.adapter.SectionPager
import id.whynot.githubuser.databinding.ActivityDetailUserBinding
import id.whynot.githubuser.db.FavoriteUser
import id.whynot.githubuser.viewmodel.UserDetailViewModel
import id.whynot.githubuser.viewmodel.UserDetailViewModelFactory
import id.whynot.githubuser.viewmodel.ViewModel


class DetailUser : AppCompatActivity() {


    private lateinit var userDetailViewModel: UserDetailViewModel
    private var isFavorite = false
    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var viewModel: ViewModel


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var link: String
        var avatar: String
        val username = intent.getStringExtra(EXTRA_PERSON)

        userDetailViewModel =
            ViewModelProvider(this, UserDetailViewModelFactory(application, username as String))
                .get(UserDetailViewModel::class.java)


        isFavorite = userDetailViewModel.isFavoriteUserExists(username)
        Log.d("TAG", "isFavoriteUserExists: $isFavorite ")
        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        username.let { viewModel.setDetailUser(it) }
        showTabLayout()

        viewModel.getUserDetail().observe(this) {
            binding.tvUsernameDetail.text = it.login
            binding.tvName.text = it.name
            binding.tvFollowingDetail.text = "Following\n ${it.following}"
            binding.tvFollowerDetail.text = "Follower\n ${it.followers}"
            avatar = it.avatar_url
            binding.tvRespositoryDetail.text = "Repository \n ${it.public_repos}"
            binding.tvCompany.text = it.company
            binding.tvBlog.text = it.blog
            Log.d("TAG", "link link: ${it.html_url}")
            link = it.html_url
            binding.btnShare.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT, link
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            if (isFavorite) {
                binding.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_true)
            }
            Glide.with(this)
                .load(it.avatar_url)
                .centerCrop()
                .into(binding.ivItemAvatar)
            binding.btnFavorite.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) { // like
                    insertFavoriteUser(username, avatar)
                } else { // dislike
                    deleteFavoriteUser(username)
                }
            }

            if (supportActionBar != null) {
                (supportActionBar as ActionBar).title = "Detail User"
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }


    private fun showTabLayout() {
        val username = intent.getStringExtra(EXTRA_PERSON)
        val bundle = Bundle()
        bundle.putString(EXTRA_PERSON, username)
        val sectionsPagerAdapter = SectionPager(this, bundle)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun insertFavoriteUser(it: String?, avatar: String) {
        val favUser = it?.let { user_name ->
            FavoriteUser(
                username = user_name,
                avatarUrl = avatar
            )
        }

        // insert to db
        if (favUser != null) {
            userDetailViewModel.insert(favUser)
        }

        // set icon fav to true and notify user
        binding.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_true)
        Toast.makeText(
            this,
            "$it has added to favorite users",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun deleteFavoriteUser(it: String?) {
        // get fav user from db and delete it
        val favUser: FavoriteUser? = it?.let { it1 -> userDetailViewModel.getFavoriteUser(it1) }
        if (favUser != null) {
            userDetailViewModel.delete(favUser)
        }

        // set icon fav to false and notify user
        binding.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_false)
        Toast.makeText(
            this,
            "$it has removed from favorite users",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.setting -> {
                val moveIntent = Intent(this@DetailUser, DarkModeSetting::class.java)
                startActivity(moveIntent)

                true
            }
            R.id.Favorit -> {
                val moveIntent = Intent(this@DetailUser, FavUserActivity::class.java)
                startActivity(moveIntent)
                true
            }
            else -> {
                false
            }
        }
    }

    companion object {

        const val EXTRA_PERSON = "username"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.txt_followers,
            R.string.txt_following
        )

    }


}

