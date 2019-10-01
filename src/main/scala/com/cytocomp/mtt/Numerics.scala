// J Kyle Medley, 2016
// Converts a network to a wiring configuration

package com.cytocomp.mtt

import scala.collection.JavaConverters._
// import scala.runtime.RichDouble

import com.cytocomp.mtt._
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator

class CircleODE extends FirstOrderDifferentialEquations {
  var c = Array[Double]()
  var omega: Double = 0

  def init(in_c: Array[Double], in_omega: Double): Unit = {
      c = in_c
      omega = in_omega
  }

  def getDimension(): Int = 2

  def computeDerivatives(t: Double, y: Array[Double], yDot: Array[Double]): Unit = {
      yDot(0) = omega * (c(1) - y(1))
      yDot(1) = omega * (y(0) - c(0))
  }
}

object circle_solver {
  def solve(): Unit = {
    val ode = new CircleODE()
    ode.init(Array[Double](1.0, 1.0), 0.1);
    val integrator = new ClassicalRungeKuttaIntegrator(0.1)
    var y = Array[Double](0.0, 1.0)
    val y0 = y.clone()
    integrator.integrate(ode, 0.0, y0, 1.0, y)
  }
}

class BlockInput {
  var _Atot: Option[Double] = None
  def Atot = {
    if (_Atot.isEmpty)
      0.0
    else
      _Atot.get
  }
  def Atot_=(u: Double) = { _Atot = Some(u) }

  var _Btot: Option[Double] = None
  def Btot = {
    if (_Btot.isEmpty)
      0.0
    else
      _Btot.get
  }
  def Btot_=(u: Double) = { _Btot = Some(u) }

  var _Cprod: Option[Double] = None
  def Cprod = {
    if (_Cprod.isEmpty)
      0.0
    else
      _Cprod.get
  }
  def Cprod_=(u: Double) = { _Cprod = Some(u) }

  var _Cdeg: Option[Double] = None
  def Cdeg = {
    if (_Cdeg.isEmpty)
      0.0
    else
      _Cdeg.get
  }
  def Cdeg_=(u: Double) = { _Cdeg = Some(u) }

  var _Ctot_in: Option[Double] = None
  def Ctot_in = {
    if (_Ctot_in.isEmpty)
      0.0
    else
      _Ctot_in.get
  }
  def Ctot_in_=(u: Double) = { _Ctot_in = Some(u) }

  var _Cfree: Option[Double] = None
  def Cfree = {
    if (_Cfree.isEmpty)
      0.0
    else
      _Cfree.get
  }
  def Cfree_=(u: Double) = { _Cfree = Some(u) }

  var _Dfree: Option[Double] = None
  def Dfree = {
    if (_Dfree.isEmpty)
      0.0
    else
      _Dfree.get
  }
  def Dfree_=(u: Double) = { _Dfree = Some(u) }

  def setValueFor(terminal: InputTerminal, v: Double): Unit = {
    terminal match {
      case x: Atot  => {Atot  = v}
      case x: Btot  => {Btot  = v}
      case x: Cfree => {Cfree = v}
      case x: Cprod => {Cprod = v}
      case x: Cdeg  => {Cdeg  = v}
      case x: Dfree => {Dfree = v}
      case x: Ctot_in => {Ctot_in = v}
      case _ => throw new RuntimeException(s"No matching input found for $terminal")
    }
  }

  def getValueFor(terminal: InputTerminal): Double = {
    terminal match {
      case x: Atot  => _Atot.getOrElse(0d)
      case x: Btot  => _Btot.getOrElse(0d)
      case x: Cfree => _Cfree.getOrElse(0d)
      case x: Cprod => _Cprod.getOrElse(0d)
      case x: Cdeg  => _Cdeg.getOrElse(0d)
      case x: Dfree => _Dfree.getOrElse(0d)
      case x: Ctot_in => _Ctot_in.getOrElse(0d)
      case _ => throw new RuntimeException(s"No matching input found for $terminal")
    }
  }

