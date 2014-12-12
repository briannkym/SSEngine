/*The MIT License (MIT)

Copyright (c) 2014 Mark Groeneveld

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package dialog;

import java.util.ArrayList;

/**
 * Node data structure for implementing dialog graphs.
 * 
 * @author Mark Groeneveld
 * @version 1.0
 */

//TODO Should I check every variable for every conceivable error?
//TODO Finish class javadoc.
public class DialogNode {
	private String text;
	private DialogNode[] children;
	private ArrayList<double[]> probSets;
	private boolean isNPC;
	
	/**
	 * Constructs and initializes a DialogNode.
	 * 
	 * @param isNPC
	 *            Is this node spoken by an NPC (Non-Playable Character)?
	 * @param text
	 *            Text to be spoken
	 *			  The unique string "initial" indicates a dialog's starting node.
	 *            This node will not be spoken, but leads to one or more other nodes which are.
	 * @param probSets
	 *            Map of probabilities the NPC uses to select a response
	 *            There may be multiple arrays corresponding to different strategies.
	 *            Array index is the strategy number.
	 *            Each array should sum to 1.0 and should be of same length as children.
	 *            Each probability set is also described as this node's row of the discrete time Markov chain's transition matrix, 
	 *            the part of which is nonzero for all strategies.
	 *            An empty array indicates no probability set. Do not use a null object.  
	 * @param children
	 *            Array of children nodes
	 *            If children is empty this is an end node.
	 */		
	//TODO should @thows also be here in addition to in the check methods?
	public DialogNode(boolean isNPC, String text, ArrayList<double[]> probSets ,DialogNode[] children) { 
		//Checking for valid inputs.
		checkText(text);
		checkProbSets(children, probSets, isNPC);
		
		this.text = text;
		this.children = children;
		this.isNPC = isNPC;
		this.probSets = probSets;
	}
	
	/**
	 * Checks for null text.
	 * 
	 * @param text node text to be checked
	 * @throws NullPointerException if text is null
	 */
	static void checkText(String text) {
		if (text == null)
			throw new NullPointerException("Null text is not allowed.");
	}
	
	/**
	 * Checks the validity of probability sets.
	 * 
	 * @param children children of node
	 * @param probSets probability sets of node
	 * @param isNPC NPC status of node
	 * @throws IllegalArgumentException if probability sets don't sum to 1.0
	 * @throws IllegalArgumentException if problem set lengths don't equal children length
	 * @throws IllegalArgumentException if node doesn't have a probability set but is player-controlled and has children 
	 */
	static void checkProbSets(DialogNode[] children, ArrayList<double[]> probSets, boolean isNPC) {
		//Checks that each probability set sums to 1.0.
		for (int strategy = 0; strategy < probSets.size(); strategy++) {
			double sum = 0.0;
			for (double value : probSets.get(strategy))
				sum += value;
			if (sum > 1.01 || sum < 0.99)
				throw new IllegalArgumentException("probSet " + Integer.toString(strategy) + " does not sum to 1.0.");
		}
		
		//Checks that player-controlled nodes with children have probSets.
		if (children.length > 0 && probSets.size() == 0 && !isNPC)
			throw new IllegalArgumentException("is player-controlled and has children and therefore must have at least one probability set");
		
		//Checks that probSet lengths match children length.
		for (int strategy = 0; strategy < probSets.size(); strategy++)
			if (probSets.get(strategy).length != children.length)
				throw new IllegalArgumentException("probSet length of strategy " + Integer.toString(strategy) + " does not match children length");
	}
	
	/**
	 * Gets this node's text.
	 * 
	 * @return text of this node
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Gets this node's children nodes.
	 * 
	 * @return children of this node
	 */
	public DialogNode[] getChildren() {
		return children;
	}
	
	/**
	 * Gets whether this node is player or non-player controlled.
	 * 
	 * @return true if node is non-player controlled
	 */
	public boolean getIsNPC() {
		return isNPC;
	}
	
	/**
	 * Gets this node's probability set(s).
	 * 
	 * @return probability set(s) for this node
	 */	
	public ArrayList<double[]> getProbSets() {
		return probSets;
	}
	
	/**
	 * Sets this node's text.
	 * 
	 * @param text text of this node
	 */
	void setText(String text) {
		checkText(text);
		this.text = text;
	}
	
	/**
	 * Sets this node's probability set(s).
	 * 
	 * @param probSets probability set(s) for this node
	 */	
	public void setProbSets(ArrayList<double[]> probSets) {
		this.probSets = probSets;
	}
	
	/**
	 * Marks node as non-player controlled.
	 */
	public void setNPC() {
		isNPC = true;
	}
	
	/**
	 * Marks node as player controlled.
	 */
	public void setPC() {
		isNPC = false;
	}
	
	/**
	 * Sets this node's children nodes.
	 * 
	 * @param children children of this node
	 */
	public void setChildren(DialogNode[] children) {
		this.children = children;
	}
}