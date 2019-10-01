// MTT
// J Kyle Medley 2016-2019

package com.cytocomp.mtt

import com.cytocomp.mtt._
import com.cytocomp.mtt.mxrender.{WiringDiagramStyle,Grid}
import java.util.Base64
// import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.io.{BufferedOutputStream, FileOutputStream}
import java.io.{File, BufferedWriter, FileWriter}


class SimulinkExporter(val wiring: Wiring, val program: Option[SRAMProgram] = None) {
  val block_map = collection.mutable.Map[Block,String]()
  val port_map  = collection.mutable.Map[Terminal,String]()
  var block_counter = 1
  val use_human_readable_names = true
  val assign_images = true

  // https://gist.github.com/capotej/1975463
  def mkdirp(path: String) {
    var prepath = ""
    for(dir <- path.split("/")){
      prepath += (dir + "/")
      new java.io.File(prepath).mkdir()
    }
  }

  def writeChipLibrary(target_directory: String): Unit = {
    mkdirp(target_directory)
    val s = new BufferedOutputStream(new FileOutputStream(Paths.get(target_directory, "Chip_Library.slx").toString()))
    Stream.continually(s.write(ChipLibraryData()))
    s.close()
  }

  def writeBlockImage(target_directory: String): Unit = {
    mkdirp(target_directory)
    import java.io.{File,FileInputStream,FileOutputStream}
    val src = new File("assets/chip-block.png")
    val dest = new File(Paths.get(target_directory, "chip-block.png").toString())
    new FileOutputStream(dest) getChannel() transferFrom(
        new FileInputStream(src) getChannel, 0, Long.MaxValue )
  }

  def newBlockId(block: Block): String = {
    val result = s"block${block_counter}"
    block_map += (block -> result)
    block_counter += 1
    result
  }

  def getPortCoords(t: InputTerminal, x: Double, y: Double, width: Double, height: Double): Seq[Double] = {
    val x_offset = -50
    val increment = 60
    val y_offset = (t match {
      case x: Atot => 0
      case x: Btot => increment
      case x: Cfree => 2*increment
      case x: Dfree => 3*increment
      case x: Cdeg => 4*increment
      case x: Cprod => 5*increment
      case x: Ctot_in => 6*increment
      case _ => 0
    })
    Seq(x+x_offset,y+y_offset,x+x_offset+20,y+y_offset+20)
  }

  def inputPortToPin(t: InputTerminal): Int = {
    t match {
      case x: Atot => 1
      case x: Btot => 2
      case x: Cfree => 3
      case x: Dfree => 4
      case x: Cdeg => 5
      case x: Cprod => 6
      case x: Ctot_in => 7
    }
  }

  def outputPortToPin(t: OutputTerminal, invert: Boolean = false): Int = {
    t match {
      case x: Afree => 1
      case x: Bfree => 3
      case x: rate_fw => 5
      case x: fw_tot => 6
      case x: fw_up => 7
      case x: Ctot => if (!invert) 11 else 9
      case x: rate_rv => 14
      case x: rv_tot => 15
      case x: rv_up => 16
      case x: Cfree_out => 18
      case x: Dfree_out => 19
    }
  }

  def addBlock_Aug15_Jon1(block: Block, x: Double, y: Double, width: Double, height: Double): String = {
    val id = newBlockId(block)
    var block_lines = s"% Block '${block}'\n"
    block_lines += s"add_block('Chip_Library/Aug15_Jon1',[sys '/${id}'],'Position',[${x}, ${y}, ${x+width}, ${y+height}]);\n"

    // parameters
    block_lines += s"set_param([sys '/${id}'],'KDfw','${block.KDfw}');\n"
    block_lines += s"set_param([sys '/${id}'],'KDrv','${block.KDrv}');\n"
    block_lines += s"set_param([sys '/${id}'],'kr','${block.kr}');\n"
    block_lines += s"set_param([sys '/${id}'],'kdeg','${block.kdeg}');\n"
    block_lines += s"set_param([sys '/${id}'],'ratC','${block.ratC}');\n"
    block_lines += s"set_param([sys '/${id}'],'n','${block.n}');\n"
    block_lines += s"set_param([sys '/${id}'],'A_FB_EN','${if (block.A_FB_EN) 1 else 0}');\n"
    block_lines += s"set_param([sys '/${id}'],'B_FB_EN','${if (block.B_FB_EN) 1 else 0}');\n"
    block_lines += s"set_param([sys '/${id}'],'sel_rate','${if (block.FF_EN_sw2) 1 else 0}');\n"
    block_lines += s"set_param([sys '/${id}'],'sel_Ctot','${if (!block.Ctot_sw) 1 else 0}');\n"

    block_lines += "\n"

    block_lines

    // // loop through output terminals
    // for (t <- Seq(block.Afree, block.Bfree, block.Cfree_out, block.Ctot, block.rate_fw, block.rate_rv, block.fw_tot, block.rv_tot, block.fw_up, block.rv_up)) {
    //   // terminal has observable?
    //   if (!t.observable.isEmpty) {
    //     // wire to scope
    //   }
    // }
  }

