package komachizankt

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

class Rational(numerator: Long, denominator: Long = 1L) : Comparable<Rational> {

    val numerator: Long
    val denominator: Long

    constructor(numerator: Int, denominator: Int = 1) : this(numerator.toLong(), denominator.toLong())

    init {
        require(denominator != 0L) { "denominator must not be zero" }

        val divisor = gcd(abs(numerator), abs(denominator))
        this.numerator = numerator / divisor
        this.denominator = denominator / divisor
    }

    operator fun plus(that: Rational): Rational =
            Rational(numerator * that.denominator + that.numerator * denominator, denominator * that.denominator)

    operator fun minus(that: Rational): Rational =
            Rational(numerator * that.denominator - that.numerator * denominator, denominator * that.denominator)

    operator fun times(that: Rational): Rational =
            Rational(numerator * that.numerator, denominator * that.denominator)

    operator fun div(that: Rational): Rational =
            Rational(numerator * that.denominator, denominator * that.numerator)

    operator fun rem(that: Rational): Rational =
            (this / that).let { this - (that * Rational(it.numerator / it.denominator)) }

    override fun toString(): String = "$numerator/$denominator"

    override fun equals(other: Any?): Boolean =
            other is Rational && numerator == other.numerator && denominator == other.denominator

    override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

    override fun compareTo(other: Rational): Int =
            (numerator * other.denominator).compareTo(other.numerator * denominator)

    fun toBigDecimal(scale: Int = 8, roundingMode: RoundingMode = RoundingMode.HALF_UP): BigDecimal =
            BigDecimal.valueOf(numerator).divide(BigDecimal.valueOf(denominator), scale, roundingMode)

    private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
}
