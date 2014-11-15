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

import java.util.HashMap;

/**
 * Node data structure for implementing dialog graphs.
 * 
 * @author Mark Groeneveld
 * @version 1.0
 */

//TODO do top dialog
public class DialogNode {
	private String text;
	private DialogNode[] children;
	private HashMap<Integer, Double[]> probSets;
	private boolean isNPC;
	private double x, y;
	
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
	 *            Map key is the strategy, array index is child.
	 *            Each array should sum to 1 and should be of same length as children.
	 *            Each probability set is also described as this node's row of the discrete time Markov chain's transition matrix, 
	 *            the part of which is nonzero for all strategies.
	 *            An empty map indicates no probability set. Do not use a null object.  
	 * @param children
	 *            Array of children nodes
	 *            If children is empty this is an end node.
	 * @param x
	 * 			  Node's horizontal position in a graphical editor application
	 * @param y
	 * 			  Node's vertical position in a graphical editor application
	 * @throws IllegalArgumentException
	 * 			  if there is no text
	 * @throws IllegalArgumentException
	 * 			  if node is player controlled and does not contain the default strategy (key = 0) probability set
	 * @throws IllegalArgumentException
	 * 			  if node is player controlled and does not contain a probability set
	 * @throws IllegalArgumentException
	 * 			  if each probability set does not sum to 1.0
	 */		
	public DialogNode(boolean isNPC, String text, HashMap<Integer, Double[]> probSets ,DialogNode[] children, double x, double y) { 
		if (text == null)
			throw new IllegalArgumentException("Nodes must have text.");
		this.text = text;
		if (children.length > 0 && probSets.size() == 0 && !isNPC)
			throw new IllegalArgumentException("Player-controlled node '" + text + "' has children and therefore must have at least one probability set");
		checkProbSets(probSets);
		
		this.children = children;
		this.isNPC = isNPC;
		this.probSets = probSets;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * If probability set is not empty, checks for default (0) set.
	 * Also makes sure each set sums to 1.0.
	 * 
	 * @param probSets probability sets to be checked
	 */
	private void checkProbSets(HashMap<Integer, Double[]> probSets) {
		if (!probSets.containsKey(0) && probSets.size() > 0)
			throw new IllegalArgumentException("Node '" + text + "' has a probability set but does not have a probability set for the default (0) strategy.");
		for (int strategy : probSets.keySet()) {
			double sum = 0.0;
			for (double value : probSets.get(strategy))
				sum += value;
			if (sum > 1.01 || sum < 0.99)
				throw new IllegalArgumentException("Probability set (strategy " + Integer.toString(strategy) + ") of node '" + text + "' must sum to 1.0.");
		}
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
	public HashMap<Integer, Double[]> getProbSets() {
		return probSets;
	}
	
	/**
	 * Gets horizontal position of node in a graphical editor application.
	 * 
	 * @return horizontal position
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Gets vertical position of node in a graphical editor application.
	 * 
	 * @return vertical position
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Sets this node's text.
	 * 
	 * @param text text of this node
	 */
	void setText(String text) {
		if (text == null)
			throw new IllegalArgumentException("Node '" + this.text + "' must have text.");
		this.text = text;
	}
	
	/**
	 * Sets this node's probability set(s).
	 * 
	 * @param probSets probability set(s) for this node
	 * @throws IllegalArgumentException if each probability set does not sum to 1.0.
	 */	
	public void setProbSets(HashMap<Integer, Double[]> probSets) {
		checkProbSets(probSets);		
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
	
	/**
	 * Sets horizontal position of node in a graphical editor application.
	 * 
	 * @param x horizontal position
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * Sets vertical position of node in a graphical editor application.
	 * 
	 * @param y vertical position
	 */
	public void setY(double y) {
		this.y = y;
	}
}