/*
 * Copyright (C) 2019 - present Instructure, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.instructure.student.ui.interaction

import com.instructure.canvas.espresso.mockCanvas.MockCanvas
import com.instructure.canvas.espresso.mockCanvas.addAssignment
import com.instructure.canvas.espresso.mockCanvas.addAssignmentsToGroups
import com.instructure.canvas.espresso.mockCanvas.addSubmissionForAssignment
import com.instructure.canvas.espresso.mockCanvas.init
import com.instructure.canvasapi2.models.Assignment
import com.instructure.canvasapi2.utils.toApiString
import com.instructure.panda_annotations.FeatureCategory
import com.instructure.panda_annotations.Priority
import com.instructure.panda_annotations.SecondaryFeatureCategory
import com.instructure.panda_annotations.TestCategory
import com.instructure.panda_annotations.TestMetaData
import com.instructure.student.ui.utils.StudentTest
import com.instructure.student.ui.utils.routeTo
import com.instructure.student.ui.utils.tokenLogin
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.*

@HiltAndroidTest
class AssignmentDetailsInteractionTest : StudentTest() {
    override fun displaysPageObjects() = Unit

    @Test
    @TestMetaData(Priority.MANDATORY, FeatureCategory.SUBMISSIONS, TestCategory.INTERACTION, false, SecondaryFeatureCategory.SUBMISSIONS_ONLINE_URL)
    fun testSubmission_submitAssignment() {
        // TODO - Test submitting for each submission type
        // For now, I'm going to just test one submission type
        val data = MockCanvas.init(
            studentCount = 1,
            courseCount = 1
        )

        val course = data.courses.values.first()
        val student = data.students[0]
        val token = data.tokenFor(student)!!
        val assignment = data.addAssignment(courseId = course.id, submissionType = Assignment.SubmissionType.ONLINE_URL)
        data.addSubmissionForAssignment(
            assignmentId = assignment.id,
            userId = data.users.values.first().id,
            type = Assignment.SubmissionType.ONLINE_URL.apiString
        )
        tokenLogin(data.domain, token, student)
        routeTo("courses/${course.id}/assignments", data.domain)
        assignmentListPage.waitForPage()

        assignmentListPage.clickAssignment(assignment)
        assignmentDetailsPage.clickSubmit()
        urlSubmissionUploadPage.submitText("https://google.com")
        assignmentDetailsPage.assertStatusSubmitted()
    }

    @Test
    @TestMetaData(Priority.MANDATORY, FeatureCategory.ASSIGNMENTS, TestCategory.INTERACTION)
    fun testSubmissionStatus_Missing() {
        // Test clicking on the Assignment item in the Assignment List to load the Assignment Details Page
        val data = goToAssignmentFromList()
        val assignmentList = data.assignments
        val assignmentWithoutSubmissionEntry = assignmentList.filter { it.value.submission == null && it.value.dueAt != null && !it.value.isSubmitted }

        val missingAssignment = assignmentWithoutSubmissionEntry.entries.first().value
        assignmentListPage.clickAssignment(missingAssignment)
        assignmentDetailsPage.assertStatusMissing()
    }

    @Test
    @TestMetaData(Priority.MANDATORY, FeatureCategory.ASSIGNMENTS, TestCategory.INTERACTION)
    fun testSubmissionStatus_NotSubmitted() {

        val data = goToAssignmentFromList()
        val assignmentList = data.assignments
        val assignmentWithoutSubmissionEntry = assignmentList.filter {it.value.submission == null && it.value.dueAt == null}
        val assignmentWithoutSubmission = assignmentWithoutSubmissionEntry.entries.first().value

        assignmentListPage.clickAssignment(assignmentWithoutSubmission)

        assignmentDetailsPage.assertPageObjects()
        assignmentDetailsPage.assertStatusNotSubmitted()
        assignmentDetailsPage.assertDisplaysDate("No Due Date")
    }

    @Test
    @TestMetaData(Priority.MANDATORY, FeatureCategory.ASSIGNMENTS, TestCategory.INTERACTION)
    fun testDisplayToolbarTitles() {
        val data = goToAssignmentFromList()
        val assignmentList = data.assignments
        val testAssignment = assignmentList.entries.first().value
        val course = data.courses.values.first()

        assignmentListPage.clickAssignment(testAssignment)

        assignmentDetailsPage.assertDisplayToolbarTitle()
        assignmentDetailsPage.assertDisplayToolbarSubtitle(course!!.name!!)

   }

    @Test
    @TestMetaData(Priority.COMMON, FeatureCategory.ASSIGNMENTS, TestCategory.INTERACTION)
    fun testDisplayBookmarMenu() {
        val data = goToAssignmentFromList()
        val assignmentList = data.assignments
        val testAssignment = assignmentList.entries.first().value

        assignmentListPage.clickAssignment(testAssignment)

        assignmentDetailsPage.openOverflowMenu()
        assignmentDetailsPage.assertDisplaysAddBookmarkButton()
    }

    @Test
    @TestMetaData(Priority.MANDATORY, FeatureCategory.ASSIGNMENTS, TestCategory.INTERACTION)
    fun testDisplayDueDate() {
        val data = goToAssignmentFromList()
        val calendar = Calendar.getInstance().apply { set(2023, 0, 31, 23, 59, 0) }
        val expectedDueDate = "January 31, 2023 11:59 PM"
        val course = data.courses.values.first()
        val assignmentWithNoDueDate = data.addAssignment(course.id, name = "Test Assignment", dueAt = calendar.time.toApiString())
        assignmentListPage.refresh()
        assignmentListPage.clickAssignment(assignmentWithNoDueDate)

        assignmentDetailsPage.assertDisplaysDate(expectedDueDate)
    }

    @Test
    @TestMetaData(Priority.MANDATORY, FeatureCategory.ASSIGNMENTS, TestCategory.INTERACTION)
    fun testNavigating_viewAssignmentDetails() {
        // Test clicking on the Assignment item in the Assignment List to load the Assignment Details Page
        val data = goToAssignmentFromList()
        val assignmentList = data.assignments
        val assignmentWithSubmissionEntry = assignmentList.filter {it.value.submission != null}
        val assignmentWithSubmission = assignmentWithSubmissionEntry.entries.first().value

        assignmentListPage.clickAssignment(assignmentWithSubmission)

        assignmentDetailsPage.assertPageObjects()
        assignmentDetailsPage.assertAssignmentDetails(assignmentWithSubmission)
    }

    @Test
    @TestMetaData(Priority.IMPORTANT, FeatureCategory.SUBMISSIONS, TestCategory.INTERACTION)
    fun testNavigating_viewSubmissionDetailsWithSubmission() {
        // Test clicking on the Submission and Rubric button to load the Submission Details Page
        val data = goToAssignmentFromList()
        val assignmentList = data.assignments
        val assignmentWithSubmissionEntry = assignmentList.filter {it.value.submission != null}
        val assignmentWithSubmission = assignmentWithSubmissionEntry.entries.first().value

        assignmentListPage.clickAssignment(assignmentWithSubmission)

        assignmentDetailsPage.goToSubmissionDetails()
        submissionDetailsPage.assertPageObjects()
    }

    @Test
    @TestMetaData(Priority.IMPORTANT, FeatureCategory.SUBMISSIONS, TestCategory.INTERACTION)
    fun testNavigating_viewSubmissionDetailsWithoutSubmission() {
        // Test clicking on the Submission and Rubric button to load the Submission Details Page
        val data = goToAssignmentFromList()
        val assignmentList = data.assignments
        val assignmentWithoutSubmissionEntry = assignmentList.filter {it.value.submission == null}
        val assignmentWithoutSubmission = assignmentWithoutSubmissionEntry.entries.first().value

        assignmentListPage.clickAssignment(assignmentWithoutSubmission)

        assignmentDetailsPage.goToSubmissionDetails()
        submissionDetailsPage.assertPageObjects()
    }

    private fun goToAssignmentFromList(): MockCanvas {
        // Test clicking on the Submission and Rubric button to load the Submission Details Page
        val data = MockCanvas.init(
            studentCount = 1,
            courseCount = 1
        )

        val course = data.courses.values.first()
        val student = data.students[0]
        val token = data.tokenFor(student)!!
        val assignmentGroups = data.addAssignmentsToGroups(course)
        tokenLogin(data.domain, token, student)
        routeTo("courses/${course.id}/assignments", data.domain)
        assignmentListPage.waitForPage()

        // Let's find and click an assignment with a submission, so that we get meaningful
        // data in the submission details.
        val assignmentWithSubmission = assignmentGroups.flatMap { it.assignments }.find {it.submission != null}
        val assignmentWithoutSubmission = assignmentGroups.flatMap { it.assignments }.find {it.submission == null}
        assertNotNull("Expected at least one assignment with a submission", assignmentWithSubmission)
        assertNotNull("Expected at least one assignment without a submission", assignmentWithoutSubmission)

        return data

    }
}
