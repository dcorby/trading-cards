package com.example.tradingcards.views

import android.content.Context
import com.example.tradingcards.ui.main.CreateDesignFragment

class RectangleView: PartnerView {

    constructor(context: Context) : super(context)

    constructor(context: Context?, mCreateDesignFragment : CreateDesignFragment)
            : super(context!!, mCreateDesignFragment, LayoutParams(100, 100)) {
    }
}