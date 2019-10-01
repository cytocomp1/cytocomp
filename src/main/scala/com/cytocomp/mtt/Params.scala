// J Kyle Medley, 2016
// Uses roadrunner to compute forward gains

package com.cytocomp.mtt

import com.cytocomp.mtt._
import scala.sys.process._

import org.sbml.jsbml.{SBMLDocument, ASTNode, SBMLError, Reaction=>SBMLReaction, Model=>SBMLModel, Species=>SBMLSpecies}

class AssignParameters(model: SBMLModel) {
  def getGain(rxnId: String, unitQuant: Seq[String]): Double = {
    val sbmlfile = "/home/poltergeist/devel/models/cytomorphic-mm-irrev.xml"
    val code = s"""
from __future__ import print_function
import roadrunner as rr
from os import path

r = rr.RoadRunner(path.expanduser('${sbmlfile}'))
${(for (q <- unitQuant) yield "r."+q+" = 1").mkString("\n")}
print(r.$rxnId, end="")
"""
// println(code)

    var output = ""
    var errors = ""
    // http://stackoverflow.com/questions/5221524/idiomatic-way-to-convert-an-inputstream-to-a-string-in-scala
    val io = new ProcessIO(_ => (),
                           stdout => {output = scala.io.Source.fromInputStream(stdout).mkString; stdout.close()},
                           stderr => {errors = scala.io.Source.fromInputStream(stderr).mkString; stderr.close()})
    val p = Process(Seq("python", "-c", code)).run(io)
    val exitval = p.exitValue()
    val sbmlfile_abs = if (exitval != 0) {
      throw new RuntimeException("Unable to assign parameters:\n" + errors)
    } else {
      output
    }
//     println(s"gain $output")
    output.toDouble
  }

  def apply(net: CyNet, rxn_map: collection.mutable.Map[CyReaction, SBMLReaction]): Unit = {
    for (r <- net.r) {
//       println(s"rxn $r")
      r match {
        case u: CyRxUniBi => {
          val sbmlreaction = rxn_map(r)
          val gain = getGain(sbmlreaction.getId, Seq(u.r.toString))
//           println(s"Gain for reaction ${sbmlreaction.getId} = $gain")
          u.setGain(gain)
        }
        case u: CyRxBiUni => {
          val sbmlreaction = rxn_map(r)
          val gain = getGain(sbmlreaction.getId, Seq(u.r1.toString, u.r2.toString))
//           println(s"Gain for reaction ${sbmlreaction.getId} = $gain")
          u.setGain(gain)
        }
        case _ => null
      }
    }
  }
}
