package com.dev_marinov.chatalyze.presentation.util

object IfLetHelper {
    inline fun <T: Any, R: Any> execute(vararg elements: T?, closure: (List<T>) -> R): R? {
        return if (elements.all { it != null }) {
            closure(elements.filterNotNull())
        } else null
    }
}