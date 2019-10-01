// J Kyle Medley, 2018
// Converts a network to a wiring configuration

package com.cytocomp.mtt

import com.cytocomp.mtt._

import scala.collection.mutable
import scala.collection.immutable
import scala.collection.mutable.Set
import scala.collection.mutable.Map
import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.MultiMap
import scala.collection.mutable.ArrayBuffer

/**
 * Stores blocks and configuration information for a single reaction.
 * A single reaction can expand into multiple blocks. This class keeps
 * track of those blocks and associated producers and consumers for each node.
 *
 * @constructor Fully parameterized constructor; use [[com.cytocomp.mtt.ReactionTable2$]] instead.
 */
class ReactionTable2(val reaction: CyReaction,
                     val blocks: Seq[Block],
                     val producer_map: Map[CyNode, _ <: Set[Block]],
                     val consumer_map: Map[CyNode, _ <: Set[Block]],
                     val input_terminal_map: Map[CyNode, _ <: Set[InputTerminal]])
                     {

}

object ReactionTable2 {
  /**
   * Create a new reaction table for a reaction.
   * @param reaction The CyReaction to expand into a table.
   * @param blocks A sequence of blocks that were expanded from the reaction.
   * @param map_elts Zero or more tuples mapping from CyNodes to Blocks or vice-versa. If a tuple mapping a node to a block is encountered, it will be added to the consumer map. If a tuple mapping a block to a node is encountered, it will be added to the producer map.
   * {{{
   * val reaction: CyReaction
   * val r1: CyNode, r2: CyNode // reactants
   * val p: CyNode // product
   * val b: Block
   * val reaction_table1 = ReactionTable2(reaction, b, r1 -> b, r2 -> b) // add reactants r1 and r2 to consumer map for b
   * val reaction_table2 = ReactionTable2(reaction, b, b -> p) // add product p to producer map for b
   * val reaction_table3 = ReactionTable2(reaction, b, r1 -> b, r2 -> b, b -> p) // do both in one go
   * }}}
   */
  def apply(reaction: CyReaction, blocks: Seq[Block], map_elts: Tuple2[_,_]*): ReactionTable2 = {
    val producer_map = LinkedHashMap[CyNode,LinkedHashSet[Block]]()
    val consumer_map = LinkedHashMap[CyNode,LinkedHashSet[Block]]()
    val input_terminal_map = LinkedHashMap[CyNode,LinkedHashSet[InputTerminal]]()
    for (m <- map_elts) {
      m match {
        case (n: CyNode, b: Block) => {
          if (!blocks.contains(b))
            throw new RuntimeException("ReactionTable2 received producer/consumer mapping outside of blocks passed")
          consumer_map.getOrElseUpdate(n, LinkedHashSet()) += b
        }
        case (b: Block, n: CyNode) => {
          if (!blocks.contains(b))
            throw new RuntimeException("ReactionTable2 received producer/consumer mapping outside of blocks passed")
          producer_map.getOrElseUpdate(n, LinkedHashSet()) += b
        }
        case (n: CyNode, t: InputTerminal) => {
          if (!blocks.contains(t.block))
            throw new RuntimeException("ReactionTable2 received terminal mapping outside of blocks passed")
          input_terminal_map.getOrElseUpdate(n, LinkedHashSet()) += t
        }
        case _ => throw new RuntimeException("Could not match type, not a producer or consumer")
      }
    }
    new ReactionTable2(reaction, blocks, producer_map, consumer_map, input_terminal_map)
  }

  /**
   * Convenience overload when passing just one block.
   */
  def apply(reaction: CyReaction, b: Block, map_elts: Tuple2[_,_]*): ReactionTable2 =
    apply(reaction, List(b), map_elts:_*)

  /**
   * Convenience overload when passing just two blocks.
   */
  def apply(reaction: CyReaction, b1: Block, b2: Block, map_elts: Tuple2[_,_]*): ReactionTable2 =
    apply(reaction, List(b1,b2), map_elts:_*)
}

/**
 * A container that stores the blocks which produce `node` as a sequence.
 */
