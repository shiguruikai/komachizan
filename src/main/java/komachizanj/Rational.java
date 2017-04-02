package komachizanj;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Rational implements Comparable<Rational> {

    private final long numerator;
    private final long denominator;

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public Rational(long numerator, long denominator) {
        if (denominator != 0L) {
            long divisor = gcd(Math.abs(numerator), Math.abs(denominator));
            this.numerator = numerator / divisor;
            this.denominator = denominator / divisor;
        } else {
            throw new IllegalArgumentException("denominator must not be zero");
        }
    }

    public Rational plus(Rational that) {
        return new Rational(numerator * that.denominator + that.numerator * denominator, denominator * that.denominator);
    }

    public Rational minus(Rational that) {
        return new Rational(numerator * that.denominator - that.numerator * denominator, denominator * that.denominator);
    }

    public Rational times(Rational that) {
        return new Rational(numerator * that.numerator, denominator * that.denominator);
    }

    public Rational div(Rational that) {
        return new Rational(numerator * that.denominator, denominator * that.numerator);
    }

    public Rational rem(Rational that) {
        Rational r = div(that);
        return minus(that.times(new Rational(r.numerator / r.denominator, 1)));
    }

    public BigDecimal toBigDecimal() {
        return toBigDecimal(8, RoundingMode.DOWN);
    }

    public BigDecimal toBigDecimal(int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(numerator).divide(BigDecimal.valueOf(denominator), scale, roundingMode);
    }

    private long gcd(long a, long b) {
        if (b == 0L) return a;
        else return gcd(b, a % b);
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public int compareTo(@NotNull Rational other) {
        Objects.requireNonNull(other);
        return new Long(numerator * other.denominator).compareTo(other.numerator * denominator);
    }
}
