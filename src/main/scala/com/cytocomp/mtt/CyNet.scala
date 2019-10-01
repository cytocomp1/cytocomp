// J Kyle Medley, 2016
// Mass-Action Network

package com.cytocomp.mtt

import org.sbml.jsbml.{SBMLDocument, ASTNode, SBMLError, Reaction=>SBMLReaction, Model=>SBMLModel, Species=>SBMLSpecies}
import com.cytocomp.mtt._

import scala.math.sqrt
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.LinkedHashMap

class CyNode(val name: String, val productionGain: Double=0, val degradationGain: Double=0) {
  /**
   * Return the string representation of this node.
   */
  override def toString(): String = name

  var initial_value: Option[Double] = None
}

trait CyReaction {
  // true for second step in michaelis-menten
  // TODO: find a better way to do this
  var enzymatic_step: Boolean = false
  // TODO: remove
  var irreversible: Boolean = false

  var kf: Option[Double] = None
  var kr: Option[Double] = None
  var n: Int = 1
  var A_depletion: Boolean = true
  var B_depletion: Boolean = true

  def gainsString(): String = {
    (
      (if (!kf.isEmpty)
        Seq(s"kf = ${kf.get}")
      else
        Seq())
      ++
      (if (!kr.isEmpty)
        Seq(s"kr = ${kr.get}")
      else
        Seq())
    ).mkString(", ") match {
      case "" => ""
      case x => s"($x)"
    }
  }

  def arrowString: String = if (reversible) "<>" else ">"

  /**
   * Return `true` if this reaction produces `n`.
   */
  def produces(n: CyNode): Boolean

  /**
   * Return `true` if this reaction consumes `n`.
   */
  def consumes(n: CyNode): Boolean

  /**
   * Get all reactants.
   */
  def reactants(): Seq[CyNode]

  /**
   * Get all products.
   */
  def products(): Seq[CyNode]

  /**
   * Return the main product of this reaction (i.e. output of the block)
   */
  def mainProduct(): CyNode

  var gain: Double = 0
  def setGain(g: Double): Unit = { gain = g }
  def getGain(): Double = gain

  def reversible: Boolean
}

case class CyRxConstProduction(val p: CyNode) extends CyReaction {
  /**
   * Return `true` if this reaction produces `n`.
   */
  def produces(n: CyNode): Boolean = { p == n }

  /**
   * Return `true` if this reaction consumes `n`.
   */
  def consumes(n: CyNode): Boolean = { false }

  /**
   * Get all reactants.
   */
  def reactants() = Seq.empty[CyNode]

  /**
   * Get all products.
   */
  def products() = Seq[CyNode](p)

  /**
   * Return the main product of this reaction (i.e. output of the block)
   */
  def mainProduct(): CyNode = p

  /**
   * Return the string representation of this reaction.
   */
  override def toString(): String = {
    Seq(s"∅ $arrowString " + p.toString, gainsString).mkString(" ")
  }

  def reversible: Boolean = false
}

object MakeCyRxConstProduction {
  def apply(p: Seq[CyNode]): CyRxConstProduction = {
    if (p.length != 1) throw new RuntimeException(s"Wrong number of products: ∅ -> ${p.length}")
    new CyRxConstProduction(p(0))
  }
}

case class CyRxUniUni(val r: CyNode, val p: CyNode, is_reversible: Boolean) extends CyReaction {
  var basal: Double = 0d

  /**
   * Return `true` if this reaction produces `n`.
   */
  def produces(n: CyNode): Boolean = { p == n }

  /**
   * Return `true` if this reaction consumes `n`.
   */
  def consumes(n: CyNode): Boolean = { r == n }

  /**
   * Get all reactants.
   */
  def reactants() = Seq[CyNode](r)

  /**
   * Get all products.
   */
  def products() = Seq[CyNode](p)

  /**
   * Return the main product of this reaction (i.e. output of the block)
   */
  def mainProduct(): CyNode = p

  /**
   * Return the string representation of this reaction.
   */
  override def toString(): String = {
    Seq(r.toString + (if (reversible) " <> " else " > ") + p.toString, gainsString).mkString(" ")
  }

  def reversible: Boolean = is_reversible
}

object MakeCyRxUniUni {
  def apply(r: Seq[CyNode], p: Seq[CyNode], reversible: Boolean): CyRxUniUni = {
    if (r.length != 1 || p.length != 1) throw new RuntimeException(s"Wrong number of reactants/products: ${r.length} -> ${p.length}")
    new CyRxUniUni(r(0), p(0), reversible)
  }
}

case class CyRxBiUni(val r1: CyNode, val r2: CyNode, val p: CyNode, is_reversible: Boolean) extends CyReaction {
  /**
   * Return `true` if this reaction produces `n`.
   */
  def produces(n: CyNode): Boolean = { p == n }

  /**
   * Return `true` if this reaction consumes `n`.
   */
  def consumes(n: CyNode): Boolean = { r1 == n || r2 == n }

  /**
   * Get all reactants.
   */
  def reactants() = Seq[CyNode](r1, r2)

  /**
   * Get all products.
   */
  def products() = Seq[CyNode](p)

  /**
   * Return the main product of this reaction (i.e. output of the block)
   */
  def mainProduct(): CyNode = p

  /**
   * Return the string representation of this reaction.
   */
  override def toString(): String = {
    Seq(r1.toString + " + " + r2.toString + s" $arrowString " + p.toString, gainsString).mkString(" ")
  }

  def reversible: Boolean = is_reversible
}

