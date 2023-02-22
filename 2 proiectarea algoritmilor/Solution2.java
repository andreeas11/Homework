import java.io.*;
import java.util.*;

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

class Graph {
    int N;
    LinkedList<Integer> adj[];

    Graph(int n) {
        N = n;
        adj = new LinkedList[n];
        for (int i = 0; i < n; ++i)
            adj[i] = new LinkedList();
    }

    void muchie(int v, int w) {
        adj[v].add(w);
    }

    LinkedList<Integer> bfs(int s) {
        boolean visited[] = new boolean[N];
        LinkedList<Integer> queue = new LinkedList<Integer>(); //coada pt bfs
        LinkedList<Integer> rez = new LinkedList<Integer>();

        visited[s] = true; //vizitam
        queue.add(s); //se adauga la lista

        while (queue.size() != 0) {
            s = queue.poll();
            rez.add(s);

            Iterator<Integer> i = adj[s].listIterator();
            while (i.hasNext()) {
                int n = i.next();
                if (!visited[n]) {
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }
        return rez;
    }
}


public class Solution2 {

    public static void main(String args[]) {
        MyScanner scanner = new MyScanner();
        int n = scanner.nextInt(); //nr total teme
        int x = scanner.nextInt(); //nr teme obligatorii
        LinkedList<Integer> rez = new LinkedList<>();
        int []obligatorii = new int[x];
        Graph g = new Graph(n);

        for(int i = 0; i < x; i++) obligatorii[i] = scanner.nextInt() - 1;
        for(int i = 0; i < n; i++) {
            int scan = scanner.nextInt();
            if(scan != 0) {
                for(int j = 0; j < scan; j++) {
                    int a = scanner.nextInt() - 1;
//                    System.out.println(i + " " + a);
                    g.muchie(i, a);
                }
            }
        }

        for(int i = 0; i < x; i++) {
            rez.addAll(g.bfs(obligatorii[i]));
        }
        Collections.sort(rez);
        LinkedHashSet<Integer> hash = new LinkedHashSet<Integer>(rez);
        LinkedHashSet<Integer> hashTotal = new LinkedHashSet<Integer>();
        for(int i = 1; i <= n; i++) hashTotal.add(i);
        if (hashTotal.size() == hash.size()) {
            System.out.println("-1");
            return;
        }
        System.out.println(hash.size());
        for (Integer j : hash) System.out.print(j+1 + " ");
    }
}
