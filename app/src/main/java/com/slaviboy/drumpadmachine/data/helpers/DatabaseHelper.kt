package com.slaviboy.drumpadmachine.data.helpers

import java.util.UUID

object DatabaseHelper {

    const val DEFAULT_UUID = "00000000-0000-0000-0000-000000000000"

    val defaultUUID: UUID
        get() = UUID.fromString(DEFAULT_UUID)
}