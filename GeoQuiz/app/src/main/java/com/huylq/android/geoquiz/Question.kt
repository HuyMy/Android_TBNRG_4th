package com.huylq.android.geoquiz

import androidx.annotation.StringRes

data class Question(
    @StringRes val textResId: Int,
    val answer: Boolean,
    var isAnswered: Boolean,
    var isCheated: Boolean
)