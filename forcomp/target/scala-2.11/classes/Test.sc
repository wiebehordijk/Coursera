import forcomp.Anagrams._

//wordOccurrences("Halloh").toMap
//sentenceOccurrences(List("Hallo", "wereld"))
//dictionaryByOccurrences(wordOccurrences("ate"))
//wordAnagrams("ate")
//combinations(List(('a',2),('b', 2))).mkString("\n")
//subtract(List(('a',2),('b',2)), List(('a',1)))
//sentenceAnagrams(List("yes", "man"))
//
val occurrences = sentenceOccurrences(List("and"))
val comb = combinations(occurrences)
for (c <- comb; w <- dictionaryByOccurrences(c); a <- List(List())) yield w :: a
for {
  combination <- combinations(occurrences)
  word <- dictionaryByOccurrences(combination)
  //anagramsWithRest <- anagrams(subtract(occurrences, combination))
} yield (word, subtract(occurrences, combination)) //:: anagramsWithRest

def anagrams(occurrences: Occurrences): List[Sentence] = {
  if (occurrences.isEmpty) List[Sentence](List[Word]())
  else for {
    combination <- combinations(occurrences)
    word <- dictionaryByOccurrences(combination)
    anagramsWithRest: Sentence <- anagrams(subtract(occurrences, combination))
  } yield word :: anagramsWithRest
}
anagrams(List(('a',1),('n',1)))

dictionary.contains("yes")