  def accumulateValueFor(terminal: InputTerminal, v: Double): Unit = {
    val value: Double = getValueFor(terminal)
    setValueFor(terminal, value + v)
  }

  def inputToNumeric(input: Input): Double = {
    input.value
  }

  def initializeValueFor(terminal: InputTerminal, wiring: Wiring): Unit = {
    // clear inputs with connections
    if (wiring.incoming(terminal).size > 0)
      setValueFor(terminal,0d)
    // if there is an external numeric input, set the value now
    terminal.input.map(inputToNumeric).map(x => setValueFor(terminal,x))
  }

  def getListOfInputTerminals(block: Block): Seq[InputTerminal] = {
    List(
      block.Atot,
      block.Btot,
      block.Cfree,
      block.Ctot_in,
      block.Cprod,
      block.Cdeg,
      block.Dfree
    )
  }

  def initializeInputs(block: Block, wiring: Wiring): Unit = {
    for (terminal <- getListOfInputTerminals(block)) {
      initializeValueFor(terminal, wiring)
    }
  }

  def update(evaluator: BlockEvaluator, ode: WiringODE): Unit = {
    initializeInputs(evaluator.block, ode.wiring)
    for (u <- getListOfInputTerminals(evaluator.block)) {
      for (v <- ode.wiring.incoming(u)) {
        v match {
          case (t:OutputTerminal, true)  => accumulateValueFor(u,  ode.getValue(t))
          case (t:OutputTerminal, false) => accumulateValueFor(u, -ode.getValue(t))
          case _ => None
        }
      }
    }
  }
}

class BlockValues (block: Block, input: BlockInput, previous: Option[BlockValues]) {
  // input / feedback
  val Atot = input.Atot
  val Btot = input.Btot
  val Ctot_prev = previous.map(_.Ctot).getOrElse(0d)
  val Ctot_in = input.Ctot_in
  val Cprod  = input.Cprod
  val Cdeg   = input.Cdeg
  val Cfree  = input.Cfree
  val Dfree  = input.Dfree
  val KDfw   = block.KDfw
  val KDrv   = block.KDrv
  val ratC   = block.ratC
  val kr     = block.kr
  val kdeg   = block.kdeg

  // switches
  val A_FB_EN_sw = block.A_FB_EN_sw // true  = same state as Fig 3-3
  val FF_EN_sw1  = block.FF_EN_sw1  // false = same state as Fig 3-3
  val FF_EN_sw2  = block.FF_EN_sw2  // true  = same state as Fig 3-3
  val FF_EN_sw3  = block.FF_EN_sw3  // true  = same state as Fig 3-3
  val FF_EN_sw4  = block.FF_EN_sw4  // false = same state as Fig 3-3
  val B_FB_EN_sw = block.B_FB_EN_sw // true  = same state as Fig 3-3
  val Ctot_sw    = block.Ctot_sw    // false = same state as Fig 3-3

  implicit def bool2double(b:Boolean) = if (b) 1d else 0d

  // compute forward rate

  val Afree = Atot-A_FB_EN_sw*Ctot_prev
  // Add1 = Afree
  val Bfree = Btot-B_FB_EN_sw*Ctot_prev
  // Add2 = Bfree

  val n = block.n
  val Add2_term = scala.math.pow(Bfree/KDfw,n)
  val rate_fw = Afree * Add2_term
  // Product1 = rate_fw

  val fw_up  = Cprod-FF_EN_sw1*rate_fw
  val fw_tot = Cprod+FF_EN_sw2*rate_fw
  // Add11 = fw_tot

  // compute reverse rate

  val Cfree_out = Cfree
  val Dfree_out = Dfree

  val rate_rv = Cfree*Dfree/KDrv

  val Add6 = Cdeg+Cfree*ratC
  // Add5 = rv_tot
  val rv_tot = FF_EN_sw3*rate_rv+Add6
  // Add9 = rv_up
  val rv_up = Add6-FF_EN_sw4*rate_rv

