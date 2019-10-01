// J Kyle Medley, 2016

package com.cytocomp.mtt

// TODO: make drag-drop protein block editor
// TODO: make drag-drop network editor
// convert between the above two

import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.DefaultRenderContext

import py4j.GatewayServer

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

import scala.collection.JavaConverters._
import scala.collection.mutable.LinkedHashMap

// http://stackoverflow.com/questions/4730866/scala-expression-to-replace-a-file-extension-in-a-string
// FIXME: handle '.' in directory
object changeExt {
  def apply(filename: String, ext: String, newext: String) = (filename.split('.').map((x:String) => if (x == ext) newext else x)) mkString(".")
}

// FIXME: handle '.' in directory
// TODO: allow trailing name: lib.so.1.2.3
object checkExt {
  def apply(filename: String, ext: String) = {
    if (filename.split('.').last == ext) true else false
  }
}

class NamedOptionDef(val name: String, val help: String) {

}

// Option with single argument
class ValOption1Def[T](name: String, help: String) extends NamedOptionDef(name, help) {

}

class Command(val name: String, val help: String, val shortname:String="") {

}

class MTT(val wiring: Wiring) {
  // -- procedural --

  def getWiringString(): String = {
    s"$wiring"
  }

  def getWiring(): Wiring = wiring

//   def loadSBMLFile(file: String): Unit = {
//     val doc: SBML.Wrapper = if (checkExt(file, "sb"))
//       Antimony.readFile(file)
//     else if (checkExt(file, "xml") || checkExt(file, "sbml"))
//       readSBML(file)
//     else {
//       throw new RuntimeException(s"Extension not recognized for file ${file}")
//     }
//
//     val sbml_importer = new SBMLImporter(doc)
//     val cynet = sbml_importer.getNetwork
//     wiring = Wiring.fromNetwork(cynet)
//   }

  var timecourse = new TimecourseGenerator(wiring)

  def getTimecourse(): TimecourseGenerator = timecourse

  def simulate(start: Double, stop: Double, steps: Int, block_output: Boolean = false): TimecourseResults = {
    timecourse.simulate(start, stop, steps, block_output)
  }

  def reset(): Unit = {
    timecourse.reset()
  }

  def updateRates(): Unit = {
    timecourse.updateRates()
  }

  def renderDiagram(columns: Int, file: String): Unit = {
    val diagram = new mxrender.WiringDiagram(wiring, columns)
    diagram.render(file)
  }

  def getNumBlocks(): Int = {
    wiring.blocks.length
  }

  def getBlock(index: Int): Block = {
    wiring.blocks(index)
  }

  def getBlockName(index: Int): String = {
    wiring.blocks(index).getName
  }

  def getBlockValue(index: Int): Double = {
    timecourse.solver.ode.getValues()(index)
  }

  def setBlockValue(index: Int, value: Double): Unit = {
    timecourse.solver.ode.setValue(index, value)
  }

  def getBlockRate(index: Int): Double = {
    timecourse.solver.ode.getRates()(index)
  }

  def get_Cprod(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.Cprod
  }

  def get_Cdeg(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.Cdeg
  }

  def get_rate_fw(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.rate_fw
  }

  def get_rate_rv(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.rate_rv
  }

  def get_fw_tot(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.fw_tot
  }

  def get_rv_tot(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.rv_tot
  }

  def get_Afree(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.Afree
  }

  def get_Bfree(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.Bfree
  }

  def get_Dfree(index: Int): Double = {
    timecourse.solver.ode.evaluators(index).values.Dfree
  }

  def getQuantity(index: Int, quantity: String): Double = {
    timecourse.solver.ode.updateRates()
    val values = timecourse.solver.ode.evaluators(index).values
    quantity match {
      case "fw_tot" => values.fw_tot
      case _ => throw new RuntimeException(s"Quantity not found: $quantity")
    }
  }

  def getValueSummary(index: Int): String = {
    timecourse.solver.ode.evaluators(index).values.getSummary()
  }

  // def updateRates(): Unit = {
  //   timecourse.solver.ode.updateRates()
  // }

  def compileSRAM(): SRAMProgram = SRAMProgram.singleChipFromWiring(wiring)

  def compileSRAMWithBlockRemap(remap: java.util.List[Int]): SRAMProgram = {
    SRAMProgram.programChipWithMappedBlocks(wiring, LinkedHashMap[Block, Int](wiring.blocks.zip(remap.asScala): _*))
  }

  def digitize(v: Double): java.util.List[Int] = ShiftRegProgram.digitizeJ(v)

  def exportToSimulink(target_directory: String, system_name: String, n_columns: Int): Unit = {
    new SimulinkExporter(wiring).exportToDirectory(target_directory, system_name, n_columns)
  }

