import scala.collection.mutable

class Tranz[A](start: A, end: A, c : Char) {
  def getStart : A = start;
  def getEnd : A = end;
  def getCaracter : Char = c;
  override def toString: String = {
    val outString = "(" + start + ", " + end + ", " + c +  ")"
    outString
  }
}

class Nfa[A](start : A, end : Set[A], states : Set[A], tranz : Set[Tranz[A]] /* TODO : define the constructor params */) {

  // The following methods are only the methods directly called by the test suite. You can (and should) define more.


  def map[B](f: A => B) : Nfa[B] = { // TODO implement map
    def trans (tranz : Tranz[A]) : Tranz[B] = {
      val newStart = f(tranz.getStart)
      val newEnd = f(tranz.getEnd)
      val newC = tranz.getCaracter
      new Tranz[B](newStart, newEnd, newC)
    }
    val s = states.map(f);
    val t = tranz.map(x => trans(x))
    val st = f(start)
    val e = end.map(f);
    val nfa = new Nfa[B](st, e, s, t)
    return nfa
  }

  def next(state:A, c: Char): Set[A] = { // TODO implement next
    var s = Set[A]()
    for(i <- tranz) {
      if(i.getStart == state && i.getCaracter == c) {
        s ++= Set(i.getEnd)
      }
    }
    return s
  }

  def isEps (a : (A, String)) : Set[(A, String)] = {
    val nStates = next(a._1, '.')
    var x = Set[(A, String)]()
    for (i <- nStates) {
      x ++= Set((i, a._2))
    }
//    println(x)
    return x
  }

  def accepts(str: String): Boolean = { // TODO implement accepts
    def nextAcc(a: (A, String)): Set[(A, String)] = {
      var nStates = Set[A]()
      if(a._2.nonEmpty) nStates = next(a._1, a._2.head)
      var x = Set[(A, String)]()
      for (i <- nStates) {
        if(x.equals(Set())) x = Set((i, a._2.drop(1)))
        else x ++= Set((i, a._2.substring(1)))
      }
      return x
    }
    def finalConf(conf: Set[(A, String)]): Boolean = {
      for(i <- conf)
        if(i._2.equals("") && isFinal(i._1)) return true
      return false
    }
    var init = (start, str)
    var configs : Set[(A, String)] = Set(init)
    var prevConf = Set[(A, String)]()

    while(!prevConf.equals(configs) && !finalConf(configs)) {
      prevConf = configs
      for (conf <- configs) {
        var epsTuples = isEps(conf)
        var tuples = nextAcc(conf)
        configs ++= tuples
        configs ++= epsTuples
      }
    }
    if(finalConf(configs)) return true
    return false
  }

  def getStart : A = start

  def getEnd : Set[A] = end

  def getStates : Set[A] = states // TODO implement getStates

  def getTranz : Set[Tranz[A]] = tranz

  def isFinal(state: A): Boolean = { // TODO implement isFinal
    return end.contains(state)
  }

  def nrOfStates: Int = {
    var nr = 0;
    for(i <- 0 until states.size)
      nr += 1
    return nr;
  }
}

// This is a companion object to the Nfa class. This allows us to call the method fromPrenex without instantiating the Nfa class beforehand.
// You can think of the methods of this object like static methods of the Nfa class
object Nfa {

  def vid : Nfa[Int] = {
    val stari = Set(0,1)
    val t = Set[Tranz[Int]]()
    val end = Set(1)
    val nfa = new Nfa[Int](0, end, stari, t)
    return nfa
  }

  def epsi : Nfa[Int] = {
    val stari = Set(0)
    val t = Set[Tranz[Int]]()
    val nfa = new Nfa[Int](0, stari, stari, t)
    return nfa
  }

  def caract(caracter : Char) : Nfa[Int] = {
    val transition = Set(new Tranz[Int](0, 1, caracter))
    val stari = Set(0, 1)
    val end = Set(1)
    val nfa = new Nfa[Int](0, end, stari, transition)
//    if(caracter == ' ') {
//      println("states: "+stari+" tranz:   "+"st: "+0+" en: "+1+" char: "+caracter)
//    }
    return nfa
  }

  def concat(nfa1: Nfa[Int], nfa2: Nfa[Int]) : Nfa[Int] = {
    val newNfa2 = nfa2.map(x => x + nfa1.nrOfStates)
    val stari = nfa1.getStates ++ newNfa2.getStates
    val states = stari

    var tranz = nfa1.getTranz ++ newNfa2.getTranz
    for(i <- nfa1.getEnd)
      tranz += new Tranz[Int](i, newNfa2.getStart, '.')

    val nfa = new Nfa[Int](nfa1.getStart, newNfa2.getEnd, states, tranz)
    return nfa
  }

