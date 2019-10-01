# Introduction

This repository hosts the code for the Model Topology Toolkit (MTT), a compiler for cytomorphic hardware (analogous to a HDL for digital logic). MTT essentially converts Systems Biology Markup Language (SBML) into a hardware configuration which can be used to program cytomorphic hardware

## Design Documents

A design plan is available [here](https://docs.google.com/document/d/1Di-Arw7D3oA38utko94PaDOYi06F3X9ToWq-YKgw0DQ/edit?usp=sharing).

## Misc. Notes
* The code is written in Scala 2.11, so it should be possible to wrap it up in a .jar file for use with MATLAB. The 2.11 version was an explicit design choice, since 2.12 requires JRE 1.8 whereas MATLAB does not yet support JRE 1.8. In plain english, this means that you would not be able to use Scala 2.12 code with MATLAB.

## References
* Woo, Sung Sik, "Fast Simulation of Stochastic Biochemical Reaction Networks on Cytomorphic Chips." PhD thesis, Massachusetts Instite of Technology, Cambridge MA 02139-4307, 2016.
* Next reference...
