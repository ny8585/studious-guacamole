import java.lang.String;

public class LCS {
	public void lcs(String a, String b) {
		String[][] solution;
		int[][] lcs_matrix;
		solution = new String[a.length()][];
		for (int i = 0; i < a.length(); i++)
			solution[i] = new String[b.length()];
		lcs_matrix = new int[a.length()][];
		for (int i = 0; i < a.length(); i++) {
			lcs_matrix[i] = new int[b.length()];
		}
		for (int i = 0; i < a.length(); i++) {
			for (int j = 0; j < b.length(); j++) {
				solution[i][j] = "None";
				lcs_matrix[i][j] = 0;
			}
		}
		for (int i = 1; i < a.length(); i++) {
			for (int j = 1; j < b.length(); j++) {
				if (a.charAt(i) == b.charAt(j)) {
					lcs_matrix[i][j] = lcs_matrix[i - 1][j - 1] + 1;
					solution[i][j] = "Diagonal";
				} else {
					lcs_matrix[i][j] = Math.max(lcs_matrix[i][j - 1], lcs_matrix[i - 1][j]);
					if (lcs_matrix[i][j] == lcs_matrix[i - 1][j]) {
						solution[i][j] = "Top-Down";
					} else {
						solution[i][j] = "Left-Right";
					}
				}
			}
		}
		for (int i = 0; i < a.length(); i++) {
			for (int j = 0; j < b.length(); j++) {
				System.out.print(solution[i][j] + " ");
			}
			System.out.println("");
		}
		for (int i = 0; i < a.length(); i++) {
			for (int j = 0; j < b.length(); j++) {
				System.out.print(lcs_matrix[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public static void main(String[] args) {
		LCS test = new LCS();
		test.lcs("ABCDEF", "AEBCFEDEBF");
	}
}