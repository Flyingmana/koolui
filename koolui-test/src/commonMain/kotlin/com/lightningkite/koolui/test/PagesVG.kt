package com.lightningkite.koolui.test

import com.lightningkite.reacktive.property.StandardObservableProperty
import com.lightningkite.koolui.builders.pagesEmbedded
import com.lightningkite.koolui.builders.text
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair

class PagesVG<VIEW>() : MyViewGenerator<VIEW> {
    override val title: String = "Pages"

    override fun generate(dependency: MyViewFactory<VIEW>): VIEW = with(dependency) {
        pagesEmbedded(
                dependency,
                StandardObservableProperty(0),
                {
                    text(text = "First page", size = TextSize.Header, align = AlignPair.CenterCenter)
                },
                {
                    text(text = "Second page", size = TextSize.Header, align = AlignPair.CenterCenter)
                },
                {
                    text(text = "Third page", size = TextSize.Header, align = AlignPair.CenterCenter)
                },
                {
                    text(text = "Last page", size = TextSize.Header, align = AlignPair.CenterCenter)
                }
        )
    }
}
