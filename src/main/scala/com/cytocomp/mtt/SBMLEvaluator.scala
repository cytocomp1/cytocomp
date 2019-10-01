// J Kyle Medley, 2016
// Mass-Action Network

package com.cytocomp.mtt

import org.sbml.jsbml.{SBMLDocument, ASTNode, SBMLError, Reaction=>SBMLReaction, Model=>SBMLModel, Species=>SBMLSpecies, Parameter=>SBMLParameter, Compartment=>SBMLCompartment, InitialAssignment}
import com.cytocomp.mtt._

import scala.math.{max,min,pow,log,log10,exp,cos,sin,tan,cosh,sinh,tanh,asin,acos,atan,abs}

// just maps ids to numeric values
class ValueScope(val m: Map[String, Double], val parent_scope: ValueScope) {
}

class SBMLEvaluator(model: SBMLModel) {
  // sbml objects

  // species
  private val sbml_species: Seq[SBMLSpecies] = (for (k <- 0 until model.getNumSpecies) yield model.getSpecies(k)) toSeq
  private val species_ids: Set[String] = sbml_species.map(species => species.getId).toSet

  // species
  private val sbml_compartments: Seq[SBMLCompartment] = (for (k <- 0 until model.getNumCompartments) yield model.getCompartment(k)) toSeq

  // species
  private val initial_assignments: Seq[InitialAssignment] = (for (k <- 0 until model.getNumInitialAssignments) yield model.getInitialAssignment(k)) toSeq

  // parameters
  private val sbml_parameters: Seq[SBMLParameter] = (for (k <- 0 until model.getNumParameters) yield model.getParameter(k)) toSeq

  def toDouble(v: Boolean): Double = {
    if (v)
      1d
    else
      0d
  }