class ProducerGroup(val node: CyNode, val blocks: Seq[Block]) extends Iterable[Block] {
  def length: Int = {
    blocks.length
  }

  def iterator = blocks.iterator

  /**
   * Returns the arbitrarily-designated main block (used in fan-in configurations).
   */
  def main_block: Block = blocks(0)

  /**
   * Allows iterating over non-main blocks.
   */
  def non_main_blocks = blocks.drop(1)
}

/**
* A container that stores the blocks which consume `node` as a sequence.
*/
class ConsumerGroup(val node: CyNode, val blocks: Seq[Block]) extends Iterable[Block] {
  def length: Int = {
    blocks.length
  }

  def iterator = blocks.iterator

  /**
  * Returns the arbitrarily-designated main block (used in fan-in configurations).
  */
  def main_block: Block = blocks(0)

  /**
  * Allows iterating over non-main blocks.
  */
  def non_main_blocks = blocks.drop(1)
}

/**
 * Represents a connection between terminals `u` and `v`.
 * The wiring configuration will store a sequence of these connections to specify the
 * routing information for SRAM.
 * @constructor Parameterized constructor.
 * @param u The source terminal.
 * @param v The destination terminal.
 * @param invert If true, flip the sign of the current from `u` to `v` (only possible for terminals with negative mirrors like Ctot).
 */
class WiringConnection(val u: OutputTerminal, val v: InputTerminal, val invert: Boolean = true) {
  /**
   * Apply permanently a la the first wiring implementation.
   */
  def applyToTerminals(): Unit = {
    Terminal.makeConnection(u,v,!invert)
  }

  def inputBlockMatches (block: Block): Boolean = u.block eq block
  def outputBlockMatches(block: Block): Boolean = v.block eq block

  def toStringWrtInputBlock(block: Block): String =
    s"  ${u.getName} -> ${if (invert) "-" else ""}${if (u.block eq v.block) v.getName else v.getFullName}"
}

/**
 * Wiring table holding intermediates for construction of a wiring configuration.
 */
class WiringTable2(n: CyNet, input_val: Double = 1d, current_assignment: String = "universal", disable_block_elision: Boolean = false) {
  // create producer map for all reactions
  /**
   * Maps nodes to sets of blocks which produce them.
   */
  val producer_map = new scala.collection.mutable.LinkedHashMap[CyNode,Set[Block]] with MultiMap[CyNode,Block]

  /**
   * Mutable seq for adding connections.
   */
  val connections = ArrayBuffer.empty[WiringConnection]

  /**
   * Store nodes that need to be doubled.
   */
  val double_value = ArrayBuffer.empty[CyNode]

  /**
   * Add a new connection to the mutable seq of connections.
   */
  def addConnection(u: OutputTerminal, v: InputTerminal, invert: Boolean = false): Unit = {
    connections += new WiringConnection(u,v,invert)
  }

  /**
   * Checks if the connection exists
   */
  def connectionExists(u: OutputTerminal, v: InputTerminal, invert: Boolean = false): Boolean = {
    connections.exists(connection => {
      (connection.u eq u) && (connection.v eq v) && (connection.invert == invert)
    })
  }

  /**
   * Returns true if the seqs of reactions match exactly
   */
  def reactionsMatch(rxns1: Seq[CyReaction], rxns2: Seq[CyReaction]): Boolean = {
    rxns1.forall(r => rxns2.contains(r))
  }

