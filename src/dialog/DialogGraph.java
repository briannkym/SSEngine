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
 * Methods for starting, creating, editing, saving, and loading dialog graphs.
 * 
 * @author Mark Groeneveld
 * @author Brian Nakayama
 * @version 0.5
 */

//TODO when to use this. when accessing 
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JOptionPane;

//TODO Surround calls to methods with try catch to handle exceptions.
public class DialogGraph {
	private HashMap<String, DialogNode> nodeMap = new HashMap<String, DialogNode>();
	
	//TODO Completely redo
//	public DialogNode[] load(String fileName) {
//		int numNodes = 0;
//		
//		File file = new File("resources/dialogs/" + fileName);
//		Scanner scanner = null;
//		try {
//			scanner = new Scanner(file);
//		} catch (FileNotFoundException e) {
//			JOptionPane.showMessageDialog(null, "File: " + fileName + " not found", null, JOptionPane.PLAIN_MESSAGE);
//		}
//		
//		numNodes = scanner.nextInt();
//			nodes = new DialogNode[numNodes];
//		for (int i = 0; i < numNodes; i++) {
//			String temp = scanner.nextLine();
//			String text = scanner.nextLine();
//			int numResponses = scanner.nextInt();
//			int[] followingNodeOptions = new int[numResponses];
//			String[] responseText = new String[numResponses];
//			for (int j = 0; j < numResponses; j++) {
//				followingNodeOptions[j] = scanner.nextInt();
//				responseText[j] = scanner.nextLine().substring(1);
//			}
//				nodes[i] = new DialogNode(text, followingNodeOptions);
//		}
//		
//		scanner.close();	
//			return nodes;
//	}
	
	//TODO
	public void save() {
		
	}
	
	public static String[] ar(String... elems) {
	    return elems;
	}
	
	public static double[] ar(double... elems) {
	    return elems;
	}
	
	public static double[][] ar2(double[]... elems) {
	    return elems;
	}
	
	public void test() {
		createNode(true, "initial", ar2(ar(0.5, 0.5), ar(1.0, 0.0)), ar("hi", "hello"));		
		createNode(true, "hi", null, ar("yo", "what's up"));		
		createNode(true, "hello", null, null);
		createNode(false, "yo", ar2(ar(0.1, 0.7, 0.2)), ar("hey", "...", "yoyoyo"));
		createNode(false, "what's up", ar2(ar(1)), ar("not much"));		
		createNode(true, "hey", null, null);		
		createNode(true, "...", null, null);		
		createNode(true, "yoyoyo", null, null);		
		createNode(true, "not much", null, ar("say hello"));		
		createNode(false, "say hello", ar2(ar(1)), ar("hello"));
		
//		changeNodeProbSet("initial", ar2(ar(0.3, 0.7), ar(0.8, 0.2)));
//		changeNodeChildren("initial", ar("hi", "hello", "..."), ar2(ar(0.5, 0.4, 0.1)));
//		switchNodeNPC("...");
//		removeNode("...");
//		changeNodeText("...", "iii");
				
//		start();
	}
	
	//TODO I could pass in a method as an argument for displaying the text in-game.
	public void start() {
		DialogNode cn = nodeMap.get("initial"); //current node
		
		//exits when the current node no longer has children
		while (true) {
			if (cn.isNPC()) {
				if (cn.getText() == "initial"){
					cn = NPCNextResponse(cn);
				}
				else {
					//NPC response
					System.out.println(cn.getText());
					if (cn.getChildren() == null)
						break;
					//player next response list
					for (int c = 0; c < cn.getChildren().length; c++) {
						System.out.println(Integer.toString(c) + " " + nodeMap.get(cn.getChildren()[c]).getText());
					}
					//gets user selection of response and updates current node
					String response = JOptionPane.showInputDialog(null, "Response", "Response", JOptionPane.PLAIN_MESSAGE);
					cn = nodeMap.get(cn.getChildren()[Integer.parseInt(response)]);
				}
			}
			else {
				//Player response
				System.out.println(cn.getText());
				if (cn.getChildren() == null)
					break;
				cn = NPCNextResponse(cn);
			}
		}
	}
	
	//NPC next response selection
	private DialogNode NPCNextResponse(DialogNode cn) {
		int strategy = 0; //TODO changeable strategies
		double rf = Math.random(); //random factor
		double floor = 0;
		double ceiling = 0;
		for (int i = 0; i < cn.getChildren().length; i++) {
			ceiling += cn.getProbSet()[strategy][i];
			if ( rf >= floor && rf < ceiling)
				return nodeMap.get(cn.getChildren()[i]);
			floor += ceiling;
		}
		return null;
	}
	
	public void createNode(boolean npc, String text, double[][] pSet, String[] children) {
		if (nodeMap.put(text, new DialogNode(npc, text, pSet, children)) != null)
			throw new IllegalArgumentException("A node with that text already exists in this dialog.");
	}
	
