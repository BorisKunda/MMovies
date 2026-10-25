package com.bk.mmovies.demomobile.ui.screen.details

import android.content.res.Resources
import androidx.annotation.StringRes
import com.bk.mmovies.demomobile.R
import com.bk.mmovies.domain.model.CastMemberModel

private const val MOCK_PERSON_IMAGE_URI_PREFIX = "android.resource://com.bk.mmovies.demomobile/drawable/mock_person_"

data class MockPerson(
        val id: Int,
        val slug: String,
        @StringRes val nameRes: Int,
        @StringRes val biographyRes: Int
                     )

val mockPeople = listOf(
    MockPerson(1, "alex_morgan", R.string.mock_person_name_alex_morgan, R.string.mock_person_biography_alex_morgan),
    MockPerson(2, "maya_bennett", R.string.mock_person_name_maya_bennett, R.string.mock_person_biography_maya_bennett),
    MockPerson(3, "daniel_park", R.string.mock_person_name_daniel_park, R.string.mock_person_biography_daniel_park),
    MockPerson(4, "sofia_reyes", R.string.mock_person_name_sofia_reyes, R.string.mock_person_biography_sofia_reyes),
    MockPerson(5, "oliver_reed", R.string.mock_person_name_oliver_reed, R.string.mock_person_biography_oliver_reed),
    MockPerson(6, "amara_okafor", R.string.mock_person_name_amara_okafor, R.string.mock_person_biography_amara_okafor),
    MockPerson(7, "leo_martin", R.string.mock_person_name_leo_martin, R.string.mock_person_biography_leo_martin),
    MockPerson(8, "nora_hayes", R.string.mock_person_name_nora_hayes, R.string.mock_person_biography_nora_hayes),
    MockPerson(9, "arjun_mehta", R.string.mock_person_name_arjun_mehta, R.string.mock_person_biography_arjun_mehta),
    MockPerson(10, "ella_chen", R.string.mock_person_name_ella_chen, R.string.mock_person_biography_ella_chen)
)

fun mockCastMember(resources: Resources, personId: Int, character: String): CastMemberModel {
    val person = mockPeople.first { it.id == personId }
    return CastMemberModel(
            id = person.id,
            name = resources.getString(person.nameRes),
            character = character,
            profileUrl = MOCK_PERSON_IMAGE_URI_PREFIX + person.slug
                          )
}