  /**
   * Convert a [[CyReaction]] to a reaction table, which contains producer / consumer blocks for the reaction.
   */
  def convertReaction(reaction: CyReaction): ReactionTable2 = {
    reaction match {
      case x @ CyRxUniUni(r,p,_) => {
        val b = new Block(reaction.toString)
        producer_map.addBinding(p, b)

        b.Anode = r
        b.Cnode = p

        // disable feedback for B since it's a uni reaction
        b.A_FB_EN_sw = reaction.A_depletion
        b.B_FB_EN_sw = false
        b.n = reaction.n

        b.reversible = reaction.reversible

        if (x.basal > 0d)
          // b.Atot.setValue(x.basal)
          b.Atot.setInput(new Input("Basal", b.Atot, x.basal))

        ReactionTable2(reaction, b, r -> b, r -> b.Atot, b -> p)
      }
      case CyRxBiUni(r1,r2,p,_) => {
        val b = new Block(reaction.toString)
        producer_map.addBinding(p, b)

        b.Anode = r1
        b.Bnode = r2
        b.Cnode = p

        b.A_FB_EN_sw = reaction.A_depletion
        b.B_FB_EN_sw = reaction.B_depletion
        b.n = reaction.n

        b.reversible = reaction.reversible

        ReactionTable2(reaction, b, r1 -> b, r2 -> b, r1 -> b.Atot, r2 -> b.Btot, b -> p)
      }
      case CyRxDimerization(r,p,_) => {
        val b = new Block(reaction.toString)
        producer_map.addBinding(p, b)

        // add connections required for a dimerization block
        addConnection(b.Afree, b.Btot)
        addConnection(b.Ctot, b.Atot, true)

        b.B_FB_EN_sw = false

        b.reversible = reaction.reversible

        ReactionTable2(reaction, b, r -> b, r -> b.Atot, b -> p)
      }
      case CyRxUniBi(r,p1,p2,_) => {
        // dissocation (3.5c)

        // in the special case where both products have the same
        // degradation gains and neither is consumed by another reaction,
        // (or they are both consumed by the same reaction), these two
        // blocks can be collapsed into one
        val can_collapse = (p1.degradationGain == p2.degradationGain) && reactionsMatch(n.consumers(p1), n.consumers(p2))

        if (!can_collapse || disable_block_elision) {
          val b1 = new Block(reaction.toString + s" (for ${p1})")
          val b2 = new Block(reaction.toString + s" (for ${p2})")
          producer_map.addBinding(p1, b1)
          producer_map.addBinding(p2, b2)

          b1.reversible = reaction.reversible
          b2.reversible = reaction.reversible
          b2.driver = b1

          // add connections required for a uni-bi reaction
          addConnection(b1.rate_fw, b2.Cprod)
          addConnection(b1.rate_rv, b2.Cdeg)
          if (b1.reversible)
            addConnection(b2.Ctot, b1.Dfree)

          // disable feedback for B
          b1.B_FB_EN_sw = false
          b2.B_FB_EN_sw = false
          // and also A for non-main block
          b2.A_FB_EN_sw = false

          b1.Anode = r
          b2.Anode = r
          b1.Cnode = p1
          b2.Cnode = p2

          // ReactionTable2(reaction, b1, b2, r -> b1, r -> b2, r -> b1.Atot, r -> b2.Atot, b1 -> p1, b2 -> p2)
          ReactionTable2(reaction, b1, b2,
            r -> b1,
            b1 -> p1, b2 -> p2,
            r -> b1.Atot, r -> b2.Atot)
        } else {
          val b = new Block(reaction.toString)
          producer_map.addBinding(p1, b)
          producer_map.addBinding(p2, b)

          // disable feedback for B
          b.B_FB_EN_sw = false

          b.Anode = r

          b.reversible = reaction.reversible

          ReactionTable2(reaction, b, r -> b, r -> b.Atot, b -> p1, b -> p2)
        }
      }
      case CyRxMonomerization(r,p,_) => {
        val b = new Block(reaction.toString)
        producer_map.addBinding(p, b)

        // add connections required for monomerization
        addConnection(b.Cfree_out, b.Dfree)
        // make an extra connection to Cfree
        addConnection(b.Ctot, b.Cfree)

        // disable feedback for B
        b.B_FB_EN_sw = false

        b.reversible = reaction.reversible

        double_value += p

        ReactionTable2(reaction, b, r -> b, r -> b.Atot, b -> p)
      }
      case CyRxConstProduction(p) => {
        val b = new Block(reaction.toString)
        producer_map.addBinding(p, b)

        // disable feedback for B since it's const production
        b.B_FB_EN_sw = false

        b.reversible = reaction.reversible

        ReactionTable2(reaction, b, b -> p)
      }
      case _ => throw new RuntimeException("Unknown reaction type")
    }
  }

