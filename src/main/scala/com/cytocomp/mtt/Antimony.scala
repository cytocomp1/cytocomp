package com.cytocomp.mtt

import com.cytocomp.mtt._
import scala.sys.process._

object Antimony {
  def readFile(sbfile: String): SBML.Wrapper = {
//     val r = Runtime.getRuntime()
    val sbmlfile = changeExt(sbfile, "sb", "xml")
//     println(s"reading Sb file ${sbfile} and writing ${sbmlfile}")

    val code = s"""
from __future__ import print_function
import antimony as sb
from os import path

sbfile_abs = path.abspath(path.expanduser('${sbfile}'))
sbmlfile_abs = path.abspath(path.expanduser('${sbmlfile}'))
sb.loadAntimonyFile(sbfile_abs)
e = sb.getLastError()
if e != '':
  raise RuntimeError('Encountered errors while converting Antimony file:\\n' + e)
else:
  sbmlstr = sb.getSBMLString(sb.getModuleNames()[-1])
  with open(sbmlfile_abs, 'w') as f:
    f.write(sbmlstr)
    print(sbmlfile_abs, end="")
"""

    var output = ""
    var errors = ""
    // http://stackoverflow.com/questions/5221524/idiomatic-way-to-convert-an-inputstream-to-a-string-in-scala
    val io = new ProcessIO(_ => (),
                           stdout => {output = scala.io.Source.fromInputStream(stdout).mkString; stdout.close()},
                           stderr => {errors = scala.io.Source.fromInputStream(stderr).mkString; stderr.close()})
    val p = Process(Seq("python", "-c", code)).run(io)
    val exitval = p.exitValue()
    val sbmlfile_abs = if (exitval != 0) {
      throw new RuntimeException("Unable to read Antimony file:\n" + errors)
    } else {
      output
    }

//     println(s"reading abs sbml file ${sbmlfile_abs}")
    SBML.readFile(sbmlfile_abs)
  }
}