  def remapSRAMVariable(variable: Int): Int = {
    variable match {
      // case 7 => 9
      // case 8 => 10
      case 9 => 11 // Ctot copy 1
      case 10 => 12 // Ctot copy 2
      case 11 => 13 // Ctot copy 3
      case 16 => 5 // rate_fw
      case 18 => 6 // fw_tot
      case 20 => 7 // fw_up
      case 21 => 8 // fw_up
      case 22 => 16 // rv_up
      case 23 => 17 // rv_up
      case _ => variable
    }
  }

  def addConnectionsBasedOnSRAM(program: SRAMProgram): String = {
    var lines: String = ""
    // add connections
    for (rule <- program.getRoutingRules) {
      rule match {
        case src_rule: RoutingRuleFromTerminal => {
          val src_block: Block = src_rule.terminal.block
          for (dst_rule <- program.getOtherRulesForWire(src_rule)) {
            dst_rule match {
              case dst_rule: RoutingRuleFromTerminal => {
                val dst_block: Block = dst_rule.terminal.block
                lines += s"add_line(sys,'${block_map(src_block)}/${remapSRAMVariable(src_rule.variable)}','${block_map(dst_block)}/${remapSRAMVariable(dst_rule.variable)}','autorouting','on');\n"
              }
            }
          }
        }
      }
    }
    return lines
  }

  def generateConnectionCode(grid: Grid): String = {
    var lines: String = "\n% ** Connections **\n"
    // add connections
    for (block <- wiring.blocks) {
      val cell = grid.cellmap(block)
      val x = cell.blockx
      val y = cell.blocky
      val width = cell.blockWidth
      val height = cell.blockHeight

      lines += s"\n% Incoming connections for block '${block.getName}'\n"

      for (input_terminal <- block.input_terminals) {
        val block_name = block_map(block)

        val incoming = wiring.incoming(input_terminal).toSeq
        val num_incoming = incoming.length + (if (!input_terminal.input.isEmpty) 1 else 0)

        // either the block itself or the proxy adder
        val receiving_block_name = if (num_incoming > 1)
            block_name+"_"+s"${input_terminal.getName}" + "_adder"
          else
            block_name

        if (num_incoming > 1) {
          // need to make an adder
          val port_pos = getPortCoords(input_terminal,x,y,width,height)
          val port_pos_str = s"${port_pos(0)}, ${port_pos(1)}, ${port_pos(2)}, ${port_pos(3)}"
          lines += s"add_block('simulink/Math Operations/Sum', [sys '/${receiving_block_name}'],'Position',[${port_pos_str}]);\n"

          val signs = (incoming.map(x => "+") ++ (if (!input_terminal.input.isEmpty) Seq("+") else Seq.empty[String])).mkString("")
          lines += s"set_param([sys '/${receiving_block_name}'],'Inputs','${signs}');\n"

          lines += s"add_line(sys, '${receiving_block_name}/1','$block_name/${inputPortToPin(input_terminal)}','autorouting','on');\n"
        }

        // terminal has input?
        if (!input_terminal.input.isEmpty) {
          val input_pos = getPortCoords(input_terminal,x,y,width,height)
          val x_offset = if (num_incoming > 1) -20 else 0
          val input_pos_str = s"${input_pos(0)+x_offset}, ${input_pos(1)}, ${input_pos(2)+x_offset}, ${input_pos(3)}"
          lines += s"add_block('simulink/Sources/Constant', [sys '/${block_name}_${input_terminal.getName}'],'Position',[${input_pos_str}]);\n"
          lines += s"set_param([sys '/${block_name}_${input_terminal.getName}'],'Value','${input_terminal.getValue}');\n"
          lines += s"set_param([sys '/${block_name}_${input_terminal.getName}'],'BackgroundColor','[0.754,0.828,0.934]'); "
          // lines += s"add_line(sys, '${incoming_port_name}','${receiving_port_name}','autorouting','on');\n"
        }

        val incoming_port_names = incoming.map(_ match {
          case (output_terminal: OutputTerminal, positive) => {
            val incoming_block = block_map(output_terminal.block)
            val incoming_pin = outputPortToPin(output_terminal, !positive)
            s"$incoming_block/$incoming_pin"
          }
          case _ => throw new RuntimeException("Could not match incoming rule")
        }) ++
           (if (!input_terminal.input.isEmpty) Seq(s"${block_name}_${input_terminal.getName}/1") else Seq.empty[String])

        // ports on the block itself or the proxy adder
        val receiving_port_names =
          if (num_incoming > 1) {
            (1 to num_incoming).map(port => s"$receiving_block_name/$port")
          } else {
            Seq(s"${block_name}/${inputPortToPin(input_terminal)}")
          }

        for ((incoming_port_name, receiving_port_name) <- incoming_port_names.zip(receiving_port_names)) {
          lines += s"add_line(sys, '${incoming_port_name}','${receiving_port_name}','autorouting','on');\n"
        }
      }
    }
    return lines
  }

