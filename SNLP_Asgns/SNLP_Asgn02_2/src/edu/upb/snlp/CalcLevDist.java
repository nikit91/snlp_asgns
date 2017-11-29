package edu.upb.snlp;

public class CalcLevDist {

	public static void main(String[] args) {
		String str1 = args[0];
		String str2 = args[1];
		// Calculate Distance
		CalcLevDist dist = new CalcLevDist();
		System.out.println(dist.calculateDistance(str1, str2));
	}

	private int calculateDistance(String str1, String str2) {
		int dist=0;
		int len1 = str1.length();
		int len2 = str2.length();
		//Create the distance matrix
		int[][] distMat = new int[len1+1][len2+1];
		//Initialize the matrix with default values
		for(int i=0;i<=len1;i++)
			distMat[i][0] = i;
		for(int j=0;j<=len2;j++)
			distMat[0][j] = j;
		//Calculate and fill the matrix
		for(int i=1;i<=len1;i++) {
			for(int j=1;j<=len2;j++) {
				if(str1.charAt(i-1)==str2.charAt(j-1)) {
					distMat[i][j] = min(distMat[i-1][j]+1, distMat[i][j-1]+1, distMat[i-1][j-1]);
				}
				else {
					distMat[i][j] = min(distMat[i-1][j]+1, distMat[i][j-1]+1, distMat[i-1][j-1]+1);
				}
			}
		}
		//Read and return distance
		dist = distMat[len1][len2];
		return dist;
	}

	private int min(int op1, int op2, int op3) {
		int l = Math.min(op1, op2);
		return Math.min(l, op3);
	}

}
