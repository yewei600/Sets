package com.ericwei.sets.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        binding.playBtn.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(v: View?) {
        val navController = v!!.findNavController()
        when (v!!.id) {
            R.id.playBtn ->
                navController.navigate(R.id.action_homeFragment_to_gameFragment)

        }

    }

}
