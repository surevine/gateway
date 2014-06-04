package com.surevine.community.gateway.hooks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.surevine.community.gateway.properties.CMISProperties;

public class TestCMISUploadImportTransportHook {

	private static File OUTPUT_DIR=new File("/tmp/alfresco_gateway_test");
	static {
		String outputDirFromProps=System.getProperty(TestCMISUploadImportTransportHook.class.getName().concat(".OUTPUT_DIR"));
		if (outputDirFromProps!=null) {
			OUTPUT_DIR=new File(outputDirFromProps);
		}
	}
	
	@BeforeClass
	public static void init() {
		if (!(OUTPUT_DIR.exists() || OUTPUT_DIR.mkdirs())) {
			throw new RuntimeException("Couldn't instaniate output directory: "+OUTPUT_DIR);
		}
	}
	
	protected File createTempDir(String testName) {
		File rV = new File(OUTPUT_DIR, testName + "_"+(new Date().getTime()/1000l));
		rV.mkdirs();
		return rV;
	}	
	
	protected File createFileToTransfer(String data, String testName) throws IOException {
		File parent = createTempDir(testName);
		File file = new File(parent, testName);
		file.createNewFile();
		OutputStream os = new FileOutputStream(file);
		os.write(data.getBytes(Charset.forName("UTF-8")));
		os.flush();
		os.close();
		return file;
	}
	
	protected Map<String, String> getTestProperties() {
		Map<String,String> rV = new HashMap<String, String>();
		rV.put("foo", "bar");
		return rV;
	}
	
	@Test
	public void testBasicTransfer() throws IOException {
		File[] files = new File[1];
		files[0]=createFileToTransfer("Sometimes I wish I had a cat. All I’ve ever had was a head, and that the seagulls took", "testBasicTransfer");
		new CMISUploadImportTransportHook().deployMainArtifact(files, getTestProperties());
	}
}
