/*******************************************
 * Name     : CloneManager
 * Function : Example class to show the use of proto-type pattern
 * Author   : Victor@JoyfulVillage
 * Date     : August 28, 2012 
 * 
 * modification:
 * Date  :
 * Author:
 * Change:
 * Ref   :
 * 
 * 
 */

package com.joyfulv.example.gof.creational;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CloneManager {
	
	/**
	 * clone manager is also a factory pattern
	 * I have the simplified version (without reflection)
	 * so that instance would be in initial
	 * state after clone, regardless if it is a shallow clone.
	 * The class would need a public init method to do internal  
	 * initialization
	 * 
	 * However, the clone() method would allow shallow clone on object to 
	 * clone the current internal state.
	 * 
	 */
	private static CloneManager cloneManager = 
									new CloneManager();
	
	private final Map<String, Prototype> RegisteredCloneMap = 
			new ConcurrentHashMap<String, Prototype>();
	
	private CloneManager() {}
	
	public static CloneManager getCloneManager() {
		return cloneManager;
	}
	
	public void registerCloneableType(String Type, Prototype clonableClass){
		if (!RegisteredCloneMap.containsKey(Type)) {
			RegisteredCloneMap.put(Type, clonableClass);
		}
	}
	
	public Prototype createClone(String cloneType) {
		
		if (RegisteredCloneMap.containsKey(cloneType)){
			try {
				return (Prototype)RegisteredCloneMap.get(cloneType).clone();
			} catch (CloneNotSupportedException cnse) {
				System.err.println(cnse.getMessage());
				cnse.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 * ==============================================
	 * Interface and class should extracted
	 */
	
	/**
	 * An container class to expend the visibility of clone function
	 * @author Victor.Chan
	 */
	static class Prototype implements Cloneable {
		
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}
	}
	
	
	/**
	 * A simple tree class in game with shallow clone
	 * @author Victor.Chan
	 *
	 */
	static class Tree extends Prototype {
		
		Double locX;
		Double locY;

		public Tree() {
			this.locX = 0.0;
			this.locY = 0.0;
		}
		
		public Tree(Double x, Double y) {
			this.locX = x;
			this.locY = y;
		}
		
		/**
		 * Shallow clone
		 */
		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException cnse){
				return null;
			}
		}
		
		public void setLocation(Double x, Double y){
			this.locX = x;
			this.locY = y;
		}
		
		public String toString(){
			return "Location: "+ locX + ", "+ locY;
		}
	}
	
	/**
	 * A simple TreasureBox with initialize OPENED state
	 * while being cloned
	 * @author Victor.Chan
	 */
	static class TreasureBox extends Prototype {
		boolean opened;
		int defaultValue;
		int value;
		
		public TreasureBox(){ 
			defaultValue = 100;
		}
		
		public TreasureBox(int value){
			this.defaultValue = value;
			this.value = value;
		}
		
		public void init(int value){ 
			opened = false;
			this.defaultValue = value;
			this.value = value;
		}
		
		public void openIt(){ 
			opened = true;
			value  = 0;
		}
		
		/**
		 * initialize internal state
		 */
		@Override
		public Object clone() {
			try {
				TreasureBox tbox = (TreasureBox) super.clone();
				tbox.init(this.defaultValue);
				return tbox;
			} catch (CloneNotSupportedException cnse){
				return null;
			}
		}
		
		public String toString() {
			return "I am "+(opened?"opened, value: 0":"not opened, "+" value: "+value);
		}
	}
	
	/**
	 * A simple Villain class with deep clone
	 * @author Victor.Chan
	 *
	 */
	static class Villain extends Prototype {
		
		int initHP;
		int HP;
		int ID;
		TreasureBox treasure;
		
		public Villain(){
			treasure = new TreasureBox();
		}
		
		public Villain(int initHP, int treasureValue, int ID){
			this.initHP = initHP;
			this.HP = initHP;
			this.ID = ID;
			treasure = new TreasureBox(treasureValue);
		}
		
		public int deductHP(int value){
			int retval = 0;
			HP -= value;
			if (HP <= 0){
				treasure.openIt();
				retval = treasure.value;
			}
			return retval;
		}
		
		public void init(int initHP, int ID){
			this.HP = initHP;
			this.ID = ID;
		}
		
		/**
		 * Deep clone
		 */
		@Override
		public Object clone() {
			try {
				Villain villain = (Villain)super.clone();
				villain.init(this.initHP, ++ID);
				villain.treasure = (TreasureBox)villain.treasure.clone();
				return villain;
			} catch (CloneNotSupportedException cnse){
				return null;
			}
		}
		
		@Override
		public String toString(){
			return "Villian: ID: "+ID+" HP: "+HP+" treasure: "+treasure.value;
		}
	}
	
	/**
	 * Embedded testing module
	 * @param args
	 */
	public static void main(String[] args){
		
		System.out.println("===== Clone by clone manager =====");
		CloneManager cloneManager = CloneManager.getCloneManager();
		cloneManager.registerCloneableType("Tree", new Tree());
		cloneManager.registerCloneableType("TreasureBox", new TreasureBox());
		cloneManager.registerCloneableType("Villain", new Villain());
		System.out.println();
		
		Tree treeTry = (Tree)cloneManager.createClone("Tree");
		System.out.println("Tree from manager :"+treeTry.toString());
		TreasureBox tboxTry = (TreasureBox)cloneManager.createClone("TreasureBox");
		System.out.println("Treasure from manager :"+tboxTry.toString());
		Villain villainTry = (Villain)cloneManager.createClone("Villain");
		System.out.println("Villain from manager :"+villainTry.toString());
		
		System.out.println("\n===== Clone by object =====");
		
		Tree tree = new Tree(5.0, 6.0);
		System.out.println("Tree      :"+tree.toString());
		Tree copyTree = (Tree)tree.clone();
		System.out.println("Tree clone:"+copyTree.toString());
		System.out.println();
		
		TreasureBox tbox = new TreasureBox(100);
		System.out.println("Treasure box: "+ tbox.toString());
		tbox.openIt();
		System.out.println("Open the treasure box");
		System.out.println("After opened: "+ tbox.toString());
		TreasureBox copyTbox = (TreasureBox)tbox.clone();
		System.out.println("Treasure box copy: "+copyTbox.toString());
		System.out.println();
		
		Villain clown = new Villain(100, 10, 1);
		System.out.println(clown.toString());
		clown.deductHP(50);
		System.out.println("hurt by 50HP");
		System.out.println(clown.toString());
		clown.deductHP(50);
		System.out.println("hurt by 50HP");
		System.out.println(clown.toString());
		System.out.println("make 2 clones - ");
		Villain clownTwin = (Villain)clown.clone();
		System.out.println(clownTwin.toString());
		
		Villain clownThree = (Villain)clown.clone();
		System.out.println(clownThree.toString());
	}
}
