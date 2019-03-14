package komachizanj;

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

    private static Rational calculate(String expression) {

        ArrayDeque<Character> opStack = new ArrayDeque<>();
        ArrayDeque<Rational> varStack = new ArrayDeque<>();
        StringBuilder number = new StringBuilder();
        boolean plusOrMinusSign = false;
        String expr = "(" + expression + ")";

        try {
            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);

                if ('0' <= c && c <= '9' || plusOrMinusSign && (c == '+' || c == '-')) {
                    plusOrMinusSign = false;
                    number.append(c);
                    continue;
                }

                plusOrMinusSign = c == '(';

                if (number.length() > 0) {
                    varStack.push(new Rational(Long.parseLong(number.toString())));
                    number.setLength(0);
                }

                while (!opStack.isEmpty()
                        && opStack.peek() != '('
                        && operatorToRank.get(opStack.peek()) >= operatorToRank.get(c)) {
                    Character op = opStack.pop();
                    Rational a = varStack.pop();
                    Rational b = varStack.pop();
                    switch (op) {
                        case '*':
                            varStack.push(b.times(a));
                            break;
                        case '/':
                            if (a.getNumerator() == 0) {
                                throw new ArithmeticException("division by zero : " + expression);
                            }
                            varStack.push(b.div(a));
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

                if (c == ')') {
                    opStack.pop();
                } else {
                    opStack.push(c);
                }
            }

            return varStack.pop();

        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("syntax error : $expression");
        }
    }

    private static void komachizan() {

        final int target = 2017;
        final Character[] digits = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        final String[] operators = {"", "+", "-", "*", "/"};
        final String[] firstOperators = {"", "-"};

        final List<String> first = Arrays.stream(firstOperators).map(op -> op + digits[0]).collect(Collectors.toList());

        final List<String> expressions = Arrays.stream(digits)
                .skip(1)
                .map(num -> Arrays.stream(operators).map(op -> op + num).collect(Collectors.toList()))
                .reduce(first, (acc, list) -> acc.stream().flatMap(a -> list.stream().map(b -> a + b)).collect(Collectors.toList()));

        final Rational answer = new Rational(target);

        final AtomicInteger cnt = new AtomicInteger();

        expressions
                .parallelStream()
                .filter(it -> calculate(it).compareTo(answer) == 0)
                .collect(Collectors.toList())
                .forEach(it -> System.out.println(cnt.incrementAndGet() + " : " + it + " = " + target));
    }

    public static void main(String[] args) {

        final long start = System.currentTimeMillis();
        komachizan();
        final long end = System.currentTimeMillis();

        System.out.printf("%n処理時間 : %.3f [sec]%n", (end - start) / 1000.0);
    }
}
