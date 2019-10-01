// J Kyle Medley, 2016
// Converts a network to a wiring configuration

package com.cytocomp.mtt

import scala.collection.mutable.ListBuffer
import com.cytocomp.mtt._

abstract class Terminal(val block: Block) {
  def getName: String
  def getFullName: String = s"${block.getQuotedName}.${getName}"
  override def toString: String = getFullName
  def isInputTerminal(): Boolean
  def isOutputTerminal(): Boolean

  var observable: Option[Observable] = None
  def setObservable(name: String, double_val: Boolean = false): Unit = throw new RuntimeException("Set observable invalid except for output terminals")

  def setValue(value: Double): Unit = throw new RuntimeException("Set value invalid except for input terminals")
  def getValue(): Double = throw new RuntimeException("Get value invalid except for input terminals")
  // def getDigitizedValue(): java.util.List[Int] =
}

trait SignalReceiver

abstract class InputTerminal(block: Block) extends Terminal(block) with SignalReceiver {
  var incoming_ = ListBuffer.empty[Tuple2[SignalSource,Boolean]]

  def incoming: Seq[Tuple2[SignalSource,Boolean]] = incoming_

  /**
   * Add an incoming connection to this terminal.
   */
  def addIncoming(t: SignalSource, positive: Boolean = true) = incoming_ += Tuple2(t, positive)

  def numIncoming() = incoming_.length

  def isInputTerminal(): Boolean = true
  def isOutputTerminal(): Boolean = false

  var input: Option[Input] = None
  def setInput(i: Input): Unit = input = Some(i)

  /// Deprecated
  def setInputFromAmount(label: String, value: Double): Unit = {
    setInput(new Input(label, this))
  }

  var value_ = 0d
  override def setValue(value: Double): Unit = {
    value_ = value
    if (!input.isEmpty)
      input = None
  }

  override def getValue(): Double = {
    if (!input.isEmpty)
      input.get.value
    else
      value_
  }
}

trait SignalSource

abstract class OutputTerminal(block: Block) extends Terminal(block) with SignalSource {
  var outgoing_ = ListBuffer.empty[Tuple2[SignalReceiver,Boolean]]

  /**
   * Add an outgoing connection to this terminal.
   */
  def addOutgoing(t: SignalReceiver, positive: Boolean = true) = outgoing_ += Tuple2(t, positive)

  def numOutgoing() = outgoing_.length

  def isInputTerminal(): Boolean = false
  def isOutputTerminal(): Boolean = true

  def makeConnection(v: InputTerminal, positive: Boolean = true): Unit = {
    Terminal.makeConnection(this, v, positive)
  }

  override def setObservable(name: String, double_val: Boolean = false): Unit = {observable = Some(new Observable(name, this, double_val))}
}

object Terminal {
  /**
   * Makes a connection from u (outgoing) to v (incoming).
   */
  def makeConnection(u: OutputTerminal, v: InputTerminal, positive: Boolean = true): Unit = {
    u.addOutgoing(v, positive)
    v.addIncoming(u, positive)
  }
}

case class Atot    (b: Block) extends InputTerminal(b) {
  def getName: String = "Atot"
}

case class Afree   (b: Block) extends OutputTerminal(b) {
  def getName: String = "Afree"
}

case class Btot    (b: Block) extends InputTerminal(b) {
  def getName: String = "Btot"
}

case class Bfree   (b: Block) extends OutputTerminal(b) {
  def getName: String = "Bfree"
}

case class Ctot    (b: Block) extends OutputTerminal(b) {
  def getName: String = "Ctot"
}

case class Cfree   (b: Block) extends InputTerminal(b) {
  def getName: String = "Cfree"
}

case class Cfree_out(b: Block) extends OutputTerminal(b) {
  def getName: String = "Cfree_out"
}

case class Cdeg    (b: Block) extends InputTerminal(b) {
  def getName: String = "Cdeg"
}

case class Cprod   (b: Block) extends InputTerminal(b) {
  def getName: String = "Cprod"
}

case class Ctot_in (b: Block) extends InputTerminal(b) {
  def getName: String = "Ctot_in"
}

case class Dfree   (b: Block) extends InputTerminal(b) {
  def getName: String = "Dfree"
}

case class Dfree_out(b: Block)extends OutputTerminal(b) {
  def getName: String = "Dfree_out"
}

case class fw_up   (b: Block) extends OutputTerminal(b) {
  def getName: String = "fw_up"
}

case class rv_up   (b: Block) extends OutputTerminal(b) {
  def getName: String = "rv_up"
}

case class rate_fw (b: Block) extends OutputTerminal(b) {
  def getName: String = "rate_fw"
}

case class rate_rv (b: Block) extends OutputTerminal(b) {
  def getName: String = "rate_rv"
}

case class fw_tot  (b: Block) extends OutputTerminal(b) {
  def getName: String = "fw_tot"
}

case class rv_tot  (b: Block) extends OutputTerminal(b) {
  def getName: String = "rv_tot"
}

class Block(val name: String) {
  // true for second step in michaelis-menten
  // TODO: find a better way to do this
  var enzymatic_step = false
  // true if this block is the main producer of its product (true for all configs except fan-in)
  var main_producer = true
  var reversible = false

  // only for dissoc
  var driver: Block = this

  var _Anode: Option[CyNode] = None
  def Anode_is(node: CyNode): Boolean = _Anode.map(_ eq node).getOrElse(false)
  def Anode: CyNode = _Anode.get
  def Anode_=(node: CyNode) = {_Anode = Some(node)}

  var _Bnode: Option[CyNode] = None
  def Bnode_is(node: CyNode): Boolean = _Bnode.map(_ eq node).getOrElse(false)
  def Bnode: CyNode = _Bnode.get
  def Bnode_=(node: CyNode) = {_Bnode = Some(node)}

  var _Cnode: Option[CyNode] = None
  def Cnode_is(node: CyNode): Boolean = _Cnode.map(_ eq node).getOrElse(false)
  def Cnode: CyNode = _Cnode.get
  def Cnode_=(node: CyNode) = {_Cnode = Some(node)}

  // ** Terminals **

  private var _Atot = new Atot(this)
  /**
   * Return this block's Atot terminal.
   */
  def Atot = _Atot

  private var _Afree = new Afree(this)
  /**
   * Return this block's Afree terminal.
   */
  def Afree = _Afree

  private var _Btot = new Btot(this)
  /**
   * Return this block's Btot terminal.
   */
  def Btot = _Btot

