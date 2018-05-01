/**
 * 
 */
package com.flyover.kube.tools.connector.model;


/**
 * @author mramach
 *
 */
public class NamespaceModel extends KubeModel {
	
	public NamespaceModel() {
		setApiVersion("v1");
		setKind("Namespace");
	}
	
}
