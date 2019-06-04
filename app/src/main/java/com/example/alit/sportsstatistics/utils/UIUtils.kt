package com.example.alit.sportsstatistics.utils

class UIUtils {

    companion object {

        @Throws(Exception::class)
        fun getId(resourceName: String, c: Class<*>): Int {
            val idField = c.getDeclaredField(resourceName)
            return idField.getInt(idField)
        }

    }

}