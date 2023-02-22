package bianca.tema2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Main {

    Map<String, Dictionary> dicts = new HashMap();

    void run() throws FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Word> words = new ArrayList<>();
        for (File file: new File("input").listFiles()) { //iterez prin fisiere si pun cuvintele in hashmap (in functie de primele 2 litere ale numelui fisierului)
            words = gson.fromJson(new FileReader(file), new TypeToken<ArrayList<Word>>() {
            }.getType());
            String language = file.getName().substring(0,2);
            getOrCreate(language).words.addAll(words);
        }

        //TESTE

        String[] t1 = {"activitate distractivă (mai ales la copii)", "distracție cu cărți, cu zaruri etc. care " +
                "angajează de obicei sume de bani și care se desfășoară după anumite reguli respectate de parteneri, " +
                "câștigul fiind determinat de întâmplare sau de calcul"};
        String[] t2 = {"Competiție sportivă de echipă căreia îi este proprie și lupta sportivă", "Acțiunea de a interpreta " +
                "un rol într-o piesă de teatru; felul cum se interpretează"};
        String[] t3 = {"zbenguiala", "joacă"};
        Definition d1 = new Definition("Dicționarul explicativ al limbii române", "definitions", 1923, t1);
        Definition d2 = new Definition("Dicționar universal al limbei române", "definitions", 2018, t2);
        Definition d3 = new Definition("Dicționar de sinonime", "synonyms", 2009, t3);
        List<Definition> def1 = new ArrayList<>();
        def1.add(d2);
        def1.add(d1);
        String[] sg1 = {"joc"};
        String[] pl1 = {"jocuri"};
        Word w1 = new Word("joc", "game", "noun", sg1, pl1, def1);

        //addWord
        addWord(w1, "ro");
        addWord(w1, "ro");
        //addDefinitionForWord
        addDefinitionForWord("merge", "ro", d3);
        addDefinitionForWord("apa", "fr", d3);
        //removeDefinition
        removeDefinition("merge", "ro", "Dicționar de sinonime");
        removeDefinition("merge", "ro", "Dictionar de apa");
        removeDefinition("apa", "ro", "Dictionar de apa");
        //translateWord
        System.out.println(translateWord("merge", "ro", "en"));
        System.out.println(translateWord("joc", "ro", "fr"));
        System.out.println(translateWord("jeu", "fr", "ro"));
        System.out.println(translateWord("jeu", "fr", "en"));
        System.out.println(translateWord("foc", "fr", "en"));
        //translateSentence
        System.out.println(translateSentence("pisică merge", "ro", "en"));
        System.out.println(translateSentence("pisică joc", "ro", "fr"));
        System.out.println(translateSentence("chat jeu", "fr", "ro"));
        //translateSentences
        for (String s : translateSentences("pisică joc", "ro", "fr"))
            System.out.println(s);
        for (String s : translateSentences("chat jeu", "fr", "ro"))
            System.out.println(s);
        //removeWord
        removeWord("merge", "ro");
        removeWord("apa", "ro");
        removeWord("chat", "fr");
        //getDefinitionsForWord
        for (Definition definition : getDefinitionsForWord("joc", "ro"))
            System.out.println(definition.year + " " + definition.dict + " " + definition.dictType + " " + Arrays.toString(definition.text));
        for (Definition definition : getDefinitionsForWord("apa", "ro"))
            System.out.println(definition.year + " " + definition.dict + " " + definition.dictType + " " + Arrays.toString(definition.text));
        for (Definition definition : getDefinitionsForWord("jeu", "fr"))
            System.out.println(definition.year + " " + definition.dict + " " + definition.dictType + " " + Arrays.toString(definition.text));
        //exportDictionary
        exportDictionary("fr");
        exportDictionary("ro");
    }

    public static void main(String[] args) throws FileNotFoundException {
        new Main().run();
    }

    Dictionary getOrCreate(String language) { //daca dictionarul exista este returnat, altfel il creaza
        Dictionary dict = dicts.get(language);
        if (dict == null) {
            dict = new Dictionary();
            dicts.put(language, dict);
        }
        return dict;
    }

    boolean addWord(Word word, String language){ // apeleaza apeleaza metoda anterioara pentru a adauga cuvantul in dictionarul potrivit cu ajutorul functiei addWord
        return getOrCreate(language).addWord(word);
    }

    boolean removeWord(String word, String language){ // apeleaza removeWord pentru dictionarul aferent
        return dicts.get(language).removeWord(word);
    }

    boolean addDefinitionForWord(String word, String language, Definition definition){ // apeleaza addDefinitionForWord pentru dictionarul aferent
        return dicts.get(language).addDefinitionForWord(word, definition);
    }

    boolean removeDefinition(String word, String language, String dictionary){ // apeleaza removeDefinition pentru dictionarul aferent
        return dicts.get(language).removeDefinition(word,dictionary);
    }

    String translateWord(String word, String fromLanguage, String toLanguage){
        if (fromLanguage.equals("en")) return dicts.get(toLanguage).translateWordFromEn(word); // daca cuvantul trebuie tradus din engleza apeleaza translateWordFromEn si il traduce in toLanguage
        if (toLanguage.equals("en")) return dicts.get(fromLanguage).translateWordToEn(word); // daca cuvantul trebuie tradus in engleza apeleaza translateWordToEn si il traduce din FromLanguage
        return dicts.get(toLanguage).translateWordFromEn(dicts.get(fromLanguage).translateWordToEn(word)); // daca trebuie tradus din alta limba acesta este tradus mai intai din fromLanguage in engleza, iar apoi in toLanguage
    }

    String translateSentence(String sentence, String fromLanguage, String toLanguage){
        String[] words = sentence.split(" "); // despartim stringul intr-un array de stringuri
        StringBuilder sentenceBuilder = new StringBuilder();
        for (String word : words) { // luam fiecare cuvant in parte si il traducem asemanator metodei anterioare
            if (fromLanguage.equals("en")) {
                sentenceBuilder.append(dicts.get(toLanguage).translateWordFromEn(word));
                sentenceBuilder.append(" ");
            } else if (toLanguage.equals("en")) {
                sentenceBuilder.append(dicts.get(fromLanguage).translateWordToEn(word));
                sentenceBuilder.append(" ");
            } else {
                if (!sentenceBuilder.isEmpty()) sentenceBuilder.append(" ");
                sentenceBuilder.append(dicts.get(toLanguage).translateWordFromEn(dicts.get(fromLanguage).translateWordToEn(word)));
            }
        }
        sentence = sentenceBuilder.toString(); // refacem un sentence si il intoarcem
        return sentence;
    }

    ArrayList<String> translateSentences(String sentenceStr, String fromLanguage, String toLanguage){
        String[] words = sentenceStr.split(" "); // despartim stringul intr-un array de stringuri
        ArrayList<String> sentences = new ArrayList<>();
        sentences.add("");
        for (String word : words) { // luam fiecare cuvant in parte si il traducem asemanator metodei anterioare
            ArrayList<String> newSentences = new ArrayList<>();
            final ArrayList<String> translations = fromLanguage.equals("en") ?
                    dicts.get(toLanguage).translateWordFromEnOptions(word) :
                    dicts.get(toLanguage).translateWordFromEnOptions(dicts.get(fromLanguage).translateWordToEn(word));
            outer: for (String sentence: sentences) {
                for (String translation : translations) {
                    String newSentence = (sentence.isEmpty() ? "" : sentence + " ") + translation;
                    newSentences.add(newSentence);
                    if (newSentences.size() >= 3) break outer;
                }
            }
            sentences = newSentences;
        }
        return sentences;
    }

    ArrayList<Definition> getDefinitionsForWord(String word, String language){ // apeleaza getDefinitionsForWord pentru dictionarul aferent
        return dicts.get(language).getDefinitionsForWord(word);
    }

    void exportDictionary(String language){ // apeleaza exportDictionary pentru dictionarul aferent
        dicts.get(language).exportDictionary();
    }

}
