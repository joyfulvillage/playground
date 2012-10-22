/*******************************************
 * Name     : CoRAWSItemInfoReqSvr
 * Function : Example class to show the use of chain of responsibility
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

package com.joyfulv.example.gof.behavioral;

public class CoRAWSItemInfoReqSvr {
	
	/**
	 * Dummy database class to fake database connection
	 * Replying testing variable
	 * @author Victor.Chan
	 *
	 */
	static class DummyDatabase {
		static public boolean isBook(String ASIN){
			return ASIN.endsWith("BK")?true:false;
		}
		static public boolean isMovie(String ASIN){
			return ASIN.endsWith("MV")?true:false;
		}
		static public String getBookTitle(String ASIN){
			return "JAVA Blackbook";
		}
		static public String getMovieTitle(String ASIN){
			return "Star War";
		}
		static public String getBookISBN(String ASIN){
			return "1234567890";
		}
		static public String getMovieRate(String ASIN){
			return "PG-13";
		}
		static public String getPrice(String ASIN){
			return "13.99";
		}
		static public String getListedPrice(String ASIN){
			return "15.97";
		}
	}
	
	/**
	 * Basic Request class
	 * @author Victor.Chan
	 */
	static class Request {
		private String ASIN;
		
		public Request(String ASIN){
			this.ASIN = ASIN;
		}
		
		public String getASIN(){ return ASIN; }
	}
	
	/**
	 * Hander not initialized properly exception
	 * @author Victor.Chan
	 */
	static class HandlerNotInitException extends Exception {
		
		private static final long serialVersionUID = -9040924854597299894L;
		
		public HandlerNotInitException() { super(); }
		
		@Override
		public String getMessage(){
			return "Handler is not initialized properly";
		}
	}
	
	/**
	 * The base handler class with prevention of broken chain
	 * @author Victor.Chan
	 */
	static abstract class Handler {
		protected Handler successor;
		protected abstract String handleRequestImpl(Request request, 
													String replyMsg);
		
		public void setSuccessor(Handler successor) 
										throws HandlerNotInitException{ 
			if (successor == null) { throw new HandlerNotInitException(); }
			this.successor = successor; 
		}
		
		/**
		 * Prevention of broken chain
		 * @param request
		 */
		public final String handleRequest(Request request, String replyMsg){
			String updatedMsg = this.handleRequestImpl(request, replyMsg);
			if (successor != null && !updatedMsg.endsWith("EOF")){
				updatedMsg = successor.handleRequest(request, updatedMsg);
			}
			return updatedMsg;
		}
	}
	
	/**
	 * Concrete Handler classes
	 * @author Victor.Chan
	 *
	 */
	static class BookRequestHandler extends Handler {
		
		@Override
		protected String handleRequestImpl(Request request, String replyMsg) {
			String updatedMsg = replyMsg;
			if (DummyDatabase.isBook(request.getASIN())){
				updatedMsg += "|BookTitle:" + 
						DummyDatabase.getBookTitle(request.getASIN());
				updatedMsg += "|ISBN:"+
						DummyDatabase.getBookISBN(request.getASIN());
				updatedMsg += "|EOF";
			} 
			return updatedMsg;
		}
		
	}
	
	static class GeneralRequestHandler extends Handler {

		@Override
		protected String handleRequestImpl(Request request, String replyMsg) {
			String updatedMsg = replyMsg;
			updatedMsg += "ASIN:"+request.getASIN();
			updatedMsg += "|ListedPrice:" +
					DummyDatabase.getListedPrice(request.getASIN());
			updatedMsg += "|Price:" +
					DummyDatabase.getPrice(request.getASIN());
			return updatedMsg;
		}
		
	}
	
	static class MovieRequestHandler extends Handler {
		
		@Override
		protected String handleRequestImpl(Request request, String replyMsg) {
			String updatedMsg = replyMsg;
			if (DummyDatabase.isMovie(request.getASIN())){
				updatedMsg += "|MovieTitle:" + 
						DummyDatabase.getMovieTitle(request.getASIN());
				updatedMsg += "|MovieRating:" +
						DummyDatabase.getMovieRate(request.getASIN());
				updatedMsg += "|EOF";
			} 
			return updatedMsg;
		}
	}
	
	/**
	 * Concrete handler class to prevent un-handled request
	 * @author Victor.Chan
	 *
	 */
	static class UnknownRequestHandler extends Handler {

		@Override
		protected String handleRequestImpl(Request request, String replyMsg) {		
			return replyMsg + "\nUNKNOW\n|EOF";
		}
		
	}
	
	
	private GeneralRequestHandler grHandler;
	private BookRequestHandler    brHandler;
	private MovieRequestHandler   mrHandler;
	private UnknownRequestHandler ukHandler;
	
	public CoRAWSItemInfoReqSvr() {
		grHandler = new GeneralRequestHandler();
		brHandler = new BookRequestHandler();
		mrHandler = new MovieRequestHandler();
		ukHandler = new UnknownRequestHandler();
		
		try {
			grHandler.setSuccessor(brHandler);
			brHandler.setSuccessor(mrHandler);
			mrHandler.setSuccessor(ukHandler);
			
		} catch (HandlerNotInitException hnie ){
			System.err.println(hnie.getMessage());
			hnie.printStackTrace();
		}
	}
	
	/**
	 * Request handling entry point - start the chain
	 * @param request
	 */
	public String handleRequest(Request request){
		String replyMsg = "";
		return grHandler.handleRequest(request, replyMsg);
	}

	/**
	 * Embedded testing module
	 * @param args
	 */
	public static void main(String[] args) {
		
		CoRAWSItemInfoReqSvr infoReqSvr = new CoRAWSItemInfoReqSvr();
		
		Request bookRequest = new Request("1234BK");	
		Request movieRequest = new Request("5678MV");
		Request unknownRequest = new Request("013579");
		
		
		System.out.println(infoReqSvr.handleRequest(bookRequest));
		System.out.println();
		System.out.println(infoReqSvr.handleRequest(movieRequest));
		System.out.println();
		System.out.println("Error example since the price is hard-coded\nprice should be 0");
		System.out.println(infoReqSvr.handleRequest(unknownRequest));
	}


	
}
