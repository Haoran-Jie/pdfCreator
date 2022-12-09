# pdfCreator
> Haoran Jie 
## Assumptions

- Every line break should be represented by a `.paragraph`
  - In the example, the third paragraph starts without a specific key word (maybe it is because of the change of indent)
    - Hence, in `readin()` I would make adjustments so that following every change of indent there should be a `.paragraph` (except for the first one)
- Every paragraph should directly follow a `InText`
  - Hence, in the `readin()` I would make adjustment to the sequence of input.