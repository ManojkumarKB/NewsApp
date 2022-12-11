package com.example.hackersnewsapp.model

data class Story(
    var by:String? = null,
    var descendants:String? = null,
    var id:String? = null,
    var kids:ArrayList<String>? = null,
    var score:String? = null,
    var time:String? = null,
    var title:String? = null,
    var type:String? = null,
    var url:String? = null
)


/*
{
    "by" : "kungfudoi",
    "descendants" : 246,
    "id" : 33879258,
    "kids" : [ 33879711, 33879726, 33880362, 33879761, 33879985, 33880728, 33880185, 33880691, 33883446, 33879576, 33880198, 33879831, 33880562, 33880114, 33881894, 33879900, 33882542, 33880269, 33879947, 33880146, 33882897, 33880510, 33883450, 33882496, 33882690, 33881750, 33882639, 33880941, 33880299, 33892070, 33882040, 33879834, 33884395, 33880252, 33884806, 33879973, 33881250, 33880431, 33887087, 33882554, 33881304, 33883422, 33882768, 33880412, 33880057, 33881181, 33879893, 33883489, 33884850, 33879572, 33885822, 33881908, 33881816, 33879935, 33883638, 33882080, 33879949, 33881815, 33882389 ],
    "score" : 198,
    "time" : 1670333077,
    "title" : "Remote work is gutting downtowns",
    "type" : "story",
    "url" : "https://www.businessinsider.com/remote-work-gutted-city-downtowns-office-real-estate-apocalypse-2022-12"
}*/
