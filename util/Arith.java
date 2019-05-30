package com.hbsi.util;

import java.math.BigDecimal;

public class Arith {

	private static final int DEF_DIV_SCALE = 10;

	/**
	 * * Adding two Double numbers *
	 * 
	 * @param v1 *
	 * @param v2 *
	 * @return Double
	 */
	public static Double add(Double v1, Double v2) {
		BigDecimal b1 = new BigDecimal(v1.toString());
		BigDecimal b2 = new BigDecimal(v2.toString());
		return new Double(b1.add(b2).doubleValue());
	}

	/**
	 * * Subtracting two Double numbers *
	 * 
	 * @param v1 *
	 * @param v2 *
	 * @return Double
	 */
	public static Double sub(Double v1, Double v2) {
		BigDecimal b1 = new BigDecimal(v1.toString());
		BigDecimal b2 = new BigDecimal(v2.toString());
		return new Double(b1.subtract(b2).doubleValue());
	}

	/**
	 * * Multiply two Double numbers *
	 * 
	 * @param v1 *
	 * @param v2 *
	 * @return Double
	 */
	public static Double mul(Double v1, Double v2) {
		BigDecimal b1 = new BigDecimal(v1.toString());
		BigDecimal b2 = new BigDecimal(v2.toString());
		return new Double(b1.multiply(b2).doubleValue());
	}

