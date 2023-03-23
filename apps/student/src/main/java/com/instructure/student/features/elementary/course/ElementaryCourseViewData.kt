/*
 * Copyright (C) 2021 - present Instructure, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.instructure.student.features.elementary.course

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.instructure.canvasapi2.models.CanvasContext

data class ElementaryCourseViewData(
    val tabs: List<ElementaryCourseTab>,
    @ColorInt val color: Int
)

data class ElementaryCourseTab(
    val tabId: String,
    val icon: Drawable?,
    val text: String?,
    val url: String
)

sealed class ElementaryCourseAction {
    object RedirectToCourseBrowserPage : ElementaryCourseAction()
    object RedirectToGrades : ElementaryCourseAction()
    object RedirectToModules : ElementaryCourseAction()
}