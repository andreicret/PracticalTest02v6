package ro.pub.cs.systems.eim.practicaltest02v6.general

data class CurrencyInformation(
    val currency : String,

) {
    // MODIFICARE: Folosim \n (New Line) in loc de virgula
    override fun toString(): String {
        return "currency: $currency\n"
    }
}