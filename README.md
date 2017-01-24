This fork of DaisyDiff [daisydiff.github.io](https://daisydiff.github.io/) is specialized for library ("embedded") use. It also removes the HTML diffing,
as we are interested only plain text diffing.

# Histogram diff support

We have repackaged HistogramDiff algorithm from JGit. It seems more promising than the LCS/RangeDifferencer algorithms. Also all unnecessary runtime dependencies have been dropped (or repackaged - where expedient).


# Acknowledgements

 * upstream: [daisydiff.github.io](https://daisydiff.github.io/)
 * Guy Van den Broeck <guy@guyvdb.eu>
 * Daniel Dickison
 * Antoine Taillefer
 * Thomas Roger
