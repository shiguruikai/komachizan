package komachizankt

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class RationalBig(numerator: BigInteger, denominator: BigInteger = BigInteger.ONE) : Comparable<RationalBig> {

    constructor(numerator: Long, denominator: Long) :
            this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator))

    private val divisor = numerator.gcd(denominator)
    val numerator: BigInteger = numerator / divisor
    val denominator: BigInteger = denominator / divisor

    init {
        require(denominator.compareTo(BigInteger.ZERO) != 0) { "denominator must not be zero" }
    }

    operator fun plus(that: RationalBig): RationalBig =
            RationalBig(numerator * that.denominator + that.numerator * denominator, denominator * that.denominator)

    operator fun minus(that: RationalBig): RationalBig =
            RationalBig(numerator * that.denominator - that.numerator * denominator, denominator * that.denominator)

    operator fun times(that: RationalBig): RationalBig =
            RationalBig(numerator * that.numerator, denominator * that.denominator)

    operator fun div(that: RationalBig): RationalBig =
            RationalBig(numerator * that.denominator, denominator * that.numerator)

    operator fun rem(that: RationalBig): RationalBig =
            (this / that).let { this - (that * RationalBig(it.numerator / it.denominator)) }

    fun toBigDecimal(scale: Int = 8, roundingMode: RoundingMode = RoundingMode.DOWN): BigDecimal =
            BigDecimal(numerator).divide(BigDecimal(denominator), scale, roundingMode)

    override fun toString(): String = "$numerator/$denominator"

    override fun equals(other: Any?): Boolean =
            other is RationalBig && numerator == other.numerator && denominator == other.denominator

    override fun hashCode(): Int = 31 * numerator.hashCode() + denominator.hashCode()

    override fun compareTo(other: RationalBig): Int =
            (numerator * other.denominator).compareTo(other.numerator * denominator)
}
