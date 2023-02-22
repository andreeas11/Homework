import scala.collection.mutable

object Regex {
  val alphabet = "abcdefghijklmnopqrstuvwxyz"
  val Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  val nr = "0123456789"

  //<starplusmaybe> ::= <paranteze> '*' | <paranteze> '?' | <paranteze> '+' | <paranteze>
  //<concat> ::= <starplusmaybe> '.' <concat> | <starplusmaybe>
  //<union> ::= <concat> '|' <concat> | <concat>
  //<paranteze> ::= '(' <union> ')' | eps | void | sugars | <charact>
  //<sugars> ::= <alfabet> | <Alfabet> | <nr>
  //<charact> ::= [a-z] | [A-Z] | [0-9]

  def union(s: String) : (String, List[String]) = {
    val (ns, l) = concat(s)

    if(ns.startsWith("|")) { // sum ::= <concat> '|' <concat>
      val (ns2, l2) = union(ns.tail) // scapa de '|'
      return (ns2, "UNION"::l ++ l2)
    } else
      return (ns, l) // sum ::= <concat>
  }

  def concat(s: String) : (String, List[String]) = {
    val (ns, l) = starPlusMaybe(s)
    if(ns.nonEmpty && !ns.startsWith("|") && !ns.startsWith("*") && !ns.startsWith(")")) { // concat ::= <starplusmaybe> '.' <concat>
      val (ns2, l2) = concat(ns)
      return (ns2, "CONCAT"::l ++ l2)
    } else
      return (ns, l) // concat ::= <starplusmaybe>
  }

  def starPlusMaybe(s: String) : (String, List[String]) = {
    val (ns, l) = paranteze(s)
    if(ns.nonEmpty && ns.startsWith("*")) { // starplusmaybe ::= <paranteze> *
      return (ns.substring(1), "STAR"::l)
    } else if (ns.nonEmpty && ns.startsWith("+")) { // starplusmaybe ::= <paranteze> +
      return (ns.substring(1), "PLUS"::l)
    } else if (ns.nonEmpty && ns.startsWith("?")) { // starplusmaybe ::= <paranteze> ?
      return (ns.substring(1), "MAYBE"::l)
    } else
      return (ns, l) // star ::= <paranteze>
  }

  def paranteze(s: String) : (String, List[String]) = {
    if(s.startsWith("(")) { // paranteze ::= '(' <union> ')'
      val (ns, l) = union(s.substring(1)) // scapa de '('
      return (ns.substring(1), l) // scapa de ')'
    }else if(s.startsWith("void"))
      return (s.drop(4), List("void"))
    else if(s.startsWith("eps"))
      return (s.drop(3), List("eps"))
    else if(s.startsWith("["))
      return sugars(s.drop(1))
    else if(s.startsWith("\'"))
      return (s.drop(3), List(s.substring(1,2)))
    else
      return (s.drop(1), List(s.substring(0,1)))
  }

  def sugars(s: String) : (String, List[String]) = {
    if(s.startsWith("a"))
      return (s.drop(4), List("UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION a b c d e f g h i j k l m n o p q r s t u v w x y z"))
    if(s.startsWith("A"))
      return (s.drop(4), List("UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION UNION A B C D E F G H I J K L M N O P Q R S T U V W X Y Z"))
    else
      return(s.drop(4), List("UNION UNION UNION UNION UNION UNION UNION UNION UNION 0 1 2 3 4 5 6 7 8 9"))
  }


  // This function should construct a prenex expression out of a normal one.
  def toPrenex(str: String): String = {
    var list = union(str)._2
    var s = ""
    for(i <-list) {
      s+=i
      s+=" "
    }
//    println(s)
  return s
  }
}