object MakeCyRxBiUni {
  def apply(r: Seq[CyNode], p: Seq[CyNode], reversible: Boolean): CyRxBiUni = {
    if (r.length != 2 || p.length != 1) throw new RuntimeException(s"Wrong number of reactants/products: ${r.length} -> ${p.length}")
    new CyRxBiUni(r(0), r(1), p(0), reversible)
  }
}

case class CyRxDimerization(val r: CyNode, val p: CyNode, is_reversible: Boolean) extends CyReaction {
  /**
   * Return `true` if this reaction produces `n`.
   */
  def produces(n: CyNode): Boolean = { p == n }

  /**
   * Return `true` if this reaction consumes `n`.
   */
  def consumes(n: CyNode): Boolean = { r == n }

  /**
   * Get all reactants.
   */
  def reactants() = Seq[CyNode](r)

  /**
   * Get all products.
   */
  def products() = Seq[CyNode](p)

  /**
   * Return the main product of this reaction (i.e. output of the block)
   */
  def mainProduct(): CyNode = p

  /**
   * Return the string representation of this reaction.
   */
  override def toString(): String = {
    Seq(r.toString + " + " + r.toString + s" $arrowString " + p.toString, gainsString).mkString(" ")
  }

  def reversible: Boolean = is_reversible
}

object MakeCyRxDimerization {
  def apply(r: Seq[CyNode], p: Seq[CyNode], reversible: Boolean): CyRxDimerization = {
    if (r.length != 1 || p.length != 1) throw new RuntimeException(s"Wrong number of reactants/products: ${r.length} -> ${p.length}")
    new CyRxDimerization(r(0), p(0), reversible)
  }
}

case class CyRxUniBi(val r: CyNode, val p1: CyNode, val p2: CyNode, is_reversible: Boolean) extends CyReaction {
  /**
   * Return `true` if this reaction produces `n`.
   */
  def produces(n: CyNode): Boolean = { p1 == n || p2 == n }

  /**
   * Return `true` if this reaction consumes `n`.
   */
  def consumes(n: CyNode): Boolean = { r == n }

  /**
   * Get all reactants.
   */
  def reactants() = Seq[CyNode](r)

  /**
   * Get all products.
   */
  def products() = Seq[CyNode](p1,p2)

  /**
   * Return the main product of this reaction (i.e. output of the block)
   */
  def mainProduct(): CyNode = p1

  /**
   * Return the string representation of this reaction.
   */
  override def toString(): String = {
    Seq(r.toString + s" $arrowString " + p1.toString + " + " + p2.toString, gainsString).mkString(" ")
  }

  def reversible: Boolean = is_reversible
}

object MakeCyRxUniBi {
  def apply(r: Seq[CyNode], p: Seq[CyNode], reversible: Boolean): CyRxUniBi = {
    if (r.length != 1 || p.length != 2) throw new RuntimeException(s"Wrong number of reactants/products: ${r.length} -> ${p.length}")
    new CyRxUniBi(r(0), p(0), p(1), reversible)
  }
}

case class CyRxMonomerization(val r: CyNode, val p: CyNode, is_reversible: Boolean) extends CyReaction {
  /**
   * Return `true` if this reaction produces `n`.
   */
  def produces(n: CyNode): Boolean = { p == n }

  /**
   * Return `true` if this reaction consumes `n`.
   */
  def consumes(n: CyNode): Boolean = { r == n }

  /**
   * Get all reactants.
   */
  def reactants() = Seq[CyNode](r)

  /**
   * Get all products.
   */
  def products() = Seq[CyNode](p)

  /**
   * Return the main product of this reaction (i.e. output of the block)
   */
  def mainProduct(): CyNode = p

  /**
   * Return the string representation of this reaction.
   */
  override def toString(): String = {
    Seq(r.toString + s" $arrowString " + p.toString + " + " + p.toString, gainsString).mkString(" ")
  }

  def reversible: Boolean = is_reversible
}

object MakeCyRxMonomerization {
  def apply(r: Seq[CyNode], p: Seq[CyNode], reversible: Boolean): CyRxMonomerization = {
    if (r.length != 1 || p.length != 1) throw new RuntimeException(s"Wrong number of reactants/products: ${r.length} -> ${p.length}")
    new CyRxMonomerization(r(0), p(0), reversible)
  }
}

/**
 * Represents a loop in the network.
 */
class CyLoop(val r: Seq[CyReaction]) {
  val n: Seq[CyNode] = (for (x <- r) yield List(x.reactants.toList ++ List(x.mainProduct))).flatten.flatten.toSeq.distinct

  override def toString(): String = {
    s"Loop ($r)"
  }

  def contains(rx: CyReaction): Boolean = r.contains(rx)
}

/**
 * Represents a network that the chip can be programmed with.
 */
class CyNet(val n: Seq[CyNode], val r: Seq[CyReaction]) {
  /**
   * Find all reactions that produce `n`.
   */
  def producers(n: CyNode): Seq[CyReaction] = {
    r.filter(x => x.produces(n))
  }

  /**
   * Find all reactions that consume `n`.
   */
  def consumers(n: CyNode): Seq[CyReaction] = {
    r.filter(x => x.consumes(n))
  }

  var loops = ArrayBuffer[CyLoop]()

  /**
   * Returns true if the given reaction is in a loop, false otherwise.
   */
  def in_loop(rx: CyReaction): Boolean = {
    for (l <- loops)
      if (l.contains(rx))
        return true
    return false
  }

  def has_zero_order_reaction(u: CyNode): Boolean = {
    u.productionGain != 0
  }

