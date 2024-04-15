
package com.mycompany.login.logic;

import java.io.Serializable;
import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class User implements Serializable {
    
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String userName;
    private String password;

    @ManyToOne
    @JoinColumn(name = "fk_role")
    private Role oneRole;
    
    public User() {
    }

    public User(int id, String userName, String password, Role oneRole) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.oneRole = oneRole;
    }

    public Role getOneRole() {
        return oneRole;
    }

    public void setOneRole(Role oneRole) {
        this.oneRole = oneRole;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