  private var _Bfree = new Bfree(this)
  /**
   * Return this block's Bfree terminal.
   */
  def Bfree = _Bfree

  private var _Ctot = new Ctot(this)
  /**
   * Return this block's Ctot terminal.
   */
  def Ctot = _Ctot

  private var _Cfree = new Cfree(this)
  /**
   * Return this block's Cfree terminal.
   */
  def Cfree = _Cfree

  private var _Cfree_out = new Cfree_out(this)
  /**
   * Return this block's Cfree_out terminal.
   */
  def Cfree_out = _Cfree_out

  private var _Cdeg = new Cdeg(this)
  /**
   * Return this block's Cdeg terminal.
   */
  def Cdeg = _Cdeg

  private var _Cprod = new Cprod(this)
  /**
   * Return this block's Cprod terminal.
   */
  def Cprod = _Cprod

  private var _Ctot_in = new Ctot_in(this)
  /**
   * Return this block's Ctot_in terminal.
   */
  def Ctot_in = _Ctot_in

  private var _Dfree = new Dfree(this)
  /**
   * Return this block's Dfree terminal.
   */
  def Dfree = _Dfree

  private var _Dfree_out = new Dfree_out(this)
  /**
   * Return this block's Dfree output terminal.
   */
  def Dfree_out = _Dfree_out

  private var _fw_up = new fw_up(this)
  /**
   * Return this block's fw_up terminal.
   */
  def fw_up = _fw_up

  private var _rv_up = new rv_up(this)
  /**
   * Return this block's rv_up terminal.
   */
  def rv_up = _rv_up

  private var _rate_fw = new rate_fw(this)
  /**
   * Return this block's rate_fw terminal.
   */
  def rate_fw = _rate_fw

  private var _rate_rv = new rate_rv(this)
  /**
   * Return this block's rate_rv terminal.
   */
  def rate_rv = _rate_rv

  private var _fw_tot = new fw_tot(this)
  /**
   * Return this block's fw_tot terminal.
   */
  def fw_tot = _fw_tot

  private var _rv_tot = new rv_tot(this)
  /**
   * Return this block's rv_tot terminal.
   */
  def rv_tot = _rv_tot

  def getTerminal(terminal_name: String): Terminal = terminal_name match {
    case "Atot" => this.Atot
    case "Afree" => this.Afree
    case "Btot" => this.Btot
    case "Bfree" => this.Bfree
    case "Ctot" => this.Ctot
    case "Cfree" => this.Cfree
    case "Cfree_out" => this.Cfree_out
    case "Cdeg" => this.Cdeg
    case "Cprod" => this.Cprod
    case "Ctot_in" => this.Ctot_in
    case "Dfree" => this.Dfree
    case "Dfree_out" => this.Dfree_out
    case "fw_up" => this.fw_up
    case "rv_up" => this.rv_up
    case "fw_tot" => this.fw_up
    case "rv_tot" => this.rv_up
    case "rate_fw" => this.rate_fw
    case "rate_rv" => this.rate_rv
  }

  // ** Constants **

  private var _ratC = 0d
  /**
   * Get the degradation gain (ratC) for this block.
   */
  def ratC = _ratC
  /**
   * Set the degradation gain (ratC) for this block.
   */
  def ratC_= (v: Double) = { _ratC = v }
  def set_ratC(v: Double) = { _ratC = v }

  private var _n = 1d
  /**
   * Get the Hill coefficient (n) for this block
   */
  def n = _n
  /**
   * Set the Hill coefficient (n) for this block.
   */
  def n_= (v: Double) = { _n = v }
  def set_n(v: Double) = { _n = v }

  private var _KDfw = 10d
  /**
   * Get the forward gain (KDfw) for this block
   */
  def KDfw = _KDfw
  /**
   * Set the forward gain (KDfw) for this block.
   */
  def KDfw_= (v: Double) = { _KDfw = v }
  def set_KDfw(v: Double) = { _KDfw = v }

  private var _KDrv = 10d
  /**
   * Get the reverse gain (KDrv) for this block
   */
  def KDrv = _KDrv
  /**
   * Set the reverse gain (KDrv) for this block.
   */
  def KDrv_= (v: Double) = { _KDrv = v }
  def set_KDrv(v: Double) = { _KDrv = v }

  private var _kdeg = 1d
  /**
   * Get the degradation gain (kdeg) for this block
   */
  def kdeg = _kdeg
  /**
   * Set the degradation gain (kdeg) for this block.
   */
  def kdeg_= (v: Double) = { _kdeg = v }
  def set_kdeg(v: Double) = { _kdeg = v }

  private var _kr = 1d
  /**
   * Get the product gain (kr) for this block
   */
  def kr = _kr
  /**
   * Set the product gain (kr) for this block.
   */
  def kr_= (v: Double) = { _kr = v }
  def set_kr(v: Double) = { _kr = v }

  // ** Switches **

  private var _A_FB_EN_sw = true
  /**
   * Get the product gain (A_FB_EN_sw) for this block
   */
  def A_FB_EN_sw = _A_FB_EN_sw
  def A_FB_EN = _A_FB_EN_sw
  /**
   * Set the product gain (A_FB_EN_sw) for this block.
   */
  def A_FB_EN_sw_= (v: Boolean) = { _A_FB_EN_sw = v }
  def set_A_FB_EN(v: Boolean) = { _A_FB_EN_sw = v }

  private var _B_FB_EN_sw = true
  /**
   * Get the product gain (B_FB_EN_sw) for this block
   */
  def B_FB_EN_sw = _B_FB_EN_sw
  def B_FB_EN = _B_FB_EN_sw
  /**
   * Set the product gain (B_FB_EN_sw) for this block.
   */
  def B_FB_EN_sw_= (v: Boolean) = { _B_FB_EN_sw = v }
  def set_B_FB_EN(v: Boolean) = { _B_FB_EN_sw = v }

  var Ctot_sw = false
  var FF_EN_sw1 = false
  var FF_EN_sw2 = true
  var FF_EN_sw3 = true
  var FF_EN_sw4 = false

  /**
   * Get the quoted name of this block (usu. a reaction string).
   */
  def getQuotedName: String = s"'$name'"

  def getName: String = s"$name"

  def terminals() = List(Atot, Afree, Btot, Bfree, Ctot, Cfree, Cfree_out, Cprod, Cdeg, Ctot_in, rate_fw, rate_rv, fw_up, rv_up, fw_tot, rv_tot)

