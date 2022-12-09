# pdfCreator
> Author: Haoran Jie 
## Assumptions

- Every line break should be represented by a `.paragraph`
  - In the example, the third paragraph starts without a specific key word (maybe it is because of the change of indent)
    - Hence, in `readin()` I would make adjustments so that following every change of indent there should be a `.paragraph` (except for the first one)
- Every paragraph should directly follow a `InText`
  - Hence, in the `readin()` I would make adjustment to the sequence of input.


## Example 
### Input
```text
.large
My PDF Document
.normal
.paragraph
This is my
.italics
very first
.regular
pdf document, and the output is formatted really well. While this paragraph is not filled, the following one has automatic filling set:
.paragraph
.indent +2
.fill
“Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.”
.nofill
.indent -2
Well that was
.bold
exciting
, good luck!
```
### Output

![](https://s2.loli.net/2022/12/10/RMZiAT683kVxKXu.png)

> The reason that the "good luck" here is in bold is that there is no such .regular between the last two lines of the example input