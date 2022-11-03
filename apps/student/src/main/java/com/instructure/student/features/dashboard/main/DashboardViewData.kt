package com.instructure.student.features.dashboard.main

import com.instructure.canvasapi2.models.Course
import com.instructure.pandautils.mvvm.ItemViewModel
import com.instructure.pandautils.utils.ThemedColor
import com.instructure.student.features.dashboard.main.itemviewmodels.DashboardCourseItemViewModel

data class DashboardViewData(
    val courses: List<DashboardCourseItemViewModel>,
    val widgets: List<ItemViewModel>
)

data class DashboardCourseItemViewData(
    val imageUrl: String?,
    val courseName: String,
    val courseCode: String?,
    val grade: String,
    val courseColor: ThemedColor
)

data class DashboardGradeItemViewData(
    val assignmentName: String,
    val grade: String,
    val courseName: String,
    val courseColor: ThemedColor
)

sealed class DashboardAction {
    data class OpenCourse(val course: Course) : DashboardAction()
    data class OpenSubmission(val url: String) : DashboardAction()
    data class ShowToast(val toast: String) : DashboardAction()
    object ExpandCourses : DashboardAction()
}