  /**
   * Return the string representation of this block.
   */
  override def toString(): String = {
//     val connections = ( (for(u <- terminals()) yield {
//       ( for( v <- u.outgoing ) yield {
//         s"${u.getName} -> ${if (v._1.block != this) v._1.getFullName else v._1.getName}${if (v._2) "" else "-"}"
//       } ) ++
//       (if (u.observable.isEmpty) Seq.empty else Seq(s"${if (u.observable.get.double_val) "2" else ""}${u.getName} -> ${u.observable.get.getQuotedName}") )++
//       (if (u.input.isEmpty) Seq.empty else Seq(s"${u.getName} <- ${u.input.get.getQuotedName}") )
//     }) flatten ) ++
//     // degradation gain
//     (if (ratC != 0) Seq(s"ratC <- $ratC") else Seq.empty)
//     s"Block $name {${connections.mkString(", ")}}"
    s"Block $name"
  }

  def getInputCurrent(t: InputTerminal): Double = {
    if (!t.input.isEmpty)
      t.input.get.value
    else
      0d
  }

  def printCurrentConfig(): String = {
    Seq(
      toString(),
      s"  Atot = ${getInputCurrent(Atot)}",
      s"  Btot = ${getInputCurrent(Btot)}",
      s"  KDfw = $KDfw",
      s"  kr = $kr",
      s"  kdeg = $kdeg",
      s"  Dfree = ${getInputCurrent(Dfree)}",
      s"  KDrv = $KDrv",
      s"  ratC = $ratC",
      s"  n = $n"
    ).mkString("\n")
  }

  def input_terminals = Seq(Atot, Btot, Cfree, Cprod, Cdeg, Ctot_in, Dfree)

  def output_terminals = Seq(Afree, Bfree, Cfree_out, Ctot, rate_fw, rate_rv, fw_tot, rv_tot, fw_up, rv_up)
}

/**
 * Represents a one or two blocks comprising a single reaction
 */
class ReactionUnit(val name: String, val blocks: Seq[Block]) {
  // TODO: remove
  var irreversible: Boolean = false

  private var _Atot: Option[Atot] = None
  /**
   * Return this reaction unit's Atot terminal.
   */
  def Atot = {
    if (_Atot.isEmpty)
      throw new RuntimeException("Atot is not set")
    _Atot.get
  }
  def Atot_=(u: Atot) = { _Atot = Some(u) }

  private var _Afree: Option[Afree] = None
  /**
   * Return this reaction unit's Afree terminal.
   */
  def Afree = {
    if (_Afree.isEmpty)
      throw new RuntimeException("Afree is not set")
    _Afree.get
  }
  def Afree_=(u: Afree) = { _Afree = Some(u) }

  private var _Btot: Option[Btot] = None
  /**
   * Return this reaction unit's Btot terminal.
   */
  def Btot = {
    if (_Btot.isEmpty)
      throw new RuntimeException("Btot is not set")
    _Btot.get
  }
  def Btot_=(u: Btot) = { _Btot = Some(u) }

  private var _Bfree: Option[Bfree] = None
  /**
   * Return this reaction unit's Bfree terminal.
   */
  def Bfree = {
    if (_Bfree.isEmpty)
      throw new RuntimeException("Bfree is not set")
    _Bfree.get
  }
  def Bfree_=(u: Bfree) = { _Bfree = Some(u) }

  private var _Ctot: Option[Ctot] = None
  /**
   * Return this reaction unit's Ctot terminal.
   */
  def Ctot = {
    if (_Ctot.isEmpty)
      throw new RuntimeException("Ctot is not set")
    _Ctot.get
  }
  def Ctot_=(u: Ctot) = { _Ctot = Some(u) }

  private var _Cfree: Option[Cfree] = None
  /**
   * Return this reaction unit's Cfree terminal.
   */
  def Cfree = {
    if (_Cfree.isEmpty)
      throw new RuntimeException("Cfree is not set")
    _Cfree.get
  }
  def Cfree_=(u: Cfree) = { _Cfree = Some(u) }

  private var _Cfree_out: Option[Cfree_out] = None
  /**
   * Return this reaction unit's Cfree_out terminal.
   */
  def Cfree_out = {
    if (_Cfree_out.isEmpty)
      throw new RuntimeException("Cfree_out is not set")
    _Cfree_out.get
  }
  def Cfree_out_=(u: Cfree_out) = { _Cfree_out = Some(u) }

  private var _Cdeg: Option[Cdeg] = None
  /**
   * Return this reaction unit's Cdeg terminal.
   */
  def Cdeg = {
    if (_Cdeg.isEmpty)
      throw new RuntimeException("Cdeg is not set")
    _Cdeg.get
  }
  def Cdeg_=(u: Cdeg) = { _Cdeg = Some(u) }

  private var _Cprod: Option[Cprod] = None
  /**
   * Return this reaction unit's Cprod terminal.
   */
  def Cprod = {
    if (_Cprod.isEmpty)
      throw new RuntimeException("Cprod is not set")
    _Cprod.get
  }
  def Cprod_=(u: Cprod) = { _Cprod = Some(u) }

  private var _Ctot_in: Option[Ctot_in] = None
  /**
   * Return this reaction unit's Ctot_in terminal.
   */
  def Ctot_in = {
    if (_Ctot_in.isEmpty)
      throw new RuntimeException("Ctot_in is not set")
    _Ctot_in.get
  }
  def Ctot_in_=(u: Ctot_in) = { _Ctot_in = Some(u) }

  private var _ratC: Option[Double] = None
  /**
   * Return this reaction unit's ratC parameter.
   */
  def ratC = {
    if (_ratC.isEmpty)
      throw new RuntimeException("ratC is not set")
    _ratC.get
  }
  def ratC_=(u: Double) = {
    _ratC = Some(u)
    for (b <- blocks)
      b.ratC = u
  }

  private var _Dfree: Option[Dfree] = None
  /**
   * Return this reaction unit's Dfree terminal.
   */
  def Dfree = {
    if (_Dfree.isEmpty)
      throw new RuntimeException("Dfree is not set")
    _Dfree.get
  }
  def Dfree_=(u: Dfree) = { _Dfree = Some(u) }

  private var _fw_up: Option[fw_up] = None
  /**
   * Return this reaction unit's fw_up terminal.
   */
  def fw_up = {
    if (_fw_up.isEmpty)
      throw new RuntimeException("fw_up is not set")
    _fw_up.get
  }
  def fw_up_=(u: fw_up) = { _fw_up = Some(u) }

