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
 * @author Brian Nakayama
 * @version 1.3
 */

public class DialogNode {
	private String[] children;
	private String text;
	private HashMap<Integer, Double[]> probSets;
	private boolean isNPC;
	private double x, y;
	
	/**
	 * Constructs a DialogNode with the specified properties.
	 * 
	 * @param isNPC
	 *            Is this node spoken by an NPC (Non-Playable Character)?
	 * @param text
	 *            Text to be spoken. The unique string "initial" indicates a dialog's starting node.
	 *            This node will not be spoken, but leads to one or more other nodes which are.
	 * @param probSets
	 *            Map of probabilities the NPC uses to select a child node.
	 *            There may be multiple arrays corresponding to different strategies.
	 *            Map key is the strategy, array index is child.
	 *            Each array should sum to 1 and should be of same length as children.
	 *            Each probability set is also described as this node's row of the discrete time Markov chain's transition matrix, 
	 *            the part of which is nonzero for all strategies.
	 *            An empty map indicates no probability set. Do not use a null object.  
	 * @param children
	 *            Array of children nodes. Nodes are addressed by their text (i.e. a node's .getText() is its HashMap key).
	 *            If children is null this is an end node. 
	 * @param x
	 * 			  Node's horizontal position in DialogMaker.
	 * @param y
	 * 			  Node's vertical position in DialogMaker.
	 * @throws IllegalArgumentException
	 * 			  if there is no text.
	 * @throws IllegalArgumentException
	 * 			  if node is player controlled and does not contain the default strategy (key = 0) probability set. 
	 * @throws IllegalArgumentException
	 * 			  if node has probability set but no children to apply it to.
	 * @throws IllegalArgumentException
	 * 			  if node is player controlled and does not contain a probability set.
	 * @throws IllegalArgumentException
	 * 			  if each probability set does not sum to 1.
	 * @throws IllegalArgumentException
	 * 			  if a probability set is not of same length as children.
	 */		
	public DialogNode(boolean isNPC, String text, HashMap<Integer, Double[]> probSets ,String[] children, double x, double y) { 
		if (text == null)
			throw new IllegalArgumentException("Nodes must have text.");
		if (!probSets.containsKey(0) && probSets.size() > 0)
			throw new IllegalArgumentException("Node must contain a probability set for the default (0) strategy.");
		if (children == null && probSets.size() > 0)
			throw new IllegalArgumentException("Nodes with probability sets must have children");
		if (children != null && probSets.size() == 0 && !isNPC)
			throw new IllegalArgumentException("Player-controlled nodes with children must at least one probability set");
		if (probSets.size() > 0) {
			Object[] working = probSets.keySet().toArray();
			int[] strategies = new int[probSets.size()];
			for (int i = 0; i < probSets.size(); i++)
				strategies[i] = (int) working[i];
			for (int s : strategies) {
				double sum = 0;
				for (int c = 0; c < probSets.get(s).length; c++) {
					sum += probSets.get(s)[c];
					if (children != null)
						if (children.length != probSets.get(s).length)
							throw new IllegalArgumentException("Probability array " + Integer.toString(s) + " is not of same length as children.");
				}
				if (sum > 1.01 || sum < 0.99)
					throw new IllegalArgumentException("Each probability array must sum to 1.");
			}
		}
		
		this.children = children;
		this.text = text;
		this.isNPC = isNPC;
		this.probSets = probSets;
		this.x = x;
		this.y = y;
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
	public String[] getChildren() {
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
		this.text = text;
	}
	
	/**
	 * Sets this node's probability set(s).
	 * 
	 * @param probSets probability set(s) for this node
	 * @throws IllegalArgumentException if each probability set does not sum to 1.0.
	 */	
	public void setProbSets(HashMap<Integer, Double[]> probSets) {
		Object[] array = probSets.values().toArray();
		for (int s = 0; s < probSets.size(); s++) {
			double sum = 0.0;
			for (int c = 0; c < ((Double[]) array[s]).length; c++)
				sum += ((Double[]) array[s])[c];
			if (sum > 1.01 || sum < 0.99)
				throw new IllegalArgumentException("Each probability set must sum to 1.0.");
		}
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
	public void setChildren(String[] children) {
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