  // compute output

  val Add3  = fw_tot-FF_EN_sw3*rate_rv
  val Add10 = Add3*kr-Add6*kdeg

  var Ctot: Double = Ctot_sw*Ctot_in + (!Ctot_sw)*Ctot_prev

  def getSummary(): String = {
    s"Block ${block.getName}\n" +
    s"  Atot $Atot\n" +
    s"  Afree $Afree\n" +
    s"  Btot $Btot\n" +
    s"  Bfree $Bfree\n" +
    s"  Ctot_prev $Ctot_prev\n" +
    s"  Ctot_in $Ctot_in\n" +
    s"  ratC $ratC\n" +
    s"  Cprod $Cprod\n" +
    s"  Cdeg $Cdeg\n" +
    s"  rate_fw $rate_fw\n" +
    s"  rate_rv $rate_rv\n" +
    s"  fw_up $fw_up\n" +
    s"  rv_up $rv_up\n" +
    s"  fw_tot $fw_tot\n" +
    s"  rv_tot $rv_tot\n" +
    s"  KDfw $KDfw\n" +
    s"  Add3 $Add3\n" +
    s"  Add6 $Add6\n" +
    s"  Add10 $Add10\n" +
//     s"  Ctot_sw $Ctot_sw\n" +
//     s"  t1 ${Ctot_sw*Ctot_in}\n" +
//     s"  t2 ${(!Ctot_sw)*Ctot_prev}\n" +
//     s"  t3 ${Ctot_sw*Ctot_in + (!Ctot_sw)*Ctot_prev}\n" +
    s"  Ctot $Ctot"
  }
}

class BlockEvaluator (val block: Block, initial_input: BlockInput) {
  // stateful
  var input = initial_input
  var values = new BlockValues(block, input, None)

  def evaluate(): Unit = {
    values = new BlockValues(block, input, Some(values))
  }

  def getValue(t: Terminal): Double = {
    t match {
      case x: Afree => values.Afree
      case x: Bfree => values.Bfree
      case x: Ctot  => values.Ctot
      case x: rv_up => values.rv_up
      case x: rate_fw => values.rate_fw
      case x: rate_rv => values.rate_rv
      case x: Cfree_out => values.Cfree_out
      case x: fw_up => values.fw_up
      case x: fw_tot => values.fw_tot
      case x: rv_tot => values.rv_tot
      case _ => throw new RuntimeException(s"Could not get value for terminal ${t.getFullName}")
    }
  }
}

class BlockOutput(evaluator: BlockEvaluator, terminal: OutputTerminal) {
  def getFactor(): Double = if (terminal.observable.get.double_val) 2d else 1d

  def getValue(): Double = {
    terminal match {
      case t: Afree    => getFactor()*evaluator.values.Afree
      case t: Bfree    => getFactor()*evaluator.values.Bfree
      case t: Ctot     => getFactor()*evaluator.values.Ctot
      case t: Cfree_out=> getFactor()*evaluator.values.Cfree_out
//       case t: rate_fw => evaluator.values.rate_fw
      case _ => throw new RuntimeException(s"Unknown terminal $terminal for block output")
    }
  }

  override def toString(): String = {
    s"$terminal -> ${terminal.observable.get} (${getValue()})"
  }

  def getObservableName(): String = {
    terminal.observable.get.toString
  }
}

class WiringODE (val wiring: Wiring) extends FirstOrderDifferentialEquations {
  def makeInitialInput(block: Block): BlockInput = {
    var input = new BlockInput()
    input.initializeInputs(block, wiring)
    // TODO: input._Ctot_in
    input
  }

  // create evaluators and seed with initial conditions
  val evaluator_map: Map[Block, BlockEvaluator] = wiring.blocks.map(block => block -> new BlockEvaluator(block, makeInitialInput(block))).toMap
  val evaluators: Seq[BlockEvaluator] = wiring.blocks.map(block => evaluator_map(block))