  private var _rv_up: Option[rv_up] = None
  /**
   * Return this reaction unit's rv_up terminal.
   */
  def rv_up = {
    if (_rv_up.isEmpty)
      throw new RuntimeException("rv_up is not set")
    _rv_up.get
  }
  def rv_up_=(u: rv_up) = { _rv_up = Some(u) }

  private var _rate_fw: Option[rate_fw] = None
  /**
   * Return this reaction unit's rate_fw terminal.
   */
  def rate_fw = {
    if (_rate_fw.isEmpty)
      throw new RuntimeException("rate_fw is not set")
    _rate_fw.get
  }
  def rate_fw_=(u: rate_fw) = { _rate_fw = Some(u) }

  private var _rate_rv: Option[rate_rv] = None
  /**
   * Return this reaction unit's rate_rv terminal.
   */
  def rate_rv = {
    if (_rate_rv.isEmpty)
      throw new RuntimeException("rate_rv is not set")
    _rate_rv.get
  }
  def rate_rv_=(u: rate_rv) = { _rate_rv = Some(u) }

  private var _A_FB_EN_sw: Option[Boolean] = None
  /**
   * Return this reaction unit's A_FB_EN_sw parameter.
   */
  def A_FB_EN_sw = {
    if (_A_FB_EN_sw.isEmpty)
      throw new RuntimeException("A_FB_EN_sw is not set")
    _A_FB_EN_sw.get
  }
  def A_FB_EN_sw_=(u: Boolean) = {
    _A_FB_EN_sw = Some(u)
    for (b <- blocks)
      b.A_FB_EN_sw = u
  }

}

/**
 * Represents a measurement taken at a terminal
 */
// TODO: make Observable inherit from InputPort
class Observable(val name: String, val terminal: OutputTerminal, val double_val: Boolean = false) extends SignalReceiver {
  def getQuotedName(): String = "'" + name + "'"
  override def toString(): String = {
    name
  }
}

/**
 * Represents input to a terminal
 */
class Input(val name: String, val terminal: Terminal, val value: Double = 1d) extends SignalSource {
  def getQuotedName(): String = "'" + name + "'"
}

class BlockConfig(val n: CyNet) {

  // maps nodes to the block(s) that produce them
  var producer_map = collection.mutable.Map[CyNode, Seq[ReactionUnit]]()
  for(node <- n.n) { producer_map += ( node -> Seq[ReactionUnit]() ) }

  def producers(node: CyNode): Seq[ReactionUnit] = producer_map(node)

  def producers_irreversible(node: CyNode): Boolean = {
    for (p <- producers(node))
      if (!p.irreversible)
        return false
    return true
  }

  var product_map = collection.mutable.Map[Block, CyNode]()

  def product(b: Block): CyNode = product_map(b)

  var _producer_blocks = collection.mutable.Map[CyNode, Seq[Block]]()
  for(node <- n.n) { _producer_blocks += ( node -> Seq[Block]() ) }

  def producer_blocks(node: CyNode): Seq[Block] = _producer_blocks(node)

  var _main_producer_block = collection.mutable.Map[CyNode, Block]()

  def has_main_producer_block(node: CyNode): Boolean = _main_producer_block.contains(node)
  def main_producer_block(node: CyNode): Block = _main_producer_block(node)

  var _reversible_producer_blocks = collection.mutable.Map[CyNode, Seq[Block]]()
  for(node <- n.n) { _reversible_producer_blocks += ( node -> Seq[Block]() ) }

  def reversible_producer_blocks(node: CyNode): Seq[Block] = _reversible_producer_blocks(node)

  var consumer_map = collection.mutable.Map[CyNode, Seq[ReactionUnit]]()
  for(node <- n.n) { consumer_map += ( node -> Seq[ReactionUnit]() ) }

  def consumers(node: CyNode): Seq[ReactionUnit] = consumer_map(node)

  var terminal_map = collection.mutable.Map[CyNode, Terminal]() // TODO: delete?

  def terminal(node: CyNode): Option[Terminal] = { // TODO: delete?
    if (terminal_map contains node)
      Some(terminal_map(node))
    else
      None
  }

  var reaction_map = collection.mutable.Map[CyReaction, ReactionUnit]()

  /**
   * Get the ReactionUnit associated with a reaction.
   */
  def reaction_unit(reaction: CyReaction): ReactionUnit = reaction_map(reaction)

  def reaction_units() = reaction_map.values

