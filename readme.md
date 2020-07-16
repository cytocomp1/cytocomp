[![DOI](https://zenodo.org/badge/263393844.svg)](https://zenodo.org/badge/latestdoi/263393844)

# Introduction

This repository contains a compiler for cytomorphic chips developed by the Sarpeshlar lab at Dartmouth. This compiler essentially converts Systems Biology Markup Language (SBML) into a hardware configuration, similar to VLSI technologies for digital hardware.

## Building

First install `sbt` and ensure you have it on your path. Compile the project with:

```
sbt compile
```

The cytomorphic compiler can be used either from the command line or as a [Py4J server](https://www.py4j.org/) (and can thus be called from Python inside Jupyter notebooks, see `notebooks`). 

### Recommended Approach: Working with Jupyter Notebooks using Py4J

To use the Py4J server mode, first run the above `sbt compile` command, then run:

```
sbt ~run server
```

See the `notebooks` directory for examples for how to use the compiler's API.

:warning: Some shells treat (e.g. zsh) the tilde `~` as a special character. In this case run:

```
sbt \~run server
```

In order to run the included notebook examples, the `tellurium` environment is required, as described below.

### Model input via Antimony

[Antimony](https://sourceforge.net/projects/antimony/) is a short-hand language for SBML. The cytomorphic compiler can accept Antimony as input instead of SBML provided that you have the `antimony` Python module installed and that it is on the path that Python searches for modules (in other words, running `python3 -c 'import antimony` in the terminal should produce no errors). Additionally, some notebook examples require [Tellurium](https://www.github.com/sys-bio/tellurium). These have been tested only on Python 3.6. You can install both Tellurium and Antimony by simply using the `requirements.txt` file in this repository:

#### Install Antimony / Tellurium using requirements.txt

```
pip install -r requirements.txt
```

:warning: These notebooks have only been tested with Python 3.6. You can obtain Python 3.6 through `conda`, via the [python.org](https://www.python.org/) website on most platforms, or by building from source.

Alternatively, you can use pipenv.

#### Install Antimony / Tellurium using pipenv

Run:

```
pipenv install
```

Then, before running any other commands, run:

```
pipenv shell
```

For more information on how to use Pipenv, see the [Pipenv homepage](https://pipenv.pypa.io/en/latest/).

### Test your setup

Assuming that the setup has completed correctly, you should be able to run:

```
PYTHONPATH=/path/to/cytocomp/python python3.6 -c 'import tellurium; import mtt'
```

If this command works you should be able to run Jupyter:

```
PYTHONPATH=/path/to/cytocomp/python python3.6 -m jupyter
```

and open any of the noteboks in `notebooks`.

## Misc. Design Documents

A design plan is available [here](https://docs.google.com/document/d/1Di-Arw7D3oA38utko94PaDOYi06F3X9ToWq-YKgw0DQ/edit?usp=sharing).

## Misc. Notes
* The code is written in Scala 2.11, so it should be possible to wrap it up in a .jar file for use with MATLAB. The 2.11 version was an explicit design choice, since 2.12 requires JRE 1.8 whereas MATLAB does not yet support JRE 1.8. In plain english, this means that you would not be able to use Scala 2.12 code with MATLAB.

## References
* Woo, Sung Sik, "Fast Simulation of Stochastic Biochemical Reaction Networks on Cytomorphic Chips." PhD thesis, Massachusetts Instite of Technology, Cambridge MA 02139-4307, 2016.
