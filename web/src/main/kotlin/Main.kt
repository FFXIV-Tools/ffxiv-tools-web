import kotlinext.js.require
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import kotlin.math.roundToInt

external interface Material {
    val itemId: Int
    val name: String
    val quantity: Int
    val datacenterMinimum: Int
    val datacenterMean: Int
    val datacenterDeviation: Double
    val worldMinimum: Int
    val worldMean: Int
    val worldDeviation: Double
}

external interface Watch {
    val itemId: Int
    val name: String
    val datacenterMinimum: Int
    val datacenterMean: Int
    val datacenterDeviation: Double
    val worldMinimum: Int
    val worldMean: Int
    val worldDeviation: Double
    val materials: Array<Material>
}

fun Int.localeFormat(): String {
    return "${this.asDynamic().toLocaleString()}"
}

fun Double.decimalFormat(digits: Int = 2): String {
    return "${(this * 100.0).asDynamic().toFixed(digits)}%"
}

external interface WatchItemProps : PropsWithChildren {
    var watch: Watch
}

val watchItem = functionComponent<WatchItemProps> { props ->
    val (active, setActive) = useState(false)
    val dropdownRef = useRef(null)

    val watch = props.watch
    val itemMin = watch.worldMinimum
    val itemMax = (watch.worldMean + watch.worldDeviation).roundToInt()
    val materialsMin = watch.materials.sumOf { it.datacenterMinimum * it.quantity }
    val materialsMax = watch.materials.sumOf {
        (it.datacenterMean + it.datacenterDeviation) * it.quantity
    }.roundToInt()

    val minProfit = itemMin - materialsMax
    val maxProfit = itemMax - materialsMin
    val minProfitPercent = ((itemMin - materialsMax).toDouble() / itemMin).decimalFormat()
    val maxProfitPercent = ((itemMax - materialsMin).toDouble() / itemMax).decimalFormat()

    tr {
        key = watch.itemId.toString()
        td { +watch.name }
        td { +itemMin.localeFormat() }
        td { +itemMax.localeFormat() }
        td { +materialsMin.localeFormat() }
        td { +materialsMax.localeFormat() }
        td { +"${minProfit.localeFormat()} ($minProfitPercent)" }
        td { +"${maxProfit.localeFormat()} ($maxProfitPercent)" }
        td {
            // No button if the item is not a recipe
            if (watch.materials.isEmpty()) {
                return@td
            }

            div(classes = "dropdown is-right") {
                ref = dropdownRef
                div(classes = "dropdown-trigger") {
                    attrs.onClickFunction = {
                        val element = dropdownRef.current.asDynamic()
                        if (active) {
                            element.classList.remove("is-active")
                        } else {
                            element.classList.add("is-active")
                        }
                        setActive(!active)
                    }

                    button(classes = "button") { +"Materials" }
                }
                div(classes = "dropdown-menu") {
                    div(classes = "dropdown-content") {
                        div(classes = "dropdown-item") {
                            table(classes = "table is-striped") {
                                thead {
                                    tr {
                                        th { +"Name" }
                                        th { +"Unit Minimum" }
                                        th { +"Unit Maximum" }
                                        th { +"Quantity" }
                                    }
                                }
                                tbody {
                                    watch.materials.map {
                                        val materialMin = it.datacenterMinimum
                                        val materialMax = (it.datacenterMean + it.datacenterDeviation).toInt()

                                        tr {
                                            key = it.itemId.toString()
                                            td { +it.name }
                                            td { +materialMin.localeFormat() }
                                            td { +materialMax.localeFormat() }
                                            td { +it.quantity.toString() }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@DelicateCoroutinesApi
val watchList = functionComponent<PropsWithChildren> {
    val (watches, setWatches) = useState<Array<Watch>>()
    useEffect(emptyList<dynamic>()) {
        GlobalScope.launch {
            val list = window.fetch("http://localhost:8080/api/v1/watches")
                .await()
                .json()
                .await() as Array<dynamic>

            setWatches(list)
        }
    }

    section(classes = "section") {
        h2(classes = "title") { +"Watched Items" }
    }

    main(classes = "content") {
        table(classes = "table is-striped") {
            thead {
                tr {
                    th { +"Item Name" }
                    th { +"Item Min" }
                    th { +"Item Max" }
                    th { +"Materials Min" }
                    th { +"Materials Max" }
                    th { +"Profit Min" }
                    th { +"Profit Max" }
                    th {}
                }
            }
            tbody {
                watches?.map { child(watchItem) { attrs.watch = it } }
            }
        }
    }
}

fun main() {
    window.onload = {
        require("bulma/css/bulma.css")

        render(document.getElementById("root")) {
            header(classes = "hero is-primary") {
                div(classes = "hero-body") {
                    h1(classes = "title") {
                        +"FFXIV Tools"
                    }
                }
            }
            main(classes = "container") {
                child(watchList) {}
            }
        }
    }
}
