// J Kyle Medley, 2018
// SRAM programming

package com.cytocomp.mtt

import com.cytocomp.mtt._

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.ArrayBuffer

import collection.mutable
import collection.JavaConverters._

abstract class RoutingRule {
  def dump(): java.util.Map[String,Any]
  def getVariable(): Int
  def getBlock(): Int
  def getGroup(): Int
  def getWire(): Int
}

case class RoutingRuleFromTerminal(val terminal: Terminal, val block: Int, val group: Int, val wire: Int, positive: Boolean, block_resources: BlockResources) extends RoutingRule {
  val variable = terminal match {
    // IN:
    case x: Atot  => 0
    case x: Btot  => 1
    case x: Cfree => 2
    case x: Dfree => 3
    case x: Cprod => 4
    case x: Cdeg  => 5
    // OUT:
    case x: Ctot  => block_resources.bumpCtotPos
    case x: Afree => 12 // TODO: needs resource allocation
    case x: Bfree => 14 // TODO: needs resource allocation
    case x: rv_up => 22 // TODO: needs resource allocation
  }

  def dump() = mutable.Map("terminal" -> terminal.getName, "variable" -> variable, "block" -> block, "group" -> group, "wire" -> wire).asJava

  def getVariable() = variable
  def getBlock() = block
  def getGroup() = group
  def getWire() = wire
}

case class RoutingRuleFromVariable(val variable: Int, val block: Int, val group: Int, val wire: Int) extends RoutingRule {
  def dump() = mutable.Map("terminal" -> "N/A", "variable" -> variable, "block" -> block, "group" -> group, "wire" -> wire).asJava

  def getVariable() = variable
  def getBlock() = block
  def getGroup() = group
  def getWire() = wire

  if (variable == 0 && block == 0 && group == 0)
    throw new RuntimeException("Should not be blank")
}

class SRAMResources(wiring: Wiring, val block_map: LinkedHashMap[Block,Int] = LinkedHashMap[Block,Int](), val group_map: LinkedHashMap[Block,Int] = LinkedHashMap[Block,Int]()) {
  var wire_counter: Int = 0
//   var block_counter: Int = 0
  var group_counter: Int = 0
  var group_size: Int = 0
  val rules = ArrayBuffer.empty[RoutingRule]
  var adc_counter: Int = 76
  val adc_usage = mutable.Set.empty[Int]

  def getAvailableWire(): Int = wire_counter

  def getAvailableADC(): Int = adc_counter

  def addRoutingRule(rule: RoutingRule): RoutingRule = {
    if (rule.getWire == wire_counter)
      wire_counter += 1
    if (rule.getWire == adc_counter) {
      adc_counter += 1
      adc_usage += rule.getWire
    }
    rules += rule
    rule
  }

  def getBlockNumber(block: Block): Int = {
    val n = block_map.getOrElseUpdate(block, group_size)
    group_map.getOrElseUpdate(block, group_counter)
    if (n == group_size) {
      group_size += 1
      if (group_size >= 3) { // FIXME: temp
        group_size = 0
        group_counter += 1
      }
    }
    n
  }

  def getGroupNumber(block: Block): Int = {
    group_map(block)
  }

  // make sure all block numbers are allocated in order
  wiring.blocks.map(block => getBlockNumber(block))
}

case class BlockResources(var Ctot_neg_counter: Int = 7, var Ctot_pos_counter: Int = 9) {
  def bumpCtotNeg() = {
    Ctot_neg_counter += 1
    Ctot_neg_counter-1
  }
  def bumpCtotPos() = {
    Ctot_pos_counter += 1
    Ctot_pos_counter-1
  }
}

class RoutingTable(val rules: Seq[RoutingRule]) {
}

class BlockGroup(val routing_tables: Seq[RoutingTable]) {
  def dump() = mutable.Map("routing_tables" -> routing_tables.map(_.rules.map(_.dump()).toBuffer.asJava).toBuffer.asJava).asJava
  def getRoutingRules(): Seq[RoutingRule] = routing_tables.flatMap(_.rules)
  def getNumRules(): Int = routing_tables.size
}

// trait ChipConfig {
//   def dump(): Any
// }

class ProteinChipOriginal(val groups: Seq[BlockGroup]) {
  def dump() = mutable.Map("groups" -> groups.map(_.dump()).toBuffer.asJava).asJava
  def getRoutingRules(): Seq[RoutingRule] = groups.flatMap(_.getRoutingRules())
  def getGroupsListJ(): java.util.List[BlockGroup] = groups.toBuffer.asJava
}

class SRAMProgram(val chip_configs: Seq[ProteinChipOriginal]) {
  def dump() = mutable.Map("chips" -> chip_configs.map(_.dump()).toBuffer.asJava).asJava

  def getRoutingRules(): Seq[RoutingRule] = chip_configs.flatMap(_.getRoutingRules())

  def getRoutingRulesJ() = {
    val rules = getRoutingRules()
    rules.toBuffer.asJava
  }

  def getOtherRuleForWire(rule: RoutingRule): Option[RoutingRule] = {
    getRoutingRules().filter(r => (r.getWire() == rule.getWire() && !(r eq rule))) match {
      case Seq(u) => Some(u)
      case _ => None
    }
  }

  def getOtherRulesForWire(rule: RoutingRule): Seq[RoutingRule] = {
    getRoutingRules().filter(r => (r.getWire() == rule.getWire() && !(r eq rule)))
  }

  def getChip(k: Int) = chip_configs(k)
}

