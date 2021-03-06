/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.xpertsms.constant;

/**
 * @author ali.habib@irdresearch.org
 */
public class ASTMNetworkConstants {
	
	//	Hexadecimal - Character
	//
	//    | 00 NUL| 01 SOH| 02 STX| 03 ETX| 04 EOT| 05 ENQ| 06 ACK| 07 BEL|
	//    | 08 BS | 09 HT | 0A NL | 0B VT | 0C NP | 0D CR | 0E SO | 0F SI |
	//    | 10 DLE| 11 DC1| 12 DC2| 13 DC3| 14 DC4| 15 NAK| 16 SYN| 17 ETB|
	//    | 18 CAN| 19 EM | 1A SUB| 1B ESC| 1C FS | 1D GS | 1E RS | 1F US |
	//    | 20 SP | 21  ! | 22  " | 23  # | 24  $ | 25  % | 26  & | 27  ' |
	//    | 28  ( | 29  ) | 2A  * | 2B  + | 2C  , | 2D  - | 2E  . | 2F  / |
	//    | 30  0 | 31  1 | 32  2 | 33  3 | 34  4 | 35  5 | 36  6 | 37  7 |
	//    | 38  8 | 39  9 | 3A  : | 3B  ; | 3C  < | 3D  = | 3E  > | 3F  ? |
	//    | 40  @ | 41  A | 42  B | 43  C | 44  D | 45  E | 46  F | 47  G |
	//    | 48  H | 49  I | 4A  J | 4B  K | 4C  L | 4D  M | 4E  N | 4F  O |
	//    | 50  P | 51  Q | 52  R | 53  S | 54  T | 55  U | 56  V | 57  W |
	//    | 58  X | 59  Y | 5A  Z | 5B  [ | 5C  \ | 5D  ] | 5E  ^ | 5F  _ |
	//    | 60  ` | 61  a | 62  b | 63  c | 64  d | 65  e | 66  f | 67  g |
	//    | 68  h | 69  i | 6A  j | 6B  k | 6C  l | 6D  m | 6E  n | 6F  o |
	//    | 70  p | 71  q | 72  r | 73  s | 74  t | 75  u | 76  v | 77  w |
	//    | 78  x | 79  y | 7A  z | 7B  { | 7C  | | 7D  } | 7E  ~ | 7F DEL|
	
	//public static final int RESULT_SERVER_PORT = 12221;
	public static final byte NULL = 0x0000;
	
	public static final byte ENQ = 0x0005;
	
	public static final byte ACK = 0x0006;
	
	public static final byte NAK = 0x0015;
	
	public static final byte STX = 0x0002;
	
	public static final byte ETX = 0x0003;
	
	public static final byte EOT = 0x0004;
	
	public static final byte SOH = 0x0001;
	
	public static final byte ETB = 0x0017;
	
	public static final byte CR = 0x000D;
	
	public static final byte LF = 0x000A;
	
	public static final String webappString = "/tbrmobilewebxpert/tbrmobile.jsp";
	//public static final String smsServerAddress = "202.141.249.109";
	//public static final int smsServerPort = 8080;
}
