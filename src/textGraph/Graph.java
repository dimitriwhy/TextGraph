package textGraph;
import java.util.*;
public class Graph {
	/** n is the number of nodes in the graph */
	public int n;
	/** map is the adjacent matrix for graph */
	private int [][] map;
	/** names is the word for a specific node */
	private String[] names;
	/** nameMap is the map between words to indexes */
	private Map<String, Integer> nameMap = new HashMap<String, Integer>();
	/**
	 * @param input_n number of nodes in the graph
	 */
	public Graph(int input_n) {
		n = input_n;
		map = new int[n][n];
		names = new String[n];
	}
	/**
	 * naming a node of index u a specific string s.
	 * @param u the index of node in the graph
	 * @param s the name for the index
	 */
	public void namingNode(int u, String s) {
		//
		names[u] = s;
		nameMap.put(s, u);
	}
	/**
	 * add weight d to arc (u,v)
	 * @param u head of the arc
	 * @param v tail of the arc
	 * @param d weight added to the graph
	 */
	public void addEdge(int u, int v, int d) {
		map[u][v] += d;
	}
	/**
	 * give the weight of arc (u,v). 0 means there's no such arc.
	 * @param u head of the arc
	 * @param v tail of the arc
	 * @return weight of the arc
	 */
	public int queryWeight(int u, int v) {
		return map[u][v];
	}
	/**
	 * return the index of word s.
	 * @param s word
	 * @return index of word s
	 */
	public int getIndex(String s) {
		return nameMap.get(s);
	}
	/**
	 * return the name of index u
	 * @param u index
	 * @return word of index u
	 */
	public String getName(int u) {
		return names[u];
	}
	/**
	 * get a list of indexes of bridge words from word1 to word2.
	 * @param word1 word1
	 * @param word2 word2
	 * @return a list of indexes of bridge words from word1 to word2
	 */
	public ArrayList<Integer> getBridges(String word1, String word2){
		int u = getIndex(word1), v = getIndex(word2);
		ArrayList<Integer> bridgeList = new ArrayList<Integer>();
		for (int x = 0; x < n; x++) {
			if (x != u && x != v) {
				if (queryWeight(u, x) > 0 && queryWeight(x, v) > 0) {
					bridgeList.add(x);
				}
			}
		}
		return bridgeList;
	}
	/**
	 * calculate the SSSP DAG using Dijkstra's Algorithm.
	 * @param u single source.
	 * @return SSSP DAG adjacent matrix.
	 */
	int[][] _getShortestPath(int u) {
		int[] distance = new int[n];
		int[] visit = new int[n];
		for (int i = 0; i < n; i++)
			distance[i] = 1 << 29;
		int [][] postNode = new int[n][n];
		distance[u] = 0;
		for (int i = 1; i < n; i++) {
			int minimalDistance = 1 << 29;
			int minimalNode = 0;
			for (int v = 0; v < n; v++)
				if (distance[v] < minimalDistance && visit[minimalNode] == 0) {
					minimalDistance = distance[v];
					minimalNode = u;
				}
			visit[minimalNode] = 1;
			for (int v = 0; v < n; v++) {
				if (v != minimalNode && map[minimalNode][v] != 0) {
					if (distance[minimalNode] + map[minimalNode][v] < distance[v]) {
						distance[v] = distance[minimalNode] + map[minimalNode][v];
						for (int w = 0; w < n; w++)
							postNode[w][v] = 0;
						postNode[minimalNode][v] = 1;
					}else if (distance[minimalNode] + map[minimalNode][v] == distance[v]) {
						postNode[minimalNode][v] = 1;
					}
				}
			}
		}
		for (int i = 0; i < n; i++)
			postNode[i][i] = distance[i];
		return postNode;
	}
	void findPaths(int u, int v, ArrayList<String> shortestPaths, ArrayList<Integer> alreadyVisited, int[][] postNode) {
		alreadyVisited.add(u);
		if (u == v) {
			String path = Integer.toString(postNode[v][v]) + " ";
			int length = alreadyVisited.size();
			for (int i = 0; i < length; i++)
				path += getName(alreadyVisited.get(i)) + " ";
			shortestPaths.add(path);
			return;
		}
		for (int i = 0; i < n; i++)
			if (i != u && postNode[u][i] > 0)
				findPaths(i, v, shortestPaths, alreadyVisited, postNode);
	}
	void getShortestPathBetweenTwoNode(int u, int v, int[][] postNode) {
		Queue<Integer> Q = new LinkedList<Integer>();
		int[] visit = new int[n];
		visit[v] = 1;
		Q.offer(v);
		while (!Q.isEmpty()) {
			int w = Q.poll();
			for (int x = 0; x < n; x++)
				if (postNode[x][w]  != 0 && x != w) {
					postNode[x][w] = 2;
					if (visit[x] == 0) {
						visit[x] = 1;
						Q.offer(x);
					}
				}
		}
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (i != j) {
					if (postNode[i][j] == 1) postNode[i][j] = 0;
					if (postNode[i][j] == 2) postNode[i][j] = 1;
				}		
	}
	public ArrayList<String> getShortestPathList(int u, int v){
		int[][] postNode = _getShortestPath(u);
		getShortestPathBetweenTwoNode(u, v, postNode);
		ArrayList<String> shortestPaths = new ArrayList<String>();
		ArrayList<Integer> alreadyVisited = new ArrayList<Integer>();
		findPaths(u,v,shortestPaths, alreadyVisited, postNode);
		return shortestPaths;
	}
	public ArrayList<Integer> getShortestPath(int u, int v){
		int[][] postNode = _getShortestPath(u);
		getShortestPathBetweenTwoNode(u, v, postNode);
		ArrayList<Integer> path = new ArrayList<Integer>();
		while (u != v) {
			path.add(u);
			for (int i = 0; i < n; i++)
				if (i != u && postNode[u][i] > 0)
					u = i;
		}
		return path;
	}
}