  def toSBML(): String = {
    new SBMLExporter(wiring).writeToString()
  }

  def getReactionBlockIndices(): java.util.List[Int] = {
    wiring match {
      case wiring: Wiring2 => wiring.reaction_to_block_indices.toBuffer.asJava
      case _ => throw new RuntimeException("Not a Wiring2")
    }
  }

//   def compileShiftReg()

  // -- functional --

//   def transpileSBMLFile(file: String): Wiring = {
//     val doc: SBML.Wrapper = if (checkExt(file, "sb"))
//       Antimony.readFile(file)
//     else if (checkExt(file, "xml") || checkExt(file, "sbml"))
//       readSBML(file)
//     else {
//       throw new RuntimeException(s"Extension not recognized for file ${file}")
//     }
//
//     val sbml_importer = new SBMLImporter(doc)
//     val cynet = sbml_importer.getNetwork
//
// //         println("Loops:")
// //         println(cynet.loops)
//
//     Wiring.fromNetwork(cynet)
//   }
//
//   def simulateSBMLFile(file: String, start: Double, stop: Double, steps: Int): Unit = {
//     val wiring: Wiring = transpileSBMLFile(file)
//
//     println("\n")
//     println("Wiring:")
//     println(wiring)
//
//     val diagram = new mxrender.WiringDiagram(wiring, 3)
// //         diagram.render("/tmp/wd.png")
//     val timecourse = new TimecourseGenerator(wiring)
//     println("Rates at beginning:")
//     timecourse.printBlockRates
//     timecourse.simulate(start, stop, steps)
//     println("Rates at end:")
//     timecourse.printBlockRates
//   }
}

object MTTUtils {
  def readSBML(sbmlfile: String): SBML.Wrapper = {
    try {
      val doc = SBML.readFile(sbmlfile)
      if (doc.getNumErrors == 0) {
        println(s"Successfully read SBML file ${sbmlfile}")
        return doc
      } else {
        var errors = ""
        errors += s"${doc.getNumErrors} encountered in reading document ${sbmlfile}\n"
        for (i <- 0 until doc.getNumErrors) {
          errors += s"  Error ${i+1} of ${doc.getNumErrors}: " + doc.getError(i).getMessage + "\n"
        }
        throw new RuntimeException(errors)
      }
    } catch {
      // JSBML can throw pretty much anything
      case e: Throwable => {
        throw new RuntimeException(s"Not readable: ${sbmlfile}")
      }
    }
  }
}

object CyNetFromFile {
  def apply(file: String): CyNet = {
    val doc: SBML.Wrapper = if (checkExt(file, "sb"))
      Antimony.readFile(file)
    else if (checkExt(file, "xml") || checkExt(file, "sbml"))
      MTTUtils.readSBML(file)
    else {
      throw new RuntimeException(s"Extension not recognized for file ${file}")
    }

    val sbml_importer = new SBMLImporter(doc)
    sbml_importer.getNetwork
  }
}

object MTTFromFile {
  def apply(file: String): MTT = {
    val doc: SBML.Wrapper = if (checkExt(file, "sb"))
      Antimony.readFile(file)
    else if (checkExt(file, "xml") || checkExt(file, "sbml"))
      MTTUtils.readSBML(file)
    else {
      throw new RuntimeException(s"Extension not recognized for file ${file}")
    }

    val sbml_importer = new SBMLImporter(doc)
    val cynet = sbml_importer.getNetwork

    var wiring: Wiring = Wiring2.fromNetwork(cynet)

    new MTT(wiring)
  }
}

object WiringFromFile {
  def apply(file: String): Wiring = {
    val doc: SBML.Wrapper = if (checkExt(file, "sb"))
      Antimony.readFile(file)
    else if (checkExt(file, "xml") || checkExt(file, "sbml"))
      MTTUtils.readSBML(file)
    else {
      throw new RuntimeException(s"Extension not recognized for file ${file}")
    }

    val sbml_importer = new SBMLImporter(doc)
    val cynet = sbml_importer.getNetwork

    val wiring: Wiring = Wiring.fromNetwork(cynet)
    wiring
  }
}

object Wiring2FromFile {
  def apply(file: String, margins: java.util.HashMap[String,Double], default_margin: Double, input_val: Double, current_assignment: String, disable_block_elision: Boolean): Wiring2 = {
    val doc: SBML.Wrapper = if (checkExt(file, "sb"))
      Antimony.readFile(file)
    else if (checkExt(file, "xml") || checkExt(file, "sbml"))
      MTTUtils.readSBML(file)
    else {
      throw new RuntimeException(s"Extension not recognized for file ${file}")
    }

    val sbml_importer = new SBMLImporter(doc, margins.asScala.toMap, default_margin)
    val cynet = sbml_importer.getNetwork

    val wiring: Wiring2 = Wiring2.fromNetwork(cynet, input_val, current_assignment, disable_block_elision)
    wiring
  }
}

