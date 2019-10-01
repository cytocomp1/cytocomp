package com.cytocomp.mtt

import org.sbml.jsbml.{SBMLReader, SBMLDocument, ASTNode, SBMLError}

object SBML {

  class Wrapper(val doc: SBMLDocument) {
    def getNumErrors() : Int = {
      doc.getNumErrors() // SBMLError.ERROR
    }
    def getError(k: Int) : SBMLError = {
      doc.getError(k)
    }
    def getNumWarnings() : Int = {
      doc.getNumErrors() // SBMLError.WARNING
    }
  }

  def ASTprec(n: ASTNode) : Int = {
    n.getType match {
      case ASTNode.Type.FUNCTION           => 5
      case ASTNode.Type.FUNCTION_ABS       => 5
      case ASTNode.Type.FUNCTION_ARCCOS    => 5
      case ASTNode.Type.FUNCTION_ARCCOSH   => 5
      case ASTNode.Type.FUNCTION_ARCCOT    => 5
      case ASTNode.Type.FUNCTION_ARCCOTH   => 5
      case ASTNode.Type.FUNCTION_ARCCSC    => 5
      case ASTNode.Type.FUNCTION_ARCCSCH   => 5
      case ASTNode.Type.FUNCTION_ARCSEC    => 5
      case ASTNode.Type.FUNCTION_ARCSECH   => 5
      case ASTNode.Type.FUNCTION_ARCSIN    => 5
      case ASTNode.Type.FUNCTION_ARCSINH   => 5
      case ASTNode.Type.FUNCTION_ARCTAN    => 5
      case ASTNode.Type.FUNCTION_ARCTANH   => 5
      case ASTNode.Type.FUNCTION_CEILING   => 5
      case ASTNode.Type.FUNCTION_COS       => 5
      case ASTNode.Type.FUNCTION_COSH      => 5
      case ASTNode.Type.FUNCTION_COT       => 5
      case ASTNode.Type.FUNCTION_COTH      => 5
      case ASTNode.Type.FUNCTION_CSC       => 5
      case ASTNode.Type.FUNCTION_CSCH      => 5
      case ASTNode.Type.FUNCTION_DELAY     => 5
      case ASTNode.Type.FUNCTION_EXP       => 5
      case ASTNode.Type.FUNCTION_FACTORIAL => 5
      case ASTNode.Type.FUNCTION_FLOOR     => 5
      case ASTNode.Type.FUNCTION_LN        => 5
      case ASTNode.Type.FUNCTION_LOG       => 5
      case ASTNode.Type.FUNCTION_PIECEWISE => 5
      case ASTNode.Type.FUNCTION_POWER     => 5
      case ASTNode.Type.FUNCTION_ROOT      => 5
      case ASTNode.Type.FUNCTION_SEC       => 5
      case ASTNode.Type.FUNCTION_SECH      => 5
      case ASTNode.Type.FUNCTION_SIN       => 5
      case ASTNode.Type.FUNCTION_SINH      => 5
      case ASTNode.Type.FUNCTION_TAN       => 5
      case ASTNode.Type.FUNCTION_TANH      => 5
      case ASTNode.Type.LAMBDA             => 5

      case ASTNode.Type.POWER              => 4

      case ASTNode.Type.DIVIDE             => 3
      case ASTNode.Type.TIMES              => 3

      case ASTNode.Type.MINUS              => 2
      case ASTNode.Type.PLUS               => 2

      case _                               => 10
    }
  }

  def GroupASTExpr(n: ASTNode, p: Int) : String = {
    if (ASTprec(n) > p) ASTtoStr(n) else s"(${ASTtoStr(n)})"
  }

