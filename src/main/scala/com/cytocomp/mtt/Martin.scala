// MTT
// J Kyle Medley 2016-2019
package com.cytocomp.mtt
// Algebraic identity and equivalence
// William Martin, ACM 18:4 1971

import scala.math._
import scala.collection.breakOut
import java.util.NoSuchElementException

import com.cytocomp.mtt._
import org.sbml.jsbml.{SBMLReader, SBMLDocument, ASTNode, SBMLError}

abstract class MathExpr {
  def group(u: MathExpr) : String = {
    if (u.prec >= prec) u.toString else s"(${u.toString})"
  }

  def prec() : Int = {
    this match {
      case FUNCTION(u)           => 11
      case FUNCTION_ABS(u)       => 11
      case FUNCTION_ARCCOS(u)    => 11
      case FUNCTION_ARCCOSH(u)   => 11
      case FUNCTION_ARCCOT(u)    => 11
      case FUNCTION_ARCCOTH(u)   => 11
      case FUNCTION_ARCCSC(u)    => 11
      case FUNCTION_ARCCSCH(u)   => 11
      case FUNCTION_ARCSEC(u)    => 11
      case FUNCTION_ARCSECH(u)   => 11
      case FUNCTION_ARCSIN(u)    => 11
      case FUNCTION_ARCSINH(u)   => 11
      case FUNCTION_ARCTAN(u)    => 11
      case FUNCTION_ARCTANH(u)   => 11
      case FUNCTION_CEILING(u)   => 11
      case FUNCTION_COS(u)       => 11
      case FUNCTION_COSH(u)      => 11
      case FUNCTION_COT(u)       => 11
      case FUNCTION_COTH(u)      => 11
      case FUNCTION_CSC(u)       => 11
      case FUNCTION_CSCH(u)      => 11
      case FUNCTION_DELAY(u)     => 11
      case FUNCTION_EXP(u)       => 11
      case FUNCTION_FACTORIAL(u) => 11
      case FUNCTION_FLOOR(u)     => 11
      case FUNCTION_LN(u)        => 11
      case FUNCTION_LOG(u)       => 11
      case FUNCTION_PIECEWISE(u) => 11
      case FUNCTION_POWER(u,v)   => 11
      case FUNCTION_ROOT(u,v)    => 11
      case FUNCTION_SEC(u)       => 11
      case FUNCTION_SECH(u)      => 11
      case FUNCTION_SIN(u)       => 11
      case FUNCTION_SINH(u)      => 11
      case FUNCTION_TAN(u)       => 11
      case FUNCTION_TANH(u)      => 11

      case POWER(u,v)            => 4

      case DIVIDE(u,v)           => 3
      case MULTIPLY(u,v)         => 3

      case SUBTRACT(u,v)         => 2
      case SUM(u,v)         => 2

      case _                     => 10
    }
  }

  override def toString() : String = {
    this match {
      case CONSTANT_E()          => "e"
      case CONSTANT_FALSE()      => "false"
      case CONSTANT_PI()         => "π"
      case CONSTANT_TRUE()       => "true"
      case DIVIDE(u,v)           => group(u) + "/" + group(v)
      case FUNCTION(u)           => "fct" + group(u)
      case FUNCTION_ABS(u)       => "abs" + group(u)
      case FUNCTION_ARCCOS(u)    => "arccos" + group(u)
      case FUNCTION_ARCCOSH(u)   => "arccosh" + group(u)
      case FUNCTION_ARCCOT(u)    => "arccot" + group(u)
      case FUNCTION_ARCCOTH(u)   => "arccoth" + group(u)
      case FUNCTION_ARCCSC(u)    => "arccsc" + group(u)
      case FUNCTION_ARCCSCH(u)   => "arccsch" + group(u)
      case FUNCTION_ARCSEC(u)    => "arcsec" + group(u)
      case FUNCTION_ARCSECH(u)   => "arcsech" + group(u)
      case FUNCTION_ARCSIN(u)    => "arcsin" + group(u)
      case FUNCTION_ARCSINH(u)   => "arcsinh" + group(u)
      case FUNCTION_ARCTAN(u)    => "arctan" + group(u)
      case FUNCTION_ARCTANH(u)   => "arctanh" + group(u)
      case FUNCTION_CEILING(u)   => "ceil" + group(u)
      case FUNCTION_COS(u)       => "cos" + group(u)
      case FUNCTION_COSH(u)      => "cosh" + group(u)
      case FUNCTION_COT(u)       => "cot" + group(u)
      case FUNCTION_COTH(u)      => "coth" + group(u)
      case FUNCTION_CSC(u)       => "csc" + group(u)
      case FUNCTION_CSCH(u)      => "csch" + group(u)
      case FUNCTION_DELAY(u)     => "delay" + group(u)
      case FUNCTION_EXP(u)       => "exp" + group(u)
      case FUNCTION_FACTORIAL(u) => "factorial" + group(u)
      case FUNCTION_FLOOR(u)     => "floor" + group(u)
      case FUNCTION_LN(u)        => "ln" + group(u)
      case FUNCTION_LOG(u)       => "log" + group(u)
      case FUNCTION_PIECEWISE(u) => "piecewise" + group(u)
      case FUNCTION_POWER(u,v)   => "pow" + group(u) + "," + group(v)
      case FUNCTION_ROOT(u,v)    => "root" + group(u)
      case FUNCTION_SEC(u)       => "sec" + group(u)
      case FUNCTION_SECH(u)      => "sech" + group(u)
      case FUNCTION_SIN(u)       => "sin" + group(u)
      case FUNCTION_SINH(u)      => "sinh" + group(u)
      case FUNCTION_TAN(u)       => "tan" + group(u)
      case FUNCTION_TANH(u)      => "tanh" + group(u)
      case INTEGER(x)            => x.toString
      case LAMBDA()              => "lambda"
      case LOGICAL_AND(u,v)      => "and"
      case LOGICAL_NOT(u,v)      => "not"
      case LOGICAL_OR(u,v)       => "or"
      case LOGICAL_XOR(u,v)      => "xor"
      case SUBTRACT(u,v)         => group(u) + "-" + group(v)
      case NAME(s)               => s
      case NAME_AVOGADRO()       => "avogadro"
      case NAME_TIME()           => "time"
      case SUM(u,v)              => group(u) + "+" + group(v)
      case POWER(u,v)            => group(u) + "^" + group(v)
      case REAL(x)               => x.toString
      case REAL_E()              => "e"
      case RELATIONAL_EQ(u,v)    => "eq"
      case RELATIONAL_GEQ(u,v)   => ">="
      case RELATIONAL_GT(u,v)    => ">"
      case RELATIONAL_LEQ(u,v)   => "<="
      case RELATIONAL_LT(u,v)    => "<"
      case RELATIONAL_NEQ(u,v)   => "!="
      case MULTIPLY(u,v)         => group(u) + "*" + group(v)
      case _                     => "???"
    }
  }