  // create reaction tables (which store consumer and producer blocks)
  val reaction_tables: Seq[ReactionTable2] = n.r.map(r => convertReaction(r))
  // map reactions to blocks
  val reaction_map = new LinkedHashMap[CyReaction,Set[Block]] with MultiMap[CyReaction,Block]
  reaction_tables.map( t => t.blocks.map( block => reaction_map.addBinding(t.reaction, block) ) )
  // create blocks
  val blocks: Seq[Block] = reaction_tables.flatMap(_.blocks)

  /**
   * Iterates over blocks for a given reaction in order.
   */
  def blocks_for(reaction: CyReaction) = {
    blocks.filter(reaction_map(reaction) contains _)
  }

  // create producer groups
  val producer_groups: Map[CyNode,ProducerGroup] = producer_map.map( {
    case (n, producer_blocks) => n -> new ProducerGroup(n,blocks.filter(block => producer_blocks.contains(block)))
  } )

  def printProducerGroups(): Unit = {
    println("Producer groups:")
    for (node <- n.n) {
      if (producer_groups.contains(node)) {
        val producer_group = producer_groups(node)
        if (producer_group.length == 0)
          println(s"  $node: None")
        else {
          println(s"  $node:")
          for (b: Block <- producer_group) {
            println(s"    $b")
          }
        }
      }
    }
  }
  // printProducerGroups

  // create consumer groups
  def isConsumer(r: CyReaction, u: CyNode, block: Block): Boolean = {
    val table = reaction_tables.find(_.reaction eq r)
    if (table.isEmpty)
      throw new RuntimeException(s"Could not find a reaction table for $r")
    table.get.consumer_map.contains(u) && table.get.consumer_map(u).contains(block)
  }
  val consumer_groups: immutable.Map[CyNode,ConsumerGroup] = reaction_map.toSeq.flatMap( {
    case (r: CyReaction, blocks: Set[Block]) => r.reactants.map(
      u => (u -> blocks.filter(block => isConsumer(r,u,block))) // assumes a reactant does not appear twice
    )
  } ).groupBy(_._1).mapValues(_.map(_._2).reduce(_ ++ _).toSeq).map( {
    case (n: CyNode, consumer_blocks: Seq[Block]) => (n -> new ConsumerGroup(n, blocks.filter(block => consumer_blocks.contains(block))))
  } ).filter({case (n: CyNode, c: ConsumerGroup) => c.blocks.length > 0})

  val node_to_input_terminal_map: immutable.Map[CyNode,Seq[InputTerminal]] = reaction_tables.flatMap( table =>
    table.input_terminal_map.map({
      case (n: CyNode, terminals: Set[InputTerminal]) => (n -> terminals)
    })
  ).groupBy(_._1).mapValues(_.map(_._2).reduce(_ ++ _).toSeq)

  def printConsumerGroups(): Unit = {
    println("Consumer groups:")
    for (node <- n.n) {
      if (consumer_groups.contains(node)) {
        val consumer_group = consumer_groups(node)
        if (consumer_group.length == 0)
          println(s"  $node: None")
        else {
          println(s"  $node:")
          for (b: Block <- consumer_group) {
            println(s"    $b")
          }
        }
      }
    }
  }
  // printConsumerGroups


  // a node has external degradation, which must be propagated via rv_up
  val external_deg_nodes = new LinkedHashSet[CyNode]
  def hasExternalDeg(node: CyNode) = external_deg_nodes.contains(node)
  // a node has external production, which must be propagated via fw_up
  val external_prod_nodes = new LinkedHashSet[CyNode]
  def hasExternalProd(node: CyNode) = external_prod_nodes.contains(node)
  def hasExternalProdDeg(node: CyNode) = hasExternalDeg(node) || hasExternalProd(node)

  def connectFanInBlockToMainBlock(block: Block, main_block: Block): Unit = {
    addConnection(block.driver.rate_fw, main_block.Cprod)
    addConnection(block.driver.rate_rv, main_block.Cdeg)
    if (block.reversible){
      addConnection(main_block.Ctot, block.Ctot_in)
    }
  }

