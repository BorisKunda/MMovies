package com.bk.mmovies.network

const val MOCK_GET_IS_TOKEN_VALID_TRUE_RESPONSE = """
            {
                "success": true
            }
            """

const val MOCK_GET_IS_TOKEN_VALID_FALSE_RESPONSE = """
            {
                "status_code": 7,
                "status_message": "Invalid API key: You must be granted a valid key.",
                "success": false
            }
            """

const val MOCK_GET_POPULAR_MOVIES_RESPONSE = """
    {
  "page": 1,
  "results": [
    {
      "adult": false,
      "backdrop_path": "/3ooXDVaz4xHKtwe4lkmF1gNopOC.jpg",
      "genre_ids": [
        27,
        53
      ],
      "id": 1290417,
      "original_language": "en",
      "original_title": "Thrash",
      "overview": "When a Category 5 hurricane decimates a coastal town, the storm surge brings devastation, chaos, and something far more frightening onto shore: hungry sharks.",
      "popularity": 183.2522,
      "poster_path": "/adk8weka3O5648g3de4z3y4aE7G.jpg",
      "release_date": "2026-04-10",
      "title": "Thrash",
      "video": false,
      "vote_average": 5.988,
      "vote_count": 366
    },
    {
      "adult": false,
      "backdrop_path": "/wns4x1GyxCudgOZRyhXKjXVJf3T.jpg",
      "genre_ids": [
        12,
        53,
        878
      ],
      "id": 840464,
      "original_language": "en",
      "original_title": "Greenland 2: Migration",
      "overview": "Having found the safety of the Greenland bunker after the comet Clarke decimated the Earth, the Garrity family must now risk everything to embark on a perilous journey across the wasteland of Europe to find a new home.",
      "popularity": 193.9049,
      "poster_path": "/z2tqCJLsw6uEJ8nJV8BsQXGa3dr.jpg",
      "release_date": "2026-01-07",
      "title": "Greenland 2: Migration",
      "video": false,
      "vote_average": 6.5,
      "vote_count": 889
    },
    {
      "adult": false,
      "backdrop_path": "/7lizs4SuEU2ihkkAa0SZ66NtHbl.jpg",
      "genre_ids": [
        28,
        53
      ],
      "id": 1641319,
      "original_language": "en",
      "original_title": "Sniper: No Nation",
      "overview": "Brandon Beckett and Agent Zero lead a rescue mission in Venezuela when their friends are taken hostage.",
      "popularity": 153.5395,
      "poster_path": "/tUmARo0TZEK1EaSuS6dU35FhDyU.jpg",
      "release_date": "2026-04-07",
      "title": "Sniper: No Nation",
      "video": false,
      "vote_average": 6.2,
      "vote_count": 32
    }
  ],
  "total_pages": 56299,
  "total_results": 1125967
}
"""

const val MOCK_MATRIX_JSON_OBJ = """
     {
    "adult": false,
    "backdrop_path": "/tlm8UkiQsitc8rSuIAscQDCnP8d.jpg",
    "belongs_to_collection": {
        "id": 2344,
        "name": "The Matrix Collection",
        "poster_path": "/bV9qTVHTVf0gkW0j7p7M0ILD4pG.jpg",
        "backdrop_path": "/bRm2DEgUiYciDw3myHuYFInD7la.jpg"
    },
    "budget": 63000000,
    "genres": [
        {
            "id": 28,
            "name": "Action"
        },
        {
            "id": 878,
            "name": "Science Fiction"
        }
    ],
    "homepage": "http://www.warnerbros.com/matrix",
    "id": 603,
    "imdb_id": "tt0133093",
    "origin_country": [
        "US"
    ],
    "original_language": "en",
    "original_title": "The Matrix",
    "overview": "Set in the 22nd century, The Matrix tells the story of a computer hacker who joins a group of underground insurgents fighting the vast and powerful computers who now rule the earth.",
    "popularity": 45.5132,
    "poster_path": "/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg",
    "production_companies": [
        {
            "id": 79,
            "logo_path": "/at4uYdwAAgNRKhZuuFX8ShKSybw.png",
            "name": "Village Roadshow Pictures",
            "origin_country": "US"
        },
        {
            "id": 372,
            "logo_path": null,
            "name": "Groucho II Film Partnership",
            "origin_country": ""
        },
        {
            "id": 1885,
            "logo_path": "/xlvoOZr4s1PygosrwZyolIFe5xs.png",
            "name": "Silver Pictures",
            "origin_country": "US"
        },
        {
            "id": 174,
            "logo_path": "/zhD3hhtKB5qyv7ZeL4uLpNxgMVU.png",
            "name": "Warner Bros. Pictures",
            "origin_country": "US"
        }
    ],
    "production_countries": [
        {
            "iso_3166_1": "US",
            "name": "United States of America"
        }
    ],
    "release_date": "1999-03-31",
    "revenue": 463517383,
    "runtime": 136,
    "softcore": false,
    "spoken_languages": [
        {
            "english_name": "English",
            "iso_639_1": "en",
            "name": "English"
        }
    ],
    "status": "Released",
    "tagline": "Believe the unbelievable.",
    "title": "The Matrix",
    "video": false,
    "vote_average": 8.25,
    "vote_count": 28128
}
"""