  def getValue(t: Terminal): Double = {
    evaluator_map(t.block).getValue(t)
  }

  // finds all outputs for a given block
  def findConnectedObservables(block: Block): Seq[BlockOutput] = {
    Seq(block.Afree.observable, block.Bfree.observable, block.Ctot.observable).map(_.map(observable =>
      new BlockOutput(evaluator_map(block), observable.terminal)
    )).flatten
  }

  val outputs: Seq[BlockOutput] = wiring.blocks.flatMap(block => findConnectedObservables(block))

  def getOutputValues(): Seq[Double] = {
    outputs.map(_.getValue)
  }

  def getDimension(): Int = {
    evaluators.length
  }

  def updateBlockOutputs(y: Array[Double]): Unit = {
    for (k <- 0 until getDimension) {
      if (!wiring.blocks(k).Ctot_sw) {
        evaluators(k).values.Ctot = y(k)
      }
    }
  }

  def getEvaluator(index: Int): BlockEvaluator = {
    evaluators(index)
  }

  var lastt: Double = 0d

  // assigns block outputs (Ctots) and state variables
  def assignStateVariables(y: Array[Double], yDot: Array[Double]): Unit = {
    // update the Ctot values
    updateBlockOutputs(y)
    for (k <- 0 until getDimension) {
      yDot(k) = evaluators(k).values.Add10
    }
  }

  // call with the initial block Ctot values to set up evaluator outputs
  // if block_values are all zero, no need to call
  def computeInitialValues(block_values: Seq[Double]): Unit = {
    var y = new Array[Double](wiring.blocks.length)
    var yDot = new Array[Double](wiring.blocks.length)

    for (k <- 0 until getDimension) {
      y(k) = block_values(k)
    }
    assignStateVariables(y, yDot)
  }

  def computeDerivatives(t: Double, y: Array[Double], yDot: Array[Double]): Unit = {
    // update Ctot values
    for (k <- 0 until getDimension) {
      if (!wiring.blocks(k).Ctot_sw) // necessary?
        evaluators(k).values.Ctot = y(k)
    }
    // update inputs
    for (evaluator <- evaluators) {
      evaluator.input.update(evaluator, this)
    }
    // update values
    for (evaluator <- evaluators) {
      evaluator.evaluate()
    }

    assignStateVariables(y, yDot)
    lastt = t
  }

  def updateRates(): Unit = {
    var y    = new Array[Double](getDimension)
    var yDot = new Array[Double](getDimension)
    for (k <- 0 until getDimension) {
      y(k) = evaluators(k).values.Ctot
    }
    computeDerivatives(lastt, y, yDot)
  }

  def getRates(): Seq[Double] = {
    updateRates()
    evaluators.map(_.values.Add10)
  }

  // Ctot of each block
  def getValues(): Seq[Double] = {
    var y    = new Array[Double](getDimension)
    var yDot = new Array[Double](getDimension)
    for (k <- 0 until getDimension) {
      y(k) = evaluators(k).values.Ctot
    }
    computeDerivatives(lastt, y, yDot)
    evaluators.map(_.values.Ctot)
  }

  def setValue(index: Int, value: Double): Unit = {
    evaluators(index).values.Ctot = value
  }

  def reset(): Unit = {
    for (k <- 0 until getDimension) {
      evaluators(k).values.Ctot = 0d
    }
    lastt = 0d
  }

  def setTime(t: Double): Unit = {
    lastt = t
  }
}

// takes care of solving ODEs
class BlockODESolver(wiring: Wiring) {
  val ode = new WiringODE(wiring)