  def connectSequentialFanInBlocks(prev_block: Block, next_block: Block): Unit = {
    if (next_block.reversible){
      next_block.FF_EN_sw1 = true
      next_block.FF_EN_sw2 = false
      next_block.FF_EN_sw3 = false
      next_block.FF_EN_sw4 = true
      addConnection(prev_block.Cfree_out, next_block.Cfree)
      next_block.Ctot_sw   = true
      addConnection(prev_block.fw_tot, next_block.Cprod)
      addConnection(prev_block.rv_tot, next_block.Cdeg)
    } else {
      addConnection(next_block.Ctot, next_block.Cfree)
    }
  }

  def processFanInBlocks(main_block: Block, blocks: Seq[Block], prev_block: Block): Unit = {
    blocks match {
      case Seq() => ; // not fan-in, do nothing
      case block +: rest => {
        connectFanInBlockToMainBlock(block, main_block)
        connectSequentialFanInBlocks(prev_block, block)
        processFanInBlocks(main_block, rest, block)
      }
      case block +: Seq() => {
        connectFanInBlockToMainBlock(block, main_block)
        connectSequentialFanInBlocks(prev_block, block)
      }
    }
  }

  def connectFanOutBlocksIfNotDissoc(node: CyNode, block: Block, main_block: Block): Unit = {
    // dissociation reactions have rate_fw and rate_rv connected to Cprod and Cdeg resp.
    // and so don't need these connections
    if (connectionExists(block.rate_fw,main_block.Cprod) && connectionExists(block.rate_rv,main_block.Cdeg))
      return
    if (connectionExists(main_block.rate_fw,block.Cprod) && connectionExists(main_block.rate_rv,block.Cdeg))
      return
    val port = if (block.Anode_is(node)) block.Atot else block.Btot
    if (main_block.Anode_is(node)) {
      addConnection(main_block.Afree, port)
      if (block.A_FB_EN_sw)
        addConnection(block.Ctot, main_block.Atot, true)
    } else {
      addConnection(main_block.Bfree, port)
      if (block.B_FB_EN_sw)
        addConnection(block.Ctot, main_block.Btot, true)
    }
    if (block.Anode_is(node)) {
      block.A_FB_EN_sw = false
    } else {
      block.B_FB_EN_sw = false
    }
  }

  def processFanOutBlocks(node: CyNode, main_block: Block, blocks: Seq[Block]): Unit = {
    blocks match {
      case Seq() => ; // not fan-in, do nothing
      case block +: rest => {
        connectFanOutBlocksIfNotDissoc(node, block, main_block)
        processFanOutBlocks(node, main_block, rest)
      }
      case block +: Seq() => {
        connectFanOutBlocksIfNotDissoc(node, block, main_block)
      }
    }
  }

  // def getInputTerminalFor(node: CyNode, block: Block): InputTerminal = {
  //   if (node_to_input_terminal_map.contains(node)) {
  //     val terminals = node_to_input_terminal_map(node)
  //     for (t <- terminals) {
  //       if (t.block eq block)
  //         return t
  //     }
  //     throw new RuntimeException(s"Node was expected to map to an input terminal in block $block but did not")
  //   }
  //   throw new RuntimeException(s"Could not find input terminal for $node")
  // }

  // def processBlockOutput(node: CyNode, upstream_block: Block, downstream_blocks: Seq[Block]): Unit = {
  //   downstream_blocks match {
  //     case Seq() => {
  //       // connect the block to its own Cfree unless it represents an irreversible reaction
  //       if (n.producers(node).exists(p => p.reversible)) {
  //         addConnection(upstream_block.Ctot, upstream_block.Cfree)
  //       }
  //     }
  //     case block +: Seq() => {
  //       addConnection(upstream_block.Ctot, getInputTerminalFor(node,block))
  //     }
  //     case block +: rest => {
  //       addConnection(upstream_block.Ctot, getInputTerminalFor(node,block))
  //       processBlockOutput(node, upstream_block, rest)
  //     }
  //   }
  // }

