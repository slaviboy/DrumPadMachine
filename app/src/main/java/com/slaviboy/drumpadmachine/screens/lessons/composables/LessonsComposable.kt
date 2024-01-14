package com.slaviboy.drumpadmachine.screens.lessons.composables

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.slaviboy.drumpadmachine.data.entities.Lesson
import com.slaviboy.drumpadmachine.screens.lessons.viewmodels.LessonsViewModel

@Destination
@Composable
@RootNavGraph(start = false)
fun LessonsComposable(
    navigator: DestinationsNavigator,
    lessonsViewModel: LessonsViewModel,
    onError: (error: String) -> Unit = {},
    lesson: Lesson
) {
}