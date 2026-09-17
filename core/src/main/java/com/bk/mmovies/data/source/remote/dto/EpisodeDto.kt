package com.bk.mmovies.data.source.remote.dto

import com.google.gson.annotations.SerializedName

data class EpisodeDto(
        val id: Int?,
        val name: String?,
        val overview: String?,
        @SerializedName("air_date")
        val airDate: String?,
        @SerializedName("episode_number")
        val episodeNumber: Int?,
        @SerializedName("season_number")
        val seasonNumber: Int?,
        @SerializedName("still_path")
        val stillPath: String?,
        @SerializedName("vote_average")
        val voteAverage: Double?,
        val runtime: Int?,
        // Unlike movie/TV-series credits (nested under append_to_response=credits),
        // TMDB's episode endpoint returns crew as a top-level field already.
        val crew: List<CrewMemberDto>?
                     )
