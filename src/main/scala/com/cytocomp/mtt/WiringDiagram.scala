// J Kyle Medley, 2017
// Produces a diagram of a wiring configuration

package com.cytocomp.mtt.mxrender

import com.cytocomp.mtt._

import javax.swing._;

import scala.collection.JavaConverters._

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;

class WiringDiagramStyle(val blockwidth: Int, val hpad: Int, val blockheight: Int, val vpad: Int) {
  def calcWidth (ncols: Int): Int = (blockwidth+2*hpad)*ncols
  def calcHeight(nrows: Int): Int = (blockheight+2*vpad)*nrows
}

class Point2d(val x: Double, val y: Double) {
}

object Point2d {
  def apply(x: Double, y: Double): Point2d = new Point2d(x,y)
}

class Box2d(p1: Point2d, p2: Point2d) {
  val min = Point2d(if (p1.x < p2.x) p1.x else p2.x, if (p1.y < p2.y) p1.y else p2.y)
  val max = Point2d(if (p1.x > p2.x) p1.x else p2.x, if (p1.y > p2.y) p1.y else p2.y)

  def width = max.x - min.x
  def height = max.y - min.y
}

object Box2d {
  def apply(xmin: Double, ymin: Double, xmax: Double, ymax: Double) = {
    new Box2d(Point2d(xmin,ymin), Point2d(xmax,ymax))
  }
}

class GridCell(val block: Block, val row: Int, val col: Int, val bounds: Box2d, val hpad: Double, val vpad: Double) {
  def blockx      = bounds.min.x
  def blocky      = bounds.min.y
  def blockWidth  = bounds.width
  def blockHeight = bounds.height

  def cellx       = bounds.min.x-hpad
  def celly       = bounds.min.y-vpad
  def cellWidth   = bounds.width  + 2*hpad
  def cellHeight  = bounds.height + 2*vpad
}

class Grid(val wiring: Wiring, val ncols: Int, val blockwidth: Double, val blockheight: Double, val hpad: Double, val vpad: Double) {
  // grid of blocks
  val cellmap = collection.mutable.Map[Block,GridCell]()

  // initialize grid
  var j = 0
  var x = 0.5*blockwidth + hpad
  var k = 0
  var y = 0.5*blockheight + vpad
  for (block <- wiring.blocks) {
    val p = Point2d(x-0.5*blockwidth,y-0.5*blockheight)
    val q = Point2d(x+0.5*blockwidth,y+0.5*blockheight)

    // add cell to grid
    val cell = new GridCell(block, k, j, Box2d(p.x, p.y, p.x+blockwidth, p.y+blockheight), hpad, vpad)
    cellmap += (block -> cell)

    j+=1
    x+=blockwidth+2*hpad
    if (j == ncols) {
      j=0
      x=0.5*blockwidth + hpad
      y+=blockheight+2*vpad
      k+=1
    }
  }

  val nrows = if (j == 0) k else k+1

  def cells = cellmap.values
}

/**
 * A wiring diagram.
 */
class WiringDiagram(val wiring: Wiring, val ncols: Int, val style: WiringDiagramStyle = new WiringDiagramStyle(150,70,320,70)) {

