/*******************************************
 * Name     : FrontEndBuilder
 * Function : Example class to show the use of builder pattern
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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class FrontEndBuilder {
	
	/**
	 * Abstract Builder - FormBuilder
	 * @author Victor.Chan
	 */
	static abstract class FormBuilder {
		abstract List<String> getGrids();
		abstract List<String> getButtons();
		abstract List<String> getDialogs();
	}
	
	/**
	 * Product - Form
	 * @author Victor.Chan
	 */
	static class Form {
		private List<String> grids = new Vector<String>();
		private List<String> buttons = new Vector<String>();
		private List<String> dialogs = new Vector<String>();
		
		public void addGrids(List<String> newGrids) { 
			grids.addAll(newGrids);
		}
		
		public void addButtons(List<String> newButtons) {
			buttons.addAll(newButtons);
		}
		
		public void addDialogs(List<String> newDialogs) {
			dialogs.addAll(newDialogs);
		}
		
		public List<String> getGrids() { return grids; }
		
		public List<String> getButtons() { return buttons; }
		
		public List<String> getDialogs() { return dialogs; }
	}
	
	/**
	 * Concrete Builder - orderEntryFormBuilder
	 * @author Victor.Chan
	 */
	static class OrderEntryFormBuilder extends FormBuilder {

		@Override
		List<String> getGrids() {
			
			return Arrays.asList("ActiveOrderGrid", 
								 "PendingOrderGrid");
		}

		@Override
		List<String> getButtons() {
			return Arrays.asList("ClientBuyButton",
					  			 "ClientSellButton");
		}

		@Override
		List<String> getDialogs() {
			return Arrays.asList("OrderEntryDialog", 
							     "OrderAmendDialog");
		}
	}
	
	/**
	 * Concrete Builder - derivativeFormBuilder
	 * @author Victor.Chan
	 */
	static class DerivativeFormBuilder extends FormBuilder {

		@Override
		List<String> getGrids() {
			return Arrays.asList("DerivativeOrderGrid");
		}

		@Override
		List<String> getButtons() {
			return Arrays.asList("OptionBuyButton", 
								 "OptionSellButton",
					             "FuturesBuyButton", 
					             "FuturesSellButton");
		}

		@Override
		List<String> getDialogs() {
			return Arrays.asList("OptionEntryDialog",
					             "OptionAmendDialog",
					             "FuturesEntryDialog",
					             "FuturesAmendDialog") ;
		}
	}
	
	/**
	 * Concrete Builder - MarketDataFormBuilder
	 * @author Victor.Chan
	 */
	static class MarketDataFormBuilder extends FormBuilder {

		@Override
		List<String> getGrids() {
			return Arrays.asList("MarketDepthGrid", 
								 "QuoteGrid");
		}

		@Override
		List<String> getButtons() {
			return null;
		}

		@Override
		List<String> getDialogs() {
			return Arrays.asList("ProfolioEntryDialog");
		}
	}
	
	/**
	 * Driver - ExecutionTraderFormCreater
	 * @author Victor.Chan
	 */
	static class ExecutionTraderFormCreater {
		static Form etraderForm = new Form();
		
		private static void buildForm(){
			OrderEntryFormBuilder oeBuilder = 
					new OrderEntryFormBuilder();
			DerivativeFormBuilder deBuilder = 
					new DerivativeFormBuilder();
			MarketDataFormBuilder mdBuilder = 
					new MarketDataFormBuilder();
			
			etraderForm.addGrids(oeBuilder.getGrids());
			etraderForm.addGrids(mdBuilder.getGrids());
			etraderForm.addButtons(oeBuilder.getButtons());
			etraderForm.addButtons(deBuilder.getButtons());
			etraderForm.addDialogs(mdBuilder.getDialogs());
		}
		
		public static Form getForm(){
			buildForm();
			return etraderForm;
		}
	}
	
	/**
	 * Testing purposes - Main
	 * Simplified version of creating a front-end
	 * For practical use: create instance of the list of items
	 * by name (or change String to .class) and add them to the swing panel
	 * @param args
	 */
	public static void main(String[] args){
		
		Form instanceForm = ExecutionTraderFormCreater.getForm();
		System.out.println("My buttons list:");
		for (String button : instanceForm.getButtons()) {
			System.out.println(button);
		}
		System.out.println("My grids list:");
		for (String grid : instanceForm.getGrids()) {
			System.out.println(grid);
		}
		System.out.println("My Dialog list:");
		for (String dialog : instanceForm.getDialogs()) {
			System.out.println(dialog);
		}
	}
}
