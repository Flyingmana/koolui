package com.lightningkite.koolui.builders

import com.lightningkite.koolui.async.UI
import com.lightningkite.koolui.concepts.Importance
import com.lightningkite.koolui.concepts.TextSize
import com.lightningkite.koolui.geometry.AlignPair
import com.lightningkite.koolui.image.ImageWithSizing
import com.lightningkite.koolui.views.ViewFactory
import com.lightningkite.koolui.views.ViewGenerator
import com.lightningkite.reacktive.property.*
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.recktangle.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun <VIEW> ViewFactory<VIEW>.text(
        text: String,
        size: TextSize = TextSize.Body,
        align: AlignPair = AlignPair.CenterLeft,
        importance: Importance = Importance.Normal,
        maxLines: Int = Int.MAX_VALUE
): VIEW = text(
        text = ConstantObservableProperty(text),
        size = size,
        importance = importance,
        align = align,
        maxLines = maxLines
)

fun <VIEW> ViewFactory<VIEW>.image(
        imageWithSizing: ImageWithSizing
): VIEW = image(ConstantObservableProperty(imageWithSizing))

fun <VIEW> ViewFactory<VIEW>.space(size: Float): VIEW = space(Point(size, size))
fun <VIEW> ViewFactory<VIEW>.space(width: Float, height: Float): VIEW = space(Point(width, height))

fun <VIEW> ViewFactory<VIEW>.button(
        label: String,
        imageWithSizing: ImageWithSizing? = null,
        importance: Importance = Importance.Normal,
        onClick: () -> Unit
): VIEW = button(
        label = ConstantObservableProperty(label),
        imageWithSizing = ConstantObservableProperty(imageWithSizing),
        importance = importance,
        onClick = onClick
)

fun <VIEW> ViewFactory<VIEW>.imageButton(
        imageWithSizing: ImageWithSizing,
        label: String? = null,
        importance: Importance = Importance.Normal,
        onClick: () -> Unit
): VIEW = imageButton(
        label = ConstantObservableProperty(label),
        imageWithSizing = ConstantObservableProperty(imageWithSizing),
        importance = importance,
        onClick = onClick
)

fun <DEPENDENCY, VIEW> ViewFactory<VIEW>.pagesEmbedded(
        dependency: DEPENDENCY,
        page: MutableObservableProperty<Int>,
        vararg pageGenerators: (DEPENDENCY) -> VIEW
) = pages(dependency, page, *pageGenerators.map {
    object : ViewGenerator<DEPENDENCY, VIEW> {
        @Suppress("UNCHECKED_CAST")
        override fun generate(dependency: DEPENDENCY): VIEW = (dependency as ViewFactory<VIEW>).space(5f)
    }
}.toTypedArray())

fun <VIEW> ViewFactory<VIEW>.loadingImage(
        imageWithSizingObservable: ObservableProperty<ImageWithSizing?>
) = work(image(imageWithSizing = imageWithSizingObservable.transform { it ?: ImageWithSizing.none }), imageWithSizingObservable.transform { it == null })

fun <VIEW> ViewFactory<VIEW>.loadingImage(
        load: suspend ()->ImageWithSizing
): VIEW {
    val obs = StandardObservableProperty<ImageWithSizing?>(null)
    return loadingImage(obs).apply {
        scope.launch(Dispatchers.UI) {
            obs.value = load()
        }
    }
}

fun <VIEW, T> ViewFactory<VIEW>.loadingImage(
        observable: ObservableProperty<T>,
        load: suspend (T)->ImageWithSizing
): VIEW {
    val obs = StandardObservableProperty<ImageWithSizing?>(null)
    return loadingImage(obs).apply {
        lifecycle.bind(observable){
            obs.value = null
            scope.launch(Dispatchers.UI){
                obs.value = load(it)
            }
        }
    }
}

fun <VIEW> ViewFactory<VIEW>.launchConfirmationDialog(
        title: String = "",
        message: String = "Are you sure you want to do this?",
        onCancel: () -> Unit = {},
        onConfirm: () -> Unit
) {
    var approved = false
    launchDialog(
            dismissable = true,
            onDismiss = {
                if (approved)
                    onConfirm.invoke()
                else
                    onCancel.invoke()
            }
    ) { dismiss ->
        card(scrollVertical(vertical {
            -text(text = title, size = TextSize.Header)
            -text(text = message)
            -horizontal {
                +space(1f)
                -button(label = "Cancel") {
                    approved = false
                    dismiss()
                }
                -button(label = "OK") {
                    approved = true
                    dismiss()
                }
            }
        }))
    }
}

fun <VIEW> ViewFactory<VIEW>.launchInfoDialog(
        title: String,
        message: String,
        onDismiss: () -> Unit = {}
) {
    launchDialog(
            dismissable = true,
            onDismiss = onDismiss
    ) { dismiss ->
        card(scrollVertical(vertical {
            -text(text = title, size = TextSize.Header)
            -text(text = message)
            -horizontal {
                +space(1f)
                -button(label = "OK") {
                    dismiss()
                }
            }
        }).margin(8f))
    }
}