  // def neverDegraded(node: CyNode): Boolean = {
  //   !external_deg_nodes.contains(node)
  // }

  // a fan-in configuration can be optimized by adding the Ctot of each producer block
  // (instead of using fw_tot and rv_tot as a master signal)
  // this only works if each fan-in block is irreversible and only one consumer exists
  // scrapped idea - instead can check if all degradation rates are the same and optimize
//   def shouldOptimizeFanIn(node: CyNode, producer_group: ProducerGroup): Boolean =
//     n.consumers(node) == 1 && n.producers(node).forall(p => p.reversible == false)

  // connect fan-in configurations
  for (node <- n.n) {
    if (producer_groups.contains(node)) {
      val producer_group = producer_groups(node)
      val main_block = producer_group.main_block
      if (producer_group.length > 1) {
        processFanInBlocks(main_block, producer_group.non_main_blocks, main_block)
        if (main_block.reversible) {
          external_prod_nodes += node
          external_deg_nodes += node
        }
      }
    }
  }

  // set degradation for blocks that have it
  for (node <- n.n) {
    if (node.degradationGain != 0) {
      producer_groups(node).main_block.ratC = node.degradationGain
      external_deg_nodes += node
    }
  }

  /**
   * Propagate degradation signals (and reverse production signals for reversible reactions).
   */
  def propagateDegProdSignals(): Unit = {
    var fixed=false
    while (!fixed) {
      fixed = true
      for (node <- n.n) {
        val propagate_prod = hasExternalProd(node)
        val propagate_deg  = hasExternalDeg (node)
        if (propagate_prod || propagate_deg) {
          for (reaction <- n.producers(node)) {
            for (block <- blocks_for(reaction).filter(block => producer_map.entryExists(node, _ == block))) {
              for (reactant <- reaction.reactants) {
                val has_upstream_prod = hasExternalProd(reactant)
                val has_upstream_deg  = hasExternalDeg (reactant)
                if (propagate_deg && !has_upstream_deg && producer_groups.contains(reactant)) {
                  val upstream_main_block = producer_groups(reactant).main_block
                  addConnection(block.rv_up, upstream_main_block.Cdeg)
                  external_deg_nodes += reactant
                  fixed = false
                }
                if (propagate_prod && !has_upstream_prod && producer_groups.contains(reactant)) {
                  val upstream_main_block = producer_groups(reactant).main_block
                  addConnection(block.fw_up, upstream_main_block.Cprod)
                  external_prod_nodes += reactant
                  fixed = false
                }
              }
            }
          }
        }
      }
    }
  }

  propagateDegProdSignals()

  // propagate main output (Ctot) to downstream consumers
  // for (node <- n.n) {
  //   if (producer_groups.contains(node)) {
  //     val producer_group = producer_groups(node)
  //     val main_block = producer_group.main_block
  //     val downstream_blocks = if (consumer_groups.contains(node)) consumer_groups(node).blocks else Seq.empty[Block]
  //     processBlockOutput(node, producer_group.main_block, downstream_blocks)
  //   }
  // }

  def hasDepletion(node: CyNode): Boolean = {
    if (consumer_groups.contains(node)) {
      val consumer_group = consumer_groups(node)
      val consumer_main_block = consumer_group.main_block
      if (consumer_main_block.Anode_is(node))
        consumer_main_block.A_FB_EN_sw
      else
        consumer_main_block.B_FB_EN_sw
    } else
      true
  }