object SRAMProgram {
  // routes blocks on a single chip
  def routeBlocks(blocks: Seq[Block], wiring: Wiring, resources: SRAMResources, block_resources_map: Map[Block,BlockResources], routed: mutable.Set[Terminal]): Seq[RoutingTable] = {
    blocks.map(block => new RoutingTable(
      block.terminals().flatMap(terminal =>
        (terminal match {
          case terminal: InputTerminal => {
            wiring.incoming(terminal).map{
              case (other_terminal: OutputTerminal, positive) => {
                if (!routed(terminal)) {
                  val wire = resources.getAvailableWire()
                  routed += other_terminal
                  Seq[RoutingRule](
                    resources.addRoutingRule(RoutingRuleFromTerminal(terminal, resources.getBlockNumber(terminal.block), resources.getGroupNumber(terminal.block), wire, positive, block_resources_map(terminal.block))),
                    resources.addRoutingRule(RoutingRuleFromTerminal(other_terminal, resources.getBlockNumber(other_terminal.block), resources.getGroupNumber(other_terminal.block), wire, positive, block_resources_map(other_terminal.block)))
                  )
                } else {
                  Seq.empty[RoutingRule]
                }
              }
              case _ => Seq.empty[RoutingRule]
            }
          }
          case _ => Seq.empty[Seq[RoutingRule]]
        }).flatten
        // TODO: convert to use signals
//         // wired to unity
//         terminal.input.map(i => resources.addRoutingRule(RoutingRuleFromVariable(26, resources.getBlockNumber(block), resources.getGroupNumber(block), resources.getAvailableWire()))) ++
//         // output observable - connect terminal
//         terminal.observable.map(o => resources.addRoutingRule(RoutingRuleFromTerminal(terminal, resources.getBlockNumber(block), resources.getGroupNumber(block), resources.getAvailableWire(), true, block_resources_map(block)))) ++
//         // output observable - connect output adc
//         terminal.observable.map(o => resources.addRoutingRule(RoutingRuleFromVariable(31, 0, 0, resources.getAvailableWire())))
    )))
  }

  def singleChipFromWiring(wiring: Wiring): SRAMProgram = {
    if (wiring.blocks.length > 20)
      throw new RuntimeException(s"Too many blocks for single chip - received ${wiring.blocks.length} blocks but maximum is 20")

    val resources = new SRAMResources(wiring)
    val block_resources_map = LinkedHashMap[Block,BlockResources](
      wiring.blocks.map(block => (block -> new BlockResources())): _*
    )
    val routed = LinkedHashSet[Terminal]()
    return new SRAMProgram(Seq(new ProteinChipOriginal(
      (1 to 4).map(group_num => new BlockGroup(routeBlocks(
        wiring.blocks.slice((group_num-1)*3, group_num*3), // FIXME: hack for repressilator
        wiring,
        resources,
        block_resources_map.toMap,
        routed)))
    )))
  }

  def programChipWithMappedBlocks(wiring: Wiring, block_map: LinkedHashMap[Block, Int]): SRAMProgram = {
    if (wiring.blocks.length > 20)
      throw new RuntimeException(s"Too many blocks for single chip - received ${wiring.blocks.length} blocks but maximum is 20")

    val intra_group_map = block_map.map({case (block, i) => block -> (i%5)})
    val inter_group_map = block_map.map({case (block, i) => block -> math.floor(i/5).toInt})
    val resources = new SRAMResources(wiring, intra_group_map, inter_group_map)
    val block_resources_map = LinkedHashMap[Block,BlockResources](
      wiring.blocks.map(block => (block -> new BlockResources())): _*
    )
    val routed = LinkedHashSet[Terminal]()
    return new SRAMProgram(Seq(new ProteinChipOriginal(
      (1 to 4).map(group_num => new BlockGroup(routeBlocks(
        wiring.blocks.slice((group_num-1)*3, group_num*3), // FIXME: hack for repressilator
        wiring,
        resources,
        block_resources_map.toMap,
        routed)))
    )))
  }
}

class CurrentDigitizer(val Iref_10u: Double = 20000d/2d, val upper_clamp: Double = 19000d, val boundary_factor: Double = 15.5) {
  var lower_clamp: Double = Iref_10u / scala.math.pow(2d,17)
// class CurrentDigitizer(val Iref_10u = 1, val upper_clamp = 1, val lower_clamp = 1, val boundary_factor = 1) {
  /// Returns (range,value)
  def apply(current: Double): Tuple2[Int,Int] = {
    if (current == 0d || current == -99d)
      // unlike MATLAB code, a current value of 0 passed to this function will return 0
      // (in MATLAB it returns the lower clamp value)
      (0,0)
    else {
      def p(k: Int, lsb_currents: Seq[Double]): Tuple2[Int,Int] = {
        lsb_currents match {
          case lsb_current +: Seq() => (7,scala.math.round((current/lsb_current).toFloat))
          case lsb_current +: rest => {
            val boundary = lsb_current * boundary_factor
            if (current < boundary)
              (k,scala.math.round((current/lsb_current).toFloat))
            else
              p(k+1, rest)
          }
        }
      }
      p(0, Seq(17, 15, 13, 11, 9, 7, 5, 3).map(n =>
        // 15.5
        Iref_10u/scala.math.pow(2d,n)))
    }
  }
}

object ShiftRegProgram {
  val digitizer = new CurrentDigitizer()
  def digitize(v: Double) = digitizer(v)
  def digitizeJ(v: Double): java.util.List[Int] = {
    val t = digitizer(v)
    List(t._1,t._2).toBuffer.asJava
  }
}
