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
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * Methods for creating, changing, saving, and loading dialog graphs.
 * 
 * @author Mark Groeneveld
 * @version 1.0
 */

//TODO better comments
public class DialogGraph {
	private HashMap<String, DialogNode> nodeMap = new HashMap<String, DialogNode>(50);
	
	
	public void load(String filename) {
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new File(filename));		
			Element doc = dom.getDocumentElement();
			
			NodeList nodeList = doc.getElementsByTagName("DialogNode");
			for (int n = 0; n < nodeList.getLength(); n++) {
				Element dialogNodeElement = (Element) nodeList.item(n);
				
				String text = dialogNodeElement.getElementsByTagName("Text").item(0).getTextContent();
				boolean isNPC = Boolean.parseBoolean(dialogNodeElement.getElementsByTagName("NPC").item(0).getTextContent());
				Double x = Double.parseDouble(dialogNodeElement.getElementsByTagName("X").item(0).getTextContent());
				Double y = Double.parseDouble(dialogNodeElement.getElementsByTagName("Y").item(0).getTextContent());
				
				NodeList childrenNodeList = dialogNodeElement.getElementsByTagName("Child");
				String[] children;
				if (childrenNodeList.getLength() == 0)
						children = null;
				else
					children = new String[childrenNodeList.getLength()];
				for (int c = 0; c < childrenNodeList.getLength(); c++)
					children[c] = childrenNodeList.item(c).getTextContent();
				
				NodeList probSetNodeList = dialogNodeElement.getElementsByTagName("ProbSet");
				HashMap<Integer, Double[]> probSet = new HashMap<Integer, Double[]>();
				for (int s = 0; s < probSetNodeList.getLength(); s++) {
					Element probSetElement = (Element) probSetNodeList.item(s);
					int strategy = Integer.parseInt(probSetElement.getElementsByTagName("strategy").item(0).getTextContent());
					NodeList valuesNodeList = probSetElement.getElementsByTagName("value");
					Double[] individualSet = new Double[valuesNodeList.getLength()];
					for (int v = 0; v < valuesNodeList.getLength(); v++)
						individualSet[v] = Double.parseDouble(valuesNodeList.item(v).getTextContent());
					probSet.put(strategy, individualSet);
				}
				
				addNode(new DialogNode(isNPC, text, probSet, children, x, y));
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void save(String filename) {
		Document dom;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.newDocument();			
			Element root = dom.createElement("NodeList");
			
			for (Object o : nodeMap.values().toArray()) {
				Element el, el2;
				DialogNode n = (DialogNode) o;
				el = dom.createElement("DialogNode");
				
				el2 = dom.createElement("Text");
				el2.appendChild(dom.createTextNode(n.getText()));
				el.appendChild(el2);
				
				el2 = dom.createElement("NPC");
				el2.appendChild(dom.createTextNode(Boolean.toString(n.getIsNPC())));
				el.appendChild(el2);
				
				el2 = dom.createElement("X");
				el2.appendChild(dom.createTextNode(Double.toString(n.getX())));
				el.appendChild(el2);
				
				el2 = dom.createElement("Y");
				el2.appendChild(dom.createTextNode(Double.toString(n.getY())));
				el.appendChild(el2);
				
				if (n.getChildren() != null)
					for (int c = 0; c < n.getChildren().length; c ++) {
						el2 = dom.createElement("Child");
//						el2.setAttribute("n", Integer.toString(c));
						el2.appendChild(dom.createTextNode(n.getChildren()[c]));
						el.appendChild(el2);
					}
				
				for (Object o2 : n.getProbSets().keySet().toArray()) {
					Element el3;
					el2 = dom.createElement("ProbSet");
					
					el3 = dom.createElement("strategy");
					el3.appendChild(dom.createTextNode(Integer.toString((Integer) o2)));
					el2.appendChild(el3);
					
					for (int c = 0; c < n.getProbSets().get((Integer) o2).length; c++) {
						el3 = dom.createElement("value");
//						el3.setAttribute("n", Integer.toString(c));
						el3.appendChild(dom.createTextNode(Double.toString(n.getProbSets().get((Integer) o2)[c])));
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
	
	//TODO I could pass in a method as an argument for displaying the text in-game.
	public void start() {
		DialogNode cn = nodeMap.get("initial"); //current node
		
		//exits when the current node no longer has children
		while (true) {
			if (cn.getIsNPC()) {
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
		//TODO each graph needs to have independent strategies
		//TODO changeable strategies
		int strategy = 0;
		double randomDouble = Math.random(); //random factor
		double floor = 0;
		double ceiling = 0;
		for (int i = 0; i < cn.getChildren().length; i++) {
			if (cn.getProbSets().get(strategy) != null)
				ceiling += cn.getProbSets().get(strategy)[i];
			else
				ceiling += cn.getProbSets().get(strategy)[0];
			if ( randomDouble >= floor && randomDouble < ceiling)
				return nodeMap.get(cn.getChildren()[i]);
			floor += ceiling;
		}
		return null;
	}
	
	public void addNode(DialogNode node) {
		if (nodeMap.put(node.getText(), node) != null)
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
						n.setChildren(newChildren);
						if (n.getProbSets().size() > 0)
							JOptionPane.showMessageDialog(null, "Make sure to change probability sets of node: " + n.getText(), "Alert", JOptionPane.PLAIN_MESSAGE);
					}
				}
		}
	}
	
	public void changeNodeText(DialogNode node, String text) {
		String oldText = node.getText();
		
		if (nodeMap.get(node.getText()) == null)
			throw new IllegalArgumentException("That node does not exist in this dialog.");
		if (nodeMap.get(text) != null)
			throw new IllegalArgumentException("A node with that new text already exists in this dialog.");
		nodeMap.remove(node.getText());
		node.setText(text);
		nodeMap.put(text, node);
		
		//Changes all child references to changed node
		Object[] dnArray = nodeMap.values().toArray();
		//Iterates over all nodes in graph
		for (Object o : dnArray) {
			DialogNode n = (DialogNode) o;
			//If node has children
			if (n.getChildren() != null)
				//Iterates over that node's children
				for (int c = 0; c < n.getChildren().length; c++)
					//If child matches removed node
					if (n.getChildren()[c].equals(oldText)) {
						String[] newChildren = n.getChildren();
						newChildren[c] = text;
						n.setChildren(newChildren);
					}
		}
	}
	
//	public void switchNodeNPC(String text) {
//		DialogNode n = nodeMap.get(text);
//		if (n.getChildren() != null && n.getProbSet().size() == 0 && n.getIsNPC())
//			throw new IllegalArgumentException("Player-controlled nodes with children must have probability arrays");			
//		if (n.getIsNPC())
//			n.setPC();
//		else
//			n.setNPC();
//	}
	
//	public void changeNodeProbSet(String text, HashMap<Integer, Double[]> newProbSet) {
//		DialogNode n = nodeMap.get(text);
//		if (n == null)
//			throw new IllegalArgumentException("A node with that text does not exist in this dialog.");
//		if (newProbSet.size() > 0) {
//			Double[][] array = (Double[][]) newProbSet.values().toArray();
//			for (int s = 0; s < newProbSet.size(); s++) {
//				double sum = 0;
//				for (int c = 0; c < array[s].length; c++) {
//					sum += array[s][c];
//					if (n.getChildren() != null && newProbSet.size() > 0)
//						if (n.getChildren().length != array[s].length)
//							throw new IllegalArgumentException("Probability array " + Integer.toString(s) + " is not of same length as children.");
//				}
//				if (sum > 1.01 || sum < 0.99)
//					throw new IllegalArgumentException("Each probability array must sum to 1.");
//			}
//		}
//		n.setProbSet(newProbSet);
//	}
	
//	public void changeNodeChildren(String text, String[] newChildren) {
//		DialogNode n = nodeMap.get(text);
//		if (n == null)
//			throw new IllegalArgumentException("A node with that text does not exist in this dialog.");
//		n.setChildren(newChildren);
//	}
	
	//Runs various checks making sure the graph is valid
	public boolean check() {
		Object[] dnArray = nodeMap.values().toArray();
		for (Object o : dnArray) {
			DialogNode n = (DialogNode) o;
			
			//Checks for player nodes following player nodes
			if (!n.getIsNPC() && n.getChildren() != null)
				for (int c = 0; c < n.getChildren().length; c++)
					if (!nodeMap.get(n.getChildren()[c]).getIsNPC()) {
						System.out.println("Error, player node '" + n.getText() + "' has a child '" + nodeMap.get(n.getChildren()[c]).getText() + "' which is also a player node.");
						return false;
					}
			
			//Checks for NPC nodes following an NPC node that is not "initial"
			if (n.getIsNPC() && n.getChildren() != null && !n.getText().equals("initial"))
				for (int c = 0; c < n.getChildren().length; c++)
					if (nodeMap.get(n.getChildren()[c]).getIsNPC())
						System.out.println("Warning, NPC node '" + n.getText() + "' has a child '" + nodeMap.get(n.getChildren()[c]).getText() + "' which is also an NPC node.");
			
			//Checks if all probSet lengths match children lengths
			if (n.getProbSets().size() != 0) {
				Object[] strategies = n.getProbSets().keySet().toArray();
				for (int strategyIndex = 0; strategyIndex < strategies.length; strategyIndex++) {
					int strategy = (int) strategies[strategyIndex]; 
					if (n.getProbSets().get(strategy).length != n.getChildren().length) {
						System.out.println("Error, probSet size of node '" + n.getText() + "' in strategy " + Integer.toString(strategy) + " does not match the number of its children");
						return false;
					}
				}
			}
			
			//Checks if each node's children are of same type (NPC or player)
			if (n.getChildren() != null) {
				boolean firstChildIsNPC = false;
				for (int c = 0; c < n.getChildren().length; c++) {
					if (c == 0)
						firstChildIsNPC = nodeMap.get(n.getChildren()[c]).getIsNPC();
					else
						if (nodeMap.get(n.getChildren()[c]).getIsNPC() != firstChildIsNPC) {
							System.out.println("Error, the children of node '" + n.getText() + "' are not all of the same type");
							return false;
						}
				}
			}
		}
		
		//Checks to make sure an initial node exists
		if (nodeMap.get("initial") == null) {
			System.out.println("Error, there is no initial node in this graph.");
			return false;
		}
		
		return true;
	}
	
	public HashMap<String, DialogNode> getGraph() {
		return nodeMap;
	}
}