package com.joyfulv.example.game

/**
 * A TicTacToe game example implemented in functional aspect
 */
object TicTacToe {
	
	/*****
	 * Players definition
	 ****/

	trait Player {
		val sym:Char
		val name:String
		def getInput:Pos
	}
	
	class HumanPlayer(val name:String, val sym:Char) extends Player {
	
		def getInput = {
			val x = askForInput
			x foreach ( y => print(y.toString+" ") )
			if ((x.length == 2) && 
				((x filter ( y => y forall Character.isDigit )).length == 2) &&
				((x filter ( y => y.toInt >= 0 && y.toInt <= 2 )).length == 2))
				new Tuple2(x(0).toInt,x(1).toInt)
			else
				getInput
		}
		
		def askForInput = readLine("\n"+ name + " turn :>").split(" ")
	}
	
	/*
	 * TODO
	class ComputerPlayer(val name :String, val sym:Char) extends Player {

		def getInput = {
			print("computer turn :>")
			new Tuple2(0,0)
		}
	}
	
	def findEmptySlot(map: MapRep)  = {
		map.zipWithIndex.map( x => (x._1.zipWithIndex.filter (_._1 == '_')).map(y => (x._2, y._2)))
	}
	*/
	
	/*****
	 * Game property definition
	 ****/
	
	/*
	val basicMap = """|-|-|-
	                  |-|-|-
			          |-|-|-""".stripMargin
	*/
	
	val maxTurn  = 9
	val DRAWGAME = "Draw Game"
	val WON      = " Won!"
	 
	type Turns = Int

	def nextTurn(turn:Turns):Turns = if (turn == 0) 1 else 0 
					 
	type Pos = Tuple2[Int, Int]
	
	type MapRep = Vector[Vector[Char]]
	
	//val initialMap = Vector.tabulate(3,3) { (i, j) => '-' }
	val initialMap = Vector.fill(3,3)('-')
	
	def updateMap(sym:Char, pos:Pos, map:MapRep): MapRep = {
		return map.updated(pos._1, map(pos._1).updated(pos._2, sym))       
	}
	
	def isValidMove(pos:Pos, map:MapRep):Boolean = {
		return map(pos._1)(pos._2).equals('-')
	}
	
	def renderMap(map: MapRep) = {
		println("\nNew move:")
		for (i <- 0 to 2){
			for( j <- 0 to 1) {
				print(map(i)(j) + "|")
			}
			println(map(i)(2))
		}
		println
	}
		
	def isWon(sym: Char, map: MapRep):Boolean = {
		return isHorizonLine(sym, map) ||
		       isVerticalLine(sym, map) ||
			   isDiagonalLine(sym, map)
	}
	
	def isHorizonLine(sym:Char, map: MapRep): Boolean = {
		return map.filter(_ equals Vector.fill(3)(sym)).length > 0
	}
	
	def isVerticalLine(sym:Char, map: MapRep): Boolean = {
		return isHorizonLine(sym, map transpose)
	}

	def isDiagonalLine(sym:Char, map: MapRep): Boolean = {
		isDiagonalLineLeft(sym, map) || isDiagonalLineRight(sym, map)
	}
	
	def isDiagonalLineLeft(sym:Char, map: MapRep) : Boolean = {
		map.zipWithIndex.map(x => toVector(roundRobinMove(x._1.toList, x._2)(MoveForward))).forall(_.last == sym)
	}
	
	def isDiagonalLineRight(sym:Char, map: MapRep) : Boolean = {
		map.zipWithIndex.map(x => toVector(roundRobinMove(x._1.toList, x._2)(MoveBackward))).forall(_.head == sym)
	}
	
	def toVector[T](ll:List[T]): Vector[T] = {
		Vector.empty ++ ll
	}
	
	def MoveForward[T](ll:List[T]): List[T] = (List(ll.last)):::ll.init
	
	def MoveBackward[T](ll:List[T]): List[T] = ll.tail:::List(ll.head)
	
	def roundRobinMove[T](ll:List[T], step: Int)(f: List[T] => List[T]):List[T] = {
	    step match {
			case 0 => ll
			case _ => roundRobinMove(f(ll), step-1)(f)
		}
	}
	
	/****
	 * Game action
	 ****/
	
	
	def gameStart(p1: Player, p2: Player, map:MapRep) : String = {
		val players = List(p1, p2)
		val startTurn = 0
				
		def gameInTurn(playerTurn:Turns, map: MapRep, totalTurns:Turns) : String = {
		
			if (totalTurns > maxTurn)
				DRAWGAME
			else {
				val input = players(playerTurn) getInput

				if (isValidMove(input, map)) {
					val newMap = updateMap(players(playerTurn).sym, input, map)
					renderMap(newMap)
					if (isWon(players(playerTurn).sym, newMap)) 
						players(playerTurn).name + WON
					else 
						gameInTurn(nextTurn(playerTurn), newMap, totalTurns + 1)
				} else 
					gameInTurn(playerTurn, map, totalTurns)
			}
		}
		
		gameInTurn(startTurn, initialMap, 1)
	}
	
		
	object main {
	
		println("\nGame Start\n")
		
		renderMap(initialMap)
	
		val p1 = new HumanPlayer("Player 1", 'o')
		val p2 = new HumanPlayer("Player 2", 'x')
		
		println( gameStart(p1, p2, initialMap) )
		
		println("\nGame over\n")
		
	}
}

object main extends App {
  TicTacToe.main
}