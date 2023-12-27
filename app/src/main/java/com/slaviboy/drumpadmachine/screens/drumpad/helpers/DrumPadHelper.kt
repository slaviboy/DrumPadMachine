package com.slaviboy.drumpadmachine.screens.drumpad.helpers

object DrumPadHelper {

    var numberOfRows = 4
    var numberOfColumns = 3
    private val numberOfPages = 2

    fun getIndex(page: Int, row: Int, column: Int): Int {
        return (page * numberItemsPerPage()) + row * numberOfColumns + column
    }

    fun containsIndex(row: Int, column: Int, indices: List<Int>): Boolean {
        for (i in 0 until numberOfPages) {
            val index = getIndex(i, row, column)
            if (indices.contains(index)) {
                return true
            }
        }
        return false
    }

    fun isIndexInPage(page: Int, index: Int): Boolean {
        return (page == index / numberItemsPerPage())
    }

    fun numberItemsPerPage(): Int {
        return numberOfColumns * numberOfRows
    }
}