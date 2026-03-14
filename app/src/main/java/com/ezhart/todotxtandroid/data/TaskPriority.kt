package com.ezhart.todotxtandroid.data

sealed class TaskPriority {
    fun display(noneLabel: String = " "): String {
        return when (this) {
            is NoPriority -> noneLabel
            is Priority -> "${this.letter}"
        }
    }

    companion object {

        val options: List<TaskPriority> by lazy { options() }

        private fun options(): List<TaskPriority> {
            val list = mutableListOf<TaskPriority>()

            list.add(NoPriority)

            for (n in 65..90) {
                list.add(Priority(Char(n)))
            }

            return list
        }
    }
}

data object NoPriority : TaskPriority()
data class Priority(val letter: Char) : TaskPriority() {
    init {
        require(letter.isLetter() && letter.isUpperCase())
    }
}

