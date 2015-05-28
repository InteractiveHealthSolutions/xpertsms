package com.ihsinformatics.xpertsmsweb.server;

public class XpertSmsWebMain {
	public static void main(String[] args) {
		try {
			System.out.println(System.getProperty("user.dir"));
			// SmsTarseel.Instantiate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HELLO!");
	}

	public static int findIndex(String[] array, String str) {
		for (int i = 0; i < array.length; i++)
			if (array[i].equalsIgnoreCase(str))
				return i;
		return -1;
	}
}
