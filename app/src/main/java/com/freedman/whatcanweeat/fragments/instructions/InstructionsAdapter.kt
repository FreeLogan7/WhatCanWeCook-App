package com.freedman.whatcanweeat.fragments.instructions


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.freedman.whatcanweeat.databinding.ItemInstructionsBinding
import com.freedman.whatcanweeat.tableDetails.Recipe
import com.freedman.whatcanweeat.databinding.ItemRecipesBinding
import com.freedman.whatcanweeat.tableDetails.Instructions

class InstructionsAdapter() :
    RecyclerView.Adapter<InstructionsAdapter.ViewHolder>() { //private val listener: SendMeToInstructionsListener

    private var instructions: List<Instructions> = listOf()

    override fun getItemCount() = instructions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bindingInstructions =
            ItemInstructionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bindingInstructions)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setInstructions(instructions: List<Instructions>) {
        this.instructions = instructions
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(instructions[position], position+1)
    }

    inner class ViewHolder(private val binding: ItemInstructionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(instructions: Instructions, position: Int) {
            binding.textViewDescription.text = instructions.instructions
            binding.stepNumber.text = position.toString()
        }
    }

    fun getInstructionAtPosition(position: Int): Instructions{
        return instructions[position]
    }
}


