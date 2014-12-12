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

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.xml.sax.*;
import org.w3c.dom.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * This class contains methods for creating, changing, saving,
 * loading, and validating dialog graphs.
 * 
 * A dialog graph (dialog or graph for short) is a directed
 * graph representing a game dialog between a player and a
 * non-player character (NPC). Each vertex (node) of the graph
 * (a DialogNode) represents something spoken by a player
 * or non-player character. The edges represent parent/child
 * relationships between the nodes.
 * 
 * The dialog starts at the
 * 'initial' node and moves forward through other nodes based
 * on player choices and NPC choices. The NPC choices are
 * probabilistic. The dialog ends when a node with no children
 * is reached.
 * 
 * Each node may have multiple sets of probabilities for
 * determining child selection. These correspond to different
 * 'strategies'.
 * 
 * Each node is held in a hash table with the node's text as its
 * key. 
 * 
 * @author Mark Groeneveld
 * @version 1.0
 */
//TODO Nodes should be able to change environmental flags.
//TODO Finish class javadoc.
public class DialogGraph {
	/**
	 * Map of nodes in this dialog graph. A node's key is its text.
	 */
	private HashMap<String, DialogNode> nodeMap = new HashMap<String, DialogNode>(50);
	
	/**
	 * Loads dialog graph from file.
	 * 
	 * @param filename name of save file in the resources/dialogs/ directory
	 * @throws IllegalStateException if a node's child does not exist 
	 */
	public void load(String filename) {
		HashMap<DialogNode, String[]> childrenTextMap = new HashMap<DialogNode, String[]>();		
		
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new File(filename));
			Element doc = dom.getDocumentElement();
			
			//Iterates over each DialogNode element.
			NodeList nodeList = doc.getElementsByTagName("DialogNode");
			for (int n = 0; n < nodeList.getLength(); n++) {
				Element dialogNodeElement = (Element) nodeList.item(n);
				
				//Gets the node's text, NPC status, x, and y data.
				String text = dialogNodeElement.getElementsByTagName("Text").item(0).getTextContent();
				boolean isNPC = Boolean.parseBoolean(dialogNodeElement.getElementsByTagName("NPC").item(0).getTextContent());
//				Double x = Double.parseDouble(dialogNodeElement.getElementsByTagName("X").item(0).getTextContent());
//				Double y = Double.parseDouble(dialogNodeElement.getElementsByTagName("Y").item(0).getTextContent());
				
				//Gets the node's children. For now they are saved as strings.
				//Later they are added to the node as DialogNodes.
				NodeList childrenNodeList = dialogNodeElement.getElementsByTagName("Child");
				String[] children;
				children = new String[childrenNodeList.getLength()];
				for (int c = 0; c < childrenNodeList.getLength(); c++)
					children[c] = childrenNodeList.item(c).getTextContent();
				
				//Gets the node's probability sets.
				NodeList probSetNodeList = dialogNodeElement.getElementsByTagName("ProbSet");
				ArrayList<double[]> probSet = new ArrayList<double[]>();
				for (int s = 0; s < probSetNodeList.getLength(); s++) {
					Element probSetElement = (Element) probSetNodeList.item(s);
//					int strategy = Integer.parseInt(probSetElement.getElementsByTagName("strategy").item(0).getTextContent());
					NodeList valuesNodeList = probSetElement.getElementsByTagName("value");
					double[] individualSet = new double[valuesNodeList.getLength()];
					for (int v = 0; v < valuesNodeList.getLength(); v++)
						individualSet[v] = Double.parseDouble(valuesNodeList.item(v).getTextContent());
					probSet.add(individualSet);
				}
				
				//Creates the node and adds it to the dialog.
				DialogNode[] emptyArray = new DialogNode[0];
				DialogNode node = new DialogNode(isNPC, text, probSet, emptyArray);
				addNode(node);
				
				//Saves node's children for later addition.
				childrenTextMap.put(node, children);
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//Adds children to nodes
		for (DialogNode n : nodeMap.values()) {
			DialogNode[] children = new DialogNode[childrenTextMap.get(n).length];
			for (int child = 0; child < childrenTextMap.get(n).length; child++) {
				children[child] = nodeMap.get(childrenTextMap.get(n)[child]);
				if (children[child] == null)
					throw new IllegalStateException("Child '" + childrenTextMap.get(n)[child] + "' of node '" + n.getText() + "' does not exist in this dialog.");
			}			
			n.setChildren(children);
		}
	}
	
