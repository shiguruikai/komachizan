package komachizanj;

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

    public Rational(long numerator) {
        this(numerator, 1);
    }

    public Rational(long numerator, long denominator) {
        if (denominator == 0L) {
            throw new IllegalArgumentException("denominator must not be zero");
        }

        long divisor = gcd(Math.abs(numerator), Math.abs(denominator));
        this.numerator = numerator / divisor;
        this.denominator = denominator / divisor;
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

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rational rational = (Rational) o;
        return numerator == rational.numerator &&
                denominator == rational.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public int compareTo(Rational other) {
        Objects.requireNonNull(other);
        return Long.compare(numerator * other.denominator, other.numerator * denominator);
    }

    private long gcd(long a, long b) {
        return b == 0L ? a : gcd(b, a % b);
    }
}
