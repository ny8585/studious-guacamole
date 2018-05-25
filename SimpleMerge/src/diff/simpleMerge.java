package diff;
import diff.diff_match_patch;
import java.util.LinkedList;

public class simpleMerge {
	public static void main(String[] args){
	diff_match_patch diffMatchPatch = new diff_match_patch();
	LinkedList<diff_match_patch.Diff> linkDiff = new LinkedList<diff_match_patch.Diff>();
	 
	String str1 = "³ª´Â ¹äÀ» ¸Ô´Â´Ù.\n³ª´Â »§À» ¸Ô¾ú´Âµ¥.\n¹è°¡ °íÇÁ´Ù.";
	String str2 = "³ª´Â »§À» ¸Ô°í ¹äÀ» ¸Ô¾ú´Âµ¥ °úÀÚµµ.\n¹äÀ» ¸Ô¾îµµ\n¹è°¡ °íÇÁ´Ù.";
	 
	 
	if(str1 !=null & str2 != null) {
	    linkDiff = diffMatchPatch.diff_main(str1, str2);
	    diffMatchPatch.diff_cleanupEfficiency(linkDiff);
	}
	for(diff_match_patch.Diff d: linkDiff){
		System.out.print(d.text+" "+d.operation);
		System.out.println();
		System.out.println();
	}
//	for(diff_match_patch.Diff d: linkDiff){
//    	if(d.operation != diff.diff_match_patch.Operation.INSERT){
//    		System.out.print(d.text);
//    	}
//    }
//	System.out.println();
//	for(diff_match_patch.Diff d: linkDiff){
//		if(d.operation == diff.diff_match_patch.Operation.INSERT||d.operation == diff.diff_match_patch.Operation.EQUAL){
//    		System.out.print(d.text);
//    	}
//	}
//	System.out.println();
//	System.out.println();
//	for(diff_match_patch.Diff d: linkDiff){
//		if(d.operation == diff.diff_match_patch.Operation.DELETE){
//    		System.out.print(d.text);
//    	}
//    }
//	System.out.println();
//	for(diff_match_patch.Diff d: linkDiff){
//    	if(d.operation == diff.diff_match_patch.Operation.INSERT){
//    		System.out.print(d.text);
//    	}
//	}
//	System.out.println();
//	System.out.println();
}
}
