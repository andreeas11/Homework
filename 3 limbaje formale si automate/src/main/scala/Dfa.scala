import scala.collection.{SortedSet, mutable}

class DfaTranz[A](start: A, end: A, c : Char) {
  def getStart : A = start;
  def getEnd : A = end;
  def getCaracter : Char = c;
}

class Dfa[A] (start : A, end : Set[A], states : Set[A], tranz : Set[DfaTranz[A]]/*, alphabet: Set[Char]*/, nfa: Nfa[A]/* TODO : define the constructor params */){

  // The following methods are only the methods directly called by the test suite. You can (and should) define more.

  def map[B](f: A => B) : Dfa[B] = {
    def trans (tranz : DfaTranz[A]) : DfaTranz[B] = {
      val newStart = f(tranz.getStart)
      val newEnd = f(tranz.getEnd)
      val newC = tranz.getCaracter
      new DfaTranz[B](newStart, newEnd, newC)
    }
    val s = states.map(f);
    val t = tranz.map(x => trans(x))
    val st = f(start)
    val e = end.map(f)
    val dfa = new Dfa[B](st, e, s, t, null)
    return dfa
  } // TODO implement map

  def next(state:A, c: Char): A = {
    for(i <- tranz) {
      if(i.getStart == state && i.getCaracter == c)
        return i.getEnd
    }
    return state
  } // TODO implement next

  def accepts(str: String): Boolean = {
    return nfa.accepts(str)
  } // TODO implement accepts

  def getStart : A = start

  def getEnd : Set[A] = end

  def getStates : Set[A] = states // TODO implement getStates

//  def getAlphabet: Set[Char] = alphabet

  def getNfa: Nfa[A] = nfa

  def isFinal(state: A): Boolean = {
    return end.contains(state)
  }  // TODO implement isFinal
}

// This is a companion object to the Dfa class. This allows us to call the method fromPrenex without instantiating the Dfa class beforehand.
// You can think of the methods of this object like static methods of the Dfa class
object Dfa {
val alphabet = Set('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                   'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                   '0','1','2','3','4','5','6','7','8','9');

  def EClosure(state: Int, nfa:Nfa[Int]): Set[Int] = {
    var nfaAuxTranz = nfa.getTranz
    var eClosure = Set(state)
    def checkrec(state: Int): Set[Int] = {
      for(t <- nfaAuxTranz) {
        if(t.getStart == state && t.getCaracter == '.') {
          eClosure += t.getEnd
          nfaAuxTranz -= t
          checkrec(t.getEnd)
        }
      }
      return eClosure
    }
      for(s<-eClosure)
        checkrec(s)
      return eClosure
  }

  def EClosures(nfa:Nfa[Int]): Set[Set[Int]] = {
    var eClosures = Set[Set[Int]]()
    for(state <- nfa.getStates) {
      eClosures += EClosure(state, nfa)
//      println("epsClos:" + state+ "  " + EClosure(state, nfa))
    }
    return eClosures
  }

  def fromNfaToDfa(eClosures: Set[Set[Int]], nfa: Nfa[Int]): (Dfa[Int]/*Set[(Set[Int], DfaTranz[Int])]/*, Set[Int]*/*/) = {
    var first = EClosure(nfa.getStart, nfa)
    var allStates = Set(first)
    var visitedStates = Set[Set[Int]]()
    var visitedTranz = Set[DfaTranz[Int]]()
//    var rez = (Set[(Set[Int], DfaTranz[Int])])()
    var allTranz = Set[DfaTranz[Int]]()
    while(allStates.nonEmpty) {
      for(state <- allStates) {
        for (c <- alphabet) {
          var nextStates = Set[Int]()
          for(s <- state) {
            var nextState = nfa.next(s, c)
            if(nextState.nonEmpty) {
              nextStates ++= nextState
            }
          }
          var newState = Set[Int]()
          for(next <- nextStates) {
            newState ++= EClosure(next, nfa)
          }
          var tranz = new DfaTranz[Int](stateName(state), stateName(newState), c)
          if(newState.nonEmpty && !visitedStates.contains(newState)) allStates += newState
          if(newState.nonEmpty && !visitedTranz.contains(tranz)){
            allTranz += tranz
            visitedTranz += tranz
//            println("start: "+stateName(state)+" end: "+ stateName(newState)+" char: "+ c)
          }
        }
        visitedStates += state
        allStates -= state
      }
    }
    var endStates = Set[Int]()
    var states = Set[Int]()
    for(v <- visitedStates){
      states += stateName(v)
      for(end <- nfa.getEnd)
      if(v.contains(end)) {
        endStates += stateName(v)
      }
    }
    val myDfa = new Dfa[Int](stateName(first), endStates, states, allTranz, nfa)
//    println("start: " + stateName(first))
//    println("end: "+endStates)
//    println("states "+states)
    return myDfa
  }


  def stateName(states: Set[Int]): Int = {
    var list: List[Int] = (states.toList).sorted
    var str = ""
    if(list.equals(List())) return 666
    if(list.equals(List(0))) return 0
    if(list.head == 0) {
      list = list.tail
      val a = list :+ 0
      for(l<-a) {
        str += l
      }
      if(str.length > 9) str = str.take(9)
      return str.toInt
    }

    for(l<-list) {
      str += l
    }
    if(str.length > 9) str = str.take(9)
    return str.toInt
  }

  def fromPrenex(str: String): Dfa[Int] = {
    val myNfa = Nfa.fromPrenex(str)
    var epsClosures = EClosures(myNfa)
    return fromNfaToDfa(epsClosures, myNfa)
  } // TODO implement Prenex -> Dfa transformation. hint: you should make use of Nfa.fromPrenex to build the Dfa

  // You can add more methods to this object
}