  def convertReaction(r: CyReaction): Seq[Block] = {
    r match {
      case x: CyRxUniUni => {
        val b = new Block(r.toString)

        if (r.enzymatic_step == true)
          b.enzymatic_step = true
        if (producer_blocks(x.p).length > 0)
          // if this var is already being produced, designate non-main producer
          b.main_producer = false
        else
          _main_producer_block += (x.p -> b)
        product_map += (b -> x.p)

        val ru = new ReactionUnit(r.toString, Seq(b))
        ru.Atot = b.Atot
        ru.Afree = b.Afree
        ru.Btot = b.Btot
        ru.Bfree = b.Bfree
        ru.Ctot = b.Ctot
        ru.Cfree = b.Cfree
        ru.Cdeg = b.Cdeg
        ru.Cprod = b.Cprod
        ru.ratC = b.ratC
        ru.fw_up = b.fw_up
        ru.rv_up = b.rv_up
        ru.rate_fw = b.rate_fw
        ru.rate_rv = b.rate_rv
        ru.irreversible = r.irreversible
        reaction_map += (r -> ru)

        // disable feedback for B since it's a uni reaction
        b.B_FB_EN_sw = false

        terminal_map += x.r -> b.Afree
        val sq = Seq(b)
        producer_map(x.p) ++= Seq(ru)
        _producer_blocks(x.p) ++= Seq(b)
        // for michaelis-menten
        if (!consumer_map.contains(x.r))
          consumer_map += ( x.r -> Seq[ReactionUnit]() )
        consumer_map(x.r) ++= Seq(ru)
        sq
      }
      case x: CyRxBiUni => {
        val b = new Block(r.toString)

        if (producer_blocks(x.p).length > 0)
          // if this var is already being produced, designate non-main producer
          b.main_producer = false
        else
          _main_producer_block += (x.p -> b)
        product_map += (b -> x.p)

        val ru = new ReactionUnit(r.toString, Seq(b))
        ru.Atot = b.Atot
        ru.Afree = b.Afree
        ru.Btot = b.Btot
        ru.Bfree = b.Bfree
        ru.Ctot = b.Ctot
        ru.Cfree = b.Cfree
        ru.Cdeg = b.Cdeg
        ru.Cprod = b.Cprod
        ru.ratC = b.ratC
        ru.fw_up = b.fw_up
        ru.rv_up = b.rv_up
        ru.rate_fw = b.rate_fw
        ru.rate_rv = b.rate_rv
        ru.irreversible = r.irreversible
        reaction_map += (r -> ru)

        terminal_map += x.r1 -> b.Afree
        terminal_map += x.r2 -> b.Bfree
        val sq = Seq(b)
        consumer_map(x.r1) ++= Seq(ru)
        consumer_map(x.r2) ++= Seq(ru)
        // for michaelis-menten
        if (!producer_map.contains(x.p)) {
          producer_map += ( x.p -> Seq[ReactionUnit]() )
          _producer_blocks += ( x.p -> Seq[Block]() )
        }
        producer_map(x.p) ++= Seq(ru)
        _producer_blocks(x.p) ++= Seq(b)
        sq
      }
      case x: CyRxDimerization => {
        val b = new Block(r.toString)

        if (producer_blocks(x.p).length > 0)
          // if this var is already being produced, designate non-main producer
          b.main_producer = false
        else
          _main_producer_block += (x.p -> b)
        product_map += (b -> x.p)

        // add connections required for a dimerization block
        Terminal.makeConnection(b.Afree, b.Btot)
        Terminal.makeConnection(b.Ctot, b.Atot, false)

        val ru = new ReactionUnit(r.toString, Seq(b))
        ru.Atot = b.Atot
        ru.Afree = b.Afree
        ru.Btot = b.Btot
        ru.Bfree = b.Bfree
        ru.Ctot = b.Ctot
        ru.Cfree = b.Cfree
        ru.Cdeg = b.Cdeg
        ru.Cprod = b.Cprod
        ru.ratC = b.ratC
        ru.fw_up = b.fw_up
        ru.rv_up = b.rv_up
        ru.rate_fw = b.rate_fw
        ru.rate_rv = b.rate_rv
        ru.irreversible = r.irreversible
        reaction_map += (r -> ru)

        // disable feedback for B
        b.B_FB_EN_sw = false

        terminal_map += x.r -> b.Afree
        val sq = Seq(b)
        consumer_map(x.r) ++= Seq(ru)
        producer_map(x.p) ++= Seq(ru)
        _producer_blocks(x.p) ++= Seq(b)
        sq
      }
      case x: CyRxUniBi => {
        if (false && n.consumers(x.p1) == n.producers(x.r)) {
          // enzymatic
          // hack to detect enzymatic configuration
          // product (enzyme) is consumed by preceding reaction
          val b = new Block(r.toString)

          if (r.enzymatic_step == true)
            b.enzymatic_step = true
          if (producer_blocks(x.p2).length > 0)
            // if this var is already being produced, designate non-main producer
            b.main_producer = false
          else
            _main_producer_block += (x.p2 -> b)
          product_map += (b -> x.p2)

          // add connections required for a uni-bi reaction
          n.producers(x.r) match {
            case Seq(upstream) => {
              Terminal.makeConnection(b.Ctot, reaction_unit(upstream).Atot)
              Terminal.makeConnection(b.Afree, reaction_unit(upstream).Cfree)
            }
            case _ => None
          }

          val ru = new ReactionUnit(r.toString, Seq(b))
          ru.Atot = b.Atot
          ru.Afree = b.Afree
          ru.Ctot = b.Ctot
          ru.Cfree = b.Cfree
          ru.Cdeg = b.Cdeg
          ru.Cprod = b.Cprod
          ru.ratC = b.ratC
          ru.fw_up = b.fw_up
          ru.rv_up = b.rv_up
          ru.rate_fw = b.rate_fw
          ru.rate_rv = b.rate_rv
          ru.irreversible = r.irreversible
          reaction_map += (r -> ru)

          terminal_map += x.r -> b.Afree
          val sq = Seq(b)
          producer_map(x.p2) ++= Seq(ru)
          _producer_blocks(x.p2) ++= Seq(b)
          // for michaelis-menten
          if (!consumer_map.contains(x.r))
            consumer_map += ( x.r -> Seq[ReactionUnit]() )
          consumer_map(x.r) ++= Seq(ru)
          sq
        } else {
          // dissocation (3.5c)
          val b1 = new Block(r.toString + s" (for ${x.p1})")
          val b2 = new Block(r.toString + s" (for ${x.p2})")

          if (producer_blocks(x.p1).length > 0)
            // if this var is already being produced, designate non-main producer
            b1.main_producer = false
          else
            _main_producer_block += (x.p1 -> b1)
          product_map += (b1 -> x.p1)
          if (producer_blocks(x.p2).length > 0)
            // if this var is already being produced, designate non-main producer
            b2.main_producer = false
          else
            _main_producer_block += (x.p2 -> b2)
          product_map += (b2 -> x.p2)

          // add connections required for a uni-bi reaction
          Terminal.makeConnection(b1.rate_fw, b2.Cprod)
          Terminal.makeConnection(b1.rate_rv, b2.Cdeg)

          val ru = new ReactionUnit(r.toString, Seq(b1, b2))
          ru.Atot = b1.Atot
          ru.Afree = b1.Afree
          ru.Btot = b1.Btot
          ru.Bfree = b1.Bfree
          ru.Ctot = b1.Ctot
          ru.Cfree = b1.Cfree
  //         ru.Cdeg = b1.Cdeg
  //         ru.Cprod = b1.Cprod
          ru.ratC = b1.ratC
          ru.fw_up = b1.fw_up
          ru.rv_up = b1.rv_up
          ru.rate_fw = b1.rate_fw
          ru.rate_rv = b1.rate_rv
          ru.irreversible = r.irreversible
          reaction_map += (r -> ru)

          // disable feedback for B in first block since it's a uni reaction
          b1.B_FB_EN_sw = false
          // disable feedback for second block
          b2.A_FB_EN_sw = false

          terminal_map += x.r -> b1.Afree
          val sq = Seq(b1, b2)
          consumer_map(x.r) ++= Seq(ru)
          producer_map(x.p1) ++= Seq(ru)
          _producer_blocks(x.p1) ++= Seq(b1)
          producer_map(x.p2) ++= Seq(ru)
          _producer_blocks(x.p2) ++= Seq(b2)
          sq
        }
      }
      case x: CyRxMonomerization => {
        val b = new Block(r.toString)

        if (producer_blocks(x.p).length > 0)
          // if this var is already being produced, designate non-main producer
          b.main_producer = false
        else
          _main_producer_block += (x.p -> b)
        product_map += (b -> x.p)

        // add connections required for monomerization
        Terminal.makeConnection(b.Cfree_out, b.Dfree)
        // make an extra connection to Cfree
        Terminal.makeConnection(b.Ctot, b.Cfree)

        val ru = new ReactionUnit(r.toString, Seq(b))
        ru.Atot = b.Atot
        ru.Afree = b.Afree
        ru.Btot = b.Btot
        ru.Bfree = b.Bfree
        ru.Ctot = b.Ctot
        ru.Cfree = b.Cfree
        ru.Cdeg = b.Cdeg
        ru.Cprod = b.Cprod
        ru.ratC = b.ratC
        ru.fw_up = b.fw_up
        ru.rv_up = b.rv_up
        ru.rate_fw = b.rate_fw
        ru.rate_rv = b.rate_rv
        ru.irreversible = r.irreversible
        reaction_map += (r -> ru)

        // disable feedback for B
        b.B_FB_EN_sw = false

        terminal_map += x.r -> b.Afree
        val sq = Seq(b)
        consumer_map(x.r) ++= Seq(ru)
        producer_map(x.p) ++= Seq(ru)
        _producer_blocks(x.p) ++= Seq(b)
        sq
      }
      case x: CyRxConstProduction => {
        val b = new Block(r.toString)

        if (producer_blocks(x.p).length > 0)
          // if this var is already being produced, designate non-main producer
          b.main_producer = false
        else
          _main_producer_block += (x.p -> b)
        product_map += (b -> x.p)

        val ru = new ReactionUnit(r.toString, Seq(b))
        ru.Atot = b.Atot
        ru.Afree = b.Afree
        ru.Btot = b.Btot
        ru.Bfree = b.Bfree
        ru.Ctot = b.Ctot
        ru.Cfree = b.Cfree
        ru.Cdeg = b.Cdeg
        ru.Cprod = b.Cprod
        ru.ratC = b.ratC
        ru.fw_up = b.fw_up
        ru.rv_up = b.rv_up
        ru.rate_fw = b.rate_fw
        ru.rate_rv = b.rate_rv
        ru.irreversible = r.irreversible
        reaction_map += (r -> ru)

        // disable feedback for B since it's const production
        b.B_FB_EN_sw = false

        val sq = Seq(b)
        producer_map(x.p) ++= Seq(ru)
        _producer_blocks(x.p) ++= Seq(b)
        sq
      }
      case _ => throw new RuntimeException("Unknown reaction type")
    }
  }