	public void removeNode(String text) {
		if (nodeMap.remove(text) == null)
			throw new IllegalArgumentException("A node with that text does not exist in this dialog.");
		
		//Removes all references to removed node
		Collection<DialogNode> collection = nodeMap.values();
		Object[] dnArray = collection.toArray();
		for (int i = 0; i < dnArray.length; i++) { //Iterates over every node
			DialogNode n = (DialogNode) dnArray[i];
			if (n.getChildren() != null) //If node has children
				for (int c = 0; c < n.getChildren().length; c++) { //Iterates over that node's children
					if (n.getChildren()[c].equals(text)) { //If child matches removed node
						String[] newChildren = new String[n.getChildren().length - 1]; //New child array for parent node
						for (int j = 0; j < n.getChildren().length - 1; j++) { //Creates new child array
							if (j < c)
								newChildren[j] = n.getChildren()[j];
							else
								newChildren[j] = n.getChildren()[j+1];
						}
						n.changeChildren(newChildren);
						if (n.getProbSet() != null)
							JOptionPane.showMessageDialog(null, "Make sure to change probability sets of node: " + n.getText(), "Alert", JOptionPane.PLAIN_MESSAGE);
					}
				}
		}
	}
	
	public void changeNodeText(String oldText, String newText) {
		DialogNode n1 = nodeMap.get(oldText);
		if (n1 == null)
			throw new IllegalArgumentException("A node with that text does not exist in this dialog.");
		if (nodeMap.get(newText) != null)
			throw new IllegalArgumentException("A node with that new text already exist in this dialog.");
		n1.changeText(newText);
		nodeMap.put(newText, n1);
		nodeMap.remove(oldText);
		
		//Changes all references to removed node
		Collection<DialogNode> collection = nodeMap.values();
		Object[] dnArray = collection.toArray();
		for (int i = 0; i < dnArray.length; i++) { //Iterates over every node
			DialogNode n = (DialogNode) dnArray[i];
			if (n.getChildren() != null) //If node has children
				for (int c = 0; c < n.getChildren().length; c++) { //Iterates over that node's children
					if (n.getChildren()[c].equals(oldText)) { //If child matches removed node
						String[] newChildren = n.getChildren(); //New child array for parent node
						newChildren[c] = newText;
						n.changeChildren(newChildren);
					}
				}
		}
	}
	
	public void switchNodeNPC(String text) {
		DialogNode n = nodeMap.get(text);
		if (n.getChildren() != null && n.getProbSet() == null && n.isNPC())
			throw new IllegalArgumentException("Player-controlled nodes with children must have probability arrays");			
		n.switchNPC();
	}
	
	public void changeNodeProbSet(String text, double[][] newProbSet) {
		DialogNode n = nodeMap.get(text);
		if (n == null)
			throw new IllegalArgumentException("A node with that text does not exist in this dialog.");
		if (newProbSet != null) {
			for (int s = 0; s < newProbSet.length; s++) {
				double sum = 0;
				for (int c = 0; c < newProbSet[s].length; c++) {
					sum += newProbSet[s][c];
					if (n.getChildren() != null && newProbSet != null)
						if (n.getChildren().length != newProbSet[s].length)
							throw new IllegalArgumentException("Probability array " + Integer.toString(s) + " is not of same length as children.");
				}
				if (sum > 1.01 || sum < 0.99)
					throw new IllegalArgumentException("Each probability array must sum to 1.");
			}
		}
		n.changeProbSet(newProbSet);
	}
	
	public void changeNodeChildren(String text, String[] newChildren) {
		DialogNode n = nodeMap.get(text);
		if (n == null)
			throw new IllegalArgumentException("A node with that text does not exist in this dialog.");
//		if (n.getProbSet() != null && n.getChildren().length != newChildren.length)
//			throw new IllegalArgumentException("This node has a probability set and the number of it children is being changed. The probability set must also be changed.");
		n.changeChildren(newChildren);
	}
	
//	public void changeNodeChildren(String text, String[] newChildren, double[][] newProbSet) {
//		DialogNode n = nodeMap.get(text);
//		if (n == null)
//			throw new IllegalArgumentException("A node with that text does not exist in this dialog.");
//		if (newProbSet != null) {
//			for (int s = 0; s < newProbSet.length; s++) {
//				double sum = 0;
//				for (int c = 0; c < newProbSet[s].length; c++) {
//					sum += newProbSet[s][c];
//					if (newChildren != null && newProbSet != null)
//						if (newChildren.length != newProbSet[s].length)
//							throw new IllegalArgumentException("Probability array " + Integer.toString(s) + " is not of same length as children.");
//				}
//				if (sum > 1.01 || sum < 0.99)
//					throw new IllegalArgumentException("Each probability array must sum to 1.");
//			}
//		}	
//		n.changeChildren(newChildren);
//		if (n.getProbSet() != null)
//			n.changeProbSet(newProbSet);
//	}
	
	//TODO check for player nodes following player nodes, or NPC nodes following NPC nodes that are not initial
	//TODO run before saving any dialog
//	public void check() {
//		Collection<DialogNode> collection = nodeMap.values();
//		DialogNode[] dnArray = (DialogNode[]) collection.toArray();
//		if (children != null)
//			for (int i = 0; i < children.length; i++)
//				if (nodeMap.containsKey(children[i])) {
//					if (npc && nodeMap.get(children[i]).isNPC())
//						if (1 == JOptionPane.showConfirmDialog(null, "Child " + Integer.toString(i) + " is also an NPC. Continue?", null, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE))
//							throw new IllegalArgumentException();
//					if (!npc && !nodeMap.get(children[i]).isNPC())
//						if (1 == JOptionPane.showConfirmDialog(null, "Child " + Integer.toString(i) + " is also a player. Continue?", null, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE))
//							throw new IllegalArgumentException();
//				}
//	//TODO check if all probset lengths match children lengths
	//TODO check if each node's children are of same npc type
	//TODO check to make sure one initial node exists
//	}
	
	public HashMap<String, DialogNode> getMap() {
		return nodeMap;
	}
}