// MTT
// J Kyle Medley 2016-2019

package com.cytocomp.mtt

import scala.collection.breakOut

import com.cytocomp.mtt._

object SymbolExtractor {
  def apply(x: MathExpr): Seq[String] = {
    x match {
      case DIVIDE(u,v) => apply(u) ++ apply(v)
      case FUNCTION_ABS(u) => apply(u)
      case FUNCTION_DELAY(u) => apply(u)
      case FUNCTION_EXP(u) => apply(u)
      case FUNCTION_LN(u) => apply(u)
      case FUNCTION_LOG(u) => apply(u)
      case FUNCTION_MAX(u,v) => apply(u) ++ apply(v)
      case FUNCTION_MIN(u,v) => apply(u) ++ apply(v)
      case FUNCTION_POWER(u,v) => apply(u) ++ apply(v)
      case FUNCTION_ROOT(u,v) => apply(u) ++ apply(v)
      case FUNCTION_SIN(u) => apply(u)
      case FUNCTION_TAN(u) => apply(u)
      case SUBTRACT(u,v) => apply(u) ++ apply(v)
      case NAME(s) => Seq(s)
      case SUM(u,v) => apply(u) ++ apply(v)
      case POWER(u,v) => apply(u) ++ apply(v)
      case MULTIPLY(u,v) => apply(u) ++ apply(v)
      case _ => Seq()
    }
  }
  def extractParameters(u: MathExpr, evaluator: SBMLEvaluator): Seq[String] = {
    apply(u).filter(s => evaluator.parameter_init.contains(s)).distinct
  }
}

trait SymbolMapper {
  val variables: Map[String, Long]
  val hash: Long

  def makeHash(u: MathExpr): Long = {
    val h = new Hash(variables)
    h(u)
  }

  def satisfies(src: MathExpr, bound_vars: Map[String, Long]): Boolean = {
    // compute the hash of the source expression with the bound variable mappings
    val h = new Hash(bound_vars)
    val v = h(src)
    // compare the hash to the archetype
    v == hash
  }
}

case class SymbolMapperZeroOrder_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val variables = Map[String, Long]()
  final val hash = 0L

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    if (params.length > 0)
      Some(params(0))
    else
      None
  }
}

