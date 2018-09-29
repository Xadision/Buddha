package com.jimi.bude.arm.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cc.darhao.dautils.api.BytesParser;
import cc.darhao.dautils.api.StringUtil;

public class Stest {

	private static short controllId = -10;
	@Test
	public void test() {
		String a = "201808091020";
		System.err.println(a.getBytes().length);
		String ip = BytesParser.parseBytesToHexString((BytesParser.parseXRadixStringToBytes("182 168 255 2",10)));
		System.out.println(ip);
		System.out.println(parseBytesToXRadixString(BytesParser.parseHexStringToBytes(ip), 10));
		for (int i = 0; i < 20; i++) {
			System.out.println(genControllId());
		}
		
	}
	
public static List<Byte> parseXRadixStringToBytes(String text, int radix) {
		
		List<Byte> bytes = new ArrayList<Byte>();
		for (String byteString : text.split(" ")) {
			int i = Integer.valueOf(byteString, radix);
			bytes.add((byte) i);
		}
		return bytes;
	}

private synchronized static Short genControllId() {
	controllId %= 65536;
	controllId++;
	return controllId;
}
public static String parseBytesToXRadixString(List<Byte> bytes, int radix) {
	StringBuffer sb = new StringBuffer();
	for (Byte b : bytes) {
		switch (radix) {
		case 16:
			String hexString = Integer.toHexString(b).toUpperCase();
			hexString = StringUtil.fixLength(hexString, 2);
			sb.append(hexString);
			break;
		case 10:
			sb.append(Integer.toString(Byte.toUnsignedInt(b)));
			break;
		case 2:
			String binString = Integer.toBinaryString(b);
			binString = StringUtil.fixLength(binString, 8);
			sb.append(binString);
			break;
		default:
			break;
		}
		sb.append(" ");
	}
	return sb.toString().trim();
}
}