object BareWiring {
  def apply(block_names: Seq[String]): Wiring =
      new Wiring(block_names.map(name => new Block(name)), Seq.empty[Observable])
}

object BareWiringJ {
  def apply(block_names: java.util.List[String]): Wiring = BareWiring(block_names.asScala)
}

object MTTFromWiring {
  def apply(wiring: Wiring): MTT = {
    new MTT(wiring)
  }
}

// class MTTpy extends MTT {
// }

object MTTApp extends App {
  def writeBashCompletion() {
    val completion="""
_mtt()
{
  local cur prev opts
  COMPREPLY=()
  cur="${COMP_WORDS[COMP_CWORD]}"
  prev="${COMP_WORDS[COMP_CWORD-1]}"
  cmds="transpile afakecmd"

  COMPREPLY=( $(compgen -W "${cmds} --help -h" -- ${cur}) )
  return 0
  if [[ ${prev} == --help ]] ; then
    COMPREPLY=( $(compgen -W "${cmds}" -- ${cur}) )
    return 0
  fi
}
complete -F _mtt mtt
"""
    val engine = new TemplateEngine
    engine.allowCaching =  false
    val t = engine.compileMoustache(completion)
    val buffer = new StringWriter()
    val ctx = new DefaultRenderContext("", engine, new PrintWriter(buffer))
//     ctx.attributes("name") = "Homer Simpson"
//     ctx.attributes("value") = 123.45
//     ctx.attributes("taxed_value") = "3.50"
//     ctx.attributes("in_ca") = true
    val z = t.render(ctx)
    println(buffer.toString)
    val pw = new PrintWriter(new File("mtt-completion.bash" ))
    try {
      pw.write(buffer.toString)
    } finally {
      pw.close()
    }
  }

  // Commands
  var cmds: List[Command] = Nil

  def addCommand(name: String, help: String) {
    cmds :+= new Command(name,help)
  }

  def printCommands(): String = {
    var r = ""
    for (c <- cmds) {
      r+=s"  ${c.name}: "
      r+=c.help+"\n"
    }
    r
  }

  // Options
  var opts: List[NamedOptionDef] = Nil

  def addOption(o: NamedOptionDef) {
    opts :+= o
  }

  def printOptions(): String = {
    var r = ""
    for (o <- opts) {
      r+=s"  ${o.name}: "
      r+=o.help+"\n"
    }
    r
  }

  // http://stackoverflow.com/questions/2315912/scala-best-way-to-parse-command-line-parameters-cli

  addCommand("transpile",
            "Compile / transform a source form to a dest form."+
            "Transpilation proceeds SBML -> mass-action network -> cytoprogram." +
            "This command can accept input at any stage and produce output at any subsequent stage.")

  addCommand("-h, --help",
            "Produce this help message.")

  addCommand("--write-bash-completion",
            "Write a Bash completion def.")

  addOption(new NamedOptionDef("--help", "Produce help text"))

  val usage = s"""
MTT: Model topology tool

Usage: mtt COMMAND [OPTION] input

Commands/Switches:
${printCommands}

Type: mtt COMMAND --help
to get more help on a specific command
"""

  if (args.length == 0) println(usage)

  if (args.length >= 1) {
    val arg = args.head
    val rest = args.tail
    arg match {
      case "--help" | "-h" => println(usage)
      case "--write-bash-completion" => writeBashCompletion
      case "crn" => {
        if (rest.length == 0) {
          println(args(0))
          println("Expected filename after 'transpile'")
          System.exit(1)
        }
        val infile = rest.head

        val cynet = CyNetFromFile(infile)

        println("\n")
        println(cynet)
      }
      case "transpile" => {
        if (rest.length == 0) {
          println(args(0))
          println("Expected filename after 'transpile'")
          System.exit(1)
        }
        val infile = rest.head

        val mtt = MTTFromFile(infile)

        val wiring = mtt.wiring

        println("\n")
        println("Wiring:")
        println(wiring)
      }
      case "simulate" => {
        if (rest.length == 0) {
          println(args(0))
          println("Expected filename after 'transpile'")
          System.exit(1)
        }
        val infile = rest.head

        val mtt = MTTFromFile(infile)

        println("Rates at beginning:")
        mtt.timecourse.printBlockRates
        mtt.simulate(0.0, 10.0, 100)
        println("Rates at end:")
        mtt.timecourse.printBlockRates
        println("Values:")
        println(mtt.timecourse.solver.ode.evaluators.map(_.values.Ctot))
      }
      case "server" => {
        val gateway: GatewayServer = new GatewayServer()
        gateway.start
      }
      case _ => {
        println("Must specify a command")
        System.exit(1)
      }
    }
  }
}
