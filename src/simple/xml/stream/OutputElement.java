/*
 * OutputElement.java July 2006
 *
 * Copyright (C) 2006, Niall Gallagher <niallg@users.sf.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * Public License along with this library; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */

package simple.xml.stream;

/**
 * The <code>OutputElement</code> object represents an XML element.  
 * Attributes can be added to this before ant child element has been
 * acquired from it. Once a child element has been acquired the
 * attributes will be written an can no longer be manipulated, the
 * same applies to any text value set for the element.
 * 
 * @author Niall Gallagher
 */ 
class OutputElement implements OutputNode {
   
   /**
    * Represents the attributes that have been set for the element.
    */         
   protected OutputNodeMap table;
   
   /**
    * Used to write the start tag and attributes for the document.
    */ 
   protected NodeWriter writer;
   
   /**
    * This is the parent XML element to this output node.
    */
   private OutputNode parent;
   
   /**
    * Represents the value that has been set for the element.
    */ 
   private String value;

   /**
    * Represents the name of the element for this output node.
    */ 
   private String name;
   
   /**
    * This is the output mode that this element object is using.
    */
   private Mode mode;
   
   /**
    * Constructor for the <code>OutputElement</code> object. This is
    * used to create an output element that can create elements for
    * an XML document. This requires the writer that is used to 
    * generate the actual document and the name of this node.
    *
    * @param parent this is the parent node to this output node
    * @param writer this is the writer used to generate the file
    * @param name this is the name of the element this represents
    */ 
   public OutputElement(OutputNode parent, NodeWriter writer, String name) {
      this.table = new OutputNodeMap(this);
      this.mode = Mode.INHERIT;
      this.writer = writer;           
      this.parent = parent;
      this.name = name;
   }     
   
   /**
    * This is used to acquire the <code>Node</code> that is the
    * parent of this node. This will return the node that is
    * the direct parent of this node and allows for siblings to
    * make use of nodes with their parents if required.  
    *   
    * @return this returns the parent node for this node
    */
   public OutputNode getParent() {
	   return parent;
   }
   
   /**
    * Returns the name of the node that this represents. This is
    * an immutable property and cannot be changed. This will be
    * written as the tag name when this has been committed.
    *  
    * @return returns the name of the node that this represents
    */   
   public String getName() {
      return name;           
   }
  
   /**
    * Returns the value for the node that this represents. This 
    * is a modifiable property for the node and can be changed,
    * however once committed any change will be irrelevent.
    * 
    * @return the name of the value for this node instance
    */   
   public String getValue() {
      return value;
   }
   
   /**
    * This method is used to determine if this node is the root 
    * node for the XML document. The root node is the first node
    * in the document and has no sibling nodes. This is false
    * if the node has a parent node or a sibling node.
    * 
    * @return true if this is the root node within the document
    */
   public boolean isRoot() {
      return writer.isRoot(this);
   }
   
   /**
    * The <code>Mode</code> is used to indicate the output mode
    * of this node. Three modes are possible, each determines
    * how a value, if specified, is written to the resulting XML
    * document. This is determined by the <code>setData</code>
    * method which will set the output to be CDATA or escaped, 
    * if neither is specified the mode is inherited.
    * 
    * @return this returns the mode of this output node object
    */
   public Mode getMode() {
      return mode;
   }
   
   /**
    * This returns a <code>NodeMap</code> which can be used to add
    * nodes to the element before that element has been committed. 
    * Nodes can be removed or added to the map and will appear as
    * attributes on the written element when it is committed.
    *
    * @return returns the node map used to manipulate attributes
    */    
   public NodeMap getAttributes() {
      return table;
   }

   /**
    * This is used to set a text value to the element. This should
    * be added to the element if the element contains no child
    * elements. If the value cannot be added an exception is thrown.
    * 
    * @param value this is the text value to add to this element
    */    
   public void setValue(String value) {
      this.value = value;
   }
   
   /**
    * This is used to set the output mode of this node to either
    * be CDATA or escaped. If this is set to true the any value
    * specified will be written in a CDATA block, if this is set
    * to false the values is escaped. If however this method is
    * never invoked then the mode is inherited from the parent.
    * 
    * @param data if true the value is written as a CDATA block
    */
   public void setData(boolean data) {
      if(data) {
         mode = Mode.DATA;
      } else {
         mode = Mode.ESCAPE;
      }      
   }

   /**
    * This method is used for convinience to add an attribute node 
    * to the attribute <code>NodeMap</code>. The attribute added
    * can be removed from the element by useing the node map.
    * 
    * @param name this is the name of the attribute to be added
    * @param value this is the value of the node to be added
    */    
   public void setAttribute(String name, String value) {
      table.put(name, value);
   }

   /**
    * This is used to create a child element within the element that
    * this object represents. When a new child is created with this
    * method then the previous child is committed to the document.
    * The created <code>OutputNode</code> object can be used to add
    * attributes to the child element as well as other elements.
    *
    * @param name this is the name of the child element to create
    */    
   public OutputNode getChild(String name) throws Exception {
      return writer.writeElement(this, name);
   }
   
   /**
    * This is used to remove any uncommitted changes. Removal of an
    * output node can only be done if it has no siblings and has
    * not yet been committed. If the node is committed then this 
    * will throw an exception to indicate that it cannot be removed. 
    * 
    * @throws Exception thrown if the node cannot be removed
    */
   public void remove() throws Exception {
      writer.remove(this);
   }
   
   /**
    * This will commit this element and any uncommitted elements
    * elements that are decendents of this node. For instance if
    * any child or grand child remains open under this element
    * then those elements will be closed before this is closed.
    *
    * @throws Exception this is thrown if there is an I/O error
    */ 
   public void commit() throws Exception{
      writer.commit(this);
   }
  
   /**
    * This is used to determine whether this node has been committed.
    * If the node is committed then no further child elements can
    * be created from this node instance. A node is considered to
    * be committed if a parent creates another child element or if
    * the <code>commit</code> method is invoked.
    *
    * @return true if the node has been committed
    */  
   public boolean isCommitted() {
      return writer.isCommitted(this);
   }
}
