package com.slaviboy.drumpadmachine.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import com.slaviboy.drumpadmachine.dispatchers.Dispatchers as LocalDispatchers

class DefaultDispatchers : LocalDispatchers {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
}