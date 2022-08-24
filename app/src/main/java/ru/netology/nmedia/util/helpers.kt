package ru.netology.nmedia

fun formatCount(count: Int
):String{
    return when {
        (count < 1000) -> {
            count.toString()
        }
        (count < 1000000) -> {
            formatDigit(count, 1000000, "K")
        }
        (count < 1000000000) -> {
            formatDigit(count, 1000000000, "M")
        }
        else -> {
            ">1B"
        }
    }
}

fun formatDigit(number: Int, digit: Int, suffix: String): String {
    val divider = digit/1000
    val remainder= digit/10000
    return when {
        (number < (divider*10)) -> {
            val str = StringBuilder()
            str.append(number /divider)
            if ((number % divider / remainder) > 0) {
                str.append(".")
                    .append(number % divider / remainder)
            }
            str.append(suffix)
            str.toString()
        }
        (number < digit) -> {
            "${number / divider}$suffix"
        }
        else -> {
            return number.toString()
        }
    }
}
