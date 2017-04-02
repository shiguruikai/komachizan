package komachizanj;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Komachizan {

    private static final Map<Character, Integer> operatorToRank = new HashMap<>();

    static {
        operatorToRank.put('(', 3);
        operatorToRank.put('*', 2);
        operatorToRank.put('/', 2);
        operatorToRank.put('+', 1);
        operatorToRank.put('-', 1);
        operatorToRank.put(')', 0);
    }

    private static BigDecimal calculate(String expression) {

        ArrayDeque<Character> opStack = new ArrayDeque<>();
        ArrayDeque<Rational> varStack = new ArrayDeque<>();
        StringBuilder num = new StringBuilder();
        boolean enablePlusOrMinusSign = false;
        String expr = "(" + expression + ")";

        for (char it : expr.toCharArray()) {
            if ('0' <= it && it <= '9' || enablePlusOrMinusSign && (it == '+' || it == '-')) {
                enablePlusOrMinusSign = false;
                num.append(it);
            } else {
                enablePlusOrMinusSign = it == '(';

                if (num.length() != 0) {
                    varStack.push(new Rational(Long.parseLong(num.toString()), 1));
                    num.setLength(0);
                }

                while (!opStack.isEmpty() && opStack.peek() != '(' && operatorToRank.get(opStack.peek()) >= operatorToRank.get(it)) {
                    Character op = opStack.pop();
                    Rational a = varStack.pop();
                    Rational b = varStack.pop();
                    switch (op) {
                        case '*':
                            varStack.push(b.times(a));
                            break;
                        case '/':
                            if (a.getNumerator() != 0) {
                                varStack.push(b.div(a));
                            } else {
                                throw new IllegalArgumentException("division by zero : " + expression);
                            }
                            break;
                        case '+':
                            varStack.push(b.plus(a));
                            break;
                        case '-':
                            varStack.push(b.minus(a));
                            break;
                        default:
                            throw new IllegalArgumentException("syntax error : " + expression);
                    }
                }

                if (it == ')') {
                    opStack.pop();
                } else {
                    opStack.push(it);
                }
            }
        }

        if (opStack.isEmpty() && varStack.size() == 1) {
            return varStack.pop().toBigDecimal();
        } else {
            throw new IllegalArgumentException("syntax error : " + expression);
        }
    }

    private static void komachizan() {

        final String target = "2017";
        final Character[] digits = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        final String[] operators = {"", "+", "-", "*", "/"};
        final String[] firstOperators = {"", "-"};

        List<String> first = Arrays.stream(firstOperators).map(op -> op + digits[0]).collect(Collectors.toList());

        List<String> expressions = Arrays.stream(digits)
                .skip(1)
                .map(num -> Arrays.stream(operators).map(op -> op + num).collect(Collectors.toList()))
                .reduce(first, (acc, list) -> acc.stream().flatMap(a -> list.stream().map(b -> a + b)).collect(Collectors.toList()));

        final BigDecimal ans = new BigDecimal(target);

        AtomicInteger cnt = new AtomicInteger();

        expressions
                .parallelStream()
                .filter(it -> calculate(it).compareTo(ans) == 0)
                .collect(Collectors.toList())
                .forEach(it -> {
                    System.out.println(cnt.incrementAndGet() + " : " + it + " = " + target);
                });
    }

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        komachizan();
        long end = System.currentTimeMillis();

        System.out.printf("%n処理時間 : %.3f [sec]%n", (end - start) / 1000.0);
    }
}
