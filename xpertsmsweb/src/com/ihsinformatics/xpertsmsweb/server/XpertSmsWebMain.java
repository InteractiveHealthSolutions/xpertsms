package com.ihsinformatics.xpertsmsweb.server;

import java.io.IOException;

import javax.management.InstanceAlreadyExistsException;

public class XpertSmsWebMain {
    public static void main(String[] args) {
	try {
	    SmsTarseel.Instantiate();
	} catch (InstanceAlreadyExistsException e) {
	    e.printStackTrace();
	} catch (IOException e) {
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
