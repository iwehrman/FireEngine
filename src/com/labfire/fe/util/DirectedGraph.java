/*
 * DirectedGraph.java
 * 
 * Copyright (c) 2001-2002 Labfire, Inc. All rights reserved.
 * Visit http://labfire.com/ for more information. 
 * 
 * This software is the confidential and proprietary information of
 * Labfire, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Labfire.
 */


package com.labfire.fe.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
* An implementation of an interface for a directed graph. Every vertex 
* in the graph is identified by a unique key. Data can be stored at each 
* edge and vertex in the graph.
*
* @author <a href="http://labfire.com/">Labfire, Inc.</a>
*/
public class DirectedGraph {

    // Methods to build the graph
    Map vert = new HashMap();
    Map edge = new HashMap();

    /**
    * Add a vertex to the graph. If the graph already contains a vertex
    * with the given key, the old value is replaced.
    *
    * @param key the key that identifies the vertex.
    * @param data the data associated with the vertex.
    */
    public void addVertex( Object key, Object data ) {
        vert.put( key, data );
        edge.put( key, new HashMap() );
    }
    
    /**
    * Add an edge to the graph starting at the vertex identified by
    * fromKey and ending at the vertex identified by toKey. If either of
    * the end points do not exist a NoSuchVertexException will be thrown.
    * If the graph already contains this edge, the old data will be replaced
    * by the new data.
    *
    * @param fromKey the key associated with the starting vertex of the edge.
    * @param toKey the key associated with the ending vertex of the edge.
    * @param data the data to be associated with the edge.
    *
    * @exception NoSuchVertexException if either end point is not a
    * key associated with a vertex in the graph.
    */
    public void addEdge( Object fromKey, Object toKey, Object data ) throws NoSuchVertexException {
        if ( !isVertex( fromKey ) || !isVertex( toKey ) ) 
            throw new NoSuchVertexException( "Invalid Vertex" + fromKey + " or " + toKey );
    
        HashMap from = (HashMap) edge.get( fromKey );
    
        from.put( toKey, data );
    }
    
    // Operations on edges
    
    /**
    * Return true if the edge defined by the given vertices is an
    * edge in the graph. False will be returned if the edge is not
    * in the graph.
    *
    * @param from the key of the vetex where the edge starts.
    * @param to the key of the vertex where the edge ends.
    *
    * @return true if the edge defined by the given vertices is in
    * the graph and false otherwise.
    *
    * @exception NoSuchVertexException if either end point is not a
    * key associated with a vertex in the graph.
    */
    public boolean isEdge( Object fromKey, Object toKey ) throws NoSuchVertexException {
        return ( getEdgeData( fromKey, toKey ) != null ); 
    }
    
    /**
    * Return a reference to the data associated with the edge that is 
    * defined by the given end points. Null will be returned if the 
    * edge is not in the graph or if either vertex has not been assigned
    * to a vertex in the graph.
    *
    * @param from the key of the vertex where the edge starts.
    * @param to the key of the vertex where the edge ends.
    *
    * @return a reference to the data associated with the edge defined by
    * the specified end points. Null is returned if the edge 
    * is not in the graph or the end points do not exist.
    *
    * @exception NoSuchVertexException if either end point is not a
    * key associated with a vertex in the graph.
    */
    public Object getEdgeData( Object fromKey, Object toKey ) throws NoSuchVertexException {
   
        if ( !isVertex( fromKey ) || !isVertex( toKey ) ) 
            throw new NoSuchVertexException( "Invalid Vertex" + fromKey + " or " + toKey );

        Object foundThis = null;
        HashMap temp = (HashMap) ( edge.get( fromKey ) );
        
        if ( temp.containsKey( toKey ) )
            foundThis = temp.get( toKey ); 
        
        return foundThis;
    }
    
    // Operations on vertices
    
    /**
    * Returns true if the graph contains a vertex with the associated
    * key.
    *
    * @param key the key of the vertex being looked for.
    *
    * @return true if the key is associated with a vertex in the graph
    * and false otherwise.
    */
    public boolean isVertex( Object key ) {
        return vert.containsKey( key );
    }

    /**
    * Returns the data associated with the vertex identified by the
    * key.
    *
    * @param key the key of the vertex being looked for.
    *
    * @return the data associated with the vertex that is identifed by the
    * key if the vertex exists, and null otherwise.
    *
    * @exception NoSuchVertexException if either end point is not a
    * key associated with a vertex in the graph.
    */
    public Object getVertexData( Object key ) throws NoSuchVertexException {
        if ( !isVertex( key ) ) 
            throw new NoSuchVertexException( "No vertex: " + key );
    
        return vert.get( key );
    }

