/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tripad.cootrack.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mfachmirizal
 */
public class CustomerClass {
    private String id;
    private String name;
    private String showname;
    private List<CustomerClass> children;
    
    public CustomerClass() {
        id = null;
        name=null;
        showname=null;
        children=new ArrayList<CustomerClass>();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the showname
     */
    public String getShowname() {
        return showname;
    }

    /**
     * @param showname the showname to set
     */
    public void setShowname(String showname) {
        this.showname = showname;
    }

    /**
     * @return the children
     */
    public List<CustomerClass> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<CustomerClass> children) {
        this.children = children;
    }
}