  def convertReactions(n: CyNet): Seq[Block] = {
    (n.r.map(r => convertReaction(r))).flatten.toSeq
  }

  // create all blocks
  val blocks = convertReactions(n)
}

class Wiring(val blocks: Seq[Block], val observables: Seq[Observable]) {
  /**
   * Return the string representation of this wiring configuration.
   */
  override def toString(): String = {
    (for (b <- blocks) yield b.toString) mkString("\n")
  }

  def getNumBlocks(): Int = {
    blocks.length
  }

  def getBlock(index: Int) = {
    blocks(index)
  }

  def incoming(t: InputTerminal):  Iterable[Tuple2[SignalSource,Boolean]]   = t.incoming_

  def incoming(t: Terminal): Iterable[Tuple2[SignalSource,Boolean]] = {
    t match {
      case t: InputTerminal => incoming(t)
      case _ => Seq.empty[Tuple2[SignalSource,Boolean]]
    }
  }

  def outgoing(t: OutputTerminal): Iterable[Tuple2[SignalReceiver,Boolean]] = t.outgoing_

  def outgoing(t: Terminal): Iterable[Tuple2[SignalReceiver,Boolean]] = {
    t match {
      case t: OutputTerminal => outgoing(t)
      case _ => Seq.empty[Tuple2[SignalReceiver,Boolean]]
    }
  }

  def connect(u: OutputTerminal, v: InputTerminal, invert: Boolean = false): Unit = {
    Terminal.makeConnection(u, v)
  }
}

object Wiring {
  /**
   * Propagates the degradation signal to upstream blocks.
   * If the the current block has degradation,
   * it should be propagated to all upstream producecers.
   */
  def propagateDegradation(r: CyReaction, config: BlockConfig): Unit = {
    val current_reaction_unit = config.reaction_unit(r)
//     println(current_reaction_unit)
//     println(r)
    for (upstream_node <- r.reactants) {
//       println(s"  upstream_node = $upstream_node")
      val upstream_producers = config.n.producers(upstream_node)
      upstream_producers match {
        case Seq(upstream_producer) => {
//           println("right before rv_up")
//           println(current_reaction_unit)
          Terminal.makeConnection(current_reaction_unit.rv_up, config.reaction_unit(upstream_producer).Cdeg)
          // recursively propagate degradation signal
          propagateDegradation(upstream_producer, config)
        }
        case Seq(upstream_producer1, upstream_producer2) => {
          // TODO: refine
          // only one can degrade
          // used in loop
          Terminal.makeConnection(current_reaction_unit.rv_up, config.reaction_unit(upstream_producer1).Cdeg)
          propagateDegradation(upstream_producer1, config) // FIXME: potentially recursive for loops
        }
        case _ => None
      }
    }
  }

  def propagateDegradationInLoop(r: CyReaction, l: CyLoop, config: BlockConfig): Unit = {
    if (!l.contains(r))
      throw new RuntimeException("propagateDegradationInLoop called on reaction outside loop")
    val current_reaction_unit = config.reaction_unit(r)
    for (upstream_node <- r.reactants) {
      val upstream_producers = config.n.producers(upstream_node)
      upstream_producers match {
        case Seq(upstream_producer) => {
          Terminal.makeConnection(current_reaction_unit.rv_up, config.reaction_unit(upstream_producer).Cdeg)
          // recursively propagate degradation signal
          if (l.contains(upstream_producer))
            propagateDegradationInLoop(upstream_producer, l, config)
        }
        case _ => None
      }
    }
  }

  /**
   * Propagates the production signal to upstream blocks.
   * If the the current block has production,
   * it should be propagated to all upstream producecers.
   */
  def propagateProduction(r: CyReaction, config: BlockConfig): Unit = {
    val current_reaction_unit = config.reaction_unit(r)
    for (upstream_node <- r.reactants) {
      val upstream_producers = config.n.producers(upstream_node)
      upstream_producers match {
        case Seq(upstream_producer) => {
          Terminal.makeConnection(current_reaction_unit.fw_up, config.reaction_unit(upstream_producer).Cprod)
          propagateProduction(upstream_producer, config)
        }
        case _ => None
      }
    }
  }

