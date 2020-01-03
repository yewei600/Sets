package com.ericwei.sets.gameover

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ericwei.sets.R

class GameOverDialogFragment : DialogFragment(), GameOverContract.View {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).setTitle("Game Over")
                .setMessage(
                    "You have earned a score of " +
                            arguments!!.get(getString(R.string.game_score)) + " points!"
                )
                .setPositiveButton("Play Again") { _, _ ->
                    restartGame()
                }.setNegativeButton("Exit") { _, _ ->
                    exitGameScreen()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun exitGameScreen() {
        findNavController().navigate(R.id.action_gameOverDialogFragment_to_homeFragment)
    }

    override fun restartGame() {
        findNavController().navigate(R.id.action_gameOverDialogFragment_to_gameFragment)
    }
}
