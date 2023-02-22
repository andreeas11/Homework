import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

class MyScanner {
    BufferedReader br;
    StringTokenizer st;

    public MyScanner() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    String next() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }

    int nextInt() {
        return Integer.parseInt(next());
    }

    long nextLong() {
        return Long.parseLong(next());
    }

    double nextDouble() {
        return Double.parseDouble(next());
    }

    String nextLine(){
        String str = "";
        try {
            str = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}

public class Solution {

    static int getResult(Integer[] v, double prev_left, double prev_right) {
        for(int i = 0; i < v.length; i++) {
            prev_right = prev_right - Math.log(v[i]);
            prev_left = Math.log(v[i]) + prev_left;
            if(prev_left > prev_right) return i + 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        MyScanner scanner = new MyScanner();
        int n = scanner.nextInt();
        Integer[] v = new Integer[n];
        for(int i = 0; i < n; i++)
            v[i] = scanner.nextInt();
        Arrays.sort(v, Collections.reverseOrder());

        double prev_right = 0;
        double prev_left = 0;
        for (int j : v) prev_right += Math.log(j);

        System.out.println(getResult(v, prev_left, prev_right));
    }
}
