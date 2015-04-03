/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.xpertsms.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TestClient {
	
	public static void main(String[] args) {
		Socket socket = null;
		
		try {
			socket = new Socket("localhost", 12221);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("connected to localhost:12222");
		PrintWriter out = null;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("established output stream");
		
		out.println("H|@^\\|URM-Gq3kPdSA-01||CEPHEID^GeneXpert^2.1|||||LIS-1||P|1394-97|20100311153337");
		out.println("P|1|||");
		out.println("O|1|100303FIIFV2001HM+E4||^^^HemosIL|R|20100303114352|||||||||ORH||||||||||F");
		out.println("R|1|^HemosIL^^F2^Xpert HemosIL FII & FV^1^FII^|FII HOMOZYGOUS^|||||F||samir alsanady|20100303114352|20100303121429|Sheth-Opt745^704185^602023^26874780^02001^20110912");
		out.println("R|2|^HemosIL^^F2^^^FII 20210G^|NEG^|||");
		out.println("R|3|^HemosIL^^F2^^^FII 20210G^Ct|^0|||");
		out.println("R|4|^HemosIL^^F2^^^FII 20210G^EndPt|^10.0|||");
		out.println("R|5|^HemosIL^^F2^^^FII 20210A^|POS^|||");
		out.println("R|6|^HemosIL^^F2^^^FII 20210A^Ct|^24.5|||");
		out.println("R|7|^HemosIL^^F2^^^FII 20210A^EndPt|^455.0|||");
		out.println("R|8|^HemosIL^^F5^Xpert HemosIL FII & FV^1^FV^|FV HOMOZYGOUS^|||||F||samir alsanady|20100303114352|20100303121429|Sheth-Opt745^704185^602023^26874780^02001^20110912");
		out.println("R|9|^HemosIL^^F5^^^FV 1691G^|NEG^|||");
		out.println("R|10|^HemosIL^^F5^^^FV 1691G^Ct|^0|||");
		out.println("R|11|^HemosIL^^F5^^^FV 1691G^EndPt|^0.0|||");
		out.println("R|12|^HemosIL^^F5^^^FV 1691A^|POS^|||");
		out.println("R|13|^HemosIL^^F5^^^FV 1691A^Ct|^25.5|||");
		out.println("R|14|^HemosIL^^F5^^^FV 1691A^EndPt|^281.0|||");
		out.println("L|1|N");
		System.out.println("Data written");
		try {
			//socket.setSoLinger(true, 60);
			out.close();
			socket.close();
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