  def fromNetwork(n: CyNet): Wiring = {
    val config = new BlockConfig(n)
    val blocks = config.blocks
    var derived_input = collection.mutable.Set[Terminal]()

    // iterate over all nodes
    for(node <- config.producer_map.keys) {
//       println(s"${n.producers(node).length} producers for node $node")
      n.producers(node) match {
        case Seq(p) => {
          // handle degradation
          if (node.degradationGain != 0) {
            // when degradation is present, set ratC
            config.reaction_unit(p).ratC = node.degradationGain
//             println(s"Set degradation gain for $p")
            // connect rv_up to upstream producers
            propagateDegradation(p, config)
          }
        }
        // multiple producers: fan-in
        case producers: Seq[CyReaction] => {
          if (node.degradationGain != 0) {
            for (b <- config.producer_blocks(node)) {
              // handle degradation
              // when degradation is present, set ratC
              if (b.main_producer)
                b.ratC = node.degradationGain
//               println(s"Set degradation gain for $p")
              // connect rv_up to upstream producers
            }
//             for (p <- producers) {
//               propagateDegradation(p, config)
//             }
          }
        }
        case _ => None
      }

      config.consumers(node) match {
        // no consumers: Cfree = Ctot
        case Seq() => {
          n.producers(node) match {
            case Seq(p1) => {
              for (p <- config.producer_blocks(node)) {
//               println(s"producers for $node: connect ${p.Ctot} to ${p.Cfree}")
                Terminal.makeConnection(p.Ctot, p.Cfree)
              }
            }
            case _ => {
              for (block <- config.producer_blocks(node)) {
                if (block.main_producer)
                  Terminal.makeConnection(block.Ctot, block.Cfree)
              }
            }
          }
        }
        // just one consumer
        case Seq(c) => {
          for (p <- config.producer_blocks(node)) {
            Terminal.makeConnection(p.Ctot, c.Atot)
          }
        }
        // two consumers: fan-out
        case Seq(c1,c2) => {
          Terminal.makeConnection(c1.Afree, c2.Atot)
          derived_input += c2.Atot
          // subtract Ctot of c2 from Atot (or Btot) of c1
          // FIXME: figure out which terminal of c1 (Atot or Btot)
          Terminal.makeConnection(c2.Ctot, c1.Atot, false)
          // for the second consumer, disable A_FB_EN_sw, since the incoming
          // Atot is already the desired "free" amount
          c2.A_FB_EN_sw = false
        }
        case x => None
      }
    }

    // assign Dfree for dissociation reactions (depends on whether Ctot = Cfree for second block)
    for (reaction <- n.r) {
      reaction match {
        case x: CyRxUniBi => {
          val ru = config.reaction_unit(x)
          if (ru.blocks.length == 2) {
            val (b1,b2) = (ru.blocks(0), ru.blocks(1))
            b2.Cfree.incoming match {
              case Seq() => Terminal.makeConnection(b2.Ctot, b1.Dfree)
              // TODO: make sure this works
              case Seq((cfree_input: OutputTerminal, positive)) => Terminal.makeConnection(cfree_input, b1.Dfree)
              case _ => None
            }
          }
        }
        case x: CyRxUniUni => {
          if (x.enzymatic_step) {
            val ru = config.reaction_unit(x)
            for (p <- config.producers(x.r)) {
              Terminal.makeConnection(ru.Afree, p.Cfree)
              // assumes enzymes come first
              Terminal.makeConnection(ru.Ctot, p.Atot)
            }
          }
        }
        case _ => None
      }
    }

    for (block <- blocks) {
      // wire non-main producers up to main producers
      val product = config.product(block)
      if (config.producers(config.product(block)).length == 2 && config.producers_irreversible(config.product(block))) {
        // irreversible case (hack to make loop work)
//         println(s"  irreversible case ${config.producers_irreversible(config.product(block))}")
        if (!block.main_producer) {
          val main_block = config.main_producer_block(product)
          Terminal.makeConnection(block.Ctot, block.Cfree)
          Terminal.makeConnection(block.rv_up, main_block.Cprod)
        } else if (block.main_producer && config.producers(product).length > 1) {
          // if this is the main producer, propagate production / degradation signals
          for (upstream_producer <- n.producers(product)) {
//             println(s"irreversible block ${block.getName}: propagate prod/deg signal for $product to $upstream_producer")
//             propagateDegradation(upstream_producer, config)
          }
        }
      } else {
        if (!block.main_producer) {
//             println(s"  reversible case ${config.producers_irreversible(config.product(block))}")
//             println(s"  ${config.producers(config.product(block))(0).irreversible}")
            val main_block = config.main_producer_block(product)
            Terminal.makeConnection(block.rate_fw, main_block.Cprod)
            Terminal.makeConnection(block.rate_rv, main_block.Cdeg)
            // ensure fw_tot and rv_tot are propagated to all non-main producers
            Terminal.makeConnection(main_block.fw_tot, block.Cprod)
            Terminal.makeConnection(main_block.rv_tot, block.Cdeg)
            Terminal.makeConnection(main_block.Cfree_out, block.Cfree)
            Terminal.makeConnection(main_block.Ctot, block.Ctot_in)
            // set switches for fan-in
            block.Ctot_sw   = true
            block.FF_EN_sw1 = true
            block.FF_EN_sw2 = false
            block.FF_EN_sw3 = false
            block.FF_EN_sw4 = true
        } else if (block.main_producer && config.producers(product).length > 1) {
          // if this is the main producer, propagate production / degradation signals
          for (upstream_producer <- n.producers(product)) {
//             println(s"block ${block.getName}: propagate prod/deg signal for $product to $upstream_producer")
            propagateProduction(upstream_producer, config)
            propagateDegradation(upstream_producer, config)
          }
        }
      }
    }

    // find observables
    val observables: List[Observable] = (for (node <- n.n) yield {
      // if this node is a main product of a block, that block's Cfree is the observable
      var obs: Option[Observable] = None
      if (config.has_main_producer_block(node)) {
        val block = config.main_producer_block(node)
        val Cfree = block.Cfree
        Cfree.incoming match {
          // no inputs - do nothing
          case Seq() => None
          // one input - Cfree can be used as an observable
          case Seq((incoming_terminal: OutputTerminal, positive)) => {
            incoming_terminal.observable = Some(new Observable(node.toString, incoming_terminal))
            obs = Some(incoming_terminal.observable.get)
          }
          // duplicate inputs
          case Seq((t1: OutputTerminal, positive1), (t2, positive2)) => {
            if (t1 == t2 && positive1 == positive2) {
              t1.observable = Some(new Observable(node.toString, t1, true))
              obs = Some(t1.observable.get)
            }
          }
          // http://stackoverflow.com/questions/6807540/scala-pattern-matching-on-sequences-other-than-lists
          case _ => None // throw new RuntimeException(s"Multiple inputs for terminal: $Cfree")
        }
        // otherwise, look at terminal_map to see where the observable goes
        if (obs.isEmpty) {
          config.terminal(node).map( t => t match { case t:OutputTerminal => {
            t.observable = Some(new Observable(node.toString, t))
            obs = t.observable
          }})
        }
        obs
      } else {
        // look at terminal_map to see where the observable goes
        if (obs.isEmpty) {
          config.terminal(node).map( t => t match { case t:OutputTerminal => {
            t.observable = Some(new Observable(node.toString, t))
            obs = t.observable
        }})
        }
        obs
      }
    }).toList.flatten

    for (block <- blocks) {
      // find cases where Cfree needs to be specified
      if (block.Atot.incoming.size > 1) {
        // Cfree != Ctot - Atot
        if (config.consumers(config.product(block)).length > 0)
          Terminal.makeConnection(config.consumers(config.product(block))(0).Afree, block.Cfree)
      }
    }

    for (l <- n.loops) {
      for (r <- l.r) {
        val ru = config.reaction_unit(r)
        for (block <- ru.blocks) {
          if (block.ratC == 0) {
            val product = config.product(block)
            // set arbitrary degradation gain
            block.ratC = 1
            propagateDegradationInLoop(r, l, config)
          }
        }
      }
    }

    // assign inputs
    // var inputs: Seq[Input] = Seq.empty[Input]
    for (reaction <- n.r) {
      val reaction_unit = config.reaction_unit(reaction)
      reaction match {
        case x: CyRxConstProduction => {
          reaction_unit.Atot.setInput(new Input(x.p.toString + "prod", reaction_unit.Atot))
          // production is constant, so disable Ctot feedback
          reaction_unit.A_FB_EN_sw = false
          // const production reactions always have B wired to unity
          reaction_unit.Btot.setInput(new Input("Unity", reaction_unit.Btot))
        }
        case x: CyRxBiUni => {
          // second condition: no blocks produce this node - it must be an input
          if (reaction_unit.Atot.numIncoming == 0 || (!config.has_main_producer_block(x.r1) && !derived_input.contains(reaction_unit.Atot))) // || config.producers(x.r1).length == 0
            reaction_unit.Atot.setInput(new Input(x.r1.toString + "tot", reaction_unit.Atot))

          if ((reaction_unit.Btot.numIncoming == 0 || (!config.has_main_producer_block(x.r2) && !derived_input.contains(reaction_unit.Atot))) && n.producers(x.r2).length == 0) {
            println(s"  setting input for ${reaction_unit.Btot} / ${x.r2}, which has ${n.producers(x.r2).length} producers")
            reaction_unit.Btot.setInput(new Input(x.r2.toString + "tot", reaction_unit.Btot))
          }
        }
        case x: CyRxUniUni => {
          // uni reactions always have B wired to unity
          reaction_unit.Btot.setInput(new Input("Unity", reaction_unit.Btot))
        }
        case x: CyRxUniBi => {
          // uni reactions always have B wired to unity
          reaction_unit.Btot.setInput(new Input("Unity", reaction_unit.Btot))
          if (reaction_unit.Atot.numIncoming == 0 || (!config.has_main_producer_block(x.r) && !derived_input.contains(reaction_unit.Atot)))
            reaction_unit.Atot.setInput(new Input(x.r.toString + "tot", reaction_unit.Atot))
        }
        case x: CyRxDimerization => {
          if (reaction_unit.Atot.numIncoming == 0 || (!config.has_main_producer_block(x.r) && !derived_input.contains(reaction_unit.Atot)))
            reaction_unit.Atot.setInput(new Input(x.r.toString + "tot", reaction_unit.Atot))
        }
        case x: CyRxMonomerization => {
          if (reaction_unit.Atot.numIncoming == 0 || (!config.has_main_producer_block(x.r) && !derived_input.contains(reaction_unit.Atot)))
            reaction_unit.Atot.setInput(new Input(x.r.toString + "dimertot", reaction_unit.Atot))
          reaction_unit.Btot.setInput(new Input("Unity", reaction_unit.Btot))
        }
        case _ => None
      }
    }

    val wiring = new Wiring(blocks, observables)
//     WriteWiringSim(wiring, config)
    wiring
  }
}

