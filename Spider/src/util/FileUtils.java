package util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class FileUtils {
	public static byte[] readInputStreamToByteArray(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			int len;
			byte[] buf = new byte[512];
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.flush();
			return outputStream.toByteArray();
		} finally {
			outputStream.close();
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	 /**
     * 判断某个文件是否属于某些格式文件
     *
     * @param fileName 文件地址
     * @param exts 有效的文件扩展名，如"jpg;.jpeg;.png"
     */
    public static Boolean isValidFiles(String fileName, String exts) {
        if (StringUtil.isNullOrEmpty(fileName)) {
            return false;
        }

        String ext = getFileExtensionName(fileName);
        if (!StringUtil.isNullOrEmpty(ext)) {
            String[] extArray = exts.split(";");
            for (String e : extArray) {
                if (ext.equalsIgnoreCase(e)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
    
    /**
     * 获取文件的扩展名
     *
     * @param fileName 文件名，可带路径
     *
     * @return 返回带文件扩展名，如"jpg"，如果没有扩展名则返回空字符值
     */
    public static String getFileExtensionName(String fileName) {
        if (StringUtil.isNullOrEmpty(fileName)) {
            return "";
        }
        int p = fileName.lastIndexOf('.');
        if (p != -1) {
            return fileName.substring(p+1);
        }
        return "";
    }
    
	/**
	 * 获取URL文件名
	 */
	public static String getUrlFileName(String url) {

		// 去除"?"后面的参数
		String fileNameArr[] = url.split("\\?");
		if (fileNameArr != null && fileNameArr.length > 0) {
			// 获取"/"后的文件名
			return fileNameArr[0]
					.substring(fileNameArr[0].lastIndexOf('/') + 1);
		} else {
			// 获取"/"后的文件名
			return url.substring(url.lastIndexOf('/') + 1);
		}
	}

	public static String readInputStreamToString(InputStream inputStream,
			String charset) throws IOException {
		return new String(readInputStreamToByteArray(inputStream), charset);
	}

	public static boolean isWriting(File file) {
		return file.lastModified() / 1000 >= System.currentTimeMillis() / 1000;
	}

	/**
	 * 功能描述：根据路径名创建目录
	 * 
	 * @param filePath
	 *            路径名
	 * @return 当创建目录成功后，返回true,否则返回false.
	 */
	public static boolean createDirectory(String filePath) {
		boolean isDone = false;
		File file = new File(filePath);
		if (file.exists())
			return true;
		isDone = file.mkdirs();
		return isDone;
	}

	/**
	 * 转换文件名
	 * @param filePath
	 * @return
	 */
	public static String parseFilePath(String filePath){
		
		File file = new File(filePath);
		String fileName = getUrlFileName(file.getName());
		String extName = getFileExtensionName(fileName);
		
		if("".equals(fileName) || "/".equals(fileName)){
			fileName = "index.html";
		}else if(StringUtil.isNullOrEmpty(extName)){
			fileName = new java.util.Date().getTime()+".html"; 
			File other = null;
			if(StringUtil.isNullOrEmpty(file.getParent())){
				other = new File("others");
			}else{
			    other = new File(file.getParent().concat(File.separator).concat("others"));
			}
			if(!other.exists()){
				other.mkdirs();
			}
			fileName = "others".concat(File.separator).concat(new java.util.Date().getTime()+".html"); 
		}else{
			fileName = URLEncoder.encode(fileName); 
		}
		String path = "";
		if(StringUtil.isNullOrEmpty(file.getParent())){
			path = fileName;
		}else{
			path = file.getParent().concat(File.separator).concat(fileName);
		}
		 		
		return path;
	}
	/**
	 * 输出String到文件
	 */
	public static void writeFile(String str, String outFile, String charSet) {
		byte[] buff = new byte[] {};
		FileOutputStream out = null;
		try {
			buff = str.getBytes(charSet);
			File file = new File(outFile);
			if (file.exists()) {
				file.delete();
			}
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			} 
			   
			out = new FileOutputStream(outFile);
			out.write(buff, 0, buff.length);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					out = null;
				}
			}
		}
	}

	/**
	 * 
	 * @param urlStr
	 * @return
	 */
	public static String readFromUrl(String urlStr, String charSet) {

		URL url = null;
		String readString; 
		
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(30000);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),charSet));   
			while ((readString = br.readLine()) != null) {
				sb.append(readString);
			}  
		} catch (MalformedURLException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO异常：" + e);
		}
		return sb.toString();
	}

}
