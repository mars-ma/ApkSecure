package dev.mars.apksecure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 将原APK与脱壳DEX合并，输出新的apk
 * 
 * @author ma.xuanwei
 * 
 */
public class Main {
	private static final String DEX_APP_NAME = "dev.mars.secure.ProxyApplication";
	private static final String PASSWORD = "laChineestunlionendormi";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	private static Config config;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String cmd = args[0];
		if (!"b".equals(cmd)) {
			System.out.println("错误的参数:" + cmd);
			return;
		}
		// apk路径
		String apkPath = args[1];
		System.out.println("apkPath:" + apkPath);
		// 反编译目录
		String decompiledDirName = apkPath.split("\\.")[0];
		System.out.println("decompiledDir:" + decompiledDirName);

		// 删除反编译目录
		File decompiledFile = new File(getWorkPath() + "\\" + decompiledDirName);
		if (decompiledFile.exists()) {
			FileUtil.delete(decompiledFile);
			System.out.println("已删除" + decompiledFile.getAbsolutePath());
		}

		// 创建反编译目录
		boolean decompiled = false;
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("正在反编译" + apkPath);

			// 确保apktool.jar放在工作目录下
			SystemCommand.execute("java -jar apktool.jar d " + apkPath);
			System.out.println("反编译耗时 "
					+ (System.currentTimeMillis() - startTime) + " ms");
			System.out.println("反编译结束,生成目录" + decompiledFile.getAbsolutePath());
			decompiled = true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (decompiled) {
			if (alterAndroidMainifest(decompiledFile.getAbsolutePath())) {

				// try {
				// buildAPK(decompiledDir, newPath);
				try {
					SystemCommand.execute("java -jar apktool.jar b "
							+ decompiledFile.getAbsolutePath());
					System.out.println("编译成功");
					String compiledApkPath = System.getProperty("user.dir")
							+ "\\" + decompiledDirName + "\\dist\\"
							+ decompiledDirName + ".apk";
					File compiledApkFile = new File(compiledApkPath);
					if (compiledApkFile.exists()) {
						System.out.println("找到修改Manifest后的apk:"
								+ compiledApkFile.getAbsolutePath());

						// 解压新的apk，并找到classes.dex，将其移动到assets中
						String unzipFilePath = System.getProperty("user.dir")
								+ "\\" + decompiledDirName + "\\dist\\"
								+ decompiledDirName;
						FileUtil.delete(new File(unzipFilePath));
						ZipUtils.upzipFile(compiledApkFile, unzipFilePath);
						System.out.println("解压完成 解压输出:" + unzipFilePath);
						
						File assetsFile = new File(unzipFilePath+"\\assets");
						if(!assetsFile.exists()){
							assetsFile.mkdir();
							System.out.println("生成assets文件夹");
						}
						// 找到素有.dex文件
						List<File> allDexFiles = findAllDexFiles(unzipFilePath);
						String zipFilePath = unzipFilePath
								+ "\\assets\\abc"+System.currentTimeMillis()+".zip";
						// 将所有dex文件压缩进assets下的abc.zip
						ZipUtils.zipFile(zipFilePath, allDexFiles);
						System.out.println("生成压缩文件:" + zipFilePath);
						
						DESUtils desUtils = new DESUtils();
						desUtils.initialize_encryptKey(PASSWORD);
						String outputPath = unzipFilePath + "\\assets\\apksecurefile";
						desUtils.encrypt(zipFilePath, outputPath);
						(new File(zipFilePath)).delete();
						System.out.println("生成加密文件:" + outputPath);
						outputPath.endsWith(suffix)
						// 删除原classes.dex
						for (File f : allDexFiles) {
							f.delete();
						}

						String decladdingDexPath = System
								.getProperty("user.dir") + "\\classes.dex";
						String libsFolderPath = System.getProperty("user.dir")
								+ "\\secure-lib\\";
						String destLibsFolderPath = new File(unzipFilePath
								+ "\\lib\\").getAbsolutePath();
						String destDexPath = unzipFilePath+"\\classes.dex";
						if (copyDecladdingDexAndLibs(decladdingDexPath,
								destDexPath, libsFolderPath,
								destLibsFolderPath)) {
							String newAppPath = unzipFilePath + "\\"
									+ decompiledDirName + ".zip";
							packageApkFiles(unzipFilePath, newAppPath);
							File newZipFile = new File(newAppPath);
							if (newZipFile.exists()) {
								System.out.println("生成压缩文件:" + newAppPath);
								File unsignedApkFile = new File(unzipFilePath
										+ "\\new-app.apk");
								newZipFile.renameTo(unsignedApkFile);
								System.out.println("打包APP:"
										+ unsignedApkFile.getAbsolutePath());

								String signedApkPath = unzipFilePath + "\\"
										+ decompiledDirName + "_signed_"
										+ sdf.format(new Date()) + ".apk";

								signApk(unsignedApkFile.getAbsolutePath(),
										signedApkPath);

							} else {
								System.out.println("打包app失败");
							}
						} else {
							System.out.println("复制壳DEX或so失败");
						}

					} else {
						System.out.println("未找到新生成的apk");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("反编译失败");
		}
	}

	/**
	 * 从指定路径找到所有dex文件
	 * 
	 * @param unzipFilePath
	 * @return
	 * @throws IOException
	 */
	private static List<File> findAllDexFiles(String unzipFilePath)
			throws IOException {
		LinkedList<File> files = new LinkedList<>();
		File folderFile = new File(unzipFilePath);
		for (File f : folderFile.listFiles()) {
			if (f.getPath().endsWith(".dex")) {
				System.out.println("找到dex:" + f.getCanonicalPath());
				files.add(f);
			}
		}
		return files;
	}

	private static void packageApkFiles(String unzipFilePath, String newAppPath)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String winRARPath = getConfig().winRARPath;
		String zipCommand = "cd " + unzipFilePath + " && \"" + winRARPath
				+ "\" a -r " + newAppPath + " ./*";
		System.out.println("cmd:" + zipCommand);
		SystemCommand.execute(zipCommand);
	}

	/**
	 * 执行此方法确保，jarsigner的路径被添加到系统环境变量中
	 * 
	 * @param unsignedApkPath
	 *            未签名的apk的路径
	 * @param signedApkPath
	 *            生成的签名apk的路径
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void signApk(String unsignedApkPath, String signedApkPath)
			throws IOException, InterruptedException {

		String signCommand = "jarsigner -verbose -keystore "
				+ getConfig().signaturePath
				+ " "
				+ "-storepass "
				+ getConfig().storePwd
				+ " -keypass "
				+ getConfig().aliasPwd
				+ " "
				+ "-sigfile CERT -digestalg SHA1 -sigalg MD5withRSA -signedjar "
				+ signedApkPath + " " + unsignedApkPath + " "
				+ getConfig().alias;
		System.out.println("cmd:" + signCommand);
		;
		SystemCommand.execute(signCommand);
		System.out.println("签名后的apk路径:" + signedApkPath);
	}

	private static boolean copyDecladdingDexAndLibs(String decladdingDexPath,
			String destPath, String libsFolderPath, String destLibsFolderPath) {
		// TODO Auto-generated method stub
		File dexFile = new File(decladdingDexPath);
		if (dexFile.exists()) {
			System.out.println("脱壳dex路径:" + decladdingDexPath);
			// 开始复制文件
			try {
				FileInputStream fis = new FileInputStream(dexFile);
				FileOutputStream fos = new FileOutputStream(new File(destPath));
				byte[] buffer = new byte[1024];
				int readLength = 0;
				while (readLength != -1) {
					readLength = fis.read(buffer);
					if (readLength > 0) {
						fos.write(buffer, 0, readLength);
					}
				}
				fos.flush();
				fis.close();
				fos.close();
				System.out.println("壳Dex已复制到:" + destPath);

				System.out.println("开始复制libs,原始路径:" + libsFolderPath + " 目标路径:"
						+ destLibsFolderPath);
				FileUtil.copyDir(libsFolderPath, destLibsFolderPath);
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("脱壳dex未找到");
		}
		return false;
	}

	/**
	 * 修改AndroidMinifest.xml中的Application Class为脱壳的Application Class名
	 * 在Application标签中增加原Application Class名
	 * 
	 * @param workPath
	 */
	private static boolean alterAndroidMainifest(String workPath) {
		// TODO Auto-generated method stub
		String manifestFileName = "AndroidManifest.xml";
		File manifestFile = new File(workPath + "\\" + manifestFileName);
		if (!manifestFile.exists()) {
			System.err.println("找不到" + manifestFile.getAbsolutePath());
			return false;
		}
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(manifestFile);
			Element root = document.getRootElement();

			System.out.println("当前包名:" + root.attribute("package").getText());
			Element applicationEle = root.element("application");
			System.out.println("遍历application标签的属性:");
			Iterator<Attribute> attrIterator = applicationEle
					.attributeIterator();
			String APP_NAME = null;
			while (attrIterator.hasNext()) {
				Attribute attr = attrIterator.next();
				System.out.println(attr.getNamespacePrefix() + ":"
						+ attr.getName() + " = " + attr.getValue());
				if ("android".equals(attr.getNamespacePrefix())
						&& "name".equals(attr.getName())) {
					APP_NAME = attr.getValue();
					attr.setValue(DEX_APP_NAME);
					System.out.println("原application name:" + APP_NAME);
					System.out.println("新application name:" + attr.getValue());
				}
			}
			Element mataDataEle = applicationEle.addElement("meta-data");
			mataDataEle.addAttribute("android:name", "APP_NAME");
			mataDataEle.addAttribute("android:value", APP_NAME);

			manifestFile.delete();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");// 设置编码
			Writer writer = new FileWriter(manifestFile.getAbsolutePath());
			XMLWriter outPut = new XMLWriter(writer, format);
			outPut.write(document);
			outPut.close();
			System.out.println("修改Manifest成功");
			return true;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private static Config getConfig() {
		if (config != null) {
			return config;
		}

		File signerConfigFile = new File(getWorkPath() + "\\" + "config.xml");
		if (!signerConfigFile.exists()) {
			System.err.println("找不到" + signerConfigFile.getAbsolutePath());
			return null;
		}
		// 读取XML
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(signerConfigFile);
			Element root = document.getRootElement();
			Element signaturePathEle = root.element("signature-path");
			String signaturePath = signaturePathEle.getText();

			Element storePwdEle = root.element("store-pwd");
			String storePwd = storePwdEle.getText();

			Element aliasEle = root.element("alias");
			String alias = aliasEle.getText();

			Element aliasPwdEle = root.element("alias-pwd");
			String aliasPwd = aliasPwdEle.getText();

			Element winRARPathEle = root.element("winrar-path");
			String winRARPath = winRARPathEle.getText();

			System.out.println("signature-path:" + signaturePath
					+ " store-pwd:" + storePwd + " alias:" + alias
					+ " aliasPwd:" + aliasPwd + " winRARPath:" + winRARPath);
			config = new Config();
			config.signaturePath = signaturePath;
			config.storePwd = storePwd;
			config.alias = alias;
			config.aliasPwd = aliasPwd;
			config.winRARPath = winRARPath;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return config;
	}

	private static String getWorkPath() {
		return System.getProperty("user.dir");
	}

	static class Config {
		public String signaturePath;
		public String storePwd;
		public String alias;
		public String aliasPwd;
		public String winRARPath;
	}
}