  def union(nfa1: Nfa[Int], nfa2: Nfa[Int]) : Nfa[Int] = {
    val newNfa2 = nfa2.map(x => x + nfa1.nrOfStates)
    val stari = nfa1.getStates ++ newNfa2.getStates
    val stateNr = nfa1.nrOfStates + newNfa2.nrOfStates
    val st = stateNr
    val en = st + 1
    val states = stari ++ Set(st, en)

    var tranz = nfa1.getTranz ++ newNfa2.getTranz;
    tranz += new Tranz[Int](st, nfa1.getStart,'.' )
    tranz += new Tranz[Int](st, newNfa2.getStart, '.')

    for(i <- nfa1.getEnd)
      tranz += new Tranz[Int](i, en, '.')
    for(i <- newNfa2.getEnd)
      tranz += new Tranz[Int](i, en, '.')

    val nfa = new Nfa[Int](st, Set(en), states, tranz)
    return nfa
  }

  def star(nfa: Nfa[Int]) : Nfa[Int] = {
    val st = nfa.nrOfStates
    val en = nfa.nrOfStates + 1
    val states = nfa.getStates ++ Set(st, en)

    var tranz = nfa.getTranz
    tranz += new Tranz[Int](st, nfa.getStart, '.')
    tranz += new Tranz[Int](st, en, '.')

    for(i <- nfa.getEnd)
      tranz += new Tranz[Int](i, en, '.')

    for(i <- nfa.getEnd)
      tranz += new Tranz[Int](i, nfa.getStart, '.')
    val newNfa = new Nfa[Int](st, Set(en), states, tranz)
    return newNfa
  }

  def plus(nfa: Nfa[Int]) : Nfa[Int] = {
////    println(nfa.map(_+nfa.nrOfStates).getStates)
//    var aux = star(nfa.map(_+nfa.nrOfStates))
    return concat(nfa, star(nfa));
  }

  def maybe(nfa: Nfa[Int]) : Nfa[Int] = {
    return union(nfa, epsi)
  }

  def mySplit(str: String): List[String] = {
    if (str.isEmpty) return List[String]()
    else if(str.startsWith("void")) return "void" :: splitSpace(str.substring(4))
    else if(str.startsWith("eps")) return "eps" :: splitSpace(str.substring(3))
    else if(str.startsWith("UNION")) return "UNION" :: splitSpace(str.substring(5))
    else if(str.startsWith("CONCAT")) return "CONCAT" :: splitSpace(str.substring(6))
    else if(str.startsWith("STAR")) return "STAR" :: splitSpace(str.substring(4))
    else if(str.startsWith("PLUS")) return "PLUS" :: splitSpace(str.substring(4))
    else if(str.startsWith("MAYBE")) return "MAYBE" :: splitSpace(str.substring(5))
    else if(str.length > 2 && str.charAt(0) == '\'' && str.charAt(2) == '\'')
      return str.substring(1,2) :: splitSpace(str.substring(3))
    return str.substring(0,1) :: splitSpace(str.substring(1))
  }

  def splitSpace(str: String): List[String] = {
    if(str.isEmpty) return mySplit(str)
    return mySplit(str.substring(1))
  }

  def splitPre(str : String):Nfa[Int] = {
    val stack = new mutable.Stack[Nfa[Int]]()
    var p = mySplit(str)
//    println(p)
    for (i <- p.reverse) {
      if(i == "void") stack.push(vid)
      else if(i == "eps") stack.push(epsi)
      else if(i.length == 1) {
        stack.push(caract(i.charAt(0)))
      }
      else if(i == "CONCAT") {
        val nfa1 = stack.pop()
        val nfa2 = stack.pop()
        stack.push(concat(nfa1, nfa2))
      }
      else if(i == "UNION") {
        val nfa1 = stack.pop()
        val nfa2 = stack.pop()
        stack.push(union(nfa1, nfa2))
      }
      else if(i == "STAR") stack.push(star(stack.pop()))
      else if(i == "PLUS") stack.push(plus(stack.pop()))
      else if(i == "MAYBE") stack.push(maybe(stack.pop()))
    }
    return stack.pop()
  }

    def fromPrenex(str: String): Nfa[Int] = { // TODO implement Prenex -> Nfa transformation.
//      println("tranz: "+splitPre(str).getTranz)
//      println("states: "+splitPre(str).getStates +" start: " + splitPre(str).getStart + " end: "+ splitPre(str).getEnd)

      splitPre(str)

  }
  // You can add more methods to this object
}