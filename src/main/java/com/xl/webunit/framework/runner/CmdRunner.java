package com.xl.webunit.framework.runner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xl.webunit.framework.Action;
import com.xl.webunit.framework.Step;
import com.xl.webunit.framework.TestCase;
import com.xl.webunit.framework.TestElement;
import com.xl.webunit.framework.TestListener;
import com.xl.webunit.framework.Verification;
import com.xl.webunit.util.Constants;

/**
 * 
 *  this is a test runner for debug, it will output running status to log file, 
 *  each test case has separate file. 
 */
public class CmdRunner implements TestListener {
	
	private boolean console=true;
	private String projectName="testgo";
	private FileWriter writer;
	private List<String> buffer=new ArrayList<String>();
	private int bcount=20;
	
	public CmdRunner(String name) {
		String sep = System.getProperty("file.separator");
		String userHome=System.getProperty("user.home");
		
		String path = userHome+sep + projectName + sep ;
		File fPath = new File (path);
		
		if (fPath.exists()) {
			//delete old files if any
			File[] allFiles = fPath.listFiles();
			
			if (allFiles!=null)
				for (File f:allFiles) {
					f.delete();
				}
		} else {
			//else create this directory
				fPath.mkdir();
		}
		try {
			File fLog = new File(path+name+".log");
			writer = new FileWriter(fLog);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void log(String msg) {
		try {
			//to reduce the I/O of external files
			buffer.add(msg);
			if (buffer.size() >= bcount) {
				for (String s : buffer) {
					writer.write(s);
					writer.write("\n");
				}
				writer.flush();
				
				//clear all elements
				while (buffer.size()>0) {
					buffer.remove(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				//try again to flush content in writer
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startTest(TestCase test) {
		StringBuffer sb = new StringBuffer();
		sb.append(test.getName() + " started");
		print(sb.toString());
	}

	
	public void endTest(TestCase test) {
		String result;
		if (test.isSuccess())
			result="succeeds";
		else 
			result="fails";
		String msg  = test.getName() + " " + result+", "+test.getExecTime() + " seconds";
		print(msg);
	}

	public void startStep(Step step) {
		StringBuffer sb = new StringBuffer("\t");
		sb.append("step" +  step.getIndex() + " ");
		if (step.getComment()!=null)
			sb.append(step.getComment());
		print(sb.toString());
	}
	
	public void endStep(Step step) {
		String result;
		if (step.isSuccess())
			result="succeeds";
		else 
			result="fails";
		String msg  = "\t--Step" +step.getIndex() + " " + result+", " + step.getExecTime() +" seconds";
		print(msg);
	}
	
	public void startAction(Action action) {
		StringBuffer sb = new StringBuffer("\t\t");
		
		sb.append(action.getActionType() + " ");
		for (String p : action.getParams()) {
			sb.append(p+ " ");
		}
		//String msg = action.getActionType() + action.get
		print(sb.toString());
	}

	public void endAction(Action action) {
		if (!action.isSuccess()) {
			if (console) {
				System.out.println("\t\t--Action fails, " + action.getExecTime() + " seconds");
				System.out.println("--------------------------");
				if (action.getException()!=null)
					action.getException().printStackTrace();
				System.out.println("--------------------------");
			}
			log ("\t\tAction fails, " + action.getExecTime());
			log("--------------------------");
			if (action.getException()!=null) {
				StackTraceElement[] stes = action.getException().getStackTrace();
				for (StackTraceElement ste : stes) {
					log(ste.toString());
				}
			}
			log("--------------------------");
		} else {
			String msg =  "\t\t--Action succeeds, " + action.getExecTime() + " seconds";
			print(msg);
		}
	}

	public void startVerif(Verification verif) {
		StringBuffer sb = new StringBuffer("\t\t");
		sb.append(verif.getType() + " ");
		sb.append(verif.getObject() + " ");
		sb.append(verif.getValue() + " ");
		sb.append(verif.getParam() + " ");
		
		print(sb.toString());
	}
	public void endVerif(Verification verif) {
		String result;
		if (verif.isSuccess())
			result="succeeds";
		else 
			result="fails";
		String msg  = "\t\t--Verif " + result+", " + verif.getExecTime() + " seconds";
		print(msg);
		
		if (verif.getException()!=null) {
			StringBuffer bu = new StringBuffer("--------------------------\n");
			bu.append(verif.getException().getMessage() + "\n");
			
			bu.append("--------------------------\n");
			print(bu.toString());
			verif.getException().printStackTrace();
		}
	}
	
	public void addMessage(TestElement element, String message) {
		String tab = createTab(element);
		message = tab + message;
		print(message);
	}
	
	public void addFailure(TestElement element, String message) {
		String tab = createTab(element);
		
		message = tab+"[failure]"+message;
		print(message);
	}

	private String createTab(TestElement element) {
		String tab=null;
		if (element instanceof Step) {
			tab="\t";
		} else if (element instanceof Action ||
				element instanceof Verification) {
			tab="\t\t";
		}
		return tab;
	}

	private void print(String str) {
		if (console)
			System.out.println(str);
		log(str);
	}

	public void cleanup() {
		try {
		if (buffer.size() > 0) {
			for (String s : buffer) {
				writer.write(s);
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		}} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