  /**
   * Find all loops in the network.
   */
  def find_loops() = {
    for (u <- n) {
      if (has_zero_order_reaction(u)) {
        for(rx <- producers(u)) {
          for (v <- rx.reactants) {
            if (!in_loop(rx))
              find_loop(v,u,List(rx))
          }
        }
      }
    }
  }
  find_loops

  // used in loop finding
  def find_loop(u: CyNode, src: CyNode, chain: List[CyReaction], visited: Set[CyNode] = Set[CyNode]()): Unit = {
    if (u == src) {
      val loop = new CyLoop(chain.toSeq)
      loops += loop
      // println(s"  found loop $loop")
    } else {
      if (!visited.contains(u)) {
        for(rx <- producers(u)) {
          for (v <- rx.reactants) {
            find_loop(v,src,chain++List(rx),visited+u)
          }
        }
      }
    }
  }

  /**
   * Return the string representation of this network.
   */
  override def toString(): String = {
    "CyNet\n" +
      r.map(rxn => "  "+rxn.toString).mkString("\n")
  }
}

object getSBMLSpeciesByID {
  def apply(model: SBMLModel, id: String): SBMLSpecies = {
    for (i <- 0 until model.getNumSpecies)
      if (model.getSpecies(i).getId == id)
        return model.getSpecies(i)
    throw new RuntimeException(s"Cannot find species with id $id")
  }
}

class SBMLImporter(w: SBML.Wrapper, margins: Map[String,Double] = Map.empty[String,Double], default_margin: Double = 10d) {
  def genUniqueNodeID(prefix: String, nodes: collection.mutable.Map[String, CyNode]): String = {
    for (k <- 0 until 10000) {
      val s = prefix+s"$k"
      if (!nodes.contains(s)) {
        return s
      }
    }
    throw new RuntimeException("Unable to generate unique id")
  }

  val model = w.doc.getModel
  val evaluator = new SBMLEvaluator(model)
  val productionGains  = getProductionGains ()
  val degradationGains = getDegradationGains()

  /**
  * Convert all nodes in the SBML model to CyNodes.
  */
  def convertNodes(productionGains: Map[String, Double], degradationGains: Map[String, Double]): collection.mutable.Map[String, CyNode] = {
    // http://stackoverflow.com/questions/5042878/how-can-i-convert-immutable-map-to-mutable-map-in-scala
    val result = new LinkedHashMap[String, CyNode]
    for (i <- 0 until model.getNumSpecies) {
      val id = model.getSpecies(i).getId
      val node = new CyNode(id,
                 productionGains.getOrElse(id, 0),
                 degradationGains.getOrElse(id, 0))
      if (evaluator.species_init_defined.contains(id))
        node.initial_value = Some(evaluator.species_init_defined(id))
      result += id -> node
    }
    result
  }

  /**
  * Figure out the type of reaction (e.g. mass-action, Michaelis-Menten, etc.).
  */
  def mapSBMLtoArchetype(rx: SBMLReaction) = {
    val r:   Seq[String] = for (j <- 0 until rx.getNumReactants) yield rx.getReactant(j).getSpecies
    val p:   Seq[String] = for (j <- 0 until rx.getNumProducts) yield rx.getProduct(j).getSpecies
    val mod: Seq[String] = for (j <- 0 until rx.getNumModifiers) yield rx.getModifier(j).getSpecies

    val kin = rx.getKineticLaw
    val math = kin.getMath
    val m = Martin.fromSBMLMath(math)
    val a = MapToArchetype(m, r, p, mod)

    if (false) {
      // print matching archetype
      println("")
      println(s"mapSBMLtoArchetype: trying to map SBML reaction ${rx.getId}")
      println("Found archetype:")
      println(a map {_.getName} getOrElse "")
      println("")
      printMartinSummary(m, r, p, mod)
    }

    (a,r,p,m,mod)
  }

  def computeReaction_kf(rx: SBMLReaction, a: Archetype, u: MathExpr): Option[String] = {
    val r: Seq[String] = for (j <- 0 until rx.getNumReactants) yield rx.getReactant(j).getSpecies
    val p: Seq[String] = for (j <- 0 until rx.getNumProducts) yield rx.getProduct(j).getSpecies
    val mod: Seq[String] = for (j <- 0 until rx.getNumModifiers) yield rx.getModifier(j).getSpecies
    a match {
      case ArchetypeMassActionIrrevZeroOrder => SymbolMapperZeroOrder_kf(u,evaluator)(r,p)
      case ArchetypeMassActionIrrevUni => SymbolMapperIrrevUni_kf(u,evaluator)(r,p)
      case ArchetypeMassActionIrrevUniNoDepletion => SymbolMapperIrrevUniMod_kf(u,evaluator)(mod,p)
      case ArchetypeTranslationWithDeg => SymbolMapperRevUniUni_kf(u,evaluator)(mod,p)
      case ArchetypeMassActionRevUniUni => SymbolMapperRevUniUni_kf(u,evaluator)(r,p)
      case ArchetypeMassActionRevBiUni => SymbolMapperRevBiUni_kf(u,evaluator)(r,p)
      case ArchetypeMassActionRevUniBi => SymbolMapperRevUniBi_kf(u,evaluator)(r,p)
      case ArchetypeMassActionRevMonomer => SymbolMapperRevMonomer_kf(u,evaluator)(r,p)
      case ArchetypeMassActionIrrevDimer => None
      case ArchetypeMassActionRevDimer => SymbolMapperRevDimer_kf(u,evaluator)(r,p)
      case ArchetypeMassActionIrrevBi => None
      case _ => None
    }
  }

