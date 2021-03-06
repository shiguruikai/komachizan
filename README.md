# 小町算を解くプログラム

小町算を総当りで計算して、求める数と一致した式を出力します。

小町算とは、□1□2□3□4□5□6□7□8□9 = (求める数) という数式の中に、 ＋,−,×,÷,空白 のいずれかを一つずつ入れて正しい数式を完成させるというパズル。

求める数が100の場合、162パターンの解がある。

### 実行例
□1□2□3□4□5□6□7□8□9 = 2017 (＋,−,×,÷,空白) このような式の場合

[Komachizan.kt](src/main/kotlin/komachizankt/Komachizan.kt)

```kotlin
val target = 2017
val digits = "123456789".toCharArray()
val operators = arrayOf("", "+", "-", "*", "/")
val firstOperators = arrayOf("", "-")
```

実行結果

```text
1 : 12+345*6+7-8*9 = 2017
2 : 12+345*6-7*8-9 = 2017
3 : 1+2*34*5*6-7-8-9 = 2017
4 : -1+2345-6*7*8+9 = 2017
5 : -1*2+3+4*567*8/9 = 2017
6 : -1*2+3/4*5*67*8+9 = 2017

処理時間 : 0.913 [sec]
```
