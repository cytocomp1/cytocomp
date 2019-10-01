// MTT
// J Kyle Medley 2016-2019

package com.cytocomp.mtt

import com.cytocomp.mtt._

import scala.collection.mutable.Set
import scala.collection.mutable.MultiMap
import scala.math.pow

object AssignInputs {
  def apply(reaction_map: MultiMap[CyReaction,Block], value: Double): Unit = {
    for ((reaction,blocks) <- reaction_map) {
      for (block <- blocks) {
        reaction match {
          case x: CyRxConstProduction => {
            block.Btot.setInput(new Input("Btot", block.Btot, value))
            block.Atot.setInput(new Input(x.p.toString + "prod", block.Atot, value))
            block.A_FB_EN_sw = false
          }
          case x: CyRxUniUni => {
            block.Btot.setInput(new Input("Btot", block.Btot, value))
            if (x.reversible) {
              block.Dfree.setInput(new Input("Dfree", block.Dfree, value))
            }
          }
          case x: CyRxUniBi => {
            block.Btot.setInput(new Input("Btot", block.Btot, value))
          }
          case x: CyRxBiUni => {
            if (x.reversible) {
              block.Dfree.setInput(new Input("Dfree", block.Dfree, value))
            }
          }
          case x: CyRxDimerization => {
            if (x.reversible) {
              block.Dfree.setInput(new Input("Dfree", block.Dfree, value))
            }
          }
          case x: CyRxMonomerization => {
            block.Btot.setInput(new Input("Btot", block.Btot, value))
          }
          case _ => ;
        }
      }
    }
  }
}

object AssignCurrentsMinKD {
  def apply(reaction_map: MultiMap[CyReaction,Block]): Unit = {
    for ((reaction,blocks) <- reaction_map) {
      for (block <- blocks) {
        val kf = reaction.kf.getOrElse(1d)
        val kr = reaction.kr.getOrElse(0d)
        // println(s"reaction $reaction kf = $kf")
        // the minimum of KDfw,KDrv should be 10
        if (kf >= kr) {
          block.KDfw = 10d
          block.kr   = kf*block.KDfw
        } else {
          block.KDrv = 10d
          block.kr   = kr*block.KDrv
        }

        reaction match {
          case x: CyRxConstProduction => {
            // both Atot and Btot should have inputs
            block.kr = block.kr/pow(block.Atot.getValue()*block.Btot.getValue(), block.n)
            // KDrv not used so reset it
            block.KDrv = 10d
          }
          case x: CyRxUniUni => {
            // Btot should have an input
            block.kr = block.kr/pow(block.Btot.getValue(), block.n)
          }
          case x: CyRxUniBi => {
            // Btot should have an input
            block.kr = block.kr/pow(block.Btot.getValue(), block.n)
          }
          case x: CyRxBiUni => {
            if (x.reversible) {
              block.kr = block.kr/block.Dfree.getValue()
            }
          }
          case _ => ;
        }

        if (kf >= kr) {
          block.KDrv = if (kr > 0d)
              pow(block.kr/kr * (if (reaction.reversible) block.Dfree.getValue else 1d),
              1d/block.n)
            else
              10d
        } else {
          block.KDfw = block.kr/kf
        }
      }
    }
  }
}

object AssignCurrentsMaxKD {
  def apply(reaction_map: MultiMap[CyReaction,Block]): Unit = {
    for ((reaction,blocks) <- reaction_map) {
      for (block <- blocks) {
        if (!reaction.reversible) {
          val kf = reaction.kf.getOrElse(1d)
          block.KDfw = 10d
          block.kr   = kf*block.KDfw
          block.KDrv = 10d

          reaction match {
            case x: CyRxUniUni => {
              // Btot should have an input
              block.kr = block.kr/block.Btot.getValue()
            }
            case _ => ;
          }
        } else {
          val kf = reaction.kf.getOrElse(1d)
          val kr = reaction.kr.getOrElse(1d)

          val Btot_factor = reaction match {
            case x: CyRxUniUni => block.Btot.getValue()
            case _ => 1d;
          }
          // the maximum of KDfw,KDrv should be 10
          // if (kf < kr) {
          //   block.KDfw = 10d
          //   block.kr   = kf*block.KDfw
          //   block.KDrv = block.kr/kr * (if (reaction.reversible) block.Dfree.getValue else 1d)
          // } else {
            block.KDrv = 10d
            block.kr   = kr*block.KDrv/block.Dfree.getValue()
            // println(s"block $block kr = ${block.kr}, rx kr = $kr, KDrv = ${block.KDrv}")
            block.KDfw = pow(block.kr, 1d/block.n)*Btot_factor/kf
            // println(s"block $block KDfw = pow(${block.kr}/${kf}, ${1d/block.n})")
          // }

          // reaction match {
          //   case x: CyRxConstProduction => {
          //     // both Atot and Btot should have inputs
          //     block.kr = block.kr/pow(block.Atot.getValue()*block.Btot.getValue(), block.n)
          //     // KDrv not used so reset it
          //     block.KDrv = 10d
          //   }
          //   case x: CyRxUniUni => {
          //     // Btot should have an input
          //     block.KDfw = block.kr/pow(block.Btot.getValue(), block.n)
          //   }
          //   case x: CyRxUniBi => {
          //     // Btot should have an input
          //     block.kr = block.kr/pow(block.Btot.getValue(), block.n)
          //   }
          //   case x: CyRxBiUni => {
          //     block.KDfw = block.KDfw*block.Atot.getValue()
          //     println(s"block $block KDfw *= ${block.Atot.getValue()}")
          //   }
          //   case _ => ;
          // }
        }
      }
    }
  }
}

object AssignCurrentsUniversal_kr {
  def apply(reaction_map: MultiMap[CyReaction,Block]): Unit = {
    for ((reaction,blocks) <- reaction_map) {
      for (block <- blocks) {
        val kf = reaction.kf.getOrElse(1d)
        val kr = reaction.kr.getOrElse(1d)

        block.kr = 1d
        block.KDfw = block.kr/kf
        block.KDrv = block.kr/kr
      }
    }
  }
}



object AssignCurrentsKDfw {
  def apply(reaction_map: MultiMap[CyReaction,Block], KDfw: Double): Unit = {
    for ((reaction,blocks) <- reaction_map) {
      for (block <- blocks) {
        val kf = reaction.kf.getOrElse(1d)
        val kr = reaction.kr.getOrElse(0d)
        block.KDfw = 10d
        block.kr   = kf*block.KDfw

        reaction match {
          case x: CyRxConstProduction => {
            // both Atot and Btot should have inputs
            block.kr = block.kr/pow(block.Atot.getValue()*block.Btot.getValue(), block.n)
            // KDrv not used so reset it
            block.KDrv = 10d
          }
          case x: CyRxUniUni => {
            // Btot should have an input
            block.kr = block.kr/pow(block.Btot.getValue(), block.n)
          }
          case x: CyRxUniBi => {
            // Btot should have an input
            block.kr = block.kr/pow(block.Btot.getValue(), block.n)
          }
          case x: CyRxBiUni => {
            if (x.reversible) {
              block.kr = block.kr/block.Dfree.getValue()
            }
          }
          case _ => ;
        }

        block.KDrv = if (kr > 0d)
            pow(block.kr/kr * (if (reaction.reversible) block.Dfree.getValue else 1d),
            1d/block.n)
          else
            10d
      }
    }
  }
}
