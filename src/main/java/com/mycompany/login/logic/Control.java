
package com.mycompany.login.logic;

import com.company.login.persistence.PersistenceController;
import java.util.List;

public class Control {
    
    PersistenceController controlPersis = new PersistenceController();
    

    public User userValidation(String username, String password) {
        //String msg = "";
        User usr= null;
        List<User> userList = controlPersis.getUsers();
        
        for(User user : userList){
            System.out.println("Usuario "+ user.getUserName());
            if(user.getUserName().equals(username)){
                if(user.getPassword().equals(password)){
                  //  msg = "Correct username and password. Welcome!";
                    //return msg;
                    usr=user;
                    return usr;
                }else{
                   // msg="Incorrect password.";
                   // return msg;
                   usr=null;
                   return usr;
                }
            }else{
               // msg="User not found.";
               usr=null;
               
                
            }
        }
        //return msg;
        return usr;

    }


    public List<User> getUsers() {
        return controlPersis.getUsers();
    }


    public List<Role> getRoles() {
        return controlPersis.getRoles();
    }

    public void createUser(String user, String pass, String role) {
        User usr = new User();
        usr.setUserName(user);
        usr.setPassword(pass);
        
        Role rol = new Role();
        rol = this.getRole(role);
        
        if(rol!=null){
            usr.setOneRole(rol);
        }
        
        int id = this.searchLastID();
        usr.setId(id+1);
        
        controlPersis.createUser(usr);
    }

    private Role getRole(String role) {
       List<Role> roleList =  controlPersis.getRoles();
       for(Role rl:roleList){
           if(rl.getRoleName().equals(role)){
               return rl;
           }
       }
       return null;
    }

    private int searchLastID() {
        
        List<User> userList = this.getUsers();
        
        User us = userList.get(userList.size()-1);
        return us.getId();
        
    }

    public void deleteUser(int user_id) {
        controlPersis.deleteUser(user_id);
    }

    public User getUser(int user_id) {
        return controlPersis.getUser(user_id);
    }

    public void editUser(User usu, String user, String pass, String role) {
        usu.setUserName(user);
        usu.setPassword(pass);
        
        Role rol = new Role();
        rol = this.getRole(role);
        
        if(rol!=null){
            usu.setOneRole(rol);
        }
        
        controlPersis.editUser(usu);
        
    }

    
}