  def evaluateMath(n: ASTNode, k: Int = 0): Double = {
    def singleChild(): Double = {
      if (n.getChildCount != 1)
        throw new RuntimeException(s"Expected one child, found ${n.getChildCount}")
      else
        evaluateMath(n.getChild(0))
    }

    n.getType match {
      case ASTNode.Type.CONSTANT_E => 2.71828
      case ASTNode.Type.CONSTANT_FALSE => 0d
      case ASTNode.Type.CONSTANT_PI => 3.14159265359
      case ASTNode.Type.CONSTANT_TRUE => 1d
      case ASTNode.Type.DIVIDE => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          evaluateMath(n.getChild(k)) / evaluateMath(n, k+1)
      }
      // case ASTNode.Type.FUNCTION => function(singleChild)
      case ASTNode.Type.FUNCTION_ABS => abs(singleChild)
      // case ASTNode.Type.FUNCTION_ARCCOS => acos(singleChild)
      // case ASTNode.Type.FUNCTION_ARCCOSH => acosh(singleChild)
      // case ASTNode.Type.FUNCTION_ARCCOT => acot(singleChild)
      // case ASTNode.Type.FUNCTION_ARCCOTH => acoth(singleChild)
      // case ASTNode.Type.FUNCTION_ARCCSC => acsc(singleChild)
      // case ASTNode.Type.FUNCTION_ARCCSCH => acsch(singleChild)
      // case ASTNode.Type.FUNCTION_ARCSEC => asec(singleChild)
      // case ASTNode.Type.FUNCTION_ARCSECH => asech(singleChild)
      case ASTNode.Type.FUNCTION_ARCSIN => asin(singleChild)
      // case ASTNode.Type.FUNCTION_ARCSINH => asinh(singleChild)
      case ASTNode.Type.FUNCTION_ARCTAN => atan(singleChild)
      // case ASTNode.Type.FUNCTION_ARCTANH => atanh(singleChild)
      case ASTNode.Type.FUNCTION_CEILING => singleChild
      case ASTNode.Type.FUNCTION_COS => cos(singleChild)
      case ASTNode.Type.FUNCTION_COSH => cosh(singleChild)
      // case ASTNode.Type.FUNCTION_COT => cot(singleChild)
      // case ASTNode.Type.FUNCTION_COTH => coth(singleChild)
      // case ASTNode.Type.FUNCTION_CSC => csc(singleChild)
      // case ASTNode.Type.FUNCTION_CSCH => csch(singleChild)
      // case ASTNode.Type.FUNCTION_DELAY => (singleChild)
      case ASTNode.Type.FUNCTION_EXP => exp(singleChild)
      // case ASTNode.Type.FUNCTION_FACTORIAL => singleChild
      case ASTNode.Type.FUNCTION_FLOOR => singleChild
      case ASTNode.Type.FUNCTION_LN => log(singleChild)
      case ASTNode.Type.FUNCTION_LOG => log10(singleChild)
      case ASTNode.Type.FUNCTION_MAX => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          max(evaluateMath(n.getChild(k)), evaluateMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION_MIN => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          min(evaluateMath(n.getChild(k)), evaluateMath(n, k+1))
      }
      // case ASTNode.Type.FUNCTION_PIECEWISE => piecewise(singleChild)
      case ASTNode.Type.FUNCTION_POWER => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          pow(evaluateMath(n.getChild(k)), evaluateMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION_ROOT => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          pow(evaluateMath(n.getChild(k)), 1d/evaluateMath(n, k+1))
      }
      case ASTNode.Type.FUNCTION_SEC => 1d/cos(singleChild)
      case ASTNode.Type.FUNCTION_SECH => 1d/cosh(singleChild)
      case ASTNode.Type.FUNCTION_SIN => sin(singleChild)
      case ASTNode.Type.FUNCTION_SINH => sinh(singleChild)
      case ASTNode.Type.FUNCTION_TAN => tan(singleChild)
      case ASTNode.Type.FUNCTION_TANH => tanh(singleChild)
      case ASTNode.Type.INTEGER => n.getInteger
      // case ASTNode.Type.LAMBDA => new LAMBDA()
      case ASTNode.Type.LOGICAL_AND => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          evaluateMath(n.getChild(k)) * evaluateMath(n, k+1)
      }
      case ASTNode.Type.LOGICAL_NOT => {
        1d-evaluateMath(n.getChild(0))
      }
      case ASTNode.Type.LOGICAL_OR => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          evaluateMath(n.getChild(k)) + evaluateMath(n, k+1)
      }
      case ASTNode.Type.LOGICAL_XOR => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          evaluateMath(n.getChild(k)) - evaluateMath(n, k+1)
      }
      case ASTNode.Type.MINUS => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          evaluateMath(n.getChild(k)) - evaluateMath(n, k+1)
      }
      case ASTNode.Type.NAME => getSymbolValueNoThrow(n.getName)
      case ASTNode.Type.NAME_AVOGADRO => 1d
      case ASTNode.Type.NAME_TIME => 0d
      case ASTNode.Type.PLUS => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          evaluateMath(n.getChild(k)) + evaluateMath(n, k+1)
      }
      case ASTNode.Type.POWER => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          pow(evaluateMath(n.getChild(k)), evaluateMath(n, k+1))
      }
      case ASTNode.Type.RATIONAL => n.getReal
      case ASTNode.Type.REAL => n.getReal
      case ASTNode.Type.REAL_E => 2.71828
      case ASTNode.Type.RELATIONAL_EQ => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          toDouble(evaluateMath(n.getChild(k)) == evaluateMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_GEQ => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          toDouble(evaluateMath(n.getChild(k)) >= evaluateMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_GT => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          toDouble(evaluateMath(n.getChild(k)) > evaluateMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_LEQ => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          toDouble(evaluateMath(n.getChild(k)) <= evaluateMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_LT => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          toDouble(evaluateMath(n.getChild(k)) < evaluateMath(n, k+1))
      }
      case ASTNode.Type.RELATIONAL_NEQ => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          toDouble(evaluateMath(n.getChild(k)) != evaluateMath(n, k+1))
      }
      case ASTNode.Type.TIMES => {
        if (k+1 == n.getChildCount)
          evaluateMath(n.getChild(k))
        else
          evaluateMath(n.getChild(k)) * evaluateMath(n, k+1)
      }
      case _ => throw new RuntimeException("Missing conversion for AST node type")
    }
  }

  // parameter initial values
  val parameter_init: Map[String, Double] = (
    for (p <- sbml_parameters) yield p.getId -> p.getValue
  ) toMap

  // compartment initial values
  val compartment_init: Map[String, Double] = (
    for (c <- sbml_compartments; if (c.isSetSize)) yield c.getId -> c.getSize
  ) toMap

  // species initial values
  val species_init_defined: Map[String, Double] =
    sbml_species.map( s =>
      s.getId -> (
        if (s.isSetInitialConcentration) {
          val scalar = (if (s.isSetCompartment)
            compartment_init.getOrElse(s.getCompartment, 1d)
          else
            1d)
          s.getInitialConcentration*scalar
        } else if (s.isSetInitialAmount) {
          s.getInitialAmount
        } else {
          0d
        }
      )).toMap

  val initial_assignment: Map[String, Double] = (
    for (i <- initial_assignments; if (i.isSetSymbol)) yield i.getSymbol -> evaluateMath(i.getMath)
  ).toMap
  // println(initial_assignments)
  // println(initial_assignment)

  // val species_null: Map[String, Double] = (
    // for (s <- sbml_species) yield s.getId -> 0d
  // ).toMap
  val species_init: Map[String, Double] = initial_assignment.filterKeys(s => species_ids.contains(s)) ++ species_init_defined

  def getSymbolValue(symbol: String, throw_on_error: Boolean = true): Double = {
    // println(s"parameter_init = ${parameter_init}")
    // println(s"initial_assignment = ${initial_assignment}")
    if (initial_assignment != null) {
      for ((c,v) <- initial_assignment) {
        if (c == symbol){
          if (!v.isNaN) {
            return v
          } else {
            for (i <- initial_assignments) {
              if (i.isSetSymbol && i.getSymbol == symbol)
                return evaluateMath(i.getMath)
            }
          }
        }
      }
    }
    for ((c,v) <- compartment_init) {
      if (c == symbol)
        return v
    }
    for ((p,v) <- parameter_init) {
      if (p == symbol)
        return v
    }
    for ((u,v) <- species_init) {
      if (u == symbol)
        return v
    }
    if (throw_on_error)
      throw new RuntimeException(s"Could not find value for symbol $symbol")
    else
      0d
  }

  def getSymbolValueNoThrow(symbol: String): Double = {
    getSymbolValue(symbol, false)
  }

  // for ((p,v) <- parameter_init)
  //   println(s"$p (param): $v")
  // for ((s,v) <- species_init)
  //   println(s"$s (spec):  $v")

}
