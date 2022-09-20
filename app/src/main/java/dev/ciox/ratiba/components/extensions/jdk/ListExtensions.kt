package dev.ciox.ratiba.components.extensions.jdk

/**
 *  Extension function to create an ArrayList
 *  from the current List object
 */
fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}