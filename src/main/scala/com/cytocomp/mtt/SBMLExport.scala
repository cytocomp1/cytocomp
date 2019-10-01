// J Kyle Medley, 2016-2019

package com.cytocomp.mtt

import com.cytocomp.mtt._

import scala.collection.JavaConverters._

import org.sbml.jsbml.{SBMLWriter, SBMLDocument, ASTNode, SBMLError, Reaction=>SBMLReaction, Model=>SBMLModel, Species=>SBMLSpecies, Parameter=>SBMLParameter, Compartment=>SBMLCompartment, InitialAssignment, RateRule, AssignmentRule}

class SBMLSymbolResolver(wiring: Wiring) {
  val block_ids: Map[Block,String] = wiring.blocks.zipWithIndex.map(_ match {case (block,k) => (block -> s"block$k")}).toMap
  def apply(b: Block): String = block_ids(b)

  val output_terminal_ids: Map[OutputTerminal,String] = wiring.blocks.flatMap(block => {
    Seq(block.Afree, block.Bfree, block.Ctot, block.rv_up, block.fw_up,
      block.rate_fw, block.rate_rv, block.Cfree_out, block.Dfree_out, block.fw_tot, block.rv_tot).map(
        t => (t -> (apply(block)+"_"+t.getName))
      )
  }).toMap
  def apply(t: OutputTerminal): String = output_terminal_ids(t)

  val input_terminal_ids: Map[InputTerminal,String] = wiring.blocks.flatMap(block => {
    Seq(block.Atot, block.Btot, block.Cfree, block.Ctot_in, block.Cprod, block.Cdeg, block.Dfree).map(
        t => (t -> (apply(block)+"_"+t.getName))
      )
  }).toMap
  def apply(t: InputTerminal): String = input_terminal_ids(t)
}

class BlockSBMLExporter(block: Block, wiring: Wiring, resolver: SBMLSymbolResolver) {
  def getMathForInputPort(t: InputTerminal): ASTNode = {
    val nodes = Seq(new ASTNode(t.getValue())) ++ wiring.incoming(t).map(_ match {
      case (t:OutputTerminal, true)  => new ASTNode(resolver(t))
      case (t:OutputTerminal, false) => ASTNode.uMinus(new ASTNode(resolver(t)))
      case _ => throw new RuntimeException(s"Could not match $t")
    })
    val ar: Array[ASTNode] = nodes.toArray
    ASTNode.sum(nodes:_*)
  }

  def writeInput(t: InputTerminal, model: SBMLModel): Unit = {
    val name = resolver(t)
    val p = model.createParameter()
    p.setId(name)
    p.setValue(t.getValue())
    p.setConstant(false)

    val rule = model.createAssignmentRule()
    rule.setVariable(name)
    rule.setMath(getMathForInputPort(t))
  }

  def writeSwitch(switch: Boolean, name: String, model: SBMLModel): Unit = {
    val p = model.createParameter()
    p.setId(name)
    p.setValue(if (switch) 1d else 0d)
    p.setConstant(true)
  }

  def writeParam(param: Double, name: String, model: SBMLModel): Unit = {
    val p = model.createParameter()
    p.setId(name)
    p.setValue(param)
    p.setConstant(true)
  }

  def writeOutput(t: OutputTerminal, model: SBMLModel, node: ASTNode): Unit = {
    val name = resolver(t)
    val p = model.createParameter()
    p.setId(name)
    p.setValue(0d)
    p.setConstant(false)

    val rule = model.createAssignmentRule()
    rule.setVariable(name)
    rule.setMath(node)
  }

  def writeIntermediate(symbol: String, model: SBMLModel, node: ASTNode): Unit = {
    val name = resolver(block)+"_"+symbol
    val p = model.createParameter()
    p.setId(name)
    p.setValue(0d)
    p.setConstant(false)

    val rule = model.createAssignmentRule()
    rule.setVariable(name)
    rule.setMath(node)
  }

  def writeCtotRate(model: SBMLModel, node: ASTNode): Unit = {
    val name = resolver(block.Ctot)+"_computed"
    val p = model.createParameter()
    p.setId(name)
    p.setValue(0d)
    p.setConstant(false)

    val rule = model.createRateRule()
    rule.setVariable(name)
    rule.setMath(node)
  }

  def ast(t: InputTerminal): ASTNode = new ASTNode(resolver(t))
  def ast(t: OutputTerminal): ASTNode = new ASTNode(resolver(t))
  def ast(symbol: String): ASTNode = {
    val base = resolver(block)
    new ASTNode(base+"_"+symbol)
  }

