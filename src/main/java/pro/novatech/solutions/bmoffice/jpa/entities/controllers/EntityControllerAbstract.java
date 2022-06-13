/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.jpa.entities.controllers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author philippefgx
 */
public abstract class EntityControllerAbstract
{
    protected EntityManagerFactory emf = null;

    public EntityControllerAbstract()
    {
        emf = Persistence.createEntityManagerFactory("BMOffice_PU");
    }

    protected EntityManager getEntityManager()
    {
        return emf.createEntityManager();
    }
    
}
