package com.slaviboy.drumpadmachine.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.drumpadmachine.data.room.lesson.LessonEntity
import com.slaviboy.drumpadmachine.data.room.pad.PadEntity

data class LessonWithRelations(
    @Embedded
    val owner: LessonEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "lessonId"
    )
    val pads: List<PadEntity>
)