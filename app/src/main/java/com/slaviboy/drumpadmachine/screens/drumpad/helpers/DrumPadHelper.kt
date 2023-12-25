package com.slaviboy.drumpadmachine.screens.drumpad.helpers

object DrumPadHelper {

    var numberOfRows = 4
    var numberOfColumns = 3
    var page = 0 // 0,1

    fun getIndex(row: Int, column: Int): Int {
        return (page * numberItemsPerPage()) + row * numberOfColumns + column
    }

    fun isIndexInPage(index: Int): Boolean {
        return (page == index / numberItemsPerPage())
    }

    fun numberItemsPerPage(): Int {
        return numberOfColumns * numberOfRows
    }
}