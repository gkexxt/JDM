/*
 * The MIT License
 *
 * Copyright 2019 G.K #gkexxt@outlook.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package javadm.app;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
import java.util.Random;

public class TestEnglishFour {
	
	/**
	 * 17/06/1 	
	 * 17/06/2 	
	 * 16/12/1 	
	 * 16/12/2 	
	 * 16/06/1 	
	 * 16/06/2
	 * 15/12/1
	 * 15/12/2
	 */
//  ÌýÁ¦
	public String[][] daan=new String[][]{
		{"D","A","B","A","D","B","C","B","B","C","A","D","A","D","C","A","B","C","A","D","C","C","B","D","A"},
		{"C","A","C","B","D","D","B","C","A","B","D","B","A","D","C","A","D","B","A","B","D","A","C","C","B"},
		{"A","B","B","C","D","C","A","D","B","D","C","D","A","C","B","D","C","B","C","A","D","B","B","A","A"},
		{"B","D","C","D","C","A","D","B","C","A","B","A","B","C","A","D","A","C","A","C","B","D","B","D","C"},
		{"C","A","B","A","D","B","C","D","B","A","C","B","A","C","D","A","C","D","B","D","C","B","D","A","C"},
		{"C","C","B","D","D","A","B","A","D","C","A","B","C","D","C","D","B","A","A","D","B","C","A","B","D"}
	};
	
	//ÔÄ¶Á
//	public String[][] daan=new String[][]{
//		{"D","B","C","B","D","A","D","C","A","B"},
//		{"A","B","C","A","D","B","D","C","B","A"},
//		{"B","A","C","D","B","D","C","B","B","C"},
//		{"C","A","D","A","B","D","D","B","A","C"},
//		{"C","B","A","D","A","D","B","C","B","D"},
//		{"B","C","D","A","D","C","D","C","B","A"},
//		{"C","D","C","B","A","A","D","C","D","B"},
//		{"D","A","B","A","C","C","B","D","B","A"},
//		{"B","D","C","D","A","C","A","B","D","C"}
//	};
	

	public int problemcount=daan[0].length;
	
	public String[] randdaan=new String[problemcount];
	public String[] randomdaan=new String[problemcount];
	
	int asum,bsum,csum,dsum;

	public TestEnglishFour() {
		
		sumabcd();
		
		for(int k=0;k<5;k++){
			for(int index=0;index<problemcount;index++){
				
				asum=0;
				bsum=0;
				csum=0;
				dsum=0;

				for (int i = 0; i < daan.length; i++) {
					switch (daan[i][index]) {
					case "A":
						asum++;
						break;
					case "B":
						bsum++;
						break;
					case "C":
						csum++;
						break;
					case "D":
						dsum++;
						break;
					}
				}
				
				randdaan[index]=Max(asum,bsum,csum,dsum);
				
			}
			
			for(int i=0;i<randdaan.length;i++){
				System.out.print(randdaan[i]);
			}
			System.out.println();
			for(int i=0;i<daan.length;i++){
				System.out.print(compare(randdaan, daan[i])+"  ");
			}
			System.out.println();
		}
		
		System.out.println("------------------------");
		
		Random random=new Random();
		for(int k=0;k<10;k++){
			for(int i=0;i<randomdaan.length;i++){
				int rand=random.nextInt(4)+1;
				String str="";
				switch (rand) {
				case 1:
					str="A";
					break;
				case 2:
					str="B";
					break;
				case 3:
					str="C";
					break;
				case 4:
					str="D";
					break;
				}
				randomdaan[i]=str;
				System.out.print(str);
			}
			System.out.println();
			for(int i=0;i<daan.length;i++){
				System.out.print(compare(randomdaan, daan[i])+"  ");
			}
			System.out.println();
		}
		
	}
	
	public void sumabcd(){
		
		for(int i=0;i<daan.length;i++){
			
			asum=0;
			bsum=0;
			csum=0;
			dsum=0;
			
			for(int j=0;j<daan[i].length;j++){
				switch (daan[i][j]) {
				case "A":
					asum++;
					break;
				case "B":
					bsum++;
					break;
				case "C":
					csum++;
					break;
				case "D":
					dsum++;
					break;
				}
			}
			
			System.out.println(i+" - "+asum+" - "+bsum+" - "+csum+" - "+dsum);
		}
		
	}
	
	public String Max(int a,int b,int c,int d){
//		System.out.println(a+" - "+b+" - "+c+" - "+d);
		int max=0;
		int t=0;
		String str="";
		if(a>=max){
			max=a;
			t=1;
			str="A";
		}
		if(b>=max){
			max=b;
			t=2;
			str="B";
		}
		if(c>=max){
			max=c;
			t=3;
			str="C";
		}
		if(d>=max){
			max=d;
			t=4;
			str="D";
		}
		
		char s[]=new char[4];
		int equalcount=0;
		if(a==max){
			s[equalcount]='A';
			equalcount++;
		}
		if(b==max){
			s[equalcount]='B';
			equalcount++;
		}
		if(c==max){
			s[equalcount]='C';
			equalcount++;
		}
		if(d==max){
			s[equalcount]='D';
			equalcount++;
		}
		
		Random random=new Random();
		str=s[random.nextInt(equalcount)]+"";
		
		return str;
	}
	
	public int compare(String[] a,String[] b){
		
		int sum=0;
		
		for(int i=0;i<a.length;i++){
			if(a[i].equals(b[i])){
				sum++;
			}
		}
		
		return sum;
	}
	
	public static void main(String[] args) {
		TestEnglishFour tef=new TestEnglishFour();
	}
	
}
