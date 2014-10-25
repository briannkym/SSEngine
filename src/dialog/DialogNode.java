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

/**
 * Node data structure for implementing dialog graphs.
 * 
 * @author Mark Groeneveld
 * @author Brian Nakayama
 * @version 1.0
 */

public class DialogNode {
	private String[] children;
	private String text;
	private double[][] probSet;
	private boolean npc;
	private double x, y;
	
	/**
	 * Constructor
	 * 
	 * @param npc
	 *            Is this node spoken by an NPC (Non-Playable Character)?
	 * @param text
	 *            Text to be spoken. The unique string "initial" indicates a dialog's starting node.
	 *            This node will not be spoken, but leads to one or more other nodes which are.
	 * @param probSet
	 *            Array of probabilities the NPC uses to select a child node.
	 *            There may be multiple arrays corresponding to different strategies.
	 *            First index is strategy, second index is child.
	 *            Each array should sum to 1 and should be of same length as children.
	 *            This variable is also described as this node's row of the discrete time Markov chain's transition matrix, the part of which is nonzero for all strategies.  
	 * @param ChildrenIn
	 *            Array of children nodes. Nodes are addressed by their text (i.e. a node's .getText() is its HashMap key).
	 *            If children is null this is an end node. 
	 */		
	public DialogNode(boolean npc, String text, double[][] probSet ,String[] children) { 
		if (text == null)
			throw new IllegalArgumentException("Nodes must have text.");
		if (children == null && probSet != null)
			throw new IllegalArgumentException("Nodes with probability arrays must have children");
		if (children != null && probSet == null && !npc)
			throw new IllegalArgumentException("Player-controlled nodes with children must have probability arrays");
		if (probSet != null) {
			for (int s = 0; s < probSet.length; s++) {
				double sum = 0;
				for (int c = 0; c < probSet[s].length; c++) {
					sum += probSet[s][c];
					if (children != null && probSet != null)
						if (children.length != probSet[s].length)
							throw new IllegalArgumentException("Probability array " + Integer.toString(s) + " is not of same length as children.");
				}
				if (sum > 1.01 || sum < 0.99)
					throw new IllegalArgumentException("Each probability array must sum to 1.");
			}
		}
		
		this.children = children;
		this.text = text;
		this.npc = npc;
		this.probSet = probSet;
//		x = xIn;
//		y = yIn;
	}
	
	public String getText() {
		return text;
	}
	
	public String[] getChildren() {
		return children;
	}
	
	public boolean isNPC() {
		return npc;
	}
	
	public double[][] getProbSet() {
		return probSet;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	void changeText(String text) {
		this.text = text;
	}
	
	void changeProbSet(double[][] probSet) {
		this.probSet = probSet;
	}
	
	void switchNPC() {
		if (npc)
			npc = false;
		else
			npc = true;
	}
	
	void changeChildren(String[] children) {
		this.children = children;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
}