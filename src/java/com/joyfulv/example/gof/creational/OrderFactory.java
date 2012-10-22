/*******************************************
 * Name     : OrderFactory
 * Function : Example class to show the use of Factory pattern
 * Author   : Victor@JoyfulVillage
 * Date     : August 27, 2012 
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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderFactory {
	private static OrderFactory orderFactory = new OrderFactory();
	
	/**
	 * Internal map to register different order
	 */
	private final Map<String, Order> RegisteredOrderTypeMap = 
						new ConcurrentHashMap<String, Order>();
	
	/**
	 * Internal map for reflection method
	 * ? extends Order is used for child class
	 */
	private final Map<String, Class<? extends Order>> RegisteredOrderTypeByReflectionMap = 
			new ConcurrentHashMap<String, Class<? extends Order>>();
	
	private OrderFactory() {}
		
	/*=========================================================
	 * Again, this class defining section should be pulled
	 * out to separate files.
	 */
	/**
	 * Simplified version of Order class
	 * @author victor.chan
	 */
	static abstract class Order {
		String orderType;
		public Order(String type){ 
			this.orderType = type;
			System.out.println("creating "+orderType+" order");
		}
		abstract Order createOrder();
	}
	
	/**
	 * quick declare of limit order
	 * @author victor.chan
	 */
	static class LimitOrder extends Order {
		public LimitOrder() {
			super("limit");
		}
		LimitOrder(Double price) {
			super("limit");
			System.out.println("with price:" + price);
		}
		@Override
		public Order createOrder(){
			return (new LimitOrder());
		}
	}
	
	/**
	 * quick declare of market order, constructor should not use primitive type!
	 * @author victor.chan
	 */
	static class MarketOrder extends Order {
		public MarketOrder() {
			super("market");
		}
		MarketOrder(String market, Boolean isDMA){
			super("market order in "+market+ (isDMA?"is DMA":"not DMA"));
		}
		@Override
		public Order createOrder(){
			return (new MarketOrder());
		}
	}
	
	/**
	 * Exception to be thrown when duplicate factory key is detected
	 * @author Victor.Chan
	 */
	static class FactoryKeyDuplicateException extends Exception {
		
		private static final long serialVersionUID = -347529444870129065L;

		public FactoryKeyDuplicateException() { super(); }
		
		@Override
		public String getMessage(){
			return "Factory key entry is duplicated";
		}
	}
	
	/*
	 * ============ End static class declaration =========================
	 */
	
	/**
	 * Public getInstance interface
	 * @return OrderFactory singleton
	 */
	public static OrderFactory getInstance() {
		return orderFactory;
	}
	
	/**
	 * Function to register new order type to be generated
	 * @param orderType
	 * @param orderClass
	 * @throws FactoryKeyDuplicateException 
	 */
	public void registerOrderType(String orderType, Order orderClass) 
									throws FactoryKeyDuplicateException{
		if (!RegisteredOrderTypeMap.containsKey(orderType)) {
			RegisteredOrderTypeMap.put(orderType, orderClass);
		} else {
			throw new FactoryKeyDuplicateException();
		}
	}
	
	/**
	 * Public interface to get new order with specific type
	 * @param orderType
	 * @return new order object
	 */
	public Order createOrder(String orderType){
		if (RegisteredOrderTypeMap.containsKey(orderType)){
			Order order = RegisteredOrderTypeMap.get(orderType);
			return order.createOrder();
		} else {
			return null;
		}
	}
	
	/**
	 * Another way to register in factory by reflection
	 * @param orderType
	 * @param orderClass
	 * @throws FactoryKeyDuplicateException 
	 */
	public void registerOrderTypeByReflection(String orderType, 
											  Class<? extends Order> orderClass) 
												throws FactoryKeyDuplicateException{
		if (!RegisteredOrderTypeByReflectionMap.containsKey(orderType)) {
			RegisteredOrderTypeByReflectionMap.put(orderType, orderClass);
		} else {
			throw new FactoryKeyDuplicateException();
		}
	}

	/**
	 * Another way to create object from reflection class
	 * @param orderType
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public Order createOrderFromReflection(String orderType, Object[] parameters) 
							throws IllegalArgumentException, SecurityException, 
								   InvocationTargetException, NoSuchMethodException{
		
		if (RegisteredOrderTypeByReflectionMap.containsKey(orderType)){
			try {
				Class<? extends Order> orderClass = RegisteredOrderTypeByReflectionMap.get(orderType);
				
				if (parameters != null){

					Class<?>[] classList = new Class[parameters.length];
					
					int count = 0;
					for (Object parameter:parameters){
						classList[count++] = parameter.getClass();						
					}
				
					return orderClass.getDeclaredConstructor(classList).newInstance(parameters);
				}
				else {	
					return orderClass.newInstance();
				}
			} catch (InstantiationException ie) {
				ie.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			}
			return null;
		} else {
			return null;
		}
	}
	
	/**
	 * Embedded testing module
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args){
		OrderFactory of = OrderFactory.getInstance();
		
		//the new LimitOrder and new MarketOrder 
		//has side effect of 2 printing statement
		try {
			of.registerOrderType("limit", new LimitOrder());
			of.registerOrderType("market", new MarketOrder());
		} catch (FactoryKeyDuplicateException fkde){
			fkde.printStackTrace();
		}
		
		System.out.println("\n===== By abstract class ====");
		
		Order lo = of.createOrder("limit");
		Order mo = of.createOrder("market");
		
		System.out.println("\n===== By Reflection ====");
		
		//No side effect
		try {
			of.registerOrderTypeByReflection("limit", LimitOrder.class);
			of.registerOrderTypeByReflection("market", MarketOrder.class);
		} catch (FactoryKeyDuplicateException fkde){
			fkde.printStackTrace();
		}
		
		
		try {
			Order lof = of.createOrderFromReflection("limit", null);
			Order lofwp = of.createOrderFromReflection("limit", (new Object[] { (new Double(10.99)) }));
			Order mof = of.createOrderFromReflection("market", null);
			Order mofwp = of.createOrderFromReflection("market", new Object[] {(new String("OTC-PINK")), new Boolean(true)});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
	}
}
