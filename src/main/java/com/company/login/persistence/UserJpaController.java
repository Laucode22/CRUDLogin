/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.company.login.persistence;

import com.company.login.persistence.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.mycompany.login.logic.Role;
import com.mycompany.login.logic.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lausa
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public UserJpaController() {
        emf = Persistence.createEntityManagerFactory("loginPU");
    }
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role oneRole = user.getOneRole();
            if (oneRole != null) {
                oneRole = em.getReference(oneRole.getClass(), oneRole.getId());
                user.setOneRole(oneRole);
            }
            em.persist(user);
            if (oneRole != null) {
                oneRole.getUsersList().add(user);
                oneRole = em.merge(oneRole);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            Role oneRoleOld = persistentUser.getOneRole();
            Role oneRoleNew = user.getOneRole();
            if (oneRoleNew != null) {
                oneRoleNew = em.getReference(oneRoleNew.getClass(), oneRoleNew.getId());
                user.setOneRole(oneRoleNew);
            }
            user = em.merge(user);
            if (oneRoleOld != null && !oneRoleOld.equals(oneRoleNew)) {
                oneRoleOld.getUsersList().remove(user);
                oneRoleOld = em.merge(oneRoleOld);
            }
            if (oneRoleNew != null && !oneRoleNew.equals(oneRoleOld)) {
                oneRoleNew.getUsersList().add(user);
                oneRoleNew = em.merge(oneRoleNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Role oneRole = user.getOneRole();
            if (oneRole != null) {
                oneRole.getUsersList().remove(user);
                oneRole = em.merge(oneRole);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