  // connect fan-out configurations
  for (node <- n.n) {
    if (hasDepletion(node)) {
      if (consumer_groups.contains(node)) {
        val consumer_group = consumer_groups(node)
        val consumer_main_block = consumer_group.main_block
        if (consumer_group.length > 1) {
          // println(s"processFanOutBlocks for node ${node}: ${consumer_group.blocks}")
          processFanOutBlocks(node, consumer_main_block, consumer_group.non_main_blocks)
        }
        if (producer_groups.contains(node)) {
          val producer_group = producer_groups(node)
          val producer_main_block = producer_group.main_block
          if (consumer_main_block.Anode_is(node)) {
            addConnection(producer_main_block.Ctot, consumer_main_block.Atot)
          } else {
            addConnection(producer_main_block.Ctot, consumer_main_block.Btot)
          }
        }
      } else {
        if (producer_groups.contains(node)) {
          val producer_group = producer_groups(node)
          val producer_main_block = producer_group.main_block
          addConnection(producer_main_block.Ctot, producer_main_block.Cfree)
        }
      }
    } else {
      // transcription / translation, no depletion
      if (consumer_groups.contains(node)) {
        val consumer_group = consumer_groups(node)
        val consumer_main_block = consumer_group.main_block
        if (consumer_group.length > 1) {
          // println(s"processFanOutBlocks for node ${node}: ${consumer_group.blocks}")
          processFanOutBlocks(node, consumer_main_block, consumer_group.non_main_blocks)
        }
        if (producer_groups.contains(node)) {
          val producer_group = producer_groups(node)
          val producer_main_block = producer_group.main_block
          if (consumer_main_block.Anode_is(node)) {
            addConnection(producer_main_block.Ctot, consumer_main_block.Atot)
          } else {
            addConnection(producer_main_block.Ctot, consumer_main_block.Btot)
          }
        }
      }
      if (producer_groups.contains(node)) {
        val producer_group = producer_groups(node)
        val producer_main_block = producer_group.main_block
        if (producer_main_block.reversible)
          addConnection(producer_main_block.Ctot, producer_main_block.Cfree)
      }
    }
  }

  // connect Afree to upstream blocks for reversible reactions
  for (reaction <- n.r) {
    for (node <- reaction.products) {
      // if all the upstream reactions are reversible, this is unnecessary
      if (n.producers(node).exists(p => p.reversible)) {
        if (consumer_groups.contains(node)) {
          val consumer_group = consumer_groups(node)
          val consumer_block = consumer_group.main_block
          if (producer_groups.contains(node)) {
            val producer_group = producer_groups(node)
            val producer_block = producer_group.main_block
            // if the block is producing two identical products (e.g. E & P),
            // don't wire twice
            if (!connectionExists(consumer_block.Afree, producer_block.Cfree) && hasDepletion(node)) {
              addConnection(consumer_block.Afree, producer_block.Cfree)
            }
          }
        }
      }
    }
  }
  // for (node <- n.n) {
  //   // if all the upstream reactions are reversible, this is unnecessary
  //   if (n.producers(node).exists(p => p.reversible)) {
  //     if (consumer_groups.contains(node)) {
  //       val consumer_group = consumer_groups(node)
  //       val consumer_block = consumer_group.main_block
  //       if (producer_groups.contains(node)) {
  //         val producer_group = producer_groups(node)
  //         val producer_block = producer_group.main_block
  //         addConnection(consumer_block.Afree, producer_block.Cfree)
  //       }
  //     }
  //   }
  // }

  // connect observables
  for (node <- n.n) {
    if (consumer_groups.contains(node)) {
      for (reaction_table <- reaction_tables) {
        for (block <- reaction_table.blocks) {
          if (block eq consumer_groups(node).main_block) {
            val input_ports = reaction_table.input_terminal_map(node)
            // TODO: check if Afree copies exhausted ${double_value.contains(node)}")
            if (input_ports.contains(block.Atot)) block.Afree.setObservable(node.name, double_value.contains(node))
            else if (input_ports.contains(block.Btot)) block.Bfree.setObservable(node.name, double_value.contains(node))
            else throw new RuntimeException(s"Block ${block} was expected to have an input port for ${node} but it did not")
          }
        }
      }
    }
    else if (producer_groups.contains(node))
      // not consumed, so Ctot is C
      producer_groups(node).main_block.Ctot.setObservable(node.name, double_value.contains(node))
  }