  def render(outfile: String) {
    object graph extends mxGraph {
      override def isPort(cell: Object): Boolean = {
        val geo = getCellGeometry(cell)
        if (geo != null) geo.isRelative() else false;
      }

      override def getToolTipForCell(cell: Object): String = {
        if (model.isEdge(cell))
          convertValueToString(model.getTerminal(cell, true)) + " -> " + convertValueToString(model.getTerminal(cell, false))
        else
          super.getToolTipForCell(cell)
      }

      override def isCellFoldable(cell: Object, collapse: Boolean) = false
    }

    val ss: scala.collection.mutable.Map[String, Object] = graph.getStylesheet().getDefaultEdgeStyle().asScala
//     ss(mxConstants.STYLE_EDGE) = mxEdgeStyle.ElbowConnector

    val myparent = graph.getDefaultParent()
    graph.getModel().beginUpdate()

    // maps terminals to graphical ports
    val portmap = collection.mutable.Map[Terminal,mxCell]()

    val grid = new Grid(wiring, ncols, style.blockwidth, style.blockheight, style.hpad, style.vpad)

    for (cell <- grid.cells) {
      val block = cell.block

      // render block
      val vblock: mxCell = graph.insertVertex(myparent, null, block.getName, cell.blockx, cell.blocky, cell.blockWidth, cell.blockHeight, "fillColor=#d3e0ba").asInstanceOf[mxCell]
      vblock.setConnectable(false)
      val geo: mxGeometry = graph.getModel().getGeometry(vblock)
      geo.setOffset(new mxPoint(0,style.blockheight/2+style.vpad/3))

      // loop through input terminals
      val term_spacing=30
      var term_off=term_spacing
      for (t <- Seq(block.Atot, block.Btot, block.Cfree, block.Cprod, block.Cdeg, block.Ctot_in, block.Dfree)) {
        val port: mxCell = graph.insertVertex(vblock, null, t.getName, 0, term_off, 8, 8, "align=left;fillColor=#919a80").asInstanceOf[mxCell]
        val portgeo: mxGeometry = graph.getModel().getGeometry(port)
        portgeo.setOffset(new mxPoint(10, 0))
        port.setVertex(true)

        // terminal has input?
        if (!t.input.isEmpty) {
          val inputvert: mxCell = graph.insertVertex(myparent, null, t.input.get.getQuotedName, cell.blockx-60, cell.blocky+term_off+8/2-20/2, 46, 20, "align=center;fillColor=#c1d4ef").asInstanceOf[mxCell]
          graph.insertEdge(myparent, null, "", inputvert, port)
        }

        portmap += (t -> port)
        term_off += term_spacing
      }

      // loop through output terminals
      term_off=term_spacing
      for (t <- Seq(block.Afree, block.Bfree, block.Cfree_out, block.Ctot, block.rate_fw, block.rate_rv, block.fw_tot, block.rv_tot, block.fw_up, block.rv_up)) {
        val port: mxCell = graph.insertVertex(vblock, null, t.getName, cell.blockWidth, term_off, 8, 8, "align=right;fillColor=#919a80").asInstanceOf[mxCell]
        val portgeo: mxGeometry = graph.getModel().getGeometry(port)
        portgeo.setOffset(new mxPoint(-10, 0))
        port.setVertex(true)

        // terminal has observable?
        if (!t.observable.isEmpty) {
          val obsvert: mxCell = graph.insertVertex(myparent, null, s"${if (t.observable.get.double_val) "1/2" else ""}${t.observable.get.getQuotedName}", cell.blockx+cell.blockWidth+20, cell.blocky+term_off+8/2-20/2, 46, 20, "align=center;fillColor=#f1d9d7").asInstanceOf[mxCell]
          graph.insertEdge(myparent, null, "", port, obsvert)
        }

        portmap += (t -> port)
        term_off += term_spacing
      }
    }

    // add connections
    for ((t,port) <- portmap) {
      for ((v,positive) <- wiring.outgoing(t)) {
        v match {
          case v: Terminal => if (portmap contains v) {
            val portgeo = graph.getModel().getGeometry(port)

            val edge = graph.insertEdge(myparent, null, "", port, portmap(v))
            val g = graph.getModel().getGeometry(edge)
            g.setRelative(false)
            import collection.mutable.ArrayBuffer
            val srccell = grid.cellmap(t.block)
            val dstcell = grid.cellmap(v.block)
            val drows = dstcell.row - srccell.row
            val dcols = dstcell.col - srccell.col
          }
        }
      }
    }

    graph.getModel().endUpdate()

    val f = new JFrame("Wiring Diagram")
    val graphComponent = new mxGraphComponent(graph)
    f.getContentPane().add(graphComponent)

    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    f.setSize(style.calcWidth(ncols), style.calcHeight(grid.nrows))
    f.setUndecorated(true)
    f.setVisible(true)

    import java.awt.image.BufferedImage

    val b = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB)
    val g2d = b.createGraphics
    f.paintAll(g2d)
    import java.io.File
    val q = new File(outfile)
    import javax.imageio._;
    ImageIO.write(b, "png", q)
    f.dispose()
  }
}