  def ASTtoStr(n: ASTNode) : String = {
    lazy val p = ASTprec(n)
    lazy val recurse = (x: ASTNode) => GroupASTExpr(x, p)
    // Make sure only one child
    lazy val singleChild = () => {
      if (n.getChildCount > 1) throw new RuntimeException(s"Expected one child, found ${n.getChildCount}") else GroupASTExpr(n.getChild(0), p)
    }

    n.getType match {
      case ASTNode.Type.CONSTANT_E                          => "e"
      case ASTNode.Type.CONSTANT_FALSE                      => "false"
      case ASTNode.Type.CONSTANT_PI                         => "π"
      case ASTNode.Type.CONSTANT_TRUE                       => "true"
      case ASTNode.Type.DIVIDE                              => (for (k <- 0 until n.getChildCount) yield recurse(n.getChild(k))) mkString("/")
      case ASTNode.Type.FUNCTION                            => "fct" + singleChild
      case ASTNode.Type.FUNCTION_ABS                        => "abs" + singleChild
      case ASTNode.Type.FUNCTION_ARCCOS                     => "arccos" + singleChild
      case ASTNode.Type.FUNCTION_ARCCOSH                    => "arccosh" + singleChild
      case ASTNode.Type.FUNCTION_ARCCOT                     => "arccot" + singleChild
      case ASTNode.Type.FUNCTION_ARCCOTH                    => "arccoth" + singleChild
      case ASTNode.Type.FUNCTION_ARCCSC                     => "arccsc" + singleChild
      case ASTNode.Type.FUNCTION_ARCCSCH                    => "arccsch" + singleChild
      case ASTNode.Type.FUNCTION_ARCSEC                     => "arcsec" + singleChild
      case ASTNode.Type.FUNCTION_ARCSECH                    => "arcsech" + singleChild
      case ASTNode.Type.FUNCTION_ARCSIN                     => "arcsin" + singleChild
      case ASTNode.Type.FUNCTION_ARCSINH                    => "arcsinh" + singleChild
      case ASTNode.Type.FUNCTION_ARCTAN                     => "arctan" + singleChild
      case ASTNode.Type.FUNCTION_ARCTANH                    => "arctanh" + singleChild
      case ASTNode.Type.FUNCTION_CEILING                    => "ceil" + singleChild
      case ASTNode.Type.FUNCTION_COS                        => "cos" + singleChild
      case ASTNode.Type.FUNCTION_COSH                       => "cosh" + singleChild
      case ASTNode.Type.FUNCTION_COT                        => "cot" + singleChild
      case ASTNode.Type.FUNCTION_COTH                       => "coth" + singleChild
      case ASTNode.Type.FUNCTION_CSC                        => "csc" + singleChild
      case ASTNode.Type.FUNCTION_CSCH                       => "csch" + singleChild
      case ASTNode.Type.FUNCTION_DELAY                      => "delay" + singleChild
      case ASTNode.Type.FUNCTION_EXP                        => "exp" + singleChild
      case ASTNode.Type.FUNCTION_FACTORIAL                  => "factorial" + singleChild
      case ASTNode.Type.FUNCTION_FLOOR                      => "floor" + singleChild
      case ASTNode.Type.FUNCTION_LN                         => "ln" + singleChild
      case ASTNode.Type.FUNCTION_LOG                        => "log" + singleChild
      case ASTNode.Type.FUNCTION_PIECEWISE                  => "piecewise" + singleChild
      case ASTNode.Type.FUNCTION_POWER                      => "pow" + singleChild
      case ASTNode.Type.FUNCTION_ROOT                       => "root" + singleChild
      case ASTNode.Type.FUNCTION_SEC                        => "sec" + singleChild
      case ASTNode.Type.FUNCTION_SECH                       => "sech" + singleChild
      case ASTNode.Type.FUNCTION_SIN                        => "sin" + singleChild
      case ASTNode.Type.FUNCTION_SINH                       => "sinh" + singleChild
      case ASTNode.Type.FUNCTION_TAN                        => "tan" + singleChild
      case ASTNode.Type.FUNCTION_TANH                       => "tanh" + singleChild
      case ASTNode.Type.INTEGER                             => "int"
      case ASTNode.Type.LAMBDA                              => "lambda"
      case ASTNode.Type.LOGICAL_AND                         => "and"
      case ASTNode.Type.LOGICAL_NOT                         => "not"
      case ASTNode.Type.LOGICAL_OR                          => "or"
      case ASTNode.Type.LOGICAL_XOR                         => "xor"
      // TODO: check if this is how they encode unary minus
      case ASTNode.Type.MINUS                               => (for (k <- 0 until n.getChildCount) yield ASTtoStr(n.getChild(k))) mkString("-")
      case ASTNode.Type.NAME                                => n.getName
      case ASTNode.Type.NAME_AVOGADRO                       => "avogadro"
      case ASTNode.Type.NAME_TIME                           => "time"
      // TODO: check if this is how they encode unary plus
      case ASTNode.Type.PLUS                                => (for (k <- 0 until n.getChildCount) yield ASTtoStr(n.getChild(k))) mkString("+")
      case ASTNode.Type.POWER                               => (for (k <- 0 until n.getChildCount) yield ASTtoStr(n.getChild(k))) mkString("^")
      case ASTNode.Type.RATIONAL                            => "rational"
      case ASTNode.Type.REAL                                => "real"
      case ASTNode.Type.REAL_E                              => "real_e"
      case ASTNode.Type.RELATIONAL_EQ                       => "eq"
      case ASTNode.Type.RELATIONAL_GEQ                      => ">="
      case ASTNode.Type.RELATIONAL_GT                       => ">"
      case ASTNode.Type.RELATIONAL_LEQ                      => "<="
      case ASTNode.Type.RELATIONAL_LT                       => "<"
      case ASTNode.Type.RELATIONAL_NEQ                      => "!="
      case ASTNode.Type.TIMES                               => (for (k <- 0 until n.getChildCount) yield ASTtoStr(n.getChild(k))) mkString("*")
      case _                                                => "???"
    }
  }

  def readFile(sbmlfile: String) : Wrapper = {
    val reader = new SBMLReader
    val doc = reader.readSBML(sbmlfile)
    return new Wrapper(doc)
  }
}
