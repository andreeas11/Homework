import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Walsh {
	public static final String INPUT_FILE = "walsh.in";
	public static final String OUTPUT_FILE = "walsh.out";

	static int n;
	static int k;
	static int x;
	static int y;

	private static void readInputWriteOutput() {
		try {
			Scanner sc = new Scanner(new File(INPUT_FILE));
			n = sc.nextInt(); //citesc din input file marimea matricei si nr perechilor
			k = sc.nextInt();
			PrintWriter pw = new PrintWriter(OUTPUT_FILE);
			for (int i = 0; i < k; i++) { //parcurg perechile de coordonate
				x = sc.nextInt();
				y = sc.nextInt();
				pw.printf("%d\n", getResult(x, y, n) ? 1 : 0);//apelez metoda
			}
			sc.close();
			pw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static Boolean getResult(int x, int y, int n) {
		if (n == 1) {
			return Boolean.FALSE; //cazul de baza W1
		}
		int m = n / 2; // mijlocul matricii
		if (x <= m) { // se apeleaza recursiv functia pt cele 4 cazuri
			if (y <= m) {
				return getResult(x, y, m);
			} else {
				return getResult(x, y - m, m);
			}
		} else {
			if (y <= m) {
				return getResult(x - m, y, m);
			} else {
				return !getResult(x - m, y - m, m);
			}
		}
	}
	public static void main(String[] args) {
		readInputWriteOutput();
	}
}