  // TODO: return output
  def solve(tstart: Double, tend: Double, steps: Int): Unit = {
    if (tend <= tstart)
      throw new RuntimeException("tend should be after tstart")
    var y = new Array[Double](wiring.blocks.length)
    for (k <- 0 until ode.getDimension) {
      y(k) = ode.evaluators(k).values.Ctot
    }
    val y0 = y.clone()
    val fsteps = steps.toDouble
    val integrator = new ClassicalRungeKuttaIntegrator(0.01d)
//     val integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10)
    for (k <- 0 until steps) {
      val hs = tstart+k*(tend - tstart)/fsteps
      val he = tstart+(k+1)*(tend - tstart)/fsteps
//       println(s"integrate $hs to $he")
      integrator.integrate(ode, hs, y, he, y)
    }
  }

  def reset(): Unit = {
    ode.reset()
  }
}

abstract class TimecourseResults(val colnames: Seq[String]) {
  var columns: collection.mutable.Seq[collection.mutable.ArrayBuffer[Double]] = collection.mutable.Seq.fill(colnames.length)(collection.mutable.ArrayBuffer[Double]())
  val colmap: Map[String, collection.mutable.ArrayBuffer[Double]] = (
    for (k <- 0 until colnames.length) yield (colnames(k) -> columns(k))
  ) toMap
  var time_col: collection.mutable.ArrayBuffer[Double] = collection.mutable.ArrayBuffer[Double]()

  def appendCurrentState(): Unit

  def getColumn(k: Int): java.util.List[Double] = {
    columns(k).asJava
  }

  def getTimeColumn(): java.util.List[Double] = {
    time_col.asJava
  }

  def getNumColumns(): Int = {
    columns.length
  }

  def getColNames(): java.util.List[String] = {
    colnames.asJava
  }
}

class TimecourseBlockResults(ode: WiringODE) extends TimecourseResults(ode.wiring.blocks.map(_.getName)) {
  // append the current state of the ode system
  def appendCurrentState(): Unit = {
    val values: Seq[Double] = ode.evaluators.map(_.values.Ctot)
    for (k <- 0 until colnames.length) {
      val name = colnames(k)
      colmap(name) += values(k)
    }
    // append to the time column
    time_col += ode.lastt
  }
}

class TimecourseObservableResults(ode: WiringODE) extends TimecourseResults(ode.outputs.map(_.getObservableName)) {
  // append the current state of the ode system
  def appendCurrentState(): Unit = {
    val values = ode.getOutputValues
    for (k <- 0 until colnames.length) {
      val name = colnames(k)
      colmap(name) += values(k)
    }
    // append to the time column
    time_col += ode.lastt
  }
}

// takes care of collection of ODE solutions over time &
// writing to e.g. csv
class TimecourseGenerator(wiring: Wiring) {
  val solver = new BlockODESolver(wiring)
  val solver_steps = 10

  def simulate(tstart: Double, tend: Double, steps: Int, block_output: Boolean = false): TimecourseResults = {
    val results = if (block_output) new TimecourseBlockResults(solver.ode) else new TimecourseObservableResults(solver.ode)
    // add the initial point
    solver.ode.setTime(tstart)
    solver.ode.updateRates
    results.appendCurrentState
    val fsteps = steps.toDouble
    for (k <- 0 until steps) {
      // println(s"step $k of $steps")
      val hs = tstart+k*(tend - tstart)/fsteps
      val he = tstart+(k+1)*(tend - tstart)/fsteps
      solver.solve(hs,he,solver_steps)
      // current time will be he
      // save output
      solver.ode.updateRates
      results.appendCurrentState
    }
    results
  }

  def updateRates(): Unit = {
    solver.ode.updateRates
  }

  def reset(): Unit = {
    solver.reset
    solver.ode.updateRates
  }

  def getBlockRates(): Seq[Double] = solver.ode.getRates

  def printBlockRates(): Unit = {
    val rates = getBlockRates
    for (k <- 0 until solver.ode.getDimension) {
      val block = wiring.blocks(k)
      val rate = rates(k)
      println(s"  ${block.getName}:")
      println(s"    $rate")
    }
  }
}
