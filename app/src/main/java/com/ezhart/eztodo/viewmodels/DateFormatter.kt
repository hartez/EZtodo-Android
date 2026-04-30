package com.ezhart.eztodo.viewmodels

import org.ocpsoft.prettytime.PrettyTime
import java.time.LocalDate

class DateFormatter private constructor() {
    companion object {
        private val prettyTime = PrettyTime()

        private fun formatRelative(date: LocalDate): String {
            return if (date.atStartOfDay() == LocalDate.now().atStartOfDay()) {
                "Today"
            } else {
                prettyTime.format(date)
            }
        }

        private fun formatDetailed(date: LocalDate) : String{
            return "${formatRelative(date)} ($date)"
        }

        fun due(date: LocalDate?) : String{
            if(date == null){
                return "No due date"
            }

            return "Due ${formatRelative(date)}";
        }

        fun completed(date: LocalDate?):String{
            if(date == null){
                return ""
            }

            return "Completed ${formatRelative(date)}";
        }

        fun created(date: LocalDate?):String{
            if(date == null){
                return "Created date unknown"
            }

            return "Created ${formatDetailed(date)}"
        }
    }
}