// creates a simulation matching this wiring
object WriteWiringSim {
  def findReaction(block: Block, config: BlockConfig): CyReaction = {
    for (r <- config.n.r) {
      if (config.reaction_unit(r).blocks == Seq(block))
        return r
    }
    null
  }

  def apply(wiring: Wiring, config: BlockConfig): Unit = {
    var block_name_map = collection.mutable.Map[Block, String]()
    var k = 0
    for (block <- wiring.blocks) {
      block_name_map += block -> s"Block${k}"
      k += 1
    }

    k = 0
    println("// Antimony model")
    for (block <- wiring.blocks) {
      println(s"// ${block.getName}")
      val Atot = s"Block${k}_Atot"
      val Afree = s"Block${k}_Afree"
      val Btot = s"Block${k}_Btot"
      val Bfree = s"Block${k}_Bfree"
      val Ctot = s"Block${k}_Ctot"
      val r = findReaction(block, config)
      val kinetics = (if (block.Btot.incoming.length == 0 && block.Btot.input.isEmpty) s"${r.getGain}*Block${k}_Afree" else s"${r.getGain}*Block${k}_Afree*Block${k}_Bfree")
      println(s"J$k: -> $Ctot; $kinetics")
      println(s"$Afree := $Atot - $Ctot")
      println(s"$Bfree := $Btot - $Ctot")
      // outputs
      for(u <- List(block.Afree, block.Bfree, block.Ctot)) {
        if (!u.observable.isEmpty)
          println(s"${u.observable.get.name} := ${block_name_map(block)}_${u.getName}")
      }
      // inputs
      for(u <- List(block.Atot, block.Btot)) {
        if (!u.input.isEmpty)
          println(s"${block_name_map(block)}_${u.getName} := ${u.input.get.name}")
      }
      k += 1
    }
  }
}
