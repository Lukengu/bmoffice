/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.jpa.entities.controllers;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import pro.novatech.solutions.bmoffice.jpa.entities.Product;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.exceptions.NonexistentEntityException;
import pro.novatech.solutions.bmoffice.jpa.repository.EntityRepository;

/**
 *
 * @author philippefgx
 */
public class ProductController extends EntityControllerAbstract implements EntityRepository<Product> {

    @Override
    public void create(Product product) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(Product product) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            product = em.merge(product);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = product.getId();
                if (null == find(id)) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Product> findAll() {
        return findAll(true, -1, -1);
    }

    @Override
    public List<Product> findAll(int maxResults, int firstResult) {
        return findAll(false, maxResults, firstResult);
    }

    @Override
    public List<Product> findAll(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    @Override
    public Product find(Integer entityId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Product.class, entityId);
        } finally {
            em.close();
        }
    }

    @Override
    public int getCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Product findByProductCode(String productCode) throws Exception {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Product.findByProductCode");
            q.setParameter("productCode", productCode);
            return (Product) q.getSingleResult();
        } catch (NoResultException e) {
            throw new Exception(e.getMessage());
        } finally {
            em.close();
        }

    }

    public List<Product> getLowStockProducts() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<Product> rt = cq.from(Product.class);
            //Query q =  cq.from(Product.class);
            cq.select(rt)
                    .where(cb.lessThan(rt.get("inStock"), 20))
                    .orderBy(cb.desc(rt.get("inStock")));
            Query q = em.createQuery(cq);
            return q.getResultList();
        } finally {
            em.close();
        }

    }

}