  def writeComputations(model: SBMLModel): Unit = {
    val base = resolver(block)
    // write inputs
    writeInput(block.Atot, model)
    writeInput(block.Btot, model)
    writeInput(block.Ctot_in, model)
    writeInput(block.Cprod, model)
    writeInput(block.Cdeg, model)
    writeInput(block.Cfree, model)
    writeInput(block.Dfree, model)

    // write switches & parameters
    writeSwitch(block.A_FB_EN_sw, base+"_A_FB_EN_sw", model)
    writeSwitch(block.FF_EN_sw1, base+"_FF_EN_sw1", model)
    writeSwitch(block.FF_EN_sw2, base+"_FF_EN_sw2", model)
    writeSwitch(block.FF_EN_sw3, base+"_FF_EN_sw3", model)
    writeSwitch(block.FF_EN_sw4, base+"_FF_EN_sw4", model)
    writeSwitch(block.B_FB_EN_sw, base+"_B_FB_EN_sw", model)
    writeSwitch(block.Ctot_sw, base+"_Ctot_sw", model)
    writeSwitch(!block.Ctot_sw, base+"_Ctot_sw_not", model)

    writeParam(block.n, base+"_n", model)
    writeParam(block.KDfw, base+"_KDfw", model)
    writeParam(block.KDrv, base+"_KDrv", model)
    writeParam(block.kr, base+"_kr", model)
    writeParam(block.ratC, base+"_ratC", model)
    writeParam(block.kdeg, base+"_kdeg", model)

    // write values
    // Afree / Bfree
    writeOutput(block.Afree, model, ast(block.Atot).minus(
      ast("A_FB_EN_sw").multiplyWith(ast(block.Ctot))))
    writeOutput(block.Bfree, model, ast(block.Btot).minus(
      ast("B_FB_EN_sw").multiplyWith(ast(block.Ctot))))

    // rate_fw
    writeOutput(block.rate_fw, model, ast(block.Afree).multiplyWith(ASTNode.pow(
      ast(block.Bfree).divideBy(ast("KDfw"))
      , ast("n"))))

    // fw_up / fw_tot
    writeOutput(block.fw_up, model, ast(block.Cprod).minus(
      ast("FF_EN_sw1").multiplyWith(ast(block.rate_fw))
    ))
    writeOutput(block.fw_tot, model, ASTNode.sum(ast(block.Cprod),
      ast("FF_EN_sw2").multiplyWith(ast(block.rate_fw))
    ))

    // Cfree_out / Dfree_out
    writeOutput(block.Cfree_out, model, ast(block.Cfree))
    writeOutput(block.Dfree_out, model, ast(block.Dfree))

    // rate_rv
    writeOutput(block.rate_rv, model, ast(block.Cfree).multiplyWith(
      ast(block.Dfree).divideBy(ast("KDrv"))
    ))

    // Add6
    writeIntermediate("Add6", model, ASTNode.sum(ast(block.Cdeg),
      ast(block.Cfree).multiplyWith(ast("ratC"))
    ))

    // rv_tot
    writeOutput(block.rv_tot, model, ASTNode.sum(
      ast("FF_EN_sw3").multiplyWith(ast(block.rate_rv)),
      ast("Add6")
    ))

    // rv_up
    writeOutput(block.rv_up, model, ast("Add6").minus(
      ast("FF_EN_sw4").multiplyWith(ast(block.rate_rv))
    ))

    // Add3
    writeIntermediate("Add3", model, ast(block.fw_tot).minus(
      ast("FF_EN_sw3").multiplyWith(ast(block.rate_rv))
    ))

    // Add10
    writeIntermediate("Add10", model, ast("Add3").multiplyWith(ast("kr")).minus(
      ast("Add6").multiplyWith(ast("kdeg"))
    ))

    // Ctot_computed
    writeCtotRate(model, ast("Ctot_sw_not").multiplyWith(ast("Add10")))

    // Ctot
    writeOutput(block.Ctot, model, ASTNode.sum(
      ast("Ctot_sw").multiplyWith(ast("Ctot_in")),
      ast("Ctot_computed")
    ))
  }

  def writeObservables(model: SBMLModel): Unit = {
    for (observable <- Seq(block.Afree.observable, block.Bfree.observable, block.Ctot.observable)) {
      observable.map( observable => {
        val name = observable.toString
        val s = model.createSpecies()
        s.setHasOnlySubstanceUnits(false)
        s.setBoundaryCondition(false)
        s.setConstant(false)
        s.setCompartment("comp1")
        s.setId(name)

        val rule = model.createAssignmentRule()
        rule.setVariable(name)
        rule.setMath(ast(observable.terminal))
      })
    }
  }

  def write(model: SBMLModel): Unit = {
    writeComputations(model)
    writeObservables(model)
  }
}

class SBMLExporter(wiring: Wiring) {
  val resolver = new SBMLSymbolResolver(wiring)

  def writeToString(): String = {
    val document = new SBMLDocument(3,1)
    val model = document.createModel()

    val comp = model.createCompartment()
    comp.setSize(1d)
    comp.setConstant(true)
    comp.setId("comp1")

    for (block <- wiring.blocks) {
      val block_exporter = new BlockSBMLExporter(block, wiring, resolver)
      block_exporter.write(model)
    }
    val writer = new SBMLWriter()
    writer.writeSBMLToString(document)
  }
}
