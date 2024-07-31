package project.stn991662556.fifaworldcup2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import project.stn991662556.fifaworldcup2022.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.homePage -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, HomeFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            R.id.addTeam -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, AddTeamFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }

            R.id.editTeam -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, EditTeamFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }

            R.id.deleteTeam -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, DeleteTeamFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }

            R.id.displayTeams -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, DisplayTeamsFragment())
                    .addToBackStack(null)
                    .commit()
                true

            }

            else -> {
                return super.onOptionsItemSelected(item)

            }
        }
    }

}