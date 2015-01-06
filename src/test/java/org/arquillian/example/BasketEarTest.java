package org.arquillian.example;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Deploying as an ear.
 * @author Augusto
 *
 */
@RunWith(Arquillian.class)
public class BasketEarTest {
    
	@Deployment
    public static Archive<?> createDeployment() {
    	
    	JavaArchive ejb = ShrinkWrap
				.create(JavaArchive.class, "test.jar")
				.addClasses(Basket.class, OrderRepository.class, SingletonOrderRepository.class, BasketEarTest.class, BasketEarTest.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");    	
    	
    	System.out.println(ejb.toString(true));
    	
		EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
//						.addAsLibrary(api)
						.addAsModule(ejb);		
				
		System.out.println(ear.toString(true));
		
        return ear;
    }
    
    @Inject
    Basket basket;
    
    @EJB
    OrderRepository repo;
    
    @Test
    @InSequence(1)
    public void place_order_should_add_order() {
        basket.addItem("sunglasses");
        basket.addItem("suit");
        basket.placeOrder();
        Assert.assertEquals(1, repo.getOrderCount());
        Assert.assertEquals(0, basket.getItemCount());
        
        basket.addItem("raygun");
        basket.addItem("spaceship");
        basket.placeOrder();
        Assert.assertEquals(2, repo.getOrderCount());
        Assert.assertEquals(0, basket.getItemCount());
    }
    
    @Test
    @InSequence(2)
    public void order_should_be_persistent() {
        Assert.assertEquals(2, repo.getOrderCount());
    }
}