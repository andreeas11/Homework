import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Prinel {
	public static final String INPUT_FILE = "prinel.in";
	public static final String OUTPUT_FILE = "prinel.out";
	static int n;
	static int k;

	public static void readInputWriteOutput() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE));
			String firstLine = br.readLine();
			String secondLine = br.readLine();
			String thirdLine = br.readLine();
			String[] splitFirstLine = firstLine.split(" ");
			String[] splitSecondLine = secondLine.split(" ");
			String[] splitThirdLine = thirdLine.split(" ");
			n = Integer.parseInt(splitFirstLine[0]);
			k = Integer.parseInt(splitFirstLine[1]);
			int[] a = new int[n + 1];
			int[] p = new int[n + 1];
			for (int i = 1; i < n + 1; i++) {
				a[i] = Integer.parseInt(splitSecondLine[i - 1]);
				p[i] = Integer.parseInt(splitThirdLine[i - 1]);
			}
			PrintWriter pw = new PrintWriter(OUTPUT_FILE);
			//apelez metoda pentru a afla scorul maxim
			pw.printf("%d\n", getResult(k ,mySteps(a) , p, a.length));
			pw.close();
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	static List<Integer> getDivisors(int x) {
		List<Integer> d = new ArrayList<>();
		for (int i = 1; i <= x / 2; i++) {
			if (x % i == 0) {
				d.add(i);
			}
		}
		d.add(x);
		d.sort(Comparator.reverseOrder());
		return d;
	}

	static int max(int[] a) {
		int max = 0;
		for (int i : a) {
			if (max < i) {
				max = i;
			}
		}
		return max;
	}

	static int[] steps(int[] a) {
		int[] steps = new int[max(a) + 1];
		for (int i = 0; i <= max(a); i++) {
			steps[i] = i - 1;
		}
		steps[0] = 0;
		for (int i = 1; i <= max(a) + 1; i++) {
			for (int j : getDivisors(i)) {
				if (i + j < max(a) + 1) {
					if (steps[i] + 1 < steps[i + j]) {
						steps[i + j] = steps[i] + 1;
					}
				}
			}
		}
		return steps;
	}

	static int[] mySteps(int[] a) {
		int[] steps = steps(a);
		int[] mysteps = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			mysteps[i] = steps[a[i]];
		}
		return mysteps;
	}

	static int getResult(int K, int[] mysteps, int[] scor, int n) {
		int []dp = new int[k + 1];

		for (int i = 2; i < n + 1; i++) {
			for (int w = k; w >= 0; w--) {
				if (mysteps[i - 1] <= w) {
					int sol_aux = dp[w - mysteps[i - 1]] + scor[i - 1];
					dp[w] = Math.max(dp[w], sol_aux);
				}
			}
		}
		return dp[k]; //valoarea scorului maxim
	}

	public static void main(String[] args) {
		readInputWriteOutput();
	}
}
