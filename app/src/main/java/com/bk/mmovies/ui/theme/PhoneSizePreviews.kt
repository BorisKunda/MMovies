package com.bk.mmovies.ui.theme

import androidx.compose.ui.tooling.preview.Preview

// Generic small/medium/large buckets, plus real screen specs (width/height/
// density) for specific device lines from their named starting model through
// their newest current equivalent, so layout gets checked against actual
// hardware rather than only synthetic buckets.
@Preview(name = "Small phone", showBackground = true, device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Medium phone", showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "Large phone", showBackground = true, device = "spec:width=430dp,height=932dp,dpi=460")
@Preview(name = "Pixel 4", showBackground = true, device = "spec:width=389dp,height=822dp,dpi=440")
@Preview(name = "Pixel 9 (latest)", showBackground = true, device = "spec:width=411dp,height=919dp,dpi=420")
@Preview(name = "Galaxy S7", showBackground = true, device = "spec:width=399dp,height=710dp,dpi=577")
@Preview(name = "Galaxy S24 (latest)", showBackground = true, device = "spec:width=415dp,height=900dp,dpi=416")
@Preview(name = "Galaxy A52", showBackground = true, device = "spec:width=425dp,height=944dp,dpi=407")
@Preview(name = "Galaxy A55 (latest)", showBackground = true, device = "spec:width=435dp,height=943dp,dpi=397")
@Preview(name = "OnePlus 9", showBackground = true, device = "spec:width=430dp,height=955dp,dpi=402")
@Preview(name = "OnePlus 12 (latest)", showBackground = true, device = "spec:width=452dp,height=994dp,dpi=510")
@Preview(name = "Xiaomi Mi 11", showBackground = true, device = "spec:width=447dp,height=994dp,dpi=515")
@Preview(name = "Xiaomi 14 (latest)", showBackground = true, device = "spec:width=417dp,height=929dp,dpi=460")
annotation class PhoneSizePreviews

// Same device matrix as PhoneSizePreviews, rendered in Hebrew (RTL) so
// longer/RTL text gets checked against the same narrow/short/tall extremes,
// not just a single fixed-size Hebrew preview.
@Preview(name = "Small phone", showBackground = true, locale = "iw", device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Medium phone", showBackground = true, locale = "iw", device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "Large phone", showBackground = true, locale = "iw", device = "spec:width=430dp,height=932dp,dpi=460")
@Preview(name = "Pixel 4", showBackground = true, locale = "iw", device = "spec:width=389dp,height=822dp,dpi=440")
@Preview(name = "Pixel 9 (latest)", showBackground = true, locale = "iw", device = "spec:width=411dp,height=919dp,dpi=420")
@Preview(name = "Galaxy S7", showBackground = true, locale = "iw", device = "spec:width=399dp,height=710dp,dpi=577")
@Preview(name = "Galaxy S24 (latest)", showBackground = true, locale = "iw", device = "spec:width=415dp,height=900dp,dpi=416")
@Preview(name = "Galaxy A52", showBackground = true, locale = "iw", device = "spec:width=425dp,height=944dp,dpi=407")
@Preview(name = "Galaxy A55 (latest)", showBackground = true, locale = "iw", device = "spec:width=435dp,height=943dp,dpi=397")
@Preview(name = "OnePlus 9", showBackground = true, locale = "iw", device = "spec:width=430dp,height=955dp,dpi=402")
@Preview(name = "OnePlus 12 (latest)", showBackground = true, locale = "iw", device = "spec:width=452dp,height=994dp,dpi=510")
@Preview(name = "Xiaomi Mi 11", showBackground = true, locale = "iw", device = "spec:width=447dp,height=994dp,dpi=515")
@Preview(name = "Xiaomi 14 (latest)", showBackground = true, locale = "iw", device = "spec:width=417dp,height=929dp,dpi=460")
annotation class HebrewPhoneSizePreviews

// Same device matrix as PhoneSizePreviews, rendered in Russian, whose
// translations tend to run longer than English/Hebrew.
@Preview(name = "Small phone", showBackground = true, locale = "ru", device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "Medium phone", showBackground = true, locale = "ru", device = "spec:width=411dp,height=891dp,dpi=420")
@Preview(name = "Large phone", showBackground = true, locale = "ru", device = "spec:width=430dp,height=932dp,dpi=460")
@Preview(name = "Pixel 4", showBackground = true, locale = "ru", device = "spec:width=389dp,height=822dp,dpi=440")
@Preview(name = "Pixel 9 (latest)", showBackground = true, locale = "ru", device = "spec:width=411dp,height=919dp,dpi=420")
@Preview(name = "Galaxy S7", showBackground = true, locale = "ru", device = "spec:width=399dp,height=710dp,dpi=577")
@Preview(name = "Galaxy S24 (latest)", showBackground = true, locale = "ru", device = "spec:width=415dp,height=900dp,dpi=416")
@Preview(name = "Galaxy A52", showBackground = true, locale = "ru", device = "spec:width=425dp,height=944dp,dpi=407")
@Preview(name = "Galaxy A55 (latest)", showBackground = true, locale = "ru", device = "spec:width=435dp,height=943dp,dpi=397")
@Preview(name = "OnePlus 9", showBackground = true, locale = "ru", device = "spec:width=430dp,height=955dp,dpi=402")
@Preview(name = "OnePlus 12 (latest)", showBackground = true, locale = "ru", device = "spec:width=452dp,height=994dp,dpi=510")
@Preview(name = "Xiaomi Mi 11", showBackground = true, locale = "ru", device = "spec:width=447dp,height=994dp,dpi=515")
@Preview(name = "Xiaomi 14 (latest)", showBackground = true, locale = "ru", device = "spec:width=417dp,height=929dp,dpi=460")
annotation class RussianPhoneSizePreviews
