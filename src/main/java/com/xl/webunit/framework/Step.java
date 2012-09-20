package com.xl.webunit.framework;

import java.util.ArrayList;
import java.util.List;


public class Step extends TestElement {

	private int index;
	private String comment;
	private String extenstion;
	
	public String getExtenstion() {
		return extenstion;
	}

	public void setExtenstion(String extenstion) {
		this.extenstion = extenstion;
	}

	public List<Action> getActions() {
		return actions;
	}

	public List<Verification> getVerifs() {
		return verifs;
	}

	private List<Action> actions=new ArrayList<Action>();
	private List<Verification> verifs=new ArrayList<Verification>();
	
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}

	public void addAction(Action ac) {
		ac.setParent(Step.this);
		if (actions==null) actions = new ArrayList<Action>();
		actions.add(ac);
	}
	
	public void addVerif(Verification verif) {
		verif.setParent(Step.this);
		if (verifs==null) verifs = new ArrayList<Verification>();
		verifs.add(verif);
	}
	
	public boolean execute() {
		
		publish(TestResult.Type.STEP_START, this);
		
		long start = System.currentTimeMillis();
		
		if (actions!=null)
		for (int i=0; success && i<actions.size();++i) {
			success=actions.get(i).execute();
		}
		
		//do verification
		if (verifs!=null)
		for (int i=0;success && i<verifs.size();++i) {
			success=verifs.get(i).execute();
		}
		
		execTime = System.currentTimeMillis() - start;
		
		publish(TestResult.Type.STEP_END, this);
		return success;
	}

	@Override
	public void count() {
		getTestResult().countStep(1);
		for (Action action : actions) {
			action.count();
		}
		for (Verification verif : verifs) {
			verif.count();
		}
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}

	public Action getLastAction() {
		if (actions.size()<=0) return null;
		else return actions.get(actions.size() - 1);
	}
}
