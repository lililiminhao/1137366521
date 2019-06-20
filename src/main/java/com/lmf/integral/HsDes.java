package com.lmf.integral;


import java.util.ArrayList;
import java.util.List;

/**
 * 加密
 *
 * @author 杭州市市民卡
 * @create 2018-03-23 16:28
 **/
public class HsDes {
  public static final String HUNDSUN_VERSION = "@system  短信平台3.0.2 @version 3.0.2.1 @lastModiDate @describe ";

  /**
   * 加密
   * @param arg
   * @return
   */
  public static String enc(String arg) {
    if ((arg == null) || (arg.length() == 0)) {
      byte[] b = new byte[1];
      arg = new String(new String(b));
    }

    Encryptor enc = new Encryptor("HsAndMoe".getBytes());
   
    
   return byte2hex(enc.encrypt(arg.getBytes()));
   
  }

  public static String dec(String arg) {
    if ((arg == null) || (arg.length() == 0)) {
      return new String();
    }

    Encryptor dec = new Encryptor("HsAndMoe".getBytes());
    byte[] decByte = dec.decrypt(hex2byte(arg));

    return byte2string(decByte);
  }

  public static String byte2string(byte[] decByte) {
    int len = 0;
    for (int i = 0; i < decByte.length; len++) {
      if (decByte[i] == 0) {
        len = i;
        break;
      }
      i++;
    }

    return len == 0 ? new String() : new String(decByte, 0, len);
  }

  public static String byte2hex(byte[] b) {
    String hs = "";
    String stmp = "";

    for (int n = 0; n < b.length; n++) {
      stmp = byteHEX(b[n]);
      hs = hs + stmp;
    }
    return hs;
  }

  public static String byteHEX(byte ib) {
    char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
      'B', 'C', 'D', 'E', 'F' };
    char[] ob = new char[2];
    ob[0] = digit[(ib >>> 4 & 0xF)];
    ob[1] = digit[(ib & 0xF)];
    String s = new String(ob);
    return s;
  }

  public static byte[] hex2byte(String hex) {
    int len = hex.length() / 2;
    byte[] result = new byte[len];
    char[] achar = hex.toCharArray();
    for (int i = 0; i < len; i++) {
      int pos = i * 2;
      result[i] = ((byte)(toByte(achar[pos]) << 4 | toByte(achar[(pos + 1)])));
    }
    return result;
  }

  private static byte toByte(char c) {
    byte b = (byte)"0123456789ABCDEF".indexOf(c);
    return b;
  }

  public static void main(String[] args) {

    List<String> aList=new ArrayList<String>();
    aList.add("smktyxx");
    aList.add("smkds");
    aList.add("smkzhyl");
    aList.add("smkbdxt");
    aList.add("smktyyh");
    aList.add("smkjdxpt");
    aList.add("smkappht");
    aList.add("smkhmzx");
    aList.add("smkkgl");
    aList.add("smkwz");
    aList.add("smkhmhj");
    aList.add("smkhmjf");
    aList.add("smkzjqs");
for(String str : aList) {
  System.out.println(enc(str));
}
//    System.out.println("加密smktyxx:"+enc("smkhmzx")); //此处填入需加密用户名：如smkhmzx
//    System.out.println("解密："+dec("388921FC4C0A3620"));
    
  }
}