  // for species which are depletable sources, set the Atot value
  for (node <- n.n) {
    if (n.consumers(node).length > 0) {
      node.initial_value.map(initial_value => {
        // println(s"node $node has initial value")
        if (consumer_groups.contains(node)) {
          val consumer_group = consumer_groups(node)
          val consumer_block = consumer_group.main_block
          val terminals = node_to_input_terminal_map(node).map( t =>
            if (t.block eq consumer_block)
              t.setInput(new Input(node.toString, t, initial_value))
          )
        } else {
          // only for nodes with no depletion
          for (t <- node_to_input_terminal_map(node)) {
            t.setInput(new Input(node.toString, t, initial_value))
          }
        }
      })
    }
  }

  // val __s7 = n.n.find(_.name == "__s7").get
  // println(s"consumer blocks for __s7: ${consumer_groups(__s7).blocks}")
  // val r2 = n.r(2)
  // val table = reaction_tables.find(_.reaction eq r2).get
  // println(s"table.consumer_map.contains(__s7) = ${table.consumer_map.contains(__s7)}, table.consumer_map(__s7).contains(block(3)) = ${table.consumer_map(__s7).contains(blocks(3))}")
  // println(s"isConsumer(r2,__s7,blocks(3)) = ${isConsumer(r2,__s7,blocks(3))}")
  // println(s"reaction_map(r2) = ${reaction_map(r2)}")
  // val flatmap = reaction_map.toSeq.flatMap( {
  //   case (r: CyReaction, blocks: Set[Block]) => r.reactants.map(
  //     u => (u -> blocks.filter(block => isConsumer(r,u,block))) // assumes a reactant does not appear twice
  //   )
  // } )
  // println(s"$flatmap")

  AssignInputs(reaction_map, input_val)
  if (current_assignment == "universal")
    AssignCurrentsUniversal_kr(reaction_map)
  else if (current_assignment == "maxKD")
    AssignCurrentsMaxKD(reaction_map)

  val reaction_to_block_indices = reaction_tables.map( t =>
    blocks.indexOf(t.blocks(0))
  )
}

class Wiring2(override val blocks: Seq[Block], override val observables: Seq[Observable], val connections: ArrayBuffer[WiringConnection], val reaction_to_block_indices: Seq[Int]) extends Wiring(blocks, observables) {

  override def outgoing(t: OutputTerminal): Iterable[Tuple2[SignalReceiver,Boolean]] = {
    val outgoing_map: immutable.Map[OutputTerminal,Seq[Tuple2[SignalReceiver,Boolean]]] =
      connections.flatMap(connection =>
      connection.u match {
        case t: OutputTerminal => Some(t, connections.filter(_.u eq t).map(other => (other.v, !other.invert)))
        case _ => None
      }).toMap
    outgoing_map.getOrElse(t, Seq.empty[Tuple2[SignalReceiver,Boolean]])
  }

  override def incoming(t: InputTerminal):  Iterable[Tuple2[SignalSource,  Boolean]] = {
    val incoming_map: immutable.Map[InputTerminal,Seq[Tuple2[SignalSource,Boolean]]] =
      connections.flatMap(connection =>
        connection.v match {
          case t: InputTerminal => Some(t, connections.filter(_.v eq t).map(other => (other.u, !other.invert)))
          case _ => None
        }).toMap
    incoming_map.getOrElse(t, Seq.empty[Tuple2[SignalSource,Boolean]])
  }


  override def toString(): String = {
    (for (b <- blocks) yield
        (Seq(b.toString) ++
        connections
          .filter(_.inputBlockMatches(b))
          .map(_.toStringWrtInputBlock(b))).mkString("\n")
    ) mkString("\n")
  }

  override def connect(u: OutputTerminal, v: InputTerminal, invert: Boolean = false): Unit = {
    connections += new WiringConnection(u,v,invert)
  }
}

object Wiring2 {
  def fromNetwork(n: CyNet, input_val: Double = 1d, current_assignment: String = "universal", disable_block_elision: Boolean = false): Wiring2 = {
    val table = new WiringTable2(n, input_val, current_assignment, disable_block_elision)
    val wiring = new Wiring2(table.blocks, Seq(), table.connections, table.reaction_to_block_indices)
    wiring
  }
}
