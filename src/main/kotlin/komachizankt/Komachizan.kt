package komachizankt

import java.util.*
import java.util.stream.Collectors
import kotlin.system.measureTimeMillis

val operatorToRank: Map<Char, Int> = mapOf(
        '(' to 3,
        '*' to 2,
        '/' to 2,
        '+' to 1,
        '-' to 1,
        ')' to 0
)

/**
 * 数式を計算します。数式は整数と四則演算子(+ - * /)と丸括弧のみ使えます。
 * （例）12+3/(-45)*67/(8+90)
 * @param expression 数式の文字列。
 * @return 計算結果。
 */
fun calculate(expression: String): Rational {

    val opStack = ArrayDeque<Char>()
    val varStack = ArrayDeque<Rational>()
    val number = StringBuilder()
    var plusOrMinusSign = false
    val expr = "($expression)"

    try {
        for (c in expr) {
            if (c in '0'..'9' || plusOrMinusSign && (c == '+' || c == '-')) {
                plusOrMinusSign = false
                number.append(c)
                continue
            }

            plusOrMinusSign = c == '('

            if (number.isNotEmpty()) {
                varStack.push(Rational(number.toString().toLong()))
                number.clear()
            }

            while (opStack.isNotEmpty()
                    && opStack.peek() != '('
                    && requireNotNull(operatorToRank[opStack.peek()]) >= requireNotNull(operatorToRank[c])
            ) {
                val op = opStack.pop()
                val a = varStack.pop()
                val b = varStack.pop()
                when (op) {
                    '*' -> varStack.push(b * a)
                    '/' -> {
                        if (a.numerator == 0L) {
                            throw ArithmeticException("division by zero : $expression")
                        }
                        varStack.push(b / a)
                    }
                    '+' -> varStack.push(b + a)
                    '-' -> varStack.push(b - a)
                    else -> throw IllegalArgumentException("syntax error : $expression")
                }
            }

            if (c == ')') {
                opStack.pop()
            } else {
                opStack.push(c)
            }
        }

        return varStack.pop()

    } catch (e: NoSuchElementException) {
        throw IllegalArgumentException("syntax error : $expression")
    }
}

fun komachizan() {

    val target = 2017
    val digits = "123456789".toCharArray()
    val operators = arrayOf("", "+", "-", "*", "/")
    val firstOperators = arrayOf("", "-")

    // 数字と演算子の組み合わせ
    // [[1, -1], [2, +2, -2, *2, /2], … [9, +9, -9, *9, /9]]]
    val comb: List<List<String>> =
            listOf(firstOperators.map { op -> op + digits.first() }) +
                    digits.drop(1).map { num -> operators.map { op -> "$op$num" } }

    val expressions: List<String> = comb.reduce { acc, list ->
        acc.flatMap { a -> list.map { b -> "$a$b" } }
    }

    val answer = Rational(target)

    expressions
            .parallelStream()
            .filter { calculate(it).compareTo(answer) == 0 }
            .collect(Collectors.toList())
            .forEachIndexed { index, it ->
                println("${index + 1} : $it = $target")
            }
}

fun main() {

    val elapsed = measureTimeMillis {
        komachizan()
    }

    println("%n処理時間 : %.3f [sec]".format(elapsed / 1000.0))
}
