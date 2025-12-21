package com.ericwei.sets.gameover

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ericwei.sets.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameOverDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val score = arguments?.getString(getString(R.string.game_score)) ?: "0"
        
        return activity?.let {
            AlertDialog.Builder(it).setTitle("Game Finished")
                .setMessage("You have earned a score of $score points!")
                .setPositiveButton("Play Again") { _, _ ->
                    findNavController().navigate(R.id.action_gameOverDialogFragment_to_gameFragment)
                }.setNegativeButton("Exit") { _, _ ->
                    findNavController().navigate(R.id.action_gameOverDialogFragment_to_homeFragment)
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
