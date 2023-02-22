import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

public class Statistics {
	public static final String INPUT_FILE = "statistics.in";
	public static final String OUTPUT_FILE = "statistics.out";
	static char[] alfabet = {'a','b','c','d','e','f','g','h','i', 'j','k','l','m',
		'n','o','p','q','r','s','t','u','v','w','x','y','z'};
	//pentru a putea sorta perechea formata dintr-un cuvant si 'scorul' acestuia
	public static class Pair implements Comparable<Pair> {
		public int key;
		public String word;

		Pair(String word, int key) {
			this.key = key;
			this.word = word;
		}
		@Override
		public int compareTo(Pair pair) {
			return Integer.compare(this.key, pair.key);
		}
	}

	public static void readInputWriteOutput() {
		try {
			PrintWriter pw = new PrintWriter(OUTPUT_FILE);
			int n;
			BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));
			n = Integer.parseInt(br.readLine()); //citesc din input file nr de cuvinte si cuvintele
			String[] a = new String[n];
			int i = 0;
			String temp;
			while ((temp = br.readLine()) != null) { //pun cuvintele intr-un vector
				a[i] = temp;
				i++;
			}
			//apelez metoda pentru a afla numarul maxim de cuvinte
			pw.printf("%d\n", getResult(n, a));
			pw.close();
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// numara de cate ori apare un caracter intr-un cuvant
	public static int nrOfOccurences(char character, String string) {
		int counter = 0;
		for (int i = 0; i < string.length(); i++) {
			if (character == string.charAt(i)) {
				counter++;
			}
		}
		return counter;
	}

	public static int getResult(int n, String[] a) {
		int maxNrWords = 0;
		int[] v = new int[n]; //se va memora scorul fiecarui cuvant
		for (char c : alfabet) {
			//se va memora un vector din perechea formata de cuvant si scorul lui
			Pair[] pairs = new Pair[n];
			for (int i = 0; i < n; i++) {
				int countChar = nrOfOccurences(c, a[i]);
				//scorul cuvantului = numarul de aparitii al literei cautate - restul literelor
				v[i] = countChar - (a[i].length() - countChar);
				pairs[i] = new Pair(a[i], v[i]);
			}
			//se sorteaza perechile in functie de scor
			Arrays.sort(pairs, Collections.reverseOrder());

			int counter = 0;
			int nrWords = 0;
			for (int i = 0; i < n; i++) { //se parcurg cuvintele ordonate
				counter += pairs[i].key;
				//counter = suma scorurilor cuvintelor parcurse
				//daca nr din litera cautata sunt mai mult de jumatate fata de celalalte=>counter>0
				if (counter > 0) {
					nrWords++; //se incrementeaza nr de cuvinte
					if (i == n - 1 && nrWords > maxNrWords) {
						maxNrWords = nrWords;
					}
				} else { //se gaseste cu nr maxim de cuvinte si se intoarce
					if (nrWords > maxNrWords) {
						maxNrWords = nrWords;
					}
				}
			}
		}
		if (maxNrWords < 1) {
			return -1;
		}
		return maxNrWords;
	}

	public static void main(String[] args) {
		readInputWriteOutput();
	}
}