	/**
	 * * Divide two Double numbers *
	 * 
	 * @param v1 *
	 * @param v2 *
	 * @return Double
	 */
	public static Double div(Double v1, Double v2) {
		BigDecimal b1 = new BigDecimal(v1.toString());
		BigDecimal b2 = new BigDecimal(v2.toString());
		return new Double(b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	/**
	 * * Divide two Double numbers and retain decimals *
	 * 
	 * @param v1 *
	 * @param v2 *
	 * @param scale *
	 * @return Double
	 */
	public static Double div(Double v1, Double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The decimal must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(v1.toString());
		BigDecimal b2 = new BigDecimal(v2.toString());
		return new Double(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	/**
	 * Inspection rules for tensile strength 
	 * 
	 * @param d
	 * @return
	 */
	public static int revision(Double d) {
		int i = 0;
		if(d >= 1000 && d < 10000) {
			String number = String.format("%.5f", d);//Prevent decimals after no value
			String[] strs = number.split("\\.");
			String s = strs[0].toString();// Integer part
			String s0 = strs[1].toString();// decimal part
			// Intercept string
			String ss = s.substring(3, 4);
			String s1 = s0.substring(0, 1);
			String s2 = s0.substring(1, 2);
			String s3 = s0.substring(2, 3);
			// Judge the first decimal
			int x = Integer.parseInt(ss);//last integer digit
			int a = Integer.parseInt(s1);//First decimal
			int b = Integer.parseInt(s2);//Second decimal
			int c = Integer.parseInt(s3);//Third decimal
			if(a>=0 && a<=4) {
				i = Integer.parseInt(s);
			}else if(a>=6 && a<=9) {
				i = Integer.parseInt(s) + 1;
			}else if(a==5) {
				if(b > 0) {//入
					i = Integer.parseInt(s) + 1;
				}else if(b == 0 || c == 0|| s3 == null) {
					if (x % 2 == 0) {
						System.out.println("even");
						i = Integer.parseInt(s);
					}else {
						System.out.println("odd");
						i = Integer.parseInt(s) + 1;
					}
				}
			}
		}else if(d >= 100 && d<1000){
			String number = String.format("%.5f", d);//Prevent decimals after no value
			String[] strs = number.split("\\.");
			String s = strs[0].toString();// Integer part
			String s0 = strs[1].toString();// decimal part
			// Intercept string
			String ss = s.substring(2, 3);
			String s1 = s0.substring(0, 1);
			String s2 = s0.substring(1, 2);
			String s3 = s0.substring(2, 3);
			// Judge the first decimal
			int x = Integer.parseInt(ss);
			int a = Integer.parseInt(s1);
			int b = Integer.parseInt(s2);
			int c = Integer.parseInt(s3);
			if(a>=0 && a<=4) {
				i = Integer.parseInt(s);
			}else if(a>=6 && a<=9) {
				i = Integer.parseInt(s) + 1;
			}else if(a==5) {
				if(b > 0) {
					i = Integer.parseInt(s) + 1;
				}else if(b == 0 || c == 0|| s3 == null) {
					if (x % 2 == 0) {
						System.out.println("even");
						i = Integer.parseInt(s);
					}else {
						System.out.println("odd");
						i = Integer.parseInt(s) + 1;
					}
				}
			}
		}else if(d>=10 && d<100){
			String number = String.format("%.5f", d);
			String[] strs = number.split("\\.");
			String s = strs[0].toString();
			String s0 = strs[1].toString();
			// Intercept string
			String ss = s.substring(1, 2);
			String s1 = s0.substring(0, 1);
			String s2 = s0.substring(1, 2);
			String s3 = s0.substring(2, 3);
			// Judge the first decimal
			int x = Integer.parseInt(ss);
			int a = Integer.parseInt(s1);
			int b = Integer.parseInt(s2);
			int c = Integer.parseInt(s3);
			if(a>=0 && a<=4) {
				i = Integer.parseInt(s);
			}else if(a>=6 && a<=9) {//入
				i = Integer.parseInt(s) + 1;
			}else if(a==5) {
				if(b > 0) {//入
					i = Integer.parseInt(s) + 1;
				}else if(b == 0 || c == 0|| s3 == null) {
					if (x % 2 == 0) {
						System.out.println("even");
						i = Integer.parseInt(s);
					}else {
						System.out.println("odd");
						i = Integer.parseInt(s) + 1;
					}
				}
			}
		}
		return i;
	}
	
	/**
	 * Performing breaking force revision
	 * @param n
	 * @return
	 */
	public static double getValue(double n) {
		if(n<100) {
			/**
			 * Keep one decimal
			 */
			double result=0;
			int x=(int)(n*100);
			int l1=x%10;//Second decimal
			if(l1<5) {
				result=Math.round(n*10)/10.0;
			}else if(l1>5) {
				result=Math.round(n*10)/10.0;
			}else if(l1==5) {//Look at the third decimal
				String s=(n+"00").substring((n+"00").indexOf('.')+3);
				long s0=Long.parseLong(s);
				if(s0!=0) {//Not 0
					int a=Integer.parseInt(s.charAt(0)+"");
					if(a%2==0) {
						result=Math.round((Math.round(n*10)/10.0-0.1)*10)/10.0;
					}else {
						result=Math.round(n*10)/10.0;
					}
				}else {
					int a=((int)n)%10;
					if(a%2==0) {
						result=Math.round((Math.round(n*10)/10.0-0.1)*10)/10.0;
					}else {
						result=Math.round(n*10)/10.0;
					}
				}
			}
			return result;
		}else if(n<1000) {
			/**
			 * Decimal
			 */
			Integer x=(int)(n*10);
			int l1=x%10;
			int result=0;
			if(l1<5&&l1>0) {
				 result=(int)n;
			}else if(l1>5) {
				 result=(int)n+1;
			}else if(l1==5) {
				System.out.println(n);
				/** After the judgment of a decimal, if != 0,
				 * odd numbers enter 1, double numbers are
				 * rounded off; if = 0, odd numbers enter 1,
				 * even numbers are rounded off
				 */
				String s=(n+"0").substring((n+"0").indexOf('.')+2);
				long s0=Long.parseLong(s);
				if(s0!=0) {//If != 0, check the 2nd decimal
					int a=Integer.parseInt(s.charAt(0)+"") ;
					if(a%2==0) {
						result=(int)n;
					}else {
						result=(int)n+1;
					}
				}else {//If = 0, check the previous single digit
					int y=(int)n;
					int l2=y%10;
					if(l2%2==0) {
						result=(int)n;
					}else {
						result=(int)n+1;
					}
				}
			}
			return result;
		}else {
			/**
			 * Single digit rounding to 0
			 */
			int x=(int)n;
			int l1=x%10;
			int result=0;
			if(l1<5) {
				result=x-l1;
			}else if(l1>5){
				result=x-l1+10;
			}else if(l1==5) {
				String s=(n+"0").substring((n+"0").indexOf('.')+1);
				long s0=Long.parseLong(s);
				if(s0!=0) {//If != 0, check the last number discarded; the first decimal
					int a=Integer.parseInt(s.charAt(0)+"") ;
					if(a%2==0) {
						result=x-l1;
					}else {
						result=x-l1+10;
					}
				}else {//If = 0, check the previous 10 digits
					int a=(x%100)/10;//Ten digits
					if(a%2==0) {
						result=x-l1;
					}else {
						result=x-l1+10;
					}
				}
			}
			return result;
		}
	}
	
	

	public static void main(String[] args) {
		double d = 1956.5;
//		double d = 883.51568899989889;
//		System.out.println(getValue(d));
		
		System.out.println(revision(d));
	}

}
