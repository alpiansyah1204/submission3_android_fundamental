package id.whynot.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.whynot.githubuser.adapter.ListAdapter
import id.whynot.githubuser.databinding.ActivityFavUserBinding
import id.whynot.githubuser.model.User
import id.whynot.githubuser.viewmodel.FavoriteUserViewModel
import id.whynot.githubuser.viewmodel.FavoriteUserViewModelFactory

class FavUserActivity : AppCompatActivity() {
    private lateinit var viewModel: FavoriteUserViewModel
    private lateinit var binding: ActivityFavUserBinding
    private lateinit var adapterList: ListAdapter
    private var isListEmpty = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterList = ListAdapter()
        adapterList.setOnItemClick(object : ListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Intent(this@FavUserActivity, DetailUser::class.java).also {
                    it.putExtra(DetailUser.EXTRA_PERSON, data.login)
                    startActivity(it)
                }
            }
        })





        viewModel = ViewModelProvider(this, FavoriteUserViewModelFactory(application))
            .get(FavoriteUserViewModel::class.java)

        binding.apply {
            binding.rvList.layoutManager = LinearLayoutManager(this@FavUserActivity)
            binding.rvList.adapter = adapterList
            binding.rvList.setHasFixedSize(true)

            viewModel.getAllFavoriteUsers().observe(this@FavUserActivity, { favoriteUserList ->
                val list = arrayListOf<User>()
                for (i in 0 until favoriteUserList.size) {
                    Log.d("TAG", "favoriteUserList loop: ${favoriteUserList[i].id}")
                    val user = User(
                        favoriteUserList[i].id,
                        favoriteUserList[i].username,
                        favoriteUserList[i].avatarUrl
                    )
                    list.add(user)
                }
                Log.d("TAG", " list.add :$list ")

                adapterList.setData(list)
                binding.progressCircular.visibility = View.GONE
                binding.rvList.visibility = View.VISIBLE

                if (favoriteUserList.isNullOrEmpty()) {
                    isListEmpty = true
                    binding.NotFavoriteFyet.visibility = View.VISIBLE
                } else {
                    isListEmpty = false
                    binding.NotFavoriteFyet.visibility = View.INVISIBLE
                }
            })
        }
        viewModel.getAllFavoriteUsers().observe(this@FavUserActivity, { favoriteUserList ->
            val list = arrayListOf<User>()
            for (i in 0 until favoriteUserList.size) {
                Log.d("TAG", "favoriteUserList loop: ${favoriteUserList[i].id}")
                val user = User(
                    favoriteUserList[i].id,
                    favoriteUserList[i].username,
                    favoriteUserList[i].avatarUrl
                )
                list.add(user)
            }
            Log.d("TAG", " list.add :$list ")
            binding.progressCircular.visibility = View.GONE
            adapterList.setData(list)
            if (favoriteUserList.isNullOrEmpty()) {
                isListEmpty = true
                binding.NotFavoriteFyet.visibility = View.VISIBLE
            } else {
                isListEmpty = false
                binding.NotFavoriteFyet.visibility = View.INVISIBLE
            }


        })


        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = "favorite  User"
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fav_user, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.setting -> {
                val moveIntent = Intent(this@FavUserActivity, DarkModeSetting::class.java)
                startActivity(moveIntent)
                true
            }
            R.id.Delete -> {
                deleteAllFavoriteUsers()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun deleteAllFavoriteUsers() {
        // check if list is already empty
        if (isListEmpty) {
            Toast.makeText(this, "Your favorite users is already empty", Toast.LENGTH_SHORT).show()
            return
        }


        val alert = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        alert.setMessage("Are you sure want to clear your favorite users?")
        alert.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAllFavorites()
            Toast.makeText(this, "Your favorite users successfully cleared", Toast.LENGTH_SHORT)
                .show()
        }
        alert.setNegativeButton("No", null)
        alert.create().show()
    }

}