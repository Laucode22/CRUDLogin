
package com.company.login.persistence;

import com.company.login.persistence.exceptions.NonexistentEntityException;
import com.mycompany.login.logic.Role;
import com.mycompany.login.logic.User;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PersistenceController {
    
    UserJpaController userJpa = new UserJpaController();
    RoleJpaController roleJpa = new RoleJpaController();

    public List<User> getUsers() {
        List<User> usersList = userJpa.findUserEntities();

        return usersList;
    }

    public List<Role> getRoles() {
        return roleJpa.findRoleEntities();
    }

    public void createUser(User usr) {
        userJpa.create(usr);
    }

    public void deleteUser(int user_id) {
        try {
            userJpa.destroy(user_id);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getUser(int user_id) {
        return userJpa.findUser(user_id);
    }

    public void editUser(User usu) {
        try {
            userJpa.edit(usu);
        } catch (Exception ex) {
            Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