  def computeReaction_kr(rx: SBMLReaction, a: Archetype, u: MathExpr): Option[String] = {
    val r: Seq[String] = for (j <- 0 until rx.getNumReactants) yield rx.getReactant(j).getSpecies
    val p: Seq[String] = for (j <- 0 until rx.getNumProducts) yield rx.getProduct(j).getSpecies
    val mod: Seq[String] = for (j <- 0 until rx.getNumModifiers) yield rx.getModifier(j).getSpecies
    a match {
      case ArchetypeMassActionIrrevZeroOrder => None
      case ArchetypeMassActionIrrevUni => None
      case ArchetypeMassActionIrrevUniNoDepletion => None
      case ArchetypeTranslationWithDeg => SymbolMapperRevUniUni_kr(u,evaluator)(mod,p)
      case ArchetypeMassActionRevUniUni => SymbolMapperRevUniUni_kr(u,evaluator)(r,p)
      case ArchetypeMassActionRevBiUni => SymbolMapperRevBiUni_kr(u,evaluator)(r,p)
      case ArchetypeMassActionRevUniBi => SymbolMapperRevUniBi_kr(u,evaluator)(r,p)
      case ArchetypeMassActionRevMonomer => SymbolMapperRevMonomer_kr(u,evaluator)(r,p)
      case ArchetypeMassActionIrrevDimer => None
      case ArchetypeMassActionRevDimer => SymbolMapperRevDimer_kr(u,evaluator)(r,p)
      case ArchetypeMassActionIrrevBi => None
      case _ => None
    }
  }

  def getMargin(reaction_id: String): Double = {
    margins.getOrElse(reaction_id, default_margin)
  }

  def assignMichaelisMentenParams(rx: SBMLReaction, reactants: Seq[String], products: Seq[String], u: MathExpr, binding: CyRxBiUni, catalysis: CyRxUniBi): Unit = {
    val kcat = SymbolMapperIrrevMM_kcat(u,evaluator)(reactants)
    if (kcat.isEmpty)
      throw new RuntimeException(s"Could not determine kcat for SBML reaction ${rx.getId}")

    val KM = SymbolMapperIrrevMM_KM(u,evaluator)(reactants)
    if (KM.isEmpty)
      throw new RuntimeException(s"Could not determine KM for SBML reaction ${rx.getId}")

    val margin = getMargin(rx.getId)
    catalysis.kf = kcat.map(kcat => evaluator.getSymbolValue(kcat))
    binding.kf = Some(margin)
    binding.kr = KM.map(KM => {
      val kr = (binding.kf.get * evaluator.getSymbolValue(KM))
      val kcat = catalysis.kf.get
      if (kr > 2d*kcat)
        kr - kcat
      else
        kr
    })
  }

  def assignBottsMoralesInhIrrevParams(rx: SBMLReaction, reactants: Seq[String], modifiers: Seq[String], u: MathExpr, substrate_binding: CyRxBiUni, inhibitor_binding: CyRxBiUni, catalysis: CyRxUniBi): Unit = {
    val kcat = SymbolBottsMoralesInhIrrev_kcat(u,evaluator)(reactants, modifiers)
    if (kcat.isEmpty)
      throw new RuntimeException(s"Could not determine kcat for SBML reaction ${rx.getId}")

    val KM = SymbolBottsMoralesInhIrrev_Km(u,evaluator)(reactants, modifiers)
    if (KM.isEmpty)
      throw new RuntimeException(s"Could not determine KM for SBML reaction ${rx.getId}")

    val KI = SymbolBottsMoralesInhIrrev_KI(u,evaluator)(reactants, modifiers)
    if (KI.isEmpty)
      throw new RuntimeException(s"Could not determine KI for SBML reaction ${rx.getId}")

    val margin = getMargin(rx.getId)
    catalysis.kf = kcat.map(kcat => evaluator.getSymbolValue(kcat))
    substrate_binding.kf = Some(margin)
    substrate_binding.kr = KM.map(KM => substrate_binding.kf.get * evaluator.getSymbolValue(KM))
    inhibitor_binding.kf = Some(margin)
    inhibitor_binding.kr = KI.map(KI => inhibitor_binding.kf.get * evaluator.getSymbolValue(KI))
  }

