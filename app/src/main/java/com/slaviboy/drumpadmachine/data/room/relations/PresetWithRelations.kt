package com.slaviboy.drumpadmachine.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.slaviboy.drumpadmachine.data.room.file.FileEntity
import com.slaviboy.drumpadmachine.data.room.lesson.LessonEntity
import com.slaviboy.drumpadmachine.data.room.preset.PresetEntity

data class PresetWithRelations(
    @Embedded
    val owner: PresetEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "presetId"
    )
    val files: List<FileEntity>,


    @Relation(
        entity = LessonEntity::class,
        parentColumn = "id",
        entityColumn = "presetId"
    )
    val lessons: List<LessonWithRelations>
)