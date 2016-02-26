package com.xbirder.bike.hummingbird.setting;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NetUtil {

	// һ����˵��һ�����һ��UUID�Ļ�����ɿ��ܶ࣬����Ͳ����������
	// ����һ����˵�ϴ��ļ������BASE64���б��룬��ֻҪ��BASE64���õķ�žͿ��Ա�֤����ͻ�ˡ�
	// �������ϴ��������ļ�ʱ�����кܿ�����\r��\n֮��Ŀ����ַ���ʱ�����ܳ������λ������������⣬���Ա�����б��롣 
	public static final String BOUNDARY = "--my_boundary--";

	/**
	 * ��ͨ�ַ����
	 * @param textParams
	 * @param ds
	 * @throws Exception
	 */
	public static void writeStringParams(Map<String, String> textParams,
			DataOutputStream ds) throws Exception {
		Set<String> keySet = textParams.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			String value = textParams.get(name);
			ds.writeBytes("--" + BOUNDARY + "\r\n");
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
			ds.writeBytes("\r\n");
			value = value + "\r\n";
			ds.write(value.getBytes());

		}
	}

	/**
	 * �ļ����
	 * @param fileparams
	 * @param ds
	 * @throws Exception
	 */
	public static void writeFileParams(Map<String, File> fileparams, 
			DataOutputStream ds) throws Exception {
		Set<String> keySet = fileparams.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			File value = fileparams.get(name);
			ds.writeBytes("--" + BOUNDARY + "\r\n");
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
					+ URLEncoder.encode(value.getName(), "UTF-8") + "\"\r\n");
			ds.writeBytes("Content-Type:application/octet-stream \r\n");
			ds.writeBytes("\r\n");
			ds.write(getBytes(value));
			ds.writeBytes("\r\n");
		}
	}

	// ���ļ�ת�����ֽ�����
	private static byte[] getBytes(File f) throws Exception {
		FileInputStream in = new FileInputStream(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = in.read(b)) != -1) {
			out.write(b, 0, n);
		}
		in.close();
		return out.toByteArray();
	}

	/**
	 * ��ӽ�β���
	 * @param ds
	 * @throws Exception
	 */
	public static void paramsEnd(DataOutputStream ds) throws Exception {
		ds.writeBytes("--" + BOUNDARY + "--" + "\r\n");
		ds.writeBytes("\r\n");
	}

	public static String readString(InputStream is) {
		return new String(readBytes(is));
	}

	public static byte[] readBytes(InputStream is) {
		try {
			byte[] buffer = new byte[1024];
			int len = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.close();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