case class SymbolMapperIrrevUni_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kf: MathTerminal = NAME("kf")
  final val A: MathTerminal = NAME("A")
  final val formula = MULTIPLY(kf, A)
  final val variables = Map[String, Long](
    "kf" -> 2,
    "A" -> 3
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 1)
      Some(Map(reactants(0) -> variables("A")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kf"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperIrrevUniMod_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kf: MathTerminal = NAME("kf")
  final val A: MathTerminal = NAME("A")
  final val formula = MULTIPLY(kf, A)
  final val variables = Map[String, Long](
    "kf" -> 2,
    "A" -> 3
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (modifiers.length == 1)
      Some(Map(modifiers(0) -> variables("A")))
    else
      None
  }

  def apply(modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(modifiers, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kf"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevUniUni_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kf: MathTerminal = NAME("kf")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(MULTIPLY(kf, A), B)
  final val variables = Map[String, Long](
    "kf" -> 2,
    "A" -> 3,
    "B" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 1 && products.length == 1)
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kf"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevUniUni_kr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kr: MathTerminal = NAME("kr")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(A, MULTIPLY(kr, B))
  final val variables = Map[String, Long](
    "kr" -> 2,
    "A" -> 3,
    "B" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 1 && products.length == 1)
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kr"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevUniBi_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kf: MathTerminal = NAME("kf")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val C: MathTerminal = NAME("C")
  final val formula = SUBTRACT(MULTIPLY(kf, A), MULTIPLY(B, C))
  final val variables = Map[String, Long](
    "kf" -> 2,
    "A" -> 3,
    "B" -> 5,
    "C" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 1 && products.length == 2)
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B"), products(1) -> variables("C")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kf"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}



case class SymbolMapperRevUniBi_kr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kr: MathTerminal = NAME("kr")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val C: MathTerminal = NAME("C")
  final val formula = SUBTRACT(A, MULTIPLY(kr, MULTIPLY(B, C)))
  final val variables = Map[String, Long](
    "kr" -> 2,
    "A" -> 3,
    "B" -> 5,
    "C" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 1 && products.length == 2)
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B"), products(1) -> variables("C")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kr"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevBiUni_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kf: MathTerminal = NAME("kf")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val C: MathTerminal = NAME("C")
  final val formula = SUBTRACT(MULTIPLY(MULTIPLY(kf, A), B), C)
  final val variables = Map[String, Long](
    "kf" -> 2,
    "A" -> 3,
    "B" -> 5,
    "C" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && products.length == 1)
      Some(Map(reactants(0) -> variables("A"), reactants(1) -> variables("B"), products(0) -> variables("C")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kf"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevBiUni_kr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kr: MathTerminal = NAME("kr")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val C: MathTerminal = NAME("C")
  final val formula = SUBTRACT(MULTIPLY(A,B), MULTIPLY(kr, C))
  final val variables = Map[String, Long](
    "kr" -> 2,
    "A" -> 3,
    "B" -> 5,
    "C" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && products.length == 1)
      Some(Map(reactants(0) -> variables("A"), reactants(1) -> variables("B"), products(0) -> variables("C")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kr"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevDimer_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kf: MathTerminal = NAME("kf")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(MULTIPLY(MULTIPLY(kf, A), A), B)
  final val variables = Map[String, Long](
    "kf" -> 2,
    "A" -> 3,
    "B" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && reactants(0) == reactants(1) && products.length == 1)
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kf"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevDimer_kr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kr: MathTerminal = NAME("kr")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(MULTIPLY(A,A), MULTIPLY(kr, B))
  final val variables = Map[String, Long](
    "kr" -> 2,
    "A" -> 3,
    "B" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && reactants(0) == reactants(1) && products.length == 1)
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kr"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevMonomer_kf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kf: MathTerminal = NAME("kf")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(MULTIPLY(kf, A), MULTIPLY(B, B))
  final val variables = Map[String, Long](
    "kf" -> 2,
    "A" -> 3,
    "B" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 1 && products.length == 2 && products(0) == products(1))
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kf"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperRevMonomer_kr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kr: MathTerminal = NAME("kr")
  final val A: MathTerminal = NAME("A")
  final val B: MathTerminal = NAME("B")
  final val formula = SUBTRACT(A, MULTIPLY(kr, MULTIPLY(B, B)))
  final val variables = Map[String, Long](
    "kr" -> 2,
    "A" -> 3,
    "B" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 1 && products.length == 2 && products(0) == products(1))
      Some(Map(reactants(0) -> variables("A"), products(0) -> variables("B")))
    else
      None
  }

  def apply(reactants: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(reactants, products)
    bv.map(bv =>
      {
        for (p <- params) {
          val complete_bv = bv ++ Map[String,Long](p -> variables("kr"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
        }
        return None
      }
    )
  }
}

case class SymbolMapperIrrevMM_kcat(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kcat: MathTerminal = NAME("kcat")
  final val E: MathTerminal = NAME("E")
  final val S: MathTerminal = NAME("S")
  final val KM: MathTerminal = NAME("KM")
  final val formula = MULTIPLY(kcat, MULTIPLY(E, DIVIDE(S,SUM(KM, S))))
  final val variables = Map[String, Long](
    "KM" -> 1,
    "kcat" -> 2,
    "E" -> 3,
    "S" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 )
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S")))
    else
      None
  }

  def apply(reactants: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      val bv = bindVariables(reactants)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("kcat"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}

case class SymbolMapperIrrevMM_KM(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val kcat: MathTerminal = NAME("kcat")
  final val E: MathTerminal = NAME("E")
  final val S: MathTerminal = NAME("S")
  final val KM: MathTerminal = NAME("KM")
  final val formula = MULTIPLY(kcat, MULTIPLY(E, DIVIDE(S,SUM(KM, S))))
  final val variables = Map[String, Long](
    "KM" -> 2,
    "kcat" -> 1,
    "E" -> 3,
    "S" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S")))
    else
      None
  }

  def apply(reactants: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      val bv = bindVariables(reactants)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("KM"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressor_Km(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (modifiers.length == 1)
      Some(Map(modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    val bv = bindVariables(modifiers)
    bv.map(bv => {
      for (p <- params) {
        val complete_bv = bv ++ Map[String,Long](p -> variables("Km"))
          if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
            return Some(p)
      }
    })
    return None
  }
}


case class SymbolMapperRepressor_a_tr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val a_tr: MathTerminal = NAME("a_tr")
  final val Km: MathTerminal = NAME("Km")
  final val R: MathTerminal = NAME("R")
  final val formula = MULTIPLY(a_tr, DIVIDE(POWER(Km,INTEGER(2)), SUM(POWER(R,INTEGER(2)), POWER(Km,INTEGER(2)))))
  final val variables = Map[String, Long](
    "a_tr" -> 3,
    // don't use 2
    "Km" -> 1,
    "R" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (modifiers.length == 1)
      Some(Map(modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("a_tr"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGeneric_a0_tr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val a0_tr: MathTerminal = NAME("a0_tr")
  final val a_tr: MathTerminal = NAME("a_tr")
  final val Km: MathTerminal = NAME("Km")
  final val n: MathTerminal = NAME("n")
  final val R: MathTerminal = NAME("R")
  final val formula = SUM(a0_tr, MULTIPLY(a_tr, DIVIDE(POWER(Km,n), SUM(POWER(R,n), POWER(Km,n)))))
  final val variables = Map[String, Long](
    "a0_tr" -> 3,
    "a_tr" -> 1,
    "Km" -> 1,
    "n" -> 1,
    "R" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (modifiers.length == 1)
      Some(Map(modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("a0_tr"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGeneric_a_tr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val a0_tr: MathTerminal = NAME("a0_tr")
  final val a_tr: MathTerminal = NAME("a_tr")
  final val Km: MathTerminal = NAME("Km")
  final val n: MathTerminal = NAME("n")
  final val R: MathTerminal = NAME("R")
  final val formula = SUM(a0_tr, MULTIPLY(a_tr, DIVIDE(POWER(Km,n), SUM(POWER(R,n), POWER(Km,n)))))
  final val variables = Map[String, Long](
    "a0_tr" -> 1,
    "a_tr" -> 3,
    "Km" -> 1,
    "n" -> 1,
    "R" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (modifiers.length == 1)
      Some(Map(modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("a_tr"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGeneric_Km(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val a0_tr: MathTerminal = NAME("a0_tr")
  final val a_tr: MathTerminal = NAME("a_tr")
  final val Km: MathTerminal = NAME("Km")
  final val n: MathTerminal = NAME("n")
  final val R: MathTerminal = NAME("R")
  final val formula = SUM(a0_tr, MULTIPLY(a_tr, DIVIDE(POWER(Km,n), SUM(POWER(R,n), POWER(Km,n)))))
  final val variables = Map[String, Long](
    "a0_tr" -> 1,
    "a_tr" -> 1,
    "Km" -> 3,
    "n" -> 1,
    "R" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (modifiers.length == 1)
      Some(Map(modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("Km"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGeneric_n(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
  final val a0_tr: MathTerminal = NAME("a0_tr")
  final val a_tr: MathTerminal = NAME("a_tr")
  final val Km: MathTerminal = NAME("Km")
  final val n: MathTerminal = NAME("n")
  final val R: MathTerminal = NAME("R")
  final val formula = SUM(a0_tr, MULTIPLY(a_tr, DIVIDE(POWER(Km,n), SUM(POWER(R,n), POWER(Km,n)))))
  final val variables = Map[String, Long](
    "a0_tr" -> 1,
    "a_tr" -> 1,
    "Km" -> 1,
    "n" -> 3,
    "R" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (modifiers.length == 1)
      Some(Map(modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("n"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGenericDeg_a0_tr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "a0_tr" -> 3,
    "a_tr" -> 1,
    "n" -> 1,
    "kd" -> 1,
    "Km" -> 1,
    "X" -> 5,
    "R" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (products.length == 1 && modifiers.length == 1)
      Some(Map(products(0) -> variables("X"), modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers, products)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("a0_tr"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGenericDeg_a_tr(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "a_tr" -> 3,
    "n" -> 1,
    "kd" -> 1,
    "Km" -> 1,
    "X" -> 5,
    "R" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (products.length == 1 && modifiers.length == 1)
      Some(Map(products(0) -> variables("X"), modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers, products)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("a_tr"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGenericDeg_Km(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "Km" -> 3,
    "X" -> 5,
    "R" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (products.length == 1 && modifiers.length == 1)
      Some(Map(products(0) -> variables("X"), modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers, products)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("Km"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGenericDeg_n(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "n" -> 3,
    "kd" -> 1,
    "Km" -> 1,
    "X" -> 5,
    "R" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (products.length == 1 && modifiers.length == 1)
      Some(Map(products(0) -> variables("X"), modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers, products)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("n"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}


case class SymbolMapperRepressorBasalGenericDeg_kd(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "kd" -> 3,
    "Km" -> 1,
    "X" -> 5,
    "R" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (products.length == 1 && modifiers.length == 1)
      Some(Map(products(0) -> variables("X"), modifiers(0) -> variables("R")))
    else
      None
  }

  def apply(modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    // println(params)
    for (modifiers <- modifiers.permutations) {
      val bv = bindVariables(modifiers, products)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("kd"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}

case class SymbolBottsMoralesInhIrrev_kcat(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "kcat" -> 7,
    "Km" -> 1,
    "KI" -> 1,
    "E" -> 2,
    "S" -> 3,
    "I" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      val bv = bindVariables(reactants, modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("kcat"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}

case class SymbolBottsMoralesInhIrrev_Km(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "Km" -> 7,
    "KI" -> 1,
    "E" -> 2,
    "S" -> 3,
    "I" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      val bv = bindVariables(reactants, modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("Km"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}

case class SymbolBottsMoralesInhIrrev_KI(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "KI" -> 7,
    "E" -> 2,
    "S" -> 3,
    "I" -> 5
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      val bv = bindVariables(reactants, modifiers)
      bv.map(bv =>
        {
          for (p <- params) {
            val complete_bv = bv ++ Map[String,Long](p -> variables("KI"))
            if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
              return Some(p)
          }
        }
      )
    }
    return None
  }
}



case class SymbolBottsMoralesInhRev_Vf(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "Vf" -> 11,
    "Ks" -> 1,
    "KI" -> 1,
    "Kp" -> 1,
    "Keq" -> 1,
    "E" -> 2,
    "S" -> 3,
    "P" -> 5,
    "I" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1 && products.length == 2)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I"), products(1) -> variables("P")))
    else if (reactants.length == 1 && modifiers.length == 2 && products.length == 1)
      Some(Map(reactants(0) -> variables("S"), modifiers(0) -> variables("E"), modifiers(1) -> variables("I"), products(0) -> variables("P")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      for (modifiers <- modifiers.permutations) {
        for (products <- products.permutations) {
          val bv = bindVariables(reactants, modifiers, products)
          bv.map(bv =>
            {
              for (p <- params) {
                val complete_bv = bv ++ Map[String,Long](p -> variables("Vf"))
                if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
                  return Some(p)
              }
            }
          )
        }
      }
    }
    return None
  }
}

case class SymbolBottsMoralesInhRev_Ks(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "Ks" -> 11,
    "KI" -> 1,
    "Kp" -> 1,
    "Keq" -> 1,
    "E" -> 2,
    "S" -> 3,
    "P" -> 5,
    "I" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1 && products.length == 2)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I"), products(1) -> variables("P")))
    else if (reactants.length == 1 && modifiers.length == 2 && products.length == 1)
      Some(Map(reactants(0) -> variables("S"), modifiers(0) -> variables("E"), modifiers(1) -> variables("I"), products(0) -> variables("P")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      for (modifiers <- modifiers.permutations) {
        for (products <- products.permutations) {
          val bv = bindVariables(reactants, modifiers, products)
          bv.map(bv =>
            {
              for (p <- params) {
                val complete_bv = bv ++ Map[String,Long](p -> variables("Ks"))
                if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
                  return Some(p)
              }
            }
          )
        }
      }
    }
    return None
  }
}



case class SymbolBottsMoralesInhRev_KI(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "KI" -> 11,
    "Kp" -> 1,
    "Keq" -> 1,
    "E" -> 2,
    "S" -> 3,
    "P" -> 5,
    "I" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1 && products.length == 2)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I"), products(1) -> variables("P")))
    else if (reactants.length == 1 && modifiers.length == 2 && products.length == 1)
      Some(Map(reactants(0) -> variables("S"), modifiers(0) -> variables("E"), modifiers(1) -> variables("I"), products(0) -> variables("P")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      for (modifiers <- modifiers.permutations) {
        for (products <- products.permutations) {
          val bv = bindVariables(reactants, modifiers, products)
          bv.map(bv =>
            {
              for (p <- params) {
                val complete_bv = bv ++ Map[String,Long](p -> variables("KI"))
                if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
                  return Some(p)
              }
            }
          )
        }
      }
    }
    return None
  }
}

case class SymbolBottsMoralesInhRev_Kp(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "Kp" -> 11,
    "Keq" -> 1,
    "E" -> 2,
    "S" -> 3,
    "P" -> 5,
    "I" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1 && products.length == 2)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I"), products(1) -> variables("P")))
    else if (reactants.length == 1 && modifiers.length == 2 && products.length == 1)
      Some(Map(reactants(0) -> variables("S"), modifiers(0) -> variables("E"), modifiers(1) -> variables("I"), products(0) -> variables("P")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      for (modifiers <- modifiers.permutations) {
        for (products <- products.permutations) {
          val bv = bindVariables(reactants, modifiers, products)
          bv.map(bv =>
            {
              for (p <- params) {
                val complete_bv = bv ++ Map[String,Long](p -> variables("Kp"))
                if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
                  return Some(p)
              }
            }
          )
        }
      }
    }
    return None
  }
}

case class SymbolBottsMoralesInhRev_Keq(val u: MathExpr, val evaluator: SBMLEvaluator) extends SymbolMapper {
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
    "Keq" -> 11,
    "E" -> 2,
    "S" -> 3,
    "P" -> 5,
    "I" -> 7
  )

  final val hash = makeHash(formula)

  def bindVariables(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[Map[String, Long]] = {
    if (reactants.length == 2 && modifiers.length == 1 && products.length == 2)
      Some(Map(reactants(0) -> variables("E"), reactants(1) -> variables("S"), modifiers(0) -> variables("I"), products(1) -> variables("P")))
    else if (reactants.length == 1 && modifiers.length == 2 && products.length == 1)
      Some(Map(reactants(0) -> variables("S"), modifiers(0) -> variables("E"), modifiers(1) -> variables("I"), products(0) -> variables("P")))
    else
      None
  }

  def apply(reactants: Seq[String], modifiers: Seq[String], products: Seq[String]): Option[String] = {
    val params = SymbolExtractor.extractParameters(u, evaluator)
    for (reactants <- reactants.permutations) {
      for (modifiers <- modifiers.permutations) {
        for (products <- products.permutations) {
          val bv = bindVariables(reactants, modifiers, products)
          bv.map(bv =>
            {
              for (p <- params) {
                val complete_bv = bv ++ Map[String,Long](p -> variables("Keq"))
                if (satisfies(u.canonical(complete_bv.keys toSet), complete_bv))
                  return Some(p)
              }
            }
          )
        }
      }
    }
    return None
  }
}