  def assignBottsMoralesInhRevParams(rx: SBMLReaction, reactants: Seq[String], modifiers: Seq[String], products: Seq[String], u: MathExpr, substrate_binding: CyRxBiUni, inhibitor_binding: CyRxBiUni, catalysis: CyRxUniBi): Unit = {
    val Vf = SymbolBottsMoralesInhRev_Vf(u,evaluator)(reactants, modifiers, products)
    if (Vf.isEmpty)
      throw new RuntimeException(s"Could not determine kcat for SBML reaction ${rx.getId}")
    println(s"botts morales rev Vf $Vf")

    val Ks = SymbolBottsMoralesInhRev_Ks(u,evaluator)(reactants, modifiers, products)
    if (Ks.isEmpty)
      throw new RuntimeException(s"Could not determine Ks for SBML reaction ${rx.getId}")
    println(s"botts morales Ks $Ks")

    val KI = SymbolBottsMoralesInhRev_KI(u,evaluator)(reactants, modifiers, products)
    if (KI.isEmpty)
      throw new RuntimeException(s"Could not determine KI for SBML reaction ${rx.getId}")
    println(s"botts morales KI $KI")

    val Kp = SymbolBottsMoralesInhRev_Kp(u,evaluator)(reactants, modifiers, products)
    if (Kp.isEmpty)
      throw new RuntimeException(s"Could not determine Kp for SBML reaction ${rx.getId}")
    println(s"botts morales Kp $Kp")

    val Keq = SymbolBottsMoralesInhRev_Keq(u,evaluator)(reactants, modifiers, products)
    if (Keq.isEmpty)
      throw new RuntimeException(s"Could not determine Keq for SBML reaction ${rx.getId}")
    println(s"botts morales Keq $Keq")

    // val I0 = evaluator.getSymbolValue(modifiers(0))
    val I0 = 1d
    val S0 = evaluator.getSymbolValue(reactants(1))
    println(s"S0 $S0")

    val margin = getMargin(rx.getId)
    val rho = if (I0 > 1d) I0 else 1d
    catalysis.kr = Some(rho*S0)
    println(s"evaluator.getSymbolValue(Keq.get) = ${evaluator.getSymbolValue(Keq.get)}")
    println(s"evaluator.getSymbolValue(Ks.get) = ${evaluator.getSymbolValue(Ks.get)}")
    catalysis.kf = Keq.map(Keq => (evaluator.getSymbolValue(Keq)*(catalysis.kr.get)/(evaluator.getSymbolValue(Ks.get))))
    substrate_binding.kr = Some(rho)
    substrate_binding.kf = Ks.map(Ks => substrate_binding.kr.get * evaluator.getSymbolValue(Ks))
    inhibitor_binding.kr = Some(rho)
    inhibitor_binding.kf = KI.map(KI => inhibitor_binding.kr.get * evaluator.getSymbolValue(KI))
  }

  def assignRepressorParams(rx: SBMLReaction, modifiers: Seq[String], u: MathExpr, binding: CyRxBiUni, transcription: CyRxUniUni): Unit = {
    val Km = SymbolMapperRepressor_Km(u,evaluator)(modifiers)
    if (Km.isEmpty)
      throw new RuntimeException(s"Could not determine repressor Km for SBML reaction ${rx.getId}")

    val a_tr = SymbolMapperRepressor_a_tr(u,evaluator)(modifiers)
    if (a_tr.isEmpty)
      throw new RuntimeException(s"Could not determine repressor a_tr for SBML reaction ${rx.getId}")

    val margin = getMargin(rx.getId)
    transcription.kf = a_tr.map(a_tr => evaluator.getSymbolValue(a_tr))
    binding.kr = Some(margin)
    binding.kf = Km.map(Km => {
      sqrt(binding.kr.get) / evaluator.getSymbolValue(Km)
    })
  }

  def assignRepressorBasalGenericParams(rx: SBMLReaction, modifiers: Seq[String], u: MathExpr, binding: CyRxBiUni, transcription: CyRxUniUni, total_P: Double): Unit = {
    val a0_tr = SymbolMapperRepressorBasalGeneric_a0_tr(u,evaluator)(modifiers)
    if (a0_tr.isEmpty)
      throw new RuntimeException(s"Could not determine repressor a0_tr for SBML reaction ${rx.getId}")
    // println(s"assignRepressorBasalGenericParams a0_tr ${a0_tr.get}")

    val Km = SymbolMapperRepressorBasalGeneric_Km(u,evaluator)(modifiers)
    if (Km.isEmpty)
      throw new RuntimeException(s"Could not determine repressor Km for SBML reaction ${rx.getId}")

    val a_tr = SymbolMapperRepressorBasalGeneric_a_tr(u,evaluator)(modifiers)
    if (a_tr.isEmpty)
      throw new RuntimeException(s"Could not determine repressor a_tr for SBML reaction ${rx.getId}")

    val n = SymbolMapperRepressorBasalGeneric_n(u,evaluator)(modifiers)
    if (n.isEmpty)
      throw new RuntimeException(s"Could not determine repressor n for SBML reaction ${rx.getId}")

    n.map(n => {binding.n = evaluator.getSymbolValue(n).toInt})

    // println(s"repressor generic params a0_tr ${a0_tr.get} Km ${Km.get} a_tr ${a_tr.get} n ${n.get}")


    // println(s"Km = ${Km.get}")
    // println(s"symbol val for Km = ${evaluator.getSymbolValue(Km.get)}")

    val margin = getMargin(rx.getId)
    transcription.kf = a_tr.map(a_tr => evaluator.getSymbolValue(a_tr)/total_P)
    a0_tr.map(a0_tr => {transcription.basal = evaluator.getSymbolValue(a0_tr)})
    binding.kr = Some(margin)
    binding.kf = Km.map(Km => {
      sqrt(binding.kr.get) / evaluator.getSymbolValue(Km)
    })
  }