  def canonical(bound_vars: Set[String]): MathExpr = {
    this
  }
}

abstract class MathTerminal extends MathExpr {
}

abstract class MathSymbol(s: String) extends MathTerminal {
}

abstract class MathLong(x: Long) extends MathTerminal {
}

abstract class MathDouble(x: Double) extends MathTerminal {
}

abstract class Operator extends MathExpr {

}

abstract class UnaryOp(u: MathExpr) extends Operator {

}

abstract class BinaryOp(u: MathExpr, v: MathExpr) extends Operator {

}

case class CONSTANT_E         ()                          extends MathExpr {
}
case class CONSTANT_FALSE     ()                          extends MathExpr {
}
case class CONSTANT_PI        ()                          extends MathExpr {
}
case class CONSTANT_TRUE      ()                          extends MathExpr {
}
case class DIVIDE           (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class FUNCTION           (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ABS       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCCOS    (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCCOSH   (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCCOT    (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCCOTH   (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCCSC    (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCCSCH   (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCSEC    (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCSECH   (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCSIN    (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCSINH   (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCTAN    (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_ARCTANH   (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_CEILING   (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_COS       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_COSH      (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_COT       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_COTH      (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_CSC       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_CSCH      (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_DELAY     (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_EXP       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_FACTORIAL (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_FLOOR     (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_LN        (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_LOG       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_MAX       (u : MathExpr, v: MathExpr) extends BinaryOp(u, v)  {
}
case class FUNCTION_MIN       (u : MathExpr, v: MathExpr) extends BinaryOp(u, v)  {
}
case class FUNCTION_PIECEWISE (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_POWER     (u : MathExpr, v: MathExpr) extends BinaryOp(u, v)  {
}
case class FUNCTION_ROOT      (u : MathExpr, v: MathExpr) extends BinaryOp(u, v)  {
}
case class FUNCTION_SEC       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_SECH      (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_SIN       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_SINH      (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_TAN       (u : MathExpr)              extends UnaryOp(u)  {
}
case class FUNCTION_TANH      (u : MathExpr)              extends UnaryOp(u)  {
}
case class INTEGER            (x: Long)                   extends MathLong(x) {
}
case class LAMBDA             ()                          extends MathExpr {
}
case class LOGICAL_AND        (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class LOGICAL_NOT        (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class LOGICAL_OR         (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class LOGICAL_XOR        (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class SUBTRACT           (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class NAME(s:String)     ()                          extends MathSymbol(s) {
}
case class NAME_AVOGADRO      ()                          extends MathSymbol("avogadro") {
}
case class NAME_TIME          ()                          extends MathSymbol("time") {
}
case class SUM           (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class POWER              (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class RATIONAL           ()                          extends MathExpr {
}
case class REAL               (x: Double)                 extends MathDouble(x) {
}
case class REAL_E             ()                          extends MathExpr {
}
case class RELATIONAL_EQ      (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class RELATIONAL_GEQ     (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class RELATIONAL_GT      (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class RELATIONAL_LEQ     (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class RELATIONAL_LT      (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class RELATIONAL_NEQ     (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
}
case class MULTIPLY           (u : MathExpr, v: MathExpr) extends BinaryOp(u, v) {
  def is_bound_var(bound_vars: Set[String], x: MathTerminal): Boolean = x match {
    case NAME(s) => bound_vars.contains(s)
    case _ => false
  }

  override def canonical(bound_vars: Set[String]): MathExpr = {
    val newu = u.canonical(bound_vars)
    val newv = v.canonical(bound_vars)
    (newu,newv) match {
      // collapse terminals
      case (newu: MathTerminal, newv: MathTerminal) => {
        val bu = is_bound_var(bound_vars, newu)
        val bv = is_bound_var(bound_vars, newv)
//         println(s"collapse terminals: $this: u: $newu ($bu), v: $newv ($bv)")
        // if both are free then collapse
        if (!bu && !bv)
          NAME("k") // FIXME: make unique name
        // if just one is free then place it first
        else if(!bv)
          MULTIPLY(newv,newu)
        else
          MULTIPLY(newu,newv)
      }
      // use associative law
      case (u: MathTerminal, MULTIPLY(ux: MathTerminal, vx)) => {
        if (!is_bound_var(bound_vars, u) && !is_bound_var(bound_vars, ux))
          MULTIPLY(NAME("k"), vx) // FIXME: make unique name
        else
          MULTIPLY(u,v)
      }
      case _ => MULTIPLY(newu,newv)
    }
  }
}

// missing: CONSTRUCTOR_OTHERWISE, CONSTRUCTOR_PIECE, FUNCTION_QUOTIENT, FUNCTION_RATE_OF, FUNCTION_REM, FUNCTION_SELECTOR, LOGICAL_IMPLIES, PRODUCT, QUALIFIER_BVAR, QUALIFIER_DEGREE, QUALIFIER_LOGBASE, SEMANTICS, SUM, UNKNOWN, VECTOR

trait MEBinaryComp {
  def apply(u : MathExpr, v: MathExpr): MathExpr
}

object Martin {
  def fromSBMLMath(n: ASTNode, k: Int = 0): MathExpr = {
    // Make sure only one child
    def singleChild(): MathExpr = {
      if (n.getChildCount != 1)
        throw new RuntimeException(s"Expected one child, found ${n.getChildCount}")
      else
        fromSBMLMath(n.getChild(0))
    }

    n.getType match {
      case ASTNode.Type.CONSTANT_E => new CONSTANT_E()
      case ASTNode.Type.CONSTANT_FALSE => new CONSTANT_FALSE()
      case ASTNode.Type.CONSTANT_PI => new CONSTANT_PI()
      case ASTNode.Type.CONSTANT_TRUE => new CONSTANT_TRUE()
      case ASTNode.Type.DIVIDE => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          DIVIDE(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION => new FUNCTION(singleChild)
      case ASTNode.Type.FUNCTION_ABS => new FUNCTION_ABS(singleChild)
      case ASTNode.Type.FUNCTION_ARCCOS => new FUNCTION_ARCCOS(singleChild)
      case ASTNode.Type.FUNCTION_ARCCOSH => new FUNCTION_ARCCOSH(singleChild)
      case ASTNode.Type.FUNCTION_ARCCOT => new FUNCTION_ARCCOT(singleChild)
      case ASTNode.Type.FUNCTION_ARCCOTH => new FUNCTION_ARCCOTH(singleChild)
      case ASTNode.Type.FUNCTION_ARCCSC => new FUNCTION_ARCCSC(singleChild)
      case ASTNode.Type.FUNCTION_ARCCSCH => new FUNCTION_ARCCSCH(singleChild)
      case ASTNode.Type.FUNCTION_ARCSEC => new FUNCTION_ARCSEC(singleChild)
      case ASTNode.Type.FUNCTION_ARCSECH => new FUNCTION_ARCSECH(singleChild)
      case ASTNode.Type.FUNCTION_ARCSIN => new FUNCTION_ARCSIN(singleChild)
      case ASTNode.Type.FUNCTION_ARCSINH => new FUNCTION_ARCSINH(singleChild)
      case ASTNode.Type.FUNCTION_ARCTAN => new FUNCTION_ARCTAN(singleChild)
      case ASTNode.Type.FUNCTION_ARCTANH => new FUNCTION_ARCTANH(singleChild)
      case ASTNode.Type.FUNCTION_CEILING => new FUNCTION_CEILING(singleChild)
      case ASTNode.Type.FUNCTION_COS => new FUNCTION_COS(singleChild)
      case ASTNode.Type.FUNCTION_COSH => new FUNCTION_COSH(singleChild)
      case ASTNode.Type.FUNCTION_COT => new FUNCTION_COT(singleChild)
      case ASTNode.Type.FUNCTION_COTH => new FUNCTION_COTH(singleChild)
      case ASTNode.Type.FUNCTION_CSC => new FUNCTION_CSC(singleChild)
      case ASTNode.Type.FUNCTION_CSCH => new FUNCTION_CSCH(singleChild)
      case ASTNode.Type.FUNCTION_DELAY => new FUNCTION_DELAY(singleChild)
      case ASTNode.Type.FUNCTION_EXP => new FUNCTION_EXP(singleChild)
      case ASTNode.Type.FUNCTION_FACTORIAL => new FUNCTION_FACTORIAL(singleChild)
      case ASTNode.Type.FUNCTION_FLOOR => new FUNCTION_FLOOR(singleChild)
      case ASTNode.Type.FUNCTION_LN => new FUNCTION_LN(singleChild)
      case ASTNode.Type.FUNCTION_LOG => new FUNCTION_LOG(singleChild)
      case ASTNode.Type.FUNCTION_MAX => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          FUNCTION_MAX(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION_MIN => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          FUNCTION_MIN(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION_PIECEWISE => new FUNCTION_PIECEWISE(singleChild)
      case ASTNode.Type.FUNCTION_POWER => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          FUNCTION_POWER(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION_ROOT => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          FUNCTION_ROOT(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION_SEC => new FUNCTION_SEC(singleChild)
      case ASTNode.Type.FUNCTION_SECH => new FUNCTION_SECH(singleChild)
      case ASTNode.Type.FUNCTION_SIN => new FUNCTION_SIN(singleChild)
      case ASTNode.Type.FUNCTION_SINH => new FUNCTION_SINH(singleChild)
      case ASTNode.Type.FUNCTION_TAN => new FUNCTION_TAN(singleChild)
      case ASTNode.Type.FUNCTION_TANH => new FUNCTION_TANH(singleChild)
      case ASTNode.Type.INTEGER => new INTEGER(n.getInteger)
      case ASTNode.Type.LAMBDA => new LAMBDA()
      case ASTNode.Type.LOGICAL_AND => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          LOGICAL_AND(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.LOGICAL_NOT => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          LOGICAL_NOT(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.LOGICAL_OR => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          LOGICAL_OR(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.LOGICAL_XOR => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          LOGICAL_XOR(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      // TODO: check if this is how they encode unary minus
      case ASTNode.Type.MINUS => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          SUBTRACT(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.NAME => NAME(n.getName)
      case ASTNode.Type.NAME_AVOGADRO => new NAME_AVOGADRO()
      case ASTNode.Type.NAME_TIME => new NAME_TIME()
      // TODO: check if this is how they encode unary plus
      case ASTNode.Type.PLUS => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          SUM(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.POWER => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          POWER(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.RATIONAL => new RATIONAL()
      case ASTNode.Type.REAL => new REAL(n.getReal)
      case ASTNode.Type.REAL_E => new REAL_E()
      case ASTNode.Type.RELATIONAL_EQ => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          RELATIONAL_EQ(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_GEQ => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          RELATIONAL_GEQ(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_GT => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          RELATIONAL_GT(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_LEQ => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          RELATIONAL_LEQ(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_LT => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          RELATIONAL_LT(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_NEQ => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          RELATIONAL_NEQ(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case ASTNode.Type.TIMES => {
        if (k+1 == n.getChildCount)
          fromSBMLMath(n.getChild(k))
        else
          MULTIPLY(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
      }
      case _ => throw new RuntimeException("Missing conversion for AST node type")
    }
  }
}

object Realization {
  // prime
  val p: Long     = 8589949373L
  // generator
  val alpha: Long =   13560097L
  // val i:   Long    = 5525736173L
  // val e:   Long    = 8364320344L
  // val pi:  Long    =      92136L
  val neg: Long    =      5525736173L
  val inv: Long    =      8364320344L
  // TODO: define reserved range
}

class Hash(symbols: Map[String, Long]) {
  // yields the proper value for a finite field
  def absmod(u: Long, v: Long): Long = {
    if (v < 0)
      throw new RuntimeException("Expected v to be non-negative")
    if (u < 0)
      -u % v
    else
      u % v
  }

  // can we just subtract?
  def subtract(u: MathExpr, v: MathExpr): Long = {
    val p = Realization.p
    val neg = Realization.neg
    absmod(apply(u) + absmod(neg*apply(v), p), p)
  }

  def handleSubtraction(u: MathExpr, v: MathExpr): Long = {
    u match {
      case NAME(s) => {
        v match {
          case NAME(t) => if (s == t) 0 else subtract(u,v)
          case _ => subtract(u,v)
        }
      }
      case _ => subtract(u,v)
    }
  }

  def apply(x: MathExpr): Long = {
    val p = Realization.p
    val alpha = Realization.alpha
    // val i = Realization.i
    // val e = Realization.e
    // val pi = Realization.pi
    val neg = Realization.neg
    val inv = Realization.inv

    // TODO: use reserved range
    val avogadro = 13490846 // made-up
    val time = 328975 // made-up

    // Overflow?
    x match {
      case DIVIDE(u,v) => absmod(apply(u) * inv*absmod(apply(v),p), p)
//       case CONSTANT_E => new CONSTANT_E()
//       case CONSTANT_FALSE => new CONSTANT_FALSE()
//       case CONSTANT_PI => new CONSTANT_PI()
//       case CONSTANT_TRUE => new CONSTANT_TRUE()
//       case DIVIDE => {
//         if (k+1 == n.getChildCount)
//           fromSBMLMath(n.getChild(k))
//         else
//           DIVIDE(fromSBMLMath(n.getChild(k)), fromSBMLMath(n, k+1))
//       }
//       case FUNCTION => new FUNCTION(singleChild)
      case FUNCTION_ABS(u) => abs(apply(u)).toLong // not usually used but need a separate symbol if so
//       case FUNCTION_ARCCOS =>
//       case FUNCTION_ARCCOSH =>
//       case FUNCTION_ARCCOT =>
//       case FUNCTION_ARCCOTH =>
//       case FUNCTION_ARCCSC =>
//       case FUNCTION_ARCCSCH =>
//       case FUNCTION_ARCSEC =>
//       case FUNCTION_ARCSECH =>
//       case FUNCTION_ARCSIN =>
//       case FUNCTION_ARCSINH =>
//       case FUNCTION_ARCTAN =>
//       case FUNCTION_ARCTANH =>
//       case FUNCTION_CEILING =>
//       case FUNCTION_COS =>
//       case FUNCTION_COSH =>
//       case FUNCTION_COT =>
//       case FUNCTION_COTH =>
//       case FUNCTION_CSC =>
//       case FUNCTION_CSCH => csch(u)
      case FUNCTION_DELAY(u) => 989*apply(u) // TODO: make safe
      case FUNCTION_EXP(u) => absmod(exp(apply(u)).toLong, p)
//       case FUNCTION_FACTORIAL => // lol nope
//       case FUNCTION_FLOOR =>
      case FUNCTION_LN(u) => log(apply(u)).toLong
      case FUNCTION_LOG(u) => log10(apply(u)).toLong
      case FUNCTION_MAX(u,v) => max(apply(u), apply(v))
      case FUNCTION_MIN(u,v) => min(apply(u), apply(v))
//       case FUNCTION_PIECEWISE =>
      case FUNCTION_POWER(u,v) => absmod(pow(apply(u), 2*apply(v)).toLong, p)
      case FUNCTION_ROOT(u,v) => pow(apply(u), 1.0/apply(v)).toLong
//       case FUNCTION_SEC(u) => sec(apply(u)).toLong
//       case FUNCTION_SECH(u) => sech(apply(u)).toLong
      case FUNCTION_SIN(u) => sin(apply(u)).toLong
//       case FUNCTION_SINH(u) => sinh(apply(u)).toLong
      case FUNCTION_TAN(u) => tan(apply(u)).toLong
//       case FUNCTION_TANH(u) => tanh(apply(u)).toLong
      // case INTEGER(x) => x*alpha
      case INTEGER(x) => x
      case SUBTRACT(u,v) => handleSubtraction(u,v)
      case NAME(s) => {
        if (symbols contains s)
          symbols(s)
        else
          // just return a default value for arbitrary symbols / parameters
          1
      }
      case NAME_AVOGADRO() => avogadro
      case NAME_TIME() => time
      case SUM(u,v) => absmod(apply(u) + apply(v), p)
      case POWER(u,v) => {
        val v0 = apply(v)
        // if (v0 == 1)
          // apply(u)
        // else
          // absmod(11*pow(apply(u), v0).toLong, p)
        absmod(pow(apply(u), 2*v0).toLong, p)
      }
      case RATIONAL() => throw new RuntimeException("Forget rationals")
      case REAL(x) => (x*alpha).toLong
      case REAL_E() => 1
      case MULTIPLY(u,v) => absmod(apply(u) * apply(v), p)
      case _ => throw new RuntimeException(s"Missing hash for this expression $x")
    }
  }
}

trait Archetype {
  type VarMap = Map[String, Long]
  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]]
  def getHash(): Long
  def getVariables(): VarMap
  def getFormula(): MathExpr
  def getName(): String
}

object ArchetypeMassActionIrrevZeroOrder extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val formula = k
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 0)
      Some(Map())
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Irreversible Zero-Order"
}

object ArchetypeMassActionIrrevUni extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val A: MathTerminal = NAME("A")
  final val formula = MULTIPLY(k, A)
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1,
    "A" -> 2
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 1)
      Some(Map("A"->reactants(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Irreversible Uni"
}

object ArchetypeMassActionIrrevUniNoDepletion extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val A: MathTerminal = NAME("A")
  final val formula = MULTIPLY(k, A)
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1,
    "A" -> 2
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 0 && modifiers.length == 1)
      Some(Map("A"->modifiers(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Irreversible Uni No Depletion"
}

object ArchetypeMassActionRevUniUni extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(MULTIPLY(k, A), MULTIPLY(k, B))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1,
    "A" -> 2,
    "B" -> 3
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 1 && products.length == 1)
      Some(Map("A"->reactants(0), "B"->products(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Reversible Uni-Uni"
}

object ArchetypeMassActionRevUniBi extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val C: MathTerminal = NAME("C")
  final val formula = SUBTRACT(MULTIPLY(k, A), MULTIPLY(k, MULTIPLY(B, C)))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1,
    "A" -> 2,
    "B" -> 3,
    "C" -> 5
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 1 && products.length == 2)
      Some(Map("A"->reactants(0), "B"->products(0), "C"->products(1)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Reversible Uni-Bi"
}

object ArchetypeMassActionRevBiUni extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val C: MathTerminal = NAME("C")
  final val formula = SUBTRACT(MULTIPLY(k, MULTIPLY(A, B)), MULTIPLY(k, C))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1,
    "A" -> 2,
    "B" -> 3,
    "C" -> 5
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 2 && products.length == 1)
      Some(Map("A"->reactants(0), "B"->reactants(1), "C"->products(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Reversible Uni-Bi"
}

object ArchetypeMassActionIrrevBi extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = MULTIPLY(k, MULTIPLY(A, B))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1,
    "A" -> 2,
    "B" -> 3
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 2)
      Some(Map("A"->reactants(0),"B"->reactants(1)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Irreversible Bi"
}

object ArchetypeMassActionIrrevDimer extends Archetype {
  final val k: MathTerminal = NAME("k")
  final val A: MathTerminal = NAME("A")
  final val formula = MULTIPLY(k, MULTIPLY(A, A))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "k" -> 1,
    "A" -> 2
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 2)
      Some(Map("A"->reactants(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Irreversible Dimer"
}

object ArchetypeMassActionRevDimer extends Archetype {
  final val kf: MathTerminal = NAME("kf")
  final val kr: MathTerminal = NAME("kr")
  final val A: MathTerminal = NAME("A")
  final val Adimer: MathTerminal = NAME("Adimer")
  final val formula = SUBTRACT(MULTIPLY(kf, MULTIPLY(A, A)), MULTIPLY(kr, Adimer))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "kf" -> 1,
    "kr" -> 1,
    "A" -> 2,
    "Adimer" -> 3
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 2 && reactants(0) == reactants(1) && products.length == 1)
      Some(Map("A"->reactants(0), "Adimer"->products(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Reversible Dimer"
}

object ArchetypeMassActionRevMonomer extends Archetype {
  final val kf: MathTerminal = NAME("kf")
  final val kr: MathTerminal = NAME("kr")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(MULTIPLY(kf, A), MULTIPLY(kr, MULTIPLY(B, B)))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "kf" -> 1,
    "kr" -> 1,
    "A" -> 2,
    "B" -> 3
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 1 && products.length == 2 && products(0) == products(1))
      Some(Map("A"->reactants(0), "B"->products(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Reversible Monomerization"
}

object ArchetypeMassActionIrrevMichaelisMenten extends Archetype {
  final val kcat: MathTerminal = NAME("kcat")
  final val Km: MathTerminal = NAME("Km")
  final val E: MathTerminal = NAME("E")
  final val S: MathTerminal = NAME("S")
  final val P: MathTerminal = NAME("P")
  final val formula = MULTIPLY(kcat, MULTIPLY(E, DIVIDE(S, SUM(S, Km))))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "kcat" -> 1,
    "Km" -> 1,
    "E" -> 2,
    "S" -> 3,
    "P" -> 5
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 2)
      Some(Map("E"->reactants(0),"S"->reactants(1)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Irreversible Michaelis-Menten"
}

object ArchetypeMassActionIrrevMichaelisMentenNoEnz extends Archetype {
  final val kcat: MathTerminal = NAME("kcat")
  final val Km: MathTerminal = NAME("Km")
  final val S: MathTerminal = NAME("S")
  final val P: MathTerminal = NAME("P")
  final val formula = MULTIPLY(kcat, DIVIDE(S, SUM(S, Km)))
  final val variables = Map[String, Long](
    // arbitrary values
    // TODO: Need to randomize for uniqueness properties to hold
    "kcat" -> 1,
    "Km" -> 2,
    "S" -> 3,
    "P" -> 4
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 1)
      Some(Map("S"->reactants(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Mass-Action Irreversible Michaelis-Menten (No Enz)"
}

object ArchetypeRepressor extends Archetype {
  final val a_tr: MathTerminal = NAME("a_tr")
  final val Km: MathTerminal = NAME("Km")
  final val R: MathTerminal = NAME("R")
  final val formula = MULTIPLY(a_tr, DIVIDE(POWER(Km,INTEGER(2)), SUM(POWER(R,INTEGER(2)), POWER(Km,INTEGER(2)))))
  final val variables = Map[String, Long](
    "a_tr" -> 1,
    // don't use 2
    "Km" -> 3,
    "R" -> 5
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (modifiers.length == 1)
      Some(Map("R"->modifiers(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Repressor"
}

object ArchetypeRepressorBasalGeneric extends Archetype {
  final val a0_tr: MathTerminal = NAME("a0_tr")
  final val a_tr: MathTerminal = NAME("a_tr")
  final val Km: MathTerminal = NAME("Km")
  final val n: MathTerminal = NAME("n")
  final val R: MathTerminal = NAME("R")
  final val formula = SUM(a0_tr, MULTIPLY(a_tr, DIVIDE(POWER(Km,n), SUM(POWER(R,n), POWER(Km,n)))))
  final val variables = Map[String, Long](
    "a0_tr" -> 1,
    "a_tr" -> 1,
    "n" -> 1,
    "Km" -> 1,
    "R" -> 5
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (modifiers.length == 1)
      Some(Map("R"->modifiers(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Repressor Basal Generic"
}

object ArchetypeRepressorBasalGenericDeg extends Archetype {
  final val a0_tr: MathTerminal = NAME("a0_tr")
  final val a_tr: MathTerminal = NAME("a_tr")
  final val kd: MathTerminal = NAME("kd")
  final val Km: MathTerminal = NAME("Km")
  final val n: MathTerminal = NAME("n")
  final val X: MathTerminal = NAME("X")
  final val R: MathTerminal = NAME("R")
  final val formula = SUBTRACT(
    SUM(a0_tr, MULTIPLY(a_tr, DIVIDE(POWER(Km,n), SUM(POWER(R,n), POWER(Km,n))))),
    MULTIPLY(kd, X)
  )
  final val variables = Map[String, Long](
    "a0_tr" -> 1,
    "a_tr" -> 1,
    "n" -> 1,
    "kd" -> 1,
    "Km" -> 1,
    "X" -> 5,
    "R" -> 7
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (products.length == 1 && modifiers.length == 1)
      Some(Map("X"->products(0), "R"->modifiers(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Repressor Basal Generic with Deg"
}

object ArchetypeTranslationWithDeg extends Archetype {
  final val k_tl: MathTerminal = NAME("k_tl")
  final val kd: MathTerminal = NAME("kd")
  final val X: MathTerminal = NAME("X")
  final val PX: MathTerminal = NAME("PX")
  final val formula = SUBTRACT(
    MULTIPLY(k_tl, X),
    MULTIPLY(kd, PX)
  )
  final val variables = Map[String, Long](
    "k_tl" -> 1,
    "kd" -> 1,
    "X" -> 5,
    "PX" -> 7
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (products.length == 1 && modifiers.length == 1)
      Some(Map("PX"->products(0), "X"->modifiers(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Translation with Deg"
}

object ArchetypeBottsMoralesInhIrrev extends Archetype {
  final val kcat: MathTerminal = NAME("kcat")
  final val Km: MathTerminal = NAME("Km")
  final val KI: MathTerminal = NAME("KI")
  final val E: MathTerminal = NAME("E")
  final val S: MathTerminal = NAME("S")
  final val I: MathTerminal = NAME("I")
  final val formula = DIVIDE(
    MULTIPLY(MULTIPLY(kcat, E), DIVIDE(S,Km)),
    SUM(INTEGER(1), SUM(DIVIDE(S,Km), DIVIDE(I,KI)))
  )
  final val variables = Map[String, Long](
    "kcat" -> 1,
    "Km" -> 1,
    "KI" -> 1,
    "E" -> 2,
    "S" -> 3,
    "I" -> 5
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    if (reactants.length == 2 && modifiers.length == 1)
      Some(Map("E"->reactants(0), "S"->reactants(1), "I"->modifiers(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Botts-Morales Inhibited Irreversible"
}

object ArchetypeBottsMoralesInhRev extends Archetype {
  final val Vf: MathTerminal = NAME("Vf")
  final val Ks: MathTerminal = NAME("Ks")
  final val KI: MathTerminal = NAME("KI")
  final val Kp: MathTerminal = NAME("Kp")
  final val Keq: MathTerminal = NAME("Keq")
  final val E: MathTerminal = NAME("E")
  final val S: MathTerminal = NAME("S")
  final val I: MathTerminal = NAME("I")
  final val P: MathTerminal = NAME("P")
  final val formula = DIVIDE(
    MULTIPLY(MULTIPLY(Vf, E), DIVIDE(SUBTRACT(S, DIVIDE(P, Keq)),Ks)),
    SUM(INTEGER(1), SUM(DIVIDE(S,Ks), SUM(DIVIDE(P,Kp), DIVIDE(I,KI))))
  )
  final val variables = Map[String, Long](
    "Vf" -> 1,
    "Ks" -> 1,
    "KI" -> 1,
    "Kp" -> 1,
    "Ks" -> 1,
    "E" -> 2,
    "S" -> 3,
    "P" -> 5,
    "I" -> 7
  )

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }
  final val hash = makeHash(formula)

  def boundVars(reactants: Seq[String], products: Seq[String] = Seq.empty[String], modifiers: Seq[String] = Seq.empty[String]): Option[Map[String, String]] = {
    // enzyme is a reactant
    if (reactants.length == 2 && modifiers.length == 1 && products.length == 2)
      Some(Map("E"->reactants(0), "S"->reactants(1), "I"->modifiers(0), "P"->products(1)))
    // enzyme is a modifier
    else if (reactants.length == 1 && modifiers.length == 2 && products.length == 1)
      Some(Map("S"->reactants(0), "E"->modifiers(0), "I"->modifiers(1), "P"->products(0)))
    else
      None
  }

  def getHash(): Long = { hash }
  def getVariables(): VarMap = { variables }
  def getFormula() = formula
  def getName() = "Botts-Morales Inhibited Reversible"
}

object MapExpression {
  def apply(src: MathExpr, dst: Archetype, bound_vars: Map[String, String]): Boolean = {
    // map the variable name in the source expression to the value in the dest expression
    val bv_map: Map[String, Long] = bound_vars.map{case (x,y) => (y, dst.getVariables()(x))}(breakOut)
    // add mappings for unbound variables in the dest expression
    val composite = bv_map ++ dst.getVariables.filter{ case(v,value) => ! (bound_vars contains v) }

    // compute the hash of the source expression with the bound variable mappings
    val h = new Hash(composite)
    val v = h(src)
    // compare the hash to the archetype
    v == dst.getHash
  }
}

object MapToArchetype {
  def apply(src: MathExpr, reactants: Seq[String], products: Seq[String], modifiers: Seq[String]): Option[Archetype] = {
    for (a <- List(ArchetypeTranslationWithDeg, ArchetypeMassActionIrrevUniNoDepletion, ArchetypeMassActionIrrevZeroOrder, ArchetypeMassActionIrrevUni, ArchetypeMassActionRevUniUni, ArchetypeMassActionRevUniBi, ArchetypeMassActionRevBiUni, ArchetypeMassActionIrrevDimer, ArchetypeMassActionRevDimer, ArchetypeMassActionRevMonomer, ArchetypeMassActionIrrevBi, ArchetypeMassActionIrrevMichaelisMenten, ArchetypeMassActionIrrevMichaelisMentenNoEnz, ArchetypeRepressor, ArchetypeRepressorBasalGeneric, ArchetypeRepressorBasalGenericDeg, ArchetypeBottsMoralesInhIrrev, ArchetypeBottsMoralesInhRev)) {
      val bv = a.boundVars(reactants, products, modifiers) // TODO: permute
      if (!bv.isEmpty) {
        if (MapExpression(src.canonical(bv.get.values toSet), a, bv.get))
          return Some(a)
      }
    }
    None
  }
}

object printMartinSummary {
  def apply(src: MathExpr, reactants: Seq[String], products: Seq[String], modifiers: Seq[String]) = {
    println("Source Expression")
    println(s"  $src")
    for (a <- List(ArchetypeTranslationWithDeg, ArchetypeMassActionIrrevUniNoDepletion, ArchetypeMassActionIrrevZeroOrder, ArchetypeMassActionIrrevUni, ArchetypeMassActionRevUniBi, ArchetypeMassActionRevBiUni, ArchetypeMassActionIrrevDimer, ArchetypeMassActionRevDimer, ArchetypeMassActionRevMonomer, ArchetypeMassActionIrrevBi, ArchetypeMassActionIrrevMichaelisMenten, ArchetypeMassActionIrrevMichaelisMentenNoEnz, ArchetypeRepressor, ArchetypeRepressorBasalGeneric, ArchetypeRepressorBasalGenericDeg, ArchetypeBottsMoralesInhIrrev, ArchetypeBottsMoralesInhRev)) {
      println(a.getName)
      println(s"  ${a.getFormula}")
      println(s"  Dest Hash:   ${a.getHash}")
      val bv = a.boundVars(reactants, products, modifiers)
      if (!bv.isEmpty) {
        val bound_vars = bv.get
        val bv_map: Map[String, Long] = bound_vars.map{case (x,y) => (y, a.getVariables()(x))}(breakOut)
        val composite = bv_map ++ a.getVariables.filter{ case(v,value) => ! (bound_vars contains v) }
//         println(composite)
        val h = new Hash(composite)
        try {
          println(s"  Source Hash: ${h(src.canonical(bound_vars.values toSet))}")
        } catch {
          case e: NoSuchElementException => println("  Cannot compute source hash")
        }
      } else {
        println( "  Cannot compute bound vars")
      }
    }
  }
}
