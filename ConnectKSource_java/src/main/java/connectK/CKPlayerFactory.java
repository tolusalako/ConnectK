package connectK;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class CKPlayerFactory {
	public static final String cppPrefix = "cpp:";
	public static final String cppPostfix = "exe";
	public static final String pythonPrefix = "py:";
	public static final String pythonPostfix = "py";
	public static final String javaPostfix = "class";
	public static final int JAVA = 0;
	public static final int CPLUSPLUS = 1;
	public static final int PYTHON = 2;
	int playerType;
	File aiProgram = null;
	Class<? extends CKPlayer> playerClass;

	//
	// CKPlayerFactory(Class<? extends CKPlayer> cls){
	// playerClass = cls;
	// playerType = JAVA;
	// try {
	// Constructor<? extends CKPlayer> c = playerClass.getConstructor(Byte.TYPE,
	// BoardModel.class);
	// c.newInstance((byte) 1, BoardModel.defaultBoard());
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new IllegalArgumentException();
	// }
	// }

	public CKPlayerFactory(int playerType, File aiFile) {
		this.playerType = playerType;
		try {
			switch (playerType) {
			case JAVA:
				if (aiFile.getParent() == null)
					aiFile = new File(new File(".").getAbsoluteFile(), aiFile.getName());
				ClassLoader cl = URLClassLoader.newInstance(new URL[] { aiFile.getParentFile().toURI().toURL() });
				Class<?> cls = Class.forName(aiFile.getName().split(".class")[0], true, cl);
				if (CKPlayer.class.isAssignableFrom(cls)) {
					playerClass = (Class<? extends CKPlayer>) cls;
					Constructor<? extends CKPlayer> c = playerClass.getConstructor(Byte.TYPE, BoardModel.class);
					c.newInstance((byte) 1, BoardModel.defaultBoard());// test													// instantiated
				} else {
					throw new IllegalArgumentException();
				}
				break;
			case CPLUSPLUS:
				File cppFile = aiFile;// new File(cpp);
				if (!cppFile.exists())
					throw new IllegalArgumentException("No such file.");
				this.playerType = CPLUSPLUS;
				this.aiProgram = cppFile;
				new CPlusPlusPlayer((byte) 1, BoardModel.defaultBoard(), aiProgram.getPath());
				break;
			case PYTHON:
				File pyFile = aiFile; // new File(py)
				if (!pyFile.exists())
					throw new IllegalArgumentException("No such file.");
				this.playerType = PYTHON;
				this.aiProgram = pyFile;
				new PythonPlayer((byte) 1, BoardModel.defaultBoard(), aiProgram.getPath());
				break;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public CKPlayerFactory(String arg) {
		try {
			if (arg.startsWith(cppPrefix)) { // C++ AI
				String cpp = arg.substring(cppPrefix.length());
				File cppFile = new File(cpp);
				if (cppFile.getParent() == null)
					cppFile = new File(new File(".").getAbsoluteFile(), cpp);
				if (!cppFile.exists())
					throw new IllegalArgumentException("No such file.");
				this.playerType = CPLUSPLUS;
				this.aiProgram = cppFile;
				new CPlusPlusPlayer((byte) 1, BoardModel.defaultBoard(), aiProgram.getPath());
			} else if (arg.endsWith(pythonPostfix)) { // Python AI
				String py = arg.substring(pythonPrefix.length());
				File pyFile = new File(py);
				if (pyFile.getParent() == null)
					pyFile = new File(new File(".").getAbsoluteFile(), py);
				if (!pyFile.exists())
					throw new IllegalArgumentException("No such file.");
				this.playerType = PYTHON;
				this.aiProgram = pyFile;
				new PythonPlayer((byte) 1, BoardModel.defaultBoard(), aiProgram.getPath());
			} else { // Java AI
				File classFile = new File(arg);
				if (classFile.getParent() == null)
					classFile = new File(new File(".").getAbsoluteFile(), classFile.getName());
				ClassLoader cl = URLClassLoader.newInstance(new URL[] { classFile.getParentFile().toURI().toURL() });
				Class<?> cls = Class.forName(classFile.getName().split(".class")[0], true, cl);
				if (CKPlayer.class.isAssignableFrom(cls)) {
					playerType = JAVA;
					playerClass = (Class<? extends CKPlayer>) cls;
					Constructor<? extends CKPlayer> c = playerClass.getConstructor(Byte.TYPE, BoardModel.class);
					c.newInstance((byte) 1, BoardModel.defaultBoard());// test											// instantiated
				} else {
					throw new IllegalArgumentException();
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public CKPlayer getPlayer(byte playerNumber, BoardModel model) {
		CKPlayer player = null;
		try {
			switch (playerType) {
			case JAVA:
				Constructor<? extends CKPlayer> c = playerClass.getConstructor(Byte.TYPE, BoardModel.class);
				player = c.newInstance(playerNumber, model);
				break;
			case CPLUSPLUS:
				player = new CPlusPlusPlayer(playerNumber, BoardModel.defaultBoard(), aiProgram.getPath());
				break;
			case PYTHON:
				player = new PythonPlayer(playerNumber, BoardModel.defaultBoard(), aiProgram.getPath());
				break;
			}
		} catch (Exception e) {
			// TODO Log this instead of printing
			// e.printStackTrace();
		}
		return player;
	}

	@Override
	public String toString() {
		String rs = null;
		switch (playerType) {
		case JAVA:
			if (playerClass == null)
				System.out.println("null playerClass");
			else
				rs = playerClass.getName();
			break;
		case CPLUSPLUS:
			rs = aiProgram.getName();
			break;
		case PYTHON:
			rs = aiProgram.getName();
			break;
		default:
			rs = "error";
		}
		return rs;
	}

	// public static void main(String[] argv){
	//
	// System.out.println("cppShell.exe".startsWith("cpp"));
	//
	// }
}