const val MOCK_TERMINATOR_JSON_OBJ = """
{
    "adult": false,
    "backdrop_path": "/ffdqHMWkh1h9MABwIfbfRJhgFW6.jpg",
    "belongs_to_collection": {
        "id": 528,
        "name": "The Terminator Collection",
        "poster_path": "/kpZxdNsAV7qTdTLwKM5NLqa7GEo.jpg",
        "backdrop_path": "/sCnBEw2Yu6foEjs4Xb4eMddYHRo.jpg"
    },
    "budget": 6400000,
    "genres": [
        {
            "id": 28,
            "name": "Action"
        },
        {
            "id": 53,
            "name": "Thriller"
        },
        {
            "id": 878,
            "name": "Science Fiction"
        }
    ],
    "homepage": "https://www.20thcenturystudios.com/movies/theterminator",
    "id": 218,
    "imdb_id": "tt0088247",
    "origin_country": [
        "US"
    ],
    "original_language": "en",
    "original_title": "The Terminator",
    "overview": "In the post-apocalyptic future, reigning tyrannical supercomputers teleport a cyborg assassin known as the \"Terminator\" back to 1984 to kill Sarah Connor, whose unborn son is destined to lead insurgents against 21st century mechanical hegemony. Meanwhile, the human-resistance movement dispatches a lone warrior to safeguard Sarah. Can he stop the virtually indestructible killing machine?",
    "popularity": 20.8974,
    "poster_path": "/qvktm0BHcnmDpul4Hz01GIazWPr.jpg",
    "production_companies": [
        {
            "id": 3952,
            "logo_path": "/hLfS5FjBQjK5MUNl7kdXgIHSSWy.png",
            "name": "Hemdale",
            "origin_country": "GB"
        },
        {
            "id": 1280,
            "logo_path": null,
            "name": "Pacific Western",
            "origin_country": "US"
        }
    ],
    "production_countries": [
        {
            "iso_3166_1": "GB",
            "name": "United Kingdom"
        },
        {
            "iso_3166_1": "US",
            "name": "United States of America"
        }
    ],
    "release_date": "1984-10-26",
    "revenue": 78371200,
    "runtime": 108,
    "softcore": false,
    "spoken_languages": [
        {
            "english_name": "English",
            "iso_639_1": "en",
            "name": "English"
        },
        {
            "english_name": "Spanish",
            "iso_639_1": "es",
            "name": "Español"
        }
    ],
    "status": "Released",
    "tagline": "Your future is in its hands.",
    "title": "The Terminator",
    "video": false,
    "vote_average": 7.685,
    "vote_count": 14731
}
"""

const val MOCK_BLADE_JSON_OBJ = """
    {
    "adult": false,
    "backdrop_path": "/7NKfxJrQn053UJeLftlx4m4NTzo.jpg",
    "belongs_to_collection": {
        "id": 735,
        "name": "Blade Collection",
        "poster_path": "/vTXwJm2UC0Z0HwXTRmsMecGJErc.jpg",
        "backdrop_path": "/d7h60yRgP6US9VYenEPzFGW9saY.jpg"
    },
    "budget": 45000000,
    "genres": [
        {
            "id": 27,
            "name": "Horror"
        },
        {
            "id": 28,
            "name": "Action"
        }
    ],
    "homepage": "https://www.warnerbros.com/movies/blade",
    "id": 36647,
    "imdb_id": "tt0120611",
    "origin_country": [
        "US"
    ],
    "original_language": "en",
    "original_title": "Blade",
    "overview": "The Daywalker known as \"Blade\" - a half-vampire, half-mortal man - becomes the protector of humanity against an underground army of vampires.",
    "popularity": 12.5397,
    "poster_path": "/oWT70TvbsmQaqyphCZpsnQR7R32.jpg",
    "production_companies": [
        {
            "id": 12,
            "logo_path": "/2ycs64eqV5rqKYHyQK0GVoKGvfX.png",
            "name": "New Line Cinema",
            "origin_country": "US"
        },
        {
            "id": 421,
            "logo_path": null,
            "name": "Amen Ra Films",
            "origin_country": "US"
        },
        {
            "id": 11321,
            "logo_path": null,
            "name": "Imaginary Forces",
            "origin_country": ""
        }
    ],
    "production_countries": [
        {
            "iso_3166_1": "US",
            "name": "United States of America"
        }
    ],
    "release_date": "1998-08-21",
    "revenue": 131183530,
    "runtime": 121,
    "softcore": false,
    "spoken_languages": [
        {
            "english_name": "Russian",
            "iso_639_1": "ru",
            "name": "Pусский"
        },
        {
            "english_name": "English",
            "iso_639_1": "en",
            "name": "English"
        }
    ],
    "status": "Released",
    "tagline": "Against an army of Immortals, one warrior must draw first blood.",
    "title": "Blade",
    "video": false,
    "vote_average": 6.843,
    "vote_count": 6763
}
"""