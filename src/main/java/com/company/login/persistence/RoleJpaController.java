/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.company.login.persistence;

import com.company.login.persistence.exceptions.NonexistentEntityException;
import com.mycompany.login.logic.Role;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.mycompany.login.logic.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lausa
 */
public class RoleJpaController implements Serializable {

    public RoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public RoleJpaController() {
        emf = Persistence.createEntityManagerFactory("loginPU");
    }
    public void create(Role role) {
        if (role.getUsersList() == null) {
            role.setUsersList(new ArrayList<User>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<User> attachedUsersList = new ArrayList<User>();
            for (User usersListUserToAttach : role.getUsersList()) {
                usersListUserToAttach = em.getReference(usersListUserToAttach.getClass(), usersListUserToAttach.getId());
                attachedUsersList.add(usersListUserToAttach);
            }
            role.setUsersList(attachedUsersList);
            em.persist(role);
            for (User usersListUser : role.getUsersList()) {
                Role oldOneRoleOfUsersListUser = usersListUser.getOneRole();
                usersListUser.setOneRole(role);
                usersListUser = em.merge(usersListUser);
                if (oldOneRoleOfUsersListUser != null) {
                    oldOneRoleOfUsersListUser.getUsersList().remove(usersListUser);
                    oldOneRoleOfUsersListUser = em.merge(oldOneRoleOfUsersListUser);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Role role) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role persistentRole = em.find(Role.class, role.getId());
            List<User> usersListOld = persistentRole.getUsersList();
            List<User> usersListNew = role.getUsersList();
            List<User> attachedUsersListNew = new ArrayList<User>();
            for (User usersListNewUserToAttach : usersListNew) {
                usersListNewUserToAttach = em.getReference(usersListNewUserToAttach.getClass(), usersListNewUserToAttach.getId());
                attachedUsersListNew.add(usersListNewUserToAttach);
            }
            usersListNew = attachedUsersListNew;
            role.setUsersList(usersListNew);
            role = em.merge(role);
            for (User usersListOldUser : usersListOld) {
                if (!usersListNew.contains(usersListOldUser)) {
                    usersListOldUser.setOneRole(null);
                    usersListOldUser = em.merge(usersListOldUser);
                }
            }
            for (User usersListNewUser : usersListNew) {
                if (!usersListOld.contains(usersListNewUser)) {
                    Role oldOneRoleOfUsersListNewUser = usersListNewUser.getOneRole();
                    usersListNewUser.setOneRole(role);
                    usersListNewUser = em.merge(usersListNewUser);
                    if (oldOneRoleOfUsersListNewUser != null && !oldOneRoleOfUsersListNewUser.equals(role)) {
                        oldOneRoleOfUsersListNewUser.getUsersList().remove(usersListNewUser);
                        oldOneRoleOfUsersListNewUser = em.merge(oldOneRoleOfUsersListNewUser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = role.getId();
                if (findRole(id) == null) {
                    throw new NonexistentEntityException("The role with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role role;
            try {
                role = em.getReference(Role.class, id);
                role.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The role with id " + id + " no longer exists.", enfe);
            }
            List<User> usersList = role.getUsersList();
            for (User usersListUser : usersList) {
                usersListUser.setOneRole(null);
                usersListUser = em.merge(usersListUser);
            }
            em.remove(role);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Role> findRoleEntities() {
        return findRoleEntities(true, -1, -1);
    }

    public List<Role> findRoleEntities(int maxResults, int firstResult) {
        return findRoleEntities(false, maxResults, firstResult);
    }

    private List<Role> findRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Role.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Role findRole(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Role> rt = cq.from(Role.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
