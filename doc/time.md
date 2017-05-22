# 中文时间识别

## 正则表达式

代码中用到了比较多的look ahead和look behind正则，原理如下

### Examples
[Look Ahead&Look Behind](http://stackoverflow.com/questions/2973436/regex-lookahead-lookbehind-and-atomic-groups)
Given the string foobarbarfoo:
```
bar(?=bar)     finds the 1st bar ("bar" which has "bar" after it)
bar(?!bar)     finds the 2nd bar ("bar" which does not have "bar" after it)
(?<=foo)bar    finds the 1st bar ("bar" which has "foo" before it)
(?<!foo)bar    finds the 2nd bar ("bar" which does not have "foo" before it)
```
You can also combine them:

```
(?<=foo)bar(?=bar)    finds the 1st bar ("bar" with "foo" before it and "bar" after it)
```
### Definitions

Look ahead positive (?=)

Find expression A where expression B follows:

A(?=B)
Look ahead negative (?!)

Find expression A where expression B does not follow:

A(?!B)
Look behind positive (?<=)

Find expression A where expression B precedes:

(?<=B)A
Look behind negative (?<!)

Find expression A where expression B does not precede:

(?<!B)A