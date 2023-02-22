package bianca.tema2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Dictionary{
    public List<Word> words = new ArrayList<>();

    Word findWord(String word) {
        for( Word w : words) { //iterez prin lista de cuvinte
            if (word.equals(w.word)) { //caut cuvantul si ii dau return odata gasit; daca nu exista intorc null
                return w;
            }
        }
        return null;
    }

    boolean addWord(Word word){
        if (findWord(word.word) == null) { //apelez findWord pentru a vedea daca exista cuvantul si daca nu exisyta il adaug
            words.add(word);
            return true;
        }
        System.out.println("Word already exists!");
        return false;
    }

    boolean removeWord(String word){
        for( Word w : words){ //iterez prin lista de cuvinte
            if(word.equals(w.word)) { //caut cuvantul care trebuie sters
                words.remove(w); //sterg cuvantul gasit
                return true;
            }
        }
        System.out.println("Word doesn't exist!");
        return false;
    }

    boolean addDefinitionForWord(String word, Definition definition){
        for( Word w : words) { //iterez prin lista de cuvinte
            if (word.equals(w.word)) { //caut cuvantul la care trebuie adaugata definitia si o adaug
                w.definitions.add(definition);
                return true;
            }
        }
        System.out.println("Word doesn't exist!");
        return false;
    }

    boolean removeDefinition(String word, String dictionary){
        for( Word w : words) { //iterez prin lista de cuvinte
            if (word.equals(w.word)) { //caut cuvantul la care trebuie stearsa definitia
                for (ListIterator<Definition> i = w.definitions.listIterator(); i.hasNext();) { //caut prin definitile cuvantului cea care trebuie stearsa
                    if (i.next().dict.equals(dictionary)) i.remove(); // sterg definitia
                }
                return true;
            }
        }
        System.out.println("Word/definition doesn't exist!");
        return false;
    }

    String translateWordToEn(String word){
        Word w = findWord(word); // caut cuvantul si returnez traducerea lui in engleza
        return w == null ? null : w.word_en;
    }

    String translateWordFromEn(String word_en) {
        for (Word w : words) { // caut cuvantul in engleza si returnez traducerea lui
            if (word_en.equals(w.word_en)) return w.word;
        }
        return null;
    }

    ArrayList<String> translateWordFromEnOptions(String word_en) {
        ArrayList<String> synonyms = new ArrayList<>();
        for (Word w : words) { // caut cuvantul in engleza si returnez traducerea lui
            if (word_en.equals(w.word_en)) {
                synonyms.add(w.word);
                for (ListIterator<Definition> i = w.definitions.listIterator(); i.hasNext();) { //caut dictionarul de sinonime si le returnez
                    Definition def = i.next();
                    if (def.dictType.equals("synonyms")) {
                        synonyms.addAll(Arrays.asList(def.text));
                    }
                }
            }
        }
        return synonyms;
    }

    ArrayList<Definition> getDefinitionsForWord(String word){
        ArrayList<Definition> definitions = new ArrayList<>();
        for( Word w : words) { //iterez prin lista de cuvinte
            if (word.equals(w.word)) { //caut cuvantul de la care trebuie afisate definitile
                for (ListIterator<Definition> i = w.definitions.listIterator(); i.hasNext();) { //merg prin lista de dictionare si le aranjez
                    Definition def = i.next();
                    definitions.addAll(Arrays.asList(def));
                    Collections.sort(definitions, new SortDef());
                }
            }
        }
        return definitions;
    }

    void exportDictionary(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<Definition> definitions = new ArrayList<>();
        Collections.sort(words, new SortWord()); //aranjez cuvintele in ordine alfabetica
        for( Word w : words) { //iterez prin lista de cuvinte
            for (ListIterator<Definition> i = w.definitions.listIterator(); i.hasNext();) { //merg prin lista de dictionare si le aranjez
                Definition def = i.next();
                definitions.addAll(Arrays.asList(def));
                Collections.sort(definitions, new SortDef());
                w.definitions = definitions;
            }
            definitions = new ArrayList<>();
        }
        try { //scriu in fisierul 'out' dictionarul
            FileWriter myWriter = new FileWriter("out.json");
            myWriter.write(gson.toJson(words));
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