  def assignRepressorBasalGenericDegParams(rx: SBMLReaction, modifiers: Seq[String], products: Seq[String], u: MathExpr, binding: CyRxBiUni, transcription: CyRxUniUni, total_P: Double): Unit = {
    val a0_tr = SymbolMapperRepressorBasalGenericDeg_a0_tr(u,evaluator)(modifiers, products)
    if (a0_tr.isEmpty)
      throw new RuntimeException(s"Could not determine repressor a0_tr for SBML reaction ${rx.getId}")
    // println(s"assignRepressorBasalGenericDegParams a0_tr ${a0_tr.get}")

    val Km = SymbolMapperRepressorBasalGenericDeg_Km(u,evaluator)(modifiers, products)
    if (Km.isEmpty)
      throw new RuntimeException(s"Could not determine repressor Km for SBML reaction ${rx.getId}")
    // println(s"assignRepressorBasalGenericDegParams Km ${Km.get}")

    val a_tr = SymbolMapperRepressorBasalGenericDeg_a_tr(u,evaluator)(modifiers, products)
    if (a_tr.isEmpty)
      throw new RuntimeException(s"Could not determine repressor a_tr for SBML reaction ${rx.getId}")
    // println(s"assignRepressorBasalGenericDegParams a_tr ${a_tr.get}")

    val n = SymbolMapperRepressorBasalGenericDeg_n(u,evaluator)(modifiers, products)
    if (n.isEmpty)
      throw new RuntimeException(s"Could not determine repressor n for SBML reaction ${rx.getId}")
    // println(s"assignRepressorBasalGenericDegParams n ${n.get}")

    val kd = SymbolMapperRepressorBasalGenericDeg_kd(u,evaluator)(modifiers, products)
    if (kd.isEmpty)
      throw new RuntimeException(s"Could not determine repressor kd for SBML reaction ${rx.getId}")
    // println(s"assignRepressorBasalGenericDegParams kd ${kd.get}")

    n.map(n => {binding.n = evaluator.getSymbolValue(n).toInt})

    // println(s"repressor generic params a0_tr ${a0_tr.get} Km ${Km.get} a_tr ${a_tr.get} n ${n.get}")


    // println(s"Km = ${Km.get}")
    // println(s"symbol val for Km = ${evaluator.getSymbolValue(Km.get)}")

    val margin = getMargin(rx.getId)
    transcription.kf = a_tr.map(a_tr => evaluator.getSymbolValue(a_tr)/total_P)
    transcription.kr = kd.map(kd => evaluator.getSymbolValue(kd))
    a0_tr.map(a0_tr => {transcription.basal = evaluator.getSymbolValue(a0_tr)})
    binding.kr = Some(margin)
    binding.kf = Km.map(Km => {
      sqrt(binding.kr.get) / evaluator.getSymbolValue(Km)
    })
  }

  /**
  * If certain species are produced in a zero-order reaction, set the corresponding gains.
  */
  def getProductionGains(): Map[String, Double] = {
    var m = collection.mutable.Map[String, Double]()
    for (rx <- for (i <- 0 until model.getNumReactions) yield model.getReaction(i)) {
      val (a,r,p,_,_) = mapSBMLtoArchetype(rx)
      if (!a.isEmpty) {
        a.get match {
          case ArchetypeMassActionIrrevZeroOrder => {
            if (rx.getNumProducts != 1)
              throw new RuntimeException(s"Expected exactly one product for reaction ${rx.getId}, found ${rx.getNumProducts}")
            m += (getSBMLSpeciesByID(model,rx.getProduct(0).getSpecies).getId -> 1.0)
          }
          case _ => ()
        }
      }
    }
    m toMap
  }

  def computeDegradationGain(rx: SBMLReaction, reactant: String): Double = {
    val m = Martin.fromSBMLMath(rx.getKineticLaw.getMath)
    SymbolMapperIrrevUni_kf(m,evaluator)(Seq(reactant),Seq[String]()).map(kd => evaluator.getSymbolValue(kd)).get
  }

  /**
  * If certain species are degraded, set the corresponding gains.
  */
  def getDegradationGains(): Map[String, Double] = {
    var m = collection.mutable.Map[String, Double]()
    for (rx <- for (i <- 0 until model.getNumReactions) yield model.getReaction(i)) {
      val (a,r,p,_,_) = mapSBMLtoArchetype(rx)
      if (!a.isEmpty) {
        a.get match {
          case ArchetypeMassActionIrrevUni => {
            if (rx.getNumReactants == 1 && rx.getNumProducts == 0) {
              // it's a degradation reaction
              val reactant = getSBMLSpeciesByID(model,rx.getReactant(0).getSpecies).getId
              m += (reactant -> computeDegradationGain(rx, reactant))
            }
          }
          case _ => ()
        }
      }
    }
    m toMap
  }

