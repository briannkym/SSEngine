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
 * Example of player character.
 * 
 * @author Mark Groeneveld
 * @author Brian Nakayama
 * @version 0.5
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class DialogGraph {
//	private DialogNode[] nodes;
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
	
	static String[] ar(String... elems) {
	    return elems;
	}
	
	static double[] ar(double... elems) {
	    return elems;
	}
	
	static double[][] ar2(double[]... elems) {
	    return elems;
	}
	
	private void newNode(boolean npc, String text, double[][] pSet, String[] children) {
		DialogNode dn = new DialogNode(npc, text, pSet, children);
		nodeMap.put(dn.getText(), dn);
	}
	
	public void test() {
//		nodes = new DialogNode[5];
//		
//		int[] children0 = {1,2};
//		double[] PSet0 = {0.5, 0.5};
//		nodes[0] = new DialogNode(children0, PSet0, null, true);
//		
//		int[] children1 = {3,4};
//		nodes[1] = new DialogNode(children1, null, "hi", true);
//		
//		nodes[2] = new DialogNode(null, null, "hello", true);
//
//		nodes[3] = new DialogNode(null, null, "yo", false);
//
//		nodes[4] = new DialogNode(null, null, "what's up", false);
//		
//		start();
				
		newNode(true, "initial", ar2(ar(0.0, 1.0), ar(1.0, 0.0)), ar("hi", "hello"));		
		newNode(true, "hi", null, ar("yo", "what's up"));		
		newNode(true, "hello", null, null);
		newNode(false, "yo", ar2(ar(0.1, 0.7, 0.2)), ar("hey", "...", "yoyoyo"));
		newNode(false, "what's up", ar2(ar(1)), ar("not much"));		
		newNode(true, "hey", null, null);		
		newNode(true, "...", null, null);		
		newNode(true, "yoyoyo", null, null);		
		newNode(true, "not much", null, ar("say hello"));		
		newNode(false, "say hello", ar2(ar(1)), ar("hello"));
		
		start();
	}
	
	//TODO I could pass in a method as an argument for displaying the text in-game.
	public void start() {
		DialogNode cn = nodeMap.get("initial"); //current node
		
//		//runs while the current node has children
//		while (true) {
//			if (nodes[cn].isNPC()) {
//				if (nodes[cn].getText() == null){
//					cn = NPCNextResponse(cn);
//				}
//				else {
//					//NPC response
//					System.out.println(nodes[cn].getText());
//					if (nodes[cn].getChildren() == null)
//						break;
//					//player next response list
//					for (int c = 0; c < nodes[cn].getChildren().length; c++) {
//						System.out.println(Integer.toString(c) + " " + nodes[nodes[cn].getChildren()[c]].getText());
//					}
//					//gets user selection of response and updates current node
//					String response = JOptionPane.showInputDialog(null, "Response", "Response", JOptionPane.PLAIN_MESSAGE);
//					cn = nodes[cn].getChildren()[Integer.parseInt(response)];
//				}
//			}
//			else {
//				//Player response
//				System.out.println(nodes[cn].getText());
//				if (nodes[cn].getChildren() == null)
//					break;
//				cn = NPCNextResponse(cn);
//			}
//		}
		
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
//		//TODO strategy based selection
//		//probabilistic selection
//		double rf = Math.random(); //random factor
//		double floor = 0;
//		double ceiling = 0;
//		for (int i = 0; i < nodes[cn].getChildren().length; i++) {
//			ceiling += nodes[cn].getPSet()[i];
//			if ( rf >= floor && rf < ceiling)
//				return nodes[cn].getChildren()[i];
//			floor += ceiling;
//		}
//		return 0;
		
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
		return cn; //TODO throw error here
	}
	
	//TODO add "are you sure" dialogs when adding an NPC node as a child of another NPC node, or a player node after another player node
	public void createNode() {
	}
	
	//TODO search for references to removed node and confirm removing them
	public void removeNode() {
		
	}
	
	//TODO search for references to this node and change those as well
	public void changeNodeText() {
		
	}
	
	//TODO add "are you sure" dialogs when adding an NPC node as a child of another NPC node, or a player node after another player node
	public void changeNodeNPC() {
		
	}
	
	public void changeNodePSet() {
		
	}
	
	public void changeNodeChildren() {
		
	}
}