    /**
    * Returns a count of the number of vertices in the graph.
    *
    * @return the count of the number of vertices in this graph
    */
    public int numVertices() {
        return vert.size();
    }
    
    /**
    * Returns the degree of the vertex that is associated with the
    * given key. Negative 1 is returned if the vertex cannot be found.
    *
    * @param key the key of the vertex being looked for.
    *
    * @return the degree of the vertex associated with the key or
    * -1 if the vertex is not in the graph.
    */
    public int degree( Object key ) {
        int deg = -1;
    
        if ( isVertex( key ) )
            deg = ( (HashMap) ( edge.get( key ) ) ).size();
    
        return deg;
    }
    
    /**
    * Returns a collection containing the data associated with the
    * neighbors of the vertex identified by the specified key.
    * The collection will be empty if there are no neighbors or
    * the vertex does not exist.
    *
    * @param key the key associated with the vertex whose neighbors we
    * wish to obtain.
    *
    * @return a collection containing the data associated with the neighbors
    * of the vertex with the given key. The collection will be
    * empty if the vertex does not exist or if it does not have
    * any neighbors.
    *
    * @exception NoSuchVertexException if either end point is not a
    * key associated with a vertex in the graph.
    */
    public Collection neighborData( Object key ) throws NoSuchVertexException {
    
        if ( !isVertex( key ) ) 
            throw new NoSuchVertexException( "No Vertex: " + key );
    
        HashSet ret = new HashSet();
        HashSet temp = new HashSet();
    
        temp.addAll( neighborKeys( key ) );
        
        Iterator i = temp.iterator();
    
        while ( i.hasNext() )
            ret.add( vert.get( i.next() ) );
        
        return ret;
    }
    
   /**
    * Returns a collection containing the keys associated with the
    * neighbors of the vertex identified by the specified key.
    * The collection will be empty if there are no neighbors or
    * the vertex does not exist.
    *
    * @param key the key associated with the vertex whose neighbors we
    * wish to obtain.
    *
    * @return a collection containing the keys associated with the neighbors
    * of the vertex with the given key. The collection will be
    * empty if the vertex does not exist or if it does not have
    * any neighbors.
    *
    * @exception NoSuchVertexException if either end point is not a
    * key associated with a vertex in the graph.
    */
    public Collection neighborKeys( Object key ) throws NoSuchVertexException {
    
        if ( !isVertex( key ) ) 
            throw new NoSuchVertexException( "No Vertex: " + key );
    
        return ( (HashMap) ( edge.get( key ) ) ).keySet();
    }
    
    // Utility

    /**
    * Returns a collection containing the data associated with all of
    * the vertices in the graph.
    *
    * @return a collection containing the data associated with the
    * vertices in the graph.
    */
    public Collection vertexData() {
        return vert.values();
    }
        

   /**
    * Returns a collection containing the keys associated with all of
    * the vertices in the graph.
    *
    * @return a collection containing the keys associated with the
    * vertices in the graph.
    */
    public Collection vertexKeys() {
        HashSet temp = new HashSet();
        temp.addAll( vert.keySet() );
    
        return temp;
    }
    
    /**
    * Return a collection containing all of the data associated with the
    * edges in the graph.
    *
    * @return a collection containing the data associated with the edges
    * in this graph.
    */
    
    public Collection edgeData() {
    
        HashSet retVal = new HashSet();
        HashSet temp = (HashSet) vertexKeys();

        Iterator i = temp.iterator();
    
        while ( i.hasNext() ) 
            retVal.addAll( ( (HashMap) ( edge.get( i.next() ) ) ).values() );
    
        return retVal;
    }

    /**
    * Remove all vertices and edges from the graph.
    */
    public void clear() {
        vert.clear();
        edge.clear();
    }
    
    /**
    * This overrides the toString method in the Object class, and is used
    * to print the contents of this graph.
    *
    * @return a value of type 'String'
    */
    public String toString() {
        String a = "";

        TreeSet b = new TreeSet( vert.keySet() );
    
        Iterator i = b.iterator();
        while ( i.hasNext() ) {
            Object key = i.next();
            String temp = key + ": " + ( ( edge.get( key ) ) ) + "\n";
            a += temp;
        }
    
        return a;
    } 
}

