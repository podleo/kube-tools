/**
 * 
 */
package com.flyover.kube.tools.connector.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mramach
 *
 */
public class PathsModel extends Model {

	private List<String> paths = new LinkedList<>();

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}
	
}
