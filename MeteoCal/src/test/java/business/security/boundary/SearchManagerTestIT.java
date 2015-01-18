/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Invite;
import business.security.entity.Users;
import java.util.Date;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author m-daniele
 */
@RunWith(Arquillian.class)
public class SearchManagerTestIT {
    
    @EJB
            SearchManager searchManager;
    
    @EJB
            EventManager eventManager;
    
    @EJB
            UserManager userManager;
    
    Users user1, user2;
    Event event1;
    Invite invite1;
    
    
    @Deployment
    public static WebArchive createArchiveAndDeploy(){
        return ShrinkWrap.create(WebArchive.class)
                .addClass(SearchManager.class)
                .addClass(EventManager.class)
                .addClass(UserManager.class)
                .addPackage(Event.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
    }
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
        
        
        
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    @InSequence(1)
    public void testFindUser(){
        user1 = new Users();
        user1.setEmail("e@t.it");
        user1.setPassword("t");
        user1.setBirthday(new Date(92,1,15));
        user1.setName("try");
        user1.setSurname("try");
        user1.setGroupName(Group.USER);
        userManager.save(user1);
        assertEquals(user1,searchManager.findUser(user1.getEmail()));
        
        assertNull(searchManager.findUser("aaa"));
    }
    
    @Test
    @InSequence(2)
    public void testFi(){
        user1 = new Users();
        user1.setEmail("t@t.it");
        user1.setPassword("t");
        user1.setBirthday(new Date(92,1,15));
        user1.setName("try");
        user1.setSurname("try");
        user1.setGroupName(Group.USER);
        userManager.save(user1);
        assertEquals(user1,searchManager.findUser(user1.getEmail()));
        
        assertNull(searchManager.findUser("aaa"));
        assertEquals(2,searchManager.findUsersFromNameSurname("try", "try").size());
    }
    
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
