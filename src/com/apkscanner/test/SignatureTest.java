package com.apkscanner.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.apkscanner.core.signer.Signature;
import com.apkscanner.core.signer.SignatureReport;
import com.apkscanner.util.Log;
import com.apkscanner.util.ZipFileUtil;

import sun.security.pkcs.PKCS7;

public class SignatureTest {
	static Signature[] mSignatures;
	
	public static void main(String[] args) throws Exception {

		long time = System.currentTimeMillis(); 
		time = 1508732155326L;

		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");

		String str = dayTime.format(new Date(time));

		System.out.println(str);


		
		
	}
	

}