	/**
	 * Saves dialog graph to file.
	 * 
	 * @param filename name of save file in the resources/dialogs/ directory
	 */
	public void save(String filename) {
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();			
			Element root = dom.createElement("NodeList");
			
			//Iterates over every node in the dialog.
			for (DialogNode n : nodeMap.values()) {
				Element el, el2;
				el = dom.createElement("DialogNode");
				
				//Saves the node's text.
				el2 = dom.createElement("Text");
				el2.appendChild(dom.createTextNode(n.getText()));
				el.appendChild(el2);
				
				//Saves the node's NPC status.
				el2 = dom.createElement("NPC");
				el2.appendChild(dom.createTextNode(Boolean.toString(n.getIsNPC())));
				el.appendChild(el2);
//				
//				//Saves the node's x position.
//				el2 = dom.createElement("X");
//				el2.appendChild(dom.createTextNode(Double.toString(n.getX())));
//				el.appendChild(el2);
//				
//				//Saves the node's y position.
//				el2 = dom.createElement("Y");
//				el2.appendChild(dom.createTextNode(Double.toString(n.getY())));
//				el.appendChild(el2);
				
				//Iterates over the node's children and saves them.
				for (DialogNode child : n.getChildren()) {
					el2 = dom.createElement("Child");
//					el2.setAttribute("n", Integer.toString(c));
					el2.appendChild(dom.createTextNode(child.getText()));
					el.appendChild(el2);
				}
				
				//Iterates over the node's probability sets.
				for (double[] strategy : n.getProbSets()) {
					Element el3;
					el2 = dom.createElement("ProbSet");
					
					el3 = dom.createElement("strategy");
//					el3.appendChild(dom.createTextNode(Integer.toString(strategy)));
//					el2.appendChild(el3);
					
					//Iterates over the probability set's individual elements and saves them.
					for (int v = 0; v < strategy.length; v++) {
						el3 = dom.createElement("value");
						el3.setAttribute("n", Integer.toString(v));
						el3.appendChild(dom.createTextNode(Double.toString(strategy[v])));
						el2.appendChild(el3);
					}
					el.appendChild(el2);
				}
				
				root.appendChild(el);
			}
			dom.appendChild(root);
			
			try {
	            Transformer tr = TransformerFactory.newInstance().newTransformer();
	            tr.setOutputProperty(OutputKeys.INDENT, "yes");
	            tr.setOutputProperty(OutputKeys.METHOD, "xml");
	            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	            //Saves file.
	            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(new File(filename))));
	        } catch (TransformerException e) {
	            System.out.println(e.getMessage());
	        } catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the flow of a dialog. Offers choices to player and responds for NPC.
	 */
	//TODO Still have to figure out how this should interact with a game.
	//TODO I could pass in a method as an argument for displaying the text in-game. Would require everyone to update to java 8 for lambda expressions. Probably not worth it.
	public void start() {
		DialogNode cn = nodeMap.get("initial"); //current node
		
		//exits when the current node no longer has children
		while (true) {
			if (cn.getIsNPC()) {
				if (cn.getText() == "initial"){
					cn = npcNodeChoice(cn);
				}
				else {
					//NPC response
					System.out.println(cn.getText());
					if (cn.getChildren().length == 0)
						break;
					//player next response list
					for (int child = 0; child < cn.getChildren().length; child++)
						System.out.println(Integer.toString(child) + " " + cn.getChildren()[child].getText());
					//gets user selection of response and updates current node
					String response = JOptionPane.showInputDialog(null, "Response", "Response", JOptionPane.PLAIN_MESSAGE);
					cn = cn.getChildren()[Integer.parseInt(response)];
				}
			}
			else {
				//Player response
				System.out.println(cn.getText());
				if (cn.getChildren().length == 0)
					break;
				cn = npcNodeChoice(cn);
			}
		}
	}
	
	/**
	 * Probabilistically decides which node the NPC chooses next.
	 * 
	 * @param currentNode current node of dialog
	 * @return next node
	 */
	private DialogNode npcNodeChoice(DialogNode currentNode) {
		//TODO Each graph needs to have independent strategies.
		//TODO Changeable strategies.
		int strategy = 0;
		double randomDouble = Math.random();
		double floor = 0;
		double ceiling = 0;
		for (int c = 0; c < currentNode.getChildren().length; c++) {
			ceiling += currentNode.getProbSets().get(strategy)[c];
			if ( randomDouble >= floor && randomDouble < ceiling)
				return currentNode.getChildren()[c];
			floor += ceiling;
		}
		return null;
	}
	
	/**
	 * Adds a node to the dialog graph.
	 * 
	 * @param node node to be added
	 * @throws IllegalArgumentException if a node with that text already exists
	 */
	public void addNode(DialogNode node) {
		if (nodeMap.put(node.getText(), node) != null)
			throw new IllegalArgumentException("A node with that text already exists.");
	}
	
	/**
	 * Removes a node from the dialog graph.
	 * 
	 * @param node node to be removed
	 * @throws IllegalArgumentException if that node does not exist
	 */
	public void removeNode(DialogNode node) {
		if (nodeMap.remove(node.getText()) == null)
			throw new IllegalArgumentException("That node does not exist.");
		
		//Removes all references to removed node
		//Iterates over every node
		for (DialogNode n : nodeMap.values())
			//Iterates over that node's children
			for (int child = 0; child < n.getChildren().length; child++)
				//If child matches removed node
				if (n.getChildren()[child].equals(node.getText())) {
					//Build new children array
					DialogNode[] newChildren = new DialogNode[n.getChildren().length - 1];
					for (int i = 0; i < n.getChildren().length - 1; i++) {
						if (i < child)
							newChildren[i] = n.getChildren()[i];
						else
							newChildren[i] = n.getChildren()[i+1];
					}
					n.setChildren(newChildren);
				}
	}
	
	/**
	 * Changes a node's text. This method is in DialogGraph instead of DialogNode because
	 * a node's text is its nodeMap key. So the node needs to be removed and re-added to the map. 
	 * 
	 * @param node node whose text is being changed
	 * @param text node's new text
	 * @throws IllegalArgumentException if a node with that new text already exists.
	 */
	public void changeNodeText(DialogNode node, String text) {
		DialogNode.checkText(text);
		if (nodeMap.get(text) != null)
			throw new IllegalArgumentException("A node with that new text already exists.");
		
		nodeMap.remove(node.getText());
		node.setText(text);
		nodeMap.put(text, node);
	}
	
