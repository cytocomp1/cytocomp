# Introduction

This repository contains a compiler for cytomorphic chips developed by the Sarpeshlar lab at Dartmouth. This compiler essentially converts Systems Biology Markup Language (SBML) into a hardware configuration, similar to VLSI technologies for digital hardware.

## Building

First install `sbt` and ensure you have it on your path. Compile the project with:

```
sbt compile
```

The cytomorphic compiler can be used either from the command line or as a [Py4J server](https://www.py4j.org/) (and can thus be called from Python inside Jupyter notebooks, see `notebooks`). To use the Py4J server mode:

```
sbt ~run server
```

See the `notebooks` directory for examples for how to use the compiler's API.

## Design Documents

A design plan is available [here](https://docs.google.com/document/d/1Di-Arw7D3oA38utko94PaDOYi06F3X9ToWq-YKgw0DQ/edit?usp=sharing).

## Misc. Notes
* The code is written in Scala 2.11, so it should be possible to wrap it up in a .jar file for use with MATLAB. The 2.11 version was an explicit design choice, since 2.12 requires JRE 1.8 whereas MATLAB does not yet support JRE 1.8. In plain english, this means that you would not be able to use Scala 2.12 code with MATLAB.

## References
* Woo, Sung Sik, "Fast Simulation of Stochastic Biochemical Reaction Networks on Cytomorphic Chips." PhD thesis, Massachusetts Instite of Technology, Cambridge MA 02139-4307, 2016.
