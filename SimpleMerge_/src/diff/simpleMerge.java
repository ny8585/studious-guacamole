package diff;

import diff.diff_match_patch;
import java.util.LinkedList;

public class simpleMerge {
	public static void main(String[] args) {
		diff_match_patch diffMatchPatch = new diff_match_patch();
		LinkedList<diff_match_patch.Diff> linkDiff = new LinkedList<diff_match_patch.Diff>();

		String str1 = "나는 한성희 입니다.b\nk\n\n\naa";
		String str2 = "나는 주은미 입니다.b\nkaa";

		if (str1 != null & str2 != null) {
			linkDiff = diffMatchPatch.diff_main(str1, str2);
			diffMatchPatch.diff_cleanupEfficiency(linkDiff);
		}
		String delete = "", insert = "";
		int deleteNum = -1, insertNum = -1;
		int deleteCount = 0, insertCount = 0;
		for (int i = 0; i < linkDiff.size() - 2; i++) {
			delete = "";
			insert = "";
			deleteNum = -1;
			insertNum = -1;
			deleteCount = 0;
			insertCount = 0;
			if (linkDiff.get(i).operation == diff_match_patch.Operation.EQUAL) {
				if (linkDiff.get(i + 1).operation == diff_match_patch.Operation.DELETE) {
					if (linkDiff.get(i + 2).operation == diff_match_patch.Operation.INSERT) {
						// 맞는게 있음
						delete = linkDiff.get(i + 1).text;
						insert = linkDiff.get(i + 2).text;
						while (true) {
							deleteNum = delete.indexOf("\n", deleteNum + 1);
							if (deleteNum != -1) {
								deleteCount++;
							} else
								break;
						}
						while (true) {
							insertNum = delete.indexOf("\n", deleteNum + 1);
							if (insertNum != -1) {
								insertCount++;
							} else
								break;
						}
						if (deleteCount > insertCount) {
							for (int k = 0; k < deleteCount - insertCount; k++) {
								insert = insert + "\n";
							}
							linkDiff.get(i + 2).text = insert;
						}
						if (insertCount < deleteCount) {
							for (int l = 0; l < insertCount - deleteCount; l++) {
								delete = delete + "\n";
							}
							linkDiff.get(i + 1).text = delete;
						}

					} else {
						// delete만 있음
						delete = linkDiff.get(i + 1).text;
						while (true) {
							deleteNum = delete.indexOf("\n", deleteNum + 1);
							if (deleteNum != -1) {
								deleteCount++;
							} else
								break;
						}
						for (int k = 0; k < deleteCount; k++) {
							insert = insert + "\n";
						}
						linkDiff.add(i + 2, new diff_match_patch.Diff(diff_match_patch.Operation.INSERT, insert));
					}

				}
				if (linkDiff.get(i + 1).operation == diff_match_patch.Operation.INSERT) {// insert만
																							// 있음
					insert = linkDiff.get(i + 1).text;
					while (true) {
						deleteNum = insert.indexOf("\n", deleteNum + 1);
						if (insertNum != -1) {
							insertCount++;
						} else
							break;
					}
					for (int k = 0; k < insertCount; k++) {
						delete = delete + "\n";
					}
					linkDiff.add(i + 1, new diff_match_patch.Diff(diff_match_patch.Operation.DELETE, delete));
				}
			}

		}

		for (diff_match_patch.Diff d : linkDiff) {
			if (d.operation == diff_match_patch.Operation.EQUAL || d.operation == diff_match_patch.Operation.DELETE) {
				System.out.print(d.text);
			}
		}
		System.out.println();
		for (diff_match_patch.Diff d : linkDiff) {
			if (d.operation == diff_match_patch.Operation.EQUAL || d.operation == diff_match_patch.Operation.INSERT) {
				System.out.print(d.text);
			}
		}

	}
}
