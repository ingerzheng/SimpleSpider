/**
 * @date 2017年3月17日
 * @version 1.0
 */
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pojo.Link;
import db.DbUtil;

public class AssertLayering {

	//db util
	private static DbUtil dbUtil = DbUtil.getInstance();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			
			List<Link> res = new ArrayList<Link>();
			System.out.print("Input UpLayerId:");
			int upLayerId = sc.nextInt();
			res = dbUtil.getLayerById(upLayerId);
			
			if(res == null) {
				System.out.println("no result!");
				continue;
			}
			for(Link link : res) {
				System.out.println(link.toString());
			}
		}

	}

}
