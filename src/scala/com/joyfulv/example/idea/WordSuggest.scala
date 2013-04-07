package com.joyfulv.example.idea;
/**
 * Submit from Siu Leung (Victor) Chan
 * 
 * Idea: word suggestion: check for case mismatch, repeat characters or different vowels
 * 
 * TODO:
 * 1. Make the check as partial function
 * 2. Correct vowel should pick up the closest word 
 * e.g. should have "weke" correct to "wake"; now is "waka".  It is correct but not perfect
 * 3. Repeat test fail test case "faaoaoad"
 * i.e. it would return faoad from checkRepeat and return feued from vowelCheck
 */

object WordSuggest {
  
  val words = io.Source.fromFile("/usr/share/dict/words").getLines.toList
  
  val nosuggestion = "NO SUGGESTION"
    
  def checkRepeat(s:String):String = {
    val sc = s.toCharArray.toList
    val ss = checkRepeatInner(sc, List()).mkString
    if (words contains ss) ss
    else checkVowels(ss)
  }
    
  def checkRepeatInner(s:List[Char], accu:List[Char]):List[Char] = {
    if (s.length <= 1) (s:::accu).reverse
    else {
        val splitPos = lookahead(s,1)
        if (splitPos > 0) {
          val (x, xs) = s.splitAt(splitPos)
          checkRepeatInner(xs, accu)
        }
        else {
          s match {
            case h::t => checkRepeatInner(t, h::accu)
          }
        }
    } 
  }
  
  def lookahead(x:List[Char], count:Int):Int = {
     if ((x.length/2) >= count) {
       val (hl, tl) = x.splitAt(count)
       val (tlh, tll) = tl.splitAt(hl.length)
       if (hl == tlh) 
         count
       else
         lookahead(x, count+1)
     } else 0
  }
    
  def checkVowels(s:String):String = {
    val vl = List('a','e','i','o','u')
    
    val sz = s.toLowerCase.toCharArray().zipWithIndex
    val ll = for {
      w <- words.map(_.toLowerCase())
      if w.length == s.length
      if (w.toCharArray.zipWithIndex.diff(sz).unzip._1).filterNot(vl.contains).length == 0 && 
          sz.diff(w.toCharArray.zipWithIndex).unzip._1.filterNot(vl.contains).length == 0
    } yield w
    if (ll.length > 0) checkCase((checkCloset(sz,ll,0,"")))
    else checkCase(s)
  }
  
  def checkCloset(w:Array[(Char, Int)], wlist:List[String], count:Int, closest:String ): String = {
     if (wlist.length == 0) closest
     else {
	     val wl = wlist.head
	     val newCount = w.diff(wl.toCharArray().zipWithIndex).length
	     if (newCount > count) checkCloset(w, wlist.tail, newCount, wl)
	     else
	       checkCloset(w, wlist.tail, count, closest)
     }
  }
  
  def checkCase(s:String):String = {
     val (a, xa) = s splitAt 1
     val mix = a.toUpperCase + xa.toLowerCase
     if (words contains s.toLowerCase ) s.toLowerCase
     else if (words contains s.toUpperCase ) s.toUpperCase
     else if (words contains mix ) mix
     else nosuggestion
  }
  
  def check(s:String):String = {
    checkRepeat(s)
  }

  def start {
    print(">")
    for(ln <- io.Source.stdin.getLines) {
      println(check(ln))
      print(">")
    }
  }  
}

object Main extends App {
  WordSuggest.start
}