  /**
  * Convert a SBML reaction to a CyReaction.
  */
  def convertReaction(rx: SBMLReaction, nodes: collection.mutable.Map[String, CyNode], rxn_map: collection.mutable.Map[CyReaction, SBMLReaction], model: SBMLModel): List[CyReaction] = {
//     println(s"  ${rx.getId}:")
//     println(s"    ${(for (j <- 0 until rx.getNumReactants) yield rx.getReactant(j).getSpecies) mkString(" + ")} " +
//       s"${if (rx.isSetReversible && rx.getReversible) "<>" else ">"} " +
//       s"${(for (j <- 0 until rx.getNumProducts) yield rx.getProduct(j).getSpecies) mkString(" + ")}")

    val (a,r,p,m,mod) = mapSBMLtoArchetype(rx)

    def mapNodes(ids: Seq[String]): Seq[CyNode] = ids.map(x => nodes(x))

    val rxns: List[CyReaction] = a.fold(throw new RuntimeException(s"No archetype found for reaction ${rx.getId}"))(_ match {
      case ArchetypeMassActionIrrevZeroOrder => {
        if (p.length == 1)
          // it's constant production
          List(MakeCyRxConstProduction(p.map(x => nodes(x))))
        else
          throw new RuntimeException("Unknown zero-order reaction")
      }
      // uni-uni (reversible)
      case ArchetypeMassActionRevUniUni => List(MakeCyRxUniUni(mapNodes(r), mapNodes(p), true))
      case ArchetypeMassActionIrrevUni => {
        p match {
          // it's a degradation reaction
          case Seq() => List()
            // it's a uni-uni
          case Seq(p1) => List(MakeCyRxUniUni(mapNodes(r), mapNodes(p), false))
          case Seq(p1,p2) => {
            if (p1 == p2)
              // irrev monomerization
              List(MakeCyRxMonomerization(mapNodes(r), mapNodes(Seq(p1)), false))
            else
              // it's a uni-bi
              List(MakeCyRxUniBi(mapNodes(r), mapNodes(p), false))
          }
          case _ => throw new RuntimeException("Unknown uni reaction")
        }
      }
      case ArchetypeMassActionIrrevUniNoDepletion => {
        p match {
            // it's a uni-uni
          case Seq(p1) => {
            val reaction = MakeCyRxUniUni(mapNodes(mod), mapNodes(p), false)
            reaction.A_depletion = false
            List(reaction)
          }
          case _ => throw new RuntimeException("Unknown uni reaction")
        }
      }
      case ArchetypeTranslationWithDeg => {
        p match {
          case Seq(p1) => {
            val reaction = MakeCyRxUniUni(mapNodes(mod), mapNodes(p), true)
            reaction.A_depletion = false
            List(reaction)
          }
          case _ => throw new RuntimeException("Unknown uni reaction")
        }
      }
      // uni-bi
      case ArchetypeMassActionRevUniBi => List(MakeCyRxUniBi(mapNodes(r), mapNodes(p), true))
      case ArchetypeMassActionRevBiUni => List(MakeCyRxBiUni(mapNodes(r), mapNodes(p), true))
      // reversible monomerization
      case ArchetypeMassActionRevMonomer => List(MakeCyRxMonomerization(mapNodes(r), mapNodes(p.slice(0,1)), true))
      case ArchetypeMassActionIrrevDimer => {
        r match {
          case Seq(r1,r2) => {
            if (r1 == r2)
              List(MakeCyRxDimerization(mapNodes(Seq(r1)), mapNodes(p), false))
            else
              throw new RuntimeException(s"Expected identical reactants for dimerization reaction ${rx.getId}")
          }
          case _ => throw new RuntimeException(s"Expected two reactants for bi-reaction ${rx.getId}, found ${r.length}")
        }
      }
      case ArchetypeMassActionRevDimer => {
        r match {
          case Seq(r1,r2) => {
            if (r1 == r2)
              List(MakeCyRxDimerization(mapNodes(Seq(r1)), mapNodes(p), true))
            else
              throw new RuntimeException(s"Expected identical reactants for dimerization reaction ${rx.getId}")
          }
          case _ => throw new RuntimeException(s"Expected two reactants for bi-reaction ${rx.getId}, found ${r.length}")
        }
      }
      case ArchetypeMassActionIrrevBi => {
        r match {
          case Seq(r1,r2) => List(MakeCyRxBiUni(mapNodes(r), mapNodes(p), false))
          case _ => throw new RuntimeException(s"Expected two reactants for bi-reaction ${rx.getId}, found ${r.length}")
        }
      }
      case ArchetypeMassActionIrrevMichaelisMenten  => {
        (r,p) match {
        case (Seq(r1,r2),Seq(p1,p2)) => {
          val ES = new CyNode(r(0)+r(1))
          nodes += (genUniqueNodeID("ES_", nodes) -> ES)
          val binding = MakeCyRxBiUni(mapNodes(Seq(r1,r2)), Seq(ES), true)
          val catalysis = MakeCyRxUniBi(Seq(ES), mapNodes(Seq(p1,p2)), false)
          assignMichaelisMentenParams(rx, r, p, m, binding, catalysis)
          List(binding, catalysis)
        }
        case _ => throw new RuntimeException(s"Expected two reactants and two products for michaelis-menten reaction ${rx.getId}, found ${r.length} -> ${p.length}")
        }
      }
      case ArchetypeMassActionIrrevMichaelisMentenNoEnz  => {
        (r,p) match {
          case (Seq(r1),Seq(p1)) => {
            val Eid: String = genUniqueNodeID("E_", nodes)
            val ESid: String = Eid+"_"+r1
            val E = new CyNode(Eid)
            val ES = new CyNode(ESid)
            nodes += (Eid -> E)
            nodes += (ESid -> ES)
            val binding = MakeCyRxBiUni(Seq(E, nodes(r1)), Seq(ES), true)
            val catalysis = MakeCyRxUniBi(Seq(ES), Seq(E, nodes(p1)), false)
            assignMichaelisMentenParams(rx, r, p, m, binding, catalysis)
            List(binding, catalysis)
          }
          case _ => throw new RuntimeException(s"Expected one reactant and one product for michaelis-menten reaction ${rx.getId}, found ${r.length} -> ${p.length}")
        }
      }
      case ArchetypeRepressor  => {
        (mod,p) match {
          case (Seq(repr), Seq(prod)) => {
            // unbound promoter
            val Pid: String = genUniqueNodeID("P_", nodes)
            // bound promoter
            val Bid: String = genUniqueNodeID("B_", nodes)
            val P = new CyNode(Pid)
            P.initial_value = Some(1d)
            val B = new CyNode(Bid)
            nodes += (Pid -> P)
            nodes += (Bid -> B)

            val binding = MakeCyRxBiUni(Seq(P, nodes(repr)), Seq(B), true)
            binding.n = 2
            binding.B_depletion = false

            val transcription = MakeCyRxUniUni(Seq(P), Seq(nodes(prod)), false)
            transcription.A_depletion = false

            assignRepressorParams(rx, mod, m, binding, transcription)

            List(binding, transcription)
          }
          case _ => throw new RuntimeException(s"Expected one reactant and one product for michaelis-menten reaction ${rx.getId}, found ${r.length} -> ${p.length}")
        }
      }
      case ArchetypeRepressorBasalGeneric  => {
        (mod,p) match {
          case (Seq(repr), Seq(prod)) => {
            // unbound promoter
            val Pid: String = genUniqueNodeID("P_", nodes)
            // bound promoter
            val Bid: String = genUniqueNodeID("B_", nodes)
            val P = new CyNode(Pid)
            P.initial_value = Some(865d)
            val B = new CyNode(Bid)
            nodes += (Pid -> P)
            nodes += (Bid -> B)

            val binding = MakeCyRxBiUni(Seq(P, nodes(repr)), Seq(B), true)
            binding.B_depletion = false

            val transcription = MakeCyRxUniUni(Seq(P), Seq(nodes(prod)), false)
            transcription.A_depletion = false

            assignRepressorBasalGenericParams(rx, mod, m, binding, transcription, P.initial_value.get)

            List(binding, transcription)
          }
          case _ => throw new RuntimeException(s"Expected one reactant and one product for michaelis-menten reaction ${rx.getId}, found ${r.length} -> ${p.length}")
        }
      }
      case ArchetypeRepressorBasalGenericDeg  => {
        (mod,p) match {
          case (Seq(repr), Seq(prod)) => {
            // unbound promoter
            val Pid: String = genUniqueNodeID("P_", nodes)
            // bound promoter
            val Bid: String = genUniqueNodeID("B_", nodes)
            val P = new CyNode(Pid)
            P.initial_value = Some(865d)
            val B = new CyNode(Bid)
            nodes += (Pid -> P)
            nodes += (Bid -> B)

            val binding = MakeCyRxBiUni(Seq(P, nodes(repr)), Seq(B), true)
            binding.B_depletion = false

            val transcription = MakeCyRxUniUni(Seq(P), Seq(nodes(prod)), true)
            transcription.A_depletion = false

            assignRepressorBasalGenericDegParams(rx, mod, p, m, binding, transcription, P.initial_value.get)

            List(binding, transcription)
          }
          case _ => throw new RuntimeException(s"Expected one reactant and one product for michaelis-menten reaction ${rx.getId}, found ${r.length} -> ${p.length}")
        }
      }
      case ArchetypeBottsMoralesInhIrrev  => {
        (r,mod,p) match {
          case (Seq(r1,r2),Seq(mod1),Seq(p1,p2)) => {
            val ES = new CyNode(r(0)+r(1))
            nodes += (genUniqueNodeID("ES_", nodes) -> ES)
            val EI = new CyNode(r(0)+mod(0))
            nodes += (genUniqueNodeID("EI_", nodes) -> EI)
            val substrate_binding = MakeCyRxBiUni(mapNodes(Seq(r1,r2)), Seq(ES), true)
            val inhibitor_binding = MakeCyRxBiUni(mapNodes(Seq(r1,mod1)), Seq(EI), true)
            val catalysis = MakeCyRxUniBi(Seq(ES), mapNodes(Seq(p1,p2)), false)
            assignBottsMoralesInhIrrevParams(rx, r, mod, m, substrate_binding, inhibitor_binding, catalysis)
            List(substrate_binding, inhibitor_binding, catalysis)
          }
          case _ => throw new RuntimeException(s"Expected two reactants, one modifier, and two products for Botts-Morales reaction ${rx.getId}, found ${r.length} -> ${p.length}")
        }
      }
      case ArchetypeBottsMoralesInhRev  => {
        (r,mod,p) match {
          case (Seq(r1,r2),Seq(mod1),Seq(p1,p2)) => {
            val ES = new CyNode(r(0)+r(1))
            nodes += (genUniqueNodeID("ES_", nodes) -> ES)
            val EI = new CyNode(r(0)+mod(0))
            nodes += (genUniqueNodeID("EI_", nodes) -> EI)
            val substrate_binding = MakeCyRxBiUni(mapNodes(Seq(r1,r2)), Seq(ES), true)
            val inhibitor_binding = MakeCyRxBiUni(mapNodes(Seq(r1,mod1)), Seq(EI), true)
            val catalysis = MakeCyRxUniBi(Seq(ES), mapNodes(Seq(p1,p2)), true)
            assignBottsMoralesInhRevParams(rx, r, mod, p, m, substrate_binding, inhibitor_binding, catalysis)
            List(substrate_binding, inhibitor_binding, catalysis)
          }
          case _ => throw new RuntimeException(s"Expected two reactants, one modifier, and two products for Botts-Morales reaction ${rx.getId}, found ${r.length} -> ${p.length}")
        }
      }
      case _ => throw new RuntimeException("Unknown archetype")
    })

    for (rxn <- rxns) {
      if (rxn.kf.isEmpty)
        rxn.kf = computeReaction_kf(rx, a.get, m).map(kf => evaluator.getSymbolValue(kf))
      if (rxn.kr.isEmpty)
        rxn.kr = computeReaction_kr(rx, a.get, m).map(kr => evaluator.getSymbolValue(kr))
      rxn_map += (rxn -> rx)
      if (!rx.getReversible)
        rxn.irreversible = true
    }
    rxns
  }


  var nodes = convertNodes(productionGains, degradationGains)
  val rxn_map = collection.mutable.Map[CyReaction, SBMLReaction]()
  val n = new CyNet ( nodes.values toSeq,
              (for (i <- 0 until model.getNumReactions) yield convertReaction(model.getReaction(i), nodes, rxn_map, model)) flatten )
//   val params = new AssignParameters(model)
//   params(n, rxn_map)

  def getNetwork(): CyNet = n
}