//Method for changing a node's children that checks if children exists. Not sure if I want to use this.
//	public void changeNodeChildren(DialogNode node, DialogNode[] children) {
//		for (DialogNode child : children)
//			if (child == null || nodeMap.get(child.getText()) == null)
//				throw new IllegalStateException("Child '" + child.getText() + "' of node '" + node.getText() + "' does not exist in this dialog.");
//	
//		node.setChildren(children);
//	}
	
	/**
	 * Runs various checks making sure the graph is valid.
	 * 
	 * @return errors
	 */
	public ArrayList<String> check() {
		boolean endNodeExists = false;
		ArrayList<String> errorList = new ArrayList<String>();
		for (DialogNode n : nodeMap.values()) {
			//Checks for player nodes following player nodes.
			if (!n.getIsNPC())
				for (DialogNode child : n.getChildren())
					if (!child.getIsNPC())
						errorList.add("Node '" + n.getText() + "': is player-controlled and has a child '" + child.getText() + "' which is also a player node.");
			
			//Checks for NPC nodes following an NPC node that is not "initial".
			if (n.getIsNPC() && !n.getText().equals("initial"))
				for (DialogNode child : n.getChildren())
					if (child.getIsNPC())
						errorList.add("Node '" + n.getText() + "': is non-player-controlled and has a child '" + child.getText() + "' which is also an NPC node.");
			
			//Checks that each probability set sums to 1.0.
			//Checks that player-controlled nodes with children have probSets.
			//Checks that probSet lengths match children length.
			try {
				DialogNode.checkProbSets(n.getChildren(), n.getProbSets(), n.getIsNPC());
			} catch (IllegalArgumentException e) {
				errorList.add("Node '" + n.getText() + "':" + e.getMessage());
			}
			
			//Makes sure an end node exists. Part 1
			if (n.getChildren().length == 0)
				endNodeExists = true;
			
			//TODO check for non-existent children
			//TODO check for nodes without parents other than initial
			//TODO check for mixed node types following 'initial'
			//TODO check that children is not null, empty array should be used instead
		}
		
		//Makes sure an end node exists. Part 2
		if (!endNodeExists)
			errorList.add("Error, there is no end node in this graph.");
		
		//Makes sure an initial node exists.
		if (!nodeMap.containsKey("initial"))
			errorList.add("Error, there is no initial node in this graph.");
		
		return errorList;
	}
	
	/**
	 * Gets the nodes in this dialog graph.
	 * 
	 * @return map of nodes in this dialog graph
	 */
	public HashMap<String, DialogNode> getGraph() {
		return nodeMap;
	}
}