  def generateObservableCode(grid: Grid): String = {
    var lines = "\n%Output\n\n"

    val scope_x = grid.ncols*(grid.blockwidth+2*grid.hpad) + 50
    val scope_y = 50
    val scope_width = 30
    val scope_height = 30
    val scope_pos_str = s"${scope_x}, ${scope_y}, ${scope_x+scope_width}, ${scope_y+scope_height}"
    val scope_id = "outputscope"
    lines += s"add_block('simulink/Commonly Used Blocks/Scope', [sys '/$scope_id'],'Position',[${scope_pos_str}]);\n"

    var k = 0
    var connection_lines: String = ""
    for (block <- wiring.blocks) {
      val block_name = block_map(block)
      connection_lines += s"\n% Output block '${block.getName}'\n"

      for (output_terminal <- block.output_terminals) {
        if (!output_terminal.observable.isEmpty) {
          k += 1
          connection_lines += s"add_line(sys, '${block_name}/${outputPortToPin(output_terminal)}','$scope_id/${k}','autorouting','on');\n"
        }
      }
    }
    lines += s"set_param([sys '/${scope_id}'],'NumInputPorts','$k');\n"
    return lines + connection_lines
  }

  val human_readable_block_names: Map[Block,String] = wiring.blocks.map(block => (block -> block.getName.replace("∅",""))).toMap

  def renameBlocks(): String = {
    var lines: String = "\n% Rename blocks\n"
    for (block <- wiring.blocks) {
      lines += s"set_param([sys '/${block_map(block)}'],'Name','${human_readable_block_names(block)}');\n"
    }
    return lines
  }

  def assignImages(system_name: String): String = {
    var lines: String = "\n% Assign images\n"
    lines += s"img = imread('chip-block.png');\n"
    for (block <- wiring.blocks) {
      val block_id = block_map(block)
      val perm_block_id = if (!use_human_readable_names) block_map(block) else human_readable_block_names(block)
      lines += s"% $block_id\n"
      lines += s"set_param([sys '/$block_id'], 'UserDataPersistent', 'on');\n"
      lines += s"ud.img = img;\n"
      lines += s"set_param([sys '/$block_id'], 'UserData', ud);\n"
      lines += s"Simulink.Mask.create([sys '/$block_id']);\n"
      lines += s"set_param([sys '/$block_id'],'MaskDisplay','ud = get_param([''$system_name'' ''/$perm_block_id''], ''UserData''); image(ud.img);');\n"
      lines += s"set_param([sys '/$block_id'], 'MaskIconOpaque', 'opaque-with-ports');\n"
    }
    return lines
  }

  def writeGeneratorScript(target_file: String, system_name: String, ncols: Int, style: WiringDiagramStyle): Unit = {
    var script = s"sys = '${system_name}';\n"+
      "new_system(sys);\n"+
      "open_system(sys);\n"

    val grid = new Grid(wiring, ncols, style.blockwidth, style.blockheight, style.hpad, style.vpad)

    var block_counter = 1
    for (block <- wiring.blocks) {
      val cell = grid.cellmap(block)
      script += addBlock_Aug15_Jon1(block, cell.blockx, cell.blocky, cell.blockWidth, cell.blockHeight)

      block_counter += 1
    }

    if (!program.isEmpty) {
      script += addConnectionsBasedOnSRAM(program.get)
    } else {
      script += generateConnectionCode(grid)
    }

    script += generateObservableCode(grid)

    if (assign_images)
      script += assignImages(system_name)

    if (use_human_readable_names)
      script += renameBlocks()

    val file = new File(target_file)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(script)
    bw.close()
  }

  /**
    * Creates a Simulink model in the target directory.
    * The directory will be created if it does not exist.
    */
  def exportToDirectory(target_directory: String, system_name: String, ncols: Int, style: WiringDiagramStyle = new WiringDiagramStyle(240,70,420,70)): Unit = {
    writeChipLibrary(target_directory)
    if (assign_images)
      writeBlockImage(target_directory)
    writeGeneratorScript(Paths.get(target_directory,"generator.m").toString(), system_name, ncols, style)
  }
}
