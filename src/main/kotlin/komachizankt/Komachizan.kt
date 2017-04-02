package komachizankt

import java.math.BigDecimal
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
fun calculate(expression: String): BigDecimal {

    val opStack = ArrayDeque<Char>()
    val varStack = ArrayDeque<Rational>()
    val num = StringBuilder()
    var enablePlusOrMinusSign = false
    val expr = "($expression)"

    expr.forEach {
        if (it in '0'..'9' || enablePlusOrMinusSign && (it == '+' || it == '-')) {
            enablePlusOrMinusSign = false
            num.append(it)
        } else {
            enablePlusOrMinusSign = it == '('

            if (num.isNotEmpty()) {
                varStack.push(Rational(num.toString().toLong()))
                num.setLength(0)
            }

            while (opStack.isNotEmpty() && opStack.peek() != '(' && operatorToRank[opStack.peek()]!! >= operatorToRank [it]!!) {
                val op = opStack.pop()
                val a = varStack.pop()
                val b = varStack.pop()
                when (op) {
                    '*' -> varStack.push(b * a)
                    '/' -> {
                        require(a.numerator != 0L) { "division by zero : $expression" }
                        varStack.push(b / a)
                    }
                    '+' -> varStack.push(b + a)
                    '-' -> varStack.push(b - a)
                    else -> error("syntax error : $expression")
                }
            }

            if (it == ')') {
                opStack.pop()
            } else {
                opStack.push(it)
            }
        }
    }

    require(opStack.isEmpty() && varStack.size == 1) { "syntax error : $expression" }
    return varStack.pop().toBigDecimal()
}

fun komachizan() {

    val target: String = "2017"
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

    val ans = BigDecimal(target)

    expressions
            .parallelStream()
            .filter { calculate(it).compareTo(ans) == 0 }
            .collect(Collectors.toList())
            .forEachIndexed { index, it ->
                println("${index + 1} : $it = $target")
            }
}

fun main(args: Array<String>) {

    val elapsed = measureTimeMillis {
        komachizan()
    }

    println("%n処理時間 : %.3f [sec]".format(elapsed / 1000.0))
}
